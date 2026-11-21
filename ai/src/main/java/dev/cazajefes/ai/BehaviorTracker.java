package dev.cazajefes.ai;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Tracks player behavior patterns to enable adaptive AI responses.
 * Detects farming (vertical evasion), kiting, hiding behind cover, and aggressive play.
 */
public final class BehaviorTracker {
    private record Sample(double yDelta, double distance, boolean lineOfSight, long tick) {}
    
    private final Map<UUID, PlayerProfile> profiles = new HashMap<>();
    
    private static final int SAMPLE_WINDOW = 200; // ticks to remember
    private static final int FARMING_THRESHOLD = 4; // consecutive elevated positions
    private static final int KITE_THRESHOLD = 6; // consecutive increasing distances
    private static final int AGGRESSIVE_THRESHOLD = 4; // consecutive close-range positions
    
    public void observe(CustomBoss boss, Player target) {
        if (target == null) return;
        var profile = profiles.computeIfAbsent(target.getUniqueId(), uuid -> new PlayerProfile());
        long tick = boss.environment().phase(boss); // use any monotonic value
        var body = boss.body();
        double distance = body.getLocation().distance(target.getLocation());
        double yDelta = target.getY() - body.getY();
        boolean los = boss.environment().canSee(boss, target);
        profile.addSample(new Sample(yDelta, distance, los, System.currentTimeMillis() / 50L));
    }
    
    public PlayerBehavior getBehavior(Player target) {
        var profile = profiles.get(target.getUniqueId());
        if (profile == null || profile.samples.isEmpty()) return PlayerBehavior.NEUTRAL;
        return profile.classify();
    }
    
    public void forget(Player target) {
        profiles.remove(target.getUniqueId());
    }
    
    /** Behavioral classifications the AI can react to. */
    public enum PlayerBehavior {
        /** Player is standing and fighting normally. */
        NEUTRAL,
        /** Player repeatedly stays above the boss (tower farming). */
        TOWER_FARMING,
        /** Player is running away while attacking (kiting). */
        KITING,
        /** Player hides behind blocks and peeks out. */
        HIDING,
        /** Player is aggressively close-range attacking. */
        AGGRESSIVE,
        /** Player is orbiting the boss at consistent distance. */
        ORBITING
    }
    
    private static final class PlayerProfile {
        final java.util.Deque<Sample> samples = new java.util.ArrayDeque<>();
        int elevatedStreak = 0;
        int retreatingStreak = 0;
        int aggressiveStreak = 0;
        int hiddenStreak = 0;
        double lastDistance = -1;
        
        void addSample(Sample s) {
            samples.addLast(s);
            while (!samples.isEmpty() && s.tick() - samples.peekFirst().tick() > SAMPLE_WINDOW) {
                samples.pollFirst();
            }
            classifySample(s);
        }
        
        void classifySample(Sample s) {
            // Tower farming detection: player consistently above boss
            if (s.yDelta >= 1.5) {
                elevatedStreak++;
            } else {
                elevatedStreak = Math.max(0, elevatedStreak - 3);
            }
            
            // Kiting detection: player consistently increasing distance
            if (lastDistance >= 0) {
                if (s.distance > lastDistance + 0.5) {
                    retreatingStreak++;
                } else if (s.distance < lastDistance - 0.5) {
                    retreatingStreak = Math.max(0, retreatingStreak - 2);
                }
            }
            lastDistance = s.distance;
            
            // Aggressive detection: player consistently close
            if (s.distance < 4 && s.lineOfSight) {
                aggressiveStreak++;
            } else {
                aggressiveStreak = Math.max(0, aggressiveStreak - 2);
            }
            
            // Hiding detection: player frequently loses line of sight
            if (!s.lineOfSight && s.distance < 15) {
                hiddenStreak++;
            } else {
                hiddenStreak = Math.max(0, hiddenStreak - 2);
            }
        }
        
        PlayerBehavior classify() {
            if (elevatedStreak >= FARMING_THRESHOLD) return PlayerBehavior.TOWER_FARMING;
            if (hiddenStreak >= 8) return PlayerBehavior.HIDING;
            if (retreatingStreak >= KITE_THRESHOLD) return PlayerBehavior.KITING;
            if (aggressiveStreak >= AGGRESSIVE_THRESHOLD) return PlayerBehavior.AGGRESSIVE;
            
            // Orbiting: samples show consistent distance with frequent LOS
            if (samples.size() >= 10) {
                double avgDist = samples.stream().mapToDouble(Sample::distance).average().orElse(0);
                double variance = samples.stream().mapToDouble(s -> Math.pow(s.distance() - avgDist, 2)).average().orElse(0);
                if (variance < 4 && avgDist > 3 && avgDist < 15) return PlayerBehavior.ORBITING;
            }
            return PlayerBehavior.NEUTRAL;
        }
    }
}
