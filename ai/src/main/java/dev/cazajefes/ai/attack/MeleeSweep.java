package dev.cazajefes.ai.attack;

import dev.cazajefes.ai.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/** Dynamic melee attack that varies based on phase and target position.
 * Uses multi-hit combos for aggressive play and upward strikes for elevated targets. */
public final class MeleeSweep extends BossAttack {
    public MeleeSweep(String id,String name,AttackCategory category,int weight,long cooldown,
                      double radius,double damage,int warningTicks) {
        super(id,name,category,weight,Math.min(cooldown,24),radius,damage,Math.min(warningTicks,7));
    }

    @Override public boolean inRange(CustomBoss boss,Player target,double distance){
        return Geometry.gapSquared(boss.body().getBoundingBox(),target.getBoundingBox())<=radius()*radius();
    }

    @Override public boolean special(){return false;}

    @Override public AttackMotion execute(CustomBoss boss,Player target) {
        if(!boss.environment().mayHit(boss,target))return AttackMotion.complete();
        
        double distance=boss.body().getLocation().distance(target.getLocation());
        double yDelta=target.getY()-boss.body().getY();
        int phase=boss.environment().phase(boss);
        boolean elevated=yDelta>0.5&&distance<=radius();
        
        // Phase 2+: multi-hit combo for more dynamic combat
        if(phase>=2&&!elevated&&boss.environment().canSee(boss,target)){
            return newComboAttack(boss,target,damage()/3,3);
        }
        // Upward strike for slightly elevated targets
        if(elevated&&yDelta<2.5&&boss.environment().canSee(boss,target)){
            return newUpwardStrike(boss,target,damage());
        }
        // Standard single hit
        if(boss.environment().canSee(boss,target)&&inRange(boss,target,0)){
            boss.environment().damage(boss,target,damage());
            boss.environment().impact(boss,this,target.getLocation());
            // Knockback push for aggressive players
            if(distance<3){
                var push=target.getLocation().toVector().subtract(boss.body().getLocation().toVector()).setY(0);
                if(push.lengthSquared()>.01){
                    push.normalize().multiply(.15);
                    var v=target.getVelocity();
                    target.setVelocity(new Vector(v.getX()+push.getX(),v.getY(),v.getZ()+push.getZ()));
                }
            }
        }
        return AttackMotion.complete();
    }
    
    /** Multi-hit combo attack for phase 2+ that creates dynamic melee combat. */
    private AttackMotion newComboAttack(CustomBoss boss,Player target,double hitDamage,int hits){
        final BossAttack self=this;
        return new AttackMotion(){
            int struck;
            @Override public boolean tick(){
                if(struck>=hits)return true;
                if(!boss.environment().mayHit(boss,target))return true;
                double dist=boss.body().getLocation().distance(target.getLocation());
                if(dist>self.radius()*.8)return true; // target moved away, cancel combo
                if(boss.environment().canSee(boss,target)){
                    boss.environment().damage(boss,target,hitDamage);
                    boss.environment().impact(boss,self,target.getLocation());
                    struck++;
                }
                return struck>=hits;
            }
            @Override public void close(){}
        };
    }
    
    /** Upward strike that hits players slightly above the boss. */
    private AttackMotion newUpwardStrike(CustomBoss boss,Player target,double dmg){
        if(boss.environment().canSee(boss,target)&&boss.environment().mayHit(boss,target)){
            boss.environment().damage(boss,target,dmg);
            boss.environment().impact(boss,this,target.getLocation());
            // Launch target upward and away
            var away=target.getLocation().toVector().subtract(boss.body().getLocation().toVector());
            if(away.lengthSquared()>.01){
                away.normalize().multiply(.25);
                away.setY(.35);
                var v=target.getVelocity();
                target.setVelocity(new Vector(v.getX()+away.getX(),v.getY()+away.getY(),v.getZ()+away.getZ()));
            }
        }
        return AttackMotion.complete();
    }
}
