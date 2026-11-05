package dev.cazajefes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

final class BossCrystals implements Listener {
   private static final NamespacedKey USED = new NamespacedKey("cazajefes", "prism_barrier_used");
   private static final NamespacedKey CRYSTAL = new NamespacedKey("cazajefes", "prism_barrier_crystal");
   private final CazaJefesPlugin plugin;
   private final Map<UUID, Shield> shields = new HashMap<>();
   private final Map<UUID, UUID> owners = new HashMap<>();
   private final BukkitTask timer;

   private record Shield(LivingEntity boss, List<EnderCrystal> crystals, long expiry) {}

   BossCrystals(CazaJefesPlugin plugin) {
      this.plugin = plugin;
      timer = plugin.getServer().getScheduler().runTaskTimer(plugin, this::expire, 10, 10);
   }

   static double multiplier(int crystals) {
      return 1 - Math.clamp(crystals, 0, 4) * .0875;
   }

   double incoming(LivingEntity boss) {
      Shield shield = shields.get(boss.getUniqueId());
      return shield == null ? 1 : multiplier(shield.crystals().size());
   }

   void check(LivingEntity boss, BossCatalog.Definition definition, Player target) {
      var data = boss.getPersistentDataContainer();
      if (boss.isInvulnerable() || boss.getHealth() > boss.getMaxHealth() * .5 || owners.size() > 44
          || data.has(USED, PersistentDataType.BYTE)
          || target.getLocation().distanceSquared(boss.getLocation()) > 144) return;
      var points = new ArrayList<Location>();
      Location center = boss.getLocation();
      for (int i = 0; i < 4; i++) {
         double angle = i * Math.PI / 2 + Math.PI / 4;
         Location point = center.clone().add(Math.cos(angle) * 4, .3, Math.sin(angle) * 4);
         if (!point.isChunkLoaded() || point.getY() < point.getWorld().getMinHeight()
             || point.getBlockY() + 1 >= point.getWorld().getMaxHeight()
             || !point.getWorld().getWorldBorder().isInside(point)
             || !point.getBlock().isPassable() || !point.clone().add(0, 1, 0).getBlock().isPassable()) return;
         points.add(point);
      }
      var crystals = new ArrayList<EnderCrystal>();
      for (Location point : points) {
         EnderCrystal crystal = point.getWorld().spawn(point, EnderCrystal.class, entity -> {
            entity.setShowingBottom(false);
            entity.setPersistent(false);
            entity.setBeamTarget(center.clone().add(0, 1, 0));
            entity.getPersistentDataContainer().set(CRYSTAL, PersistentDataType.BYTE, (byte) 1);
         });
         if (!crystal.isValid()) {
            crystals.forEach(Entity::remove);
            return;
         }
         crystals.add(crystal);
      }
      shields.put(boss.getUniqueId(), new Shield(boss, crystals, System.currentTimeMillis() + 15000));
      crystals.forEach(crystal -> owners.put(crystal.getUniqueId(), boss.getUniqueId()));
      data.set(USED, PersistentDataType.BYTE, (byte) 1);
      for (Player player : boss.getWorld().getNearbyPlayers(center, 24)) {
         if (!CombatGuard.eligible(player)) continue;
         BossWarnings.send(player, Component.text(definition.name() + " · ROMPE LOS CRISTALES", NamedTextColor.LIGHT_PURPLE));
         player.playSound(center, Sound.BLOCK_AMETHYST_BLOCK_RESONATE, .9f, .8f);
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST)
   public void damage(EntityDamageEvent event) {
      if (!(event.getEntity() instanceof EnderCrystal crystal)
          || !crystal.getPersistentDataContainer().has(CRYSTAL, PersistentDataType.BYTE)) return;
      boolean allowed = !event.isCancelled();
      event.setCancelled(true);
      if (!allowed || !(event instanceof EntityDamageByEntityEvent hit) || event.getFinalDamage() <= 0) return;
      Entity source = hit.getDamager();
      if (source instanceof Projectile projectile && projectile.getShooter() instanceof Entity shooter) source = shooter;
      if (!(source instanceof Player player) || !plugin.guard.canAttackBoss(player)) return;
      Location impact = crystal.getLocation();
      removeCrystal(crystal);
      CosmeticFX.emit(Particle.END_ROD, impact, 12, .35, .6, .35, .04);
      player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_BREAK, .85f, 1.3f);
   }

   @EventHandler(priority = EventPriority.HIGHEST)
   public void ignite(BlockIgniteEvent event) {
      Entity source = event.getIgnitingEntity();
      if (source != null && source.getPersistentDataContainer().has(CRYSTAL, PersistentDataType.BYTE)) {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void prime(ExplosionPrimeEvent event) {
      if (event.getEntity().getPersistentDataContainer().has(CRYSTAL, PersistentDataType.BYTE)) event.setCancelled(true);
   }

   @EventHandler
   public void death(EntityDeathEvent event) {
      clear(event.getEntity().getUniqueId());
   }

   @EventHandler
   public void unload(ChunkUnloadEvent event) {
      for (Entity entity : event.getChunk().getEntities()) {
         if (entity instanceof EnderCrystal crystal && owners.containsKey(entity.getUniqueId())) removeCrystal(crystal);
         else if (shields.containsKey(entity.getUniqueId())) clear(entity.getUniqueId());
      }
   }

   private void removeCrystal(EnderCrystal crystal) {
      UUID owner = owners.remove(crystal.getUniqueId());
      Shield shield = shields.get(owner);
      if (shield != null) {
         shield.crystals().remove(crystal);
         if (shield.crystals().isEmpty()) shields.remove(owner);
      }
      crystal.remove();
   }

   private void expire() {
      long now = System.currentTimeMillis();
      for (Shield shield : new ArrayList<>(shields.values())) {
         if (now >= shield.expiry() || !shield.boss().isValid() || shield.boss().isDead()) {
            clear(shield.boss().getUniqueId());
         } else {
            for (EnderCrystal crystal : new ArrayList<>(shield.crystals())) if (!crystal.isValid()) removeCrystal(crystal);
         }
      }
   }

   private void clear(UUID id) {
      Shield shield = shields.remove(id);
      if (shield == null) return;
      for (EnderCrystal crystal : shield.crystals()) {
         owners.remove(crystal.getUniqueId());
         crystal.remove();
      }
   }

   void stop() {
      timer.cancel();
      for (UUID id : new ArrayList<>(shields.keySet())) clear(id);
   }
}
