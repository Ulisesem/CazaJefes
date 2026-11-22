package dev.cazajefes;

import com.google.gson.JsonObject;
import org.junit.Test;
import static org.junit.Assert.*;

public class BossDifficultyTest {
    @Test public void everyRarityAddsDamageAndNeverExceedsTheNativeMeleeCadence() {
        double previousMelee=0,previousSpecial=0;
        int previousInterval=Integer.MAX_VALUE;
        for(var tier:BossCatalog.Tier.values()){
            var damage=BossDifficulty.defaults(tier,tier==BossCatalog.Tier.COMMON);
            var melee=BossDifficulty.meleeDefaults(tier,tier==BossCatalog.Tier.COMMON);
            assertTrue(damage.get("meleeDamageMultiplier").getAsDouble()>previousMelee);
            assertTrue(damage.get("specialDamageMultiplier").getAsDouble()>previousSpecial);
            int interval=melee.get("intervalTicks").getAsInt();
            assertTrue(interval>=10);
            assertTrue(interval<=previousInterval);
            previousInterval=interval;
            previousMelee=damage.get("meleeDamageMultiplier").getAsDouble();
            previousSpecial=damage.get("specialDamageMultiplier").getAsDouble();
        }
        assertEquals(1.33,previousMelee,0);
        assertEquals(1.25,previousSpecial,0);
    }

    @Test public void administratorDamageAndCadenceOverridesArePreserved() {
        var original=BossCatalog.get("saqueador_gris");
        var combat=original.custom().options().json().deepCopy();
        var difficulty=new JsonObject();
        difficulty.addProperty("meleeDamageMultiplier",1.75);
        difficulty.addProperty("specialDamageMultiplier",1.4);
        combat.add("difficulty",difficulty);
        var ai=original.custom().ai().deepCopy();
        var melee=new JsonObject();
        melee.addProperty("intervalTicks",25);
        melee.addProperty("warningTicks",3);
        melee.addProperty("duringMobileSpecials",false);
        ai.add("melee",melee);
        var custom=original.custom();
        var settings=new JsonBosses.Settings(custom.health(),custom.damage(),custom.scale(),custom.ultimate(),
            custom.portrait(),custom.meteorTexture(),new BossOptions(combat),ai);
        var edited=new BossCatalog.Definition(original.id(),original.name(),original.tier(),original.entityType(),
            original.mini(),original.description(),settings);
        assertEquals(1.75,BossDifficulty.of(edited).meleeDamageMultiplier(),0);
        assertEquals(1.4,BossDifficulty.of(edited).specialDamageMultiplier(),0);
        var result=BossFamilyAttacks.withDefaults(edited,false);
        assertEquals(25,result.getAsJsonObject("melee").get("intervalTicks").getAsInt());
        assertEquals(3,result.getAsJsonObject("melee").get("warningTicks").getAsInt());
        assertFalse(result.getAsJsonObject("melee").get("duringMobileSpecials").getAsBoolean());
        assertEquals(custom.damage(),edited.custom().damage(),0);
        assertEquals(ai.getAsJsonArray("attacks").get(0),result.getAsJsonArray("attacks").get(0));
    }

    @Test public void includedBossReceivesItsPublishedRaritySettings() {
        var boss=BossCatalog.get("saqueador_gris");
        var expected=BossDifficulty.defaults(boss.tier(),boss.mini());
        assertEquals(expected.get("meleeDamageMultiplier").getAsDouble(),BossDifficulty.of(boss).meleeDamageMultiplier(),0);
    }

    @Test public void nestedMeleeContextCannotLeakIntoTheNextSpecial() {
        assertFalse(BossHitContext.melee());
        BossHitContext.melee(()->{
            assertTrue(BossHitContext.melee());
            BossHitContext.melee(()->assertTrue(BossHitContext.melee()));
            assertTrue(BossHitContext.melee());
        });
        assertFalse(BossHitContext.melee());
        assertThrows(IllegalStateException.class,()->BossHitContext.melee(()->{throw new IllegalStateException();}));
        assertFalse(BossHitContext.melee());
    }
}
