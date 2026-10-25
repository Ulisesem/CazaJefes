package dev.cazajefes;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.*;
final class BossBuffs {
 static void apply(LivingEntity boss){maintain(boss,PotionEffectType.STRENGTH,BattleRules.STRENGTH_AMPLIFIER);maintain(boss,PotionEffectType.HASTE,BattleRules.HASTE_AMPLIFIER);}
 private static void maintain(LivingEntity boss,PotionEffectType type,int amplifier){var effect=boss.getPotionEffect(type);if(effect==null||effect.getAmplifier()!=amplifier||effect.getDuration()<40)boss.addPotionEffect(new PotionEffect(type,100,amplifier,true,false));}
}
