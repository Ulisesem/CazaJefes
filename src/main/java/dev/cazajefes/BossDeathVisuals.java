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
 private final CazaJefesPlugin plugin;private final Map<UUID,BukkitRunnable> tasks=new HashMap<>();private final Map<UUID,DeathCompletion> rewards=new HashMap<>();
 BossDeathVisuals(CazaJefesPlugin plugin){this.plugin=plugin;}
 private boolean visual(Entity e){return e.getPersistentDataContainer().has(VISUAL,PersistentDataType.BYTE);}
 void show(LivingEntity original,BossCatalog.Definition definition,Runnable reward){DeathCompletion completion=new DeathCompletion(reward);Location start=original.getLocation().clone();
  original.setInvisible(true);for(Player viewer:plugin.getServer().getOnlinePlayers())viewer.hideEntity(plugin,original);
  for(Player player:start.getWorld().getPlayers())if(player.getLocation().distanceSquared(start)<=144){player.sendActionBar(net.kyori.adventure.text.Component.text("⚠ CAÍDA FINAL · ¡Aléjate del boss!",net.kyori.adventure.text.format.NamedTextColor.RED));}
  try{
  Entity ghost=start.getWorld().spawnEntity(start,original.getType(),CreatureSpawnEvent.SpawnReason.CUSTOM,e->{e.getPersistentDataContainer().set(VISUAL,PersistentDataType.BYTE,(byte)1);e.setPersistent(false);e.setSilent(true);e.setInvulnerable(true);e.setGravity(false);e.customName(original.customName());e.setCustomNameVisible(true);if(e instanceof LivingEntity living){living.setCollidable(false);living.setCanPickupItems(false);if(living instanceof Mob mob)mob.setAI(false);var scale=living.getAttribute(Attribute.SCALE);var old=original.getAttribute(Attribute.SCALE);if(scale!=null&&old!=null)scale.setBaseValue(old.getBaseValue());if(living.getEquipment()!=null&&original.getEquipment()!=null){living.getEquipment().setArmorContents(original.getEquipment().getArmorContents());living.getEquipment().setItemInMainHand(original.getEquipment().getItemInMainHand().clone());living.getEquipment().setItemInOffHand(original.getEquipment().getItemInOffHand().clone());for(var slot:new org.bukkit.inventory.EquipmentSlot[]{org.bukkit.inventory.EquipmentSlot.HEAD,org.bukkit.inventory.EquipmentSlot.CHEST,org.bukkit.inventory.EquipmentSlot.LEGS,org.bukkit.inventory.EquipmentSlot.FEET,org.bukkit.inventory.EquipmentSlot.HAND,org.bukkit.inventory.EquipmentSlot.OFF_HAND})living.getEquipment().setDropChance(slot,0);}}});
  double rise=Math.max(0,Math.min(15,start.getWorld().getMaxHeight()-start.getY()-original.getHeight()-1));
  UUID id=ghost.getUniqueId();
  BukkitRunnable task=new BukkitRunnable(){int tick;
   private void finish(){tasks.remove(id);rewards.remove(id);ghost.remove();cancel();completion.finish();}
   public void run(){
    tick++;
    if(tick>DeathFlight.END){if(tick>=DeathFlight.END+6)finish();return;}
    if(!ghost.isValid()){finish();return;}
    Location at=start.clone().add(0,DeathFlight.height(tick,rise),0);ghost.teleport(at);
    if(tick==DeathFlight.PEAK)start.getWorld().playSound(at,Sound.ENTITY_WITHER_SPAWN,.6f,1.6f);
    if(tick%10==0){CombatVisuals.signatureRing(start,definition,6,24);for(Player player:start.getWorld().getPlayers())if(player.getLocation().distanceSquared(start)<=144)player.sendActionBar(net.kyori.adventure.text.Component.text("⚠ IMPACTO FINAL · Sal del círculo",net.kyori.adventure.text.format.NamedTextColor.RED));}
    if(tick%2==0){at.getWorld().spawnParticle(Particle.END_ROD,at.clone().add(0,1,0),5,.5,.6,.5,.015);at.getWorld().spawnParticle(Particle.SOUL,at,4,.4,.5,.4,.01);}
    if(tick==DeathFlight.END){try{blast(start);CombatVisuals.signatureRing(start,definition,2,24);start.getWorld().spawnParticle(Particle.FIREWORK,start.clone().add(0,1,0),30,.8,.8,.8,.03);}finally{ghost.remove();}}
   }
  };
  tasks.put(id,task);rewards.put(id,completion);task.runTaskTimer(plugin,1,1);
 }catch(IllegalArgumentException error){plugin.getLogger().warning("No se pudo mostrar la animación de muerte de "+definition.id()+": "+error.getMessage());completion.finish();}}
 private final Map<UUID,Double> blastDamage=new HashMap<>();
 private final Map<UUID,EntityDamageEvent> blastEvents=new HashMap<>();
 private void blast(Location center){
  center.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER,center.clone().add(0,1,0),1);
  center.getWorld().playSound(center,Sound.ENTITY_GENERIC_EXPLODE,1.3f,.7f);
  center.getWorld().strikeLightningEffect(center);
  for(int i=0;i<4;i++){double angle=i*Math.PI/2;center.getWorld().strikeLightningEffect(center.clone().add(Math.cos(angle)*3.5,0,Math.sin(angle)*3.5));}
  for(Player player:center.getWorld().getPlayers()){
   if(player.isDead()||!player.isOnline()||(player.getGameMode()!=GameMode.SURVIVAL&&player.getGameMode()!=GameMode.ADVENTURE)||!DeathFlight.inRange(player.getLocation().distanceSquared(center)))continue;
   UUID id=player.getUniqueId();blastDamage.put(id,DeathFlight.damage(java.util.concurrent.ThreadLocalRandom.current().nextDouble()));
   try{player.damage(blastDamage.get(id));EntityDamageEvent event=blastEvents.get(id);if(event!=null&&!event.isCancelled()&&event.getFinalDamage()>0){
    VectorBack.push(player,center);
    player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.SLOWNESS,120,1));
    player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.WEAKNESS,160,0));
    player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.BLINDNESS,40,0));
   }}finally{blastDamage.remove(id);blastEvents.remove(id);}
  }
 }
 @SuppressWarnings("deprecation") static void fixedBlastDamage(EntityDamageEvent event,double amount){
  if(event.isCancelled())return;
  for(var modifier:EntityDamageEvent.DamageModifier.values())if(event.isApplicable(modifier)&&modifier!=EntityDamageEvent.DamageModifier.INVULNERABILITY_REDUCTION&&modifier!=EntityDamageEvent.DamageModifier.BLOCKING)event.setDamage(modifier,modifier==EntityDamageEvent.DamageModifier.BASE?amount:0);
  SafeDamage.normalize(event);
 }
 @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true) public void explosionDamage(EntityDamageEvent event){Double amount=blastDamage.get(event.getEntity().getUniqueId());if(amount!=null&&event.getCause()==EntityDamageEvent.DamageCause.CUSTOM){fixedBlastDamage(event,amount);blastEvents.put(event.getEntity().getUniqueId(),event);}}
 @EventHandler(priority=EventPriority.LOWEST)public void damage(EntityDamageEvent event){if(visual(event.getEntity()))event.setCancelled(true);}
 @EventHandler(priority=EventPriority.HIGHEST)public void death(EntityDeathEvent event){if(visual(event.getEntity())){event.getDrops().clear();event.setDroppedExp(0);}}
 void stop(){for(var entry:tasks.entrySet()){entry.getValue().cancel();Entity e=Bukkit.getEntity(entry.getKey());if(e!=null)e.remove();}tasks.clear();var pending=new ArrayList<>(rewards.values());rewards.clear();for(var completion:pending)completion.finish();}
}
