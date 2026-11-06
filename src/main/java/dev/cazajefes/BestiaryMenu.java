package dev.cazajefes;
import java.util.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
final class BestiaryMenu implements CommandExecutor,Listener {
 private final CazaJefesPlugin plugin;private final BossManager manager;private final RelicItems items;
 BestiaryMenu(CazaJefesPlugin p,BossManager manager,RelicItems items){plugin=p;this.manager=manager;this.items=items;}
 static final class View implements InventoryHolder {
  Inventory inventory;final MenuActionGate gate=new MenuActionGate();final BestiaryCategory category;final int page;final BossCatalog.Definition detail;final Map<Integer,BossCatalog.Definition> bosses=new HashMap<>();
  View(BestiaryCategory c,int page,BossCatalog.Definition detail){category=c;this.page=page;this.detail=detail;}public Inventory getInventory(){return inventory;}
 }
 public boolean onCommand(CommandSender sender,Command command,String label,String[] args){if(sender instanceof Player p)open(p);else sender.sendMessage("Abre el bestiario dentro del juego.");return true;}
 void open(Player p){show(p,null,0,null);}
 private String label(Player viewer,String key,String fallback){return plugin.languages.text(viewer,"bestiary."+key,fallback);}
 private String categoryName(Player viewer,BestiaryCategory category){return label(viewer,"category."+category.name(),category.title);}
 private void show(Player player,BestiaryCategory category,int page,BossCatalog.Definition detail){
  List<BossCatalog.Definition> list=BossCatalog.all().stream().filter(b->category==null||category.matches(b)).sorted(Comparator.comparing(boss->plugin.languages.boss(player,boss,"name"))).toList();
  int pages=Math.max(1,(list.size()+27)/28);page=Math.max(0,Math.min(page,pages-1));
  View view=new View(category,page,detail);NamedTextColor color=category==null?NamedTextColor.GOLD:category.color;
  view.inventory=Bukkit.createInventory(view,54,PremiumStyle.heading(detail!=null?"✦ "+label(player,"detail","Archivo de cacería"):category==null?"✦ "+label(player,"title","BESTIARIO · LA GRIETA"):"✦ "+categoryName(player,category)+" · "+(page+1)+"/"+pages,color));
  Inventory inv=view.inventory;PremiumStyle.frame(inv,category==null?Material.YELLOW_STAINED_GLASS_PANE:switch(category){case MINI->Material.CYAN_STAINED_GLASS_PANE;case RARE->Material.LIME_STAINED_GLASS_PANE;case EPIC->Material.MAGENTA_STAINED_GLASS_PANE;case LEGENDARY->Material.ORANGE_STAINED_GLASS_PANE;case MYTHIC->Material.PURPLE_STAINED_GLASS_PANE;case ABYSSAL->Material.RED_STAINED_GLASS_PANE;});
  inv.setItem(4,PremiumStyle.tile(Material.ENCHANTED_BOOK,detail!=null?plugin.languages.boss(player,detail,"name"):category==null?"✦ "+label(player,"title","BESTIARIO · LA GRIETA")+" ✦":categoryName(player,category),color,true,BossCatalog.all().size()+" encuentros • categorías exclusivas","Selecciona un sello para explorar."));
  if(category==null){
   int[] slots={20,22,24,29,31,33};int index=0;
   for(var c:BestiaryCategory.values()){
    long count=BossCatalog.all().stream().filter(c::matches).count();
    inv.setItem(slots[index++],PremiumStyle.tile(c==BestiaryCategory.MINI?Material.HONEYCOMB:PremiumStyle.rankIcon(c.tier),"◆ "+categoryName(player,c).toUpperCase(java.util.Locale.ROOT)+" ◆",c.color,true,count+" encuentros registrados",c==BestiaryCategory.MINI?"Tributos, fragmentos y cristal común":"Equipo, núcleos y reliquias de categoría","Clic para abrir el capítulo"));
   }
   inv.setItem(49,PremiumStyle.tile(Material.COMPASS,"PROGRESIÓN DE LA CACERÍA",NamedTextColor.AQUA,false,"Minijefes → Raros → Épicos","Legendarios → Míticos → Abismales","Warden: desafío supremo de la categoría Abismal"));
  }else if(detail==null){
   for(int i=page*28;i<Math.min(list.size(),(page+1)*28);i++){
    var boss=list.get(i);int slot=PremiumStyle.GRID[i-page*28];
    inv.setItem(slot,BossPortraits.tile(boss,plugin.languages.boss(player,boss,"name"),color,"◆ "+categoryName(player,category),"Código: "+boss.id(),boss.mini()?"Tributos, fragmentos y cristal común":"Fragmentos y botín de categoría","Clic para consultar fases y botín"));view.bosses.put(slot,boss);
   }
   if(page>0)inv.setItem(45,PremiumStyle.tile(Material.ARROW,label(player,"previous","ANTERIOR"),color,false));
   if(page+1<pages)inv.setItem(53,PremiumStyle.tile(Material.ARROW,label(player,"next","SIGUIENTE"),color,false));
   inv.setItem(49,PremiumStyle.tile(Material.NETHER_STAR,label(player,"back","VOLVER A CATEGORÍAS"),NamedTextColor.GOLD,true));
  }else{
   inv.setItem(22,BossPortraits.tile(detail,plugin.languages.boss(player,detail,"name"),color,plugin.languages.boss(player,detail,"description"),categoryName(player,category),"ID: "+detail.id()));
   var ai=plugin.customEncounter.profile(detail);
   List<String> skillLore=new ArrayList<>();
   for(var attack:ai.attacks()){
    String name=plugin.languages.attack(player,detail.id()+"."+attack.id(),attack.name());
    skillLore.add("· "+name);
    skillLore.add("  "+attack.category().name()+"  |  "+attack.cooldown()+" ticks");
   }
   if(skillLore.isEmpty())skillLore.add("Sin ataques configurados");
   inv.setItem(20,PremiumStyle.tile(Material.IRON_SWORD,label(player,"aiAttacks","ATAQUES DEL JEFE"),NamedTextColor.AQUA,true,skillLore.toArray(String[]::new)));
   inv.setItem(29,PremiumStyle.tile(Material.IRON_CHESTPLATE,"IA DEL GUARDIÁN",color,true,
       "Alcance de detección: "+ai.detectionRange()+" bloques","Recarga global: "+ai.globalCooldown()+" ticks",
       "Velocidad: "+ai.speed()+" bloques/tick"));
   int rank=detail.tier().ordinal();
   inv.setItem(24,PremiumStyle.tile(Material.CHEST,label(player,"loot","TESOROS DEL ENCUENTRO"),color,true,detail.mini()?"1–2 tributos, 1–3 fragmentos y cristal común":"Fragmentos garantizados; cristales solo en minis",detail.mini()?"Sin armas ni armaduras":rank<2?"Equipo a partir de categoría Épica":"Armadura: "+Math.round((0.08+rank*0.035)*100)+" % · arma: "+Math.round((0.06+rank*0.025)*100)+" %",detail.mini()?"Primer peldaño de la progresión":"Rare Essence sin poder · Equipo sellado sin rares"));
   inv.setItem(31,detail.mini()?items.crystal(BossCatalog.Tier.COMMON,1):items.tribute("Cazador"));
   if(!detail.mini())inv.setItem(33,items.bossHeart(detail));
   if(!detail.mini()&&rank>=2)inv.setItem(34,PremiumStyle.tile(Material.NETHER_STAR,"RESONANCIA PRIMIGENIA",NamedTextColor.GOLD,true,"Desbloquea una pieza de equipo de boss","Los drops de equipo no traen rares incorporadas","Solo se fabrica: no cae como rare ya preparada","Receta: /rares → Infusiones → Resonancia Primigenia"));
   if(AdminPermissions.allows(player::hasPermission,"rareitemhunter.admin.egg"))inv.setItem(40,PremiumStyle.tile(Material.DRAGON_EGG,"✦ COLOCAR HUEVO DEL BOSS ✦",NamedTextColor.RED,true,"Clic para colocarlo junto a ti","Aparece sobre un pedestal de bedrock","Clic derecho en el huevo para despertar al boss"));
   inv.setItem(49,PremiumStyle.tile(Material.ARROW,"VOLVER A "+category.title.toUpperCase(java.util.Locale.ROOT),color,false));
  }
  player.openInventory(inv);
 }
 @EventHandler public void click(InventoryClickEvent event){
  if(!(event.getView().getTopInventory().getHolder() instanceof View view))return;event.setCancelled(true);
  int slot=event.getRawSlot();if(!(event.getWhoClicked() instanceof Player player)||!MenuActions.isActivation(event.getClick(),slot,54))return;
  MenuActions.dispatch(plugin,player,view,view.gate,()->{
   if(view.category==null){int[] slots={20,22,24,29,31,33};for(int i=0;i<slots.length;i++)if(slot==slots[i])show(player,BestiaryCategory.values()[i],0,null);}
   else if(view.detail!=null){
    if(slot==49)show(player,view.category,view.page,null);
    else if(slot==40){
     if(!player.hasPermission("cazajefes.admin")&&!player.hasPermission("rareitemhunter.admin.egg")){player.sendMessage("No tienes permiso para colocar huevos.");return;}
     if(manager.placeEggNear(view.detail.id(),player)){player.closeInventory();player.sendMessage("✦ Huevo colocado sobre bedrock. Haz clic derecho para despertar al boss.");}
     else player.sendMessage("No hay una superficie segura y libre cerca. No se sustituyen cofres ni otros contenedores.");
    }
   }
   else if(slot==49)open(player);else if(slot==45)show(player,view.category,view.page-1,null);else if(slot==53)show(player,view.category,view.page+1,null);else if(view.bosses.containsKey(slot))show(player,view.category,view.page,view.bosses.get(slot));
  });
 }
 @EventHandler public void drag(InventoryDragEvent e){if(e.getView().getTopInventory().getHolder() instanceof View)e.setCancelled(true);}
}
