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
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
final class MarketMenu implements CommandExecutor,Listener {
 private final CazaJefesPlugin plugin;private final RelicItems items;
 MarketMenu(CazaJefesPlugin p,RelicItems items,CazaJefesCommand catalog){plugin=p;this.items=items;}
 enum Section {
  REBIRTH("Santuario del Renacer",Material.TOTEM_OF_UNDYING);
  final String title;final Material icon;Section(String title,Material icon){this.title=title;this.icon=icon;}
 }
 record Cost(String prefix,boolean exact,int amount,String label){}
 record Offer(ItemStack reward,List<Cost> costs,Section section){}
 private static final class View implements InventoryHolder {
  Inventory inventory;final MenuActionGate gate=new MenuActionGate();final Section section;final int page;final Offer offer;final Map<Integer,Offer> offers=new HashMap<>();
  View(Section section,int page,Offer offer){this.section=section;this.page=page;this.offer=offer;}public Inventory getInventory(){return inventory;}
 }
 public boolean onCommand(CommandSender sender,Command command,String label,String[] args){if(sender instanceof Player p)open(p);else sender.sendMessage("Abre el mercado dentro del juego.");return true;}
 void open(Player player){show(player,Section.REBIRTH,0,null);}
 private List<Offer> offers(){
  List<Offer> offers=new ArrayList<>();
  int fragments=MarketRules.sigilFragments(plugin.getConfig().getInt("market.sigil-fragments",64));
  int sigils=MarketRules.amuletSigils(plugin.getConfig().getInt("market.amulet-sigils",4));
  int tributes=MarketRules.amuletTributes(plugin.getConfig().getInt("market.amulet-tributes",8));
  offers.add(new Offer(items.colossusSigil(),List.of(new Cost("fragmento_grieta",true,fragments,"Fragmentos de Tributo")),Section.REBIRTH));
  List<Cost> amuletCost=new ArrayList<>();amuletCost.add(new Cost("sello_coloso",true,sigils,"Sellos del Renacido"));if(tributes>0)amuletCost.add(new Cost("tributo_grieta",true,tributes,"Tributos del Cazador"));
  offers.add(new Offer(items.amulet(),List.copyOf(amuletCost),Section.REBIRTH));
  return offers;
 }
 private String label(ItemStack stack){Component title=stack.getItemMeta().displayName();return title==null?stack.getType().name():PlainTextComponentSerializer.plainText().serialize(title);}
 private ItemStack display(Offer offer){
  ItemStack result=offer.reward.clone();var meta=result.getItemMeta();List<Component> lore=new ArrayList<>(meta.lore()==null?List.of():meta.lore());
  lore.add(Component.text(" "));lore.add(Component.text("✦ PRECIO DE CANJE",NamedTextColor.GOLD));for(Cost cost:offer.costs)lore.add(Component.text(cost.amount+" × "+cost.label,NamedTextColor.AQUA));
  lore.add(Component.text("Clic para revisar y confirmar",NamedTextColor.GRAY));meta.lore(lore);result.setItemMeta(meta);return result;
 }
 private int count(Player p,String id){int count=0;for(ItemStack stack:p.getInventory().getStorageContents())if(id.equals(items.customId(stack)))count+=stack.getAmount();return count;}
 private void show(Player player,Section section,int page,Offer offer){
  items.migrateCurrencies(player);List<Offer> list=offers().stream().filter(o->section==null||o.section==section).toList();int pages=Math.max(1,(list.size()+27)/28);page=Math.max(0,Math.min(page,pages-1));
  View view=new View(section,page,offer);view.inventory=Bukkit.createInventory(view,54,PremiumStyle.heading(offer!=null?"✦ CONFIRMAR CANJE":section==null?"SANTUARIO DEL RENACER":"✦ "+plugin.getConfig().getString("presentation.sanctuary.title","Santuario del Renacer"),NamedTextColor.GOLD));Inventory inv=view.inventory;
  Material pane=Material.matchMaterial(plugin.getConfig().getString("presentation.sanctuary.pane","BLUE_STAINED_GLASS_PANE"));RecipePresentation.frame(inv,pane!=null&&pane.name().endsWith("STAINED_GLASS_PANE")?pane:Material.BLUE_STAINED_GLASS_PANE);
  inv.setItem(4,PremiumStyle.tile(section==null?Material.END_CRYSTAL:section.icon,section==null?"SANTUARIO DEL RENACER":section.title.toUpperCase(java.util.Locale.ROOT),NamedTextColor.GOLD,true,"Elige tu recompensa • revisa su precio","Los canjes requieren espacio y monedas originales"));
  if(offer==null){
   for(int i=page*28;i<Math.min(list.size(),(page+1)*28);i++){int slot=FeatureSettings.marketSlots(plugin.getConfig())[i-page*28];inv.setItem(slot,display(list.get(i)));view.offers.put(slot,list.get(i));}
   if(page>0)inv.setItem(45,PremiumStyle.tile(Material.ARROW,"ANTERIOR",NamedTextColor.AQUA,false));if(page+1<pages)inv.setItem(53,PremiumStyle.tile(Material.ARROW,"SIGUIENTE",NamedTextColor.AQUA,false));inv.setItem(49,PremiumStyle.tile(Material.NETHER_STAR,"VOLVER A RECETAS",NamedTextColor.GOLD,true));
  }else{
   int[] costs={20,29,38};for(int i=0;i<offer.costs.size();i++){
    Cost cost=offer.costs.get(i);Material icon=cost.prefix.equals("fragmento_grieta")?Material.DISC_FRAGMENT_5:cost.prefix.equals("sello_coloso")?Material.NETHER_STAR:cost.prefix.startsWith("tributo")?Material.EMERALD:Material.ECHO_SHARD;
    inv.setItem(costs[i],PremiumStyle.tile(icon,cost.amount+" × "+cost.label,NamedTextColor.AQUA,true,"Se consume al confirmar el canje"));
   }
   inv.setItem(22,PremiumStyle.tile(Material.SPECTRAL_ARROW,"INTERCAMBIO",NamedTextColor.GOLD,true));inv.setItem(24,offer.reward.clone());
   inv.setItem(16,PremiumStyle.tile(Material.BOOK,"1 · REVISA EL PRECIO",NamedTextColor.AQUA,false,"Las vistas previas no son objetos gratuitos"));
   inv.setItem(34,PremiumStyle.tile(Material.CHEST,"2 · RESERVA ESPACIO",NamedTextColor.GOLD,false,"Si no cabe, no se consume ninguna moneda"));
   inv.setItem(40,PremiumStyle.tile(Material.LIME_DYE,"✦ CONFIRMAR CANJE ✦",NamedTextColor.GREEN,true,label(offer.reward),"Entrega una unidad de la recompensa"));
   inv.setItem(49,PremiumStyle.tile(Material.ARROW,"VOLVER A "+section.title,NamedTextColor.AQUA,false));
  }player.openInventory(inv);
 }
 private void buy(Player player,Offer offer){
  if(!player.hasPermission("rareitemhunter.hunter.craft")){player.sendMessage("No tienes permiso para canjear.");return;}
  Offer current=offers().stream().filter(o->o.section==offer.section&&o.reward.isSimilar(offer.reward)&&o.reward.getAmount()==offer.reward.getAmount()).findFirst().orElse(null);
  if(current==null){player.sendMessage("Esta oferta ya no está disponible.");open(player);return;}
  if(!current.costs.equals(offer.costs)){player.sendMessage("El precio cambió. Revisa el canje antes de confirmar.");show(player,offer.section,0,current);return;}
  items.migrateCurrencies(player);ItemStack[] original=player.getInventory().getStorageContents();ItemStack[] planned=Arrays.stream(original).map(i->i==null?null:i.clone()).toArray(ItemStack[]::new);
  List<java.util.function.IntPredicate> requirements=new ArrayList<>();for(Cost cost:offer.costs){java.util.function.IntPredicate match=slot->{String id=items.customId(original[slot]);return id!=null&&(cost.exact?id.equals(cost.prefix):id.startsWith(cost.prefix));};for(int i=0;i<cost.amount;i++)requirements.add(match);}
  int[] counts=Arrays.stream(original).mapToInt(i->i==null?0:i.getAmount()).toArray();int[] left=CraftPlan.remaining(counts,requirements);if(left==null){player.sendMessage("Te faltan monedas para este canje.");return;}
  for(int i=0;i<planned.length;i++){if(left[i]==0)planned[i]=null;else planned[i].setAmount(left[i]);}
  ItemStack reward=offer.reward.clone();int remaining=reward.getAmount();for(int slot=0;slot<planned.length&&remaining>0;slot++){
   ItemStack stack=planned[slot];if(stack!=null&&!stack.isSimilar(reward))continue;int amount=Math.min(remaining,reward.getMaxStackSize()-(stack==null?0:stack.getAmount()));if(amount<=0)continue;
   if(stack==null){stack=reward.clone();stack.setAmount(amount);planned[slot]=stack;}else stack.setAmount(stack.getAmount()+amount);remaining-=amount;
  }
  if(remaining>0){player.sendMessage("No hay espacio. No se consumió ninguna moneda.");return;}
  player.getInventory().setStorageContents(planned);player.playSound(player.getLocation(),Sound.BLOCK_AMETHYST_BLOCK_CHIME,0.7f,1.1f);player.sendMessage(Component.text("✦ Canje completado: ",NamedTextColor.GOLD).append(offer.reward.getItemMeta().displayName()));
 }
 @EventHandler public void click(InventoryClickEvent event){
  if(!(event.getView().getTopInventory().getHolder() instanceof View view))return;event.setCancelled(true);int slot=event.getRawSlot();if(!(event.getWhoClicked() instanceof Player player)||!MenuActions.isActivation(event.getClick(),slot,54))return;
  MenuActions.dispatch(plugin,player,view,view.gate,()->{
   if(view.offer!=null){if(slot==49)show(player,view.section,view.page,null);else if(slot==40)buy(player,view.offer);}
   else if(slot==49)plugin.getServer().dispatchCommand(player,"rares");else if(slot==45)show(player,view.section,view.page-1,null);else if(slot==53)show(player,view.section,view.page+1,null);else if(view.offers.containsKey(slot))show(player,view.section,view.page,view.offers.get(slot));
  });
 }
 @EventHandler public void drag(InventoryDragEvent event){if(event.getView().getTopInventory().getHolder() instanceof View)event.setCancelled(true);}
}
