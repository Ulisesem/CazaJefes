package dev.cazajefes;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.*;
final class BossBuffs {
 static void apply(LivingEntity boss){boss.setFireTicks(0);for(var type:new PotionEffectType[]{PotionEffectType.BLINDNESS,PotionEffectType.NAUSEA,PotionEffectType.MINING_FATIGUE})if(boss.hasPotionEffect(type))boss.removePotionEffect(type);BossCatalog.Definition definition=BossCatalog.get(boss.getPersistentDataContainer().get(new org.bukkit.NamespacedKey("cazajefes","boss_type"),org.bukkit.persistence.PersistentDataType.STRING));maintain(boss,PotionEffectType.STRENGTH,definition==null?1:BattleRules.strength(definition)-1);maintain(boss,PotionEffectType.HASTE,BattleRules.HASTE_AMPLIFIER);}
 private static void maintain(LivingEntity boss,PotionEffectType type,int amplifier){var effect=boss.getPotionEffect(type);if(effect==null||effect.getAmplifier()!=amplifier||effect.getDuration()<40)boss.addPotionEffect(new PotionEffect(type,100,amplifier,true,false));}
}
