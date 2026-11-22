package dev.cazajefes;

import java.util.*;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/** Diagnostic command: /ri rares-debug — inspects equipped items for rare infusion data. */
final class RareDiagnostics {

    private final CazaJefesPlugin plugin;
    private final RelicItems items;

    RareDiagnostics(CazaJefesPlugin plugin, RelicItems items) {
        this.plugin = plugin;
        this.items = items;
    }

    void inspect(CommandSender sender) {
        if (!AdminPermissions.allows(sender::hasPermission, "rareitemhunter.admin.diagnostico")) {
            sender.sendMessage("Necesitas permiso cazajefes.admin.");
            return;
        }
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Ejecuta /ri rares-debug como jugador.");
            return;
        }

        sender.sendMessage(Component.text("═══ Diagnóstico de Rares ═══", NamedTextColor.GREEN));
        sender.sendMessage(Component.text("Jugador: " + player.getName() + " | Saturación: " + String.format("%.1f", player.getSaturation()), NamedTextColor.AQUA));

        // Check armor pieces
        sender.sendMessage(Component.text("── Armadura ──", NamedTextColor.YELLOW));
        String[] slotNames = {"Botas", "Grebas", "Peto", "Casco"};
        ItemStack[] armor = player.getInventory().getArmorContents();
        for (int i = 0; i < armor.length; i++) {
            ItemStack piece = armor[i];
            if (piece == null || piece.getType().isAir()) {
                sender.sendMessage(Component.text("  " + slotNames[i] + ": vacío", NamedTextColor.GRAY));
                continue;
            }
            checkItem(sender, "  " + slotNames[i], piece);
        }

        // Check main hand
        sender.sendMessage(Component.text("── Mano principal ──", NamedTextColor.YELLOW));
        ItemStack main = player.getInventory().getItemInMainHand();
        if (main == null || main.getType().isAir()) {
            sender.sendMessage(Component.text("  Mano: vacío", NamedTextColor.GRAY));
        } else {
            checkItem(sender, "  Mano", main);
        }

        // Check off hand
        sender.sendMessage(Component.text("── Mano secundaria ──", NamedTextColor.YELLOW));
        ItemStack off = player.getInventory().getItemInOffHand();
        if (off == null || off.getType().isAir()) {
            sender.sendMessage(Component.text("  Off-hand: vacío", NamedTextColor.GRAY));
        } else {
            checkItem(sender, "  Off-hand", off);
        }

        // Check armor rare levels
        sender.sendMessage(Component.text("── Niveles de armadura detectados ──", NamedTextColor.YELLOW));
        String[] armorRares = {"hardy", "fly", "regeneration", "durability", "strength", "aura_esporas", "nofalldamage", "doublejump", "saturacion", "clarity", "hungersteal"};
        boolean anyArmor = false;
        for (String id : armorRares) {
            int level = items.armorLevel(player, id);
            if (level > 0) {
                anyArmor = true;
                sender.sendMessage(Component.text("  " + id + ": " + level, NamedTextColor.GREEN));
            }
        }
        if (!anyArmor) {
            sender.sendMessage(Component.text("  Ninguna rare de armadura detectada", NamedTextColor.RED));
        }

        // Check melee rare levels
        sender.sendMessage(Component.text("── Rares de melee detectadas ──", NamedTextColor.YELLOW));
        Map<String, Integer> meleePowers = items.activeProperties(main);
        if (meleePowers.isEmpty()) {
            sender.sendMessage(Component.text("  Ninguna rare de melee detectada", NamedTextColor.RED));
        } else {
            for (Map.Entry<String, Integer> entry : meleePowers.entrySet()) {
                sender.sendMessage(Component.text("  " + entry.getKey() + ": " + entry.getValue(), NamedTextColor.GREEN));
            }
        }

        sender.sendMessage(Component.text("═══ Fin del diagnóstico ═══", NamedTextColor.GREEN));
    }

    private void checkItem(CommandSender sender, String label, ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            sender.sendMessage(Component.text(label + ": sin meta", NamedTextColor.RED));
            return;
        }

        var meta = item.getItemMeta();
        var pdc = meta.getPersistentDataContainer();

        // Check for rare_enchants key
        String rareEnchants = pdc.get(new NamespacedKey(plugin, "rare_enchants"), PersistentDataType.STRING);
        String rareLevels = pdc.get(new NamespacedKey(plugin, "rare_levels"), PersistentDataType.STRING);
        String rareBook = pdc.get(new NamespacedKey(plugin, "rare_book"), PersistentDataType.STRING);

        if (rareEnchants == null && rareLevels == null && rareBook == null) {
            sender.sendMessage(Component.text(label + ": " + item.getType() + " — SIN PDC de rares", NamedTextColor.RED));
            return;
        }

        StringBuilder info = new StringBuilder();
        info.append(label).append(": ").append(item.getType());
        if (rareEnchants != null) {
            info.append(" | rares: ").append(rareEnchants);
        }
        if (rareLevels != null) {
            info.append(" | niveles: ").append(rareLevels);
        }
        if (rareBook != null) {
            info.append(" | libro: ").append(rareBook);
        }

        // Check active properties
        Map<String, Integer> active = items.activeProperties(item);
        if (active.isEmpty()) {
            sender.sendMessage(Component.text(info.toString() + " — ¡PERO activeProperties() devuelve vacío!", NamedTextColor.RED));
        } else {
            sender.sendMessage(Component.text(info.toString() + " — activas: " + active, NamedTextColor.GREEN));
        }
    }
}
