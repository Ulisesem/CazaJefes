package dev.cazajefes.example;

import dev.cazajefes.api.CazaJefesApi;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class CazaJefesExample extends JavaPlugin {
    @Override public void onEnable() {
        var api = getServer().getServicesManager().load(CazaJefesApi.class);
        if (api == null) {
            getLogger().warning("CazaJefes todavía no publicó su API.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getCommand("cazajefesejemplo").setExecutor((sender,command,label,args) -> {
            if (!(sender instanceof Player player)) return true;
            if (args.length == 2 && args[0].equalsIgnoreCase("boss")) {
                var spawned = api.spawn(args[1],player.getLocation().add(player.getLocation().getDirection().setY(0).multiply(3)),player.getName());
                sender.sendMessage(spawned.isPresent() ? "Jefe invocado." : "ID o ubicación inválidos.");
            } else if (args.length == 1 && args[0].equalsIgnoreCase("item")) {
                api.item(CazaJefesApi.ItemKind.COMPASS,null,1).ifPresent(stack -> {
                    for (var excess : player.getInventory().addItem(stack).values())
                        player.getWorld().dropItemNaturally(player.getLocation(),excess);
                });
            } else {
                var stats = api.player(player.getUniqueId());
                sender.sendMessage("Victorias de " + stats.name() + " · " + stats.victories());
            }
            return true;
        });
    }
}
