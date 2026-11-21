package dev.cazajefes.ai;

import com.google.gson.JsonObject;
import org.junit.Test;
import static org.junit.Assert.*;

/** Unit tests for RangedMeleeSwap configuration parsing. */
public class RangedMeleeSwapTest {

    @Test public void defaultConfigIsDisabled() {
        var cfg = RangedMeleeSwap.DEFAULT;
        assertFalse(cfg.enabled());
        assertEquals(3.0, cfg.meleeRange(), 0.001);
        assertEquals(6.0, cfg.rangedRange(), 0.001);
        assertNull(cfg.meleeWeapon());
        assertNull(cfg.rangedWeapon());
    }

    @Test public void readConfigWithNoJsonReturnsDefault() {
        var cfg = RangedMeleeSwap.readConfig(null);
        assertFalse(cfg.enabled());
    }

    @Test public void readConfigWithMissingKeyReturnsDefault() {
        var json = new JsonObject();
        json.addProperty("speed", 0.3);
        var cfg = RangedMeleeSwap.readConfig(json);
        assertFalse(cfg.enabled());
    }

    @Test public void readConfigEnabled() {
        var json = new JsonObject();
        var swap = new JsonObject();
        swap.addProperty("enabled", true);
        swap.addProperty("meleeRange", 2.5);
        swap.addProperty("rangedRange", 7.0);
        swap.addProperty("meleeWeapon", "DIAMOND_SWORD");
        swap.addProperty("rangedWeapon", "CROSSBOW");
        json.add("rangedMeleeSwap", swap);
        var cfg = RangedMeleeSwap.readConfig(json);
        assertTrue(cfg.enabled());
        assertEquals(2.5, cfg.meleeRange(), 0.001);
        assertEquals(7.0, cfg.rangedRange(), 0.001);
        assertEquals("DIAMOND_SWORD", cfg.meleeWeapon());
        assertEquals("CROSSBOW", cfg.rangedWeapon());
    }

    @Test public void readConfigPartialKeysUsesDefaults() {
        var json = new JsonObject();
        var swap = new JsonObject();
        swap.addProperty("enabled", true);
        // Only meleeRange provided; rangedRange defaults to 6.0 (which is > meleeRange 4.0, so no fix needed)
        swap.addProperty("meleeRange", 4.0);
        json.add("rangedMeleeSwap", swap);
        var cfg = RangedMeleeSwap.readConfig(json);
        assertTrue(cfg.enabled());
        assertEquals(4.0, cfg.meleeRange(), 0.001);
        assertEquals(6.0, cfg.rangedRange(), 0.001); // default rangedRange
        assertNull(cfg.meleeWeapon());
        assertNull(cfg.rangedWeapon());
    }

    @Test public void configConstructorFixesInvalidRanges() {
        // meleeRange <= 0 should default to 3.0
        var cfg1 = new RangedMeleeSwap.Config(true, 0, 10, null, null);
        assertEquals(3.0, cfg1.meleeRange(), 0.001);

        // rangedRange <= meleeRange should default to meleeRange + 3
        var cfg2 = new RangedMeleeSwap.Config(true, 5.0, 4.0, null, null);
        assertEquals(8.0, cfg2.rangedRange(), 0.001); // 5 + 3
    }

    @Test public void modeEnumHasTwoValues() {
        assertEquals(2, RangedMeleeSwap.Mode.values().length);
        assertEquals(RangedMeleeSwap.Mode.RANGED, RangedMeleeSwap.Mode.valueOf("RANGED"));
        assertEquals(RangedMeleeSwap.Mode.MELEE, RangedMeleeSwap.Mode.valueOf("MELEE"));
    }

    @Test public void swapInstanceStartsInRangedMode() {
        var swap = new RangedMeleeSwap();
        assertEquals(RangedMeleeSwap.Mode.RANGED, swap.mode());
    }

    @Test public void swapResetReturnsToRanged() {
        var swap = new RangedMeleeSwap();
        var cfg = new RangedMeleeSwap.Config(true, 3.0, 6.0, null, null);
        swap.configure(cfg);
        swap.reset();
        assertEquals(RangedMeleeSwap.Mode.RANGED, swap.mode());
    }
}
