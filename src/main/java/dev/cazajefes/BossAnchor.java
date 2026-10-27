package dev.cazajefes;

import java.util.*;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/** Holds a caster in place without cancelling damage or changing its health. */
final class BossAnchor {
    record Snapshot(boolean ai, boolean gravity, boolean collidable) {
        static Snapshot capture(LivingEntity entity) {
            return new Snapshot(entity instanceof Mob mob && mob.hasAI(), entity.hasGravity(), entity.isCollidable());
        }
        void freeze(LivingEntity entity) {
            if (entity instanceof Mob mob) mob.setAI(false);
            entity.setGravity(false);
            entity.setCollidable(false);
            entity.setVelocity(new Vector());
        }
        void restore(LivingEntity entity) {
            if (entity instanceof Mob mob) mob.setAI(ai);
            entity.setGravity(gravity);
            entity.setCollidable(collidable);
            entity.setVelocity(new Vector());
        }
    }
    private final CazaJefesPlugin plugin;
    private final Map<UUID, Hold> active = new HashMap<>();
    BossAnchor(CazaJefesPlugin plugin) { this.plugin = plugin; }
    void hold(LivingEntity boss, int ticks) {
        remove(boss.getUniqueId());
        Hold hold = new Hold(boss, ticks);
        active.put(boss.getUniqueId(), hold);
        hold.snapshot.freeze(boss);
        hold.runTaskTimer(plugin, 1, 1);
    }
    private final class Hold extends BukkitRunnable {
        final LivingEntity boss;
        final Location origin;
        final Snapshot snapshot;
        int remaining;
        Hold(LivingEntity boss, int ticks) {
            this.boss = boss;
            origin = boss.getLocation().clone();
            snapshot = Snapshot.capture(boss);
            remaining = ticks;
        }
        public void run() {
            if (!boss.isValid() || boss.isDead() || remaining-- <= 0) { finish(); return; }
            boss.setVelocity(new Vector());
            if (!boss.getWorld().equals(origin.getWorld()) || boss.getLocation().distanceSquared(origin) > .0001) boss.teleport(origin);
        }
        void finish() {
            cancel();
            active.remove(boss.getUniqueId(), this);
            if (boss.isValid() && !boss.isDead()) snapshot.restore(boss);
        }
    }
    boolean held(UUID id) { return active.containsKey(id); }
    void remove(UUID id) { Hold hold = active.get(id); if (hold != null) hold.finish(); }
    void clear() { for (Hold hold : new ArrayList<>(active.values())) hold.finish(); }
}
