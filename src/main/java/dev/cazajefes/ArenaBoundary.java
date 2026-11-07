package dev.cazajefes;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.World;

final class ArenaBoundary {
   private record Api(Object container, Method adapt, Method manager, Method vector,
                      Method applicable, Method regions, Method id) {}
   private static Api api;
   private static Set<String> protectedIds=Set.of();
   private static boolean warned;
   private static CazaJefesPlugin plugin;

   static void load(CazaJefesPlugin owner) {
      plugin=owner;
      protectedIds=owner.getConfig().getStringList("combat.worldguard-arenas").stream()
         .map(id -> id.strip().toLowerCase(Locale.ROOT)).filter(id -> !id.isBlank()).collect(Collectors.toUnmodifiableSet());
      api=null;
      warned=false;
      var worldGuard=owner.getServer().getPluginManager().getPlugin("WorldGuard");
      if (protectedIds.isEmpty()) return;
      if (worldGuard==null || !worldGuard.isEnabled()) {
         owner.getLogger().warning("worldguard-arenas requiere WorldGuard habilitado.");
         return;
      }
      try {
         var loader=worldGuard.getClass().getClassLoader();
         Class<?> wg=loader.loadClass("com.sk89q.worldguard.WorldGuard");
         Class<?> adapter=loader.loadClass("com.sk89q.worldedit.bukkit.BukkitAdapter");
         Class<?> vector=loader.loadClass("com.sk89q.worldedit.math.BlockVector3");
         Method platform=wg.getMethod("getPlatform");
         Method container=platform.getReturnType().getMethod("getRegionContainer");
         Object instance=wg.getMethod("getInstance").invoke(null);
         Method adapt=adapter.getMethod("adapt",World.class);
         Method manager=container.getReturnType().getMethod("get",adapt.getReturnType());
         Method applicable=manager.getReturnType().getMethod("getApplicableRegions",vector);
         Method regions=applicable.getReturnType().getMethod("getRegions");
         Method id=loader.loadClass("com.sk89q.worldguard.protection.regions.ProtectedRegion").getMethod("getId");
         api=new Api(container.invoke(platform.invoke(instance)),adapt,manager,vector.getMethod("at",int.class,int.class,int.class),
            applicable,regions,id);
      } catch (ReflectiveOperationException | LinkageError | RuntimeException error) { warn(error); }
   }

   private static final org.bukkit.NamespacedKey ORIGIN=new org.bukkit.NamespacedKey("cazajefes","arena_origin");

   static void pin(org.bukkit.entity.Entity entity,Location at) {
      if (entity.getPersistentDataContainer().has(ORIGIN)) return;
      entity.getPersistentDataContainer().set(ORIGIN,org.bukkit.persistence.PersistentDataType.INTEGER_ARRAY,
         new int[]{at.getBlockX(),at.getBlockY(),at.getBlockZ()});
   }

   static boolean allows(org.bukkit.entity.Entity entity,Location destination) {
      if (protectedIds.isEmpty()) return true;
      if (!allows(entity.getLocation(),destination)) return false;
      int[] at=entity.getPersistentDataContainer().get(ORIGIN,org.bukkit.persistence.PersistentDataType.INTEGER_ARRAY);
      return at==null || at.length!=3 || allows(new Location(entity.getWorld(),at[0],at[1],at[2]),destination);
   }

   static boolean allowsTerrain(org.bukkit.entity.Entity boss,Location destination) {
      if(plugin==null||!plugin.getServer().getPluginManager().isPluginEnabled("WorldGuard"))
         return allows(boss,destination);
      if(api==null||!allows(boss,destination))return false;
      try{
         if(regions(destination).isEmpty())return false;
         Object set=applicable(destination);
         var all=(Iterable<?>)api.regions().invoke(set);
         for(var region:all){
            String name=(String)api.id().invoke(region);
            if(!protectedIds.contains(name))return false;
         }
         var loader=set.getClass().getClassLoader();
         var stateFlag=loader.loadClass("com.sk89q.worldguard.protection.flags.StateFlag");
         var flags=loader.loadClass("com.sk89q.worldguard.protection.flags.Flags");
         var associable=loader.loadClass("com.sk89q.worldguard.protection.association.RegionAssociable");
         var array=java.lang.reflect.Array.newInstance(stateFlag,2);
         java.lang.reflect.Array.set(array,0,flags.getField("BUILD").get(null));
         java.lang.reflect.Array.set(array,1,flags.getField("BLOCK_BREAK").get(null));
         var query=loader.loadClass("com.sk89q.worldguard.protection.ApplicableRegionSet")
            .getMethod("queryState",associable,array.getClass());
         Object result=query.invoke(set,null,array);
         return result==null||!result.toString().equals("DENY");
      }
      catch(ReflectiveOperationException|RuntimeException failure){warn(failure);return false;}
   }

   static boolean allows(Location source, Location destination) {
      if (protectedIds.isEmpty()) return true;
      if (source.getWorld()!=destination.getWorld()) return false;
      if (api==null) return false;
      try { return sharesArena(regions(source),regions(destination)); }
      catch (ReflectiveOperationException | LinkageError | RuntimeException error) { warn(error);return false; }
   }

   static boolean sharesArena(Set<String> a, Set<String> b) {
      return a.isEmpty() && b.isEmpty() || a.stream().anyMatch(b::contains);
   }

   private static Set<String> regions(Location at) throws ReflectiveOperationException {
      var ids=new HashSet<String>();
      var regions=(Iterable<?>)api.regions().invoke(applicable(at));
      for (Object region : regions) {
         String id=(String)api.id().invoke(region);
         if (protectedIds.contains(id)) ids.add(id);
      }
      return ids;
   }

   private static Object applicable(Location at) throws ReflectiveOperationException {
      Object manager=api.manager().invoke(api.container(),api.adapt().invoke(null,at.getWorld()));
      if (manager==null) throw new IllegalStateException("WorldGuard sin gestor para "+at.getWorld().getName());
      Object vector=api.vector().invoke(null,at.getBlockX(),at.getBlockY(),at.getBlockZ());
      return api.applicable().invoke(manager,vector);
   }

   private static void warn(Throwable error) {
      if (warned) return;
      warned=true;
      plugin.getLogger().warning("No se pudo consultar WorldGuard; combate protegido bloqueado: "+error.getMessage());
   }
}
