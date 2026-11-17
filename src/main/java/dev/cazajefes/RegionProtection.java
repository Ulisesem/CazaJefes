package dev.cazajefes;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.persistence.PersistentDataType;

/** Applies region boundaries to secondary mechanics and summoned units too. */
final class RegionProtection implements Listener {
    private static final NamespacedKey MINION=new NamespacedKey("cazajefes","boss_minion");

    private static boolean guarded(Entity entity){
        return BossIntegrity.boss(entity)||entity.getPersistentDataContainer().has(MINION,PersistentDataType.BYTE);
    }

    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void teleport(EntityTeleportEvent event){
        if(!guarded(event.getEntity())||event.getTo()==null)return;
        Entity entity=event.getEntity();
        var at=event.getTo();
        if(!ArenaBoundary.allows(entity,at)){
            event.setCancelled(true);
            return;
        }
        if(!(entity instanceof LivingEntity living))return;
        var bounds=living.getBoundingBox();
        for(double x:new double[]{-bounds.getWidthX()/2,bounds.getWidthX()/2})
            for(double z:new double[]{-bounds.getWidthZ()/2,bounds.getWidthZ()/2})
                if(!ArenaBoundary.allows(entity,at.clone().add(x,bounds.getHeight()*.5,z))){
                    event.setCancelled(true);
                    return;
                }
    }

    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void changeBlock(EntityChangeBlockEvent event){
        if(guarded(event.getEntity())&&!ArenaBoundary.allowsTerrain(event.getEntity(),event.getBlock().getLocation()))
            event.setCancelled(true);
    }

    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void explode(EntityExplodeEvent event){
        if(guarded(event.getEntity()))
            event.blockList().removeIf(block->!ArenaBoundary.allowsTerrain(event.getEntity(),block.getLocation()));
    }
}
