package dev.cazajefes.ai;

import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

/**
 * Configurable ranged-to-melee weapon swap for boss mobs.
 *
 * When a ranged boss (skeleton, pillager, drowned, etc.) detects the player
 * is too close, it swaps its ranged weapon for a melee weapon and fights
 * with it. When the player retreats, the boss swaps back.
 *
 * All thresholds are configurable via the boss JSON profile under the
 * "rangedMeleeSwap" key.
 *
 * Example JSON configuration:
 * <pre>{
 *   "rangedMeleeSwap": {
 *     "enabled": true,
 *     "meleeRange": 3.0,
 *     "rangedRange": 6.0,
 *     "meleeWeapon": "IRON_SWORD",
 *     "rangedWeapon": "BOW"
 *   }
 * }</pre>
 *
 * - meleeRange: distance threshold to switch TO melee (player closer than this)
 * - rangedRange: distance threshold to switch back TO ranged (player farther than this, must be > meleeRange)
 * - meleeWeapon: material name for the melee weapon (default: matches boss tier)
 * - rangedWeapon: material name for the ranged weapon (default: auto-detected from entity type)
 */
public final class RangedMeleeSwap {

    /** Default configuration when no JSON is provided. */
    public static final Config DEFAULT = new Config(
        false,   // disabled by default; enable per-boss in JSON
        3.0,     // switch to melee when player is within 3 blocks
        6.0,     // switch back to ranged when player is beyond 6 blocks
        null,    // auto-detect melee weapon from boss tier
        null     // auto-detect ranged weapon from entity type
    );

    /** Configuration for the weapon swap behavior. */
    public record Config(boolean enabled, double meleeRange, double rangedRange,
                         String meleeWeapon, String rangedWeapon) {
        public Config {
            if (meleeRange <= 0) meleeRange = 3.0;
            if (rangedRange <= meleeRange) rangedRange = meleeRange + 3.0;
        }
    }

    private Config config = DEFAULT;
    private Mode currentMode = Mode.RANGED; // starts in ranged mode
    private Mode lastModeCheck = Mode.RANGED;  // avoids flickering swaps

    /** Current combat mode: RANGED (bow/crossbow/trident) or MELEE (sword/axe). */
    public enum Mode { RANGED, MELEE }

    /** Parse configuration from the boss JSON profile. */
    public static Config readConfig(JsonObject json) {
        if (json == null || !json.has("rangedMeleeSwap")) return DEFAULT;
        var obj = json.getAsJsonObject("rangedMeleeSwap");
        boolean enabled = obj.has("enabled") && obj.get("enabled").getAsBoolean();
        double meleeRange = obj.has("meleeRange") ? obj.get("meleeRange").getAsDouble() : 3.0;
        double rangedRange = obj.has("rangedRange") ? obj.get("rangedRange").getAsDouble() : 6.0;
        String meleeWeapon = obj.has("meleeWeapon") ? obj.get("meleeWeapon").getAsString() : null;
        String rangedWeapon = obj.has("rangedWeapon") ? obj.get("rangedWeapon").getAsString() : null;
        if (meleeRange <= 0) meleeRange = 3.0;
        if (rangedRange <= meleeRange) rangedRange = meleeRange + 3.0;
        return new Config(enabled, meleeRange, rangedRange, meleeWeapon, rangedWeapon);
    }

    /** Apply the configuration from the boss profile. */
    public void configure(Config cfg) {
        this.config = cfg;
    }

    /**
     * Check and apply the weapon swap if needed.
     * Returns the current mode after evaluation.
     */
    public Mode evaluate(CustomBoss boss, Player target) {
        if (!config.enabled() || target == null || !target.isValid() || boss.environment().weaponLocked(boss)) {
            return currentMode;
        }

        EntityEquipment equipment = boss.body().getEquipment();
        if (equipment == null) return currentMode;

        double distanceSquared = boss.body().getLocation().distanceSquared(target.getLocation());
        double distance = Math.sqrt(distanceSquared);

        // Determine desired mode based on distance with hysteresis
        Mode desired;
        if (distance < config.meleeRange()) {
            desired = Mode.MELEE;
        } else if (distance > config.rangedRange()) {
            desired = Mode.RANGED;
        } else {
            // In the hysteresis band — keep current mode to avoid flickering
            desired = currentMode;
        }

        // Only swap if the mode actually changed
        if (desired != currentMode) {
            currentMode = desired;
            performSwap(boss.body(), desired);
        }

        return currentMode;
    }

    /** Perform the actual weapon swap in the boss's equipment. */
    private void performSwap(LivingEntity entity, Mode mode) {
        EntityEquipment equipment = entity.getEquipment();
        if (equipment == null) return;

        // Save the current weapon before swapping
        ItemStack mainHand = equipment.getItemInMainHand();

        if (mode == Mode.MELEE) {
            // Switching to melee: put away ranged weapon, equip melee
            ItemStack melee = createMeleeWeapon(entity, config.meleeWeapon());
            equipment.setItemInMainHand(melee);
        } else {
            // Switching back to ranged: restore the ranged weapon
            ItemStack ranged = createRangedWeapon(entity, config.rangedWeapon());
            equipment.setItemInMainHand(ranged);
        }
    }

    /** Create a melee weapon, either from config or auto-detected from entity type. */
    private ItemStack createMeleeWeapon(LivingEntity entity, String configuredMaterial) {
        Material material;
        if (configuredMaterial != null && !configuredMaterial.isEmpty()) {
            try {
                material = Material.valueOf(configuredMaterial.toUpperCase());
            } catch (IllegalArgumentException e) {
                material = detectDefaultMeleeMaterial(entity);
            }
        } else {
            material = detectDefaultMeleeMaterial(entity);
        }
        return new ItemStack(material);
    }

    /** Create a ranged weapon, either from config or auto-detected from entity type. */
    private ItemStack createRangedWeapon(LivingEntity entity, String configuredMaterial) {
        Material material;
        if (configuredMaterial != null && !configuredMaterial.isEmpty()) {
            try {
                material = Material.valueOf(configuredMaterial.toUpperCase());
            } catch (IllegalArgumentException e) {
                material = detectDefaultRangedMaterial(entity);
            }
        } else {
            material = detectDefaultRangedMaterial(entity);
        }
        return new ItemStack(material);
    }

    /** Detect the default melee material based on entity type. */
    private Material detectDefaultMeleeMaterial(LivingEntity entity) {
        // Zombies and variants typically use swords or axes
        return switch (entity.getType()) {
            case ZOMBIE, ZOMBIE_VILLAGER, HUSK -> Material.IRON_SWORD;
            case ZOMBIFIED_PIGLIN, PIGLIN, PIGLIN_BRUTE -> Material.GOLDEN_AXE;
            case GIANT -> Material.STONE_AXE;
            default -> Material.IRON_SWORD;
        };
    }

    /** Detect the default ranged material based on entity type. */
    private Material detectDefaultRangedMaterial(LivingEntity entity) {
        return switch (entity.getType()) {
            case SKELETON, STRAY, BOGGED -> Material.BOW;
            case PILLAGER, VINDICATOR -> Material.CROSSBOW;
            case DROWNED -> Material.TRIDENT;
            default -> Material.BOW;
        };
    }

    /** Return the current combat mode. */
    public Mode mode() {
        return currentMode;
    }

    /** Reset the swap state (e.g., when boss respawns or target changes). */
    public void reset() {
        currentMode = Mode.RANGED;
        lastModeCheck = Mode.RANGED;
    }
}
