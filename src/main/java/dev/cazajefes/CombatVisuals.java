package dev.cazajefes;

import dev.cazajefes.integration.PacketBridge;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
final class CombatVisuals {
 static Color color(BossCatalog.Tier tier){return switch(tier){case COMMON->Color.WHITE;case RARE->Color.AQUA;case EPIC->Color.FUCHSIA;case LEGENDARY->Color.ORANGE;case MYTHIC->Color.RED;case ABYSSAL->Color.PURPLE;};}
 static void ring(Location center,BossCatalog.Tier tier,double radius,int points){for(int i=0;i<points;i++){double a=2*Math.PI*i/points;CosmeticFX.emit(Particle.DUST,center.clone().add(Math.cos(a)*radius,.2,Math.sin(a)*radius),1,0,0,0,0,new Particle.DustOptions(color(tier),1.2f));}}
 static void special(LivingEntity boss,BossCatalog.Tier tier){ring(boss.getLocation(),tier,1.6,24);CosmeticFX.emit(Particle.SOUL_FIRE_FLAME,boss.getLocation().add(0,1,0),14,.6,.7,.6,.025);}
 static void summon(Location point,BossCatalog.Tier tier){ring(point,tier,.8,16);CosmeticFX.emit(Particle.REVERSE_PORTAL,point.clone().add(0,.6,0),18,.35,.5,.35,.03);}
 static void egg(Location point,BossCatalog.Tier tier,double angle){var black=new Particle.DustOptions(Color.fromRGB(8,0,16),1.65f);var purple=new Particle.DustOptions(Color.fromRGB(175,35,245),1.25f);for(int i=0;i<6;i++){double a=angle+i*Math.PI/3,y=.2+(Math.sin(angle+i*.7)+1)*.45;CosmeticFX.emit(Particle.DUST,point.clone().add(Math.cos(a)*.7,y,Math.sin(a)*.7),1,0,0,0,0,black);CosmeticFX.emit(Particle.DUST,point.clone().add(-Math.cos(a)*.7,1.1-y,-Math.sin(a)*.7),1,0,0,0,0,purple);}for(int i=0;i<8;i++){double a=i*Math.PI/4+angle*.3;CosmeticFX.emit(Particle.DUST,point.clone().add(Math.cos(a)*.9,-.45,Math.sin(a)*.9),1,0,0,0,0,i%2==0?black:purple);}}


