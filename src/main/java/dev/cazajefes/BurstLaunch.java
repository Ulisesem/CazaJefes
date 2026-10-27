package dev.cazajefes;
import java.util.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;
final class BurstLaunch implements Listener {
 private static final class Trace {long at;double requested,observed;boolean cancelled,applied,retried;String status="en espera";}
 private final CazaJefesPlugin plugin;private final Map<UUID,Trace> traces=new HashMap<>();
 BurstLaunch(CazaJefesPlugin plugin){this.plugin=plugin;}
 static boolean retry(double requested,double observed,boolean cancelled){return !cancelled&&requested>1&&Double.isFinite(observed)&&observed<requested*.6;}
 void launch(Player player,Vector velocity){Trace trace=new Trace();trace.at=System.currentTimeMillis();trace.requested=velocity.getY();traces.put(player.getUniqueId(),trace);var world=player.getWorld();Vector motion=velocity.clone();
  plugin.getServer().getScheduler().runTask(plugin,()->{if(traces.get(player.getUniqueId())!=trace||!CombatGuard.eligible(player)||player.isOp()||!player.getWorld().equals(world)){trace.status="objetivo no disponible";return;}trace.applied=true;trace.at=System.currentTimeMillis();trace.status="aplicado";player.setVelocity(motion.clone());
   plugin.getServer().getScheduler().runTaskLater(plugin,()->{if(traces.get(player.getUniqueId())!=trace||!CombatGuard.eligible(player)||player.isOp()||!player.getWorld().equals(world))return;trace.observed=player.getVelocity().getY();if(retry(trace.requested,trace.observed,trace.cancelled)){player.setVelocity(motion.clone());trace.retried=true;trace.status="reforzado una vez";}},2);
   plugin.getServer().getScheduler().runTaskLater(plugin,()->{if(traces.get(player.getUniqueId())==trace&&player.isOnline()){trace.observed=player.getVelocity().getY();if(trace.cancelled)trace.status="evento de velocidad cancelado";}},5);
  });
 }
 @EventHandler(priority=EventPriority.MONITOR)public void velocity(PlayerVelocityEvent event){Trace trace=traces.get(event.getPlayer().getUniqueId());if(trace!=null&&trace.applied&&System.currentTimeMillis()-trace.at<750&&event.isCancelled())trace.cancelled=true;}
 String describe(Player player){Trace trace=traces.get(player.getUniqueId());return trace==null?"sin lanzamiento registrado; OP="+player.isOp():"hace="+(System.currentTimeMillis()-trace.at)/1000+"s estado="+trace.status+" Y_solicitada="+trace.requested+" Y_observada="+trace.observed+" cancelado="+trace.cancelled+" refuerzo="+trace.retried;}
 @EventHandler public void quit(PlayerQuitEvent event){traces.remove(event.getPlayer().getUniqueId());}
 void stop(){traces.clear();}
}
