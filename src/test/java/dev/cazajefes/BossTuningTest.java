package dev.cazajefes;
import com.google.gson.JsonObject;
import org.junit.Test;
import static org.junit.Assert.*;
public class BossTuningTest {
 @Test public void upgradingAddsEggAndFlightControlsWithoutChangingCustomAttacks(){
  var document=new JsonObject();var combat=new JsonObject();document.add("combat",combat);
  var mobility=new JsonObject();mobility.addProperty("climbSpeed",.2);combat.add("mobility",mobility);
  var ai=new JsonObject();ai.addProperty("customAttack","keep-me");document.add("ai",ai);
  assertTrue(BossTuning.complete(document));
  assertEquals(.2,mobility.get("climbSpeed").getAsDouble(),0);
  assertFalse(mobility.get("pursueFlyingPlayers").getAsBoolean());
  assertEquals(2,combat.getAsJsonObject("eggParticles").get("height").getAsDouble(),0);
  assertTrue(combat.getAsJsonObject("eggParticles").get("removeGeneratedPedestal").getAsBoolean());
  assertEquals("keep-me",document.getAsJsonObject("ai").get("customAttack").getAsString());
  assertFalse(BossTuning.complete(document));
 }
 @Test public void invalidPresentationNumbersFallBackAndFiniteOverridesAreBounded(){
  var values=new JsonObject();values.addProperty("height","bad");values.addProperty("radius",100);
  assertEquals(2,BossTuning.number(values,"height",2,1,4),0);
  assertEquals(2,BossTuning.number(values,"radius",1,.5,2),0);
 }
}
