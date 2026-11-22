package dev.cazajefes;

import com.google.gson.JsonObject;
import org.bukkit.Material;

/** Per-boss weapon choreography; timings are server ticks. */
record AxeThrowSettings(boolean enabled, int lodgedTicks, int flightTicks, float visualScale,
                        int swordSharpness, int swordUnbreaking, Material fallbackAxe) {
    static AxeThrowSettings of(BossCatalog.Definition boss) {
        JsonObject json = boss.custom() == null ? new JsonObject() : BossTuning.section(boss, "axeThrow");
        return read(json, Material.valueOf(BossEquipment.material(boss.tier()) + "_AXE"));
    }

    static AxeThrowSettings read(JsonObject json, Material fallback) {
        return new AxeThrowSettings(BossTuning.flag(json,"enabled",true),
            (int)BossTuning.number(json,"lodgedTicks",300,20,72000),
            (int)BossTuning.number(json,"flightTicks",10,5,59),
            (float)BossTuning.number(json,"visualScale",2.1,.5,5),
            (int)BossTuning.number(json,"swordSharpness",10,1,255),
            (int)BossTuning.number(json,"swordUnbreaking",10,1,255),fallback);
    }

    static Material axe(Material held, Material fallback) {
        if (held.name().endsWith("_AXE")) return held;
        if (held.name().endsWith("_SWORD")) return Material.valueOf(held.name().replace("_SWORD","_AXE"));
        return fallback;
    }

    static Material sword(Material axe) {
        return Material.valueOf(axe.name().replace("_AXE","_SWORD"));
    }

    AxeThrowSettings withFlightTicks(int ticks) {
        return new AxeThrowSettings(enabled,lodgedTicks,Math.clamp(ticks,1,59),visualScale,
            swordSharpness,swordUnbreaking,fallbackAxe);
    }

    static JsonObject defaults() {
        var json = new JsonObject();
        json.addProperty("enabled",true);
        json.addProperty("lodgedTicks",300);
        json.addProperty("flightTicks",10);
        json.addProperty("visualScale",2.1);
        json.addProperty("swordSharpness",10);
        json.addProperty("swordUnbreaking",10);
        return json;
    }
}
