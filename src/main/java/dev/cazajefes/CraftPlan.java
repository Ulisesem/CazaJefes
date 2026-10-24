package dev.cazajefes;

import java.util.List;
import java.util.function.IntPredicate;

/** Plans consumption without mutating the caller's inventory. Null means insufficient ingredients. */
final class CraftPlan {
    static int[] remaining(int[] available, List<IntPredicate> ingredients) {
        int[] result=available.clone();
        for (IntPredicate ingredient:ingredients) {
            boolean found=false;
            for (int slot=0;slot<result.length;slot++) if (result[slot]>0 && ingredient.test(slot)) {
                result[slot]--; found=true; break;
            }
            if (!found) return null;
        }
        return result;
    }
}
