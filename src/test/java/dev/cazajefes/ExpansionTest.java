package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
import org.bukkit.entity.EntityType;
public class ExpansionTest {
 @Test public void abyssalExpansionAndRetiredAliases(){assertEquals(28,BossCatalog.all().stream().filter(b->b.tier()==BossCatalog.Tier.ABYSSAL).count());assertEquals(1,BossCatalog.all().stream().filter(b->b.entityType()==EntityType.WARDEN).count());assertSame(BossCatalog.get("nharzul_soberano_del_abismo"),BossCatalog.get("nharzul_soberano_del_abismo"));assertSame(BossCatalog.get("nharzul_soberano_del_abismo"),BossCatalog.get("nharzul_soberano_del_abismo"));}
 @Test public void strengthProgressionIncludesAllCategories(){for(var boss:BossCatalog.all()){int expected=boss.mini()?2:boss.tier().ordinal()+2;assertEquals(expected,BattleRules.strength(boss));assertEquals(expected*3.0,BattleRules.strengthBonus(boss),0);}}
 @Test public void matchingArmorAndWeaponsFourThroughSeven(){for(int i=0;i<4;i++){var tier=EquipmentSets.RANKS.get(i);assertEquals(i+4,EquipmentSets.sharpness(tier));assertEquals(i<2?7:8,EquipmentSets.protection(tier));assertEquals(tier,EquipmentSets.tier("armadura_set_"+tier.name().toLowerCase(Locale.ROOT)+"_0"));assertEquals(tier,EquipmentSets.tier("arma_set_"+tier.name().toLowerCase(Locale.ROOT)+"_1"));assertEquals(tier,EquipmentCommand.parse(new String[]{"epico","legendario","mitico","abismal"}[i]));}assertNull(EquipmentCommand.parse("comun"));}
 @Test public void recipesHaveDistinctPotionThemes(){assertEquals(50,RecipeAlchemy.PROFILES.size());Set<RecipeAlchemy.Profile> unique=new HashSet<>();for(var profile:RecipeAlchemy.PROFILES.values())assertTrue(unique.add(profile));assertEquals(org.bukkit.potion.PotionType.STRONG_HEALING,RecipeAlchemy.profile("hardy").potion());assertEquals(org.bukkit.Material.PHANTOM_MEMBRANE,RecipeAlchemy.profile("durability").reagent());}
 @Test public void minisOnlyDropCommonCrystals(){Random random=new Random(8);for(var boss:BossCatalog.all())for(int i=0;i<100;i++){var crystals=LootRules.roll(boss,random).stream().filter(r->r.kind()==LootRules.Kind.CRYSTAL).toList();assertEquals(boss.mini()?1:0,crystals.size());for(var c:crystals){assertEquals(0,c.variant());assertEquals(1,c.amount());}}}
 @Test public void airSupportAndRepairBounds(){assertTrue(EggPlacement.support(true,false,false));assertFalse(EggPlacement.support(false,true,true));assertEquals(3,ArmorPropertyRules.repair(1));assertEquals(15,ArmorPropertyRules.repair(5));assertEquals(15,ArmorPropertyRules.repair(100));}
}
