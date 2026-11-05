package dev.cazajefes;

import org.bukkit.entity.EntityType;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.Assert.*;

public class JsonBossesTest {
   private static final String SAMPLE = """
      {"id":"centinela_prisma","template":"golem_de_la_grieta","name":"Centinela de Prisma",
       "health":800,"damage":22,"scale":1.65}
      """;

   @Test public void exampleRegistersThroughExistingBossCatalog() throws Exception {
      var boss = JsonBosses.parse(Files.readString(Path.of("docs/examples/centinela_prisma.json")));
      assertEquals(EntityType.IRON_GOLEM, boss.entityType());
      assertEquals(BestiaryCategory.EPIC, BestiaryCategory.of(boss));
      assertEquals(800, boss.custom().health(), 0);
      assertEquals(22, boss.custom().damage(), 0);
      assertEquals(1.65, boss.custom().scale(), 0);

      try {
         BossCatalog.installCustom(Map.of(boss.id(), boss));
         assertSame(boss, BossCatalog.get("CENTINELA_PRISMA"));
         assertTrue(BossCatalog.ids().contains(boss.id()));
         assertEquals(169, BossCatalog.all().size());
         var ultimate = BossAbilityCatalog.ultimate(boss);
         assertEquals("Fractura de Prisma", ultimate.name());
         assertEquals(BossAbilityCatalog.Pattern.LANES, ultimate.pattern());
         assertEquals(3, ultimate.pulses());
      } finally {
         BossCatalog.installCustom(Map.of());
      }
   }

   @Test public void inheritsUltimateWhenNotSpecified() throws Exception {
      var boss = JsonBosses.parse(SAMPLE);
      assertEquals(BossAbilityCatalog.ultimate(BossCatalog.get("golem_de_la_grieta")),
         BossAbilityCatalog.ultimate(boss));
   }

   @Test public void rejectsInvalidOrAmbiguousDefinitions() {
      assertThrows(IllegalArgumentException.class, () -> JsonBosses.parse(SAMPLE.replace("centinela_prisma", "random")));
      assertThrows(IllegalArgumentException.class, () -> JsonBosses.parse(SAMPLE.replace("centinela_prisma", "golem_de_la_grieta")));
      assertThrows(IllegalArgumentException.class, () -> JsonBosses.parse(SAMPLE.replace("golem_de_la_grieta", "desconocido")));
      assertThrows(IllegalArgumentException.class, () -> JsonBosses.parse(SAMPLE.replace("800", "2000")));
      assertThrows(IllegalArgumentException.class, () -> JsonBosses.parse(SAMPLE.replace("800", "\"800\"")));
      assertThrows(IllegalArgumentException.class, () -> JsonBosses.parse(SAMPLE.replace("\"damage\":22", "\"health\":22")));
      assertThrows(IllegalArgumentException.class, () -> JsonBosses.parse(SAMPLE.replace("\"damage\":22", "\"damge\":22")));
      assertThrows(IllegalArgumentException.class, () -> JsonBosses.parse(SAMPLE.replace("\"scale\":1.65", "\"scale\":1.65,\"ultimate\":{\"pulses\":2.5}")));
   }
}
