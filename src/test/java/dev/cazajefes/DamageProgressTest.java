package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
public class DamageProgressTest {
 @Test public void armoredFinalStageHitsKeepVisibleProgress(){assertEquals(1,DamageProgress.floor(10,.08),0);assertEquals(.3,DamageProgress.floor(2,.02),.0001);assertEquals(5,DamageProgress.floor(10,5),0);}
 @Test public void zeroOrInvalidDamageIsNotInvented(){assertEquals(0,DamageProgress.floor(10,0),0);assertEquals(0,DamageProgress.floor(0,0),0);assertEquals(0,DamageProgress.allowance(Double.NaN),0);}
 @Test public void vampireBudgetCannotEraseAllPlayerDamage(){double taken=100,heal=DamageProgress.allowance(taken);assertEquals(35,heal,0);assertTrue(taken-heal>0);assertEquals(0,DamageProgress.allowance(-2),0);}
 @Test public void deathFlightRisesAndReturnsWithoutKeepingBossAlive(){assertEquals(0,DeathFlight.height(0,4),0);assertEquals(2,DeathFlight.height(16,4),0);assertEquals(4,DeathFlight.height(32,4),0);assertEquals(2,DeathFlight.height(40,4),0);assertEquals(0,DeathFlight.height(48,4),0);assertEquals(0,DeathFlight.height(100,4),0);}
}
