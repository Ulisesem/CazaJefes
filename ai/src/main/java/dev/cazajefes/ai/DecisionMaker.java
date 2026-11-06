package dev.cazajefes.ai;

import java.util.List;
import java.util.function.ToLongFunction;
import java.util.random.RandomGenerator;

public final class DecisionMaker {
    private DecisionMaker() {}

    public static BossAttack choose(List<BossAttack> attacks, double distance, long tick,
                                    ToLongFunction<String> readyAt, RandomGenerator random) {
        long total = 0;
        for (BossAttack attack : attacks) {
            if (eligible(attack,distance,tick,readyAt)) total += attack.weight();
        }
        if (total == 0) return null;

        // Each eligible skill owns a consecutive interval of weight integers in [0,total).
        // A uniform draw therefore gives exactly weight / total, with no sorting or temporary list.
        long roll = random.nextLong(total);
        for (BossAttack attack : attacks) {
            if (!eligible(attack,distance,tick,readyAt)) continue;
            if (roll < attack.weight()) return attack;
            roll -= attack.weight();
        }
        throw new IllegalStateException("Attack availability changed during selection");
    }

    private static boolean eligible(BossAttack attack,double distance,long tick,ToLongFunction<String> readyAt) {
        return attack.category().includes(distance) && attack.weight() > 0 && tick >= readyAt.applyAsLong(attack.id());
    }
}
