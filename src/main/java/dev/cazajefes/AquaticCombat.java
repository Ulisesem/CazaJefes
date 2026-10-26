package dev.cazajefes;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.*;
import org.bukkit.util.Vector;
final class AquaticCombat implements Listener {
 private static final NamespacedKey BOSS=new NamespacedKey("cazajefes",BossKeys.BOSS);
 static boolean swimming(LivingEntity boss){return boss.getLocation().getBlock().isLiquid()||boss.getEyeLocation().getBlock().isLiquid();}
 static void maintain(LivingEntity boss){if(!swimming(boss))return;boss.setRemainingAir(boss.getMaximumAir());var movement=boss.getAttribute(Attribute.WATER_MOVEMENT_EFFICIENCY);if(movement!=null)movement.setBaseValue(1);for(var type:new PotionEffectType[]{PotionEffectType.WATER_BREATHING,PotionEffectType.DOLPHINS_GRACE}){var effect=boss.getPotionEffect(type);if(effect==null||effect.getDuration()<40)boss.addPotionEffect(new PotionEffect(type,100,0,true,false));}}
 static Vector motion(Vector delta,boolean fox){double limit=fox?.30:.42;if(delta.lengthSquared()<.01)return new Vector();Vector result=delta.clone().normalize().multiply(limit);result.setY(Math.max(-.25,Math.min(.25,result.getY())));return result;}
 @EventHandler(priority=EventPriority.LOWEST,ignoreCancelled=true)public void waterDamage(EntityDamageEvent event){if(event.getEntity() instanceof LivingEntity boss&&boss.getPersistentDataContainer().has(BOSS,PersistentDataType.BYTE)&&(event.getCause()==EntityDamageEvent.DamageCause.DROWNING||(event.getCause()==EntityDamageEvent.DamageCause.CUSTOM&&!(event instanceof EntityDamageByEntityEvent)&&swimming(boss))))event.setCancelled(true);}
 @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)public void waterTeleport(EntityTeleportEvent event){if(event.getEntity() instanceof LivingEntity boss&&boss.getPersistentDataContainer().has(BOSS,PersistentDataType.BYTE)&&swimming(boss))event.setCancelled(true);}
}
