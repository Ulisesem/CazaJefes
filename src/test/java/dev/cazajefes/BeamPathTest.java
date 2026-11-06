package dev.cazajefes;

import org.bukkit.Location;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class BeamPathTest {
   @Test public void reflectedLegPreservesTangentialDirection() {
      var heading = new Vector(1,0,1).normalize();
      var reflected = BeamPath.reflect(heading,new Vector(-1,0,0));
      assertEquals(-heading.getX(),reflected.getX(),1e-9);
      assertEquals(heading.getZ(),reflected.getZ(),1e-9);
      var leg = new BeamPath.Segment(new Location(null,4,0,0),reflected,5);
      assertTrue(BeamPath.hits(List.of(leg),new BoundingBox(2,-1,1,3,1,2)));
      assertFalse(BeamPath.hits(List.of(leg),new BoundingBox(2,-1,-2,3,1,-1)));
      assertFalse(BeamPath.same(List.of(leg),List.of()));
   }

   @Test public void arcaneNativeProfilesUseTheNewAttacks() {
      var boss = BossCatalog.get("arcaneth_maestro_del_vacio_azul");
      assertEquals(BossAbilityCatalog.Pattern.REFLECTION,StageAttackRules.profile(boss,2,3).pattern());
      assertEquals(BossAbilityCatalog.Pattern.ANCHORS,StageAttackRules.profile(boss,3,0).pattern());
   }
}
