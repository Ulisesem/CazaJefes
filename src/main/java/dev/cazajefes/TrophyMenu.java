package dev.cazajefes;
import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
final class TrophyMenu implements Listener {
 private final CazaJefesPlugin plugin;private final RelicItems items;
 TrophyMenu(CazaJefesPlugin p,RelicItems items){plugin=p;this.items=items;}
 private static final class View implements InventoryHolder {
  Inventory inventory;final boolean guide;final int page;final MenuActionGate gate=new MenuActionGate();
  View(boolean guide,int page){this.guide=guide;this.page=page;}public Inventory getInventory(){return inventory;}
 }
 void open(Player player,boolean guide,int page){
  List<BossCatalog.Definition> bosses=BossCatalog.all().stream().filter(b->!b.mini()).sorted(Comparator.comparingInt((BossCatalog.Definition b)->b.tier().ordinal()).thenComparing(BossCatalog.Definition::name)).toList();
  int pages=Math.max(1,(bosses.size()+27)/28);page=Math.max(0,Math.min(page,pages-1));View view=new View(guide,page);
  view.inventory=Bukkit.createInventory(view,54,Component.text(guide?"✦ GUÍA DE MATERIALES":"✦ CORAZONES · "+(page+1)+"/"+pages,NamedTextColor.GOLD));Inventory inv=view.inventory;PremiumStyle.frame(inv,Material.ORANGE_STAINED_GLASS_PANE);
  inv.setItem(4,PremiumStyle.tile(Material.HEART_OF_THE_SEA,guide?"EL CAMINO DEL CAZADOR":"CORAZONES ENCANTADOS",NamedTextColor.GOLD,true,guide?"Materiales reales del sistema de progresión":"RARES de boss • no se fabrican",guide?"Cada material conserva su identificación propia":"Probabilidad: "+String.format(java.util.Locale.ROOT,"%.2f",100*LootRules.probability(plugin.getConfig().getDouble("loot.boss-heart-chance",0.01)))+" % por boss principal"));
  if(guide){
   inv.setItem(20,items.fragment(1));inv.setItem(22,items.tribute("Cazador"));inv.setItem(24,items.compass());
   int[] slots={28,29,30,32,33,34};int i=0;for(var tier:BossCatalog.Tier.values())inv.setItem(slots[i++],items.crystal(tier,1));
   inv.setItem(40,PremiumStyle.tile(Material.BOOK,"ORDEN DE PROGRESIÓN",NamedTextColor.AQUA,false,"Minijefes → tributos y fragmentos","Cristales → equipo y forjas","Los corazones solo caen de bosses principales","El brillo no añade encantamientos artificiales"));
  }else{
   for(int i=page*28;i<Math.min(bosses.size(),(page+1)*28);i++)inv.setItem(PremiumStyle.GRID[i-page*28],items.bossHeart(bosses.get(i)));
   if(page>0)inv.setItem(45,PremiumStyle.tile(Material.ARROW,"ANTERIOR",NamedTextColor.GOLD,false));if(page+1<pages)inv.setItem(53,PremiumStyle.tile(Material.ARROW,"SIGUIENTE",NamedTextColor.GOLD,false));
  }
  inv.setItem(49,PremiumStyle.tile(Material.NETHER_STAR,"VOLVER A LA FORJA",NamedTextColor.GOLD,true));player.openInventory(inv);
 }
 @EventHandler public void click(InventoryClickEvent event){
  if(!(event.getView().getTopInventory().getHolder() instanceof View view))return;event.setCancelled(true);int slot=event.getRawSlot();if(!(event.getWhoClicked() instanceof Player player)||!MenuActions.isActivation(event.getClick(),slot,54))return;
  MenuActions.dispatch(plugin,player,view,view.gate,()->{if(slot==49)plugin.getServer().dispatchCommand(player,"rares");else if(!view.guide&&slot==45)open(player,false,view.page-1);else if(!view.guide&&slot==53)open(player,false,view.page+1);});
 }
 @EventHandler public void drag(InventoryDragEvent e){if(e.getView().getTopInventory().getHolder() instanceof View)e.setCancelled(true);}
}
