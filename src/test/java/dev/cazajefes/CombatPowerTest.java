package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
public class CombatPowerTest {
 @Test public void specialsExceedNormalReferenceAcrossCategories(){for(int tier=0;tier<6;tier++)for(int phase=1;phase<=3;phase++){double normal=18+tier*10;assertTrue(CombatPower.special(normal,tier,phase,false)>normal);assertTrue(CombatPower.special(normal,tier,phase,true)>=CombatPower.special(normal,tier,phase,false));}assertEquals(10,CombatPower.BURST_LEVEL);assertTrue(CombatPower.lift(1)>2);}
 @Test public void minionsRequireMultipleStrongHits(){for(int tier=0;tier<6;tier++){double hp=CombatPower.minionHealth(tier);assertTrue(hp>=120);assertTrue(CombatPower.minionHitLimit(hp)<hp/2);}}
 @Test public void newAnimalFamiliesAreSupported(){assertTrue(AnimalMinions.animal(org.bukkit.entity.EntityType.PANDA));assertTrue(AnimalMinions.animal(org.bukkit.entity.EntityType.SQUID));assertFalse(AnimalMinions.animal(org.bukkit.entity.EntityType.ZOMBIE));}
}
