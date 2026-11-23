package dev.cazajefes;
import java.util.*;import org.junit.Test;import static org.junit.Assert.*;
public class ForgeLayoutTest {
 @Test public void backButtonIsAtTheBottomCenter(){assertEquals(49,RecipePresentation.LIST_BACK);}
 @Test public void booksAlternateRowsWithoutCollisions(){for(int n=1;n<=14;n++){Set<Integer> used=new HashSet<>();var slots=RareMenuStyle.bookSlots(n);assertEquals(n,slots.length);for(int slot:slots){assertTrue(used.add(slot));assertTrue(slot>=10&&slot<=43&&slot%9>0&&slot%9<8);}if(n>=2)assertEquals(1,slots[1]/9-slots[0]/9);}}
 @Test public void materialsHaveCenteredRowsForEveryPageSize(){for(int n=1;n<=20;n++){Set<Integer> used=new HashSet<>();var slots=RareMenuStyle.materialSlots(n);assertEquals(n,slots.length);for(int slot:slots){assertTrue(used.add(slot));assertTrue(slot>=10&&slot<=43);}}assertArrayEquals(new int[]{11,12,14,15,20,21,22,23,24,29,30,32,33},RareMenuStyle.materialSlots(13));}
 @Test public void chitinHasAnUnambiguousShellIcon(){assertEquals(org.bukkit.Material.TURTLE_SCUTE,DuelCatalog.get("quitina").icon());}
}
