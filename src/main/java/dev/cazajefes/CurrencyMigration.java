package dev.cazajefes;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
final class CurrencyMigration implements Listener {
 private final CazaJefesPlugin plugin;private final RelicItems items;
 CurrencyMigration(CazaJefesPlugin plugin,RelicItems items){this.plugin=plugin;this.items=items;}
 @EventHandler public void open(InventoryOpenEvent e){if(e.getPlayer() instanceof Player p)items.migrateCurrencies(p);}
 @EventHandler public void join(PlayerJoinEvent e){plugin.getServer().getScheduler().runTask(plugin,()->{if(e.getPlayer().isOnline())items.migrateCurrencies(e.getPlayer());});}
}
