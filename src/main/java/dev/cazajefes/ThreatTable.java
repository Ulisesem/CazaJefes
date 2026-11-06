package dev.cazajefes;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.function.Predicate;

final class ThreatTable {
   private static final NamespacedKey BOSS = new NamespacedKey("cazajefes", BossKeys.BOSS);
   private record Entry(double value, long updated) {}
   private static final class Fight {
      final Map<UUID, Entry> scores = new HashMap<>();
      UUID forced, focus;
      long forcedUntil, ready;
   }
   private static final Map<UUID, Fight> fights = new HashMap<>();

   static double score(double points, long elapsed) {
      if (elapsed >= 60000) return 0;
      return points * Math.pow(.5, Math.max(0, elapsed) / 12000d);
   }

   static void add(LivingEntity boss, Player player, double value) {
      if (!Double.isFinite(value) || value <= 0 || fights.size() >= 1024 && !fights.containsKey(boss.getUniqueId())) return;
      long now = System.currentTimeMillis();
      Fight fight = fights.computeIfAbsent(boss.getUniqueId(), ignored -> new Fight());
      fight.scores.values().removeIf(entry -> now - entry.updated() >= 60000);
      UUID id = player.getUniqueId();
      if (fight.scores.size() >= 32 && !fight.scores.containsKey(id)) {
         var weakest = fight.scores.entrySet().stream().min(Comparator.comparingDouble(
            entry -> score(entry.getValue().value(), now - entry.getValue().updated()))).orElseThrow();
         if (value <= score(weakest.getValue().value(), now - weakest.getValue().updated())) return;
         fight.scores.remove(weakest.getKey());
      }
      fight.scores.compute(id, (ignored, old) -> new Entry(Math.min(2000, value
         + (old == null ? 0 : score(old.value(), now - old.updated()))), now));
   }

   static Player pick(LivingEntity boss, double range, Predicate<Player> filter) {
      var at = boss.getLocation();
      Fight fight = fights.computeIfAbsent(boss.getUniqueId(),ignored -> new Fight());
      var candidates = boss.getWorld().getNearbyPlayers(at, range).stream()
         .filter(p -> CombatGuard.eligible(p) && p.getLocation().distanceSquared(at) <= range * range
            && (range <= 60 || p.getLocation().distanceSquared(at) <= 576 || fight.scores.containsKey(p.getUniqueId()))
            && ArenaBoundary.allows(boss,p.getLocation()) && filter.test(p)).toList();
      if (candidates.isEmpty()) return null;
      long now = System.currentTimeMillis();
      if (fight != null && now < fight.forcedUntil) {
         for (Player candidate : candidates) if (candidate.getUniqueId().equals(fight.forced)) return candidate;
      }
      for(Player candidate:candidates) if(candidate.getUniqueId().equals(fight.focus)) return candidate;
      var facing=at.getDirection().setY(0).normalize();
      Player selected = candidates.stream().max(Comparator.<Player,Boolean>comparing(player -> {
         var direction=player.getLocation().toVector().subtract(at.toVector()).setY(0);
         return direction.lengthSquared()>.001&&facing.dot(direction.normalize())>=.45;
      }).thenComparingDouble(player -> {
         Entry e = fight == null ? null : fight.scores.get(player.getUniqueId());
         double weight = e == null ? 0 : score(e.value(), now - e.updated());
         return weight;
      }).thenComparingDouble(player -> -player.getLocation().distanceSquared(at))).orElseThrow();
      if (fight != null) fight.focus = selected.getUniqueId();
      return selected;
   }

   static void book(Player caster, Player recipient) {
      if (!CombatGuard.eligible(caster)) return;
      var at = recipient.getLocation();
      for (var entity : at.getWorld().getNearbyEntities(at, 24, 16, 24)) {
         if (!(entity instanceof LivingEntity boss) || !marked(boss)
             || !BossParticipation.participants(boss).contains(recipient.getUniqueId())) continue;
         add(boss, caster, caster.equals(recipient) ? 5 : 12);
      }
   }

   static void taunt(Player player) {
      if (!CombatGuard.eligible(player)) return;
      var eye = player.getEyeLocation();
      var result = eye.getWorld().rayTraceEntities(eye, eye.getDirection(), 16, .35, ThreatTable::marked);
      if (result == null || !(result.getHitEntity() instanceof LivingEntity boss) || !player.hasLineOfSight(boss)
          || !BossParticipation.participants(boss).contains(player.getUniqueId())) {
         player.sendMessage("Apunta a un jefe al que ya hayas golpeado.");
         return;
      }
      var fight = fights.get(boss.getUniqueId());
      long now = System.currentTimeMillis();
      if (fight != null && now < fight.ready) {
         player.sendMessage("La provocación está recargando para este jefe.");
         return;
      }
      add(boss, player, 18);
      fight = fights.get(boss.getUniqueId());
      if (fight == null) return;
      fight.forced = player.getUniqueId();
      fight.forcedUntil = now + 4000;
      fight.ready = now + 20000;
      player.sendMessage("Provocación · El jefe te prioriza durante 4 s.");
   }

   private static boolean marked(org.bukkit.entity.Entity entity) {
      return entity.getPersistentDataContainer().has(BOSS, PersistentDataType.BYTE) && entity.isValid() && !entity.isDead();
   }

   static void remove(UUID id) { fights.remove(id); }
   static void clear() { fights.clear(); }
}
