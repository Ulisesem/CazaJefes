package dev.cazajefes.ai;

import com.google.gson.JsonParser;
import dev.cazajefes.ai.attack.FaultLineAttack;
import dev.cazajefes.ai.attack.ShockwaveAttack;
import dev.cazajefes.ai.attack.MeteoriteAoEAttack;
import dev.cazajefes.ai.attack.MeleeSweep;
import java.util.ArrayList;
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
        assertEquals(3,preset.attacks().size());
        assertTrue(preset.attacks().stream().anyMatch(a->a.id().equals("falla_del_terreno")));
        assertTrue(preset.attacks().stream().anyMatch(a->a.id().equals("pulso_de_fractura")));
        assertEquals(18,preset.globalCooldown());
        assertEquals(8,preset.basicRecovery());
        assertEquals(55,preset.specialRecovery());
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

    @Test public void meleeUsesTheGapInsteadOfTheEntityCenter(){
        var large=new org.bukkit.util.BoundingBox(-3,0,-3,3,6,3);
        var player=new org.bukkit.util.BoundingBox(3.5,0,0,4.1,1.8,.6);
        assertEquals(.25,Geometry.gapSquared(large,player),.0001);
        player.shift(0,8,0);
        assertEquals(4.25,Geometry.gapSquared(large,player),.0001);
    }

    @Test public void adaptiveDecisionBoostsAntiFarmingAttacks(){
        var faultLine=new FaultLineAttack("fault","Falla",10,120,12,30,24);
        var shockwave=new ShockwaveAttack("shock","Ola",4,140,7,25,23);
        var melee=new MeleeSweep("melee","Golpe",AttackCategory.SHORT_RANGE,6,48,3,10,7);
        var options=List.of(faultLine,shockwave,melee);
        var random=new Random(42);
        
        // Tower farming: FaultLine should be heavily favored (4x boost)
        int faultLineWins=0;
        int total=1000;
        for(int i=0;i<total;i++){
            var picked=DecisionMaker.choose(options,8,100,id->0L,random,
                BehaviorTracker.PlayerBehavior.TOWER_FARMING);
            if(picked==faultLine)faultLineWins++;
        }
        // FaultLine has 40 effective weight vs 8 (shockwave 2x) + 2 (melee /3) = 50 total
        // Expected: 40/50 = 80%
        assertTrue("FaultLine debe ganar mayoría en TOWER_FARMING ("+faultLineWins+"/"+total+")",
            faultLineWins>600);
        
        // Hiding: Shockwave and AoE should be favored
        int shockWins=0;
        for(int i=0;i<total;i++){
            var picked=DecisionMaker.choose(options,5,100,id->0L,random,
                BehaviorTracker.PlayerBehavior.HIDING);
            if(picked==shockwave)shockWins++;
        }
        // Shockwave has 12 effective weight vs 10 (faultLine) + 6 (melee) = 28 total
        // Expected: 12/28 ≈ 43%
        assertTrue("Shockwave debe ganar mayoría en HIDING ("+shockWins+"/"+total+")",
            shockWins>300);
        
        // Aggressive: melee and shockwave should be favored
        int meleeWins=0;
        for(int i=0;i<total;i++){
            var picked=DecisionMaker.choose(options,2,100,id->0L,random,
                BehaviorTracker.PlayerBehavior.AGGRESSIVE);
            if(picked==melee)meleeWins++;
        }
        // Melee has 12 effective weight vs 20 (faultLine 2x) + 12 (shockwave 3x) = 44 total
        // Expected: 12/44 ≈ 27%
        assertTrue("Melee debe ganar en AGGRESSIVE ("+meleeWins+"/"+total+")",
            meleeWins>150);
    }

    @Test public void neutralBehaviorPreservesOriginalWeights(){
        var a=attack("a",AttackCategory.MIXED,1);
        var b=attack("b",AttackCategory.MIXED,3);
        var options=List.of(a,b);
        var random=new Random(0);
        int bWins=0;
        int total=1000;
        for(int i=0;i<total;i++){
            var picked=DecisionMaker.choose(options,5,100,id->0L,random,
                BehaviorTracker.PlayerBehavior.NEUTRAL);
            if(picked==b)bWins++;
        }
        // b has 3x weight of a: expected 75%
        assertTrue("Neutral debe preservar pesos originales ("+bWins+"/"+total+")",
            bWins>650&&bWins<850);
    }

    @Test public void kitingDeprioritizesMelee(){
        var melee=new MeleeSweep("melee","Golpe",AttackCategory.SHORT_RANGE,6,48,3,10,7);
        var faultLine=new FaultLineAttack("fault","Falla",10,120,12,30,24);
        var options=List.of(melee,faultLine);
        var random=new Random(99);
        int meleeWins=0;
        int total=1000;
        for(int i=0;i<total;i++){
            var picked=DecisionMaker.choose(options,15,100,id->0L,random,
                BehaviorTracker.PlayerBehavior.KITING);
            if(picked==melee)meleeWins++;
        }
        // Melee has 1.5 effective weight (6/4) vs 10 (faultLine) = 11.5 total
        // Expected: 1.5/11.5 ≈ 13%
        assertTrue("Melee debe ser raro en KITING ("+meleeWins+"/"+total+")",
            meleeWins<250);
    }

    private static BossAttack attack(String id,AttackCategory category,int weight){
        return new BossAttack(id,id,category,weight,20,2,10,20){
            @Override public AttackMotion execute(CustomBoss boss,Player target){return AttackMotion.complete();}
        };
    }
}
