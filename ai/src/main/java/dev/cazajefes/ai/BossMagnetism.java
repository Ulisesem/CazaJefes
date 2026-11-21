package dev.cazajefes.ai;

import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * Configurable magnetism system for boss mobs.
 *
 * When enabled, the boss pulls nearby players toward itself under certain
 * conditions, preventing kiting and forcing engagement. The magnetism applies
 * a velocity pull toward the boss's position, similar to a gravitational force.
 *
 * All parameters are configurable via the boss JSON profile under the
 * "magnetism" key.
 *
 * Example JSON configuration:
 * <pre>{
 *   "magnetism": {
 *     "enabled": true,
 *     "minPhase": 2,
 *     "minDistance": 10.0,
 *     "maxDistance": 25.0,
 *     "pullStrength": 0.15,
 *     "cooldownTicks": 20,
 *     "onKiting": true,
 *     "onLowHealth": true,
 *     "lowHealthThreshold": 0.3
 *   }
 * }</pre>
 *
 * - minPhase: minimum boss phase to activate magnetism (default: 1)
 * - minDistance: minimum distance to activate (player farther than this)
 * - maxDistance: maximum distance to activate (player closer than this)
 * - pullStrength: velocity applied per tick toward the boss (0.01-0.5)
 * - cooldownTicks: ticks between pull applications per player (default: 20)
 * - onKiting: activate when player is detected kiting (default: true)
 * - onLowHealth: activate when player health is below threshold (default: true)
 * - lowHealthThreshold: player health fraction to trigger (0.0-1.0, default: 0.3)
 */
public final class BossMagnetism {

    /** Default configuration — disabled by default. */
    public static final Config DEFAULT = new Config(
        false,     // disabled by default; enable per-boss in JSON
        1,         // phase 1+
        10.0,      // activate when player is beyond 10 blocks
        25.0,      // deactivate when player is within 25 blocks
        0.15,      // pull strength per application
        20,        // cooldown of 20 ticks (1 second) between pulls
        true,      // activate on kiting
        true,      // activate on low health
        0.3        // 30% health threshold
    );

    /** Configuration for the magnetism behavior. */
    public record Config(boolean enabled, int minPhase, double minDistance, double maxDistance,
                         double pullStrength, int cooldownTicks, boolean onKiting,
                         boolean onLowHealth, double lowHealthThreshold) {
        public Config {
            if (minPhase < 1) minPhase = 1;
            if (minPhase > 5) minPhase = 5;
            if (minDistance <= 0) minDistance = 10.0;
            if (maxDistance <= minDistance) maxDistance = minDistance + 10.0;
            if (pullStrength <= 0) pullStrength = 0.15;
            if (pullStrength > 0.5) pullStrength = 0.5;
            if (cooldownTicks < 5) cooldownTicks = 20;
            if (cooldownTicks > 200) cooldownTicks = 200;
            if (lowHealthThreshold < 0.05) lowHealthThreshold = 0.3;
            if (lowHealthThreshold > 1.0) lowHealthThreshold = 1.0;
        }
    }

    private Config config = DEFAULT;

    /** Per-player cooldown tracking: last tick this player was pulled. */
    private final java.util.Map<java.util.UUID, Long> lastPullTick = new java.util.HashMap<>();
    private long currentTick = 0;

    /** Parse configuration from the boss JSON profile. */
    public static Config readConfig(JsonObject json) {
        if (json == null || !json.has("magnetism")) return DEFAULT;
        var obj = json.getAsJsonObject("magnetism");
        boolean enabled = obj.has("enabled") && obj.get("enabled").getAsBoolean();
        int minPhase = obj.has("minPhase") ? obj.get("minPhase").getAsInt() : 1;
        double minDistance = obj.has("minDistance") ? obj.get("minDistance").getAsDouble() : 10.0;
        double maxDistance = obj.has("maxDistance") ? obj.get("maxDistance").getAsDouble() : 25.0;
        double pullStrength = obj.has("pullStrength") ? obj.get("pullStrength").getAsDouble() : 0.15;
        int cooldownTicks = obj.has("cooldownTicks") ? obj.get("cooldownTicks").getAsInt() : 20;
        boolean onKiting = !obj.has("onKiting") || obj.get("onKiting").getAsBoolean();
        boolean onLowHealth = !obj.has("onLowHealth") || obj.get("onLowHealth").getAsBoolean();
        double lowHealthThreshold = obj.has("lowHealthThreshold") ? obj.get("lowHealthThreshold").getAsDouble() : 0.3;
        return new Config(enabled, minPhase, minDistance, maxDistance, pullStrength,
            cooldownTicks, onKiting, onLowHealth, lowHealthThreshold);
    }

