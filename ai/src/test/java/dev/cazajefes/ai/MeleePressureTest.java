package dev.cazajefes.ai;

import com.google.gson.JsonParser;
import dev.cazajefes.ai.attack.MeleeSweep;
import dev.cazajefes.ai.visual.BossVisuals;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.junit.Test;
import static org.junit.Assert.*;

public class MeleePressureTest {
    @Test public void fullContactDamageRemainsConstantInEveryPhaseAndForLargeBodies() {
        for(int phase=1;phase<=3;phase++){
            var fight=new Fight(phase,true);
            fight.run(0,100);
            assertTrue("phase "+phase+" hits="+fight.hits.size(),fight.hits.size()>=8);
            assertTrue(fight.damage.stream().allMatch(value->value==20));
            assertSpacing(fight.hits,10);
        }
    }

    @Test public void releasedMobileSpecialKeepsContactPressureWithoutAnotherWarning() {
        var fight=new Fight(3,true);
        assertTrue(fight.boss.requestAttack("spell",fight.player));
        fight.run(0,10);
        assertTrue("The special's warning must finish before any contact hit",fight.hits.isEmpty());
        fight.run(10,80);
        assertTrue(fight.hits.size()>=6);
        assertEquals(1,fight.warnings);
        assertEquals(BossState.ATTACKING,fight.boss.state());
        assertSpacing(fight.hits,10);
    }

    @Test public void anchoredSpecialAndDisabledPressureDoNotReceiveExtraHits() {
        var anchored=new Fight(3,false);
        assertTrue(anchored.boss.requestAttack("spell",anchored.player));
        anchored.run(0,80);
        assertTrue(anchored.hits.isEmpty());
        var disabled=new Fight(3,true,false);
        assertTrue(disabled.boss.requestAttack("spell",disabled.player));
        disabled.run(0,80);
        assertTrue(disabled.hits.isEmpty());
    }

    @Test public void eligibilityAndStunsStillStopContactDamage() {
        var blocked=new Fight(3,true);
        blocked.allowed=false;
        blocked.run(0,60);
        assertTrue(blocked.hits.isEmpty());
        var wall=new Fight(3,true);
        wall.visible=false;
        wall.run(0,60);
        assertTrue(wall.hits.isEmpty());
        assertEquals(0,wall.impacts);
        var stunned=new Fight(3,true);
        stunned.run(0,20);
        int before=stunned.hits.size();
        stunned.boss.stun(20);
        stunned.run(20,39);
        assertEquals(before,stunned.hits.size());
        stunned.run(39,65);
        assertTrue(stunned.hits.size()>before);
    }

    @Test public void jsonCadenceOverridesAreLoadedWithoutChangingDamageRangeOrWeight() {
        var document=JsonParser.parseString("""
            {"melee":{"intervalTicks":19,"warningTicks":4,"duringMobileSpecials":false},
             "disableDefaultFaultLine":true,"disableDefaultShockwave":true,
             "attacks":[{"id":"hit","name":"Hit","type":"MELEE_SWEEP","damage":37,
                "radius":2.7,"weight":7,"cooldownTicks":48,"warningTicks":12}]}
            """).getAsJsonObject();
        var profile=BossProfile.read(document,40,false);
        var attack=profile.attacks().getFirst();
        assertEquals(19,attack.cooldown());
        assertEquals(4,attack.warningTicks());
        assertEquals(37,attack.damage(),0);
        assertEquals(2.7,attack.radius(),0);
        assertEquals(7,attack.weight());
        assertFalse(profile.melee().duringMobileSpecials());
        document.getAsJsonObject("melee").addProperty("intervalTicks",9);
        assertThrows(IllegalArgumentException.class,()->BossProfile.read(document,40,false));
    }

    private static void assertSpacing(List<Integer> hits,int minimum) {
        for(int i=1;i<hits.size();i++)assertTrue("No invulnerability resets or alternate-melee spam",
            hits.get(i)-hits.get(i-1)>=minimum);
    }

    private static final class Fight implements BossEnvironment {
        final List<Integer> hits=new ArrayList<>();
        final List<Double> damage=new ArrayList<>();
        final World world;
        Player player;
        final LivingEntity body;
        final AbstractCustomBoss boss;
        final int phase;
        int tick,warnings,impacts;
        boolean allowed=true,visible=true;

