package dev.cazajefes;
import java.util.*;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.*;
final class ShulkerCombat implements Listener {
 static final int VOLLEY_SIZE=8;static final long BLINK_COOLDOWN=1800;
 private final CazaJefesPlugin plugin;private final NamespacedKey bossKey,bulletKey;private final Map<UUID,Long> nextVolley=new HashMap<>(),nextBlink=new HashMap<>();private final Set<UUID> shots=new HashSet<>();private boolean firing;
 ShulkerCombat(CazaJefesPlugin p){plugin=p;bossKey=new NamespacedKey(p,BossKeys.BOSS);bulletKey=new NamespacedKey(p,"venom_bullet");}
 private boolean boss(Entity e){return e instanceof Shulker&&e.getPersistentDataContainer().has(bossKey,PersistentDataType.BYTE);}
 void tick(LivingEntity entity,Player target,int phase){if(!(entity instanceof Shulker shulker)||!shulker.hasLineOfSight(target))return;shulker.setPeek(1);long now=System.currentTimeMillis();if(now<nextVolley.getOrDefault(entity.getUniqueId(),0L))return;nextVolley.put(entity.getUniqueId(),now+(phase==3?4500:6000));
  // Only one live volley per boss. Native shots are suppressed below.
  for(Entity e:entity.getWorld().getNearbyEntities(entity.getLocation(),40,24,40))if(e instanceof ShulkerBullet b&&b.getShooter()==entity&&b.getPersistentDataContainer().has(bulletKey,PersistentDataType.BYTE)){shots.remove(b.getUniqueId());b.remove();}
  firing=true;try{for(int i=0;i<VOLLEY_SIZE;i++){double a=2*Math.PI*i/VOLLEY_SIZE;Location point=shulker.getEyeLocation().add(Math.cos(a)*.7,.2,Math.sin(a)*.7);ShulkerBullet bullet=entity.getWorld().spawn(point,ShulkerBullet.class,b->{b.setShooter(shulker);b.setTarget(target);b.getPersistentDataContainer().set(bulletKey,PersistentDataType.BYTE,(byte)1);});shots.add(bullet.getUniqueId());plugin.getServer().getScheduler().runTaskLater(plugin,()->{shots.remove(bullet.getUniqueId());if(bullet.isValid())bullet.remove();},160L);}}finally{firing=false;}
  BossCatalog.Definition d=BossCatalog.get(entity.getPersistentDataContainer().get(new NamespacedKey(plugin,"boss_type"),PersistentDataType.STRING));if(d!=null)CombatVisuals.special(entity,d);
 }
 @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)public void nativeShot(ProjectileLaunchEvent event){if(!firing&&event.getEntity() instanceof ShulkerBullet b&&b.getShooter() instanceof Entity shooter&&boss(shooter))event.setCancelled(true);}
 @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)public void friendlyHit(EntityDamageByEntityEvent event){if(event.getDamager() instanceof ShulkerBullet b&&b.getShooter() instanceof Entity shooter&&boss(shooter)&&event.getEntity().getPersistentDataContainer().has(bossKey,PersistentDataType.BYTE)){event.setCancelled(true);b.remove();shots.remove(b.getUniqueId());}}
 @EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)public void poison(EntityDamageByEntityEvent event){if(event.getFinalDamage()>0&&event.getDamager() instanceof ShulkerBullet b&&b.getPersistentDataContainer().has(bulletKey,PersistentDataType.BYTE)&&event.getEntity() instanceof Player p&&CombatGuard.eligible(p)){p.addPotionEffect(new PotionEffect(PotionEffectType.POISON,100,1));shots.remove(b.getUniqueId());}}
 @EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)public void struck(EntityDamageByEntityEvent event){if(!boss(event.getEntity())||event.getFinalDamage()<=0)return;Shulker shulker=(Shulker)event.getEntity();Entity source=event.getDamager();if(source instanceof Projectile projectile&&projectile.getShooter() instanceof Entity owner)source=owner;if(!(source instanceof Player p)||!CombatGuard.eligible(p)||event.getFinalDamage()>=shulker.getHealth())return;long now=System.currentTimeMillis();if(now<nextBlink.getOrDefault(shulker.getUniqueId(),0L))return;nextBlink.put(shulker.getUniqueId(),now+BLINK_COOLDOWN);plugin.getServer().getScheduler().runTask(plugin,()->{if(shulker.isValid()&&!shulker.isDead()&&!shulker.isInvulnerable())blink(shulker);});}
 private void blink(Shulker shulker){Location from=shulker.getLocation();int radius=(int)Math.ceil(shulker.getWidth()/2),height=(int)Math.ceil(shulker.getHeight());Random random=new Random();for(int attempt=0;attempt<24;attempt++){Location to=from.clone().add(random.nextInt(13)-6,random.nextInt(7)-3,random.nextInt(13)-6).getBlock().getLocation().add(.5,0,.5);if(from.distanceSquared(to)<9||!to.getWorld().isChunkLoaded(to.getBlockX()>>4,to.getBlockZ()>>4)||!to.getBlock().getRelative(BlockFace.DOWN).getType().isOccluding()||!to.getWorld().getWorldBorder().isInside(to))continue;boolean clear=true;for(int x=-radius;x<=radius&&clear;x++)for(int z=-radius;z<=radius&&clear;z++)for(int y=0;y<=height;y++)if(!to.clone().add(x,y,z).getBlock().isPassable()||to.clone().add(x,y,z).getBlock().isLiquid()){clear=false;break;}if(!clear)continue;if(shulker.teleport(to)){shulker.setAttachedFace(BlockFace.DOWN);from.getWorld().spawnParticle(Particle.REVERSE_PORTAL,from.clone().add(0,1,0),24,.5,.6,.5,.04);to.getWorld().spawnParticle(Particle.PORTAL,to.clone().add(0,1,0),24,.5,.6,.5,.04);}return;}}
 @EventHandler public void death(EntityDeathEvent event){nextVolley.remove(event.getEntity().getUniqueId());nextBlink.remove(event.getEntity().getUniqueId());}
 void stop(){for(UUID id:shots){Entity e=Bukkit.getEntity(id);if(e!=null)e.remove();}shots.clear();nextVolley.clear();nextBlink.clear();}
}
