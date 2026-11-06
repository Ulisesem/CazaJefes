package dev.cazajefes.ai;

import com.google.gson.JsonParser;
import java.util.List;
import java.util.Random;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.junit.Test;
import static org.junit.Assert.*;

public class BossEngineTest {
    @Test public void selectionFiltersCategoryAndCooldownBeforeRolling(){
        var shortRange=attack("short",AttackCategory.SHORT_RANGE,900);
        var mid=attack("mid",AttackCategory.MID_RANGE,1);
        var mixed=attack("mixed",AttackCategory.MIXED,3);
        var zero=attack("disabled",AttackCategory.MIXED,0);
        var options=List.of(shortRange,mid,mixed,zero);
        var random=new Random(1370);
        int won=0;
        for(int i=0;i<8000;i++){
            var picked=DecisionMaker.choose(options,3,40,id->0,random);
            assertTrue(picked==mid||picked==mixed);
            if(picked==mixed)won++;
        }
        assertTrue("3 de 4 pesos",won>5650&&won<6350);
        assertSame(mid,DecisionMaker.choose(options,3,40,id->id.equals("mixed")?41:0,random));
        assertNull(DecisionMaker.choose(options,30,40,id->41,random));
        assertTrue(AttackCategory.LONG_RANGE.includes(10));
        assertTrue(AttackCategory.LONG_RANGE.includes(30));
        assertFalse(AttackCategory.LONG_RANGE.includes(30.001));
    }

    @Test public void radiusIsThreeDimensionalAndRespectsHitboxEdges(){
        var impact=new Vector(0,0,0);
        assertTrue(Geometry.sphereIntersects(impact,2,new BoundingBox(2,-.5,-.5,3,.5,.5)));
        assertFalse(Geometry.sphereIntersects(impact,2,new BoundingBox(1.5,1.5,0,3,3,1)));
        assertTrue(Geometry.sphereIntersects(impact,2,new BoundingBox(-3,-3,-3,3,3,3)));
    }

    @Test public void presetsCanRoundTripAndInvalidOptionsAreRejected(){
        var document=BossProfile.defaults(40,true);
        var attacks=document.getAsJsonArray("attacks");
        assertEquals(4,attacks.size());
        // The meteor's material is validated against the live Paper registry in the smoke test.
        attacks.remove(3);
        attacks.remove(2);
        attacks.remove(1);
        var preset=BossProfile.read(document,40,true);
        assertTrue(preset.aquatic());
        assertEquals(60,preset.detectionRange(),0);
        assertEquals(1,preset.attacks().size());
        assertEquals(30,preset.globalCooldown());
        var invalid=JsonParser.parseString("{\"globalCooldownTicks\":0}").getAsJsonObject();
        assertThrows(IllegalArgumentException.class,()->BossProfile.read(invalid,40,true));
    }

    @Test public void combatStylesUseDistanceAndRecoverLineOfSight(){
        var json=JsonParser.parseString("""
            {"attacks":[],"tactics":{"style":"RANGED","preferredMin":6,"preferredMax":12}}
            """).getAsJsonObject();
        var tactics=BossProfile.read(json,40,false).tactics();
        assertEquals(CombatStyle.RANGED,tactics.style());
        assertEquals(CombatStyle.Tactics.Move.RETREAT,tactics.move(4,true));
        assertEquals(CombatStyle.Tactics.Move.ORBIT,tactics.move(9,true));
        assertEquals(CombatStyle.Tactics.Move.APPROACH,tactics.move(15,true));
        assertEquals(CombatStyle.Tactics.Move.APPROACH,tactics.move(4,false));
        json.getAsJsonObject("tactics").addProperty("preferredMin",13);
        assertThrows(IllegalArgumentException.class,()->BossProfile.read(json,40,false));
    }

    private static BossAttack attack(String id,AttackCategory category,int weight){
        return new BossAttack(id,id,category,weight,20,2,10,20){
            @Override public AttackMotion execute(CustomBoss boss,Player target){return AttackMotion.complete();}
        };
    }
}
