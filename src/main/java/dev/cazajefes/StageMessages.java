package dev.cazajefes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;

/**
 * Formatea los mensajes de cambio de etapa (stage) de un boss.
 * Soporta traducción opcional vía Languages; si no está disponible, usa fallback en español.
 */
final class StageMessages {

    /** Símbolos romanos para cada etapa. */
    static final String[] ROMAN = {"", "Ⅰ", "Ⅱ", "Ⅲ"};

    /** Títulos por etapa (fallback en español). */
    static final String[] TITLES = {"", "ᴀᴄᴇᴄʜᴏ", "ʀᴜᴘᴛᴜʀᴀ", "ꜰᴜʀɪᴀ ꜰɪɴᴀʟ"};

    /** Líneas descriptivas por etapa (fallback en español). */
    static final String[] LINES = {"", "El combate comienza", "Su poder está aumentando", "¡Su poder ha despertado!"};

    /** Etiqueta base para "etapa". */
    static final String LABEL = "ᴇᴛᴀᴘᴀ";

    /** Borde decorativo del mensaje. */
    static final String BORDER = "━━━━ ";

    /**
     * Formatea el mensaje de etapa sin traducción (fallback en español).
     */
    static Component format(BossCatalog.Definition boss, int stage) {
        return format(boss, stage, null, null);
    }

    /**
     * Formatea el mensaje de etapa con traducción opcional.
     *
     * @param boss      definición del boss
     * @param stage     número de etapa (1, 2, o 3)
     * @param languages sistema de idiomas (puede ser null para fallback)
     * @param player    jugador para detectar idioma (puede ser null)
     * @return Component formateado con el mensaje de etapa
     */
    static Component format(BossCatalog.Definition boss, int stage, Languages languages, Player player) {
        // Obtener textos con fallback seguro
        int stageIndex = Math.min(Math.max(stage, 1), 3);
        String roman = ROMAN[stageIndex];
        String title = TITLES[stageIndex];
        String label = LABEL;
        String name = boss.name();
        String line = LINES[stageIndex];

        // Aplicar traducción si está disponible
        if (languages != null) {
            label = languages.text(player, "stage.label", label);
            name = languages.boss(player, boss, "name");
            title = languages.text(player, "stage." + stageIndex + ".title", title);
            line = languages.text(player, "stage." + stageIndex + ".line", line);
        }

        // Construir el componente con formato
        return Component.empty()
                .decorate(TextDecoration.BOLD)
                .decoration(TextDecoration.ITALIC, false)
                .append(Component.text(BORDER + label + " " + roman + " / Ⅲ " + BORDER, NamedTextColor.GOLD))
                .append(Component.newline())
                .append(Component.text(name, NamedTextColor.AQUA))
                .append(Component.text(" · " + title, NamedTextColor.GOLD))
                .append(Component.newline())
                .append(Component.text(line, NamedTextColor.AQUA));
    }
}
