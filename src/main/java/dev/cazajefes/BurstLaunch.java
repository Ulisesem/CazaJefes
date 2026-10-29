package dev.cazajefes;
import java.util.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;
final class BurstLaunch implements Listener {
 private static final class Trace {long at;double requested,observed;boolean cancelled,applied,retried,lifting,potionApplied,allowFlight,flying;Player target;org.bukkit.GameMode mode;String status="en espera";}
 private final CazaJefesPlugin plugin;private final Map<UUID,Trace> traces=new HashMap<>();
 BurstLaunch(CazaJefesPlugin plugin){this.plugin=plugin;}
 static boolean retry(double requested,double observed,boolean cancelled){return !cancelled&&requested>1&&Double.isFinite(observed)&&observed<requested*.6;}
 static boolean sustainedLift(double y){return Double.isFinite(y)&&y>1;}
 boolean lifting(Player player){Trace trace=traces.get(player.getUniqueId());return trace!=null&&trace.lifting&&player.getGameMode()==trace.mode&&CombatGuard.eligible(player)&&!player.isOp();}
 private void endLift(Trace trace){if(!trace.lifting)return;trace.lifting=false;Player player=trace.target;if(player!=null&&player.isOnline()&&player.getGameMode()==trace.mode){if(trace.allowFlight)player.setAllowFlight(true);if(trace.flying&&player.getAllowFlight())player.setFlying(true);}}
 void launch(Player player,Vector velocity){launch(player,velocity,true);}
 void launch(Player player,Vector velocity,boolean lift){Trace previous=traces.get(player.getUniqueId());if(previous!=null)endLift(previous);Trace trace=new Trace();trace.at=System.currentTimeMillis();trace.requested=velocity.getY();traces.put(player.getUniqueId(),trace);var world=player.getWorld();Vector motion=velocity.clone();
  plugin.getServer().getScheduler().runTask(plugin,()->{if(traces.get(player.getUniqueId())!=trace||!CombatGuard.eligible(player)||player.isOp()||!player.getWorld().equals(world)){trace.status="objetivo no disponible";return;}trace.applied=true;trace.at=System.currentTimeMillis();trace.status="aplicado";player.setVelocity(motion.clone());
   if(lift&&sustainedLift(trace.requested)&&!trace.cancelled){trace.target=player;trace.mode=player.getGameMode();trace.allowFlight=player.getAllowFlight();trace.flying=player.isFlying();trace.lifting=true;player.setFlying(false);player.setAllowFlight(false);trace.potionApplied=player.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.LEVITATION,20,9,true,false,false));trace.status=trace.potionApplied?"ascenso X activo":"impulso aplicado; levitación no aceptada";plugin.getServer().getScheduler().runTaskLater(plugin,()->endLift(trace),22);}

   plugin.getServer().getScheduler().runTaskLater(plugin,()->{if(traces.get(player.getUniqueId())!=trace||!CombatGuard.eligible(player)||player.isOp()||!player.getWorld().equals(world))return;trace.observed=player.getVelocity().getY();if(retry(trace.requested,trace.observed,trace.cancelled)){player.setVelocity(motion.clone());trace.retried=true;trace.status="reforzado una vez";}},2);
   plugin.getServer().getScheduler().runTaskLater(plugin,()->{if(traces.get(player.getUniqueId())==trace&&player.isOnline()){trace.observed=player.getVelocity().getY();if(trace.cancelled)trace.status="evento de velocidad cancelado";}},5);
  });
 }
 @EventHandler(priority=EventPriority.MONITOR)public void velocity(PlayerVelocityEvent event){Trace trace=traces.get(event.getPlayer().getUniqueId());if(trace!=null&&trace.applied&&System.currentTimeMillis()-trace.at<750&&event.isCancelled())trace.cancelled=true;}
 String describe(Player player){Trace trace=traces.get(player.getUniqueId());return trace==null?"sin lanzamiento registrado; OP="+player.isOp():"hace="+(System.currentTimeMillis()-trace.at)/1000+"s estado="+trace.status+" Y_solicitada="+trace.requested+" Y_observada="+trace.observed+" cancelado="+trace.cancelled+" refuerzo="+trace.retried+" levitación="+trace.potionApplied;}
 @EventHandler public void quit(PlayerQuitEvent event){Trace trace=traces.remove(event.getPlayer().getUniqueId());if(trace!=null)endLift(trace);}
 void stop(){for(var trace:traces.values())endLift(trace);traces.clear();}
}
