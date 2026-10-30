package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
public class RecipeStartupTest {
 @Test public void allFortyFiveDefinedRecipesBypassLegacyAlchemy(){for(var d:RareDefinitions.ALL.values())for(String prefix:new String[]{"crear_esencia_rare_","crear_libro_rare_"}){String key=prefix+d.id();assertEquals(d.id(),RecipeAlchemy.propertyForRecipe(key));assertFalse(key,RecipeAlchemy.usesLegacyProfile(key,false));}}
 @Test public void allTwelveNewRaresReachTheirExactIngredientDefinitions(){for(var spec:RangedRareCatalog.ALL){String key="crear_esencia_rare_"+spec.id();assertFalse(RecipeAlchemy.usesLegacyProfile(key,false));assertSame(RareDefinitions.get(spec.id()),RareDefinitions.get(RecipeAlchemy.propertyForRecipe(key)));assertEquals(9,RareDefinitions.get(spec.id()).ingredients().size());}}
 @Test public void legacyRecipesStillHaveTheirAlchemyProfile(){for(String id:new String[]{"strength","turtle","garlic","aura_esporas","magneto","saturacion","flame","guardian","brasas","paso_viento"}){String key="crear_esencia_rare_"+id;assertTrue(key,RecipeAlchemy.usesLegacyProfile(key,false));assertNotNull(RecipeAlchemy.profile(id));}assertTrue(RecipeAlchemy.usesLegacyProfile("hacha_sismica",true));assertNotNull(RecipeAlchemy.profile("hacha_sismica"));assertFalse(RecipeAlchemy.usesLegacyProfile("ranged_material_engranaje_umbrio",false));}
}
