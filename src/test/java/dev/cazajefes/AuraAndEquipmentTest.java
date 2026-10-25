package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
import org.bukkit.entity.EntityType;
public class AuraAndEquipmentTest {
 @Test public void auraScalesOutsideWideBossBody(){for(double width:new double[]{.6,1,4,8,14}){var points=AuraShape.points(width,10,0);assertEquals(40,points.size());for(int i=0;i<16;i++){var point=points.get(i);assertTrue(Math.hypot(point.x(),point.z())>width/2);}assertTrue(points.stream().anyMatch(point->point.y()>10));}}
 @Test public void helixSpansBodyAndAnimates(){var a=AuraShape.points(2,8,0);var b=AuraShape.points(2,8,.2);assertNotEquals(a,b);assertTrue(a.stream().anyMatch(point->point.y()>6&&point.y()<8));assertTrue(a.stream().allMatch(point->Double.isFinite(point.x())&&Double.isFinite(point.y())&&Double.isFinite(point.z())));}
 @Test public void zombieFamiliesAndMaterialProgression(){for(EntityType type:new EntityType[]{EntityType.ZOMBIE,EntityType.HUSK,EntityType.DROWNED,EntityType.GIANT})assertTrue(BossEquipment.zombie(type));assertFalse(BossEquipment.zombie(EntityType.SPIDER));assertFalse(BossEquipment.zombie(EntityType.PLAYER));assertEquals("IRON",BossEquipment.material(BossCatalog.Tier.RARE));assertEquals("DIAMOND",BossEquipment.material(BossCatalog.Tier.EPIC));assertEquals("NETHERITE",BossEquipment.material(BossCatalog.Tier.ABYSSAL));}
}
