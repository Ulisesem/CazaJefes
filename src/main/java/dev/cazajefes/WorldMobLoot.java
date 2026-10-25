package dev.cazajefes;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.persistence.PersistentDataType;
final class WorldMobLoot implements Listener {
 private final CazaJefesPlugin plugin;private final RelicItems items;private final NamespacedKey farm,boss,minion;
 WorldMobLoot(CazaJefesPlugin p,RelicItems items){plugin=p;this.items=items;farm=new NamespacedKey(p,"farm_spawn");boss=new NamespacedKey(p,BossKeys.BOSS);minion=new NamespacedKey(p,"boss_minion");}
 @EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true) public void spawn(CreatureSpawnEvent event){
  String reason=event.getSpawnReason().name();
  if(reason.equals("SPAWNER")||reason.contains("EGG")||reason.equals("CUSTOM")||reason.equals("COMMAND"))
   event.getEntity().getPersistentDataContainer().set(farm,PersistentDataType.BYTE,(byte)1);
 }
 @EventHandler(priority=EventPriority.HIGH) public void death(EntityDeathEvent event){
  LivingEntity entity=event.getEntity();var data=entity.getPersistentDataContainer();Player killer=entity.getKiller();
  if(!LootRules.eligibleMob(killer!=null&&plugin.guard.canAttackBoss(killer),entity instanceof Enemy,
      data.has(farm,PersistentDataType.BYTE),data.has(boss,PersistentDataType.BYTE),data.has(minion,PersistentDataType.BYTE)))return;
  if(Math.random()<LootRules.probability(plugin.getConfig().getDouble("loot.mob-fragment-chance",0.01)))event.getDrops().add(items.fragment(1));
 }
}
