package dev.cazajefes;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
final class CombatVisuals {
 static Color color(BossCatalog.Tier tier){return switch(tier){case COMMON->Color.WHITE;case RARE->Color.AQUA;case EPIC->Color.FUCHSIA;case LEGENDARY->Color.ORANGE;case MYTHIC->Color.RED;case ABYSSAL->Color.PURPLE;};}
 static void ring(Location center,BossCatalog.Tier tier,double radius,int points){for(int i=0;i<points;i++){double a=2*Math.PI*i/points;center.getWorld().spawnParticle(Particle.DUST,center.clone().add(Math.cos(a)*radius,.2,Math.sin(a)*radius),1,0,0,0,0,new Particle.DustOptions(color(tier),1.2f));}}
 static void special(LivingEntity boss,BossCatalog.Tier tier){ring(boss.getLocation(),tier,1.6,24);boss.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME,boss.getLocation().add(0,1,0),14,.6,.7,.6,.025);}
 static void summon(Location point,BossCatalog.Tier tier){ring(point,tier,.8,16);point.getWorld().spawnParticle(Particle.REVERSE_PORTAL,point.clone().add(0,.6,0),18,.35,.5,.35,.03);}
 static void egg(Location point,BossCatalog.Tier tier,double angle){for(int i=0;i<3;i++){double a=angle+i*2*Math.PI/3;point.getWorld().spawnParticle(Particle.DUST,point.clone().add(Math.cos(a)*.65,.2+Math.sin(angle*2+i)*.4,Math.sin(a)*.65),1,0,0,0,0,new Particle.DustOptions(color(tier),1.1f));}}
}
