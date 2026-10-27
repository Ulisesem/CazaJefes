package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
import org.bukkit.util.Vector;
public class PillagerRulesTest {
 @Test public void enchantedCrossbowHasExactRequestedLevels(){assertEquals(Integer.valueOf(10),PillagerRules.ENCHANTMENTS.get("quick_charge"));assertEquals(Integer.valueOf(15),PillagerRules.ENCHANTMENTS.get("multishot"));assertEquals(Integer.valueOf(10),PillagerRules.ENCHANTMENTS.get("unbreaking"));assertEquals(Integer.valueOf(10),PillagerRules.ENCHANTMENTS.get("knockback"));assertEquals(Integer.valueOf(1),PillagerRules.ENCHANTMENTS.get("infinity"));assertEquals(Integer.valueOf(1),PillagerRules.ENCHANTMENTS.get("mending"));assertEquals(Integer.valueOf(10),PillagerRules.ENCHANTMENTS.get("piercing"));}
 @Test public void fifteenArrowFanHasDistinctNormalizedDirections(){var forward=new Vector(0,.2,1);var original=forward.clone();var fan=PillagerRules.fan(forward);assertEquals(15,fan.size());assertEquals(original,forward);Set<String> unique=new HashSet<>();for(var v:fan){assertEquals(1,v.length(),.00001);assertTrue(unique.add(v.toString()));}assertTrue(fan.get(7).dot(forward.clone().normalize())>.9999);}
 @Test public void retaliationRequiresFiveRecentHitsAndConsumesCharge(){var hits=new PillagerRules.Hits(5);for(int i=0;i<4;i++){hits.add(i*100);assertFalse(hits.charged(i*100));}hits.add(400);assertTrue(hits.charged(400));hits.consume();assertFalse(hits.charged(500));for(int i=0;i<5;i++)hits.add(1000+i);assertFalse(hits.charged(10000));}
 @Test public void diagonalBurstKeepsVerticalMechanism(){assertEquals(1.25,CombatPower.ASCENT_LATERAL,0);assertTrue(CombatPower.ASCENT_LATERAL>.35);assertTrue(BurstLaunch.sustainedLift(StageAttackRules.lift(1)));assertEquals(2.65,StageAttackRules.lift(3),.00001);}
}
