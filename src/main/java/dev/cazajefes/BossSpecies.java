package dev.cazajefes;
import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.persistence.PersistentDataType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.*;
final class BossSpecies implements Listener {
 private record Shell(Armadillo entity,ArmadilloCycle cycle,boolean ai){}
 private record ShulkerShell(Shulker entity,ArmadilloCycle cycle,boolean ai){}
 private final Map<UUID,ShulkerShell> boxes=new HashMap<>();
 private final Map<UUID,Shell> shells=new HashMap<>();
 boolean defending(LivingEntity e){var s=shells.get(e.getUniqueId());var box=boxes.get(e.getUniqueId());return s!=null&&s.cycle().active()||box!=null&&box.cycle().active();}
 boolean tick(LivingEntity boss,long now,boolean available){
  if(boss instanceof Bee bee){bee.setHasStung(false);bee.setAnger(24000);}
  if(boss instanceof Shulker shulker){var box=boxes.computeIfAbsent(boss.getUniqueId(),id->new ShulkerShell(shulker,new ArmadilloCycle(25000,3000),shulker.hasAI()));boolean nearby=boss.getWorld().getPlayers().stream().anyMatch(p->CombatGuard.eligible(p)&&p.getLocation().distanceSquared(boss.getLocation())<=576);int event=box.cycle().tick(now,available&&nearby);if(event==1){shulker.setAI(false);shulker.setPeek(0);announce(boss,"⚠ REFUGIO DEL UMBRAL · Cerrando caja · Cura en 3 s");}if(event==2){shulker.setAI(box.ai());shulker.setPeek(1);if(heal(boss,boss.getMaxHealth()*.05))announce(boss,"✦ REFUGIO DEL UMBRAL · Ha regenerado vida · hasta 5 %");}if(box.cycle().active()){shulker.setPeek(0);return true;}return false;}
  if(!(boss instanceof Armadillo armadillo))return false;
  var shell=shells.computeIfAbsent(boss.getUniqueId(),id->new Shell(armadillo,new ArmadilloCycle(),armadillo.hasAI()));armadillo.setAI(false);
  boolean nearby=boss.getWorld().getPlayers().stream().anyMatch(p->CombatGuard.eligible(p)&&p.getLocation().distanceSquared(boss.getLocation())<=576);
  int event=shell.cycle().tick(now,available&&nearby);
  if(event==1){armadillo.rollUp();armadillo.setVelocity(new org.bukkit.util.Vector());for(Player p:boss.getWorld().getPlayers())if(p.getLocation().distanceSquared(boss.getLocation())<=1024)BossWarnings.send(p,Component.text("✦ BASTIÓN VIVIENTE · Defensa · 5 s",NamedTextColor.GOLD).decorate(TextDecoration.BOLD));}
  if(event==2&&!boss.isDead()){double amount=ArmadilloCycle.healing(boss.getMaxHealth());var heal=new EntityRegainHealthEvent(boss,amount,EntityRegainHealthEvent.RegainReason.CUSTOM);Bukkit.getPluginManager().callEvent(heal);if(!heal.isCancelled()&&Double.isFinite(heal.getAmount())&&heal.getAmount()>0)boss.setHealth(Math.min(boss.getMaxHealth(),boss.getHealth()+Math.min(amount,heal.getAmount())));}
  if(shell.cycle().active()){armadillo.setVelocity(new org.bukkit.util.Vector());return true;}armadillo.rollOut();return false;
 }
 @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)public void attack(EntityDamageByEntityEvent e){Entity source=e.getDamager();if(source instanceof Projectile p&&p.getShooter() instanceof Entity owner)source=owner;if(source instanceof LivingEntity boss&&defending(boss))e.setCancelled(true);}
 @EventHandler public void death(EntityDeathEvent e){remove(e.getEntity().getUniqueId());}
 private void announce(LivingEntity boss,String message){for(Player p:boss.getWorld().getPlayers())if(p.getLocation().distanceSquared(boss.getLocation())<=1024)BossWarnings.send(p,Component.text(message,NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD));}
 private boolean heal(LivingEntity boss,double amount){var event=new EntityRegainHealthEvent(boss,amount,EntityRegainHealthEvent.RegainReason.CUSTOM);Bukkit.getPluginManager().callEvent(event);if(event.isCancelled()||!Double.isFinite(event.getAmount())||event.getAmount()<=0||boss.isDead())return false;double before=boss.getHealth();boss.setHealth(Math.min(boss.getMaxHealth(),before+Math.min(amount,event.getAmount())));return boss.getHealth()>before;}
 void remove(UUID id){var box=boxes.remove(id);if(box!=null&&box.entity().isValid()&&!box.entity().isDead()){box.entity().setAI(box.ai());box.entity().setPeek(1);}var shell=shells.remove(id);if(shell!=null&&shell.entity().isValid()&&!shell.entity().isDead()){shell.entity().rollOut();shell.entity().setAI(shell.ai());}}
 void stop(){Set<UUID> ids=new HashSet<>(shells.keySet());ids.addAll(boxes.keySet());for(UUID id:ids)remove(id);}
}