        Fight(int phase,boolean mobile){this(phase,mobile,true);}
        Fight(int phase,boolean mobile,boolean pressure){
            this.phase=phase;
            world=proxy(World.class,(name,args)->{
                if(name.equals("getNearbyPlayers"))return List.of(player);
                throw new UnsupportedOperationException(name);
            });
            UUID playerId=UUID.randomUUID(),bodyId=UUID.randomUUID();
            player=proxy(Player.class,(name,args)->switch(name){
                case "getUniqueId" -> playerId;
                case "getLocation","getEyeLocation" -> new Location(world,3.5,0,0);
                case "getBoundingBox" -> new BoundingBox(3.2,0,-.3,3.8,1.8,.3);
                case "getWorld" -> world;
                case "getY" -> 0d;
                case "isValid","isOnline" -> true;
                case "getVelocity" -> new Vector();
                default -> throw new UnsupportedOperationException(name);
            });
            body=proxy(LivingEntity.class,(name,args)->switch(name){
                case "getUniqueId" -> bodyId;
                case "getLocation","getEyeLocation" -> new Location(world,0,0,0);
                case "getBoundingBox" -> new BoundingBox(-3,0,-3,3,6,3);
                case "getWorld" -> world;
                case "getY" -> 0d;
                case "getWidth" -> 6d;
                case "getHeight" -> 6d;
                case "setAI","setGravity" -> null;
                default -> throw new UnsupportedOperationException(name);
            });
            var first=new MeleeSweep("hit","Hit",AttackCategory.SHORT_RANGE,5,10,3,20,1);
            var second=new MeleeSweep("other_hit","Other",AttackCategory.SHORT_RANGE,5,10,3,20,1);
            var spell=new BossAttack("spell","Spell",AttackCategory.MIXED,1,400,10,0,10){
                @Override public AttackMotion execute(CustomBoss boss,Player target){
                    return new AttackMotion(){
                        int age;
                        @Override public boolean tick(){return ++age>=200;}
                        @Override public boolean mobile(){return mobile;}
                        @Override public void close(){}
                    };
                }
            };
            var profile=new BossProfile(.3,.08,60,5,2,15,false,
                new CombatStyle.Tactics(CombatStyle.BRAWLER,1,3,.2,false),List.of(first,second,spell),
                RangedMeleeSwap.DEFAULT,BossMagnetism.DEFAULT,new MeleeSettings(10,1,pressure));
            boss=new AbstractCustomBoss(body,profile,this){
                @Override protected void move(Player target,boolean approach){}
            };
        }

        void run(int from,int until){for(tick=from;tick<until;tick++)boss.tick(tick);}
        @Override public Player acquire(CustomBoss boss){return player;}
        @Override public boolean validTarget(CustomBoss boss,Player target){return target==player;}
        @Override public boolean mayMove(CustomBoss boss,Location at){return true;}
        @Override public boolean mayHit(CustomBoss boss,LivingEntity target){return allowed;}
        @Override public boolean canSee(CustomBoss boss,Player target){return visible;}
        @Override public int phase(CustomBoss boss){return phase;}
        @Override public void damage(CustomBoss boss,LivingEntity target,double amount){hits.add(tick);damage.add(amount);}
        @Override public void warning(CustomBoss boss,Player target,BossAttack attack,int ticks){warnings++;}
        @Override public void impact(CustomBoss boss,BossAttack attack,Location at){impacts++;}
        @Override public BossVisuals visuals(){return new BossVisuals(){
            private final View view=new View(){public void move(Location at){}public void close(){}};
            @Override public View boss(LivingEntity body){return view;}
            @Override public View projectile(Location at){return view;}
            @Override public View meteor(Location at,ItemStack helmet){return view;}
            @Override public void explosion(Location at){}
        };}
    }

    private static <T> T proxy(Class<T> type,BiFunction<String,Object[],Object> calls){
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(),new Class<?>[]{type},(object,method,args)->{
            return switch(method.getName()){
                case "equals" -> object==args[0];
                case "hashCode" -> System.identityHashCode(object);
                case "toString" -> "Fake"+type.getSimpleName();
                default -> calls.apply(method.getName(),args);
            };
        }));
    }
}
