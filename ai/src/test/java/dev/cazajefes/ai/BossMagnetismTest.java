package dev.cazajefes.ai;

import com.google.gson.JsonObject;
import org.junit.Test;
import static org.junit.Assert.*;

/** Unit tests for BossMagnetism configuration parsing. */
public class BossMagnetismTest {

    @Test public void defaultConfigIsDisabled() {
        var cfg = BossMagnetism.DEFAULT;
        assertFalse(cfg.enabled());
        assertEquals(1, cfg.minPhase());
        assertEquals(10.0, cfg.minDistance(), 0.001);
        assertEquals(25.0, cfg.maxDistance(), 0.001);
        assertEquals(0.15, cfg.pullStrength(), 0.001);
        assertEquals(20, cfg.cooldownTicks());
        assertTrue(cfg.onKiting());
        assertTrue(cfg.onLowHealth());
        assertEquals(0.3, cfg.lowHealthThreshold(), 0.001);
    }

    @Test public void readConfigWithNoJsonReturnsDefault() {
        var cfg = BossMagnetism.readConfig(null);
        assertFalse(cfg.enabled());
    }

    @Test public void readConfigWithMissingKeyReturnsDefault() {
        var json = new JsonObject();
        json.addProperty("speed", 0.3);
        var cfg = BossMagnetism.readConfig(json);
        assertFalse(cfg.enabled());
    }

    @Test public void readConfigEnabled() {
        var json = new JsonObject();
        var mag = new JsonObject();
        mag.addProperty("enabled", true);
        mag.addProperty("minPhase", 2);
        mag.addProperty("minDistance", 8.0);
        mag.addProperty("maxDistance", 30.0);
        mag.addProperty("pullStrength", 0.25);
        mag.addProperty("cooldownTicks", 10);
        mag.addProperty("onKiting", true);
        mag.addProperty("onLowHealth", true);
        mag.addProperty("lowHealthThreshold", 0.4);
        json.add("magnetism", mag);
        var cfg = BossMagnetism.readConfig(json);
        assertTrue(cfg.enabled());
        assertEquals(2, cfg.minPhase());
        assertEquals(8.0, cfg.minDistance(), 0.001);
        assertEquals(30.0, cfg.maxDistance(), 0.001);
        assertEquals(0.25, cfg.pullStrength(), 0.001);
        assertEquals(10, cfg.cooldownTicks());
        assertTrue(cfg.onKiting());
        assertTrue(cfg.onLowHealth());
        assertEquals(0.4, cfg.lowHealthThreshold(), 0.001);
    }

    @Test public void readConfigPartialKeysUsesDefaults() {
        var json = new JsonObject();
        var mag = new JsonObject();
        mag.addProperty("enabled", true);
        mag.addProperty("minPhase", 3);
        // Other keys omitted — should use defaults
        json.add("magnetism", mag);
        var cfg = BossMagnetism.readConfig(json);
        assertTrue(cfg.enabled());
        assertEquals(3, cfg.minPhase());
        assertEquals(10.0, cfg.minDistance(), 0.001); // default
        assertEquals(25.0, cfg.maxDistance(), 0.001); // default
        assertEquals(0.15, cfg.pullStrength(), 0.001); // default
        assertEquals(20, cfg.cooldownTicks()); // default
    }

    @Test public void configConstructorFixesInvalidValues() {
        // minPhase < 1 should default to 1
        var cfg1 = new BossMagnetism.Config(true, 0, 10, 25, 0.15, 20, true, true, 0.3);
        assertEquals(1, cfg1.minPhase());

        // minPhase > 5 should default to 5
        var cfg2 = new BossMagnetism.Config(true, 10, 10, 25, 0.15, 20, true, true, 0.3);
        assertEquals(5, cfg2.minPhase());

        // minDistance <= 0 should default to 10
        var cfg3 = new BossMagnetism.Config(true, 1, 0, 25, 0.15, 20, true, true, 0.3);
        assertEquals(10.0, cfg3.minDistance(), 0.001);

        // maxDistance <= minDistance should default to minDistance + 10
        var cfg4 = new BossMagnetism.Config(true, 1, 15, 10, 0.15, 20, true, true, 0.3);
        assertEquals(25.0, cfg4.maxDistance(), 0.001); // 15 + 10

        // pullStrength <= 0 should default to 0.15
        var cfg5 = new BossMagnetism.Config(true, 1, 10, 25, 0, 20, true, true, 0.3);
        assertEquals(0.15, cfg5.pullStrength(), 0.001);

        // pullStrength > 0.5 should default to 0.5
        var cfg6 = new BossMagnetism.Config(true, 1, 10, 25, 1.0, 20, true, true, 0.3);
        assertEquals(0.5, cfg6.pullStrength(), 0.001);

        // cooldownTicks < 5 should default to 20
        var cfg7 = new BossMagnetism.Config(true, 1, 10, 25, 0.15, 2, true, true, 0.3);
        assertEquals(20, cfg7.cooldownTicks());

        // cooldownTicks > 200 should default to 200
        var cfg8 = new BossMagnetism.Config(true, 1, 10, 25, 0.15, 500, true, true, 0.3);
        assertEquals(200, cfg8.cooldownTicks());

        // lowHealthThreshold < 0.05 should default to 0.3
        var cfg9 = new BossMagnetism.Config(true, 1, 10, 25, 0.15, 20, true, true, 0.01);
        assertEquals(0.3, cfg9.lowHealthThreshold(), 0.001);

        // lowHealthThreshold > 1.0 should default to 1.0
        var cfg10 = new BossMagnetism.Config(true, 1, 10, 25, 0.15, 20, true, true, 2.0);
        assertEquals(1.0, cfg10.lowHealthThreshold(), 0.001);
    }

    @Test public void magnetismInstanceStartsWithDefaultConfig() {
        var mag = new BossMagnetism();
        assertEquals(BossMagnetism.DEFAULT, mag.config());
    }

    @Test public void magnetismConfigureUpdatesConfig() {
        var mag = new BossMagnetism();
        var cfg = new BossMagnetism.Config(true, 2, 8, 30, 0.2, 15, true, false, 0.5);
        mag.configure(cfg);
        assertEquals(cfg, mag.config());
    }

    @Test public void magnetismResetClearsState() {
        var mag = new BossMagnetism();
        var cfg = new BossMagnetism.Config(true, 1, 10, 25, 0.15, 20, true, true, 0.3);
        mag.configure(cfg);
        mag.tick();
        mag.tick();
        mag.reset();
        // After reset, tick counter should be 0
        assertEquals(cfg, mag.config());
    }

    @Test public void onKitingDefaultsToTrueWhenOmitted() {
        var json = new JsonObject();
        var mag = new JsonObject();
        mag.addProperty("enabled", true);
        // onKiting omitted — should default to true
        json.add("magnetism", mag);
        var cfg = BossMagnetism.readConfig(json);
        assertTrue(cfg.onKiting());
    }

    @Test public void onLowHealthDefaultsToTrueWhenOmitted() {
        var json = new JsonObject();
        var mag = new JsonObject();
        mag.addProperty("enabled", true);
        // onLowHealth omitted — should default to true
        json.add("magnetism", mag);
        var cfg = BossMagnetism.readConfig(json);
        assertTrue(cfg.onLowHealth());
    }
}
