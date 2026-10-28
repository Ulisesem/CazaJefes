package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
public class ArmorAndCadenceTest {
 @Test public void heartBonusAndCountersHaveHardCaps(){assertEquals(0,ArmorRareRules.hearts(-1),0);assertEquals(2,ArmorRareRules.hearts(1),0);assertEquals(6,ArmorRareRules.hearts(99),0);assertEquals(.5,ArmorRareRules.counter(99),.00001);assertEquals(0,ArmorRareRules.counter(0),0);}
 @Test public void sensoryEffectsHaveTenSecondDurationAndTwentyFiveSecondGate(){assertEquals(200,BossEffects.duration("blindness",600));assertEquals(200,BossEffects.duration("nausea",600));assertFalse(BossEffects.ready(24999,25000));assertTrue(BossEffects.ready(25000,25000));}
 @Test public void bodyRingsFitAllHeightsWithBoundedPoints(){var points=BurstVisuals.bodyRings(2,3,0);assertEquals(32,points.size());assertEquals(-3,points.stream().mapToDouble(BurstVisuals.Point::y).min().orElseThrow(),0);assertEquals(3,points.stream().mapToDouble(BurstVisuals.Point::y).max().orElseThrow(),0);for(var p:points)assertEquals(2,Math.hypot(p.x(),p.z()),.00001);}
 @Test public void newEssencesAreArmorOnlyAndRecipesAreDistinct(){for(String id:new String[]{"spore","strength","turtle","garlic","hardy"}){assertEquals(RareCompatibility.Group.ARMOR,RareCompatibility.property(id));assertFalse(RareCompatibility.accepts(id,org.bukkit.Material.DIAMOND_SWORD));assertNotNull(RecipeAlchemy.profile(id));assertNotNull(ArmorRareRules.description(id));}assertEquals(3,ArmorRareRules.maximum("hardy"));assertEquals(5,ArmorRareRules.maximum("turtle"));}
}
