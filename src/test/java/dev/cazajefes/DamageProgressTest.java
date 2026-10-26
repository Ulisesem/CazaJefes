package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
public class DamageProgressTest {
 @Test public void armoredFinalStageHitsKeepVisibleProgress(){assertEquals(1,DamageProgress.floor(10,.08),0);assertEquals(.3,DamageProgress.floor(2,.02),.0001);assertEquals(5,DamageProgress.floor(10,5),0);}
 @Test public void zeroOrInvalidDamageIsNotInvented(){assertEquals(0,DamageProgress.floor(10,0),0);assertEquals(0,DamageProgress.floor(0,0),0);}
 @Test public void deathFlightRisesFifteenBlocksAndImpacts(){assertEquals(0,DeathFlight.height(0,15),0);assertEquals(7.5,DeathFlight.height(30,15),0);assertEquals(15,DeathFlight.height(60,15),0);assertEquals(7.5,DeathFlight.height(70,15),0);assertEquals(0,DeathFlight.height(80,15),0);assertEquals(0,DeathFlight.height(100,15),0);}
 @Test public void deathBlastRangeAndDamage(){assertTrue(DeathFlight.inRange(36));assertFalse(DeathFlight.inRange(36.01));assertFalse(DeathFlight.inRange(Double.NaN));assertEquals(6,DeathFlight.damage(0),0);assertEquals(7,DeathFlight.damage(1),0);assertEquals(6.5,DeathFlight.damage(.5),0);}
}
