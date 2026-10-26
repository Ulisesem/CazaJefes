package dev.cazajefes;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.persistence.PersistentDataType;
final class BossDamageProgress implements Listener {
 private static final NamespacedKey BOSS=new NamespacedKey("cazajefes",BossKeys.BOSS);
 private final CazaJefesPlugin plugin;BossDamageProgress(CazaJefesPlugin plugin){this.plugin=plugin;}
 @EventHandler(priority=EventPriority.HIGH,ignoreCancelled=true)public void floor(EntityDamageByEntityEvent event){
  if(!(event.getEntity() instanceof LivingEntity boss)||!boss.getPersistentDataContainer().has(BOSS,PersistentDataType.BYTE))return;
  Entity source=event.getDamager();if(source instanceof Projectile projectile&&projectile.getShooter() instanceof Entity owner)source=owner;if(!(source instanceof Player player)||!plugin.guard.canAttackBoss(player))return;
  double original=event.getDamage(),current=event.getFinalDamage(),wanted=DamageProgress.floor(original,current);if(wanted<=current)return;
  // Recalculate through Bukkit's normal armor/effect modifiers; never subtract health directly.
  double low=original,high=original*8+8;event.setDamage(high);if(event.getFinalDamage()<wanted){event.setDamage(original);return;}
  for(int i=0;i<12;i++){double mid=(low+high)*.5;event.setDamage(mid);if(event.getFinalDamage()<wanted)low=mid;else high=mid;}event.setDamage(high);
 }
}
