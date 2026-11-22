package dev.cazajefes.ai;
import org.junit.Test;import static org.junit.Assert.*;import org.bukkit.util.Vector;import dev.cazajefes.ai.attack.MagnetismAttack;
public class MagnetismTest {
 @Test public void jsonLoadsThirtyBlockMagnetismWithItsOwnCooldown(){var json=com.google.gson.JsonParser.parseString("{\"attacks\":[{\"id\":\"magnetismo\",\"name\":\"Magnetismo\",\"type\":\"MAGNETISM\",\"radius\":30,\"damage\":0,\"cooldownTicks\":400,\"warningTicks\":40}]}").getAsJsonObject();var profile=BossProfile.read(json,40,false);var attack=profile.attacks().get(0);assertTrue(attack instanceof MagnetismAttack);assertEquals(30,attack.radius(),0);assertEquals(400,attack.cooldown());assertEquals(40,attack.warningTicks());}

 @Test public void pullsFromEveryDirectionWithinThirtyBlocks(){for(Vector at:new Vector[]{new Vector(30,0,0),new Vector(-30,0,0),new Vector(0,30,0),new Vector(0,0,-30)}){var pull=MagnetismAttack.pull(new Vector(),at,30);assertNotNull(pull);assertTrue(pull.dot(at)<0);assertTrue(pull.length()<1.3);}}
 @Test public void respectsSphericalRadiusAndStopsNearBoss(){assertNull(MagnetismAttack.pull(new Vector(),new Vector(30.01,0,0),30));assertNull(MagnetismAttack.pull(new Vector(),new Vector(25,25,0),30));assertNull(MagnetismAttack.pull(new Vector(),new Vector(1,0,0),30));}
}
