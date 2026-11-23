package dev.cazajefes.ai;
import java.util.UUID;import org.junit.Test;import static org.junit.Assert.*;
public class MeleeComboTest {
 @Test public void threeHitsBuildDamageAndThenResetWithoutBypassingCadence(){var c=new MeleeCombo();var s=new MeleeSettings(12,1,true,3,10,60,.12);var id=UUID.randomUUID();c.strike(id,0,s);assertEquals(1,c.multiplier(s),0);assertEquals(10,c.interval(s));c.strike(id,10,s);assertEquals(1.12,c.multiplier(s),.00001);c.strike(id,20,s);assertEquals(1.24,c.multiplier(s),.00001);assertEquals(12,c.interval(s));c.strike(id,32,s);assertEquals(1,c.multiplier(s),0);}
 @Test public void newTargetTimeoutAndStunClearTheChain(){var c=new MeleeCombo();var s=new MeleeSettings(12,1,true,3,10,60,.12);var id=UUID.randomUUID();c.strike(id,0,s);c.strike(id,10,s);c.strike(UUID.randomUUID(),20,s);assertEquals(1,c.multiplier(s),0);c.strike(id,100,s);assertEquals(1,c.multiplier(s),0);c.clear();assertEquals(1,c.multiplier(s),0);}
}
