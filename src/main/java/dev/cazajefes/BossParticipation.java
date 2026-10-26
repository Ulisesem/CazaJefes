package dev.cazajefes;
import java.util.*;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.persistence.PersistentDataType;
final class BossParticipation implements Listener {
 static final NamespacedKey KEY=new NamespacedKey("cazajefes","battle_participants");
 private static final NamespacedKey BOSS=new NamespacedKey("cazajefes",BossKeys.BOSS);
 private final CazaJefesPlugin plugin;BossParticipation(CazaJefesPlugin plugin){this.plugin=plugin;}
 static Set<UUID> parse(String data){Set<UUID> out=new LinkedHashSet<>();if(data!=null)for(String id:data.split(","))try{out.add(UUID.fromString(id));}catch(IllegalArgumentException ignored){}return out;}
 static Set<UUID> participants(LivingEntity boss){return parse(boss.getPersistentDataContainer().get(KEY,PersistentDataType.STRING));}
 @EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)public void hit(EntityDamageByEntityEvent event){record(event);}
 void record(EntityDamageByEntityEvent event){if(event.getFinalDamage()<=0||!(event.getEntity() instanceof LivingEntity boss)||!boss.getPersistentDataContainer().has(BOSS,PersistentDataType.BYTE))return;Entity source=event.getDamager();if(source instanceof Projectile projectile&&projectile.getShooter() instanceof Entity owner)source=owner;if(!(source instanceof Player player)||!plugin.guard.canAttackBoss(player))return;DamageLedger.record(boss,player,event.getFinalDamage());Set<UUID> ids=participants(boss);if(ids.add(player.getUniqueId()))boss.getPersistentDataContainer().set(KEY,PersistentDataType.STRING,String.join(",",ids.stream().map(UUID::toString).toList()));}
}
