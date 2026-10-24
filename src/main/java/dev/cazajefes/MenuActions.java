package dev.cazajefes;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
final class MenuActions {
 static boolean isActivation(ClickType click, int rawSlot, int topSize) {
  return rawSlot>=0 && rawSlot<topSize && (click==ClickType.LEFT || click==ClickType.RIGHT);
 }
 static void dispatch(JavaPlugin plugin, Player player, InventoryHolder holder, MenuActionGate gate, Runnable action) {
  if (!gate.reserve()) return;
  try {
   plugin.getServer().getScheduler().runTask(plugin, () -> {
    try {
     if (player.isOnline() && player.getOpenInventory().getTopInventory().getHolder()==holder) action.run();
    } finally { gate.release(); }
   });
  } catch (RuntimeException failure) { gate.release(); throw failure; }
 }
}
