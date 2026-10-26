package dev.cazajefes;
import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.command.CommandSender;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
final class BossAuras implements Listener {
 private record Active(LivingEntity entity,BossCatalog.Definition definition){}
 private record FrameKey(UUID boss,AuraQuality quality){}
 private static final NamespacedKey QUALITY=new NamespacedKey("cazajefes","aura_quality");
 private final Map<UUID,Active> active=new LinkedHashMap<>();private final AuraBudget budget=new AuraBudget();private final BukkitTask task;private final JavaPlugin plugin;private final int period;private double angle;private long frame;private int nextPlayer;
 BossAuras(JavaPlugin plugin){this.plugin=plugin;period=AuraPolicy.interval(plugin.getConfig().getInt("particles.auras.interval-ticks",10));task=plugin.getServer().getScheduler().runTaskTimer(plugin,this::tick,1,period);}
 void track(LivingEntity boss,BossCatalog.Definition definition){active.put(boss.getUniqueId(),new Active(boss,definition));}
 private AuraQuality quality(Player player){AuraQuality value=AuraQuality.parse(player.getPersistentDataContainer().get(QUALITY,PersistentDataType.STRING));if(value!=null)return value;value=AuraQuality.parse(plugin.getConfig().getString("particles.auras.default-quality","media"));return value==null?AuraQuality.MEDIUM:value;}
 private void tick(){
  active.values().removeIf(e->!e.entity().isValid()||e.entity().isDead());if(active.isEmpty()||!plugin.getConfig().getBoolean("particles.auras.enabled",true))return;
  angle+=.32*period/10.0;frame++;List<Player> players=new ArrayList<>(plugin.getServer().getOnlinePlayers());if(players.isEmpty())return;
  int radius=AuraPolicy.radius(plugin.getConfig().getInt("particles.auras.radius",32)),limit=AuraPolicy.bosses(plugin.getConfig().getInt("particles.auras.max-bosses-per-player",2));
  int rate=Math.max(26,Math.min(2048,plugin.getConfig().getInt("particles.auras.max-sends-per-player-second",160))),remaining=Math.max(64,Math.min(8192,plugin.getConfig().getInt("particles.auras.max-sends-per-frame",1024)));
  long now=System.nanoTime()/1000000L;Map<FrameKey,CombatVisuals.Ambient> cache=new HashMap<>();List<Active> live=new ArrayList<>(active.values());int start=Math.floorMod(nextPlayer,players.size()),visited=0;
  for(;visited<players.size()&&remaining>=AuraQuality.LOW.sends();visited++){
   Player player=players.get((start+visited)%players.size());if(!player.isOnline()||player.isDead())continue;AuraQuality requested=quality(player);if(requested==AuraQuality.OFF||frame%requested.every!=0)continue;
   var selected=AuraPolicy.nearest(live,e->e.entity().getWorld().equals(player.getWorld())&&player.canSee(e.entity())?e.entity().getLocation().distanceSquared(player.getLocation()):Double.POSITIVE_INFINITY,limit,radius*radius);
   int available=Math.min(remaining,budget.available(player.getUniqueId(),now,rate));
   for(var entry:selected){AuraQuality fitted=AuraQuality.fit(requested,available);if(fitted==AuraQuality.OFF)break;int cost=fitted.sends();if(!budget.spend(player.getUniqueId(),cost,now,rate))break;var key=new FrameKey(entry.entity().getUniqueId(),fitted);var geometry=cache.computeIfAbsent(key,k->CombatVisuals.ambient(entry.entity(),entry.definition(),angle,fitted.points));CombatVisuals.ambient(player,geometry);remaining-=cost;available-=cost;}
  }
  nextPlayer=(start+visited)%players.size();
 }
 void command(CommandSender sender,String[] args){if(!(sender instanceof Player player)){sender.sendMessage("Elige la calidad desde un jugador.");return;}if(args.length!=2){player.sendMessage("Auras: "+quality(player).label+". Usa /ri particulas <baja|media|alta|off>.");return;}AuraQuality value=AuraQuality.parse(args[1]);if(value==null){player.sendMessage("Calidad: baja, media, alta u off.");return;}player.getPersistentDataContainer().set(QUALITY,PersistentDataType.STRING,value.label);player.sendMessage("✦ Calidad de auras: "+value.label+". Las advertencias de ataques siguen visibles.");}
 @EventHandler public void quit(PlayerQuitEvent event){budget.remove(event.getPlayer().getUniqueId());}
 void stop(){task.cancel();active.clear();budget.clear();}
}
