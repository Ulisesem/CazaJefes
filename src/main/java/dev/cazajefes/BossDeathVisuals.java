package dev.cazajefes;
import java.util.*;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
final class BossDeathVisuals implements Listener {
 private static final NamespacedKey VISUAL=new NamespacedKey("cazajefes","death_visual");
 private final CazaJefesPlugin plugin;private final Map<UUID,BukkitRunnable> tasks=new HashMap<>();
 BossDeathVisuals(CazaJefesPlugin plugin){this.plugin=plugin;}
 private boolean visual(Entity e){return e.getPersistentDataContainer().has(VISUAL,PersistentDataType.BYTE);}
 void show(LivingEntity original,BossCatalog.Definition definition){Location start=original.getLocation().clone();try{
  Entity ghost=start.getWorld().spawnEntity(start,original.getType(),CreatureSpawnEvent.SpawnReason.CUSTOM,e->{e.getPersistentDataContainer().set(VISUAL,PersistentDataType.BYTE,(byte)1);e.setPersistent(false);e.setSilent(true);e.setInvulnerable(true);e.setGravity(false);e.customName(original.customName());e.setCustomNameVisible(true);if(e instanceof LivingEntity living){living.setCollidable(false);living.setCanPickupItems(false);if(living instanceof Mob mob)mob.setAI(false);var scale=living.getAttribute(Attribute.SCALE);var old=original.getAttribute(Attribute.SCALE);if(scale!=null&&old!=null)scale.setBaseValue(old.getBaseValue());if(living.getEquipment()!=null&&original.getEquipment()!=null){living.getEquipment().setArmorContents(original.getEquipment().getArmorContents());living.getEquipment().setItemInMainHand(original.getEquipment().getItemInMainHand().clone());living.getEquipment().setItemInOffHand(original.getEquipment().getItemInOffHand().clone());for(var slot:new org.bukkit.inventory.EquipmentSlot[]{org.bukkit.inventory.EquipmentSlot.HEAD,org.bukkit.inventory.EquipmentSlot.CHEST,org.bukkit.inventory.EquipmentSlot.LEGS,org.bukkit.inventory.EquipmentSlot.FEET,org.bukkit.inventory.EquipmentSlot.HAND,org.bukkit.inventory.EquipmentSlot.OFF_HAND})living.getEquipment().setDropChance(slot,0);}}});
  double rise=Math.max(0,Math.min(4,start.getWorld().getMaxHeight()-start.getY()-original.getHeight()-1));BukkitRunnable task=new BukkitRunnable(){int tick;public void run(){if(!ghost.isValid()||++tick>48){ghost.remove();tasks.remove(ghost.getUniqueId());cancel();return;}Location at=start.clone().add(0,DeathFlight.height(tick,rise),0);ghost.teleport(at);at.getWorld().spawnParticle(Particle.END_ROD,at.clone().add(0,1,0),5,.5,.6,.5,.015);at.getWorld().spawnParticle(Particle.SOUL,at,4,.4,.5,.4,.01);if(tick==48){CombatVisuals.signatureRing(start,definition,2,24);start.getWorld().spawnParticle(Particle.FIREWORK,start.clone().add(0,1,0),30,.8,.8,.8,.03);ghost.remove();tasks.remove(ghost.getUniqueId());cancel();}}};tasks.put(ghost.getUniqueId(),task);task.runTaskTimer(plugin,1,1);
 }catch(IllegalArgumentException error){plugin.getLogger().warning("No se pudo mostrar la animación de muerte de "+definition.id()+": "+error.getMessage());}}
 @EventHandler(priority=EventPriority.LOWEST)public void damage(EntityDamageEvent event){if(visual(event.getEntity()))event.setCancelled(true);}
 @EventHandler(priority=EventPriority.HIGHEST)public void death(EntityDeathEvent event){if(visual(event.getEntity())){event.getDrops().clear();event.setDroppedExp(0);}}
 void stop(){for(var entry:tasks.entrySet()){entry.getValue().cancel();Entity e=Bukkit.getEntity(entry.getKey());if(e!=null)e.remove();}tasks.clear();}
}
