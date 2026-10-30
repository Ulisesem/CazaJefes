package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
import org.bukkit.Material;
public class BookAndCategoryTest {
 @Test public void commandedBooksAreNeverConvertedToMagmaOnRefresh(){for(var spec:RangedRareCatalog.ALL){assertFalse(RareAppearance.magmaEssence(spec.id(),true));assertTrue(RareAppearance.magmaEssence(spec.id(),false));}assertFalse(RareAppearance.magmaEssence("hardy",true));}
 @Test public void rangedSharedSectionCannotLookLikeAnotherBowCategory(){assertEquals(Material.BOW,RareCompatibility.Group.BOW.icon);assertEquals(Material.SPECTRAL_ARROW,RareCompatibility.Group.RANGED.icon);assertFalse(RareCompatibility.Group.RANGED.title.equals(RareCompatibility.Group.BOW.title));assertEquals(RareCompatibility.Group.RANGED,RareCompatibility.property("calllighting"));assertTrue(RareCompatibility.accepts("calllighting",Material.CROSSBOW));}
}
