package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
import org.bukkit.entity.EntityType;
public class FullBestiaryTest {
 @Test public void everySectionFillsTwentyEightSlots(){for(var c:BestiaryCategory.values())assertEquals(c.name(),28,BossCatalog.all().stream().filter(c::matches).count());}
 @Test public void minisAreAnimalEncounters(){Set<EntityType> animals=Set.of(EntityType.BEE,EntityType.FOX,EntityType.HOGLIN,EntityType.SNIFFER,EntityType.POLAR_BEAR,EntityType.WOLF,EntityType.GOAT,EntityType.TURTLE,EntityType.LLAMA,EntityType.COW,EntityType.PIG,EntityType.SHEEP,EntityType.CHICKEN,EntityType.RABBIT,EntityType.HORSE,EntityType.DONKEY,EntityType.MULE,EntityType.CAMEL,EntityType.PANDA,EntityType.AXOLOTL,EntityType.OCELOT,EntityType.CAT,EntityType.PARROT,EntityType.ARMADILLO,EntityType.DOLPHIN,EntityType.SQUID,EntityType.GLOW_SQUID);for(var b:BossCatalog.all())if(b.mini())assertTrue(b.name(),animals.contains(b.entityType()));}
 @Test public void expandingShellKeepsCasterInsideAndReachesFullRadius(){double previous=0;for(int i=0;i<8;i++){double r=BurstVisuals.waveRadius(4,i);assertTrue(r>=previous);assertTrue(r>.8*i/7.0);previous=r;}assertEquals(4,previous,0);}
 @Test public void modernHatchDenialAndLegacyPermissionsAreRespected(){assertTrue(EggActivation.permission(false,false,true));assertFalse(EggActivation.permission(false,true,false));assertFalse(EggActivation.permission(true,false,true));assertTrue(EggActivation.permission(true,true,false));}
 @Test public void compassNeverTracksAwakenedBoss(){var egg=new BossManager.TrackedEncounter(UUID.randomUUID(),"a","a","Raro",false,new org.bukkit.Location(null,0,0,0),true,0,0,"");var boss=new BossManager.TrackedEncounter(UUID.randomUUID(),"b","b","Raro",false,new org.bukkit.Location(null,0,0,0),false,100,100,"hunter");assertEquals(List.of(egg),HuntTracker.eggTargets(List.of(boss,egg)));assertTrue(HuntTracker.eggTargets(List.of(boss)).isEmpty());}
 @Test public void onlyCurrentIdsAreAccepted(){assertNull(BossCatalog.get("coloso"));assertNull(BossCatalog.get("cazador_oxidado"));assertNull(BossCatalog.get("sniffer_tanque"));assertNotNull(BossCatalog.get("ferrun_colmillo_de_hierro"));for(var boss:BossCatalog.all())assertEquals(BossCatalog.canonicalId(boss.name()),boss.id());}
 @Test public void combatProfilesUseOnlyCurrentIds(){assertEquals(new HashSet<>(BossCatalog.ids()),BossAbilityCatalog.ALL.keySet());assertFalse(BossAbilityCatalog.ALL.containsKey("coloso"));assertTrue(BossAbilityCatalog.ALL.containsKey(BossCatalog.get("gorath_quebrantador_de_mundos").id()));}
}
