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
    private AttackMotion motion;
    private long releaseAt,gcdUntil,stunnedUntil,tick;

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

    @Override public final void tick(long tick){
        if(closed)return;
        this.tick=tick;
        if(tick%5==0){
            if(pending==null&&motion==null&&!environment.validTarget(this,target))target=environment.acquire(this);
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
                motion=attack.execute(this,target);
            }else if(motion!=null&&motion.tick()){
                finish();
            }
            move(target,motion!=null);
        }else if(!environment.validTarget(this,target)){
            target=null;
            state=BossState.IDLE;
            move(null,false);
        }else{
            double distance=body.getLocation().distance(target.getLocation());
            BossAttack choice=tick<gcdUntil||!body.hasLineOfSight(target)?null:DecisionMaker.choose(profile.attacks(),distance,tick,
                id->ready.getOrDefault(id,0L),ThreadLocalRandom.current());
            if(choice==null){
                state=BossState.MOVING;
                move(target,true);
            }else{
                pending=choice;
                releaseAt=tick+choice.warningTicks();
                state=BossState.ATTACKING;
                environment.warning(this,target,choice,choice.warningTicks());
                move(target,false);
            }
        }
        if(!closed)view.move(body.getLocation());
    }

    protected abstract void move(Player player,boolean approach);

    @Override public final void stun(int ticks){
        finish();
        stunnedUntil=Math.max(stunnedUntil,tick+Math.max(0,ticks));
        state=BossState.STUNNED;
    }

    private void finish(){
        if(motion!=null)motion.close();
        motion=null;
        pending=null;
        gcdUntil=tick+profile.globalCooldown();
    }

    @Override public final void close(){
        if(closed)return;
        closed=true;
        finish();
        view.close();
    }
}
