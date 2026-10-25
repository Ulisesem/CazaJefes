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
      return this.items.isTracker(var1.getInventory().getItemInMainHand())||this.items.isTracker(var1.getInventory().getItemInOffHand());
   }

   private void drawTrail(Player player,Location target){
      Location start=player.getLocation().add(0,.65,0);org.bukkit.util.Vector delta=target.toVector().subtract(start.toVector());double distance=delta.length();if(distance<.5)return;
      org.bukkit.util.Vector direction=delta.normalize();double length=Math.min(18,distance);double pulse=(trailTick%10)/10.0;
      for(double step=.8+pulse;step<length;step+=1.1){Location point=start.clone().add(direction.clone().multiply(step));point.add(0,Math.sin(step*.6+trailTick*.2)*.15,0);player.spawnParticle(Particle.DUST,point,1,0,.02,0,0,new org.bukkit.Particle.DustOptions(org.bukkit.Color.AQUA,1.2f));}
      Location arrow=start.clone().add(direction.clone().multiply(Math.min(4,distance)));org.bukkit.util.Vector side=new org.bukkit.util.Vector(-direction.getZ(),0,direction.getX());for(int sign:new int[]{-1,1})for(double k=.2;k<=.8;k+=.2)player.spawnParticle(Particle.END_ROD,arrow.clone().subtract(direction.clone().multiply(k)).add(side.clone().multiply(sign*k*.6)),1,0,0,0,0);
      if(distance<48)player.spawnParticle(Particle.ENCHANT,target,3,.3,.4,.3,0);
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
      var1.sendActionBar(Component.text("➜ " + var2.name()+" · "+(int)var1.getLocation().distance(var3)+" m", NamedTextColor.AQUA));
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
