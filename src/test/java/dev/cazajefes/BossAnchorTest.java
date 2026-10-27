package dev.cazajefes;
import java.util.*;
import java.lang.reflect.Proxy;
import org.bukkit.entity.Mob;
import org.junit.Test;
import static org.junit.Assert.*;
public class BossAnchorTest {
 private Mob fake(Map<String,Boolean> flags){return (Mob)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{Mob.class},(p,m,args)->{return switch(m.getName()){case "hasAI"->flags.get("ai");case "hasGravity"->flags.get("gravity");case "isCollidable"->flags.get("collidable");case "setAI"->{flags.put("ai",(Boolean)args[0]);yield null;}case "setGravity"->{flags.put("gravity",(Boolean)args[0]);yield null;}case "setCollidable"->{flags.put("collidable",(Boolean)args[0]);yield null;}default->null;};});}
 @Test public void freezeAndRestorePreserveOriginalBossFlags(){Map<String,Boolean> flags=new HashMap<>(Map.of("ai",true,"gravity",true,"collidable",true));Mob boss=fake(flags);var snapshot=BossAnchor.Snapshot.capture(boss);snapshot.freeze(boss);assertFalse(boss.hasAI());assertFalse(boss.hasGravity());assertFalse(boss.isCollidable());snapshot.restore(boss);assertTrue(boss.hasAI());assertTrue(boss.hasGravity());assertTrue(boss.isCollidable());}
 @Test public void alreadyDisabledAiAndGravityAreNotEnabledOnRelease(){Map<String,Boolean> flags=new HashMap<>(Map.of("ai",false,"gravity",false,"collidable",false));Mob boss=fake(flags);var snapshot=BossAnchor.Snapshot.capture(boss);snapshot.freeze(boss);snapshot.restore(boss);assertFalse(boss.hasAI());assertFalse(boss.hasGravity());assertFalse(boss.isCollidable());}
}
