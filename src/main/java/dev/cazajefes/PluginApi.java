package dev.cazajefes;

import dev.cazajefes.integration.NbtItems;

import dev.cazajefes.api.CazaJefesApi;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

final class PluginApi implements CazaJefesApi {
    private final CazaJefesPlugin plugin;

    PluginApi(CazaJefesPlugin plugin) {
        this.plugin = plugin;
    }

    private static void requireServerThread() {
        if (!Bukkit.isPrimaryThread()) throw new IllegalStateException("Usa la API desde el hilo de Paper.");
    }

    @Override public List<BossInfo> bosses() {
        requireServerThread();
        return BossCatalog.all().stream()
            .map(boss -> new BossInfo(boss.id(),boss.name(),boss.mini()?"MINI":boss.tier().name(),boss.entityType(),boss.mini()))
            .toList();
    }

    @Override public Optional<LivingEntity> spawn(String id, Location location, String summoner) {
        requireServerThread();
        if (id == null || location == null || location.getWorld() == null || !location.isChunkLoaded()) return Optional.empty();
        return Optional.ofNullable(plugin.bosses().spawnBoss(id,location.clone(),Objects.requireNonNullElse(summoner,"API")));
    }

    @Override public Optional<ItemStack> item(ItemKind kind, String rareId, int level) {
        requireServerThread();
        var items = plugin.items();
        return Optional.ofNullable(switch (kind) {
            case COMPASS -> items.compass();
            case ESSENCE -> items.essence();
            case FRAGMENT -> items.fragment(level);
            case RARE_BOOK -> rareId == null ? null : items.commandBook(rareId,level);
            case RARE_ESSENCE -> rareId == null ? null : items.commandEssence(rareId,level);
        });
    }

    @Override public PlayerInfo player(UUID uuid) {
        requireServerThread();
        var row = plugin.statistics().get(Objects.requireNonNull(uuid));
        if (row != null) return new PlayerInfo(uuid,row.name(),row.total(),row.bosses(),row.categories());
        var player = Bukkit.getOfflinePlayer(uuid);
        return new PlayerInfo(uuid,Objects.requireNonNullElse(player.getName(),""),0,Map.of(),Map.of());
    }

    @Override public Optional<String> encodeItem(ItemStack item) {
        requireServerThread();
        return Optional.ofNullable(NbtItems.encode(item));
    }

    @Override public Optional<ItemStack> decodeItem(String snbt) {
        requireServerThread();
        return Optional.ofNullable(NbtItems.decode(snbt));
    }
}
