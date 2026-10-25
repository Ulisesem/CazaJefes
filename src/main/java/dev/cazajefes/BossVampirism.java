package dev.cazajefes;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.persistence.PersistentDataType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
final class BossVampirism implements Listener {
 private static final NamespacedKey READY=new NamespacedKey("cazajefes","vampirism_ready"),BOSS=new NamespacedKey("cazajefes",BossKeys.BOSS);
 static double healing(double damage,double health,double maximum,double current){if(!Double.isFinite(damage)||!Double.isFinite(health)||damage<=0||health<=0)return 0;return Math.max(0,Math.min(Math.min(damage,health)*.5,Math.min(maximum*.03,maximum-current)));}
 @EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)public void drain(EntityDamageByEntityEvent event){
  Entity source=event.getDamager();if(source instanceof Projectile projectile&&projectile.getShooter() instanceof Entity owner)source=owner;
  if(!(source instanceof LivingEntity boss)||!boss.getPersistentDataContainer().has(BOSS,PersistentDataType.BYTE)||!boss.isValid()||boss.isDead()||boss.isInvulnerable()||BossStages.phase(boss)!=3||!(event.getEntity() instanceof Player player)||!CombatGuard.eligible(player)||event.getFinalDamage()<=0)return;
  long now=System.currentTimeMillis();if(now<boss.getPersistentDataContainer().getOrDefault(READY,PersistentDataType.LONG,0L))return;
  double heal=healing(event.getFinalDamage(),player.getHealth(),boss.getMaxHealth(),boss.getHealth());if(heal<=0)return;
  boss.getPersistentDataContainer().set(READY,PersistentDataType.LONG,now+10000);boss.setHealth(Math.min(boss.getMaxHealth(),boss.getHealth()+heal));
  Location from=player.getLocation().add(0,1,0),to=boss.getLocation().add(0,boss.getHeight()*.55,0);for(int i=0;i<=10;i++){Location point=from.clone().add(to.toVector().subtract(from.toVector()).multiply(i/10.0));boss.getWorld().spawnParticle(Particle.DUST,point,1,0,0,0,0,new Particle.DustOptions(Color.RED,1.6f));}boss.getWorld().spawnParticle(Particle.HEART,to,5,.4,.4,.4,0);boss.getWorld().playSound(to,Sound.ENTITY_EVOKER_CAST_SPELL,.7f,.65f);if(!BossWarnings.active(player))player.sendActionBar(Component.text("☠ VAMPIRISMO · El boss ha absorbido parte de tu vida",NamedTextColor.RED));
 }
}
