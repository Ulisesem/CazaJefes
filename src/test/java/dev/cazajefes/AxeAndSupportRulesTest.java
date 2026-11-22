package dev.cazajefes;

import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.junit.Test;
import static org.junit.Assert.*;

public class AxeAndSupportRulesTest {
    @Test public void thrownAxeAndBackupSwordFollowTheActualWeaponMaterial() {
        for(String material:new String[]{"IRON","DIAMOND","NETHERITE"}) {
            var axe=Material.valueOf(material+"_AXE");
            var sword=Material.valueOf(material+"_SWORD");
            assertEquals(axe,AxeThrowSettings.axe(axe,Material.NETHERITE_AXE));
            assertEquals(axe,AxeThrowSettings.axe(sword,Material.NETHERITE_AXE));
            assertEquals(sword,AxeThrowSettings.sword(axe));
        }
        assertEquals(Material.IRON_AXE,AxeThrowSettings.axe(Material.CROSSBOW,Material.IRON_AXE));
    }

    @Test public void axeDefaultsGiveFifteenSecondsAndRealSharpnessTenBonus() {
        var settings=AxeThrowSettings.read(new JsonObject(),Material.IRON_AXE);
        assertTrue(settings.enabled());
        assertEquals(15*20,settings.lodgedTicks());
        assertEquals(10,settings.flightTicks());
        assertEquals(10,settings.swordSharpness());
        assertEquals(10,settings.swordUnbreaking());
        assertEquals(5.5,SharpnessRules.bonus(settings.swordSharpness()),.00001);
    }

    @Test public void perBossTimingAndSwordOverridesAreHonored() {
        var json=new JsonObject();
        json.addProperty("lodgedTicks",500);
        json.addProperty("flightTicks",15);
        json.addProperty("swordSharpness",12);
        json.addProperty("visualScale",1.4);
        var settings=AxeThrowSettings.read(json,Material.DIAMOND_AXE);
        assertEquals(500,settings.lodgedTicks());
        assertEquals(15,settings.flightTicks());
        assertEquals(12,settings.swordSharpness());
        assertEquals(1.4,settings.visualScale(),.00001);
        json.addProperty("lodgedTicks","invalid");
        assertEquals(300,AxeThrowSettings.read(json,Material.IRON_AXE).lodgedTicks());
    }

    @Test public void crossbowSquadCombinesCoveringFireAndMeleeGuards() {
        var squad=SupportHordeSettings.squad(6);
        assertEquals(6,squad.size());
        assertEquals(4,squad.stream().filter(unit->unit.type()==EntityType.PILLAGER
            &&unit.role()==CombatScripts.Role.ARCHER).count());
        assertEquals(2,squad.stream().filter(unit->unit.type()==EntityType.VINDICATOR
            &&unit.role()==CombatScripts.Role.GUARD).count());
        assertEquals(9,SupportHordeSettings.squad(9).size());
    }

    @Test public void supportCadenceAndCountsAreIndependentPerBossSettings() {
        var defaults=SupportHordeSettings.read(new JsonObject());
        assertEquals(6,defaults.count());
        assertEquals(500,defaults.cooldownTicks());
        assertEquals(100,defaults.firstDelayTicks());
        var json=new JsonObject();
        json.addProperty("count",9);
        json.addProperty("cooldownTicks",900);
        json.addProperty("firstDelayTicks",0);
        json.addProperty("lifetimeTicks",1200);
        var edited=SupportHordeSettings.read(json);
        assertEquals(9,edited.count());
        assertEquals(900,edited.cooldownTicks());
        assertEquals(0,edited.firstDelayTicks());
        assertEquals(1200,edited.lifetimeTicks());
    }

    @Test public void populationBudgetChecksTheActualRequestedSquadSize() {
        assertTrue(SupportHordeSettings.room(10,190,2));
        assertFalse(SupportHordeSettings.room(10,190,6));
        assertFalse(SupportHordeSettings.room(0,190,3));
        assertFalse(SupportHordeSettings.room(-1,0,6));
        assertFalse(SupportHordeSettings.room(0,0,0));
    }
}
