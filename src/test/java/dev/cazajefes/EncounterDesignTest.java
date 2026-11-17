package dev.cazajefes;

import com.google.gson.JsonParser;
import org.junit.Test;
import static org.junit.Assert.*;

public class EncounterDesignTest {
    @Test public void everyBossHasAValidFamilyAndCounterplayDefaults(){
        for(var boss:BossCatalog.all()){
            assertNotNull(EncounterDesign.family(boss));
            var rules=EncounterDesign.rules(boss.custom().options().encounter());
            assertTrue(rules.enabled());
            assertTrue(rules.weakpoints());
            assertTrue(rules.scaling());
        }
    }

    @Test public void groupBalanceIsBoundedAndUsesTheProposedTiers(){
        assertEquals(1,EncounterDesign.healthMultiplier(1),0);
        assertEquals(1.5,EncounterDesign.healthMultiplier(2),0);
        assertEquals(1.5,EncounterDesign.healthMultiplier(3),0);
        assertEquals(2.2,EncounterDesign.healthMultiplier(6),0);
        assertEquals(1,EncounterDesign.targetCount(1));
        assertEquals(2,EncounterDesign.targetCount(3));
        assertEquals(3,EncounterDesign.targetCount(6));
        assertThrows(IllegalArgumentException.class,()->EncounterDesign.rules(
            JsonParser.parseString("{\"cooldownTicks\":0}").getAsJsonObject()));
    }

    @Test public void zonesLeaveDodgeableGaps(){
        assertTrue(EncounterDesign.shape(EncounterDesign.Family.ARCHER,0,2,0,3));
        assertFalse(EncounterDesign.shape(EncounterDesign.Family.ARCHER,2,0,0,3));
        assertFalse(EncounterDesign.shape(EncounterDesign.Family.SHELL,0,0,1,3));
        assertTrue(EncounterDesign.shape(EncounterDesign.Family.SHELL,2,0,1,3));
        assertFalse(EncounterDesign.shape(EncounterDesign.Family.VOID,2,2,0,3));
        assertTrue(EncounterDesign.shape(EncounterDesign.Family.VOID,0,2,0,3));
    }
}
