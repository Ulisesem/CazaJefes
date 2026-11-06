package dev.cazajefes;
import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.command.CommandSender;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
final class BossAuras implements Listener {
 private record Active(LivingEntity entity,BossCatalog.Definition definition,BossParticles style){}
 private record FrameKey(UUID boss,int points){}
 private static final int FOLLOW_TICKS=2;
 private static final NamespacedKey QUALITY=new NamespacedKey("cazajefes","aura_quality");
 private final Map<UUID,Active> active=new LinkedHashMap<>();private final AuraBudget budget=new AuraBudget();private final BukkitTask task;private final CazaJefesPlugin plugin;private final int period;private double angle;private long frame;private int nextPlayer;
 BossAuras(CazaJefesPlugin plugin){this.plugin=plugin;period=AuraPolicy.interval(plugin.getConfig().getInt("particles.auras.interval-ticks",10));task=plugin.getServer().getScheduler().runTaskTimer(plugin,this::tick,1,FOLLOW_TICKS);}
 void track(LivingEntity boss,BossCatalog.Definition definition){var style=BossParticles.aura(definition);if(style.shape()!=BossParticles.Shape.OFF)active.put(boss.getUniqueId(),new Active(boss,definition,style));}
 private AuraQuality quality(Player player){AuraQuality value=AuraQuality.parse(player.getPersistentDataContainer().get(QUALITY,PersistentDataType.STRING));if(value!=null)return value;value=AuraQuality.parse(plugin.getConfig().getString("particles.auras.default-quality","media"));return value==null?AuraQuality.MEDIUM:value;}
 private void tick(){
  active.values().removeIf(e->!e.entity().isValid()||e.entity().isDead());
  if(active.isEmpty()||!plugin.getConfig().getBoolean("particles.auras.enabled",true))return;
  angle+=.32*FOLLOW_TICKS/10.0;
  frame++;
  var players=new ArrayList<>(plugin.getServer().getOnlinePlayers());
  if(players.isEmpty())return;

  int radius=AuraPolicy.radius(plugin.getConfig().getInt("particles.auras.radius",32));
  int limit=AuraPolicy.bosses(plugin.getConfig().getInt("particles.auras.max-bosses-per-player",2));
  int rate=Math.clamp(plugin.getConfig().getInt("particles.auras.max-sends-per-player-second",160),26,2048);
  int remaining=Math.clamp(plugin.getConfig().getInt("particles.auras.max-sends-per-frame",1024),64,8192);
  long now=System.nanoTime()/1000000L;
  var cache=new HashMap<FrameKey,CombatVisuals.Ambient>();
  var positions=new HashMap<Active,Location>();
  for(var entry:active.values())positions.put(entry,entry.entity().getLocation());

  int start=Math.floorMod(nextPlayer,players.size()),visited=0;
  for(;visited<players.size()&&remaining>=2;visited++){
   var player=players.get((start+visited)%players.size());
   AuraQuality requested=quality(player);
   if(player.isDead()||requested==AuraQuality.OFF)continue;
   var origin=player.getLocation();
   var selected=AuraPolicy.nearest(active.values(),entry->{
    var at=positions.get(entry);
    if(at.getWorld()!=origin.getWorld())return Double.POSITIVE_INFINITY;
    if(!player.canSee(entry.entity())&&!plugin.customEncounter.owns(entry.entity()))return Double.POSITIVE_INFINITY;
    if(!player.isChunkSent(Chunk.getChunkKey(at.getBlockX()>>4,at.getBlockZ()>>4)))return Double.POSITIVE_INFINITY;
    return origin.distanceSquared(at);
   },limit,radius*radius);
   if(selected.isEmpty())continue;
   int available=Math.min(remaining,budget.available(player.getUniqueId(),now,rate));
   for(var entry:selected){
    double distance=origin.distanceSquared(positions.get(entry));
    int cadence=distance<=256?switch(requested){case HIGH->1;case MEDIUM->2;case LOW,OFF->3;}
        :Math.max(3,(period+1)/FOLLOW_TICKS);
    if(frame%cadence!=0)continue;
    var detail=detail(requested,distance,plugin.customEncounter.attacking(entry.entity()));
    int points=pointsForFrame(rate,cadence,selected.size(),Math.min(detail.points,entry.style().points()),available);
    if(points==0)continue;
    int cost=points+1;
    if(!budget.spend(player.getUniqueId(),cost,now,rate))continue;
    var key=new FrameKey(entry.entity().getUniqueId(),points);
    var geometry=cache.computeIfAbsent(key,k->CombatVisuals.ambient(entry.entity(),entry.definition(),entry.style(),angle,points));
    CombatVisuals.ambient(player,geometry);
    remaining-=cost;
    available-=cost;
   }
  }
  nextPlayer=(start+Math.max(1,visited))%players.size();
 }
 static int pointsForFrame(int rate,int cadence,int bosses,int preferred,int available){
  // Share the refill for this interval rather than exhaust the bucket on one dense frame.
  int fairShare=Math.max(2,rate*cadence*FOLLOW_TICKS/(20*bosses));
  return Math.max(0,Math.min(preferred,Math.min(fairShare,available)-1));
 }
 static AuraQuality detail(AuraQuality chosen,double distanceSquared,boolean casting){
  if(chosen==AuraQuality.OFF)return chosen;
  return casting||distanceSquared>256?AuraQuality.LOW:chosen;
 }
 void command(CommandSender sender,String[] args){if(!(sender instanceof Player player)){sender.sendMessage("Elige la calidad desde un jugador.");return;}if(args.length!=2){player.sendMessage("Auras: "+quality(player).label+". Usa /ri particulas <baja|media|alta|off>.");return;}AuraQuality value=AuraQuality.parse(args[1]);if(value==null){player.sendMessage("Calidad: baja, media, alta u off.");return;}player.getPersistentDataContainer().set(QUALITY,PersistentDataType.STRING,value.label);player.sendMessage("✦ Calidad de auras: "+value.label+". Las advertencias de ataques siguen visibles.");}
 @EventHandler public void quit(PlayerQuitEvent event){budget.remove(event.getPlayer().getUniqueId());}
 void stop(){task.cancel();active.clear();budget.clear();}
}
