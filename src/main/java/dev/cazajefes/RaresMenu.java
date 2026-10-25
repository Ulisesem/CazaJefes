package dev.cazajefes;
import java.util.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
final class RaresMenu implements CommandExecutor,Listener {
 private final CazaJefesPlugin plugin;private final RelicItems items;
 RaresMenu(CazaJefesPlugin plugin,RelicItems items){this.plugin=plugin;this.items=items;}
 enum Category {
  PROGRESSION("Tributos y cristales",Material.AMETHYST_CLUSTER),WEAPONS("Armas de élite",Material.NETHERITE_SWORD),ARMOR("Armaduras",Material.NETHERITE_CHESTPLATE),ESSENCES("Esencias",Material.MAGMA_CREAM),SPELLS("Hechizos",Material.ENCHANTED_BOOK),ALL("Todo el arsenal",Material.NETHER_STAR);
  final String title;final Material icon;Category(String title,Material icon){this.title=title;this.icon=icon;}
  boolean matches(String key){return this==ALL||this==category(key);}
  static Category category(String key){if(key.startsWith("armadura_"))return ARMOR;if(key.startsWith("crear_libro_"))return SPELLS;if(key.startsWith("crear_esencia_"))return ESSENCES;if(List.of("hacha_sismica","espada_brezal","arco_ceniciento","lanza_magma","mandoble_vacio").contains(key))return WEAPONS;return PROGRESSION;}
 }
 private static final class Menu implements InventoryHolder {
  Inventory inventory;final MenuActionGate gate=new MenuActionGate();final Category category;final int page;final ShapedRecipe detail;final Map<Integer,ShapedRecipe> recipes=new HashMap<>();
  Menu(Category c,int page,ShapedRecipe detail){category=c;this.page=page;this.detail=detail;}public Inventory getInventory(){return inventory;}
 }
 public boolean onCommand(CommandSender sender,Command command,String label,String[] args){if(sender instanceof Player p)open(p,null,0,null);else sender.sendMessage("Este menú requiere un jugador.");return true;}
 private List<ShapedRecipe> recipes(){List<ShapedRecipe> out=new ArrayList<>();Bukkit.recipeIterator().forEachRemaining(r->{if(r instanceof ShapedRecipe s&&s.getKey().getNamespace().equals("cazajefes"))out.add(s);});out.sort(Comparator.comparing(r->r.getKey().getKey()));return out;}
 private void open(Player player,Category category,int page,ShapedRecipe detail){
  items.migrateCurrencies(player);
  List<ShapedRecipe> list=recipes().stream().filter(r->category==null||category.matches(r.getKey().getKey())).toList();int pages=Math.max(1,(list.size()+27)/28);page=Math.max(0,Math.min(page,pages-1));
  Menu menu=new Menu(category,page,detail);menu.inventory=Bukkit.createInventory(menu,54,Component.text(detail!=null?"✦ FORJA · SELLO DE CREACIÓN":category==null?"✦ LA FORJA DE LA GRIETA":"✦ "+category.title+" · "+(page+1)+"/"+pages,NamedTextColor.GOLD));Inventory inv=menu.inventory;
  PremiumStyle.frame(inv,Material.CYAN_STAINED_GLASS_PANE);inv.setItem(4,PremiumStyle.tile(Material.ENCHANTING_TABLE,"✦ FORJA DE LOS CAZADORES ✦",NamedTextColor.GOLD,true,"Recetas reales • materiales originales","Selecciona una categoría de progresión"));
  if(category==null){int[] slots={20,22,24,29,31,33};int i=0;for(var c:Category.values())inv.setItem(slots[i++],PremiumStyle.tile(c.icon,c.title.toUpperCase(java.util.Locale.ROOT),NamedTextColor.AQUA,true,list.stream().filter(r->c.matches(r.getKey().getKey())).count()+" recetas disponibles","Clic para explorar"));
   inv.setItem(49,PremiumStyle.tile(Material.ANVIL,"INFUSIÓN DE EQUIPO",NamedTextColor.LIGHT_PURPLE,true,"En una mesa: equipo compatible + 1–3 esencias","La fusión conserva el equipo y respeta su nivel máximo"));
  }else if(detail==null){
   for(int i=page*28;i<Math.min(list.size(),(page+1)*28);i++){int slot=PremiumStyle.GRID[i-page*28];inv.setItem(slot,list.get(i).getResult());menu.recipes.put(slot,list.get(i));}
   if(page>0)inv.setItem(45,PremiumStyle.tile(Material.ARROW,"ANTERIOR",NamedTextColor.AQUA,false));if(page+1<pages)inv.setItem(53,PremiumStyle.tile(Material.ARROW,"SIGUIENTE",NamedTextColor.AQUA,false));
   inv.setItem(49,PremiumStyle.tile(Material.NETHER_STAR,"VOLVER A CATEGORÍAS",NamedTextColor.GOLD,true));
  }else{
   inv.setItem(4,PremiumStyle.tile(Material.ENCHANTED_BOOK,"SELLO DE CREACIÓN",NamedTextColor.GOLD,true,"Cada símbolo consume una unidad por posición","Las alternativas exactas son intercambiables"));
   String[] shape=detail.getShape();Map<Character,RecipeChoice> choices=detail.getChoiceMap();for(int row=0;row<shape.length;row++)for(int col=0;col<shape[row].length();col++){RecipeChoice choice=choices.get(shape[row].charAt(col));if(choice!=null)inv.setItem(10+row*9+col,choice.getItemStack());}
   inv.setItem(23,PremiumStyle.tile(Material.SPECTRAL_ARROW,"FORJAR",NamedTextColor.GOLD,true));inv.setItem(25,detail.getResult());
   inv.setItem(40,PremiumStyle.tile(Material.ANVIL,"✦ FABRICAR UNA UNIDAD ✦",NamedTextColor.GREEN,true,"Consume los ingredientes de tu inventario","Solo se confirma si hay materiales y espacio"));
   inv.setItem(49,PremiumStyle.tile(Material.ARROW,"VOLVER A "+category.title,NamedTextColor.AQUA,false));
  }player.openInventory(inv);
 }
 @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=false)public void click(InventoryClickEvent event){
  if(!(event.getView().getTopInventory().getHolder() instanceof Menu menu))return;event.setCancelled(true);int slot=event.getRawSlot();if(!(event.getWhoClicked() instanceof Player player)||!MenuActions.isActivation(event.getClick(),slot,54))return;
  MenuActions.dispatch(plugin,player,menu,menu.gate,()->{
   if(menu.category==null){int[] slots={20,22,24,29,31,33};for(int i=0;i<slots.length;i++)if(slot==slots[i])open(player,Category.values()[i],0,null);}
   else if(menu.detail!=null){if(slot==49)open(player,menu.category,menu.page,null);else if(slot==40)craft(player,menu.detail);}
   else if(slot==49)open(player,null,0,null);else if(slot==45)open(player,menu.category,menu.page-1,null);else if(slot==53)open(player,menu.category,menu.page+1,null);else if(menu.recipes.containsKey(slot))open(player,menu.category,menu.page,menu.recipes.get(slot));
  });
 }
 @EventHandler public void drag(InventoryDragEvent event){if(event.getView().getTopInventory().getHolder() instanceof Menu)event.setCancelled(true);}
 private void craft(Player player,ShapedRecipe shown){
  if(!player.hasPermission("rareitemhunter.hunter.craft")){player.sendMessage("No tienes permiso para fabricar.");return;}
  if(!(Bukkit.getRecipe(shown.getKey()) instanceof ShapedRecipe recipe)){player.closeInventory();return;}
  items.migrateCurrencies(player);ItemStack[] original=player.getInventory().getStorageContents();ItemStack[] planned=Arrays.stream(original).map(i->i==null?null:i.clone()).toArray(ItemStack[]::new);
  Map<Character,RecipeChoice> choices=recipe.getChoiceMap();List<RecipeChoice> costs=new ArrayList<>();for(String row:recipe.getShape())for(char symbol:row.toCharArray())if(choices.get(symbol)!=null)costs.add(choices.get(symbol));costs.sort(Comparator.comparingInt(c->c instanceof RecipeChoice.ExactChoice?0:1));
  List<java.util.function.IntPredicate> requirements=new ArrayList<>();for(RecipeChoice choice:costs)requirements.add(slot->original[slot]!=null&&choice.test(original[slot])&&(!(choice instanceof RecipeChoice.MaterialChoice)||items.customId(original[slot])==null));
  int[] counts=Arrays.stream(original).mapToInt(i->i==null?0:i.getAmount()).toArray();int[] remainder=CraftPlan.remaining(counts,requirements);if(remainder==null){player.sendMessage("Faltan ingredientes. Los objetos de jefe deben coincidir con la receta.");return;}
  for(int slot=0;slot<planned.length;slot++){if(remainder[slot]==0)planned[slot]=null;else planned[slot].setAmount(remainder[slot]);}
  ItemStack result=recipe.getResult().clone();int remaining=result.getAmount();for(int slot=0;slot<planned.length&&remaining>0;slot++){ItemStack stack=planned[slot];if(stack!=null&&!stack.isSimilar(result))continue;int amount=Math.min(remaining,result.getMaxStackSize()-(stack==null?0:stack.getAmount()));if(amount<=0)continue;if(stack==null){stack=result.clone();stack.setAmount(amount);planned[slot]=stack;}else stack.setAmount(stack.getAmount()+amount);remaining-=amount;}
  if(remaining>0){player.sendMessage("No hay espacio para el resultado.");return;}player.getInventory().setStorageContents(planned);player.playSound(player.getLocation(),Sound.BLOCK_ANVIL_USE,0.5f,1.2f);player.sendMessage("✦ Objeto forjado correctamente.");
 }
}
