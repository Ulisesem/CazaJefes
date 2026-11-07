package dev.cazajefes.ai.attack;

import dev.cazajefes.ai.*;
import org.bukkit.entity.Player;

public final class ShockwaveAttack extends BossAttack {
    public ShockwaveAttack(String id,String name,int weight,long cooldown,double radius,double damage,int warningTicks){
        super(id,name,AttackCategory.MIXED,weight,cooldown,radius,damage,warningTicks);
    }

    @Override public boolean inRange(CustomBoss boss,Player target,double distance){
        return distance<=radius();
    }

    @Override public AttackMotion execute(CustomBoss boss,Player target){
        var center=boss.body().getLocation();
        boss.environment().impact(boss,this,center);
        for(var player:center.getWorld().getNearbyPlayers(center,radius())){
            if(Geometry.gapSquared(boss.body().getBoundingBox(),player.getBoundingBox())<=radius()*radius()
                &&boss.environment().canSee(boss,player)&&boss.environment().mayHit(boss,player)){
                boss.environment().damage(boss,player,damage());
                if(!player.isDead())boss.environment().attackHit(boss,player,this);
            }
        }
        return AttackMotion.complete();
    }
}
