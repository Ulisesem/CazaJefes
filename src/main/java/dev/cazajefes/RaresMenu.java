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
  CURRENCIES("Tesorería de tributos",Material.DISC_FRAGMENT_5),CRYSTALS("Altar de cristales",Material.AMETHYST_CLUSTER),WEAPONS("Armería de élite",Material.NETHERITE_SWORD),ARMOR("Armaduras de categoría",Material.NETHERITE_CHESTPLATE),ESSENCES("Cámara de infusiones",Material.MAGMA_CREAM),SPELLS("Biblioteca de grimorios",Material.ENCHANTED_BOOK),PVP("Rares PvP · Forja de duelo",Material.NETHERITE_AXE),MATERIALS("Materiales de duelo",Material.ECHO_SHARD),ALL("Recetario completo",Material.NETHER_STAR);
  final String title;final Material icon;Category(String title,Material icon){this.title=title;this.icon=icon;}
  boolean matches(String key){return this==ALL||this==category(key);}
  static Category category(String key){if(key.equals("sello_resonancia_primigenia"))return ESSENCES;if(key.startsWith("pvp_material_"))return MATERIALS;if(key.startsWith("pvp_gear_"))return PVP;if(key.startsWith("armadura_"))return ARMOR;if(key.startsWith("arma_set_"))return WEAPONS;if(key.startsWith("crear_libro_"))return SPELLS;if(key.startsWith("crear_esencia_"))return ESSENCES;if(List.of("hacha_sismica","espada_brezal","arco_ceniciento","lanza_magma","mandoble_vacio").contains(key))return WEAPONS;return key.startsWith("cristal_")?CRYSTALS:CURRENCIES;}
 }
 private static final class Menu implements InventoryHolder {
  Inventory inventory;final MenuActionGate gate=new MenuActionGate();final Category category;BossCatalog.Tier rank;RareCompatibility.Group group;boolean ranks,groups;final Map<Integer,RareCompatibility.Group> groupSlots=new HashMap<>();final Map<Integer,BossCatalog.Tier> rankSlots=new HashMap<>();final int page;final ShapedRecipe detail;final Map<Integer,ShapedRecipe> recipes=new HashMap<>();
  Menu(Category c,int page,ShapedRecipe detail){category=c;this.page=page;this.detail=detail;}public Inventory getInventory(){return inventory;}
 }
 public boolean onCommand(CommandSender sender,Command command,String label,String[] args){if(sender instanceof Player p)open(p,null,0,null);else sender.sendMessage("Este menú requiere un jugador.");return true;}
 private List<ShapedRecipe> recipes(){List<ShapedRecipe> out=new ArrayList<>();Bukkit.recipeIterator().forEachRemaining(r->{if(r instanceof ShapedRecipe s&&s.getKey().getNamespace().equals("cazajefes"))out.add(s);});out.sort(Comparator.comparing(r->r.getKey().getKey()));return out;}
 private void open(Player player,Category category,int page,ShapedRecipe detail){open(player,category,page,detail,null);}
 private void groupMenu(Player player,Category category){Menu menu=new Menu(category,0,null);menu.groups=true;menu.inventory=Bukkit.createInventory(menu,54,Component.text("✦ "+category.title+" · EQUIPO",NamedTextColor.GOLD));PremiumStyle.frame(menu.inventory,Material.PURPLE_STAINED_GLASS_PANE);menu.inventory.setItem(4,PremiumStyle.tile(category.icon,"SELECCIONA EL EQUIPO COMPATIBLE",NamedTextColor.GOLD,true,"Cada rare tiene un destino concreto","El equipo de boss requiere Resonancia Primigenia"));int[] slots={19,20,21,23,24,25};int index=0;for(var group:RareCompatibility.Group.values()){long count=recipes().stream().filter(r->category.matches(r.getKey().getKey())&&RareCompatibility.recipe(r.getKey().getKey())==group).count();if(count==0)continue;int slot=slots[index++];menu.groupSlots.put(slot,group);menu.inventory.setItem(slot,PremiumStyle.tile(group.icon,group.title.toUpperCase(Locale.ROOT),NamedTextColor.AQUA,true,count+" recetas compatibles","Clic para consultar y fabricar"));}menu.inventory.setItem(49,PremiumStyle.tile(Material.ARROW,"VOLVER A LA FORJA",NamedTextColor.GOLD,false));player.openInventory(menu.inventory);}
 private void rankMenu(Player player,Category category){
  Menu menu=new Menu(category,0,null);menu.ranks=true;menu.inventory=Bukkit.createInventory(menu,54,Component.text("✦ "+category.title+" · CATEGORÍAS",NamedTextColor.GOLD));PremiumStyle.frame(menu.inventory,Material.PURPLE_STAINED_GLASS_PANE);
  menu.inventory.setItem(4,PremiumStyle.tile(category.icon,category.title.toUpperCase(Locale.ROOT),NamedTextColor.GOLD,true,"Selecciona el nivel de poder"));
  List<BossCatalog.Tier> tiers=category==Category.CRYSTALS?List.of(BossCatalog.Tier.values()):EquipmentSets.RANKS;int[] slots=tiers.size()==6?new int[]{19,20,21,23,24,25}:new int[]{20,22,24,31};
  for(int i=0;i<tiers.size();i++){var tier=tiers.get(i);int slot=slots[i];menu.rankSlots.put(slot,tier);menu.inventory.setItem(slot,PremiumStyle.tile(category==Category.CRYSTALS?Material.AMETHYST_CLUSTER:category.icon,tier.display.toUpperCase(Locale.ROOT),tier.color,true,category==Category.CRYSTALS?(tier==BossCatalog.Tier.COMMON?"Se obtiene de los minijefes":"Se forja con cristales de la categoría anterior"):(category==Category.ARMOR?"Protección ":"Filo ")+(category==Category.ARMOR?EquipmentSets.protection(tier):EquipmentSets.sharpness(tier)),"Clic para explorar"));}
  menu.inventory.setItem(49,PremiumStyle.tile(Material.ARROW,"VOLVER A LA FORJA",NamedTextColor.AQUA,false));player.openInventory(menu.inventory);
 }
 private void open(Player player,Category category,int page,ShapedRecipe detail,BossCatalog.Tier rank){open(player,category,page,detail,rank,null);}
 private void open(Player player,Category category,int page,ShapedRecipe detail,BossCatalog.Tier rank,RareCompatibility.Group group){
  items.migrateCurrencies(player);
  List<ShapedRecipe> list=recipes().stream().filter(r->category==null||category.matches(r.getKey().getKey())).filter(r->rank==null||EquipmentSets.tier(items.customId(r.getResult()))==rank).filter(r->group==null||RareCompatibility.recipe(r.getKey().getKey())==group).toList();int pages=Math.max(1,(list.size()+27)/28);page=Math.max(0,Math.min(page,pages-1));
  Menu menu=new Menu(category,page,detail);menu.rank=rank;menu.group=group;menu.inventory=Bukkit.createInventory(menu,54,Component.text(detail!=null?"✦ FORJA · SELLO DE CREACIÓN":category==null?"✦ LA FORJA DE LA GRIETA":"✦ "+category.title+(group==null?"":" · "+group.title)+" · "+(page+1)+"/"+pages,NamedTextColor.GOLD));Inventory inv=menu.inventory;
  PremiumStyle.frame(inv,Material.CYAN_STAINED_GLASS_PANE);inv.setItem(4,PremiumStyle.tile(Material.ENCHANTING_TABLE,"✦ FORJA DE LOS CAZADORES ✦",NamedTextColor.GOLD,true,"Recetas reales • materiales originales","Selecciona una categoría de progresión"));
  if(category==null){int[] slots={10,12,14,16,28,30,32,34,40};int i=0;for(var c:Category.values())inv.setItem(slots[i++],PremiumStyle.tile(c.icon,c.title.toUpperCase(java.util.Locale.ROOT),NamedTextColor.AQUA,true,list.stream().filter(r->c.matches(r.getKey().getKey())).count()+" recetas disponibles","Clic para explorar"));
   inv.setItem(39,PremiumStyle.tile(Material.BOOK,"GUÍA DE MATERIALES",NamedTextColor.AQUA,true,"Tributos, fragmentos, cristales y brújula","Clic para consultar la progresión"));
   inv.setItem(41,PremiumStyle.tile(Material.HEART_OF_THE_SEA,"GALERÍA DE CORAZONES",NamedTextColor.GOLD,true,"Trofeos RARE de los bosses","Clic para explorar; no tienen receta"));
   inv.setItem(48,PremiumStyle.tile(Material.EMERALD,"MERCADO DE LA GRIETA",NamedTextColor.GOLD,true,"Canjes, sellos y Amuleto del Fénix Astral"));
   inv.setItem(49,PremiumStyle.tile(Material.ANVIL,"INFUSIÓN DE EQUIPO",NamedTextColor.LIGHT_PURPLE,true,"En una mesa: equipo compatible + 1–3 esencias","Boss: primero Resonancia Primigenia; después rare compatible"));
  }else if(detail==null){
   if(category==Category.ESSENCES&&group==RareCompatibility.Group.ARMOR)inv.setItem(4,PremiumStyle.tile(Material.NETHERITE_CHESTPLATE,"LEGADO DE LA ARMADURA",NamedTextColor.GOLD,true,"Vida, restauración, vuelo y contramedidas","Pulsa una esencia para ver su receta","Los efectos pasivos usan la pieza de mayor nivel"));
   if(category==Category.CRYSTALS&&rank==BossCatalog.Tier.COMMON){inv.setItem(22,items.crystal(BossCatalog.Tier.COMMON,1));inv.setItem(31,PremiumStyle.tile(Material.SPAWNER,"ORIGEN · MINIJEFES",NamedTextColor.GRAY,true,"Cada minijefe deja un cristal común","Los bosses mayores no dejan cristales","Se combinan para fabricar categorías superiores"));}
   for(int i=page*28;i<Math.min(list.size(),(page+1)*28);i++){int slot=PremiumStyle.GRID[i-page*28];inv.setItem(slot,list.get(i).getResult());menu.recipes.put(slot,list.get(i));}
   if(page>0)inv.setItem(45,PremiumStyle.tile(Material.ARROW,"ANTERIOR",NamedTextColor.AQUA,false));if(page+1<pages)inv.setItem(53,PremiumStyle.tile(Material.ARROW,"SIGUIENTE",NamedTextColor.AQUA,false));
   inv.setItem(49,PremiumStyle.tile(Material.NETHER_STAR,"VOLVER A CATEGORÍAS",NamedTextColor.GOLD,true));
  }else{
   inv.setItem(4,PremiumStyle.tile(Material.ENCHANTED_BOOK,category.title.toUpperCase(java.util.Locale.ROOT)+" · CREACIÓN",NamedTextColor.GOLD,true,"Cada símbolo consume una unidad por posición","Las alternativas exactas son intercambiables"));
   String[] shape=detail.getShape();Map<Character,RecipeChoice> choices=detail.getChoiceMap();for(int row=0;row<shape.length;row++)for(int col=0;col<shape[row].length();col++){RecipeChoice choice=choices.get(shape[row].charAt(col));if(choice!=null)inv.setItem(10+row*9+col,choice.getItemStack());}
   inv.setItem(16,PremiumStyle.tile(category.icon,"PASO 1 · REÚNE LOS INGREDIENTES",NamedTextColor.AQUA,true,"Un objeto por posición ocupada",category==Category.PVP?"La pieza base se consume: usa equipo sin mejoras":"Se aceptan núcleos alternativos válidos"));
   inv.setItem(34,PremiumStyle.tile(Material.CHEST,"PASO 2 · RESERVA ESPACIO",NamedTextColor.GOLD,false,"El resultado debe caber en tu inventario","No se consume nada si falta espacio"));
   if(category==Category.PVP||category==Category.MATERIALS)inv.setItem(43,PremiumStyle.tile(Material.ECHO_SHARD,category==Category.PVP?"VER MATERIAL EXCLUSIVO":"VER RELIQUIA ASOCIADA",NamedTextColor.AQUA,true,"Clic para consultar la receta vinculada"));
   inv.setItem(23,PremiumStyle.tile(Material.SPECTRAL_ARROW,"FORJAR",NamedTextColor.GOLD,true));inv.setItem(25,detail.getResult());
   inv.setItem(40,PremiumStyle.tile(Material.ANVIL,"✦ FABRICAR UNA UNIDAD ✦",NamedTextColor.GREEN,true,"Consume los ingredientes de tu inventario","Solo se confirma si hay materiales y espacio"));
   inv.setItem(49,PremiumStyle.tile(Material.ARROW,"VOLVER A "+category.title,NamedTextColor.AQUA,false));
  }player.openInventory(inv);
 }
 @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=false)public void click(InventoryClickEvent event){
  if(!(event.getView().getTopInventory().getHolder() instanceof Menu menu))return;event.setCancelled(true);int slot=event.getRawSlot();if(!(event.getWhoClicked() instanceof Player player)||!MenuActions.isActivation(event.getClick(),slot,54))return;
  MenuActions.dispatch(plugin,player,menu,menu.gate,()->{
   if(menu.category==null){if(slot==48){plugin.market.open(player);return;}if(slot==39){plugin.trophies.open(player,true,0);return;}if(slot==41){plugin.trophies.open(player,false,0);return;}int[] slots={10,12,14,16,28,30,32,34,40};for(int i=0;i<slots.length;i++)if(slot==slots[i]){Category selected=Category.values()[i];if(selected==Category.ESSENCES||selected==Category.PVP)groupMenu(player,selected);else if(selected==Category.ARMOR||selected==Category.WEAPONS||selected==Category.CRYSTALS)rankMenu(player,selected);else open(player,selected,0,null);}}
   else if(menu.groups){if(slot==49)open(player,null,0,null);else if(menu.groupSlots.containsKey(slot))open(player,menu.category,0,null,null,menu.groupSlots.get(slot));}
   else if(menu.ranks){if(slot==49)open(player,null,0,null);else if(menu.rankSlots.containsKey(slot))open(player,menu.category,0,null,menu.rankSlots.get(slot));}
   else if(menu.detail!=null){if(slot==49)open(player,menu.category,menu.page,null,menu.rank,menu.group);else if(slot==40)craft(player,menu.detail);else if(slot==43&&(menu.category==Category.PVP||menu.category==Category.MATERIALS)){String key=menu.detail.getKey().getKey();String linked=menu.category==Category.PVP?key.replace("pvp_gear_","pvp_material_"):key.replace("pvp_material_","pvp_gear_");recipes().stream().filter(r->r.getKey().getKey().equals(linked)).findFirst().ifPresent(r->open(player,menu.category==Category.PVP?Category.MATERIALS:Category.PVP,0,r,null,menu.category==Category.MATERIALS?RareCompatibility.recipe(linked):null));}}
   else if(slot==49){if(menu.rank!=null)rankMenu(player,menu.category);else if(menu.group!=null)groupMenu(player,menu.category);else open(player,null,0,null);}else if(slot==45)open(player,menu.category,menu.page-1,null,menu.rank,menu.group);else if(slot==53)open(player,menu.category,menu.page+1,null,menu.rank,menu.group);else if(menu.recipes.containsKey(slot))open(player,menu.category,menu.page,menu.recipes.get(slot),menu.rank,menu.group);
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
