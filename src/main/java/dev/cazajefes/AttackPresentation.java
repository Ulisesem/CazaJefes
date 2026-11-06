package dev.cazajefes;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Temporary attack cues. Damage stays in the encounter, even when the visual budget is full. */
final class AttackPresentation {
   private static final int GLOBAL_DISPLAYS = 144;
   private static int activeDisplays;
   private final Plugin plugin;
   private final java.util.Map<Player,BossBar> bars = new java.util.HashMap<>();
   private final Set<Player> viewers = new HashSet<>();
   private ItemDisplay prop;
   private ItemDisplay projectile;
   private final List<org.bukkit.entity.Entity> displays = new ArrayList<>();

   AttackPresentation(Plugin plugin) {
      this.plugin = plugin;
   }

   void update(List<Player> audience, java.util.function.Function<Player,Component> label, float progress, BossBar.Color color) {
      var nearby = new HashSet<>(audience);
      viewers.removeIf(player -> {
         if (nearby.contains(player)) return false;
         player.hideBossBar(bars.remove(player));
         if (prop != null) player.hideEntity(plugin, prop);
         return true;
      });
      for (var player : audience) {
         var title=label.apply(player);
         if (viewers.add(player)) {
            var bar=BossBar.bossBar(title,progress,color,BossBar.Overlay.PROGRESS);
            bars.put(player,bar);
            player.showBossBar(bar);
            if (prop != null) player.showEntity(plugin, prop);
         } else {
            var bar=bars.get(player);
            bar.name(title);
            bar.progress(progress);
            bar.color(color);
         }
      }
   }

   void meteor(Location impact, BossCatalog.Definition definition, int height) {
      var start=impact.clone().add(0,height,0);
      if (activeDisplays>=GLOBAL_DISPLAYS || !start.isChunkLoaded() || start.getY()>=start.getWorld().getMaxHeight()) return;
      var stand=start.getWorld().spawn(start,org.bukkit.entity.ArmorStand.class,entity -> {
         entity.setPersistent(false);
         entity.setGravity(false);
         entity.setInvisible(true);
         entity.setMarker(true);
         entity.setInvulnerable(true);
         entity.getEquipment().setHelmet(BossPortraits.meteorHead(definition),true);
      });
      if (!stand.isValid()) return;
      displays.add(stand);
      activeDisplays++;
      new org.bukkit.scheduler.BukkitRunnable() {
         int tick;
         @Override public void run() {
            if (!stand.isValid()) { cancel(); return; }
            double fraction=++tick/9.0;
            stand.teleport(start.clone().subtract(0,height*fraction,0));
            if (tick==9) cancel();
         }
      }.runTaskTimer(plugin,1,1);
   }

