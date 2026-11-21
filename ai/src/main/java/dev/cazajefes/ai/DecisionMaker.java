package dev.cazajefes.ai;

import java.util.List;
import java.util.function.ToLongFunction;
import java.util.random.RandomGenerator;

public final class DecisionMaker {
    private DecisionMaker() {}

    public static BossAttack choose(List<BossAttack> attacks, double distance, long tick,
                                    ToLongFunction<String> readyAt, RandomGenerator random) {
        return choose(attacks, distance, tick, readyAt, random, BehaviorTracker.PlayerBehavior.NEUTRAL);
    }
    
    /** Choose an attack adapted to the detected player behavior pattern. */
    public static BossAttack choose(List<BossAttack> attacks, double distance, long tick,
                                    ToLongFunction<String> readyAt, RandomGenerator random,
                                    BehaviorTracker.PlayerBehavior behavior) {
        return choose(attacks, tick, readyAt, random, attack -> attack.category().includes(distance), behavior);
    }

    static BossAttack choose(List<BossAttack> attacks,long tick,ToLongFunction<String> readyAt,
                             RandomGenerator random,java.util.function.Predicate<BossAttack> inRange){
        return choose(attacks, tick, readyAt, random, inRange, BehaviorTracker.PlayerBehavior.NEUTRAL);
    }
    
    static BossAttack choose(List<BossAttack> attacks, long tick, ToLongFunction<String> readyAt,
                             RandomGenerator random, java.util.function.Predicate<BossAttack> inRange,
                             BehaviorTracker.PlayerBehavior behavior) {
        // Prioritize context-appropriate attacks: boost weight for attacks that counter the detected behavior
        long total = 0;
        for (BossAttack attack : attacks) {
            if (eligible(attack, tick, readyAt, inRange)) {
                total += effectiveWeight(attack, behavior);
            }
        }
        if (total == 0) return null;

        // Each eligible skill owns a consecutive interval of weight integers in [0,total).
        // A uniform draw therefore gives exactly weight / total, with no sorting or temporary list.
        long roll = random.nextLong(total);
        for (BossAttack attack : attacks) {
            if (!eligible(attack, tick, readyAt, inRange)) continue;
            int w = effectiveWeight(attack, behavior);
            if (roll < w) return attack;
            roll -= w;
        }
        throw new IllegalStateException("Attack availability changed during selection");
    }
    
    /** Adjust attack weight based on player behavior to create adaptive AI. */
    private static int effectiveWeight(BossAttack attack, BehaviorTracker.PlayerBehavior behavior) {
        int base = attack.weight();
        if (base == 0) return 0;
        
        switch (behavior) {
            case TOWER_FARMING:
                // Prioritize attacks that hit elevated targets (FaultLine, Shockwave, Meteorite)
                if (attack instanceof dev.cazajefes.ai.attack.FaultLineAttack) return base * 4;
                if (attack instanceof dev.cazajefes.ai.attack.ShockwaveAttack) return base * 2;
                if (attack instanceof dev.cazajefes.ai.attack.MeteoriteAoEAttack) return base * 2;
                // Deprioritize melee when player is above
                if (attack instanceof dev.cazajefes.ai.attack.MeleeSweep) return Math.max(1, base / 3);
                return base;
                
            case HIDING:
                // Prioritize AoE and area attacks that hit through cover
                if (attack instanceof dev.cazajefes.ai.attack.ShockwaveAttack) return base * 3;
                if (attack instanceof dev.cazajefes.ai.attack.MeteoriteAoEAttack) return base * 3;
                if (attack instanceof dev.cazajefes.ai.attack.LightningChainAttack) return base * 2;
                return base;
                
            case KITING:
                // Prioritize long-range and fast projectiles
                if (attack instanceof dev.cazajefes.ai.attack.LinearProjectileAttack) return base * 2;
                if (attack instanceof dev.cazajefes.ai.attack.FamilyVolleyAttack) return base * 2;
                if (attack.category() == AttackCategory.LONG_RANGE || attack.category() == AttackCategory.GLOBAL) return base * 2;
                if (attack instanceof dev.cazajefes.ai.attack.MeleeSweep) return Math.max(1, base / 4);
                return base;
                
            case AGGRESSIVE:
                // Counter with close-range AoE and knockback
                if (attack instanceof dev.cazajefes.ai.attack.ShockwaveAttack) return base * 3;
                if (attack instanceof dev.cazajefes.ai.attack.FaultLineAttack) return base * 2;
                if (attack instanceof dev.cazajefes.ai.attack.MeleeSweep) return base * 2;
                return base;
                
            case ORBITING:
                // Use cone/spread attacks to hit orbiting targets
                if (attack instanceof dev.cazajefes.ai.attack.FamilyVolleyAttack) return base * 2;
                if (attack instanceof dev.cazajefes.ai.attack.ShockwaveAttack) return base * 2;
                return base;
                
            case NEUTRAL:
            default:
                return base;
        }
    }

    private static boolean eligible(BossAttack attack,long tick,ToLongFunction<String> readyAt,
                                    java.util.function.Predicate<BossAttack> inRange) {
        return attack.weight() > 0 && tick >= readyAt.applyAsLong(attack.id()) && inRange.test(attack);
    }
}
