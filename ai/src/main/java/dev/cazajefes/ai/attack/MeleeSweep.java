package dev.cazajefes.ai.attack;

import dev.cazajefes.ai.*;
import org.bukkit.entity.Player;

/** Full-strength contact attacks. The encounter owns cadence instead of splitting hits into weak combos. */
public final class MeleeSweep extends BossAttack {
    public MeleeSweep(String id,String name,AttackCategory category,int weight,long cooldown,
                      double radius,double damage,int warningTicks) {
        super(id,name,category,weight,cooldown,radius,damage,warningTicks);
    }

    @Override public boolean inRange(CustomBoss boss,Player target,double distance){
        return Geometry.gapSquared(boss.body().getBoundingBox(),target.getBoundingBox())<=radius()*radius();
    }

    @Override public boolean special(){return false;}

    @Override public AttackMotion execute(CustomBoss boss,Player target) {
        if(!boss.environment().mayHit(boss,target))return AttackMotion.complete();
        
        if(inRange(boss,target,0)&&strikeGroup(boss,target,damage())){
            boss.environment().impact(boss,this,target.getLocation());
        }
        return AttackMotion.complete();
    }
    private boolean strikeGroup(CustomBoss boss,Player target,double amount){
        var at=boss.body().getLocation();
        boolean struckTarget=false;
        for(Player candidate:at.getWorld().getNearbyPlayers(at,radius()+boss.body().getWidth())){
            if(!boss.environment().validTarget(boss,candidate)||!boss.environment().mayHit(boss,candidate)
                ||!inRange(boss,candidate,0)||!boss.environment().canSee(boss,candidate))continue;
            boss.environment().meleeDamage(boss,candidate,amount);
            if(candidate.equals(target))struckTarget=true;
        }
        return struckTarget;
    }

}
