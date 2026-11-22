package dev.cazajefes.ai;
import org.junit.Test;import static org.junit.Assert.*;
public class WaterSurfaceTest {
 @Test public void supportsFeetOnSurfaceAndRisesGentlyFromBelow(){assertEquals(0,WaterSurface.rise(64,64),0);assertEquals(.3,WaterSurface.rise(62,64),0);assertEquals(.1,WaterSurface.rise(63.9,64),.00001);assertEquals(-.12,WaterSurface.rise(66,64),0);}
}
