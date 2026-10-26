package dev.cazajefes;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;
public class PhaseImpulseTest {
 @Test public void stageTwoPushHasTenSecondCooldown(){var c=new PhaseImpulse();var id=UUID.randomUUID();assertFalse(c.ready(id,2,0));assertFalse(c.ready(id,2,9999));assertTrue(c.ready(id,2,10000));assertFalse(c.ready(id,2,10000));assertTrue(c.ready(id,2,20000));}
 @Test public void stageThreeStartsIndependentTwentySecondCooldown(){var c=new PhaseImpulse();var id=UUID.randomUUID();assertFalse(c.ready(id,2,0));assertFalse(c.ready(id,3,1000));assertFalse(c.ready(id,3,20999));assertTrue(c.ready(id,3,21000));assertFalse(c.ready(id,3,21001));}
 @Test public void noBurstAfterDelayAndBossesHaveIndependentTimers(){var c=new PhaseImpulse();var a=UUID.randomUUID();var b=UUID.randomUUID();c.ready(a,3,0);c.ready(b,3,10000);assertTrue(c.ready(a,3,25000));assertFalse(c.ready(a,3,25001));assertFalse(c.ready(b,3,25000));assertTrue(c.ready(b,3,30000));c.remove(a);assertFalse(c.ready(a,3,50000));}
 @Test public void finiteBoundedMotionPointsAwayFromBoss(){var push=PhaseImpulse.motion(2,3,4);assertEquals(2.6,Math.hypot(push.x(),push.z()),.00001);assertEquals(.45,push.y(),0);assertTrue(push.x()>0&&push.z()>0);var lift=PhaseImpulse.motion(3,0,0);assertEquals(1.35,lift.y(),0);assertTrue(Double.isFinite(lift.x())&&Double.isFinite(lift.z()));assertEquals(0,PhaseImpulse.cooldown(1));}
}
