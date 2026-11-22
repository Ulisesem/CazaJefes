package dev.cazajefes;
import org.junit.Test;import static org.junit.Assert.*;
public class HasteCycleTest {
 @Test public void grantsTwentySecondsThenWaitsThirtySeconds(){assertEquals(400,HasteCycle.remaining(0,400,600));assertEquals(1,HasteCycle.remaining(399,400,600));assertEquals(0,HasteCycle.remaining(400,400,600));assertEquals(0,HasteCycle.remaining(999,400,600));assertEquals(400,HasteCycle.remaining(1000,400,600));}
 @Test public void cannotCreateAPermanentBurstWithInvalidTimings(){assertEquals(0,HasteCycle.remaining(20,0,0));assertEquals(0,HasteCycle.remaining(2399+1,Integer.MAX_VALUE,Integer.MAX_VALUE));}
}
