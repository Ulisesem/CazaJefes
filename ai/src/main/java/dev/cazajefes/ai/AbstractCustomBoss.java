package dev.cazajefes.ai;

import dev.cazajefes.ai.visual.BossVisuals;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public abstract class AbstractCustomBoss implements CustomBoss {
    private final LivingEntity body;
    private final BossProfile profile;
    private final BossEnvironment environment;
    private final BossVisuals.View view;
    private final Map<String,Long> ready=new HashMap<>();
    private final BehaviorTracker behaviorTracker=new BehaviorTracker();
    private BossState state=BossState.IDLE;
    private final List<Player> targets=new ArrayList<>();
    private int targetIndex=0;
    private long nextTargetSwitch=0;
    private boolean closed;
    private BossAttack pending;
    private BossAttack requested;
    private AttackMotion motion;
    private long releaseAt,gcdUntil,specialUntil,stunnedUntil,tick;
    private boolean specialMotion;

    protected AbstractCustomBoss(LivingEntity body,BossProfile profile,BossEnvironment environment){
        this.body=body;
        this.profile=profile;
        this.environment=environment;
        body.setAI(false);
        body.setGravity(false);
        view=environment.visuals().boss(body);
    }

    @Override public final LivingEntity body(){return body;}
    @Override public final BossProfile profile(){return profile;}
    @Override public final BossEnvironment environment(){return environment;}
    @Override public final BossState state(){return state;}
    @Override public final Player target(){
        if(targets.isEmpty())return null;
        return targets.get(targetIndex);
    }
    
    /** Return the number of active targets tracked by this boss. */
    int targetCount(){return targets.size();}

    /** Package-private access for BossManager to clean up behavior profiles when a player quits. */
    BehaviorTracker getBehaviorTracker(){return behaviorTracker;}

    public final boolean requestSignature(Player player,java.util.function.Supplier<AttackMotion> start){
        if(!canRequest(player)||tick<specialUntil)return false;
        var signature=start.get();
        if(signature==null)return false;
        if(!targets.contains(player))targets.add(player);
        targetIndex=targets.indexOf(player);
        motion=signature;
        specialMotion=true;
        state=BossState.ATTACKING;
        return true;
    }

    private boolean canRequest(Player player){
        return !closed&&pending==null&&motion==null&&requested==null&&tick>=gcdUntil&&tick>=stunnedUntil
            &&!environment.occupied(this)&&environment.validTarget(this,player)&&environment.canSee(this,player);
    }

    public final boolean requestAttack(String id,Player player){
        if(!canRequest(player))return false;
        double distance=body.getLocation().distanceSquared(player.getLocation());
        for(var attack:profile.attacks()){
            if(attack.id().equals(id)&&available(attack)&&attack.weight()>0&&tick>=ready.getOrDefault(id,0L)
                &&attack.inRange(this,player,Math.sqrt(distance))){
                if(!targets.contains(player))targets.add(player);
                targetIndex=targets.indexOf(player);
                requested=attack;
                return true;
            }
        }
        return false;
    }

    @Override public final void tick(long tick){
        if(closed)return;
        this.tick=tick;
        if(tick%5==0){
            // Refresh all targets periodically and pick the nearest player
            refreshTargets();
            // Prioritize nearest player instead of rotating
            if(targets.size()>1){
                double nearestDist=Double.MAX_VALUE;
                int nearestIdx=0;
                Location bossLoc=body.getLocation();
                for(int i=0;i<targets.size();i++){
                    var t=targets.get(i);
                    if(t!=null&&t.isValid()){
                        double d=bossLoc.distanceSquared(t.getLocation());
                        if(d<nearestDist){nearestDist=d;nearestIdx=i;}
                    }
                }
                targetIndex=nearestIdx;
            }
            // Observe behavior of all tracked targets for adaptive AI and magnetism
            Player current=target();
            for(var t:targets){
                if(t!=null&&t.isValid())behaviorTracker.observe(this,t);
            }
            environment.heartbeat(this);
        }
        if(tick<stunnedUntil){
            state=BossState.STUNNED;
            move(null,false);
        }else if(pending!=null||motion!=null){
            state=BossState.ATTACKING;
            Player current=target();
            if(current==null||!environment.validTarget(this,current)){
                finish();
                for(var t:targets)behaviorTracker.forget(t);
                targets.clear();
                targetIndex=0;
            }else if(pending!=null&&tick>=releaseAt){
                var attack=pending;
                pending=null;
                ready.put(attack.id(),tick+attack.cooldown());
                environment.release(this,attack);
                specialMotion=attack.special();
                motion=attack.execute(this,target());
            }else if(motion!=null&&motion.tick()){
                finish();
            }
            move(target(),motion!=null&&motion.mobile()||mobileWindup());
        }else if(targets.isEmpty()){
            for(var t:targets)behaviorTracker.forget(t);
            targets.clear();
            targetIndex=0;
            requested=null;
            state=BossState.IDLE;
            move(null,false);
        }else{
            Player current=target();
            double distanceSquared=body.getLocation().distanceSquared(current.getLocation());
            double distance=Math.sqrt(distanceSquared);
            boolean free=tick>=gcdUntil&&!environment.occupied(this);
            BossAttack choice=requested;
            requested=null;
            if(choice!=null&&(!free||!choice.inRange(this,current,distance)))choice=null;
            
            // Detect player behavior for adaptive attack selection
            BehaviorTracker.PlayerBehavior behavior=current!=null?behaviorTracker.getBehavior(current):BehaviorTracker.PlayerBehavior.NEUTRAL;
            
            // Apply magnetism pull to all tracked targets
            int currentPhase=environment.phase(this);
            for(var t:targets){
                if(t!=null&&t.isValid()){
                    BehaviorTracker.PlayerBehavior tBehavior=targets.size()==1?behavior:behaviorTracker.getBehavior(t);
                    applyMagnetism(t,tBehavior,currentPhase);
                }
            }
            
            // First try melee sweep if in range (basic attacks have priority)
            if(free&&choice==null)choice=DecisionMaker.choose(profile.attacks(),tick,
                id->ready.getOrDefault(id,0L),ThreadLocalRandom.current(),
                attack->attack instanceof dev.cazajefes.ai.attack.MeleeSweep&&attack.inRange(this,current,distance),
                behavior);
            // Try signature attack from environment (legacy integration)
            if(free&&choice==null&&tick>=specialUntil)motion=environment.signature(this,current);
            if(motion!=null){
                specialMotion=true;
                state=BossState.ATTACKING;
                move(current,motion.mobile());
                view.move(body.getLocation());
                return;
            }
            // Choose special attack adapted to player behavior
            if(free&&choice==null)choice=DecisionMaker.choose(profile.attacks(),tick,
                id->ready.getOrDefault(id,0L),ThreadLocalRandom.current(),
                attack->available(attack)&&attack.inRange(this,current,distance),behavior);
            if(choice==null){
                state=BossState.MOVING;
                move(current,true);
            }else{
                pending=choice;
                releaseAt=tick+choice.warningTicks();
                state=BossState.ATTACKING;
                environment.warning(this,current,choice,choice.warningTicks());
                move(current,mobileWindup());
            }
        }
        if(!closed)view.move(body.getLocation());
    }

    private boolean available(BossAttack attack){return !attack.special()||tick>=specialUntil;}

    private boolean mobileWindup(){
        return pending!=null&&!(pending instanceof dev.cazajefes.ai.attack.MeleeSweep)
            &&profile.tactics().mobileWindup();
    }

    protected abstract void move(Player player,boolean approach);

    /**
     * Apply magnetism or other pull effects on the target.
     * Override in subclasses to implement magnetism behavior.
     */
    protected void applyMagnetism(Player target,BehaviorTracker.PlayerBehavior behavior,int phase){
        // No-op by default; KinematicBoss overrides this
    }
    
    /** Refresh the list of nearby eligible players. Called every 5 ticks. */
    private void refreshTargets(){
        double range=profile.detectionRange();
        var at=body.getLocation();
        var newTargets=new ArrayList<Player>();
        for(var player:body.getWorld().getNearbyPlayers(at,range)){
            if(environment.validTarget(this,player)){
                newTargets.add(player);
            }
        }
        // Remove players no longer valid
        targets.removeIf(t->!newTargets.contains(t));
        // Add new players
        for(var player:newTargets){
            if(!targets.contains(player)){
                targets.add(player);
            }
        }
        // Ensure index is valid
        if(targets.isEmpty()){
            targetIndex=0;
        }else if(targetIndex>=targets.size()){
            targetIndex=0;
        }
    }

    @Override public final boolean canStartSpecial(){
        return pending==null&&motion==null&&requested==null&&tick>=specialUntil&&tick>=stunnedUntil;
    }

    @Override public final void holdSpecial(int ticks){
        stun(ticks);
        specialUntil=Math.max(specialUntil,tick+profile.specialRecovery());
    }

    @Override public final void stun(int ticks){
        finish();
        stunnedUntil=Math.max(stunnedUntil,tick+Math.max(0,ticks));
        state=BossState.STUNNED;
    }

    private void finish(){
        boolean special=specialMotion;
        if(special)specialUntil=tick+profile.specialRecovery();
        specialMotion=false;
        if(motion!=null)motion.close();
        motion=null;
        pending=null;
        requested=null;
        gcdUntil=tick+(special?profile.globalCooldown():profile.basicRecovery());
    }

    @Override public final void close(){
        if(closed)return;
        closed=true;
        finish();
        view.close();
    }
}
