package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
public class LootSpiralTest {
 @Test public void eightSecondDescentLandsAtDropOrigin(){assertEquals(160,LootSpiral.DURATION);assertEquals(16,LootSpiral.at(0,0).y(),0);var end=LootSpiral.at(160,1);assertEquals(0,end.x(),0);assertEquals(.3,end.y(),.00001);assertEquals(0,end.z(),0);}
 @Test public void spiralDescendsContinuouslyAndContracts(){double last=17;for(int tick=0;tick<=160;tick++){var p=LootSpiral.at(tick,.4);assertTrue(p.y()<=last);last=p.y();assertEquals(2.2*(1-tick/160.0),Math.hypot(p.x(),p.z()),.00001);}assertEquals(LootSpiral.at(160,0),LootSpiral.at(200,0));}
}
