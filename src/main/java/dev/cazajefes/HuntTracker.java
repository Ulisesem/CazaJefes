package dev.cazajefes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public final class HuntTracker implements Listener {
   private final JavaPlugin plugin;
   private final BossManager bosses;
   private final RelicItems items;
   private final Map<UUID, HuntTracker.Session> sessions = new HashMap<>();
   private final BukkitTask task;
   private int trailTick;

   public HuntTracker(JavaPlugin var1, BossManager var2, RelicItems var3) {
      this.plugin = var1;
      this.bosses = var2;
      this.items = var3;
      this.task = var1.getServer().getScheduler().runTaskTimer(var1, this::update, 1L, 4L);
   }

   private void update() {
      this.trailTick++;
      List<BossManager.TrackedEncounter> var1 = this.bosses.trackedEncounters();

      for (Player var3 : Bukkit.getOnlinePlayers()) {
         if (!this.hasCompass(var3)) {
            this.restore(var3);
         } else {
            BossManager.TrackedEncounter var4 = var1.stream()
               .filter(var1x -> var1x.location().getWorld().equals(var3.getWorld()))
               .min((var1x, var2) -> Double.compare(var1x.location().distanceSquared(var3.getLocation()), var2.location().distanceSquared(var3.getLocation())))
               .orElse(null);
            if (var4 == null) {
               this.showNoSignal(var3);
            } else {
               Location var5 = var4.location().clone();
               var3.setCompassTarget(var5);
               var5.add(0.5, 0.8, 0.5);
               this.drawTrail(var3, var5);
               this.showTarget(var3, var4, var5);
            }
         }
      }
   }

   private boolean hasCompass(Player var1) {
      return this.items.isTracker(var1.getInventory().getItemInMainHand());
   }

   private void drawTrail(Player var1, Location var2) {
      Location var3 = var1.getLocation().add(0.0, 0.9, 0.0);
      if (!(var3.distanceSquared(var2) > 14400.0)) {
         double var4 = var3.distance(var2);
         int var6 = Math.min(28, (int)var4);
         if (var6 > 1) {
            double var7 = this.trailTick % 32 / 32.0;

            for (int var9 = 0; var9 < 8; var9++) {
               double var10 = (var7 + var9 / 8.0) % 1.0;
               Location var12 = var3.clone()
                  .add(
                     (var2.getX() - var3.getX()) * var10,
                     (var2.getY() - var3.getY()) * var10 + Math.sin(var10 * Math.PI) * 0.6,
                     (var2.getZ() - var3.getZ()) * var10
                  );
               var1.spawnParticle(Particle.ENCHANT, var12, 1, 0.02, 0.03, 0.02, 0.0);
            }

            var2.getWorld().spawnParticle(Particle.END_ROD, var2, 2, 0.35, 0.45, 0.35, 0.01);
         }
      }
   }

   private void showTarget(Player var1, BossManager.TrackedEncounter var2, Location var3) {
      HuntTracker.Session var4 = this.session(var1);
      List var5 = List.of(
         "§7Objetivo",
         "§f" + var2.name(),
         "§6Rango: " + var2.tier() + (var2.mini() ? " • MINI" : ""),
         var2.egg() ? "§eHuevo: clic derecho" : "§cVida: " + (int)Math.ceil(var2.health()) + "/" + (int)var2.maxHealth(),
         "§dDespertó: " + (var2.egg() ? "nadie" : this.shorten(var2.awakenedBy())),
         "§8────────────"
      );
      this.setLines(var4, var5);
      var1.sendActionBar(Component.text("Rastro activo hacia " + var2.name(), NamedTextColor.AQUA));
   }

   private void showNoSignal(Player var1) {
      HuntTracker.Session var2 = this.session(var1);
      this.setLines(var2, List.of("§7Objetivo", "§fSin rastro cercano", "§7No hay huevos o jefes", "§7en este mundo.", "§8────────────"));
   }

   private HuntTracker.Session session(Player var1) {
      return this.sessions.computeIfAbsent(var1.getUniqueId(), var1x -> {
         ScoreboardManager var2 = Bukkit.getScoreboardManager();
         Scoreboard var3 = var2.getNewScoreboard();
         Objective var4 = var3.registerNewObjective("cazajefes", "dummy", Component.text("CAZA DE JEFES", NamedTextColor.LIGHT_PURPLE));
         var4.setDisplaySlot(DisplaySlot.SIDEBAR);
         HuntTracker.Session var5 = new HuntTracker.Session(var1.getScoreboard(), var3, var4);
         var1.setScoreboard(var3);
         return var5;
      });
   }

   private void setLines(HuntTracker.Session var1, List<String> var2) {
      for (String var4 : var1.board.getEntries()) {
         var1.board.resetScores(var4);
      }

      int var6 = var2.size();

      for (String var5 : var2) {
         var1.objective.getScore(var5).setScore(var6--);
      }
   }

   private String shorten(String var1) {
      if (var1 != null && !var1.isBlank()) {
         return var1.length() > 13 ? var1.substring(0, 12) + "…" : var1;
      } else {
         return "?";
      }
   }

   private void restore(Player var1) {
      HuntTracker.Session var2 = this.sessions.remove(var1.getUniqueId());
      if (var2 != null && var1.getScoreboard() == var2.board) {
         var1.setScoreboard(var2.previous);
      }
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent var1) {
      this.sessions.remove(var1.getPlayer().getUniqueId());
   }

   @EventHandler
   public void onHotbarChange(PlayerItemHeldEvent var1) {
      this.plugin.getServer().getScheduler().runTask(this.plugin, this::update);
   }

   public void stop() {
      this.task.cancel();

      for (Player var2 : Bukkit.getOnlinePlayers()) {
         this.restore(var2);
      }

      this.sessions.clear();
   }

   private record Session(Scoreboard previous, Scoreboard board, Objective objective) {
   }
}
