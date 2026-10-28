package dev.cazajefes;
import java.util.Set;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.*;
final class BossEffects {
 private static final Set<String> NEGATIVE=Set.of("slowness","weakness","poison","wither","blindness","nausea","mining_fatigue","hunger","levitation","darkness","unluck","bad_omen","infested","oozing","weaving","wind_charged");
 static int duration(String key,int original){return key.equals("blindness")?100:key.equals("nausea")?200:NEGATIVE.contains(key)?600:original;}
 static boolean blockedOnBoss(String key){return key.equals("blindness")||key.equals("nausea")||key.equals("mining_fatigue");}
 static PotionEffect effect(PotionEffectType type,int duration,int amplifier){return effect(type,duration,amplifier,false,true,true);}
 static PotionEffect effect(PotionEffectType type,int duration,int amplifier,boolean ambient,boolean particles){return effect(type,duration,amplifier,ambient,particles,particles);}
 static PotionEffect effect(PotionEffectType type,int duration,int amplifier,boolean ambient,boolean particles,boolean icon){return new PotionEffect(type,duration(type.getKey().getKey(),duration),amplifier,ambient,particles,icon);}
 static boolean apply(LivingEntity target,PotionEffect effect){var old=target.getPotionEffect(effect.getType());if(NEGATIVE.contains(effect.getType().getKey().getKey())&&old!=null&&old.getAmplifier()==effect.getAmplifier()&&old.getDuration()>=Math.max(1,effect.getDuration()-20))return true;return target.addPotionEffect(effect);}
}
