package dev.cazajefes;
import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.*;
/** One shared five-tick controller, no player teleports or location locking. */
final class AbyssalGaze implements Listener {
 static final long DURATION=5000,COOLDOWN=25000,CHARGE=2000;
 static boolean available(BossCatalog.Tier tier,int phase){return tier==BossCatalog.Tier.ABYSSAL&&phase==3;}
 private static final class State {long ready,release,end;}
 private record Lock(UUID owner,Player player,UUID world,float pitch,long end){}
 private final CazaJefesPlugin plugin;private final Map<UUID,State> states=new HashMap<>();private final Map<UUID,Lock> locks=new HashMap<>();private final Map<UUID,Long> immunity=new HashMap<>();
 AbyssalGaze(CazaJefesPlugin plugin){this.plugin=plugin;}
 boolean busy(UUID id){State s=states.get(id);return s!=null&&(s.release>0||s.end>System.currentTimeMillis());}
 private List<Player> players(LivingEntity boss){return boss.getWorld().getPlayers().stream().filter(p->CombatGuard.eligible(p)&&CombatHitbox.distanceSquared(p.getEyeLocation(),boss)<=144&&CombatHitbox.visible(p,boss)).limit(8).toList();}
 boolean tick(LivingEntity boss,BossCatalog.Definition d,int phase,long now,boolean blocked){UUID id=boss.getUniqueId();
  if(!available(d.tier(),phase)){remove(id);return false;}
  State s=states.computeIfAbsent(id,k->{State state=new State();state.ready=now+12500;return state;});
  if(s.end>0){if(now>=s.end){release(id);s.end=0;}else{for(Lock lock:new ArrayList<>(locks.values()))if(lock.owner().equals(id)){Player p=lock.player();if(!CombatGuard.eligible(p)||!p.getWorld().getUID().equals(lock.world())||p.getLocation().distanceSquared(boss.getLocation())>1024){unlock(p.getUniqueId());continue;}p.setRotation(p.getYaw(),-85);}return true;}}
  if(s.release>0){if(now<s.release){warn(players(boss),"⚠ CENIT DEL ABISMO · Cargando · ¡Aléjate!");SpellVisuals.charge(boss,d,(int)(now%1000/50));return true;}
   s.release=0;s.ready=now+COOLDOWN;s.end=now+DURATION;for(Player p:players(boss)){if(now<immunity.getOrDefault(p.getUniqueId(),0L))continue;locks.put(p.getUniqueId(),new Lock(id,p,p.getWorld().getUID(),p.getPitch(),s.end));immunity.put(p.getUniqueId(),now+COOLDOWN);p.setRotation(p.getYaw(),-85);}
   boss.getWorld().playSound(boss.getLocation(),Sound.ENTITY_WARDEN_SONIC_BOOM,.8f,.6f);CombatVisuals.special(boss,d);warn(players(boss),"✦ CENIT DEL ABISMO · Mirada sellada · 5 s");return true;
  }
  if(!blocked&&now>=s.ready&&!players(boss).isEmpty()){s.release=now+CHARGE;warn(players(boss),"⚠ CENIT DEL ABISMO · Cargando · ¡Aléjate!");return true;}return false;
 }
 private void warn(List<Player> players,String text){for(Player p:players)BossWarnings.send(p,Component.text(text,NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD));}
 private void unlock(UUID id){Lock lock=locks.remove(id);if(lock!=null&&lock.player().isOnline()&&!lock.player().isDead()&&lock.player().getWorld().getUID().equals(lock.world()))lock.player().setRotation(lock.player().getYaw(),lock.pitch());}
 private void release(UUID boss){for(Lock lock:new ArrayList<>(locks.values()))if(lock.owner().equals(boss))unlock(lock.player().getUniqueId());}
 void remove(UUID boss){release(boss);states.remove(boss);}
 @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)public void turn(PlayerMoveEvent event){Lock lock=locks.get(event.getPlayer().getUniqueId());if(lock==null)return;if(System.currentTimeMillis()>=lock.end()||!CombatGuard.eligible(event.getPlayer())||!event.getPlayer().getWorld().getUID().equals(lock.world())){unlock(event.getPlayer().getUniqueId());return;}if(event.getTo()!=null){Location to=event.getTo().clone();to.setPitch(-85);event.setTo(to);}}
 @EventHandler public void quit(PlayerQuitEvent event){locks.remove(event.getPlayer().getUniqueId());immunity.remove(event.getPlayer().getUniqueId());}
 void stop(){for(UUID id:new ArrayList<>(locks.keySet()))unlock(id);states.clear();immunity.clear();}
}
