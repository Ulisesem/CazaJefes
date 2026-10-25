package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
public class RebirthAndVampirismTest {
 @Test public void totemOnlyActivatesOnLethalZombieHit(){assertTrue(RebirthRules.mayRebirth(true,false,true,30,30));assertFalse(RebirthRules.mayRebirth(true,false,true,30,29));assertFalse(RebirthRules.mayRebirth(false,false,true,30,40));}
 @Test public void consumedOrMissingTotemCannotLoop(){assertFalse(RebirthRules.mayRebirth(true,true,true,20,100));assertFalse(RebirthRules.mayRebirth(true,false,false,20,100));assertFalse(RebirthRules.mayRebirth(true,false,true,0,100));assertFalse(RebirthRules.mayRebirth(true,false,true,20,Double.NaN));}
 @Test public void restoredHealthExceedsHalfAndDoesNotExceedMaximum(){for(double maximum:new double[]{20,700,5200,14000}){double restored=RebirthRules.restoredHealth(maximum);assertEquals(maximum*.65,restored,.0001);assertTrue(restored>maximum/2);assertTrue(restored<=maximum);}}
 @Test public void rebirthExactlyDoublesDamageWithoutCompounding(){assertEquals(44,RebirthRules.damage(22,true),0);assertEquals(22,RebirthRules.damage(22,false),0);}
 @Test public void vampirismUsesEffectivePlayerDamageAndMissingBossHealth(){assertEquals(5,BossVampirism.healing(10,20,1000,500),0);assertEquals(2,BossVampirism.healing(100,4,1000,500),0);assertEquals(1,BossVampirism.healing(100,20,1000,999),0);assertEquals(3,BossVampirism.healing(100,20,100,20),0);}
 @Test public void fullHealthAndInvalidDamageDoNotHeal(){assertEquals(0,BossVampirism.healing(10,20,100,100),0);assertEquals(0,BossVampirism.healing(-1,20,100,40),0);assertEquals(0,BossVampirism.healing(Double.NaN,20,100,40),0);}
}
