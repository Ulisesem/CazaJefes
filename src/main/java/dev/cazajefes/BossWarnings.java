package dev.cazajefes;
import java.util.*;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
final class BossWarnings {
 private record Notice(Component text,long until){}
 private static final Map<UUID,Notice> notices=new HashMap<>();
 private static org.bukkit.scheduler.BukkitTask timer;
 static void start(org.bukkit.plugin.java.JavaPlugin plugin){clear();timer=plugin.getServer().getScheduler().runTaskTimer(plugin,()->{long now=System.currentTimeMillis();BossEffects.prune();var it=notices.entrySet().iterator();while(it.hasNext()){var entry=it.next();var p=plugin.getServer().getPlayer(entry.getKey());if(p==null||!p.isOnline()){it.remove();continue;}if(now>=entry.getValue().until()){p.sendActionBar(Component.empty());it.remove();}else p.sendActionBar(entry.getValue().text());}},5,5);}
 static boolean active(Player p){var n=notices.get(p.getUniqueId());return n!=null&&System.currentTimeMillis()<n.until();}
 static void send(Player p,Component text){notices.put(p.getUniqueId(),new Notice(text,System.currentTimeMillis()+3000));p.sendActionBar(text);}
 static void clear(){if(timer!=null)timer.cancel();timer=null;notices.clear();}
}
