package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
public class TenRareCollectionTest {
 @Test public void tenRecipesPerWeaponWithNoDuplicateIds(){for(var group:List.of(RareCompatibility.Group.BOW,RareCompatibility.Group.CROSSBOW)){var collection=RareDefinitions.ALL.values().stream().filter(d->RareCompatibility.recipeInGroup("crear_esencia_rare_"+d.id(),group)).toList();assertEquals(10,collection.size());assertEquals(10,collection.stream().map(d->d.id()).distinct().count());}}
 @Test public void tenSlotsAreTwoCenteredRowsAndRootButtonsStayDistinct(){assertArrayEquals(new int[]{20,21,22,23,24,29,30,31,32,33},RareMenuStyle.slots(10));Set<Integer> slots=new HashSet<>();for(int slot:RecipePresentation.CATEGORIES)assertTrue(slots.add(slot));assertEquals(9,slots.size());for(int reserved:new int[]{4,39,41,48,49,50})assertFalse(slots.contains(reserved));}
 @Test public void newRaresHaveCompleteRecipesProcRulesAndBoundedHealingReduction(){for(String id:List.of("fulgor_alba","ceniza_solar","sello_ruina","impacto_sismico")){var d=RareDefinitions.get(id);assertNotNull(d);assertEquals(3,d.maximum());assertTrue(RangedProcRules.direct(id));assertEquals(.5,RangedProcRules.chance(id,3),.00001);assertEquals(0,RangedProcRules.extraDamage(id,3),0);assertEquals(9,d.ingredients().size());assertFalse(RecipeAlchemy.usesLegacyProfile("crear_esencia_rare_"+id,false));}assertEquals(.8,RangedProcRules.healingFactor(1),.00001);assertEquals(.5,RangedProcRules.healingFactor(3),.00001);assertEquals(.5,RangedProcRules.healingFactor(99),.00001);}
}
