package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
import org.bukkit.entity.EntityType;
public class InteractionRegressionTest {
 @Test public void eggsRequireFreeSpaceAndSolidFloor(){
  assertTrue(EggPlacement.allowed(true,true,true,false,false));
  assertFalse(EggPlacement.allowed(false,true,true,false,false));assertFalse(EggPlacement.allowed(true,false,true,false,false));assertFalse(EggPlacement.allowed(true,true,false,false,false));
 }
 @Test public void pedestalCannotReplaceContainersOrRegisteredEggs(){
  assertFalse(EggPlacement.allowed(true,true,true,true,false));assertFalse(EggPlacement.allowed(true,true,true,false,true));
 }
 @Test public void heartIsRareAndNeverDropsFromMinis(){
  assertTrue(LootRules.heartEligible(false,0.0099,0.01));assertFalse(LootRules.heartEligible(false,0.01,0.01));
  assertFalse(LootRules.heartEligible(true,0,1));assertFalse(LootRules.heartEligible(false,0,-1));
 }
 @Test public void normalPickupsDoNotAnnounce(){assertFalse(LootNotices.announceRare(true,false,4,0,false));}
 @Test public void rarePickupNeedsActualAcquisitionFromBoss(){
  assertTrue(LootNotices.announceRare(true,true,1,0,false));assertTrue(LootNotices.announceRare(true,true,3,2,false));
  assertFalse(LootNotices.announceRare(true,true,1,1,false));assertFalse(LootNotices.announceRare(false,true,1,0,false));
 }
 @Test public void repeatedRarePickupDoesNotAnnounceAgain(){assertFalse(LootNotices.announceRare(true,true,1,0,true));}
 @Test public void passiveBossesAndGiantsGetCombatAssistance(){
  assertTrue(PassiveCombat.assist(EntityType.TURTLE,false));assertTrue(PassiveCombat.assist(EntityType.MOOSHROOM,false));assertTrue(PassiveCombat.assist(EntityType.GIANT,true));assertFalse(PassiveCombat.assist(EntityType.ZOMBIE,true));
 }
 @Test public void forgeKeepsCurrenciesAndCrystalsSeparate(){
  assertEquals(9,RaresMenu.Category.values().length);assertEquals(RaresMenu.Category.CURRENCIES,RaresMenu.Category.category("tributo_cazador"));assertEquals(RaresMenu.Category.CRYSTALS,RaresMenu.Category.category("cristal_common"));
 }
}
