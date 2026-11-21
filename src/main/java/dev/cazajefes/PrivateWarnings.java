package dev.cazajefes;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Envía action bars a jugadores usando PacketEvents para evitar detección de anti-cheats.
 * Si PacketEvents no está disponible o falla, usa el método nativo de Paper como fallback.
 * La reflexión se resuelve una vez en load(); send() reutiliza los objetos reflejados.
 */
final class PrivateWarnings {

    /**
     * Contiene las referencias reflejadas a PacketEvents.
     * Se resuelve una vez en load() y se reutiliza en cada envío.
     */
    record Hook(Plugin provider, Object manager, Method send, Constructor<?> actionBar) {}

    static Hook hook;

    /**
     * Inicializa la integración con PacketEvents vía reflexión.
     * Si falla, los avisos usarán el método nativo de Paper.
     *
     * @param owner el plugin propietario
     */
    static void load(Plugin owner) {
        hook = null;
        Plugin provider = owner.getServer().getPluginManager().getPlugin("packetevents");
        if (provider == null || !provider.isEnabled()) return;
        try {
            var loader = provider.getClass().getClassLoader();
            String root = "com.github.retrooper.packetevents.";

            // Obtener el API de PacketEvents
            var apiClass = loader.loadClass(root + "PacketEvents");
            Method getApi = apiClass.getMethod("getAPI");

            // Obtener el PlayerManager
            var managerMethod = getApi.getReturnType().getMethod("getPlayerManager");
            Object playerManager = managerMethod.invoke(getApi.invoke(null));

            // Obtener el método sendPacket(Player, PacketWrapper)
            var wrapperClass = loader.loadClass(root + "wrapper.PacketWrapper");
            Method sendPacket = managerMethod.getReturnType()
                    .getMethod("sendPacket", Player.class, wrapperClass);

            // Obtener el constructor de WrapperPlayServerActionBar(Component)
            var actionBarClass = loader.loadClass(root + "wrapper.play.server.WrapperPlayServerActionBar");
            Constructor<?> actionBarConstructor = actionBarClass.getConstructor(Component.class);

            hook = new Hook(provider, playerManager, sendPacket, actionBarConstructor);
        } catch (ReflectiveOperationException | LinkageError | RuntimeException error) {
            owner.getLogger().warning("PacketEvents no disponible para avisos; se usará Paper.");
        }
    }

    /**
     * Envía un action bar a un jugador.
     * Intenta usar PacketEvents primero; si falla, usa el método nativo.
     *
     * @param player  el jugador receptor
     * @param message el componente de texto a mostrar
     */
    static void send(Player player, Component message) {
        if (hook != null && hook.provider().isEnabled()) {
            try {
                // Crear el wrapper del action bar y enviarlo
                Object actionBar = hook.actionBar().newInstance(message);
                hook.send().invoke(hook.manager(), player, actionBar);
                return;
            } catch (ReflectiveOperationException | LinkageError | RuntimeException error) {
                // PacketEvents falló en runtime; desactivar y usar fallback
                hook = null;
            }
        }
        // Fallback nativo: Paper action bar
        player.sendActionBar(message);
    }
}
