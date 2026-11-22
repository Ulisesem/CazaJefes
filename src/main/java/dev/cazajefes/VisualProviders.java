package dev.cazajefes;

import java.lang.reflect.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.plugin.Plugin;

/** Optional external renderer. Own manager is disposed without touching other plugins. */
final class VisualProviders {
    private static Object manager;
    private static Method start, dispose;
    private static CazaJefesPlugin owner;
    private static final Map<UUID, Integer> ready = new HashMap<>();

    /** Load EffectLib integration if available. Call from plugin onEnable. */
    static void load(CazaJefesPlugin plugin) {
        stop();
        owner = plugin;
        CrateAuraBridge.load(plugin);
        Plugin provider = plugin.getServer().getPluginManager().getPlugin("EffectLib");
        if (provider != null && provider.isEnabled()) {
            try {
                Class<?> type = provider.getClass().getClassLoader()
                    .loadClass("de.slikey.effectlib.EffectManager");
                manager = type.getConstructor(Plugin.class).newInstance(plugin);
                start = type.getMethod("start", String.class, ConfigurationSection.class, Entity.class);
                dispose = type.getMethod("dispose");
                type.getMethod("setParticleRange", int.class).invoke(manager, 24);
                plugin.getLogger().info("EffectLib conectado para efectos de ataques.");
            } catch (ReflectiveOperationException ex) {
                manager = null;
                plugin.getLogger().warning("EffectLib incompatible; se conserva el motor de partículas propio.");
            }
        }
    }

    /** Trigger a sphere effect at the boss location on attack impact. */
    static void impact(LivingEntity boss) {
        if (manager == null || owner == null
            || !owner.getConfig().getBoolean("visual-providers.effectlib.enabled", true))
            return;
        int tick = Bukkit.getCurrentTick();
        ready.keySet().removeIf(id -> Bukkit.getEntity(id) == null);
        if (tick < ready.getOrDefault(boss.getUniqueId(), 0)) return;
        if (boss.getWorld().getNearbyPlayers(boss.getLocation(), 24).isEmpty()) return;
        ready.put(boss.getUniqueId(), tick + Math.max(0,
            owner.getConfig().getInt("visual-providers.effectlib.cooldown-ticks", 0)));
        MemoryConfiguration parameters = new MemoryConfiguration();
        parameters.set("iterations", Math.max(1,
            owner.getConfig().getInt("visual-providers.effectlib.iterations", 8)));
        parameters.set("period", 2);
        parameters.set("particles", Math.max(1,
            owner.getConfig().getInt("visual-providers.effectlib.particles", 24)));
        parameters.set("radius", Math.min(4, Math.max(.8, boss.getWidth())));
        parameters.set("particle", "ELECTRIC_SPARK");
        try {
            start.invoke(manager, "Sphere", parameters, boss);
        } catch (ReflectiveOperationException ex) {
            owner.getLogger().warning("EffectLib rechazó un efecto; se desactiva el puente hasta reiniciar.");
            stop();
        }
    }

    /** Stop and dispose the EffectLib manager. Call from plugin onDisable. */
    static void stop() {
        if (manager != null && dispose != null)
            try { dispose.invoke(manager); } catch (ReflectiveOperationException ignored) {}
        manager = null;
        start = null;
        dispose = null;
        owner = null;
        ready.clear();
        CrateAuraBridge.stop();
    }
}
