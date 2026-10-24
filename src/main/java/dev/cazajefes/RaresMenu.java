package dev.cazajefes;

import java.util.*;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

/** All views and purchases use the registered recipe, never a second cost table. */
final class RaresMenu implements CommandExecutor, Listener {
    private final CazaJefesPlugin plugin;
    private final RelicItems items;
    RaresMenu(CazaJefesPlugin plugin, RelicItems items) { this.plugin = plugin; this.items = items; }
    private static final class Menu implements InventoryHolder {
        Inventory inventory;
        final MenuActionGate gate = new MenuActionGate();
        final int page;
        final List<ShapedRecipe> recipes;
        final ShapedRecipe detail;
        Menu(int page, List<ShapedRecipe> recipes, ShapedRecipe detail) { this.page=page; this.recipes=recipes; this.detail=detail; }
        public Inventory getInventory() { return inventory; }
    }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) open(player, 0, null);
        else sender.sendMessage("Este menú requiere un jugador.");
        return true;
    }
    private List<ShapedRecipe> recipes() {
        List<ShapedRecipe> result = new ArrayList<>();
        Bukkit.recipeIterator().forEachRemaining(recipe -> {
            if (recipe instanceof ShapedRecipe shaped && shaped.getKey().getNamespace().equals("cazajefes")) result.add(shaped);
        });
        result.sort(Comparator.comparing(r -> r.getKey().getKey()));
        return result;
    }
    private ItemStack button(Material material, String name) {
        ItemStack item = new ItemStack(material); ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name)); item.setItemMeta(meta); return item;
    }
    private void open(Player player, int page, ShapedRecipe detail) {
        List<ShapedRecipe> recipes = recipes();
        page = Math.max(0, Math.min(page, Math.max(0, (recipes.size()-1)/45)));
        Menu menu = new Menu(page, recipes, detail);
        menu.inventory = Bukkit.createInventory(menu, 54, Component.text(detail == null ? "Rares · Recetas · " + (page+1) : "Forja de rares"));
        if (detail == null) {
            for (int i=0; i<45 && page*45+i<recipes.size(); i++) menu.inventory.setItem(i, recipes.get(page*45+i).getResult());
            menu.inventory.setItem(45, button(Material.ARROW, "Página anterior"));
            menu.inventory.setItem(53, button(Material.ARROW, "Página siguiente"));
        } else {
            String[] shape = detail.getShape(); Map<Character, RecipeChoice> choices = detail.getChoiceMap();
            for (int row=0; row<shape.length; row++) for (int col=0; col<shape[row].length(); col++) {
                RecipeChoice choice = choices.get(shape[row].charAt(col));
                if (choice != null) menu.inventory.setItem(10+row*9+col, choice.getItemStack());
            }
            menu.inventory.setItem(25, detail.getResult());
            menu.inventory.setItem(40, button(Material.ANVIL, "Fabricar 1 · consume materiales del inventario"));
            menu.inventory.setItem(49, button(Material.ARROW, "Volver"));
        }
        player.openInventory(menu.inventory);
    }
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=false)
    public void click(InventoryClickEvent event) {
        if (!(event.getView().getTopInventory().getHolder() instanceof Menu menu)) return;
        event.setCancelled(true);
        int slot=event.getRawSlot();
        if (!(event.getWhoClicked() instanceof Player player) || !MenuActions.isActivation(event.getClick(),slot,event.getView().getTopInventory().getSize())) return;
        MenuActions.dispatch(plugin, player, menu, menu.gate, () -> activate(player,menu,slot));
    }
    private void activate(Player player, Menu menu, int slot) {
        if (menu.detail == null) {
            if (slot==45) open(player, menu.page-1, null);
            else if (slot==53) open(player, menu.page+1, null);
            else if (slot<45 && menu.page*45+slot<menu.recipes.size()) open(player,menu.page,menu.recipes.get(menu.page*45+slot));
        } else if (slot==49) open(player,menu.page,null);
        else if (slot==40) craft(player,menu.detail);
    }
    @EventHandler public void drag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder() instanceof Menu) event.setCancelled(true);
    }
    private void craft(Player player, ShapedRecipe shown) {
        if (!player.hasPermission("rareitemhunter.hunter.craft")) { player.sendMessage("No tienes permiso para fabricar."); return; }
        // A reload invalidates old menu objects, so use the current registry entry.
        if (!(Bukkit.getRecipe(shown.getKey()) instanceof ShapedRecipe recipe)) { player.closeInventory(); return; }
        ItemStack[] original=player.getInventory().getStorageContents();
        ItemStack[] planned=Arrays.stream(original).map(i -> i==null ? null : i.clone()).toArray(ItemStack[]::new);
        Map<Character, RecipeChoice> choices=recipe.getChoiceMap();
        java.util.List<RecipeChoice> costs = new java.util.ArrayList<>();
        for (String row:recipe.getShape()) for (char symbol:row.toCharArray()) if (choices.get(symbol)!=null) costs.add(choices.get(symbol));
        costs.sort(java.util.Comparator.comparingInt(c -> c instanceof RecipeChoice.ExactChoice ? 0 : 1));
        java.util.List<java.util.function.IntPredicate> requirements = new java.util.ArrayList<>();
        for (RecipeChoice choice:costs) requirements.add(slot -> original[slot]!=null && choice.test(original[slot])
            && (!(choice instanceof RecipeChoice.MaterialChoice) || items.customId(original[slot])==null));
        int[] counts=java.util.Arrays.stream(original).mapToInt(i -> i==null ? 0 : i.getAmount()).toArray();
        int[] remainder=CraftPlan.remaining(counts,requirements);
        if (remainder==null) { player.sendMessage("Faltan ingredientes. Los objetos de jefe deben coincidir con la receta."); return; }
        for (int slot=0;slot<planned.length;slot++) {
            if (remainder[slot]==0) planned[slot]=null;
            else planned[slot].setAmount(remainder[slot]);
        }
        ItemStack result=recipe.getResult().clone(); int remaining=result.getAmount();
        for (int slot=0; slot<planned.length && remaining>0; slot++) {
            ItemStack stack=planned[slot];
            if (stack!=null && !stack.isSimilar(result)) continue;
            int amount=Math.min(remaining, result.getMaxStackSize()-(stack==null ? 0 : stack.getAmount()));
            if (amount<=0) continue;
            if (stack==null) { stack=result.clone(); stack.setAmount(amount); planned[slot]=stack; }
            else stack.setAmount(stack.getAmount()+amount); remaining-=amount;
        }
        if (remaining>0) { player.sendMessage("No hay espacio para el resultado."); return; }
        player.getInventory().setStorageContents(planned);
        player.playSound(player.getLocation(),Sound.BLOCK_ANVIL_USE,0.5f,1.2f);
        player.sendMessage("Objeto fabricado.");
    }
}