 static Color signature(BossCatalog.Definition boss){int hash=boss.id().hashCode();return Color.fromRGB(80+((hash>>>16)&127),80+((hash>>>8)&127),80+(hash&127));}
 private record AuraIdentity(Color primary,Color accent,Particle particle,int magma,int seed){}
 private static AuraIdentity identity(BossCatalog.Definition boss){
  int seed=boss.id().hashCode();
  CreatureAura style=CreatureAura.of(boss.entityType());
  return switch(boss.id()){
   case "sapo_de_fuego" -> new AuraIdentity(Color.fromRGB(255,84,12),Color.fromRGB(255,212,90),Particle.FLAME,0,seed);
   case "magmoryx_corazon_de_basalto" -> new AuraIdentity(Color.fromRGB(88,70,95),Color.fromRGB(255,111,57),Particle.ASH,1,seed);
   case "pyroth_latido_del_volcan" -> new AuraIdentity(Color.fromRGB(240,44,12),Color.fromRGB(255,219,113),Particle.SMALL_FLAME,2,seed);
   case "calderyx_corazon_del_nucleo" -> new AuraIdentity(Color.fromRGB(190,34,83),Color.fromRGB(255,160,42),Particle.LAVA,3,seed);
   default -> new AuraIdentity(blend(style.primary,signature(boss)),blend(style.accent,signature(boss)),style.particle,-1,seed);
  };
 }
 private static Color blend(Color family,Color signature){
  return Color.fromRGB((family.getRed()*2+signature.getRed())/3,
      (family.getGreen()*2+signature.getGreen())/3,(family.getBlue()*2+signature.getBlue())/3);
 }
 static Particle motif(BossCatalog.Definition boss){return identity(boss).particle();}
 record AuraDot(Location location,Particle.DustTransition data){}
 record AuraTrail(Location origin, Particle.Trail data){}
 record Ambient(java.util.List<AuraDot> dots, java.util.List<AuraTrail> trails, Location crown,Particle motif,Particle particle){}
 static Ambient ambient(LivingEntity entity,BossCatalog.Definition boss,BossParticles config,double angle,int points){
  var origin=entity.getLocation();var bounds=entity.getBoundingBox();double width=Math.max(bounds.getWidthX(),bounds.getWidthZ()),height=bounds.getHeight();var style=CreatureAura.of(boss.entityType());int phase=BossStages.phase(entity);
  AuraIdentity identity=identity(boss);
  Color firstColor=config.color()==null?identity.primary():config.color();
  Color secondColor=config.accent()==null?identity.accent():config.accent();
  var primary=new Particle.DustTransition(firstColor,secondColor,phase==3?1.55f:1.3f);
  var accent=new Particle.DustTransition(BossRebirth.used(entity)?Color.ORANGE:secondColor,firstColor,1.25f);java.util.List<AuraDot> dots=new java.util.ArrayList<>(points);
  var shape=config.shape()==BossParticles.Shape.AUTO
      ?identity.magma()>=0?AuraShape.magma(identity.magma(),width,height,angle,Math.min(points,config.points()),phase)
        :style.points(width,height,origin.getYaw(),angle,Math.min(points,config.points()),phase,identity.seed())
      :config.geometry(Math.clamp(width*.5+.55,.8,6),Math.clamp(height,.6,9),angle+(identity.seed()&255)*.02,points);
  java.util.List<AuraTrail> trails=new java.util.ArrayList<>(2);
  int first=shape.isEmpty()?0:Math.floorMod((int)(angle*3),shape.size());
  for(int i=0;i<shape.size();i++){
   var point=shape.get(i);
   var start=origin.clone().add(point.x(),point.y(),point.z());
   // Replace dust points with client-animated highlights inside the existing budget.
   if(config.particle()==null&&shape.size()>=24&&(i==first||i==(first+shape.size()/2)%shape.size())){
    int stride=style==CreatureAura.WIND||style==CreatureAura.WINGS||style==CreatureAura.NIGHT_FLIGHT||style==CreatureAura.EMBER_FLIGHT?4:2;
    var next=shape.get((i+stride)%shape.size());
    var end=origin.clone().add(next.x(),next.y(),next.z());
    trails.add(new AuraTrail(start,new Particle.Trail(end,point.accent()?secondColor:firstColor,phase==3?12:9)));
   }else{
    dots.add(new AuraDot(start,point.accent()?accent:primary));
   }
  }
  return new Ambient(dots,trails,origin.clone().add(0,height+.3,0),identity.particle(),config.particle());
 }
 static void ambient(org.bukkit.entity.Player player,Ambient frame){
  for(var dot:frame.dots()){
   if(frame.particle()==null)PacketBridge.particle(player,Particle.DUST_COLOR_TRANSITION,dot.location(),1,0,0,0,0,dot.data());
   else PacketBridge.particle(player,frame.particle(),dot.location(),1,0,0,0,0,null);
  }
  for(var trail:frame.trails())PacketBridge.particle(player,Particle.TRAIL,trail.origin(),1,0,0,0,0,trail.data());
  PacketBridge.particle(player,frame.motif(),frame.crown(),1,.2,.15,.2,.005,null);
 }
 static void special(LivingEntity entity,BossCatalog.Definition boss){signatureRing(entity.getLocation(),boss,1.6,24);CosmeticFX.emit(motif(boss),entity.getLocation().add(0,1,0),18,.6,.7,.6,.025);}
 static void summon(Location point,BossCatalog.Definition boss){signatureRing(point,boss,.8,18);CosmeticFX.emit(motif(boss),point.clone().add(0,.7,0),16,.35,.5,.35,.02);}
 static void signatureRing(Location point,BossCatalog.Definition boss,double radius,int count){for(int i=0;i<count;i++){double angle=i*Math.PI*2/count;CosmeticFX.emit(Particle.DUST,point.clone().add(Math.cos(angle)*radius,.2,Math.sin(angle)*radius),1,0,0,0,0,new Particle.DustOptions(signature(boss),1.3f));}}

 static void awakening(Location at,BossCatalog.Definition definition) {
  AuraIdentity identity=identity(definition);
  var audience=CinematicFX.audience(at,28);
  CinematicFX.ring(at,audience,1.5,0,14,identity.primary(),identity.accent());
  CinematicFX.ring(at,audience,2.4,Math.PI/6,16,identity.accent(),identity.primary());
  CosmeticFX.emit(identity.particle(),at.clone().add(0,.7,0),14,.65,.65,.65,.045);
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
