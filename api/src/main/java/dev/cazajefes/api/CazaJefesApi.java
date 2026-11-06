package dev.cazajefes.api;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

/** Obtain from Bukkit ServicesManager. Call on the server thread. */
public interface CazaJefesApi {
    enum ItemKind { COMPASS, ESSENCE, FRAGMENT, RARE_BOOK, RARE_ESSENCE }

    record BossInfo(String id, String name, String category, EntityType type, boolean mini) {}
    record PlayerInfo(UUID uuid, String name, long victories, Map<String,Long> bosses, Map<String,Long> categories) {
        public PlayerInfo {
            bosses = Map.copyOf(bosses);
            categories = Map.copyOf(categories);
        }
    }

    List<BossInfo> bosses();
    Optional<LivingEntity> spawn(String id, Location location, String summoner);
    Optional<ItemStack> item(ItemKind kind, String rareId, int level);
    PlayerInfo player(UUID uuid);
    Optional<String> encodeItem(ItemStack item);
    Optional<ItemStack> decodeItem(String snbt);
}
