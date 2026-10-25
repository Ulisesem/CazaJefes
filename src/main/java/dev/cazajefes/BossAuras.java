package dev.cazajefes;
import java.util.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
final class BossAuras {
 private record Active(LivingEntity entity,BossCatalog.Definition definition){}
 private final Map<UUID,Active> active=new HashMap<>();private final BukkitTask task;private double angle;
 BossAuras(JavaPlugin plugin){task=plugin.getServer().getScheduler().runTaskTimer(plugin,this::tick,1,5);}
 void track(LivingEntity boss,BossCatalog.Definition definition){active.put(boss.getUniqueId(),new Active(boss,definition));}
 private void tick(){angle+=.16;var iterator=active.values().iterator();while(iterator.hasNext()){var entry=iterator.next();LivingEntity boss=entry.entity();if(!boss.isValid()||boss.isDead()){iterator.remove();continue;}CombatVisuals.aura(boss,entry.definition(),angle);}}
 void stop(){task.cancel();active.clear();}
}
