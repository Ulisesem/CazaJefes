package dev.cazajefes;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
final class BossDamageProgress implements Listener {
 private static final NamespacedKey BOSS=new NamespacedKey("cazajefes",BossKeys.BOSS);
 private final CazaJefesPlugin plugin;BossDamageProgress(CazaJefesPlugin plugin){this.plugin=plugin;}
 @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
 public void finalizeBossDamage(EntityDamageEvent event){
  if(!(event.getEntity() instanceof LivingEntity boss)||!boss.getPersistentDataContainer().has(BOSS,PersistentDataType.BYTE))return;
  SafeDamage.normalize(event);
  if(event instanceof EntityDamageByEntityEvent hit){
   Entity source=hit.getDamager();
   if(source instanceof Projectile projectile&&projectile.getShooter() instanceof Entity owner)source=owner;
   if(source instanceof Player player&&plugin.guard.canAttackBoss(player)){
    SafeDamage.bossFloor(event);
    SafeDamage.finishLowHealth(event,boss.getHealth());
    var strength=player.isOp()?player.getPotionEffect(PotionEffectType.STRENGTH):null;
    if (operatorStrike(player.isOp(),hit.getDamager()==player,event.getCause(),strength==null?-1:strength.getAmplifier())) {
     SafeDamage.operatorStrike(event,boss.getHealth());
    }
    SafeDamage.bossFloor(event);
    SafeDamage.finishLowHealth(event,boss.getHealth());
   }
  }
  plugin.rebirth.tryRebirth(event);
 }
 static boolean operatorStrike(boolean operator,boolean direct,EntityDamageEvent.DamageCause cause,int amplifier){
  return operator&&direct&&cause==EntityDamageEvent.DamageCause.ENTITY_ATTACK&&amplifier>=99;
 }
}
