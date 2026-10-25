package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
import java.util.function.IntPredicate;
public class StormAndMarketTest {
 @Test public void exactPotionLevelsAndMeleeCadence(){assertEquals(4,BattleRules.STRENGTH_AMPLIFIER);assertEquals(5,BattleRules.HASTE_AMPLIFIER);assertEquals(15,BattleRules.STRENGTH_BONUS,0);assertEquals(500,BattleRules.MELEE_COOLDOWN_MS);}
 @Test public void chargeUsesAcceptedDamageAndIgnoresInvalidValues(){assertEquals(40,BattleRules.charge(30,10),0);assertEquals(30,BattleRules.charge(30,-20),0);assertEquals(30,BattleRules.charge(30,Double.NaN),0);assertEquals(2000,BattleRules.charge(1900,10000),0);}
 @Test public void onlyFinalPhaseAndSufficientChargeCanRetaliate(){assertTrue(BattleRules.retaliates(3,true,40,40,100,50));assertFalse(BattleRules.retaliates(2,true,50,40,100,50));assertFalse(BattleRules.retaliates(3,false,50,40,100,50));assertFalse(BattleRules.retaliates(3,true,39.9,40,100,50));assertFalse(BattleRules.retaliates(3,true,50,40,100,101));}
 @Test public void lightningScalesAndRespectsDamageCap(){assertEquals(24,BattleRules.lightningDamage(40,30),0);assertEquals(30,BattleRules.lightningDamage(100,30),0);assertTrue(BattleRules.lightningDamage(50,30)>BattleRules.lightningDamage(40,30));assertEquals(40,BattleRules.threshold(Double.NaN),0);}
 @Test public void exchangeCannotBeConfiguredWithFreeSigils(){assertEquals(64,MarketRules.sigilFragments(64));assertEquals(1,MarketRules.sigilFragments(0));assertEquals(4096,MarketRules.sigilFragments(Integer.MAX_VALUE));assertEquals(1,MarketRules.amuletSigils(-4));assertEquals(0,MarketRules.amuletTributes(-10));}
 @Test public void purchaseConsumesSplitStacksWithoutTouchingOthers(){
  int[] source={40,30,8,3};List<IntPredicate> costs=new ArrayList<>();for(int i=0;i<64;i++)costs.add(slot->slot<2);for(int i=0;i<8;i++)costs.add(slot->slot==2);
  assertArrayEquals(new int[]{0,6,0,3},CraftPlan.remaining(source,costs));assertArrayEquals(new int[]{40,30,8,3},source);
 }
 @Test public void failedSigilPurchaseKeepsAllCurrencies(){int[] source={3,8};List<IntPredicate> costs=List.of(i->i==0,i->i==0,i->i==0,i->i==0);assertNull(CraftPlan.remaining(source,costs));assertArrayEquals(new int[]{3,8},source);}
 @Test public void marketHasSixDistinctDepartments(){assertEquals(6,MarketMenu.Section.values().length);assertEquals(6,Arrays.stream(MarketMenu.Section.values()).map(s->s.title).distinct().count());}
}
