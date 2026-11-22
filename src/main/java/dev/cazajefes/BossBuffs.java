package dev.cazajefes;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.*;
final class BossBuffs {
 static void apply(LivingEntity boss){boss.setFireTicks(0);for(var type:new PotionEffectType[]{PotionEffectType.BLINDNESS,PotionEffectType.NAUSEA,PotionEffectType.MINING_FATIGUE,PotionEffectType.HASTE})if(boss.hasPotionEffect(type))boss.removePotionEffect(type);
  BossCatalog.Definition definition=BossCatalog.get(boss.getPersistentDataContainer().get(new org.bukkit.NamespacedKey("cazajefes","boss_type"),org.bukkit.persistence.PersistentDataType.STRING));maintain(boss,PotionEffectType.STRENGTH,definition==null?1:BattleRules.strength(definition)-1);
  var boons=definition==null?new com.google.gson.JsonObject():definition.custom().options().boons();int duration=number(boons,"hasteDurationTicks",400),cooldown=number(boons,"hasteCooldownTicks",600);int remaining=HasteCycle.remaining(boss.getTicksLived(),duration,cooldown);var current=boss.getPotionEffect(PotionEffectType.SPEED);
  if(remaining>0){if(current==null||current.getAmplifier()!=5||current.getDuration()<remaining){boss.removePotionEffect(PotionEffectType.SPEED);boss.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,remaining,5,true,false,true));}}
  else if(current!=null&&current.getAmplifier()==5)boss.removePotionEffect(PotionEffectType.SPEED);
 }
 private static int number(com.google.gson.JsonObject json,String key,int fallback){try{return json.has(key)?json.get(key).getAsInt():fallback;}catch(RuntimeException ex){return fallback;}}
 private static void maintain(LivingEntity boss,PotionEffectType type,int amplifier){var effect=boss.getPotionEffect(type);if(effect==null||effect.getAmplifier()!=amplifier||effect.getDuration()<40)boss.addPotionEffect(new PotionEffect(type,100,amplifier,true,false));}
}
