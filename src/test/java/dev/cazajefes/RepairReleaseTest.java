package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
public class RepairReleaseTest {
 @Test public void aliasesHaveOneCanonicalPowerAndDoNotAffectDistinctPowers(){assertEquals("poison",RareAliases.canonical("veneno"));assertEquals("vampiricregeneration",RareAliases.canonical("vampirismo"));assertTrue(RareAliases.duplicate("veneno"));assertFalse(RareAliases.duplicate("poison"));assertEquals("despertar",RareAliases.canonical("despertar"));assertEquals("aura_esporas",RareAliases.canonical("aura_esporas"));}
 @Test public void miniContactDamageProgressesAndHasAnIndependentCadence(){assertEquals(26,MiniAggression.damage(1),0);assertEquals(29,MiniAggression.damage(2),0);assertEquals(32,MiniAggression.damage(3),0);assertEquals(32,MiniAggression.damage(99),0);assertEquals(850,MiniAggression.INTERVAL);}
}
