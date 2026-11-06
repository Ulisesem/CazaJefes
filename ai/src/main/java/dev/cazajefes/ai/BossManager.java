package dev.cazajefes.ai;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.Plugin;

/** Single Paper scheduler task for all boss brains and simulated attacks. */
public final class BossManager extends BukkitRunnable {
    private final Map<UUID,CustomBoss> active=new LinkedHashMap<>();
    private final Plugin plugin;
    private long tick;

    public BossManager(Plugin plugin){
        this.plugin=plugin;
        runTaskTimer(plugin,1,1);
    }

    public void add(CustomBoss boss){
        var previous=active.put(boss.body().getUniqueId(),boss);
        if(previous!=null)previous.close();
    }

    public boolean owns(UUID uuid){return active.containsKey(uuid);}
    public CustomBoss get(UUID uuid){return active.get(uuid);}

    public void remove(UUID uuid){
        var boss=active.remove(uuid);
        if(boss!=null)boss.close();
    }

    @Override public void run(){
        tick++;
        // Damage can invoke Bukkit death or unload listeners synchronously; iterate a snapshot.
        for(var boss:active.values().toArray(CustomBoss[]::new)){
            UUID id=boss.body().getUniqueId();
            if(active.get(id)!=boss)continue;
            if(!boss.body().isValid()||boss.body().isDead()){
                remove(id);
                continue;
            }
            try{
                boss.tick(tick);
            }catch(RuntimeException failure){
                remove(id);
                plugin.getLogger().log(java.util.logging.Level.SEVERE,"IA detenida para "+id,failure);
            }
        }
    }

    public void stop(){
        cancel();
        active.values().forEach(CustomBoss::close);
        active.clear();
    }
}
