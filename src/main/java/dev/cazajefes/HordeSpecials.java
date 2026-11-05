package dev.cazajefes;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

final class HordeSpecials implements Listener {
   private static final NamespacedKey SHOT = new NamespacedKey("cazajefes", "horde_archer_shot");
   private static final NamespacedKey ROLE = new NamespacedKey("cazajefes", "horde_role");
   private final CazaJefesPlugin plugin;
   private final Map<UUID,Unit> tracked = new HashMap<>();
   private final Map<Arrow,Long> shots = new HashMap<>();
   private final BukkitTask timer;

   private static final class Unit {
      final LivingEntity mob, boss;
      final CombatScripts.Role role;
      long ready, impact;
      Unit(LivingEntity mob, LivingEntity boss, CombatScripts.Role role) {
         this.mob = mob;
         this.boss = boss;
         this.role = role;
         ready = System.currentTimeMillis() + 1800;
      }
   }

   HordeSpecials(CazaJefesPlugin plugin) {
      this.plugin = plugin;
      timer = plugin.getServer().getScheduler().runTaskTimer(plugin,this::tick,10,10);
   }

   void apply(LivingEntity unit, LivingEntity boss, CombatScripts.Role role) {
      unit.getPersistentDataContainer().set(ROLE,PersistentDataType.STRING,role.name());
      switch (role) {
         case TANK -> {
            var health = unit.getAttribute(Attribute.MAX_HEALTH);
            var armor = unit.getAttribute(Attribute.ARMOR);
            var knockback = unit.getAttribute(Attribute.KNOCKBACK_RESISTANCE);
            if (health != null) {
               health.setBaseValue(Math.min(240, health.getBaseValue() * 1.55));
               unit.setHealth(health.getValue());
            }
            if (armor != null) armor.setBaseValue(12);
            if (knockback != null) knockback.setBaseValue(.65);
         }
         case HEALER -> plugin.mages.healer(unit,boss);
         case ARCHER, EXPLOSIVE -> {
            tracked.put(unit.getUniqueId(),new Unit(unit,boss,role));
         }
         case GUARD -> {}
      }
   }

   private void tick() {
      long now = System.currentTimeMillis();
      shots.entrySet().removeIf(entry -> {
         Arrow arrow = entry.getKey();
         if (!arrow.isValid()) return true;
         if (now < entry.getValue() && !arrow.isInBlock()) return false;
         arrow.remove();
         return true;
      });
      for (var it = tracked.values().iterator(); it.hasNext();) {
         Unit state = it.next();
         var mob = state.mob;
         if (!mob.isValid() || mob.isDead() || !state.boss.isValid() || state.boss.isDead()
             || state.boss.getWorld() != mob.getWorld()) {
            if (mob.isValid() && !mob.isDead()) mob.remove();
            it.remove();
            continue;
         }
         if (state.impact != 0 && now >= state.impact) {
            blast(mob);
            it.remove();
            continue;
         }
         if (now < state.ready || state.impact != 0) continue;
         var target = ThreatTable.pick(mob, state.role == CombatScripts.Role.ARCHER ? 18 : 3,
            mob::hasLineOfSight);
         if (target == null) continue;
         if (state.role == CombatScripts.Role.ARCHER) {
            if (shots.size() >= 96) continue;
            state.ready = now + 2400;
            var aim = target.getEyeLocation().toVector().subtract(mob.getEyeLocation().toVector());
            if (aim.lengthSquared() < .04) continue;
            Arrow arrow = mob.launchProjectile(Arrow.class, aim.normalize().multiply(1.65), entity -> {
               entity.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
               entity.setDamage(4);
               entity.setPersistent(false);
               entity.getPersistentDataContainer().set(SHOT, PersistentDataType.BYTE, (byte)1);
            });
            if (arrow.isValid()) shots.put(arrow,now+3000);
            mob.getWorld().playSound(mob.getLocation(),Sound.ENTITY_ARROW_SHOOT,.6f,1.25f);
         } else {
            state.impact = now + 1200;
            if (mob instanceof Mob unit) unit.getPathfinder().stopPathfinding();
            mob.setAI(false);
            var at = mob.getLocation();
            CinematicFX.ring(at,CinematicFX.audience(at,24),3,0,18,Color.RED,Color.YELLOW);
            mob.getWorld().playSound(at,Sound.ENTITY_CREEPER_PRIMED,.8f,1);
            for (Player player : at.getWorld().getNearbyPlayers(at,12)) if (CombatGuard.eligible(player)) {
               BossWarnings.send(player,net.kyori.adventure.text.Component.text("¡Aléjate del súbdito explosivo!"));
            }
         }
      }
   }

   private void blast(LivingEntity mob) {
      var at = mob.getLocation();
      for (Player player : at.getWorld().getNearbyPlayers(at,3)) {
         if (CombatGuard.eligible(player) && player.getLocation().distanceSquared(at) <= 9 && mob.hasLineOfSight(player)) {
            AbilityDamage.hit(player,mob,8);
         }
      }
      CosmeticFX.emit(Particle.EXPLOSION,at.clone().add(0,1,0),1);
      at.getWorld().playSound(at,Sound.ENTITY_GENERIC_EXPLODE,.8f,1.3f);
      mob.remove();
   }

   @EventHandler(ignoreCancelled = true)
   public void naturalShot(ProjectileLaunchEvent event) {
      if (event.getEntity().getShooter() instanceof LivingEntity mob
          && "ARCHER".equals(mob.getPersistentDataContainer().get(ROLE,PersistentDataType.STRING))
          && !event.getEntity().getPersistentDataContainer().has(SHOT,PersistentDataType.BYTE)) {
         event.setCancelled(true);
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void naturalExplosion(org.bukkit.event.entity.ExplosionPrimeEvent event) {
      Entity source = event.getEntity();
      if (source instanceof Projectile projectile && projectile.getShooter() instanceof Entity shooter) source = shooter;
      if (source.getPersistentDataContainer().has(ROLE,PersistentDataType.STRING)) event.setCancelled(true);
   }

   void stop() {
      timer.cancel();
      for (Unit unit : tracked.values()) if (unit.mob.isValid()) unit.mob.remove();
      tracked.clear();
      shots.keySet().forEach(Entity::remove);
      shots.clear();
   }
}
