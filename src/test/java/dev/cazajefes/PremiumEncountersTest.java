package dev.cazajefes;

import org.junit.Test;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class PremiumEncountersTest {
   @Test public void allOriginalsAreEditableJsonWithPortraitsAndExistingIds() throws Exception {
      {
         var originals = JsonBosses.builtIns();
         assertEquals(168, originals.size());
         for (var boss : originals.values()) {
            var profile = boss.custom().ai();
            assertEquals(60, profile.get("detectionRange").getAsInt());
            var ranges = new java.util.HashSet<String>();
            for (var attack : profile.getAsJsonArray("attacks"))
               ranges.add(attack.getAsJsonObject().get("category").getAsString());
            assertEquals(java.util.Set.of("SHORT_RANGE", "MID_RANGE", "LONG_RANGE"), ranges);
         }
         assertEquals(BossCatalog.ids(), java.util.List.copyOf(originals.keySet()));
         assertTrue(originals.values().stream().allMatch(boss ->
            boss.custom().portrait().matches("[a-f0-9]{60,64}") && boss.custom().ultimate() != null));
         assertEquals(14000, originals.get("nharzul_soberano_del_abismo").custom().health(), 0);
         assertEquals(520, originals.get("melisara_emperatriz_del_enjambre").custom().health(), 0);
      }
   }

   @Test public void catalogEditsReachCombatProfiles() throws Exception {
      String json = "[" + Files.readString(Path.of("src/main/resources/bosses/abyssal/brumgor_colmillo_del_eclipse.json")) + "]";
      var edited = JsonBosses.readCatalog(new StringReader(json.replace("Colmillo del Eclipse Sangriento", "Eco de prueba")));
      var boss = edited.get("brumgor_colmillo_del_eclipse");
      try {
         BossCatalog.installCustom(edited);
         assertEquals("Eco de prueba", StageAttackRules.profile(BossCatalog.get(boss.id()), 3, 1).name());
         assertEquals(BossAbilityCatalog.Pattern.BEAM, StageAttackRules.profile(boss, 2, 3).pattern());
         assertEquals(3, StageAttackRules.profile(boss, 1, 2).pulses());
      } finally {
         BossCatalog.installCustom(java.util.Map.of());
      }
   }

   @Test public void shieldGetsWeakerAndNeverBlocksAllDamage() {
      assertEquals(.65, BossCrystals.multiplier(4), .0001);
      assertEquals(.825, BossCrystals.multiplier(2), .0001);
      assertEquals(1, BossCrystals.multiplier(0), .0001);
   }
}
