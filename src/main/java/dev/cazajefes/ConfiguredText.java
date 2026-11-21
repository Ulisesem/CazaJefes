package dev.cazajefes;

import java.lang.reflect.Method;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * Formatea mensajes de ataque usando MiniMessage templates configurables.
 * Soporta PlaceholderAPI y placeholders personalizados ({state}, {attack}).
 * El template se cachea para evitar lecturas del config en cada llamada.
 */
final class ConfiguredText {

    /** Template por defecto para mensajes de ataque. */
    static final String DEFAULT_ATTACK = "<gold><bold><state></bold></gold> <dark_gray>·</dark_gray> <white><attack></white>";

    private final CazaJefesPlugin plugin;
    private final MiniMessage miniMessage;
    private Method placeholders;
    private String cachedTemplate;

    ConfiguredText(CazaJefesPlugin plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
        this.cachedTemplate = loadTemplate();
        initPlaceholderAPI();
    }

    /** Carga el template del config. */
    String loadTemplate() {
        return plugin.getConfig().getString("messages.attack", DEFAULT_ATTACK);
    }

    /** Inicializa la integración con PlaceholderAPI vía reflexión. */
    void initPlaceholderAPI() {
        var provider = plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI");
        if (provider == null || !provider.isEnabled()) return;
        try {
            placeholders = provider.getClass().getClassLoader()
                    .loadClass("me.clip.placeholderapi.PlaceholderAPI")
                    .getMethod("setPlaceholders", OfflinePlayer.class, String.class);
        } catch (ReflectiveOperationException | LinkageError failure) {
            plugin.getLogger().warning("No se pudo activar PlaceholderAPI en mensajes de ataques.");
        }
    }

    /**
     * Notifica que el config cambió y el template debe recargarse.
     * Llamar desde onConfigReload o similar si se implementa.
     */
    void reloadTemplate() {
        this.cachedTemplate = loadTemplate();
    }

    /**
     * Formatea un mensaje de ataque con traducción y placeholders.
     *
     * @param state    estado del ataque (ej. "PREPARACIÓN", "IMPACTO")
     * @param attackId ID del ataque para traducción
     * @param attack   nombre del ataque para traducción
     * @param viewer   jugador que ve el mensaje (para idioma y PlaceholderAPI)
     * @return Component formateado con MiniMessage
     */
    Component attack(String state, String attackId, String attack, Player viewer) {
        String template = cachedTemplate;
        if (placeholders != null && template.indexOf('%') >= 0) {
            try {
                template = (String) placeholders.invoke(null, viewer, template);
            } catch (ReflectiveOperationException | RuntimeException | LinkageError failure) {
                placeholders = null;
            }
        }
        return miniMessage.deserialize(template,
                Placeholder.unparsed("state", plugin.languages.state(viewer, state)),
                Placeholder.unparsed("attack", plugin.languages.attack(viewer, attackId, attack)));
    }
}
