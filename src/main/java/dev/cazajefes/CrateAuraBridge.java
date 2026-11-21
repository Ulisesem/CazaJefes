package dev.cazajefes;
import java.lang.reflect.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;
/** Calls the provider's actual effect implementations; no source code is copied. */
final class CrateAuraBridge {
 private static final String[] STYLES={"Helix","Spiral","Sphere","Heart","Pulsar","Beacon","Tornado","Vortex","Simple"};
 private static final Map<UUID,Object> effects=new HashMap<>();private static Method step,particle;private static ClassLoader loader;
 static void load(CazaJefesPlugin owner){stop();var provider=owner.getServer().getPluginManager().getPlugin("ExcellentCrates");if(provider==null||!provider.isEnabled())return;
  try{loader=provider.getClass().getClassLoader();Class<?> base=loader.loadClass("su.nightexpress.excellentcrates.crate.effect.CrateEffect"),uni=loader.loadClass("su.nightexpress.nightcore.util.wrapper.UniParticle");step=base.getMethod("onStepPlay",Location.class,uni,int.class,Player.class);particle=uni.getMethod("of",Particle.class);owner.getLogger().info("ExcellentCrates conectado: nueve efectos reales disponibles para bosses.");}catch(ReflectiveOperationException ex){stop();owner.getLogger().warning("ExcellentCrates incompatible: se mantienen las auras propias.");}
 }
 static boolean render(Player viewer,LivingEntity boss,int frame){if(step==null)return false;try{effects.keySet().removeIf(id->Bukkit.getEntity(id)==null);Object effect=effects.get(boss.getUniqueId());if(effect==null){String name=STYLES[Math.floorMod(boss.getUniqueId().hashCode(),STYLES.length)];effect=loader.loadClass("su.nightexpress.excellentcrates.crate.effect.impl."+name+"Effect").getConstructor().newInstance();effects.put(boss.getUniqueId(),effect);}step.invoke(effect,boss.getLocation().clone(),particle.invoke(null,Particle.ELECTRIC_SPARK),Math.floorMod(frame,24),viewer);return true;}catch(ReflectiveOperationException|RuntimeException ex){stop();return false;}}
 static void stop(){effects.clear();step=null;particle=null;loader=null;}
}
