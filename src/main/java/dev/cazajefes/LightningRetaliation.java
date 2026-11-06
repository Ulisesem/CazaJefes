package dev.cazajefes;
import java.util.*;
import org.bukkit.*;
import org.bukkit.damage.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.persistence.PersistentDataType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
final class LightningRetaliation implements Listener {
 private static final ThreadLocal<Boolean> ACTIVE=new ThreadLocal<>();
 static boolean isActive(){return Boolean.TRUE.equals(ACTIVE.get());}
 private record Pair(UUID boss,UUID player){}
 private final CazaJefesPlugin plugin;private final NamespacedKey marker;
 private final Map<Pair,Double> charges=new HashMap<>();private final Map<UUID,Long> nextStrike=new HashMap<>();
 LightningRetaliation(CazaJefesPlugin p){plugin=p;marker=new NamespacedKey(p,BossKeys.BOSS);}
 private boolean boss(Entity e){return e.getPersistentDataContainer().has(marker,PersistentDataType.BYTE);}
 @EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)public void hit(EntityDamageByEntityEvent event){
  if(!plugin.getConfig().getBoolean("combat.phase3-lightning.enabled",true)||isActive()||!(event.getEntity() instanceof LivingEntity boss)||!boss(boss)||plugin.customEncounter.owns(boss))return;
  Entity source=event.getDamager() instanceof Projectile p&&p.getShooter() instanceof Entity shooter?shooter:event.getDamager();
  if(!(source instanceof Player player))return;
  Pair key=new Pair(boss.getUniqueId(),player.getUniqueId());int phase=PhaseRules.phase(boss.getHealth()/Math.max(1,boss.getMaxHealth()));
  if(phase!=3||!CombatGuard.eligible(player)){charges.remove(key);return;}
  if(!(event.getFinalDamage()>0))return;
  double charge=BattleRules.charge(charges.getOrDefault(key,0D),event.getFinalDamage());charges.put(key,charge);
  long now=System.currentTimeMillis();double threshold=plugin.getConfig().getDouble("combat.phase3-lightning.threshold-damage",40);
  if(!BattleRules.retaliates(phase,true,charge,threshold,now,nextStrike.getOrDefault(boss.getUniqueId(),0L))||!plugin.abilities.reserveWarning(boss,plugin.abilities.warningTicks(boss)))return;
  charges.remove(key);nextStrike.put(boss.getUniqueId(),now+Math.max(1,Math.min(60,plugin.getConfig().getLong("combat.phase3-lightning.cooldown-seconds",4)))*1000);
  double damage=BattleRules.lightningDamage(charge,plugin.getConfig().getDouble("combat.phase3-lightning.max-damage",30));
  BossWarnings.send(player,Component.text("⚡ "+plugin.languages.text(player,"attack.lightning","TORMENTA DE REPRESALIA · ¡Apártate!"),NamedTextColor.RED));
  plugin.getServer().getScheduler().runTaskLater(plugin,()->{
   if(!plugin.getConfig().getBoolean("combat.phase3-lightning.enabled",true)||!boss.isValid()||boss.isDead()||boss.isInvulnerable()||!CombatGuard.eligible(player)||!player.getWorld().equals(boss.getWorld())
       ||player.getLocation().distanceSquared(boss.getLocation())>576||!boss.hasLineOfSight(player)||PhaseRules.phase(boss.getHealth()/Math.max(1,boss.getMaxHealth()))!=3)return;
   player.getWorld().strikeLightningEffect(player.getLocation());
   Boolean previous=ACTIVE.get();ACTIVE.set(true);int oldImmunity=player.getNoDamageTicks();
   try{player.setNoDamageTicks(0);player.damage(damage,DamageSource.builder(DamageType.LIGHTNING_BOLT).withCausingEntity(boss).withDirectEntity(boss).withDamageLocation(player.getLocation()).build());}
   finally{if(!player.isDead()&&player.getNoDamageTicks()==0)player.setNoDamageTicks(oldImmunity);if(previous==null)ACTIVE.remove();else ACTIVE.set(previous);}
  },plugin.abilities.warningTicks(boss));
 }
 @EventHandler(priority=EventPriority.MONITOR)public void death(EntityDeathEvent event){UUID id=event.getEntity().getUniqueId();charges.keySet().removeIf(k->k.boss.equals(id));nextStrike.remove(id);}
 @EventHandler public void quit(PlayerQuitEvent event){UUID id=event.getPlayer().getUniqueId();charges.keySet().removeIf(k->k.player.equals(id));}
 @EventHandler public void unload(ChunkUnloadEvent event){for(Entity e:event.getChunk().getEntities())if(boss(e)){UUID id=e.getUniqueId();charges.keySet().removeIf(k->k.boss.equals(id));nextStrike.remove(id);}}
}
