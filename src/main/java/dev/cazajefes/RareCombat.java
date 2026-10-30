package dev.cazajefes;

import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.*;

/** Prepares damage modifiers, then charges and activates only accepted hits. */
final class RareCombat implements Listener {
 private record Proc(String id,int level,float cost){}
 private record Hit(Player player,LivingEntity target,List<Proc> procs,double health){}
 private final CazaJefesPlugin plugin;private final RelicItems items;
 private final Map<EntityDamageByEntityEvent,Hit> pending=new WeakHashMap<>();
 private final Map<UUID,Long> hungerReady=new HashMap<>();
 private final NamespacedKey arrow=new NamespacedKey("cazajefes","infused_projectile");
 private boolean secondary;
 RareCombat(CazaJefesPlugin plugin,RelicItems items){this.plugin=plugin;this.items=items;}
 static boolean behind(double dot){return dot<-.05;}
 static boolean accepted(boolean cancelled,double damage){return !cancelled&&Double.isFinite(damage)&&damage>0;}
 static boolean enemy(Player a,Player b){if(a.equals(b)||!CombatGuard.eligible(b)||!a.getWorld().getPVP())return false;var team=a.getScoreboard().getEntryTeam(a.getName());return team==null||!team.hasEntry(b.getName());}
 private Map<String,Integer> powers(ItemStack item){Map<String,Integer> out=new LinkedHashMap<>();if(item==null)return out;for(String id:items.catalogIds()){int n=items.activePropertyLevel(item,id);if(n>0)out.put(id,n);}return out;}
 @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)public void trident(ProjectileLaunchEvent e){if(e.getEntity() instanceof Trident trident&&trident.getShooter() instanceof Player){String data=powers(trident.getItemStack()).entrySet().stream().filter(v->RareCompatibility.property(v.getKey())==RareCompatibility.Group.RANGED).map(v->v.getKey()+":"+v.getValue()).collect(java.util.stream.Collectors.joining(","));if(!data.isEmpty())trident.getPersistentDataContainer().set(arrow,PersistentDataType.STRING,data);}}
 @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)public void shoot(EntityShootBowEvent e){if(e.getEntity() instanceof Player){String data=powers(e.getBow()).entrySet().stream().filter(v->RareCompatibility.property(v.getKey())==RareCompatibility.Group.RANGED).map(v->v.getKey()+":"+v.getValue()).collect(java.util.stream.Collectors.joining(","));if(!data.isEmpty())e.getProjectile().getPersistentDataContainer().set(arrow,PersistentDataType.STRING,data);}}
 private Map<String,Integer> snapshot(EntityDamageByEntityEvent e,Player player){if(!(e.getDamager() instanceof Projectile shot))return powers(player.getInventory().getItemInMainHand());Map<String,Integer> out=new LinkedHashMap<>();String data=shot.getPersistentDataContainer().get(arrow,PersistentDataType.STRING);if(data!=null)for(String value:data.split(",")){String[] pair=value.split(":");if(pair.length==2)try{if(RareDefinitions.get(pair[0])!=null&&RareCompatibility.property(pair[0])==RareCompatibility.Group.RANGED)out.put(pair[0],RareDefinitions.limited(pair[0],Integer.parseInt(pair[1])));}catch(NumberFormatException ignored){}}return out;}
 @EventHandler(priority=EventPriority.HIGH,ignoreCancelled=true)public void prepare(EntityDamageByEntityEvent e){
  if(secondary||RangedRares.secondary||!accepted(e.isCancelled(),e.getFinalDamage())||!(e.getEntity() instanceof LivingEntity target))return;Entity source=e.getDamager();if(source instanceof Projectile p&&p.getShooter() instanceof Entity owner)source=owner;if(!(source instanceof Player player)||!CombatGuard.eligible(player)||target instanceof Player victim&&!enemy(player,victim))return;
  Map<String,Integer> powers=snapshot(e,player);List<Proc> procs=new ArrayList<>();float available=player.getSaturation();ItemStack held=player.getInventory().getItemInMainHand();
  for(var entry:powers.entrySet()){String id=entry.getKey();int n=entry.getValue();boolean trigger=switch(id){case "backstab"->{var v=player.getLocation().toVector().subtract(target.getLocation().toVector()).setY(0);var facing=target.getLocation().getDirection().setY(0);yield v.lengthSquared()>.001&&facing.lengthSquared()>.001&&behind(facing.normalize().dot(v.normalize()))&&!(target instanceof Player victim&&items.armorCounter(victim,"turtle"));}case "execute"->target.getHealth()/target.getMaxHealth()<RareDefinitions.executeThreshold(n);case "vampiricregeneration","vampirismo"->Math.random()<.20&&!(target instanceof Player victim&&items.armorCounter(victim,"garlic"));case "calllighting"->Math.random()<.10;case "poison","veneno","bleed"->true;case "disarm"->target instanceof Player victim&&!victim.getInventory().getItemInMainHand().getType().isAir()&&victim.getInventory().firstEmpty()>=0&&Math.random()<.25;case "blinding","confuse","slow","weaken","brasas"->Math.random()<.25;default->false;};if(!trigger)continue;var definition=RareDefinitions.get(id);float cost=items.saturationCost(held,id);if(cost<0||cost>available)continue;available-=cost;procs.add(new Proc(id,n,cost));if(id.equals("backstab"))SafeDamage.scale(e,1.5);if(id.equals("execute"))SafeDamage.scale(e,2);}
  pending.put(e,new Hit(player,target,procs,target.getHealth()));
 }
 @EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)public void commit(EntityDamageByEntityEvent e){Hit hit=pending.remove(e);if(hit==null||!accepted(e.isCancelled(),e.getFinalDamage()))return;double dealt=Math.min(e.getFinalDamage(),hit.health());for(Proc proc:hit.procs()){if(hit.player().getSaturation()<proc.cost())continue;hit.player().setSaturation(Math.max(0,hit.player().getSaturation()-proc.cost()));plugin.getServer().getScheduler().runTask(plugin,()->activate(hit,proc,dealt));}if(!(e.getDamager() instanceof Projectile))stealFood(hit.player(),hit.target());}
 private void activate(Hit hit,Proc proc,double dealt){Player a=hit.player();LivingEntity v=hit.target();if(!a.isOnline()||a.isDead())return;int n=proc.level();String id=proc.id();if(id.equals("vampiricregeneration")||id.equals("vampirismo")){double amount=Math.min(4,dealt*.25);var heal=new EntityRegainHealthEvent(a,amount,EntityRegainHealthEvent.RegainReason.CUSTOM);Bukkit.getPluginManager().callEvent(heal);if(!heal.isCancelled()&&Double.isFinite(heal.getAmount())&&heal.getAmount()>0)a.setHealth(Math.min(a.getMaxHealth(),a.getHealth()+Math.min(amount,heal.getAmount())));return;}if(!v.isValid()||v.isDead()||!a.getWorld().equals(v.getWorld())||v instanceof Player p&&!enemy(a,p))return;
  switch(id){case "poison","veneno"->effect(v,PotionEffectType.POISON,80,n-1);case "blinding"->effect(v,PotionEffectType.BLINDNESS,60,0);case "confuse"->effect(v,PotionEffectType.NAUSEA,100,0);case "slow"->effect(v,PotionEffectType.SLOWNESS,80,0);case "weaken"->effect(v,PotionEffectType.WEAKNESS,80,0);case "bleed"->effect(v,PotionEffectType.WITHER,40*n,0);case "brasas"->v.setFireTicks(Math.max(v.getFireTicks(),40+20*n));case "disarm"->{if(v instanceof Player p){var inv=p.getInventory();int slot=inv.firstEmpty();ItemStack weapon=inv.getItemInMainHand();if(slot>=0&&!weapon.getType().isAir()&&slot!=inv.getHeldItemSlot()){inv.setItem(slot,weapon.clone());inv.setItemInMainHand(null);p.playSound(p.getLocation(),Sound.ITEM_ARMOR_EQUIP_CHAIN,.6f,.8f);}}}case "calllighting"->{v.getWorld().strikeLightningEffect(v.getLocation());secondary=true;try{v.damage(2.0*n,a);}finally{secondary=false;}}default->{}}
 }
 void effect(LivingEntity target,PotionEffectType type,int duration,int amplifier){int clarity=target instanceof Player p?items.armorLevel(p,"clarity"):0;int ticks=(type==PotionEffectType.BLINDNESS||type==PotionEffectType.NAUSEA)?RareDefinitions.clarityTicks(duration,clarity):duration;target.addPotionEffect(new PotionEffect(type,ticks,Math.max(0,Math.min(3,amplifier))));}
 private void stealFood(Player player,LivingEntity target){if(!(target instanceof Player victim)||!enemy(player,victim))return;int n=items.armorLevel(player,"hungersteal");long now=System.currentTimeMillis();if(n<=0||now<hungerReady.getOrDefault(player.getUniqueId(),0L))return;int amount=Math.min(n,Math.min(victim.getFoodLevel(),20-player.getFoodLevel()));if(amount<=0)return;victim.setFoodLevel(victim.getFoodLevel()-amount);victim.setSaturation(Math.min(victim.getSaturation(),victim.getFoodLevel()));player.setFoodLevel(player.getFoodLevel()+amount);hungerReady.put(player.getUniqueId(),now+10000);}
 @EventHandler public void quit(PlayerQuitEvent e){plugin.getServer().getScheduler().runTaskLater(plugin,()->{long now=System.currentTimeMillis();hungerReady.values().removeIf(end->end<=now);},220);}
 void stop(){pending.clear();hungerReady.clear();}
}
