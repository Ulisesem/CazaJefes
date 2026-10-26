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
   private final Map<UUID,Long> nextArrow=new HashMap<>();
   private final Map<UUID,String> signal=new HashMap<>();

   public HuntTracker(JavaPlugin var1, BossManager var2, RelicItems var3) {
      this.plugin = var1;
      this.bosses = var2;
      this.items = var3;
      this.task = var1.getServer().getScheduler().runTaskTimer(var1, this::update, 1L, 4L);
   }

   private void update() {
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
               announceSignal(var3,"none:"+var3.getWorld().getUID(),"✦ Brújula · No hay bosses ni huevos activos en este mundo.");
               this.showNoSignal(var3);
            } else {
               announceSignal(var3,var4.id().toString(),"✦ Brújula · "+(var4.egg()?"Huevo detectado: ":"Boss detectado: ")+var4.name());
               Location var5 = var4.location().clone();
               var3.setCompassTarget(var5);
               var5.add(0.5, 0.8, 0.5);
               this.drawTrail(var3, var5);
               this.showTarget(var3, var4, var5);
            }
         }
      }
   }

   private void announceSignal(Player player,String key,String message){if(!key.equals(signal.put(player.getUniqueId(),key)))player.sendMessage(Component.text(message,NamedTextColor.GREEN));}
   private boolean hasCompass(Player var1) {
      return this.items.isTracker(var1.getInventory().getItemInMainHand())||this.items.isTracker(var1.getInventory().getItemInOffHand());
   }

   private void drawTrail(Player player,Location target){
      long now=System.currentTimeMillis();if(now<nextArrow.getOrDefault(player.getUniqueId(),0L))return;
      nextArrow.put(player.getUniqueId(),now+10000);
      Location start=player.getLocation().add(0,.35,0);org.bukkit.util.Vector direction=target.toVector().subtract(start.toVector());if(direction.lengthSquared()<.25)return;direction.normalize();org.bukkit.util.Vector side=new org.bukkit.util.Vector(-direction.getZ(),0,direction.getX());if(side.lengthSquared()<.01)side=new org.bukkit.util.Vector(1,0,0);else side.normalize();
      for(double offset:new double[]{2.5,5,7.5}){Location tip=start.clone().add(direction.clone().multiply(Math.min(offset,Math.sqrt(player.getLocation().distanceSquared(target)))));for(double k=0;k<=1.5;k+=.12){arrowPoint(player,tip.clone().subtract(direction.clone().multiply(k)));if(k<=.9)for(int sign:new int[]{-1,1})arrowPoint(player,tip.clone().subtract(direction.clone().multiply(k)).add(side.clone().multiply(sign*k*.7)));}}
   }
   private void arrowPoint(Player player,Location point){player.spawnParticle(Particle.DUST,point,1,0,0,0,0,new Particle.DustOptions(org.bukkit.Color.LIME,1.5f));}

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
      if(!BossWarnings.active(var1))var1.sendActionBar(Component.text("➜ " + var2.name()+" · "+(int)var1.getLocation().distance(var3)+" m", NamedTextColor.AQUA));
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
      signal.remove(var1.getUniqueId());
      HuntTracker.Session var2 = this.sessions.remove(var1.getUniqueId());
      if (var2 != null && var1.getScoreboard() == var2.board) {
         var1.setScoreboard(var2.previous);
      }
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent var1) {
      this.sessions.remove(var1.getPlayer().getUniqueId());nextArrow.remove(var1.getPlayer().getUniqueId());signal.remove(var1.getPlayer().getUniqueId());
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

      this.sessions.clear();nextArrow.clear();signal.clear();
   }

   private record Session(Scoreboard previous, Scoreboard board, Objective objective) {
   }
}
