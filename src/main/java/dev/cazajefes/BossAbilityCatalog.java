package dev.cazajefes;
import java.util.*;
final class BossAbilityCatalog {
 enum Pattern {RINGS,CROSS,CONE,SPIRAL,METEORS,VORTEX,LANES,CHECKER,GEYSERS,NOVA,RUSH,CAGE,BEAM}
 enum Debuff {SLOWNESS,POISON,WEAKNESS,WITHER,BLINDNESS,HUNGER,LEVITATION,DARKNESS;
  org.bukkit.potion.PotionEffectType potion(){return switch(this){case SLOWNESS->org.bukkit.potion.PotionEffectType.SLOWNESS;case POISON->org.bukkit.potion.PotionEffectType.POISON;case WEAKNESS->org.bukkit.potion.PotionEffectType.WEAKNESS;case WITHER->org.bukkit.potion.PotionEffectType.WITHER;case BLINDNESS,DARKNESS->org.bukkit.potion.PotionEffectType.WEAKNESS;case HUNGER->org.bukkit.potion.PotionEffectType.HUNGER;case LEVITATION->org.bukkit.potion.PotionEffectType.LEVITATION;};}
 }
 record Ultimate(String name,Pattern pattern,Debuff effect,double radius,int pulses,double angle){}
 static final Map<String,Ultimate> ALL=BossCatalog.builtIns().values().stream().collect(
   java.util.stream.Collectors.toUnmodifiableMap(BossCatalog.Definition::id, boss -> boss.custom().ultimate()));
 static Ultimate ultimate(BossCatalog.Definition boss){
  Ultimate profile=boss.custom()!=null?boss.custom().ultimate():ALL.get(boss.id());
  if(profile==null)throw new IllegalArgumentException("Falta definitivo de "+boss.id());
  return profile;
 }
 static boolean hits(Pattern pattern,double x,double z,int pulse,int pulses,double radius,double angle){
  double c=Math.cos(angle),s=Math.sin(angle),a=x*c+z*s,b=-x*s+z*c,d=Math.hypot(x,z);if(d>radius+1)return false;
  return switch(pattern){
   case RINGS->Math.abs(d-radius*(pulse+1)/pulses)<1.25;
   case CROSS->Math.abs(a)<1||Math.abs(b)<1;
   case CONE->a>0&&Math.abs(b)<a*.65+1;
   case SPIRAL->Math.abs(Math.sin(Math.atan2(z,x)-angle-pulse*1.05))*d<1;
   case METEORS->Math.hypot(a-(pulse%2==0?-2:2),b-((pulse/2)%2==0?-2:2))<2;
   case VORTEX->pulse==pulses-1?d<radius*.7:d<radius;
   case LANES->Math.abs(b-((pulse%3)-1)*3)<1.1;
   case CHECKER->Math.floorMod((int)Math.floor(a/2)+(int)Math.floor(b/2),2)==pulse%2;
   case GEYSERS->Math.abs(d-(2+pulse*1.4))<1.1&&Math.abs(Math.sin(Math.atan2(z,x)*3-angle))<.65;
   case NOVA->d>2+Math.sin(pulse)*.6;
   case RUSH->Math.abs(b)<1.3&&a>=-1;
   case BEAM->false; // Beam uses a three-dimensional ray.
   case CAGE->Math.abs(d-(radius-pulse*1.6))<1;
  };
 }
}
