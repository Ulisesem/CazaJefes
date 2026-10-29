package dev.cazajefes;
import java.util.*;
import org.bukkit.*;
import org.bukkit.attribute.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.*;
/** One bounded armor refresh, strongest level per power, scoped health modifier. */
final class ArmorRares implements org.bukkit.event.Listener {
 private final RelicItems items;private final org.bukkit.plugin.java.JavaPlugin plugin;
 private static final NamespacedKey HEARTS=new NamespacedKey("cazajefes","indomitable_hearts");
 private final Map<UUID,Map<UUID,Long>> adversaries=new HashMap<>();
 private final Map<UUID,Long> fighting=new HashMap<>(),nextSpore=new HashMap<>(),nextRegen=new HashMap<>();
 ArmorRares(RelicItems items,org.bukkit.plugin.java.JavaPlugin plugin){this.items=items;this.plugin=plugin;plugin.getServer().getPluginManager().registerEvents(this,plugin);}
 ItemStack strongest(Player p,String power){ItemStack best=null;int level=0;for(var piece:p.getInventory().getArmorContents()){int n=items.activePropertyLevel(piece,power);if(n>level){best=piece;level=n;}}return best;}
 int level(Player p,String power){return Math.min(ArmorRareRules.maximum(power),items.activePropertyLevel(strongest(p,power),power));}
 boolean counter(Player p,String power){return Math.random()<ArmorRareRules.counter(level(p,power));}
 void tick(Player p){long stamp=System.currentTimeMillis();var foes=adversaries.get(p.getUniqueId());if(foes!=null){foes.values().removeIf(end->stamp-end>10000);if(foes.isEmpty())adversaries.remove(p.getUniqueId());}var attribute=p.getAttribute(Attribute.MAX_HEALTH);if(attribute!=null){var old=attribute.getModifier(HEARTS);double wanted=ArmorRareRules.hearts(level(p,"hardy"));if(old!=null&&old.getAmount()!=wanted)attribute.removeModifier(HEARTS);if(wanted>0&&(old==null||old.getAmount()!=wanted))attribute.addModifier(new AttributeModifier(HEARTS,wanted,AttributeModifier.Operation.ADD_NUMBER));if(p.getHealth()>attribute.getValue())p.setHealth(attribute.getValue());}
  for(String id:new String[]{"strength"}){int n=level(p,id);if(n>0&&items.armorFuel(p,strongest(p,id),id))p.addPotionEffect(new PotionEffect(id.equals("strength")?PotionEffectType.STRENGTH:PotionEffectType.REGENERATION,40,n-1,true,false,true));}
  long now=System.currentTimeMillis();int regen=level(p,"regeneration");if(regen>0&&!p.isDead()&&p.getHealth()<p.getMaxHealth()&&now>=nextRegen.getOrDefault(p.getUniqueId(),0L)){var heal=new org.bukkit.event.entity.EntityRegainHealthEvent(p,regen,org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason.CUSTOM);plugin.getServer().getPluginManager().callEvent(heal);if(!heal.isCancelled()&&Double.isFinite(heal.getAmount())&&heal.getAmount()>0){p.setHealth(Math.min(p.getMaxHealth(),p.getHealth()+Math.min(regen,heal.getAmount())));nextRegen.put(p.getUniqueId(),now+10000);}}
  if(now-fighting.getOrDefault(p.getUniqueId(),0L)>10000||now<nextSpore.getOrDefault(p.getUniqueId(),0L)||!CombatGuard.eligible(p)||!p.getWorld().getPVP())return;int n=level(p,"aura_esporas");if(n==0)return;
  var targets=p.getNearbyEntities(4,4,4).stream().filter(e->e instanceof Player q&&enemy(p,q)&&p.getLocation().distanceSquared(q.getLocation())<=16&&p.hasLineOfSight(q)&&now-adversaries.getOrDefault(p.getUniqueId(),Map.of()).getOrDefault(q.getUniqueId(),0L)<=10000).map(e->(Player)e).toList();if(targets.isEmpty()||!items.armorFuel(p,strongest(p,"aura_esporas"),"aura_esporas"))return;nextSpore.put(p.getUniqueId(),now+5000);
  for(Player target:targets)if(now-adversaries.getOrDefault(p.getUniqueId(),Map.of()).getOrDefault(target.getUniqueId(),0L)<=10000)target.addPotionEffect(new PotionEffect(PotionEffectType.POISON,60,n-1));
  CosmeticFX.emit(Particle.DUST,p.getLocation().add(0,1,0),8,.7,.6,.7,0,new Particle.DustOptions(Color.fromRGB(130,220,65),.5f));
 }
 private boolean enemy(Player p,Player q){if(!CombatGuard.eligible(q)||p.equals(q))return false;var a=p.getScoreboard().getEntryTeam(p.getName());var b=p.getScoreboard().getEntryTeam(q.getName());return a==null||b==null||!a.equals(b);}
 @org.bukkit.event.EventHandler(priority=org.bukkit.event.EventPriority.MONITOR,ignoreCancelled=true)public void hit(org.bukkit.event.entity.EntityDamageByEntityEvent e){if(e.getFinalDamage()<=0)return;Entity source=e.getDamager();if(source instanceof Projectile shot&&shot.getShooter() instanceof Entity owner)source=owner;if(source instanceof Player a&&e.getEntity() instanceof Player b&&enemy(a,b)){long now=System.currentTimeMillis();fighting.put(a.getUniqueId(),now);fighting.put(b.getUniqueId(),now);adversaries.computeIfAbsent(a.getUniqueId(),id->new HashMap<>()).put(b.getUniqueId(),now);adversaries.computeIfAbsent(b.getUniqueId(),id->new HashMap<>()).put(a.getUniqueId(),now);}}
 @org.bukkit.event.EventHandler public void quit(org.bukkit.event.player.PlayerQuitEvent e){remove(e.getPlayer());adversaries.remove(e.getPlayer().getUniqueId());fighting.remove(e.getPlayer().getUniqueId());nextSpore.remove(e.getPlayer().getUniqueId());}
 private void remove(Player p){var health=p.getAttribute(Attribute.MAX_HEALTH);if(health!=null){health.removeModifier(HEARTS);if(p.getHealth()>health.getValue())p.setHealth(health.getValue());}}
 void stop(){for(Player p:plugin.getServer().getOnlinePlayers())remove(p);adversaries.clear();fighting.clear();nextSpore.clear();nextRegen.clear();}
}
