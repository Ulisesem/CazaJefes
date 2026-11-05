package dev.cazajefes;

import com.google.gson.JsonParser;
import org.bukkit.entity.EntityType;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;

import static org.junit.Assert.*;

public class CinematicFXTest {
   @Test public void everyBossHasAThemeAndThePackHasAllItsCues() throws Exception {
      var pack = Path.of("resourcepack/assets/cazajefes/sounds.json");
      var cues = JsonParser.parseString(Files.readString(pack)).getAsJsonObject();
      var styles = EnumSet.noneOf(BossStyle.class);
      for (var boss : BossCatalog.all()) styles.add(BossStyle.of(boss.entityType()));
      assertEquals(EnumSet.allOf(BossStyle.class), styles);
      assertEquals(30, cues.size());
      for (var style : styles) for (var beat : BossStyle.Beat.values()) {
         String key = style.key(beat).split(":", 2)[1];
         assertEquals(style.sound(beat), cues.getAsJsonObject(key).getAsJsonArray("sounds")
            .get(0).getAsJsonObject().get("name").getAsString());
      }
      assertEquals(BossStyle.EXECUTIONER, BossStyle.of(EntityType.ZOMBIE));
      assertEquals(BossStyle.TIDE, BossStyle.of(EntityType.GUARDIAN));
      assertEquals(BossStyle.VOID, BossStyle.of(EntityType.WARDEN));
   }

   @Test public void bossDebuffGuardKeepsTheArenaVisible() {
      assertTrue(BossEffects.obscuresBossCombat("blindness"));
      assertTrue(BossEffects.obscuresBossCombat("darkness"));
      assertFalse(BossEffects.obscuresBossCombat("poison"));
      assertFalse(BossEffects.obscuresBossCombat("slowness"));
   }

   @Test public void packRequiresAnHttpsUrlAndFullDigest() {
      String sha = "0123456789abcdef0123456789abcdef01234567";
      assertTrue(CinematicCues.validPack("https://example.com/caza.zip", sha));
      assertFalse(CinematicCues.validPack("http://example.com/caza.zip", sha));
      assertFalse(CinematicCues.validPack("https://example.com/caza.zip", "abc"));
      assertFalse(CinematicCues.validPack("https://bad@example.com/caza.zip", sha));
   }
}
