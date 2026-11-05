package dev.cazajefes;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
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
   private static final int CAST_DISPLAYS = 18;
   private static int activeDisplays;
   private final Plugin plugin;
   private final BossBar bar = BossBar.bossBar(Component.empty(), 0, BossBar.Color.YELLOW, BossBar.Overlay.PROGRESS);
   private final Set<Player> viewers = new HashSet<>();
   private final List<BlockDisplay> displays = new ArrayList<>();

   AttackPresentation(Plugin plugin) {
      this.plugin = plugin;
   }

   void update(List<Player> audience, Component label, float progress, BossBar.Color color) {
      bar.name(label);
      bar.progress(progress);
      bar.color(color);
      var nearby = new HashSet<>(audience);
      viewers.removeIf(player -> {
         if (nearby.contains(player)) return false;
         player.hideBossBar(bar);
         for (var display : displays) player.hideEntity(plugin, display);
         return true;
      });
      for (var player : audience) {
         if (!viewers.add(player)) continue;
         player.showBossBar(bar);
         for (var display : displays) player.showEntity(plugin, display);
      }
   }

   void mark(List<Location> points, Material material) {
      clearDisplays();
      int count = Math.min(CAST_DISPLAYS, points.size());
      for (int index = 0; index < count && activeDisplays < GLOBAL_DISPLAYS; index++) {
         Location point = points.get(index * points.size() / count);
         add(point, material, false);
      }
   }

   void meteor(Location impact) {
      add(impact.clone().add(0, 6, 0), Material.MAGMA_BLOCK, true);
   }

   private void add(Location at, Material material, boolean falling) {
      if (viewers.isEmpty() || activeDisplays >= GLOBAL_DISPLAYS || !at.isChunkLoaded()) return;
      if (at.getBlockY() < at.getWorld().getMinHeight() || at.getBlockY() >= at.getWorld().getMaxHeight()) return;
      var display = at.getWorld().spawn(at, BlockDisplay.class, entity -> {
         entity.setVisibleByDefault(false);
         entity.setPersistent(false);
         entity.setBlock(material.createBlockData());
         entity.setBrightness(new Display.Brightness(15, 15));
         entity.setTransformation(transform(falling ? .65f : .18f, falling ? .65f : .065f));
         entity.setTeleportDuration(10);
         entity.setInterpolationDuration(10);
      });
      if (!display.isValid()) return;
      displays.add(display);
      activeDisplays++;
      for (var viewer : viewers) viewer.showEntity(plugin, display);
      plugin.getServer().getScheduler().runTask(plugin, () -> {
         if (!display.isValid()) return;
         if (falling) display.teleport(at.clone().subtract(0, 6, 0));
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

   void clearDisplays() {
      for (var display : displays) display.remove();
      activeDisplays -= displays.size();
      displays.clear();
   }

   void close() {
      for (var viewer : viewers) viewer.hideBossBar(bar);
      viewers.clear();
      clearDisplays();
   }
}
