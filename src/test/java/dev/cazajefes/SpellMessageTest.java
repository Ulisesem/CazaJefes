package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
public class SpellMessageTest {
 @Test public void hasteCooldownCannotClaimToBeBurst(){String text=RareAppearance.cooldownMessage("haste",9001);assertTrue(text.contains("Haste"));assertTrue(text.contains("10 segundos"));assertFalse(text.contains("Burst"));assertTrue(RareAppearance.cooldownMessage("burst",1).contains("Burst"));assertTrue(RareAppearance.cooldownMessage("invisibility",1000).contains("Invisibility"));}
 @Test public void allTwelveNewEssencesUseMagmaAppearance(){for(var spec:RangedRareCatalog.ALL)assertTrue(RareAppearance.magmaRare(spec.id()));assertFalse(RareAppearance.magmaRare("haste"));assertFalse(RareAppearance.magmaRare("hardy"));assertFalse(RareAppearance.magmaRare(null));}
}
