package dev.cazajefes;

import java.lang.reflect.Method;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

final class ConfiguredText {
    private static final String DEFAULT_ATTACK = "<gold><bold><state></bold></gold> <dark_gray>·</dark_gray> <white><attack></white>";
    private final CazaJefesPlugin plugin;
    private Method placeholders;

    ConfiguredText(CazaJefesPlugin plugin) {
        this.plugin = plugin;
        var provider = plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI");
        if (provider == null || !provider.isEnabled()) return;
        try {
            placeholders = provider.getClass().getClassLoader().loadClass("me.clip.placeholderapi.PlaceholderAPI")
                .getMethod("setPlaceholders",OfflinePlayer.class,String.class);
        } catch (ReflectiveOperationException | LinkageError failure) {
            plugin.getLogger().warning("No se pudo activar PlaceholderAPI en mensajes de ataques.");
        }
    }

    Component attack(String state, String attackId, String attack, Player viewer) {
        String template = plugin.getConfig().getString("messages.attack",DEFAULT_ATTACK);
        if (placeholders != null && template.indexOf('%') >= 0) {
            try {
                template = (String) placeholders.invoke(null,viewer,template);
            } catch (ReflectiveOperationException | RuntimeException | LinkageError failure) {
                placeholders = null;
            }
        }
        return MiniMessage.miniMessage().deserialize(template,Placeholder.unparsed("state",plugin.languages.state(viewer,state)),
            Placeholder.unparsed("attack",plugin.languages.attack(viewer,attackId,attack)));
    }
}
