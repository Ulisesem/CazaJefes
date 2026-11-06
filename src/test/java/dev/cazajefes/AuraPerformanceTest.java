package dev.cazajefes;

import org.junit.Test;
import java.util.UUID;
import static org.junit.Assert.*;

public class AuraPerformanceTest {
   @Test public void combatAndDistanceReduceDecorativeDetail() {
      assertEquals(AuraQuality.HIGH,BossAuras.detail(AuraQuality.HIGH,100,false));
      assertEquals(AuraQuality.LOW,BossAuras.detail(AuraQuality.HIGH,300,false));
      assertEquals(AuraQuality.LOW,BossAuras.detail(AuraQuality.HIGH,100,true));
      assertEquals(AuraQuality.OFF,BossAuras.detail(AuraQuality.OFF,100,true));
   }

   @Test public void everyFamilyFitsItsParticleCountAndHasItsOwnGeometry() {
      for (var style : BossStyle.values()) assertEquals(24,AuraShape.themed(style,2,3,1,24,3).size());
      var before=AuraShape.themed(BossStyle.EXECUTIONER,2,3,1,24,2);
      assertNotEquals(before,AuraShape.themed(BossStyle.EXECUTIONER,2,3,1,24,3));
      assertNotEquals(before,AuraShape.themed(BossStyle.TIDE,2,3,1,24,2));
   }

   @Test public void warningsHaveAnIndependentBudget() {
      var player=UUID.randomUUID();
      CosmeticFX.clear();
      try {
         assertTrue(CosmeticFX.allow(player,320,1000));
         assertFalse(CosmeticFX.allow(player,1,1000));
         assertTrue(CosmeticFX.allowWarning(player,1000));
      } finally { CosmeticFX.clear(); }
   }
}
