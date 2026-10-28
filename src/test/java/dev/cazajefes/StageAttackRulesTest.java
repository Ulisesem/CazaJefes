package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
public class StageAttackRulesTest {
 @Test public void fourSpecialSlotsPerPhaseForEveryBoss(){for(var b:BossCatalog.all())for(int phase=1;phase<=3;phase++){assertEquals(4,StageAttackRules.groundCount(phase)+(phase==3?2:1));Set<String> names=new HashSet<>();for(int slot=0;slot<StageAttackRules.groundCount(phase);slot++)assertTrue(names.add(StageAttackRules.profile(b,phase,slot).name()));}}
 @Test public void progressionRaisesLiftAndShortensGaps(){assertTrue(StageAttackRules.lift(1)<StageAttackRules.lift(2));assertTrue(StageAttackRules.lift(2)<StageAttackRules.lift(3));assertTrue(StageAttackRules.gap(1)>StageAttackRules.gap(3));}
 @Test public void rotationRespectsGlobalGapAndUltimateReadiness(){var r=new StageAttackRules.Rotation();assertEquals(-1,r.select(3,0,true));assertEquals(0,r.select(3,4500,true));assertEquals(-1,r.select(3,5000,true));assertEquals(-1,r.select(3,7000,false));assertEquals(1,r.select(3,7000,true));assertEquals(-1,r.select(3,7100,true));}
 @Test public void deathImpulseIsFiniteAndRadiallyOutward(){var v=VectorBack.motion(3,4);assertEquals(1.35,Math.hypot(v.getX(),v.getZ()),.00001);assertEquals(1.2,v.getY(),0);assertTrue(Double.isFinite(VectorBack.motion(0,0).getX()));}
}
