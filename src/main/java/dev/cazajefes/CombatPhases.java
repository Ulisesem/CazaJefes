package dev.cazajefes;
import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
final class CombatPhases implements Listener {
 private final CazaJefesPlugin plugin;private final NamespacedKey boss,type;
 private final Map<UUID,Long> effects=new HashMap<>(),nextRoll=new HashMap<>(),disruption=new HashMap<>();
 CombatPhases(CazaJefesPlugin p){plugin=p;boss=new NamespacedKey(p,BossKeys.BOSS);type=new NamespacedKey(p,"boss_type");}
 private boolean isBoss(Entity e){return e.getPersistentDataContainer().has(boss,PersistentDataType.BYTE);}
 private Entity source(Entity e){return e instanceof Projectile p&&p.getShooter() instanceof Entity shooter?shooter:e;}
 private int phase(LivingEntity e){return PhaseRules.phase(e.getHealth()/Math.max(1,e.getMaxHealth()));}
 private boolean warden(Entity e){return e.getType()==EntityType.WARDEN;}
 @EventHandler(priority=EventPriority.HIGH,ignoreCancelled=true) public void scale(EntityDamageEvent event){
  if(event.getEntity() instanceof LivingEntity living&&isBoss(living))event.setDamage(event.getDamage()*PhaseRules.incoming(phase(living))*(warden(living)?0.85:1));
  if(event instanceof EntityDamageByEntityEvent hit&&source(hit.getDamager()) instanceof LivingEntity attacker&&isBoss(attacker)
      &&event.getEntity() instanceof Player target&&CombatGuard.eligible(target)&&!LightningRetaliation.isActive())event.setDamage(Math.min(80,event.getDamage()*PhaseRules.outgoing(phase(attacker))*(warden(attacker)?1.35:1)));
 }
 @EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true) public void effects(EntityDamageByEntityEvent event){
  if(!(source(event.getDamager()) instanceof LivingEntity attacker)||!isBoss(attacker)||!(event.getEntity() instanceof Player target)||!CombatGuard.eligible(target)||event.getFinalDamage()<=0)return;
  long now=System.currentTimeMillis();int phase=phase(attacker);UUID id=target.getUniqueId();
  if(phase>=2&&now>=effects.getOrDefault(id,0L)){
   target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS,phase==3?100:60,phase==3?1:0));
   target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS,phase==3?80:40,0));
   if(phase==3)target.addPotionEffect(new PotionEffect(warden(attacker)?PotionEffectType.DARKNESS:PotionEffectType.WITHER,warden(attacker)?80:60,0));
   effects.put(id,now+6000);
  }
  boolean free=target.getOpenInventory().getType()==org.bukkit.event.inventory.InventoryType.CRAFTING&&target.getItemOnCursor().getType().isAir();
  long prior=nextRoll.getOrDefault(id,0L);if(phase==3&&now>=prior)nextRoll.put(id,now+10000);
  if(PhaseRules.mayDisrupt(phase,true,free,now,prior,disruption.getOrDefault(id,0L),Math.random(),plugin.getConfig().getDouble("combat.final-inventory-chance",0.02))){
   ItemStack[] storage=target.getInventory().getStorageContents();List<Integer> slots=new ArrayList<>();for(int i=0;i<storage.length;i++)if(storage[i]!=null&&!storage[i].getType().isAir())slots.add(i);if(slots.size()<2)return;Collections.shuffle(slots);int count=Math.min(4,slots.size());
   ItemStack saved=storage[slots.get(0)];for(int i=0;i<count-1;i++)storage[slots.get(i)]=storage[slots.get(i+1)];storage[slots.get(count-1)]=saved;
   target.getInventory().setStorageContents(storage);disruption.put(id,now+60000);
   target.sendActionBar(Component.text("✦ La grieta ha desordenado tu inventario ✦",NamedTextColor.DARK_PURPLE));
  }
 }
 @EventHandler public void quit(PlayerQuitEvent e){UUID id=e.getPlayer().getUniqueId();effects.remove(id);nextRoll.remove(id);disruption.remove(id);}
}
