package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
import org.bukkit.util.Vector;
public class TacticsTest {
 @Test public void rangedRetreatAndMeleeFlankOrRush(){assertEquals(TacticsRules.Move.RETREAT,TacticsRules.move(true,3,1,1));assertEquals(TacticsRules.Move.APPROACH,TacticsRules.move(false,16,3,3));assertEquals(TacticsRules.Move.FLANK,TacticsRules.move(false,5,1,1));assertEquals(TacticsRules.Move.RUSH,TacticsRules.move(false,6,3,3));}
 @Test public void foxRemainsSlowerInEveryPhaseAndManeuver(){for(var move:TacticsRules.Move.values())for(int phase=1;phase<=3;phase++)assertTrue(TacticsRules.speed(true,move,phase)<TacticsRules.speed(false,move,phase));}
 @Test public void swimmingTracksDepthInsteadOfFloatingToSurface(){Vector down=AquaticCombat.motion(new Vector(4,-10,0),false);assertTrue(down.getY()<0);assertTrue(down.getX()>0);assertTrue(down.length()<=.421);assertTrue(Math.abs(down.getY())<=.25);assertEquals(new Vector(),AquaticCombat.motion(new Vector(),false));assertTrue(AquaticCombat.motion(new Vector(2,0,0),true).length()<AquaticCombat.motion(new Vector(2,0,0),false).length());}
 @Test public void everyBossHasAStableDistinctSignature(){Set<Integer> colors=new HashSet<>();for(var boss:BossCatalog.all()){assertTrue("Duplicated signature for "+boss.id(),colors.add(CombatVisuals.signature(boss).asRGB()));assertNotNull(CombatVisuals.motif(boss));}}
 @Test public void coreNamesOnlyDependOnCategory(){for(var tier:BossCatalog.Tier.values()){String name=RelicItems.coreName(tier);assertTrue(name.contains(tier.display));for(var boss:BossCatalog.all())assertFalse(name.contains(boss.name()));}}
}
