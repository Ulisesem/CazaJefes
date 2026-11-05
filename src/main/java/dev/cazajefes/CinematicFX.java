package dev.cazajefes;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

final class CinematicFX {
   private CinematicFX() {}

   static void ring(Location at, List<Player> viewers, double radius, double pitch, int count, Color from, Color to) {
      var data = new Particle.DustTransition(from, to, .85f);
      for (int index = 0; index < count; index++) {
         double angle = pitch + Math.PI * 2 * index / count;
         var point = at.clone().add(Math.cos(angle) * radius, .12, Math.sin(angle) * radius);
         for (Player viewer : viewers) CosmeticFX.to(viewer, Particle.DUST_COLOR_TRANSITION, point, 1, 0, 0, 0, 0, data);
      }
   }

   static List<Player> audience(Location at, double radius) {
      return List.copyOf(at.getWorld().getNearbyPlayers(at, radius));
   }

   static void portal(Location at, BossStyle style, int frame) {
      var viewers = audience(at, 26);
      if (viewers.isEmpty()) return;
      double radius = .45 + frame * .21;
      ring(at, viewers, radius, frame * .4, 8, style.primary, style.accent);
      var core = at.clone().add(0, .6 + frame * .15, 0);
      for (Player viewer : viewers) {
         CosmeticFX.to(viewer, Particle.REVERSE_PORTAL, core, 4, .18, .37, .18, .025, null);
         if (frame == 3) CosmeticFX.to(viewer, style.particle, core, 8, .24, .42, .24, .025, null);
      }
   }

   static void minionHit(LivingEntity minion, LivingEntity target) {
      BossStyle style = BossStyle.of(minion.getType());
      var at = target.getLocation().add(0, target.getHeight() * .5, 0);
      CosmeticFX.emit(style.particle, at, 6, .23, .28, .23, .025);
      CosmeticFX.emit(Particle.DUST_COLOR_TRANSITION, at, 3, .16, .24, .16, 0,
         new Particle.DustTransition(style.primary, style.accent, .8f));
   }

   static void impact(LivingEntity boss, BossStyle style, List<Player> viewers) {
      Location base = boss.getLocation();
      double width = Math.clamp(boss.getWidth() * .8, .7, 2.8);
      switch (style) {
         case EXECUTIONER, WILD -> swipe(boss, style, viewers);
         case ARCHER, SKY -> {
            Location origin = boss.getEyeLocation();
            Vector direction = origin.getDirection();
            for (int step = 1; step <= 8; step++) {
               Location at = origin.clone().add(direction.clone().multiply(step * .4));
               for (Player viewer : viewers) CosmeticFX.to(viewer, style.particle, at, 1, 0, 0, 0, 0, null);
            }
         }
         case ARCANE, VOID, VENOM -> {
            ring(base, viewers, width + .45, 0, 12, style.primary, style.accent);
            ring(base.clone().add(0, Math.min(2.5, boss.getHeight() * .65), 0),
               viewers, width * .7, Math.PI / 6, 9, style.accent, style.primary);
         }
         default -> {
            ring(base, viewers, width, 0, 10, style.primary, style.accent);
            ring(base, viewers, width + .8, Math.PI / 8, 12, style.accent, style.primary);
         }
      }
   }

   private static void swipe(LivingEntity boss, BossStyle style, List<Player> viewers) {
      Location at = boss.getLocation().add(0, Math.clamp(boss.getHeight() * .45, .4, 4), 0);
      Vector forward = boss.getLocation().getDirection().setY(0);
      if (forward.lengthSquared() < .01) forward.setZ(1);
      forward.normalize();
      Vector right = new Vector(-forward.getZ(), 0, forward.getX());
      double radius = Math.clamp(boss.getWidth() * .75 + 1, 1.3, 3.1);
      var dust = new Particle.DustTransition(style.accent, style.primary, 1.1f);
      for (int i = 0; i < 11; i++) {
         double angle = -1.2 + i * .24;
         Location point = at.clone().add(forward.clone().multiply(Math.cos(angle) * radius))
            .add(right.clone().multiply(Math.sin(angle) * radius)).add(0, .35 - i * .07, 0);
         for (Player viewer : viewers) CosmeticFX.to(viewer, Particle.DUST_COLOR_TRANSITION, point, 1, 0, 0, 0, 0, dust);
      }
   }

   static void book(Player caster, Player recipient, String id) {
      Color primary = switch (id) {
         case "haste" -> Color.LIME;
         case "fireresistance" -> Color.ORANGE;
         case "invisibility" -> Color.fromRGB(186, 119, 240);
         case "repairitem" -> Color.AQUA;
         case "burst", "greaterburst" -> Color.WHITE;
         default -> Color.fromRGB(223, 133, 255);
      };
      Location at = recipient.getLocation();
      var viewers = audience(at, 24);
      if (!id.equals("burst") && !id.equals("greaterburst")) {
         ring(at, viewers, .86, 0, 10, primary, Color.WHITE);
         ring(at.clone().add(0, 1.1, 0), viewers, .55, .3, 8, primary, Color.WHITE);
      }
      for (Player viewer : viewers) {
         CosmeticFX.to(viewer, Particle.ENCHANT, at.clone().add(0, .95, 0), 9, .32, .45, .32, .1, null);
         CosmeticFX.to(viewer, Particle.END_ROD, at.clone().add(0, 1.25, 0), 4, .22, .4, .22, .018, null);
      }
      caster.playSound(caster.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, .65f, id.equals("haste") ? 1.55f : 1.08f);
   }

   static void lootTrail(Location at, double angle, boolean rare, Particle motif) {
      Color from = rare ? Color.YELLOW : Color.fromRGB(79, 255, 210);
      Color to = rare ? Color.FUCHSIA : Color.fromRGB(115, 149, 255);
      var dust = new Particle.DustTransition(from, to, rare ? 1.15f : .8f);
      var viewers = audience(at, 32);
      for (Player viewer : viewers) {
         for (int side : new int[]{-1, 1}) {
            var point = at.clone().add(Math.cos(angle) * .29 * side, .12, Math.sin(angle) * .29 * side);
            CosmeticFX.to(viewer, Particle.DUST_COLOR_TRANSITION, point, 1, 0, 0, 0, 0, dust);
         }
         CosmeticFX.to(viewer, Particle.END_ROD, at, 2, .12, .12, .12, .012, null);
         if (rare) CosmeticFX.to(viewer, Particle.ELECTRIC_SPARK, at, 2, .2, .22, .2, .025, null);
         else if (motif != null && motif.getDataType() == Void.class) {
            CosmeticFX.to(viewer, motif, at, 1, .08, .12, .08, .006, null);
         }
      }
   }
}
