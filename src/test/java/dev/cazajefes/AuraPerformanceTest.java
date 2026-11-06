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
 @Test public void bossSignaturesAndMagmaShapesUseTheSamePointBudget(){
  var shapes=new java.util.HashSet<java.util.List<AuraShape.Point>>();
  for(var boss:BossCatalog.all()){
   var dots=CreatureAura.of(boss.entityType()).points(3,4,25,.3,24,3,boss.id().hashCode());
   assertEquals(24,dots.size());
   assertTrue("Duplicate visual shape: "+boss.id(),shapes.add(dots));
  }
  for(int i=0;i<4;i++){
   var dots=AuraShape.magma(i,3,4,.3,24,3);
   assertEquals(24,dots.size());assertTrue(shapes.add(dots));
  }
 }
 @Test public void characteristicCreaturesUseCompatibleDistinctParticles(){
  assertEquals(org.bukkit.Particle.FALLING_HONEY,CreatureAura.of(org.bukkit.entity.EntityType.BEE).particle);
  assertEquals(org.bukkit.Particle.SCULK_SOUL,CreatureAura.of(org.bukkit.entity.EntityType.WARDEN).particle);
  assertEquals(org.bukkit.Particle.GLOW,CreatureAura.of(org.bukkit.entity.EntityType.GLOW_SQUID).particle);
  for(var boss:BossCatalog.all())assertEquals(Void.class,CreatureAura.of(boss.entityType()).particle.getDataType());
 }
 @Test public void crateAuraRespectsBudgetAndPulsesExpand(){
  var json=com.google.gson.JsonParser.parseString("{\"points\":30,\"radius\":4,\"height\":3}").getAsJsonObject();
  var crate=BossParticles.read(json,BossParticles.Shape.CRATE);
  var frame=crate.geometry(2,2,.2,12);
  assertEquals(12,frame.size());
  assertEquals(.16,frame.getFirst().y(),.00001);
  assertTrue(frame.getLast().y()>2.5);
  var pulse=BossParticles.read(json,BossParticles.Shape.PULSE);
  var first=pulse.geometry(2,2,-Math.PI/2,12).getFirst();
  var last=pulse.geometry(2,2,Math.PI/2,12).getFirst();
  assertEquals(4,Math.hypot(last.x(),last.z()),.00001);
  assertTrue(Math.hypot(first.x(),first.z())<Math.hypot(last.x(),last.z()));
  assertTrue(BossParticles.read(null,BossParticles.Shape.OFF).geometry(2,2,0,36).isEmpty());
 }
 @Test public void crateStylesCoverTheCatalogWithinTheParticleBudget() {
  var styles = java.util.EnumSet.noneOf(BossParticles.Shape.class);
  for (var boss : BossCatalog.all()) {
   var shape = BossParticles.crateStyle(boss.id());
   styles.add(shape);
   assertEquals(shape, BossParticles.crateStyle(boss.id()));
  }
  assertEquals(9, styles.size());
  for (var style : styles) {
   var config = BossParticles.read(null, style);
   for (int budget : new int[]{1, 3, 7, 15, 36}) {
    var frame = config.geometry(2, 3, .3, budget);
    assertEquals(budget, frame.size());
    for (var point : frame) {
     assertTrue(Math.hypot(point.x(), point.z()) <= 2.00001);
     assertTrue(point.y() >= .15 && point.y() <= 3.17);
    }
   }
  }
 }
 @Test public void followingFramesShareTheRefillWithoutGoingDark(){
  assertEquals(15,BossAuras.pointsForFrame(160,1,1,36,160));
  assertEquals(7,BossAuras.pointsForFrame(160,1,2,36,160));
  assertEquals(3,BossAuras.pointsForFrame(160,1,2,36,4));
  assertEquals(0,BossAuras.pointsForFrame(160,1,1,36,1));
 }
}