   void thrownAxe(Location start, Location impact) {
      if (activeDisplays >= GLOBAL_DISPLAYS || !impact.isChunkLoaded() || !start.isChunkLoaded()) return;
      clearProp();
      clearProjectile();
      var item = start.getWorld().spawn(start,ItemDisplay.class,display -> {
         display.setPersistent(false);
         display.setItemStack(new ItemStack(Material.NETHERITE_AXE));
         display.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIXED);
         display.setBrightness(new Display.Brightness(15,15));
         display.setTeleportDuration(9);
         display.setInterpolationDuration(9);
         display.setTransformation(new Transformation(new Vector3f(),new AxisAngle4f(.7f,0,0,1),
            new Vector3f(2.1f),new AxisAngle4f()));
      });
      if (!item.isValid()) return;
      projectile = item;
      activeDisplays++;
      plugin.getServer().getScheduler().runTask(plugin,() -> {
         if (!item.isValid()) return;
         item.setInterpolationDelay(0);
         item.setTransformation(new Transformation(new Vector3f(),new AxisAngle4f(-.55f,0,0,1),
            new Vector3f(2.1f),new AxisAngle4f()));
         item.teleport(impact.clone().add(0,.32,0));
      });
   }

   private void add(Location at, Material material, boolean falling) {
      add(at,material,falling,6,.65f);
   }

   private void add(Location at, Material material, boolean falling, double height, float size) {
      if (viewers.isEmpty() || activeDisplays >= GLOBAL_DISPLAYS || !at.isChunkLoaded()) return;
      if (at.getBlockY() < at.getWorld().getMinHeight() || at.getBlockY() >= at.getWorld().getMaxHeight()) return;
      var display = at.getWorld().spawn(at, BlockDisplay.class, entity -> {
         entity.setVisibleByDefault(false);
         entity.setPersistent(false);
         entity.setBlock(material.createBlockData());
         entity.setBrightness(new Display.Brightness(15, 15));
         entity.setTransformation(falling ? new Transformation(new Vector3f(-size/2,0,-size/2),new AxisAngle4f(.58f,.70710677f,.70710677f,0),new Vector3f(size),new AxisAngle4f()) : transform(size > 1 ? 2.0f : .18f,.065f));
         entity.setTeleportDuration(10);
         entity.setInterpolationDuration(10);
      });
      if (!display.isValid()) return;
      displays.add(display);
      activeDisplays++;
      for (var viewer : viewers) viewer.showEntity(plugin, display);
      plugin.getServer().getScheduler().runTask(plugin, () -> {
         if (!display.isValid()) return;
         if (falling) display.teleport(at.clone().subtract(0, height, 0));
         else {
            display.setInterpolationDelay(0);
            display.setTransformation(transform(.82f, .065f));
         }
      });
   }

   private static Transformation transform(float width, float height) {
      return new Transformation(new Vector3f(-width / 2, 0, -width / 2), new AxisAngle4f(),
         new Vector3f(width, height, width), new AxisAngle4f());
   }

   void prop(LivingEntity boss, BossStyle style, float progress, boolean strike) {
      if (style == BossStyle.EXECUTIONER && ThrownAxeHand.usesPhysicalHand(boss)) {
         clearProp();
         return;
      }
      if (viewers.isEmpty()) return;
      if (prop != null && !prop.isValid()) clearProp();
      var at = boss.getLocation();
      var forward = at.getDirection().setY(0);
      if (forward.lengthSquared() < .01) forward.setZ(1);
      forward.normalize();
      double reach = Math.clamp(boss.getWidth() * .6, .55, 2);
      at.add(forward.clone().multiply(strike ? reach + .8 : reach))
         .add(-forward.getZ() * reach * .6, Math.clamp(boss.getHeight() * (strike ? .4 : .5 + progress * .25), .55, 5),
            forward.getX() * reach * .6);
      if (!at.isChunkLoaded() || at.getY() >= at.getWorld().getMaxHeight()) return;
      if (prop == null) {
         if (activeDisplays >= GLOBAL_DISPLAYS) return;
         var item = at.getWorld().spawn(at, ItemDisplay.class, entity -> {
            entity.setVisibleByDefault(false);
            entity.setPersistent(false);
            entity.setItemStack(new ItemStack(style.prop));
            entity.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIXED);
            entity.setBrightness(new Display.Brightness(15, 15));
            entity.setTeleportDuration(4);
            entity.setInterpolationDuration(4);
         });
         if (!item.isValid()) return;
         prop = item;
         activeDisplays++;
         for (var viewer : viewers) viewer.showEntity(plugin, prop);
      } else prop.teleport(at);
      float scale = (float)Math.clamp((style == BossStyle.EXECUTIONER ? 1.25 : .75) + boss.getHeight() * .2, .85, 3);
      prop.setInterpolationDelay(0);
      prop.setTransformation(new Transformation(new Vector3f(),
         new AxisAngle4f(strike ? 1.45f : -.55f - progress * 1.3f, 1, 0, 0),
         new Vector3f(scale), new AxisAngle4f(progress * .65f, 0, 1, 0)));
   }

   void clearProp() {
      if (prop == null) return;
      prop.remove();
      prop = null;
      activeDisplays--;
   }

   private void clearProjectile() {
      if (projectile == null) return;
      projectile.remove();
      projectile = null;
      activeDisplays--;
   }

   void clearDisplays() {
      for (var display : displays) display.remove();
      activeDisplays -= displays.size();
      displays.clear();
   }

   void close() {
      bars.forEach(Player::hideBossBar);
      bars.clear();
      viewers.clear();
      clearProp();
      clearDisplays();
      clearProjectile();
   }
}
