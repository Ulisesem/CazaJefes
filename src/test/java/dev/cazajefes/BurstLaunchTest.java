package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
public class BurstLaunchTest {
 @Test public void overwrittenVerticalMotionCanRetryButCancelledMotionCannot(){assertTrue(BurstLaunch.retry(2.5,.4,false));assertFalse(BurstLaunch.retry(2.5,.4,true));assertFalse(BurstLaunch.retry(2.5,2.2,false));assertFalse(BurstLaunch.retry(.5,.2,false));assertFalse(BurstLaunch.retry(2.5,Double.NaN,false));}
 @Test public void animalScaleIncreasesOnceAndStaysBounded(){double initial=1.6;double larger=AnimalBossSizing.scaled(initial,false);assertEquals(2,larger,.00001);assertEquals(larger,AnimalBossSizing.scaled(larger,true),0);assertEquals(10,AnimalBossSizing.scaled(10,false),0);}
}
