package dev.cazajefes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

/**
 * Centraliza los avisos de combate (action bars) enviados a los jugadores.
 * Un jugador tiene un solo aviso activo a la vez; los avisos expiran tras 3 segundos.
 * Se ejecuta cada 5 ticks para enviar y limpiar avisos.
 */
final class BossWarnings {

    /** Duración en milisegundos de un aviso antes de expirar. */
    static final long NOTICE_DURATION_MS = 3000;

    /** Intervalo en ticks del scheduler global. */
    static final long TICK_INTERVAL = 5;

    record Notice(Component text, long until, boolean personal) {}

    static final Map<UUID, Notice> notices = new HashMap<>();
    static org.bukkit.scheduler.BukkitTask timer;

    /** Inicia el scheduler global de avisos. Limpia cualquier estado previo. */
    static void start(org.bukkit.plugin.java.JavaPlugin plugin) {
        clear();
        timer = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            long now = System.currentTimeMillis();
            BossEffects.prune();
            var it = notices.entrySet().iterator();
            while (it.hasNext()) {
                var entry = it.next();
                var p = plugin.getServer().getPlayer(entry.getKey());
                if (p == null || !p.isOnline()) {
                    it.remove();
                    continue;
                }
                if (now >= entry.getValue().until()) {
                    p.sendActionBar(Component.empty());
                    it.remove();
                } else if (entry.getValue().personal()) {
                    PrivateWarnings.send(p, entry.getValue().text());
                } else {
                    p.sendActionBar(entry.getValue().text());
                }
            }
        }, TICK_INTERVAL, TICK_INTERVAL);
    }

    /** Verifica si un jugador tiene un aviso activo en este momento. */
    static boolean active(Player p) {
        var n = notices.get(p.getUniqueId());
        return n != null && System.currentTimeMillis() < n.until();
    }

    /** Envía un aviso público (action bar normal) a un jugador. */
    static void send(Player p, Component text) {
        notices.put(p.getUniqueId(), new Notice(text, System.currentTimeMillis() + NOTICE_DURATION_MS, false));
        p.sendActionBar(text);
    }

    /** Envía un aviso privado (vía PacketEvents si disponible) a un jugador. */
    static void personal(Player p, Component text) {
        notices.put(p.getUniqueId(), new Notice(text, System.currentTimeMillis() + NOTICE_DURATION_MS, true));
        PrivateWarnings.send(p, text);
    }

    /** Cancela el scheduler y limpia todos los avisos activos. */
    static void clear() {
        if (timer != null) timer.cancel();
        timer = null;
        notices.clear();
    }
}
