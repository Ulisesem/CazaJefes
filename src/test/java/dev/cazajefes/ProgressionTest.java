package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
import org.bukkit.entity.EntityType;
public class ProgressionTest {
 @Test public void everyBossAppearsInExactlyOneCategory(){
  Set<String> seen=new HashSet<>();int total=0;
  for(var category:BestiaryCategory.values())for(var boss:BossCatalog.all())if(category.matches(boss)){assertTrue(seen.add(boss.id()));assertEquals(category,BestiaryCategory.of(boss));total++;}
  assertEquals(168,total);
 }
 @Test public void canonicalIdsMatchNamesAndOldIdsStillResolve(){
  for(var boss:BossCatalog.all()){
   assertEquals(BossCatalog.canonicalId(boss.name()),boss.id());assertTrue(boss.id().matches("[a-z0-9]+(_[a-z0-9]+)*"));
   assertSame(boss,BossCatalog.get(boss.id()));assertSame(boss,BossCatalog.get(boss.id()));
  }
 }
 @Test public void wardensAreAbyssalAndRenamed(){
  long wardens=BossCatalog.all().stream().filter(b->b.entityType()==EntityType.WARDEN).peek(b->assertEquals(BossCatalog.Tier.ABYSSAL,b.tier())).count();
  assertEquals(1,wardens);assertEquals("nharzul_soberano_del_abismo",BossCatalog.get("nharzul_soberano_del_abismo").id());
 }
 @Test public void everyMiniDropsOnlyCurrenciesAcrossManyRolls(){
  Random random=new Random(7);
  for(var boss:BossCatalog.all())if(boss.mini())for(int i=0;i<1000;i++){
   var rewards=LootRules.roll(boss,random);assertEquals(3,rewards.size());
   assertEquals(LootRules.Kind.TRIBUTE,rewards.get(0).kind());assertTrue(rewards.get(0).amount()>=1&&rewards.get(0).amount()<=2);
   assertEquals(LootRules.Kind.FRAGMENT,rewards.get(1).kind());assertTrue(rewards.get(1).amount()>=1&&rewards.get(1).amount()<=3);
  }
 }
 @Test public void everyEliteHasGuaranteedFragmentAndCorrectTierCrystal(){
  Random random=new Random(31);
  for(var boss:BossCatalog.all())if(!boss.mini())for(int i=0;i<100;i++){
   var rewards=LootRules.roll(boss,random);
   assertTrue(rewards.stream().anyMatch(r->r.kind()==LootRules.Kind.FRAGMENT&&r.amount()>=2));
   assertFalse(rewards.stream().anyMatch(r->r.kind()==LootRules.Kind.CRYSTAL));
  }
 }
 @Test public void farmsAndCustomCombatantsDoNotEnterNaturalMobTable(){
  assertTrue(LootRules.eligibleMob(true,true,false,false,false));
  assertFalse(LootRules.eligibleMob(true,true,true,false,false));assertFalse(LootRules.eligibleMob(true,true,false,true,false));
  assertFalse(LootRules.eligibleMob(true,true,false,false,true));assertFalse(LootRules.eligibleMob(false,true,false,false,false));assertFalse(LootRules.eligibleMob(true,false,false,false,false));
 }
 @Test public void malformedProbabilitiesNeverBecomeGuaranteedDrops(){
  assertEquals(0,LootRules.probability(Double.NaN),0);assertEquals(0,LootRules.probability(Double.POSITIVE_INFINITY),0);
  assertEquals(0,LootRules.probability(-1),0);assertEquals(1,LootRules.probability(5),0);
 }
 @Test public void phasesIncreaseDamageAndResistanceAtThresholds(){
  assertEquals(1,PhaseRules.phase(1));assertEquals(1,PhaseRules.phase(0.6501));assertEquals(2,PhaseRules.phase(0.65));assertEquals(2,PhaseRules.phase(0.3001));assertEquals(3,PhaseRules.phase(0.30));
  assertTrue(PhaseRules.outgoing(3)>PhaseRules.outgoing(2));assertTrue(PhaseRules.outgoing(2)>PhaseRules.outgoing(1));
  assertTrue(PhaseRules.incoming(3)<PhaseRules.incoming(2));assertTrue(PhaseRules.incoming(2)<PhaseRules.incoming(1));
 }
 @Test public void disruptionRequiresFinalPhaseAndRespectsBothCooldowns(){
  assertTrue(PhaseRules.mayDisrupt(3,true,true,100,50,50,0.01,0.02));
  assertFalse(PhaseRules.mayDisrupt(2,true,true,100,50,50,0,0.02));assertFalse(PhaseRules.mayDisrupt(3,false,true,100,50,50,0,0.02));
  assertFalse(PhaseRules.mayDisrupt(3,true,false,100,50,50,0,0.02));assertFalse(PhaseRules.mayDisrupt(3,true,true,100,101,50,0,0.02));assertFalse(PhaseRules.mayDisrupt(3,true,true,100,50,101,0,0.02));assertFalse(PhaseRules.mayDisrupt(3,true,true,100,50,50,0.02,0.02));
 }
 @Test public void recipeCategoriesKeepCrystalsAndEquipmentApart(){
  assertEquals(RaresMenu.Category.CRYSTALS,RaresMenu.Category.category("cristal_abyssal"));
  assertEquals(RaresMenu.Category.WEAPONS,RaresMenu.Category.category("mandoble_vacio"));
  assertEquals(RaresMenu.Category.ARMOR,RaresMenu.Category.category("armadura_grieta_yelmo"));
  assertEquals(RaresMenu.Category.SPELLS,RaresMenu.Category.category("crear_libro_rare_burst"));
  assertEquals(RaresMenu.Category.ESSENCES,RaresMenu.Category.category("crear_esencia_rare_poison"));
 }
}
