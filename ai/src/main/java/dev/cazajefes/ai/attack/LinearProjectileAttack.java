package dev.cazajefes.ai.attack;

import dev.cazajefes.ai.AttackCategory;
import dev.cazajefes.ai.AttackMotion;
import dev.cazajefes.ai.BossAttack;
import dev.cazajefes.ai.CustomBoss;
import dev.cazajefes.ai.Geometry;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public final class LinearProjectileAttack extends BossAttack {
    private final double speed;
    private final int lifetime;

    public LinearProjectileAttack(String id,String name,AttackCategory category,int weight,long cooldown,
                                  double radius,double damage,int warningTicks,double speed,int lifetime){
        super(id,name,category,weight,cooldown,radius,damage,warningTicks);
        this.speed=speed;
        this.lifetime=lifetime;
    }

    @Override public AttackMotion execute(CustomBoss boss,Player target){
        return launch(boss,target,boss.origin(),dev.cazajefes.ai.visual.BossVisuals.Projectile.ARROW,false,0);
    }

    AttackMotion launch(CustomBoss boss,Player target,Location from,
                        dev.cazajefes.ai.visual.BossVisuals.Projectile kind,boolean guide,double area){

        var direction=target.getBoundingBox().getCenter().subtract(from.toVector());
        if(direction.lengthSquared()<.00001)return AttackMotion.complete();
        direction.normalize();
        from.setDirection(direction);
        var view=boss.environment().visuals().projectile(from,kind);
        return new AttackMotion(){
            Location position=from;
            int age;
            @Override public boolean tick(){
                if(guide&&boss.environment().validTarget(boss,target)){
                    var desired=target.getBoundingBox().getCenter().subtract(position.toVector());
                    if(desired.lengthSquared()>.01)direction.multiply(.72).add(desired.normalize().multiply(.28)).normalize();
                }
                var next=position.clone().add(direction.clone().multiply(speed));
                if(++age>lifetime||!Geometry.loaded(position,speed)||!Geometry.loaded(next,speed))return true;
                // Sweep the whole segment from the previous tick to the next position so a fast
                // projectile cannot tunnel through a target or hit something behind a wall.
                var collision=position.getWorld().rayTrace(position,direction,speed,FluidCollisionMode.NEVER,true,
                    Math.min(radius(),2),entity->entity instanceof LivingEntity victim&&boss.environment().mayHit(boss,victim));
                if(collision!=null){
                    var hit=collision.getHitPosition().toLocation(position.getWorld());
                    if(area>0){
                        if(Geometry.loaded(hit,area))for(var victim:hit.getWorld().getNearbyPlayers(hit,area)){
                            if(!boss.environment().mayHit(boss,victim)||victim.getLocation().distanceSquared(hit)>area*area)continue;
                            var delta=victim.getEyeLocation().toVector().subtract(hit.toVector());
                            double length=delta.length();
                            if(length>.2&&hit.getWorld().rayTraceBlocks(hit.clone().add(direction.clone().multiply(-.08)),
                                delta.normalize(),length-.12,FluidCollisionMode.NEVER,true)!=null)continue;
                            boss.environment().damage(boss,victim,damage());
                        }
                    }else if(collision.getHitEntity() instanceof LivingEntity victim){
                        boss.environment().damage(boss,victim,damage());
                        if(!victim.isDead())boss.environment().projectileHit(boss,victim,kind);
                    }
                    boss.environment().impact(boss,LinearProjectileAttack.this,hit);
                    return true;
                }
                position=next;
                view.move(position);
                return false;
            }
            @Override public void close(){view.close();}
        };
    }
}
