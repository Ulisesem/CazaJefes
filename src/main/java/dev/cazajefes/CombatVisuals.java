package dev.cazajefes;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
final class CombatVisuals {
 static Color color(BossCatalog.Tier tier){return switch(tier){case COMMON->Color.WHITE;case RARE->Color.AQUA;case EPIC->Color.FUCHSIA;case LEGENDARY->Color.ORANGE;case MYTHIC->Color.RED;case ABYSSAL->Color.PURPLE;};}
 static void ring(Location center,BossCatalog.Tier tier,double radius,int points){for(int i=0;i<points;i++){double a=2*Math.PI*i/points;CosmeticFX.emit(Particle.DUST,center.clone().add(Math.cos(a)*radius,.2,Math.sin(a)*radius),1,0,0,0,0,new Particle.DustOptions(color(tier),1.2f));}}
 static void special(LivingEntity boss,BossCatalog.Tier tier){ring(boss.getLocation(),tier,1.6,24);CosmeticFX.emit(Particle.SOUL_FIRE_FLAME,boss.getLocation().add(0,1,0),14,.6,.7,.6,.025);}
 static void summon(Location point,BossCatalog.Tier tier){ring(point,tier,.8,16);CosmeticFX.emit(Particle.REVERSE_PORTAL,point.clone().add(0,.6,0),18,.35,.5,.35,.03);}
 static void egg(Location point,BossCatalog.Tier tier,double angle){for(int i=0;i<3;i++){double a=angle+i*2*Math.PI/3;CosmeticFX.emit(Particle.DUST,point.clone().add(Math.cos(a)*.65,.2+Math.sin(angle*2+i)*.4,Math.sin(a)*.65),1,0,0,0,0,new Particle.DustOptions(color(tier),1.1f));}}

 static Color signature(BossCatalog.Definition boss){int hash=boss.id().hashCode();return Color.fromRGB(80+((hash>>>16)&127),80+((hash>>>8)&127),80+(hash&127));}
 static Particle motif(BossCatalog.Definition boss){return switch(Math.floorMod(boss.id().hashCode(),8)){case 0->Particle.SOUL;case 1->Particle.ENCHANT;case 2->Particle.WITCH;case 3->Particle.REVERSE_PORTAL;case 4->Particle.END_ROD;case 5->Particle.FLAME;case 6->Particle.SPORE_BLOSSOM_AIR;default->Particle.CRIT;};}
 record AuraDot(Location location,Particle.DustOptions data){}
 record Ambient(java.util.List<AuraDot> dots,Location crown,Particle motif){}
 static Ambient ambient(LivingEntity entity,BossCatalog.Definition boss,double angle,int points){
  var origin=entity.getLocation();var bounds=entity.getBoundingBox();double width=Math.max(bounds.getWidthX(),bounds.getWidthZ()),height=bounds.getHeight();var primary=new Particle.DustOptions(signature(boss),1.8f);var accent=new Particle.DustOptions(BossRebirth.used(entity)?Color.ORANGE:color(boss.tier()),1.6f);java.util.List<AuraDot> dots=new java.util.ArrayList<>(points);
  for(var point:AuraShape.sampled(width,height,angle,points))dots.add(new AuraDot(origin.clone().add(point.x(),point.y(),point.z()),point.accent()?accent:primary));return new Ambient(dots,origin.clone().add(0,height+.3,0),motif(boss));
 }
 static void ambient(org.bukkit.entity.Player player,Ambient frame){for(var dot:frame.dots())player.spawnParticle(Particle.DUST,dot.location(),1,0,0,0,0,dot.data(),false);player.spawnParticle(frame.motif(),frame.crown(),1,.2,.15,.2,.005,null,false);}
 static void special(LivingEntity entity,BossCatalog.Definition boss){signatureRing(entity.getLocation(),boss,1.6,24);CosmeticFX.emit(motif(boss),entity.getLocation().add(0,1,0),18,.6,.7,.6,.025);}
 static void summon(Location point,BossCatalog.Definition boss){signatureRing(point,boss,.8,18);CosmeticFX.emit(motif(boss),point.clone().add(0,.7,0),16,.35,.5,.35,.02);}
 static void signatureRing(Location point,BossCatalog.Definition boss,double radius,int count){for(int i=0;i<count;i++){double angle=i*Math.PI*2/count;CosmeticFX.emit(Particle.DUST,point.clone().add(Math.cos(angle)*radius,.2,Math.sin(angle)*radius),1,0,0,0,0,new Particle.DustOptions(signature(boss),1.3f));}}
}
