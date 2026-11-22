package dev.cazajefes;

import com.google.gson.JsonObject;
import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.potion.*;

/** Boss equivalents of movement, hunger stealing and bounded lifesteal. */
final class BossBoons implements Listener {
    record Levels(int haste, int strength, int cannibalism, int vampirism, double maxSpeed) {}
    private final CazaJefesPlugin plugin;
    private final Map<UUID, Long> hungerReady = new HashMap<>();
    private final Map<UUID, Long> healReady = new HashMap<>();

    BossBoons(CazaJefesPlugin plugin) {
        this.plugin = plugin;
    }

    static JsonObject defaults() {
        JsonObject json = new JsonObject();
        json.addProperty("haste", 6);
        json.addProperty("strength", 3);
        json.addProperty("cannibalism", 3);
        json.addProperty("vampirism", 5);
        json.addProperty("maxPursuitSpeed", .42);
        return json;
    }

    static Levels levels(BossCatalog.Definition boss) {
        JsonObject config = boss == null ? new JsonObject() : boss.custom().options().boons();
        return new Levels(
            level(config, "haste", 6, 7),
            level(config, "strength", 3, 10),
            level(config, "cannibalism", 3, 3),
            level(config, "vampirism", 5, 5),
            number(config, "maxPursuitSpeed", .42, .12, .65)
        );
    }

    private static int level(JsonObject json, String key, int fallback, int max) {
        return (int) number(json, key, fallback, 0, max);
    }

    private static double number(JsonObject json, String key, double fallback, double min, double max) {
        try {
            double value = json.get(key).getAsDouble();
            return Double.isFinite(value) ? Math.clamp(value, min, max) : fallback;
        } catch (RuntimeException missing) {
            return fallback;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void accepted(EntityDamageByEntityEvent event) {
        if (event.getFinalDamage() <= 0 || !(event.getEntity() instanceof Player victim)
            || !CombatGuard.eligible(victim)) return;
        Entity source = event.getDamager();
        if (source instanceof Projectile projectile && projectile.getShooter() instanceof Entity owner)
            source = owner;
        if (!(source instanceof LivingEntity boss) || !BossIntegrity.boss(boss) || boss.isDead()
            || !ArenaBoundary.allows(boss, victim.getLocation())) return;
        var definition = BossCatalog.get(boss.getPersistentDataContainer().get(
            new NamespacedKey(plugin, "boss_type"), org.bukkit.persistence.PersistentDataType.STRING));
        var levels = levels(definition);
        UUID id = boss.getUniqueId();
        long now = System.currentTimeMillis();
        double heal = 0;
        if (levels.cannibalism() > 0 && now >= hungerReady.getOrDefault(id, 0L) && victim.getFoodLevel() > 0) {
            int stolen = Math.min(levels.cannibalism(), victim.getFoodLevel());
            victim.setFoodLevel(victim.getFoodLevel() - stolen);
            victim.setSaturation(Math.min(victim.getSaturation(), victim.getFoodLevel()));
            hungerReady.put(id, now + 10000);
            heal += stolen * .5;
        }
        if (levels.vampirism() > 0 && now >= healReady.getOrDefault(id, 0L)
            && Math.random() < levels.vampirism() * .05 && !plugin.items().armorCounter(victim, "garlic")) {
            healReady.put(id, now + 10000);
            heal += Math.min(levels.vampirism(), event.getFinalDamage() * .25);
            CosmeticFX.emit(Particle.DUST, victim.getLocation().add(0, 1, 0), 6, .2, .35, .2, 0,
                new Particle.DustOptions(Color.RED, .85f));
        }
        if (heal <= 0) return;
        final double amount = Math.min(6.5, heal);
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (!boss.isValid() || boss.isDead()) return;
            var regain = new EntityRegainHealthEvent(boss, amount, EntityRegainHealthEvent.RegainReason.CUSTOM);
            if (regain.callEvent() && Double.isFinite(regain.getAmount()) && regain.getAmount() > 0) {
                boss.setHealth(Math.min(boss.getMaxHealth(), boss.getHealth() + Math.min(amount, regain.getAmount())));
            }
        });
    }

    @EventHandler
    public void removed(EntityRemoveEvent event) {
        UUID id = event.getEntity().getUniqueId();
        hungerReady.remove(id);
        healReady.remove(id);
    }
}
