package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
public class AuraPerformanceTest {
 @Test public void profilesAndFit(){assertEquals(AuraQuality.LOW,AuraQuality.parse("baja"));assertEquals(AuraQuality.OFF,AuraQuality.parse("off"));assertNull(AuraQuality.parse("invalid"));assertEquals(AuraQuality.LOW,AuraQuality.fit(AuraQuality.HIGH,13));assertEquals(AuraQuality.OFF,AuraQuality.fit(AuraQuality.MEDIUM,12));assertEquals(AuraQuality.LOW,AuraQuality.fit(AuraQuality.LOW,1000));assertTrue(AuraQuality.MEDIUM.sends()*2<296*.2);}
 @Test public void budgetsRefillWithoutExceedingCapacity(){var b=new AuraBudget();var id=UUID.randomUUID();assertTrue(b.spend(id,100,1000,100));assertFalse(b.spend(id,1,1000,100));assertEquals(50,b.available(id,1500,100));assertEquals(100,b.available(id,9000,100));assertTrue(b.spend(id,100,9000,100));assertEquals(0,b.available(id,8000,100));assertEquals(0,b.available(id,9000,100));assertEquals(100,b.available(UUID.randomUUID(),9000,100));b.remove(id);assertEquals(100,b.available(id,9000,100));}
 @Test public void nearestAndConfigurationLimits(){assertEquals(List.of(1,2),AuraPolicy.nearest(List.of(8,2,1,90),v->v*v,2,1024));assertTrue(AuraPolicy.nearest(List.of(1),v->Double.POSITIVE_INFINITY,2,1024).isEmpty());assertEquals(40,AuraPolicy.interval(Integer.MAX_VALUE));assertEquals(5,AuraPolicy.interval(Integer.MIN_VALUE));assertEquals(48,AuraPolicy.radius(100));assertEquals(1,AuraPolicy.bosses(0));}
 @Test public void sampledAurasRetainCrownAndBody(){for(int n:new int[]{12,24,36}){var points=AuraShape.sampled(4,8,.2,n);assertEquals(n,points.size());assertTrue(points.stream().anyMatch(v->v.y()>8));assertTrue(points.stream().allMatch(v->Double.isFinite(v.x())&&Double.isFinite(v.y())&&Double.isFinite(v.z())));}}
}
