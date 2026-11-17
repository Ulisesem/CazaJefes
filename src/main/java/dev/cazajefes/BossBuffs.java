package dev.cazajefes;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.*;
final class BossBuffs {
 static void apply(LivingEntity boss){boss.setFireTicks(0);for(var type:new PotionEffectType[]{PotionEffectType.BLINDNESS,PotionEffectType.NAUSEA,PotionEffectType.MINING_FATIGUE})if(boss.hasPotionEffect(type))boss.removePotionEffect(type);BossCatalog.Definition definition=BossCatalog.get(boss.getPersistentDataContainer().get(new org.bukkit.NamespacedKey("cazajefes","boss_type"),org.bukkit.persistence.PersistentDataType.STRING));var levels=BossBoons.levels(definition);maintain(boss,PotionEffectType.STRENGTH,levels.strength()-1);maintain(boss,PotionEffectType.SPEED,levels.haste()-1);}
 private static void maintain(LivingEntity boss,PotionEffectType type,int amplifier){if(amplifier<0){boss.removePotionEffect(type);return;}var effect=boss.getPotionEffect(type);if(effect==null||effect.getAmplifier()!=amplifier||effect.getDuration()<40)boss.addPotionEffect(new PotionEffect(type,100,amplifier,true,false));}
}
