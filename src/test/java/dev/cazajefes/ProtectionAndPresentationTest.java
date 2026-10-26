package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
public class ProtectionAndPresentationTest {
 @Test public void everyCatalogBossHasExpectedProtection(){for(var boss:BossCatalog.all()){assertEquals(boss.mini()?5:(boss.tier().ordinal()+2)*5,BossProtection.level(boss));assertTrue(BossProtection.incoming(BossProtection.level(boss))>0);}}
 @Test public void protectionNeverReversesOrNullifiesDamage(){double last=1;for(int level=5;level<=35;level+=5){double value=BossProtection.incoming(level);assertTrue(value<last);assertTrue(value>=.65-1e-12);last=value;}assertEquals(.65,BossProtection.incoming(1000),.00001);}
 @Test public void essencesRecognizedWithoutMarkingOtherLoot(){assertTrue(LootPresentation.essence("rare_essence"));assertTrue(LootPresentation.essence("rare_essence_primordial"));assertTrue(LootPresentation.essence("rare_essence_abyssal"));assertFalse(LootPresentation.essence(null));assertFalse(LootPresentation.essence("fragmento_grieta"));}
 @Test public void unitSplitCreatesIndependentUnitsAndPreservesCount(){int[] copies={0};var units=LootPresentation.split(5,()->{copies[0]++;return new int[]{1};});assertEquals(5,units.size());assertEquals(5,copies[0]);assertTrue(units.stream().allMatch(v->v[0]==1));units.get(0)[0]=2;assertEquals(1,units.get(1)[0]);assertTrue(LootPresentation.split(0,()->1).isEmpty());}
}
