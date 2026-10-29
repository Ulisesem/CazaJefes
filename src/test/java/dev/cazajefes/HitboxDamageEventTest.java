package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
import java.lang.reflect.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.damage.DamageSource;
import org.bukkit.event.entity.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.*;
import org.bukkit.util.Vector;
public class HitboxDamageEventTest {
 private Object proxy(Class<?> type,java.util.function.BiFunction<String,Object[],Object> calls){return Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{type},(o,m,args)->{if(m.getName().equals("equals"))return o==args[0];if(m.getName().equals("hashCode"))return System.identityHashCode(o);if(m.getName().equals("toString"))return "test-"+type.getSimpleName();return calls.apply(m.getName(),args);});}
 private World world(boolean blocked){return (World)proxy(World.class,(name,args)->{if(name.equals("rayTraceBlocks"))return blocked?new RayTraceResult(new Vector(1,1,0)):null;throw new UnsupportedOperationException(name);});}
 private PersistentDataContainer data(boolean boss){Map<Object,Object> values=new HashMap<>();if(boss)values.put(new NamespacedKey("cazajefes",BossKeys.BOSS),(byte)1);return (PersistentDataContainer)proxy(PersistentDataContainer.class,(name,args)->switch(name){case "has"->values.containsKey(args[0]);case "get"->values.get(args[0]);case "getOrDefault"->values.getOrDefault(args[0],args[2]);case "set"->{values.put(args[0],args[2]);yield null;}default->throw new UnsupportedOperationException(name);});}
 private Player player(World world){var data=data(false);return (Player)proxy(Player.class,(name,args)->switch(name){case "getWorld"->world;case "getLocation"->new Location(world,0,0,0);case "getEyeLocation"->new Location(world,0,1.6,0);case "getPersistentDataContainer"->data;case "hasLineOfSight"->false;case "isOnline"->true;case "isDead"->false;case "getGameMode"->GameMode.SURVIVAL;default->throw new UnsupportedOperationException(name);});}
 private LivingEntity boss(World world,double centerX,BoundingBox bounds){var data=data(true);return (LivingEntity)proxy(LivingEntity.class,(name,args)->switch(name){case "getWorld"->world;case "getLocation"->new Location(world,centerX,0,0);case "getBoundingBox"->bounds;case "getPersistentDataContainer"->data;default->throw new UnsupportedOperationException(name);});}
 private EntityDamageByEntityEvent hit(Player player,LivingEntity boss){DamageSource damage=(DamageSource)proxy(DamageSource.class,(n,a)->null);return new EntityDamageByEntityEvent(player,boss,EntityDamageEvent.DamageCause.ENTITY_ATTACK,damage,10);}
 @Test public void passiveBossCanDealContactDamageUsingVisibleBody(){World world=world(false);Player player=player(world);LivingEntity boss=boss(world,3,new BoundingBox(2,0,-1,4,5,1));DamageSource damage=(DamageSource)proxy(DamageSource.class,(n,a)->null);var event=new EntityDamageByEntityEvent(boss,player,EntityDamageEvent.DamageCause.ENTITY_ATTACK,damage,26);new CombatGuard(p->true).restrict(event);assertFalse(event.isCancelled());assertEquals(26,event.getFinalDamage(),0);}
 @Test public void visibleBodyCanBeHitWhenEyeToEyeTestFails(){World world=world(false);Player player=player(world);LivingEntity boss=boss(world,3,new BoundingBox(2,0,-1,4,8,1));assertFalse(player.hasLineOfSight(boss));var event=hit(player,boss);new CombatGuard(p->true).restrict(event);assertFalse(event.isCancelled());assertEquals(10,event.getFinalDamage(),0);}
 @Test public void solidWallStillCancelsDamage(){World world=world(true);Player player=player(world);var event=hit(player,boss(world,3,new BoundingBox(2,0,-1,4,8,1)));var guard=new CombatGuard(p->true);guard.restrict(event);assertTrue(event.isCancelled());assertTrue(guard.rejection(event).contains("pared"));}
 @Test public void reachUsesActualHitboxInsteadOfFarAwayCenter(){World world=world(false);Player player=player(world);var event=hit(player,boss(world,40,new BoundingBox(2,0,-2,42,12,2)));new CombatGuard(p->true).restrict(event);assertFalse(event.isCancelled());}
 @Test public void modesAndRangeRemainRestricted(){World world=world(false);Player player=player(world);var denied=hit(player,boss(world,3,new BoundingBox(2,0,-1,4,8,1)));new CombatGuard(p->false).restrict(denied);assertTrue(denied.isCancelled());var far=hit(player,boss(world,40,new BoundingBox(39,0,-1,41,8,1)));new CombatGuard(p->true).restrict(far);assertTrue(far.isCancelled());}
 @Test public void externalCancellationIsNeverUndone(){World world=world(false);Player player=player(world);var event=hit(player,boss(world,3,new BoundingBox(2,0,-1,4,8,1)));event.setCancelled(true);new CombatGuard(p->true).restrict(event);assertTrue(event.isCancelled());}
 @Test public void aquaticProtectionDoesNotCancelAttributedPlayerCustomDamage(){World world=world(false);Player player=player(world);LivingEntity boss=boss(world,3,new BoundingBox(2,0,-1,4,8,1));DamageSource damage=(DamageSource)proxy(DamageSource.class,(n,a)->null);var event=new EntityDamageByEntityEvent(player,boss,EntityDamageEvent.DamageCause.CUSTOM,damage,10);new AquaticCombat().waterDamage(event);assertFalse(event.isCancelled());}
}
