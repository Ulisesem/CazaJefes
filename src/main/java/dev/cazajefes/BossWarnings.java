package dev.cazajefes;
import java.util.*;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
final class BossWarnings {
 private static final Map<UUID,Long> until=new HashMap<>();
 static boolean active(Player p){Long end=until.get(p.getUniqueId());if(end==null)return false;if(System.currentTimeMillis()>=end){until.remove(p.getUniqueId());return false;}return true;}
 static void send(Player p,Component text){until.put(p.getUniqueId(),System.currentTimeMillis()+700);p.sendActionBar(text);}
 static void clear(){until.clear();}
}
