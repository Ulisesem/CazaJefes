package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
import org.bukkit.*;
import java.lang.reflect.Proxy;
public class ParticleDataTest {
 @Test public void dragonBreathHasRequiredFloat(){assertEquals(Float.class,Particle.DRAGON_BREATH.getDataType());assertEquals(1f,(Float)ParticleData.simple(Particle.DRAGON_BREATH),0);}
 @Test public void everyDynamicLootAndBossParticleHasValidData(){for(Particle p:new Particle[]{Particle.CRIT,Particle.WITCH,Particle.ENCHANT,Particle.FLAME,Particle.SOUL_FIRE_FLAME,Particle.DRAGON_BREATH,Particle.HAPPY_VILLAGER,Particle.CLOUD}){Object data=ParticleData.simple(p);assertTrue(p.getDataType()==Void.class?data==null:p.getDataType().isInstance(data));}for(var boss:BossCatalog.all())assertNull(ParticleData.simple(CombatVisuals.motif(boss)));}
 @Test public void actualWorldCallReceivesDragonPayload(){int[] calls={0};World world=(World)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{World.class},(o,m,args)->{if(m.getName().equals("spawnParticle")){assertEquals(Particle.DRAGON_BREATH,args[0]);assertTrue(args[7] instanceof Float);calls[0]++;return null;}throw new UnsupportedOperationException(m.getName());});ParticleData.spawn(world,Particle.DRAGON_BREATH,new Location(world,0,0,0),1,0,0,0,0);assertEquals(1,calls[0]);}
 @Test(expected=IllegalArgumentException.class) public void explicitBlockPayloadCannotBeSilentlyOmitted(){ParticleData.simple(Particle.BLOCK);}
}
