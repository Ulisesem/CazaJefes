package dev.cazajefes;

import java.util.*;
import java.util.function.IntPredicate;
import org.junit.Test;
import static org.junit.Assert.*;

public class CraftPlanTest {
    @Test public void repeatedIngredientsConsumeExactCount() {
        int[] original={2,1,64};
        int[] result=CraftPlan.remaining(original,List.of(i->i==0,i->i==0,i->i==1));
        assertArrayEquals(new int[]{0,0,64},result);
        assertArrayEquals(new int[]{2,1,64},original);
    }
    @Test public void insufficientDoesNotMutateInventory() {
        int[] original={1};
        assertNull(CraftPlan.remaining(original,List.of(i->true,i->true)));
        assertArrayEquals(new int[]{1},original);
    }
    @Test public void similarLookingWrongIngredientIsRejected() {
        assertNull(CraftPlan.remaining(new int[]{64,0},List.of(i->i==1)));
    }
    @Test public void consumesAcrossStacks() {
        assertArrayEquals(new int[]{0,0,1},CraftPlan.remaining(new int[]{1,1,1},List.of(i->true,i->true)));
    }
}
