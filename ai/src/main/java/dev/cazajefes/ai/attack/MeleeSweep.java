package dev.cazajefes.ai.attack;

import dev.cazajefes.ai.*;
import org.bukkit.entity.Player;

public final class MeleeSweep extends BossAttack {
    public MeleeSweep(String id,String name,AttackCategory category,int weight,long cooldown,
                      double radius,double damage,int warningTicks) {
        super(id,name,category,weight,cooldown,radius,damage,warningTicks);
    }

    @Override public boolean inRange(CustomBoss boss,Player target,double distance){
        return Geometry.gapSquared(boss.body().getBoundingBox(),target.getBoundingBox())<=radius()*radius();
    }

    @Override public AttackMotion execute(CustomBoss boss,Player target) {
        if(boss.environment().mayHit(boss,target) && boss.environment().canSee(boss,target)
            && inRange(boss,target,0)) {
            boss.environment().damage(boss,target,damage());
            boss.environment().impact(boss,this,target.getLocation());
        }
        return AttackMotion.complete();
    }
}
