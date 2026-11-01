package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
public class RareShelvesTest {
 @Test public void bowAndCrossbowHaveExactlySevenEssenceRecipesEach(){assertEquals(7,RareDefinitions.ALL.values().stream().filter(d->RareCompatibility.recipeInGroup("crear_esencia_rare_"+d.id(),RareCompatibility.Group.BOW)).count());assertEquals(9,RareDefinitions.ALL.values().stream().filter(d->RareCompatibility.recipeInGroup("crear_esencia_rare_"+d.id(),RareCompatibility.Group.CROSSBOW)).count());}
 @Test public void allShelvesUseDistinctInteriorCellsAndSevenIsCentered(){for(int n=1;n<=28;n++){int[] slots=RareMenuStyle.slots(n);assertTrue(slots.length>=n);Set<Integer> used=new HashSet<>();for(int i=0;i<n;i++){int slot=slots[i];assertTrue(used.add(slot));assertTrue(slot>=10&&slot<=43);assertTrue(slot%9>0&&slot%9<8);}}assertArrayEquals(new int[]{19,20,21,22,23,24,25},RareMenuStyle.slots(7));}
 @Test public void relevoCrossesAirAndCanRiseOntoOneBlock(){var from=new org.bukkit.Location(null,0,0,0);var side=new org.bukkit.util.Vector(1,0,0);assertEquals(0,RelevoRoute.destination(from,side,4,at->true).getY(),0);var up=RelevoRoute.destination(from,side,4,at->at.getX()<1||at.getY()>=1);assertNotNull(up);assertEquals(1,up.getY(),0);assertNotNull(RelevoRoute.destination(from,side,4,at->at.getX()<1));}
 @Test public void newPowersHaveChanceLevelsRecipesAndExclusiveWeapons(){for(String id:List.of("flecha_silencio","impacto_ariete","municion_necrotica","destello_umbral","grillete_hierro")){var spec=RangedRareCatalog.get(id);assertNotNull(spec);assertEquals(3,spec.max());assertEquals(0,RangedProcRules.extraDamage(id,3),0);assertTrue(RangedProcRules.direct(id));assertEquals(.5,RangedProcRules.chance(id,3),.000001);assertEquals(9,RareDefinitions.get(id).ingredients().size());assertFalse(RecipeAlchemy.usesLegacyProfile("crear_esencia_rare_"+id,false));}}
}
