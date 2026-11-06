package dev.cazajefes;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
final class CombatVisuals {
 static Color color(BossCatalog.Tier tier){return switch(tier){case COMMON->Color.WHITE;case RARE->Color.AQUA;case EPIC->Color.FUCHSIA;case LEGENDARY->Color.ORANGE;case MYTHIC->Color.RED;case ABYSSAL->Color.PURPLE;};}
 static void ring(Location center,BossCatalog.Tier tier,double radius,int points){for(int i=0;i<points;i++){double a=2*Math.PI*i/points;CosmeticFX.emit(Particle.DUST,center.clone().add(Math.cos(a)*radius,.2,Math.sin(a)*radius),1,0,0,0,0,new Particle.DustOptions(color(tier),1.2f));}}
 static void special(LivingEntity boss,BossCatalog.Tier tier){ring(boss.getLocation(),tier,1.6,24);CosmeticFX.emit(Particle.SOUL_FIRE_FLAME,boss.getLocation().add(0,1,0),14,.6,.7,.6,.025);}
 static void summon(Location point,BossCatalog.Tier tier){ring(point,tier,.8,16);CosmeticFX.emit(Particle.REVERSE_PORTAL,point.clone().add(0,.6,0),18,.35,.5,.35,.03);}
 static void egg(Location point,BossCatalog.Tier tier,double angle){var black=new Particle.DustOptions(Color.fromRGB(8,0,16),1.65f);var purple=new Particle.DustOptions(Color.fromRGB(175,35,245),1.25f);for(int i=0;i<6;i++){double a=angle+i*Math.PI/3,y=.2+(Math.sin(angle+i*.7)+1)*.45;CosmeticFX.emit(Particle.DUST,point.clone().add(Math.cos(a)*.7,y,Math.sin(a)*.7),1,0,0,0,0,black);CosmeticFX.emit(Particle.DUST,point.clone().add(-Math.cos(a)*.7,1.1-y,-Math.sin(a)*.7),1,0,0,0,0,purple);}for(int i=0;i<8;i++){double a=i*Math.PI/4+angle*.3;CosmeticFX.emit(Particle.DUST,point.clone().add(Math.cos(a)*.9,-.45,Math.sin(a)*.9),1,0,0,0,0,i%2==0?black:purple);}}


 static Color signature(BossCatalog.Definition boss){int hash=boss.id().hashCode();return Color.fromRGB(80+((hash>>>16)&127),80+((hash>>>8)&127),80+(hash&127));}
 static Particle motif(BossCatalog.Definition boss){return BossStyle.of(boss.entityType()).particle;}
 record AuraDot(Location location,Particle.DustTransition data){}
 record Ambient(java.util.List<AuraDot> dots,Location crown,Particle motif){}
 static Ambient ambient(LivingEntity entity,BossCatalog.Definition boss,double angle,int points){
  var origin=entity.getLocation();var bounds=entity.getBoundingBox();double width=Math.max(bounds.getWidthX(),bounds.getWidthZ()),height=bounds.getHeight();var style=BossStyle.of(boss.entityType());int phase=BossStages.phase(entity);
  var primary=new Particle.DustTransition(style.primary,style.accent,phase==3?1.45f:1.1f);
  var accent=new Particle.DustTransition(BossRebirth.used(entity)?Color.ORANGE:style.accent,style.primary,1.25f);java.util.List<AuraDot> dots=new java.util.ArrayList<>(points);
  for(var point:AuraShape.themed(style,width,height,angle,points,phase))dots.add(new AuraDot(origin.clone().add(point.x(),point.y(),point.z()),point.accent()?accent:primary));return new Ambient(dots,origin.clone().add(0,height+.3,0),motif(boss));
 }
 static void ambient(org.bukkit.entity.Player player,Ambient frame){for(var dot:frame.dots())player.spawnParticle(Particle.DUST_COLOR_TRANSITION,dot.location(),1,0,0,0,0,dot.data(),false);player.spawnParticle(frame.motif(),frame.crown(),1,.2,.15,.2,.005,null,false);}
 static void special(LivingEntity entity,BossCatalog.Definition boss){signatureRing(entity.getLocation(),boss,1.6,24);CosmeticFX.emit(motif(boss),entity.getLocation().add(0,1,0),18,.6,.7,.6,.025);}
 static void summon(Location point,BossCatalog.Definition boss){signatureRing(point,boss,.8,18);CosmeticFX.emit(motif(boss),point.clone().add(0,.7,0),16,.35,.5,.35,.02);}
 static void signatureRing(Location point,BossCatalog.Definition boss,double radius,int count){for(int i=0;i<count;i++){double angle=i*Math.PI*2/count;CosmeticFX.emit(Particle.DUST,point.clone().add(Math.cos(angle)*radius,.2,Math.sin(angle)*radius),1,0,0,0,0,new Particle.DustOptions(signature(boss),1.3f));}}

 static void awakening(Location at,BossCatalog.Definition definition) {
  BossStyle style=BossStyle.of(definition.entityType());
  var audience=CinematicFX.audience(at,28);
  CinematicFX.ring(at,audience,1.5,0,14,style.primary,style.accent);
  CinematicFX.ring(at,audience,2.4,Math.PI/6,16,style.accent,style.primary);
  CosmeticFX.emit(style.particle,at.clone().add(0,.7,0),14,.65,.65,.65,.045);
 }
 static void defeat(Location chest,BossStyle style,int tick,int end) {
  double progress=Math.clamp(tick/(double)end,0,1);
  double radius=2.2*(1-progress)+.2;
  var audience=CinematicFX.audience(chest,26);
  var color=new Particle.DustTransition(style.primary,style.accent,1.05f);
  for(int i=0;i<8;i++){
   double angle=i*Math.PI/4+tick*.26;
   var point=chest.clone().add(Math.cos(angle)*radius,Math.sin(angle*2)*radius*.4,Math.sin(angle)*radius);
   for(var player:audience) CosmeticFX.to(player,Particle.DUST_COLOR_TRANSITION,point,1,0,0,0,0,color);
  }
 }
}
