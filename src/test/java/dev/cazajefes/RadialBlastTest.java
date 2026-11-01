package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
public class RadialBlastTest {
 @Test public void radialBlastHasThreeLevelsAndStrongerProgression(){assertEquals(3,RangedRareCatalog.get("detonacion_grieta").max());assertEquals(6,RangedProcRules.blastDamage(1),0);assertEquals(9,RangedProcRules.blastDamage(2),0);assertEquals(12,RangedProcRules.blastDamage(3),0);assertEquals(3,RangedProcRules.blastRadius(1),0);assertEquals(5,RangedProcRules.blastRadius(3),0);assertFalse(RecipeAlchemy.usesLegacyProfile("crear_esencia_rare_detonacion_grieta",false));}
 @Test public void cargaIsOneRecipeVisibleInBothWeaponSections(){String key="crear_esencia_rare_carga_inestable";assertTrue(RareCompatibility.recipeInGroup(key,RareCompatibility.Group.BOW));assertTrue(RareCompatibility.recipeInGroup(key,RareCompatibility.Group.CROSSBOW));assertFalse(RareCompatibility.recipeInGroup(key,RareCompatibility.Group.ARMOR));}
}
