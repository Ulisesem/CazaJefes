package dev.cazajefes;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Optional Model Engine R4 bridge. Native combat remains authoritative. */
final class ModelEngineCues {
   private record Api(Method lookup, Method create, Method model, Method add, Method remove,
                      Method getModels, Method handler, Method play, Method stop) {}
   private record Visual(String id, String prepare, String impact, String recovery) {
      String animation(BossStyle.Beat beat) {
         return switch (beat) { case PREPARE -> prepare; case IMPACT -> impact; case RECOVERY -> recovery; };
      }
   }
   private static final class Attached {
      final Object owner, handler;
      final Visual visual;
      String playing;
      Attached(Object owner, Object handler, Visual visual) {
         this.owner = owner;
         this.handler = handler;
         this.visual = visual;
      }
   }

   private final Plugin plugin;
   private final Map<String, Visual> visuals = new HashMap<>();
   private final Map<UUID, Attached> attached = new HashMap<>();
   private final Set<String> warned = new HashSet<>();
   private final Api api;

   ModelEngineCues(Plugin plugin, ConfigurationSection config) {
      this.plugin = plugin;
      if (config != null) for (String key : config.getKeys(false)) {
         var section = config.getConfigurationSection(key);
         if (section == null) continue;
         String id = section.getString("id", "");
         if (!id.matches("[a-zA-Z0-9_./-]{1,80}")) continue;
         visuals.put(key.toLowerCase(Locale.ROOT), new Visual(id, section.getString("prepare", "prepare"),
            section.getString("impact", "attack"), section.getString("recovery", "recover")));
      }
      api = visuals.isEmpty() ? null : resolve();
   }

   private Api resolve() {
      Plugin provider = plugin.getServer().getPluginManager().getPlugin("ModelEngine");
      if (provider == null || !provider.isEnabled()) return null;
      try {
         Class<?> engine = Class.forName("com.ticxo.modelengine.api.ModelEngineAPI", true, provider.getClass().getClassLoader());
         Method create = engine.getMethod("createModeledEntity", Entity.class);
         Method active = engine.getMethod("createActiveModel", String.class);
         Method handler = active.getReturnType().getMethod("getAnimationHandler");
         return new Api(engine.getMethod("getModeledEntity", Entity.class), create, active,
            create.getReturnType().getMethod("addModel", active.getReturnType(), boolean.class),
            create.getReturnType().getMethod("removeModel", String.class), create.getReturnType().getMethod("getModels"), handler,
            handler.getReturnType().getMethod("playAnimation", String.class, double.class, double.class, double.class, boolean.class),
            handler.getReturnType().getMethod("stopAnimation", String.class));
      } catch (ReflectiveOperationException | LinkageError failure) {
         plugin.getLogger().warning("Model Engine R4 no disponible para CazaJefes: " + failure.getMessage());
         return null;
      }
   }

   boolean play(LivingEntity boss, BossCatalog.Definition definition, BossStyle.Beat beat) {
      if (api == null || !boss.isValid() || boss.isDead()) return false;
      Visual visual = visuals.getOrDefault(definition.id(), visuals.get("style_" + BossStyle.of(boss.getType()).name().toLowerCase(Locale.ROOT)));
      if (visual == null) return false;
      UUID id = boss.getUniqueId();
      try {
         Attached model = attached.get(id);
         if (model == null) {
            if (attached.size() >= 32 || warned.contains(visual.id())) return false;
            Object owner = api.lookup().invoke(null, boss);
            if (owner != null && !((Map<?, ?>)api.getModels().invoke(owner)).isEmpty()) return false;
            Object active = api.model().invoke(null, visual.id());
            Object handler = api.handler().invoke(active);
            if (owner == null) owner = api.create().invoke(null, boss);
            model = new Attached(owner, handler, visual);
            attached.put(id, model);
            api.add().invoke(owner, active, false);
            if (!((Map<?, ?>)api.getModels().invoke(owner)).containsKey(visual.id())) {
               attached.remove(id);
               return false;
            }
         }
         String animation = model.visual.animation(beat);
         if (model.playing != null) api.stop().invoke(model.handler, model.playing);
         boolean played = api.play().invoke(model.handler, animation, .12, .15, 1.0, true) != null;
         model.playing = played ? animation : null;
         return played;
      } catch (ReflectiveOperationException | RuntimeException | LinkageError failure) {
         remove(id);
         if (warned.add(visual.id())) plugin.getLogger().warning("Modelo " + visual.id() + " no disponible; se usará el efecto de Paper.");
         return false;
      }
   }

   void remove(UUID id) {
      Attached model = attached.remove(id);
      if (model == null) return;
      try {
         api.remove().invoke(model.owner, model.visual.id());
      } catch (ReflectiveOperationException | RuntimeException | LinkageError ignored) {
         // The provider can be unavailable during shutdown.
      }
   }

   void stop() {
      for (UUID id : Set.copyOf(attached.keySet())) remove(id);
   }
}
