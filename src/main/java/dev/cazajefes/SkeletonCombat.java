package dev.cazajefes;
import java.util.*;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.*;
final class SkeletonCombat implements Listener {
 static final NamespacedKey ARROW=new NamespacedKey("cazajefes","skeleton_arrow");
 static boolean archer(EntityType type){return type==EntityType.SKELETON||type==EntityType.STRAY||type==EntityType.BOGGED;}
 static boolean shot(Entity entity){return entity.getPersistentDataContainer().has(ARROW,PersistentDataType.BYTE);}
 private static final class Unit {LivingEntity boss;BossCatalog.Definition definition;long nextShot,nextSpecial,release;Unit(LivingEntity b,BossCatalog.Definition d){boss=b;definition=d;nextSpecial=System.currentTimeMillis()+15000;}}
 private record Shot(Arrow arrow,UUID boss,UUID target,double damage,long end){}
 private final CazaJefesPlugin plugin;private final Map<UUID,Unit> units=new HashMap<>();private final Map<UUID,Shot> shots=new LinkedHashMap<>();private final BukkitTask timer;
 SkeletonCombat(CazaJefesPlugin plugin){this.plugin=plugin;timer=plugin.getServer().getScheduler().runTaskTimer(plugin,this::tick,5,5);}
 void track(LivingEntity boss,BossCatalog.Definition d){if(archer(boss.getType()))units.putIfAbsent(boss.getUniqueId(),new Unit(boss,d));}
 boolean charging(UUID id){var u=units.get(id);return u!=null&&u.release>0;}
 private Player target(LivingEntity boss){String focus=boss.getPersistentDataContainer().get(new NamespacedKey(plugin,"aggro_target"),PersistentDataType.STRING);if(focus!=null)try{Player p=Bukkit.getPlayer(UUID.fromString(focus));if(eligible(boss,p))return p;}catch(IllegalArgumentException ignored){}return boss.getWorld().getPlayers().stream().filter(p->eligible(boss,p)).min(Comparator.comparingDouble(p->p.getLocation().distanceSquared(boss.getLocation()))).orElse(null);}
 private boolean eligible(LivingEntity boss,Player p){return p!=null&&CombatGuard.eligible(p)&&p.getWorld().equals(boss.getWorld())&&p.getLocation().distanceSquared(boss.getLocation())<=2304&&boss.hasLineOfSight(p);}
 private void tick(){long now=System.currentTimeMillis();for(var it=shots.entrySet().iterator();it.hasNext();){Shot s=it.next().getValue();Entity owner=Bukkit.getEntity(s.boss());if(!s.arrow().isValid()||s.arrow().isInBlock()||now>=s.end()||owner==null||owner.isDead()){s.arrow().remove();it.remove();continue;}Player player=Bukkit.getPlayer(s.target());if(s.arrow().getTicksLived()<=20&&owner instanceof LivingEntity boss&&eligible(boss,player)){Vector desired=aim(s.arrow().getLocation(),player),current=s.arrow().getVelocity();if(current.lengthSquared()>.01)s.arrow().setVelocity(current.normalize().multiply(.7).add(desired.multiply(.3)).normalize().multiply(3.2));}}
  for(var it=units.entrySet().iterator();it.hasNext();){var u=it.next().getValue();LivingEntity boss=u.boss;if(!boss.isValid()||boss.isDead()){it.remove();continue;}if(boss.isInvulnerable())continue;Player player=target(boss);if(player==null){u.release=0;continue;}if(u.release>0){if(now<u.release){BossWarnings.send(player,Component.text("⚠ TRÍADA DEL ECLIPSE · Cargando arco",NamedTextColor.GOLD).decorate(TextDecoration.BOLD));continue;}u.release=0;fire(u,player,true,now);plugin.abilities.rest(boss);u.nextSpecial=now+15000;u.nextShot=now+2000;continue;}if(plugin.abilities.occupied(boss))continue;if(now>=u.nextSpecial){u.release=now+3000;boss.getWorld().playSound(boss.getLocation(),Sound.ITEM_CROSSBOW_LOADING_START,.8f,.8f);continue;}if(now>=u.nextShot){fire(u,player,false,now);u.nextShot=now+2000;}}
 }
 private Vector aim(Location from,Player p){var levitation=p.getPotionEffect(org.bukkit.potion.PotionEffectType.LEVITATION);return PillagerRules.aim(from.toVector(),p.getEyeLocation().toVector(),p.getVelocity(),3.2,levitation==null?0:levitation.getAmplifier()+1,!p.isOnGround());}
 private void fire(Unit u,Player player,boolean special,long now){if(shots.size()+(special?3:1)>128)return;var attribute=u.boss.getAttribute(Attribute.ATTACK_DAMAGE);double baseline=Math.max(u.definition.tier().damage,attribute==null?20:attribute.getValue());double damage=Math.min(120,baseline*.55*3.75*(special?1.2:1));Vector direction=aim(u.boss.getEyeLocation(),player);int count=special?3:1;for(int i=0;i<count;i++){Vector vector=direction.clone().rotateAroundY(Math.toRadians(special?(i-1)*4:0));Arrow arrow=u.boss.launchProjectile(Arrow.class,vector.multiply(3.2),a->{a.getPersistentDataContainer().set(ARROW,PersistentDataType.BYTE,(byte)1);a.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);a.setDamage(damage/3.2);a.setKnockbackStrength(10);a.setFireTicks(60);a.setCritical(false);a.setPersistent(false);});if(arrow.isValid())shots.put(arrow.getUniqueId(),new Shot(arrow,u.boss.getUniqueId(),player.getUniqueId(),damage,now+4000));}u.boss.getWorld().playSound(u.boss.getLocation(),Sound.ENTITY_SKELETON_SHOOT,1,.7f);if(special){CombatVisuals.special(u.boss,u.definition);BossWarnings.send(player,Component.text("✦ TRÍADA DEL ECLIPSE · ¡Busca cobertura!",NamedTextColor.GOLD).decorate(TextDecoration.BOLD));}}
 @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)public void nativeArrow(ProjectileLaunchEvent e){if(e.getEntity() instanceof Arrow arrow&&arrow.getShooter() instanceof LivingEntity boss&&units.containsKey(boss.getUniqueId())&&!arrow.getPersistentDataContainer().has(ARROW,PersistentDataType.BYTE))e.setCancelled(true);}
 @EventHandler(priority=EventPriority.NORMAL,ignoreCancelled=true)public void damage(EntityDamageByEntityEvent e){Shot shot=shots.get(e.getDamager().getUniqueId());if(shot==null)return;if(!(e.getEntity() instanceof Player p)||!CombatGuard.eligible(p)){e.setCancelled(true);return;}SafeDamage.setBase(e,shot.damage());}
 @EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)public void debuff(EntityDamageByEntityEvent e){Shot shot=shots.get(e.getDamager().getUniqueId());if(shot==null||e.getFinalDamage()<=0||!(e.getEntity() instanceof Player p))return;Entity owner=Bukkit.getEntity(shot.boss());if(owner!=null&&owner.getType()==EntityType.STRAY)BossEffects.applyFrom((LivingEntity)owner,p,BossEffects.effect(org.bukkit.potion.PotionEffectType.SLOWNESS,60,0));if(owner!=null&&owner.getType()==EntityType.BOGGED)BossEffects.applyFrom((LivingEntity)owner,p,BossEffects.effect(org.bukkit.potion.PotionEffectType.POISON,60,0));}
 @EventHandler(ignoreCancelled=true)public void pickup(org.bukkit.event.player.PlayerPickupArrowEvent e){if(e.getArrow().getPersistentDataContainer().has(ARROW,PersistentDataType.BYTE))e.setCancelled(true);}
 void stop(){timer.cancel();for(var s:shots.values())s.arrow().remove();shots.clear();units.clear();}
}
