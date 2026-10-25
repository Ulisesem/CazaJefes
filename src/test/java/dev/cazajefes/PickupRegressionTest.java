package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
public class PickupRegressionTest {
 @Test public void vanillaAndRenamedItemsWithoutCustomIdAreNotRare(){assertFalse(LootNotices.isRare(null,List.of("espada_brezal","hacha_sismica"),false,false));assertFalse(LootNotices.isRare("",List.of("espada_brezal"),false,false));}
 @Test public void flagsAndKnownRareIdsStillAnnounce(){assertTrue(LootNotices.isRare("espada_brezal",List.of("espada_brezal"),false,false));assertTrue(LootNotices.isRare(null,List.of("espada_brezal"),true,false));assertTrue(LootNotices.isRare(null,List.of("espada_brezal"),false,true));assertFalse(LootNotices.isRare("fragmento_grieta",List.of("espada_brezal"),false,false));}
 @Test public void unrelatedDropsAndCancelledOrRepeatedPickupDoNotAnnounce(){assertFalse(LootNotices.announceRare(false,true,1,0,false));assertFalse(LootNotices.announceRare(true,true,1,1,false));assertFalse(LootNotices.announceRare(true,true,1,0,true));assertTrue(LootNotices.announceRare(true,true,1,0,false));}
}
