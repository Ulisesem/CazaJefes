package dev.cazajefes;

import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/** Fixed red-health damage still passes through cancellable Paper damage events. */
final class SpecialDamage implements Listener {
    private record Hit(UUID owner,UUID target,double damage){}
    private static final ThreadLocal<Hit> ACTIVE=new ThreadLocal<>();
    private static final Map<UUID,Integer> READY=new HashMap<>();

    static boolean hit(LivingEntity boss,Player target,double amount){
        if(!boss.isValid()||boss.isDead()||target.isInvulnerable()||!CombatGuard.eligible(target)
            ||!ArenaBoundary.allows(boss,target.getLocation())||!Double.isFinite(amount)||amount<=0)return false;
        int tick=Bukkit.getCurrentTick();
        if(tick<READY.getOrDefault(target.getUniqueId(),0))return false;
        Hit previous=ACTIVE.get();
        ACTIVE.set(new Hit(boss.getUniqueId(),target.getUniqueId(),Math.min(20,amount)));
        int immunity=target.getNoDamageTicks();
        double health=target.getHealth();
        target.setNoDamageTicks(0);
        try{
            AbilityDamage.hit(target,boss,Math.min(20,amount),true);
            boolean accepted=target.getHealth()<health;
            if(accepted)READY.put(target.getUniqueId(),tick+12);
            else if(!target.isDead())target.setNoDamageTicks(immunity);
            return accepted;
        }catch(RuntimeException failure){if(!target.isDead())target.setNoDamageTicks(immunity);throw failure;
        }finally{
            if(previous==null)ACTIVE.remove();else ACTIVE.set(previous);
        }
    }

    static void area(LivingEntity boss,org.bukkit.Location center,double radius,double amount){
        for(var target:center.getWorld().getNearbyPlayers(center,radius)){
            var from=center.clone().add(0,.2,0);
            var delta=target.getEyeLocation().toVector().subtract(from.toVector());
            double distance=delta.length();
            if(distance>.2&&center.getWorld().rayTraceBlocks(from,delta.normalize(),distance-.1,
                org.bukkit.FluidCollisionMode.NEVER,true)!=null)continue;
            hit(boss,target,amount);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void fixed(EntityDamageByEntityEvent event){
        Hit hit=ACTIVE.get();
        if(hit!=null&&event.getDamager().getUniqueId().equals(hit.owner())
            &&event.getEntity().getUniqueId().equals(hit.target()))
            BossDeathVisuals.fixedBlastDamage(event,hit.damage());
    }

    @EventHandler public void quit(PlayerQuitEvent event){READY.remove(event.getPlayer().getUniqueId());}
    static void clear(){READY.clear();ACTIVE.remove();}
}
