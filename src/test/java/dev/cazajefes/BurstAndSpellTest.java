package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.Event.Result;
public class BurstAndSpellTest {
 @Test public void operatorIsExcludedAndNonOperatorCanBeMoved(){assertFalse(BurstVisuals.movable(true));assertTrue(BurstVisuals.movable(false));}
 @Test public void whiteSphereCoversAllDirectionsAndKeepsRadius(){var points=BurstVisuals.sphere(3);assertEquals(64,points.size());for(var p:points)assertEquals(3,Math.sqrt(p.x()*p.x()+p.y()*p.y()+p.z()*p.z()),.00001);assertTrue(points.stream().anyMatch(p->p.y()>2.8));assertTrue(points.stream().anyMatch(p->p.y()<-2.8));}
 @Test public void predictedAirCancellationDoesNotBlockBookButExplicitDenialDoes(){assertTrue(SpellInteraction.allowed(Action.RIGHT_CLICK_AIR,true,Result.DEFAULT));assertFalse(SpellInteraction.allowed(Action.RIGHT_CLICK_AIR,true,Result.DENY));assertFalse(SpellInteraction.allowed(Action.RIGHT_CLICK_BLOCK,true,Result.DEFAULT));assertTrue(SpellInteraction.allowed(Action.RIGHT_CLICK_BLOCK,false,Result.DEFAULT));assertFalse(SpellInteraction.allowed(Action.LEFT_CLICK_AIR,false,Result.DEFAULT));}
 @Test public void bossBurstHasAttackNameForEntireCatalog(){for(var boss:BossCatalog.all())assertTrue(BurstVisuals.name(boss).contains(boss.name()));assertEquals(10000,PhaseImpulse.cooldown(3));}
}
