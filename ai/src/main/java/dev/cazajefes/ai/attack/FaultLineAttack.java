package dev.cazajefes.ai.attack;

import dev.cazajefes.ai.*;
import org.bukkit.entity.Player;

/** A telegraphed strike that sends elevated targets back into the fight.
 * Extended to also hit players hiding behind cover by checking through blocks. */
public final class FaultLineAttack extends BossAttack {
    public FaultLineAttack(String id,String name,int weight,long cooldown,double range,double damage,int warningTicks){
        super(id,name,AttackCategory.MIXED,weight,cooldown,range,damage,warningTicks);
    }

    @Override public boolean inRange(CustomBoss boss,Player target,double distance){
        // Wider range for elevated targets; also effective against hiding players
        return distance<=radius()&&(target.getY()-boss.body().getY()>=1.0
            ||!boss.environment().canSee(boss,target));
    }

    @Override public AttackMotion execute(CustomBoss boss,Player target){
        double distance=boss.body().getLocation().distance(target.getLocation());
        if(inRange(boss,target,distance)
            &&boss.environment().mayHit(boss,target)){
            // Can hit through blocks - this is a ground-based attack
            boss.environment().impact(boss,this,target.getLocation());
            boss.environment().damage(boss,target,damage());
            if(!target.isDead()){
                boss.environment().dislodge(boss,target);
                // Additional knockback for farming players to force them down
                if(target.getY()-boss.body().getY()>2){
                    var down=new org.bukkit.util.Vector(0,-.4,0);
                    var current=target.getVelocity();
                    target.setVelocity(new org.bukkit.util.Vector(current.getX(),Math.min(current.getY(),0)+down.getY(),current.getZ()));
                }
            }
        }
        return AttackMotion.complete();
    }
}
