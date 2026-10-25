package dev.cazajefes;
import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.inventory.ItemStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.*;
final class BossRebirth implements Listener {
 private static final NamespacedKey USED=new NamespacedKey("cazajefes","rebirth_used"),BOSS=new NamespacedKey("cazajefes",BossKeys.BOSS);
 private final CazaJefesPlugin plugin;BossRebirth(CazaJefesPlugin plugin){this.plugin=plugin;}
 static boolean used(LivingEntity boss){return boss.getPersistentDataContainer().has(USED,PersistentDataType.BYTE);}
 boolean tryRebirth(EntityDamageEvent event){
  if(!(event.getEntity() instanceof LivingEntity boss)||!boss.getPersistentDataContainer().has(BOSS,PersistentDataType.BYTE)||boss.getEquipment()==null)return false;
  ItemStack hand=boss.getEquipment().getItemInOffHand();
  if(!RebirthRules.mayRebirth(BossEquipment.zombie(boss.getType()),used(boss),hand.getType()==Material.TOTEM_OF_UNDYING&&BossEquipment.isDisplay(hand),boss.getHealth(),event.getFinalDamage()))return false;
  if(event instanceof EntityDamageByEntityEvent hit){plugin.guard.record(hit);plugin.participation.record(hit);}
  event.setCancelled(true);boss.getPersistentDataContainer().set(USED,PersistentDataType.BYTE,(byte)1);
  boss.playEffect(EntityEffect.PROTECTED_FROM_DEATH);boss.getEquipment().setItemInOffHand(new ItemStack(Material.AIR));boss.setInvulnerable(false);boss.setHealth(RebirthRules.restoredHealth(boss.getMaxHealth()));boss.setNoDamageTicks(0);boss.setFireTicks(0);if(boss instanceof Mob mob)mob.setAI(true);
  BossCatalog.Definition definition=BossCatalog.get(boss.getPersistentDataContainer().get(new NamespacedKey(plugin,"boss_type"),PersistentDataType.STRING));String name=definition==null?"El boss":definition.name();
  Component message=Component.text("✦ "+name,NamedTextColor.GOLD).decorate(TextDecoration.BOLD).append(Component.text(" ha obtenido Suspiro del Renacido · Vida restaurada · Daño ×2",NamedTextColor.AQUA));
  Set<UUID> audience=BossParticipation.participants(boss);for(Player p:boss.getWorld().getPlayers())if(p.getLocation().distanceSquared(boss.getLocation())<=1024)audience.add(p.getUniqueId());for(UUID id:audience){Player p=Bukkit.getPlayer(id);if(p!=null&&p.isOnline())p.sendMessage(message);}
  boss.getWorld().playSound(boss.getLocation(),Sound.ITEM_TOTEM_USE,1.4f,.85f);boss.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING,boss.getLocation().add(0,boss.getHeight()*.5,0),90,boss.getWidth()*.6,boss.getHeight()*.4,boss.getWidth()*.6,.2);return true;
 }
 @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)public void stronger(EntityDamageByEntityEvent event){Entity source=event.getDamager();if(source instanceof Projectile projectile&&projectile.getShooter() instanceof Entity owner)source=owner;if(source instanceof LivingEntity boss&&used(boss)&&boss.getPersistentDataContainer().has(BOSS,PersistentDataType.BYTE)&&event.getEntity() instanceof Player)event.setDamage(RebirthRules.damage(event.getDamage(),true));}
}
