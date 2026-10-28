package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
public class SlimeIntegrityTest {
 @Test public void nativeResizeCannotRefillLowHealthAndRestoresAttributesFirst(){for(double low:new double[]{.5,3,900}){double[] health={low},max={900},armor={12},damage={28};SlimeHealth.resizePreserving(low,()->{health[0]=16;max[0]=16;armor[0]=3;damage[0]=4;},()->{max[0]=900;armor[0]=12;damage[0]=28;},()->max[0],v->health[0]=v);assertEquals(low,health[0],0);assertEquals(900,max[0],0);assertEquals(12,armor[0],0);assertEquals(28,damage[0],0);}}
 @Test public void resizedHealthIsClampedWithoutHealing(){double[] health={0};SlimeHealth.resizePreserving(100,()->{},()->{},()->80,v->health[0]=v);assertEquals(80,health[0],0);}
}
