package dev.cazajefes;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.persistence.PersistentDataType;
/** Owns native slime lifecycle without replacing the living boss or resetting its HP. */
final class BossIntegrity implements Listener {
 static final NamespacedKey BOSS=new NamespacedKey("cazajefes",BossKeys.BOSS),STABLE=new NamespacedKey("cazajefes","stable_slime");
 static boolean boss(Entity e){return e.getPersistentDataContainer().has(BOSS,PersistentDataType.BYTE);}
 static void restore(LivingEntity living){
  living.setFireTicks(0);
  if(!(living instanceof Slime slime))return;
  var armor=slime.getAttribute(Attribute.ARMOR);
  if(armor!=null)armor.setBaseValue(0);
  if(slime.getPersistentDataContainer().has(STABLE,PersistentDataType.BYTE))return;
  var max=slime.getAttribute(Attribute.MAX_HEALTH);double health=slime.getHealth(),limit=max.getBaseValue();
  // setSize resets native health/armor/damage. Restore custom values, never refill health.
  var damage=slime.getAttribute(Attribute.ATTACK_DAMAGE);
  double d=damage==null?0:damage.getBaseValue();
  SlimeHealth.resizePreserving(health,()->{if(slime.getSize()!=4)slime.setSize(4);},()->{max.setBaseValue(limit);if(armor!=null)armor.setBaseValue(0);if(damage!=null)damage.setBaseValue(d);},max::getValue,slime::setHealth);slime.getPersistentDataContainer().set(STABLE,PersistentDataType.BYTE,(byte)1);
 }
 @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)public void debuff(EntityPotionEffectEvent e){if(boss(e.getEntity())&&e.getNewEffect()!=null&&BossEffects.blockedOnBoss(e.getNewEffect().getType().getKey().getKey()))e.setCancelled(true);}
 @EventHandler public void quit(org.bukkit.event.player.PlayerQuitEvent e){CosmeticFX.forget(e.getPlayer().getUniqueId());}
 @EventHandler(priority=EventPriority.HIGHEST)public void split(SlimeSplitEvent e){if(boss(e.getEntity())||e.getEntity().getPersistentDataContainer().has(new NamespacedKey("cazajefes","death_visual"),PersistentDataType.BYTE))e.setCancelled(true);}
 @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)public void fire(EntityDamageEvent e){if(boss(e.getEntity())&&switch(e.getCause()){case FIRE,FIRE_TICK,LAVA,HOT_FLOOR->true;default->false;}){e.setCancelled(true);e.getEntity().setFireTicks(0);}}
 @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)public void ignite(EntityCombustEvent e){if(boss(e.getEntity()))e.setCancelled(true);}
}