    /** Apply the configuration from the boss profile. */
    public void configure(Config cfg) {
        this.config = cfg;
        lastPullTick.clear();
    }

    /**
     * Advance the internal tick counter (call once per boss tick).
     */
    public void tick() {
        currentTick++;
    }

    /**
     * Evaluate and apply magnetism pull on the target player.
     * Returns true if a pull was applied this tick.
     *
     * @param boss the custom boss instance
     * @param target the player to potentially pull
     * @param behavior the detected player behavior
     * @param phase the current boss phase (1-based)
     */
    public boolean evaluate(CustomBoss boss, Player target, BehaviorTracker.PlayerBehavior behavior, int phase) {
        if (!config.enabled() || target == null || !target.isValid()) {
            return false;
        }

        // Check phase requirement
        if (phase < config.minPhase()) {
            return false;
        }

        // Check distance requirement
        double distanceSquared = boss.body().getLocation().distanceSquared(target.getLocation());
        double distance = Math.sqrt(distanceSquared);
        if (distance < config.minDistance() || distance > config.maxDistance()) {
            return false;
        }

        // Check condition triggers
        boolean shouldPull = false;

        if (config.onKiting() && behavior == BehaviorTracker.PlayerBehavior.KITING) {
            shouldPull = true;
        }

        if (config.onLowHealth()) {
            double healthRatio = target.getHealth() / target.getMaxHealth();
            if (healthRatio < config.lowHealthThreshold()) {
                shouldPull = true;
            }
        }

        // If neither condition is configured, always pull when in range and phase
        if (!config.onKiting() && !config.onLowHealth()) {
            shouldPull = true;
        }

        if (!shouldPull) {
            return false;
        }

        // Check per-player cooldown
        Long lastPull = lastPullTick.get(target.getUniqueId());
        if (lastPull != null && currentTick - lastPull < config.cooldownTicks()) {
            return false;
        }

        // Apply the pull
        applyPull(boss.body(), target, distance);
        lastPullTick.put(target.getUniqueId(), currentTick);

        // Clean up old entries (players who left)
        if (currentTick % 100 == 0) {
            lastPullTick.entrySet().removeIf(entry -> !isPlayerOnline(entry.getKey()));
        }

        return true;
    }

    /** Apply velocity pull toward the boss. */
    private void applyPull(LivingEntity boss, Player target, double distance) {
        Location bossLoc = boss.getEyeLocation();
        Location targetLoc = target.getEyeLocation();

        // Direction from player to boss
        Vector direction = bossLoc.toVector().subtract(targetLoc.toVector());
        double length = direction.length();
        if (length < 0.01) return;

        direction.normalize();

        // Scale pull strength: stronger when farther, weaker when closer
        double distanceFactor = Math.clamp((distance - config.minDistance()) / (config.maxDistance() - config.minDistance()), 0, 1);
        double strength = config.pullStrength() * (0.5 + distanceFactor * 0.5);

        // Apply horizontal pull only (don't pull vertically to avoid flying effect)
        Vector pull = new Vector(direction.getX(), 0, direction.getZ()).multiply(strength);

        // Add slight upward component if player is below boss (prevent getting stuck on terrain)
        if (targetLoc.getY() < bossLoc.getY() - 1.0) {
            pull.setY(Math.min(pull.length() * 0.3, 0.1));
        }

        Vector currentVelocity = target.getVelocity();
        target.setVelocity(currentVelocity.add(pull));
    }

    /** Check if a player is still online (for cleanup). */
    private boolean isPlayerOnline(java.util.UUID uuid) {
        try {
            return org.bukkit.Bukkit.getPlayer(uuid) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /** Reset the magnetism state (e.g., when boss respawns or target changes). */
    public void reset() {
        lastPullTick.clear();
        currentTick = 0;
    }

    /** Return the current configuration. */
    public Config config() {
        return config;
    }
}
