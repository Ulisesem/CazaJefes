package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
public class DamageProgressTest {
 @Test public void armoredFinalStageHitsKeepVisibleProgress(){assertEquals(1,DamageProgress.floor(10,.08),0);assertEquals(.3,DamageProgress.floor(2,.02),.0001);assertEquals(5,DamageProgress.floor(10,5),0);}
 @Test public void zeroOrInvalidDamageIsNotInvented(){assertEquals(0,DamageProgress.floor(10,0),0);assertEquals(0,DamageProgress.floor(0,0),0);}
 @Test public void deathFlightRisesFifteenBlocksAndImpacts(){assertEquals(0,DeathFlight.height(0,15),0);assertEquals(7.5,DeathFlight.height(30,15),0);assertEquals(15,DeathFlight.height(60,15),0);assertEquals(11.25,DeathFlight.height(66,15),0);assertEquals(0,DeathFlight.height(72,15),0);assertEquals(0,DeathFlight.height(100,15),0);}
 @Test public void deathBlastRangeAndDamage(){assertTrue(DeathFlight.inRange(36));assertFalse(DeathFlight.inRange(36.01));assertFalse(DeathFlight.inRange(Double.NaN));assertEquals(8,DeathFlight.damage(0),0);assertEquals(9,DeathFlight.damage(1),0);assertEquals(8.5,DeathFlight.damage(.5),0);}
 @Test public void rewardCannotRepeatAfterImpactAndShutdown(){java.util.List<String> order=new java.util.ArrayList<>();DeathCompletion end=new DeathCompletion(()->order.add("loot"));order.add("impact");end.finish();end.finish();assertEquals(java.util.List.of("impact","loot"),order);}
 @Test public void interruptedSequenceReleasesRewardOnce(){int[] calls={0};DeathCompletion end=new DeathCompletion(()->calls[0]++);end.finish();end.finish();assertEquals(1,calls[0]);}
}
