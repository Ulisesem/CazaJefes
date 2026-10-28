package dev.cazajefes;
import java.util.*;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
final class AnimalMinions implements Listener {
 private record Unit(LivingEntity entity,LivingEntity parent,int tier){}
 private final Map<UUID,Unit> units=new HashMap<>();private final Map<UUID,Long> nextHit=new HashMap<>();private final BukkitTask task;
 private static final NamespacedKey MARK=new NamespacedKey("cazajefes","aggressive_animal");
 AnimalMinions(CazaJefesPlugin plugin){task=plugin.getServer().getScheduler().runTaskTimer(plugin,this::tick,5,5);}
 static boolean animal(EntityType type){return switch(type){case BEE,FOX,HOGLIN,SNIFFER,POLAR_BEAR,WOLF,GOAT,TURTLE,LLAMA,TRADER_LLAMA,COW,MOOSHROOM,PIG,SHEEP,CHICKEN,RABBIT,HORSE,DONKEY,MULE,CAMEL,PANDA,AXOLOTL,OCELOT,CAT,PARROT,ARMADILLO,DOLPHIN,SQUID,GLOW_SQUID->true;default->false;};}
 void track(LivingEntity entity,LivingEntity parent,int tier){if(!animal(entity.getType()))return;var health=entity.getAttribute(Attribute.MAX_HEALTH);if(health!=null){health.setBaseValue(CombatPower.minionHealth(tier));entity.setHealth(health.getValue());}var armor=entity.getAttribute(Attribute.ARMOR);if(armor!=null)armor.setBaseValue(8+tier*2);entity.getPersistentDataContainer().set(MARK,PersistentDataType.BYTE,(byte)1);entity.setCanPickupItems(false);units.put(entity.getUniqueId(),new Unit(entity,parent,tier));}
 private void tick(){long now=System.currentTimeMillis();for(var iterator=units.entrySet().iterator();iterator.hasNext();){var entry=iterator.next();var unit=entry.getValue();LivingEntity entity=unit.entity();if(!entity.isValid()||entity.isDead()||!unit.parent().isValid()||unit.parent().isDead()){if(entity.isValid())entity.remove();iterator.remove();nextHit.remove(entry.getKey());continue;}
  Player target=entity.getWorld().getPlayers().stream().filter(p->CombatGuard.eligible(p)&&p.getLocation().distanceSquared(entity.getLocation())<=576).min(java.util.Comparator.comparingDouble(p->p.getLocation().distanceSquared(entity.getLocation()))).orElse(null);if(target==null)continue;
  if(entity instanceof Armadillo armadillo){armadillo.setAI(false);armadillo.rollOut();}
  if(entity instanceof Mob mob){mob.setTarget(target);mob.getPathfinder().moveTo(target.getLocation(),1.35);}if(entity instanceof Bee bee){bee.setAnger(24000);bee.setHasStung(false);}
  Vector delta=target.getLocation().toVector().subtract(entity.getLocation().toVector());if(entity.hasLineOfSight(target)&&delta.lengthSquared()>.25){if(entity.getLocation().getBlock().isLiquid()||entity instanceof Flying||entity.getType()==EntityType.BEE||entity.getType()==EntityType.PARROT){Vector motion=delta.normalize().multiply(.4);if(entity instanceof Bee){double angle=now/350.0;motion.add(new Vector(Math.cos(angle)*.18,Math.sin(angle)*.15,Math.sin(angle)*.18));}entity.setVelocity(motion);}else{Vector flat=delta.clone().setY(0);if(flat.lengthSquared()>.01){flat.normalize().multiply(.29);entity.setVelocity(flat.setY(entity.getVelocity().getY()));}}}
  if(entity.hasLineOfSight(target)&&entity.getLocation().distanceSquared(target.getLocation())<=7.84&&now>=nextHit.getOrDefault(entry.getKey(),0L)){target.damage(8+unit.tier()*2,entity);nextHit.put(entry.getKey(),now+1200);entity.getWorld().spawnParticle(Particle.CRIT,entity.getLocation().add(0,.6,0),5,.2,.3,.2,.03);}
 }}
 static void capHit(org.bukkit.event.entity.EntityDamageEvent event,double maximum){if(event.isCancelled())return;double finalDamage=event.getFinalDamage(),limit=CombatPower.minionHitLimit(maximum);if(Double.isFinite(finalDamage)&&finalDamage>limit)SafeDamage.scale(event,limit/finalDamage);}
 @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)public void defend(EntityDamageByEntityEvent event){if(event.getEntity() instanceof LivingEntity animal&&animal.getPersistentDataContainer().has(MARK,PersistentDataType.BYTE))capHit(event,animal.getMaxHealth());}
 void stop(){task.cancel();for(var unit:units.values())if(unit.entity().isValid())unit.entity().remove();units.clear();nextHit.clear();}
}
