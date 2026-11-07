package dev.cazajefes.ai;

import dev.cazajefes.ai.visual.BossVisuals;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public abstract class AbstractCustomBoss implements CustomBoss {
    private final LivingEntity body;
    private final BossProfile profile;
    private final BossEnvironment environment;
    private final BossVisuals.View view;
    private final Map<String,Long> ready=new HashMap<>();
    private BossState state=BossState.IDLE;
    private Player target;
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
    @Override public final Player target(){return target;}

    public final boolean requestSignature(Player player,java.util.function.Supplier<AttackMotion> start){
        if(!canRequest(player)||tick<specialUntil)return false;
        var signature=start.get();
        if(signature==null)return false;
        target=player;
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
        double distance=body.getLocation().distance(player.getLocation());
        for(var attack:profile.attacks()){
            if(attack.id().equals(id)&&available(attack)&&attack.weight()>0&&tick>=ready.getOrDefault(id,0L)
                &&attack.inRange(this,player,distance)){
                target=player;
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
            if(pending==null&&motion==null&&requested==null&&!environment.validTarget(this,target))target=environment.acquire(this);
            environment.heartbeat(this);
        }
        if(tick<stunnedUntil){
            state=BossState.STUNNED;
            move(null,false);
        }else if(pending!=null||motion!=null){
            state=BossState.ATTACKING;
            if(!environment.validTarget(this,target)){
                finish();
                target=null;
            }else if(pending!=null&&tick>=releaseAt){
                var attack=pending;
                pending=null;
                ready.put(attack.id(),tick+attack.cooldown());
                environment.release(this,attack);
                specialMotion=attack.special();
                motion=attack.execute(this,target);
            }else if(motion!=null&&motion.tick()){
                finish();
            }
            move(target,motion!=null&&motion.mobile()||mobileWindup());
        }else if(!environment.validTarget(this,target)){
            target=null;
            requested=null;
            state=BossState.IDLE;
            move(null,false);
        }else{
            double distance=body.getLocation().distance(target.getLocation());
            boolean free=tick>=gcdUntil&&!environment.occupied(this)&&environment.canSee(this,target);
            BossAttack choice=requested;
            requested=null;
            if(choice!=null&&(!free||!choice.inRange(this,target,distance)))choice=null;
            if(free&&choice==null)choice=DecisionMaker.choose(profile.attacks(),tick,
                id->ready.getOrDefault(id,0L),ThreadLocalRandom.current(),
                attack->attack instanceof dev.cazajefes.ai.attack.MeleeSweep&&attack.inRange(this,target,distance));
            if(free&&choice==null&&tick>=specialUntil)motion=environment.signature(this,target);
            if(motion!=null){
                specialMotion=true;
                state=BossState.ATTACKING;
                move(target,motion.mobile());
                view.move(body.getLocation());
                return;
            }
            if(free&&choice==null)choice=DecisionMaker.choose(profile.attacks(),tick,
                id->ready.getOrDefault(id,0L),ThreadLocalRandom.current(),attack->available(attack)&&attack.inRange(this,target,distance));
            if(choice==null){
                state=BossState.MOVING;
                move(target,true);
            }else{
                pending=choice;
                releaseAt=tick+choice.warningTicks();
                state=BossState.ATTACKING;
                environment.warning(this,target,choice,choice.warningTicks());
                move(target,mobileWindup());
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
