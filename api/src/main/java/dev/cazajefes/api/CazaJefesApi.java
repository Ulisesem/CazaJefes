package dev.cazajefes.api;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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

    record AttackInfo(String id,String name,String range,double damage,long cooldownTicks,int warningTicks) {}
    record CombatInfo(String state,String style,int phase,List<AttackInfo> attacks,List<String> signatureAttacks) {
        public CombatInfo {
            attacks=List.copyOf(attacks);
            signatureAttacks=List.copyOf(signatureAttacks);
        }
    }

    /** Reads the live AI for a spawned boss, using its entity UUID. */
    default Optional<CombatInfo> combat(UUID boss){return Optional.empty();}

    /** Queues a configured attack if the boss is ready and the target is valid. */
    default boolean requestAttack(UUID boss,String attackId,Player target){return false;}

    List<BossInfo> bosses();
    Optional<LivingEntity> spawn(String id, Location location, String summoner);
    Optional<ItemStack> item(ItemKind kind, String rareId, int level);
    PlayerInfo player(UUID uuid);
    Optional<String> encodeItem(ItemStack item);
    Optional<ItemStack> decodeItem(String snbt);
}
