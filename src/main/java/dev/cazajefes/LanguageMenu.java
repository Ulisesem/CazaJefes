package dev.cazajefes;

import java.util.HashMap;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

final class LanguageMenu implements CommandExecutor, Listener {
    private final CazaJefesPlugin plugin;
    private final Languages languages;

    private static final class View implements InventoryHolder {
        private final Map<Integer,Languages.Language> choices=new HashMap<>();
        private final MenuActionGate gate=new MenuActionGate();
        private Inventory inventory;
        @Override public Inventory getInventory(){return inventory;}
    }

    LanguageMenu(CazaJefesPlugin plugin,Languages languages){
        this.plugin=plugin;
        this.languages=languages;
    }

    @Override public boolean onCommand(CommandSender sender,Command command,String label,String[] args){
        if(sender instanceof Player player)open(player);
        else sender.sendMessage(languages.text(null,"settings.console","Abre /settings dentro del juego."));
        return true;
    }

    private void open(Player player){
        var view=new View();
        var current=languages.of(player);
        view.inventory=Bukkit.createInventory(view,27,PremiumStyle.heading(
            languages.text(player,"settings.title","Idioma · CazaJefes"),NamedTextColor.GOLD));
        var border=PremiumStyle.tile(Material.GRAY_STAINED_GLASS_PANE," ",NamedTextColor.GRAY,false);
        for(int i=0;i<27;i++)if(i<9||i>=18||i==9||i==17)view.inventory.setItem(i,border);
        int[] slots={11,13,15};
        for(int i=0;i<slots.length;i++){
            var language=languages.all().get(i);
            boolean active=current.id().equals(language.id());
            var head=BossPortraits.texturedHead(language.textureHash());
            var meta=head.getItemMeta();
            meta.displayName(Component.text(language.name(),active?NamedTextColor.GREEN:NamedTextColor.GOLD));
            meta.lore(java.util.List.of(Component.text(active?languages.text(player,"settings.current","Idioma activo")
                :languages.text(player,"settings.choose","Clic para seleccionar"),NamedTextColor.GRAY)));
            if(active)meta.setEnchantmentGlintOverride(true);
            head.setItemMeta(meta);
            view.inventory.setItem(slots[i],head);
            view.choices.put(slots[i],language);
        }
        player.openInventory(view.inventory);
    }

    @EventHandler public void click(InventoryClickEvent event){
        if(!(event.getView().getTopInventory().getHolder() instanceof View view))return;
        event.setCancelled(true);
        int slot=event.getRawSlot();
        if(!(event.getWhoClicked() instanceof Player player)||!MenuActions.isActivation(event.getClick(),slot,27))return;
        var selected=view.choices.get(slot);
        if(selected==null)return;
        MenuActions.dispatch(plugin,player,view,view.gate,()->{
            languages.select(player,selected);
            player.sendMessage(Component.text(languages.text(player,"settings.changed","Idioma cambiado a {language}.")
                .replace("{language}",selected.name()),NamedTextColor.GREEN));
            open(player);
        });
    }

    @EventHandler public void drag(InventoryDragEvent event){
        if(event.getView().getTopInventory().getHolder() instanceof View)event.setCancelled(true);
    }
}
