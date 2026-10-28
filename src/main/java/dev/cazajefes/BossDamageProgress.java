package dev.cazajefes;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.persistence.PersistentDataType;
final class BossDamageProgress implements Listener {
 private static final NamespacedKey BOSS=new NamespacedKey("cazajefes",BossKeys.BOSS);
 private final CazaJefesPlugin plugin;BossDamageProgress(CazaJefesPlugin plugin){this.plugin=plugin;}
 @EventHandler(priority=EventPriority.HIGH,ignoreCancelled=true)public void floor(EntityDamageByEntityEvent event){if(!(event.getEntity() instanceof LivingEntity boss)||!boss.getPersistentDataContainer().has(BOSS,PersistentDataType.BYTE))return;Entity source=event.getDamager();if(source instanceof Projectile p&&p.getShooter() instanceof Entity owner)source=owner;if(source instanceof Player player&&plugin.guard.canAttackBoss(player))SafeDamage.floor(event);}
 @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)public void finalizeBossDamage(EntityDamageEvent event){if(!(event.getEntity() instanceof LivingEntity boss)||!boss.getPersistentDataContainer().has(BOSS,PersistentDataType.BYTE))return;SafeDamage.normalize(event);if(event instanceof EntityDamageByEntityEvent hit){Entity source=hit.getDamager();if(source instanceof Projectile projectile&&projectile.getShooter() instanceof Entity owner)source=owner;if(source instanceof Player player&&plugin.guard.canAttackBoss(player))SafeDamage.finishLowHealth(event,boss.getHealth());}plugin.rebirth.tryRebirth(event);}
}
