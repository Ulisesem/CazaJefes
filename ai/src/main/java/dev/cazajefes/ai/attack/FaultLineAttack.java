package dev.cazajefes.ai.attack;

import dev.cazajefes.ai.*;
import org.bukkit.entity.Player;

/** A telegraphed strike that sends elevated targets back into the fight. */
public final class FaultLineAttack extends BossAttack {
    public FaultLineAttack(String id,String name,int weight,long cooldown,double range,double damage,int warningTicks){
        super(id,name,AttackCategory.MIXED,weight,cooldown,range,damage,warningTicks);
    }

    @Override public boolean inRange(CustomBoss boss,Player target,double distance){
        return distance<=radius()&&target.getY()-boss.body().getY()>=1.5;
    }

    @Override public AttackMotion execute(CustomBoss boss,Player target){
        if(inRange(boss,target,boss.body().getLocation().distance(target.getLocation()))
            &&boss.environment().canSee(boss,target)&&boss.environment().mayHit(boss,target)){
            boss.environment().impact(boss,this,target.getLocation());
            boss.environment().damage(boss,target,damage());
            if(!target.isDead())boss.environment().dislodge(boss,target);
        }
        return AttackMotion.complete();
    }
}
