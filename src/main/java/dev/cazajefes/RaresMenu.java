package dev.cazajefes;

import java.util.*;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;

/** A short path from a useful recipe category to its ingredients and result. */
final class RaresMenu implements CommandExecutor, Listener {
 private final CazaJefesPlugin plugin;
 private final RelicItems items;
 private final RecipeMenuSettings settings;
 RaresMenu(CazaJefesPlugin plugin,RelicItems items) {
  this.plugin=plugin;this.items=items;settings=new RecipeMenuSettings(plugin);
 }
 enum Category {
  CURRENCIES("Tributos",Material.DISC_FRAGMENT_5),CRYSTALS("Cristales",Material.AMETHYST_CLUSTER),
  WEAPONS("Armas forjadas",Material.NETHERITE_SWORD),ARMOR("Armaduras de categoría",Material.NETHERITE_CHESTPLATE),
  ESSENCES("Esencias",Material.MAGMA_CREAM),SPELLS("Libros",Material.ENCHANTED_BOOK),
  PVP("Reliquias PvP",Material.NETHERITE_AXE),MATERIALS("Materiales",Material.ECHO_SHARD),ALL("Todas las recetas",Material.NETHER_STAR);
  final String title;final Material icon;
  Category(String title,Material icon){this.title=title;this.icon=icon;}
  boolean matches(String key){return this==ALL||this==category(key);}
  static Category category(String key) {
   if(key.equals("sello_resonancia_primigenia"))return ESSENCES;
   if(key.startsWith("ranged_material_")||key.startsWith("pvp_material_"))return MATERIALS;
   if(key.startsWith("pvp_gear_"))return PVP;
   if(key.startsWith("armadura_"))return ARMOR;
   if(key.startsWith("arma_set_"))return WEAPONS;
   if(key.startsWith("crear_libro_"))return SPELLS;
   if(key.startsWith("crear_esencia_"))return ESSENCES;
   if(List.of("hacha_sismica","espada_brezal","arco_ceniciento","lanza_magma","mandoble_vacio").contains(key))return WEAPONS;
   return key.startsWith("cristal_")?CRYSTALS:CURRENCIES;
  }
 }
 private static final class Menu implements InventoryHolder {
  Inventory inventory;
  final MenuActionGate gate=new MenuActionGate();
  final Category category;final int page;final ShapedRecipe detail;
  BossCatalog.Tier rank;RareCompatibility.Group group;boolean ranks,groups,help;
  final Map<Integer,Category> categories=new HashMap<>();
  final Map<Integer,RareCompatibility.Group> groupSlots=new HashMap<>();
  final Map<Integer,BossCatalog.Tier> rankSlots=new HashMap<>();
  final Map<Integer,ShapedRecipe> recipes=new HashMap<>();
  Menu(Category category,int page,ShapedRecipe detail){this.category=category;this.page=page;this.detail=detail;}
  public Inventory getInventory(){return inventory;}
 }
 public boolean onCommand(CommandSender sender,Command command,String label,String[] args) {
  if(sender instanceof Player player)open(player,null,0,null);else sender.sendMessage("Este menú requiere un jugador.");
  return true;
 }
 private List<ShapedRecipe> recipes() {
  List<ShapedRecipe> out=new ArrayList<>();
  Bukkit.recipeIterator().forEachRemaining(recipe->{
   if(recipe instanceof ShapedRecipe shaped&&shaped.getKey().getNamespace().equals("cazajefes")
       &&RecipePresentation.visible(shaped.getKey().getKey()))out.add(shaped);
  });
  out.sort(Comparator.comparing(recipe->net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
      .plainText().serialize(recipe.getResult().effectiveName()),String.CASE_INSENSITIVE_ORDER));
  return out;
 }
 private Inventory inventory(Menu menu,String title) {
  menu.inventory=Bukkit.createInventory(menu,54,PremiumStyle.heading(title,NamedTextColor.AQUA));
  RecipePresentation.frame(menu.inventory,settings.layout().accent());return menu.inventory;
 }
 private void back(Inventory inventory,String title) {
  inventory.setItem(RecipePresentation.LIST_BACK,PremiumStyle.tile(Material.NETHER_STAR,title,NamedTextColor.AQUA,false));
 }
 private void groupMenu(Player player,Category category) {
  settings.refresh();Menu menu=new Menu(category,0,null);menu.groups=true;
  Inventory inventory=inventory(menu,settings.label(category));
  inventory.setItem(4,PremiumStyle.tile(category.icon,"Elige tu equipo",NamedTextColor.AQUA,true,
      "Encuentra recetas compatibles con lo que usas."));
  List<ShapedRecipe> available=recipes();
  List<RareCompatibility.Group> groups=Arrays.stream(RareCompatibility.Group.values())
      .filter(group->group!=RareCompatibility.Group.RANGED&&group!=RareCompatibility.Group.UNLOCK)
      .filter(group->available.stream().anyMatch(recipe->category.matches(recipe.getKey().getKey())
          &&RareCompatibility.recipeInGroup(recipe.getKey().getKey(),group))).toList();
  int[] slots=RareMenuStyle.slots(groups.size());
  for(int i=0;i<groups.size();i++) {
   var group=groups.get(i);
   long count=available.stream().filter(recipe->category.matches(recipe.getKey().getKey())
       &&RareCompatibility.recipeInGroup(recipe.getKey().getKey(),group)).count();
   menu.groupSlots.put(slots[i],group);
   inventory.setItem(slots[i],PremiumStyle.tile(group.icon,group.title,NamedTextColor.AQUA,true,count+" recetas","Clic para explorar."));
  }
  back(inventory,"Volver a la forja");player.openInventory(inventory);
 }
 private void rankMenu(Player player,Category category) {
  settings.refresh();Menu menu=new Menu(category,0,null);menu.ranks=true;
  Inventory inventory=inventory(menu,settings.label(category));
  inventory.setItem(4,PremiumStyle.tile(category.icon,"Elige una categoría",NamedTextColor.AQUA,true,
      category==Category.CRYSTALS?"Mejora tus cristales paso a paso.":"Cada categoría ofrece un filo superior."));
  List<BossCatalog.Tier> tiers=category==Category.CRYSTALS?List.of(BossCatalog.Tier.values()):EquipmentSets.RANKS;
  int[] slots=RareMenuStyle.slots(tiers.size());
  for(int i=0;i<tiers.size();i++) {
   var tier=tiers.get(i);menu.rankSlots.put(slots[i],tier);
   inventory.setItem(slots[i],PremiumStyle.tile(category==Category.CRYSTALS?PremiumStyle.rankIcon(tier):category.icon,
       tier.display,tier.color,true,category==Category.CRYSTALS?
       (tier==BossCatalog.Tier.COMMON?"Se obtiene de los minijefes.":"Se forja con cristales de categoría anterior."):
       "Filo "+EquipmentSets.sharpness(tier),"Clic para explorar."));
  }
  back(inventory,"Volver a la forja");player.openInventory(inventory);
 }
 private void help(Player player) {
  Menu menu=new Menu(Category.ESSENCES,0,null);menu.help=true;
  Inventory inventory=inventory(menu,"Guía de esencias");
  inventory.setItem(4,PremiumStyle.tile(Material.MAGMA_CREAM,"Mejora tu equipo",NamedTextColor.AQUA,true,
      "Las esencias añaden habilidades a objetos compatibles."));
  inventory.setItem(20,PremiumStyle.tile(Material.CRAFTING_TABLE,"1 · Fabrica una esencia",NamedTextColor.AQUA,false,
      "Consulta su efecto, nivel y equipo compatible.","Abre la receta y reúne los ingredientes."));
  inventory.setItem(22,PremiumStyle.tile(Material.MAGMA_CREAM,"2 · Infunde el equipo",NamedTextColor.AQUA,false,
      "En una mesa de trabajo, combina el equipo con 1–3 esencias compatibles.","El límite de rares aparece en el objeto."));
  inventory.setItem(24,PremiumStyle.tile(Material.NETHERITE_CHESTPLATE,"3 · Equipa y combate",NamedTextColor.AQUA,false,
      "Las habilidades de armadura requieren llevar la pieza puesta.","Las de armas requieren usar el arma compatible."));
  inventory.setItem(31,PremiumStyle.tile(Material.CHEST,"Equipo de jefe",NamedTextColor.GOLD,false,
      "Sus armaduras se consiguen derrotando jefes.","Puedes añadirles esencias compatibles directamente."));
  back(inventory,"Volver a la forja");player.openInventory(inventory);
 }
 private void open(Player player,Category category,int page,ShapedRecipe detail) {open(player,category,page,detail,null,null);}
 private void open(Player player,Category category,int page,ShapedRecipe detail,BossCatalog.Tier rank,RareCompatibility.Group group) {
  settings.refresh();items.migrateCurrencies(player);
  List<ShapedRecipe> list=recipes().stream().filter(recipe->category==null||category.matches(recipe.getKey().getKey()))
      .filter(recipe->rank==null||EquipmentSets.tier(items.customId(recipe.getResult()))==rank)
      .filter(recipe->group==null||RareCompatibility.recipeInGroup(recipe.getKey().getKey(),group)).toList();
  int pageSize=settings.layout().pageSize(),pages=RecipePresentation.pages(list.size(),pageSize);
  page=Math.max(0,Math.min(page,pages-1));Menu menu=new Menu(category,page,detail);menu.rank=rank;menu.group=group;
  String section=group!=null?group.title:rank!=null?rank.display:category==null?settings.layout().title():settings.label(category);
  Inventory inventory=inventory(menu,detail!=null?"Receta":section);
  if(category==null)home(menu,list);else if(detail==null)list(menu,list,pageSize,pages,section);else detail(menu);
  player.openInventory(inventory);
 }
 private void home(Menu menu,List<ShapedRecipe> list) {
  Inventory inventory=menu.inventory;
  inventory.setItem(4,PremiumStyle.tile(Material.ENCHANTING_TABLE,settings.layout().title(),NamedTextColor.AQUA,true,
      "Elige qué quieres crear.","Consulta los ingredientes y fabrica desde aquí."));
  menu.categories.putAll(RecipePresentation.homeCategories(settings.layout().homeSlots()));
  for(var entry:menu.categories.entrySet()) {
   Category category=entry.getValue();long count=list.stream().filter(recipe->category.matches(recipe.getKey().getKey())).count();
   inventory.setItem(entry.getKey(),PremiumStyle.tile(category.icon,settings.label(category),NamedTextColor.AQUA,true,
       RecipePresentation.description(category),count+" recetas · Clic para explorar."));
  }
  inventory.setItem(45,PremiumStyle.tile(Material.BOOK,"Todas las recetas",NamedTextColor.AQUA,false,"Explora el recetario completo."));
  inventory.setItem(47,PremiumStyle.tile(Material.ECHO_SHARD,"Guía de materiales",NamedTextColor.AQUA,false,"Consulta cómo conseguir los materiales."));
  inventory.setItem(49,PremiumStyle.tile(Material.EMERALD,"Mercado de la Grieta",NamedTextColor.GOLD,true,"Intercambia tus tributos y sellos."));
  inventory.setItem(51,PremiumStyle.tile(Material.HEART_OF_THE_SEA,"Corazones de jefe",NamedTextColor.AQUA,false,"Descubre los trofeos más difíciles de conseguir."));
  inventory.setItem(53,PremiumStyle.tile(Material.NETHERITE_AXE,settings.label(Category.PVP),NamedTextColor.AQUA,false,"Equipo especial para combatir."));
 }
 private void list(Menu menu,List<ShapedRecipe> recipes,int pageSize,int pages,String section) {
  Inventory inventory=menu.inventory;
  inventory.setItem(4,PremiumStyle.tile(menu.group==null?menu.category.icon:menu.group.icon,section,NamedTextColor.AQUA,true,
      "Página "+(menu.page+1)+" de "+pages+" · "+recipes.size()+" recetas",
      "Pasa el cursor para ver el efecto.","Clic para ver ingredientes y fabricar."));
  int start=menu.page*pageSize,count=Math.min(pageSize,recipes.size()-start);int[] slots=RareMenuStyle.slots(count);
  for(int i=0;i<count;i++) {
   ShapedRecipe recipe=recipes.get(start+i);inventory.setItem(slots[i],items.refreshAppearance(recipe.getResult().clone()));
   menu.recipes.put(slots[i],recipe);
  }
  if(count==0) {
   if(menu.category==Category.CRYSTALS&&menu.rank==BossCatalog.Tier.COMMON) {
    inventory.setItem(22,items.crystal(BossCatalog.Tier.COMMON,1));
    inventory.setItem(31,PremiumStyle.tile(Material.SPAWNER,"Cristal de minijefe",NamedTextColor.AQUA,false,
        "Derrota un minijefe para conseguirlo.","Combínalos para crear cristales superiores."));
   } else inventory.setItem(22,PremiumStyle.tile(Material.BARRIER,"No hay recetas disponibles",NamedTextColor.GRAY,false,
       "Vuelve para explorar otro apartado."));
  }
  if(menu.page>0)inventory.setItem(45,PremiumStyle.tile(Material.ARROW,"Página anterior",NamedTextColor.AQUA,false));
  if(menu.page+1<pages)inventory.setItem(53,PremiumStyle.tile(Material.ARROW,"Página siguiente",NamedTextColor.AQUA,false));
  if(menu.category==Category.ESSENCES||menu.category==Category.SPELLS)
   inventory.setItem(48,PremiumStyle.tile(Material.BOOK,"Cómo usar las esencias",NamedTextColor.AQUA,false,"Consulta una guía breve."));
  back(inventory,menu.group!=null?"Volver al equipo":menu.rank!=null?"Volver a categorías":"Volver a la forja");
 }
 private void detail(Menu menu) {
  Inventory inventory=menu.inventory;ShapedRecipe recipe=menu.detail;
  inventory.setItem(4,PremiumStyle.tile(Material.CRAFTING_TABLE,"Ingredientes y resultado",NamedTextColor.AQUA,true,
      "Cada casilla consume una unidad.","Fabrica con los materiales de tu inventario."));
  String[] shape=recipe.getShape();Map<Character,RecipeChoice> choices=recipe.getChoiceMap();
  for(int row=0;row<shape.length;row++)for(int column=0;column<shape[row].length();column++) {
   RecipeChoice choice=choices.get(shape[row].charAt(column));if(choice!=null)inventory.setItem(10+row*9+column,choice.getItemStack());
  }
  inventory.setItem(23,PremiumStyle.tile(Material.SPECTRAL_ARROW,"Resultado",NamedTextColor.AQUA,false,"Pasa el cursor por el objeto para ver sus propiedades."));
  inventory.setItem(25,items.refreshAppearance(recipe.getResult().clone()));
  inventory.setItem(40,PremiumStyle.tile(Material.ANVIL,"Fabricar",NamedTextColor.GREEN,true,
      "Consume los ingredientes al confirmar.","Necesitas espacio para recibir el resultado.",
      menu.category==Category.PVP?"La pieza base también se consume.":"Clic para fabricar una receta."));
  String key=recipe.getKey().getKey();
  if(menu.category==Category.PVP||menu.category==Category.MATERIALS&&key.startsWith("pvp_material_"))
   inventory.setItem(43,PremiumStyle.tile(Material.ECHO_SHARD,menu.category==Category.PVP?"Ver su material":"Ver su reliquia",
       NamedTextColor.AQUA,false,"Abre la receta relacionada."));
  back(inventory,"Volver a las recetas");
 }
 @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=false)
 public void click(InventoryClickEvent event) {
  if(!(event.getView().getTopInventory().getHolder() instanceof Menu menu))return;
  event.setCancelled(true);int slot=event.getRawSlot();
  if(!(event.getWhoClicked() instanceof Player player)||!MenuActions.isActivation(event.getClick(),slot,54))return;
  MenuActions.dispatch(plugin,player,menu,menu.gate,()->activate(player,menu,slot));
 }
 private void activate(Player player,Menu menu,int slot) {
  if(menu.category==null) {
   if(slot==45)open(player,Category.ALL,0,null);else if(slot==47)plugin.trophies.open(player,true,0);
   else if(slot==49)plugin.market.open(player);else if(slot==51)plugin.trophies.open(player,false,0);
   else if(slot==53)groupMenu(player,Category.PVP);else if(menu.categories.containsKey(slot))select(player,menu.categories.get(slot));
   return;
  }
  if(slot==RecipePresentation.LIST_BACK) {
   switch(RecipePresentation.parent(menu.detail!=null,menu.groups,menu.ranks,menu.rank,menu.group)) {
    case ROOT -> open(player,null,0,null);
    case GROUPS -> groupMenu(player,menu.category);
    case RANKS -> rankMenu(player,menu.category);
    case LIST -> open(player,menu.category,menu.page,null,menu.rank,menu.group);
   }
   return;
  }
  if(menu.help)return;
  if(menu.groups) {
   if(menu.groupSlots.containsKey(slot))open(player,menu.category,0,null,null,menu.groupSlots.get(slot));
  } else if(menu.ranks) {
   if(menu.rankSlots.containsKey(slot))open(player,menu.category,0,null,menu.rankSlots.get(slot),null);
  } else if(menu.detail!=null) {
   if(slot==40)craft(player,menu.detail);
   else if(slot==43&&(menu.category==Category.PVP||menu.category==Category.MATERIALS)) {
    String key=menu.detail.getKey().getKey();
    String linked=menu.category==Category.PVP?key.replace("pvp_gear_","pvp_material_"):key.replace("pvp_material_","pvp_gear_");
    recipes().stream().filter(recipe->recipe.getKey().getKey().equals(linked)).findFirst().ifPresent(recipe->
        open(player,menu.category==Category.PVP?Category.MATERIALS:Category.PVP,0,recipe,null,
            menu.category==Category.MATERIALS?RareCompatibility.recipe(linked):null));
   }
  } else if(slot==45&&menu.page>0)open(player,menu.category,menu.page-1,null,menu.rank,menu.group);
  else if(slot==53)open(player,menu.category,menu.page+1,null,menu.rank,menu.group);
  else if(slot==48&&(menu.category==Category.ESSENCES||menu.category==Category.SPELLS))help(player);
  else if(menu.recipes.containsKey(slot))open(player,menu.category,menu.page,menu.recipes.get(slot),menu.rank,menu.group);
 }
 private void select(Player player,Category category) {
  if(category==Category.ESSENCES||category==Category.PVP)groupMenu(player,category);
  else if(category==Category.WEAPONS||category==Category.CRYSTALS)rankMenu(player,category);
  else open(player,category,0,null);
 }
 @EventHandler public void drag(InventoryDragEvent event) {
  if(event.getView().getTopInventory().getHolder() instanceof Menu)event.setCancelled(true);
 }
 private void craft(Player player,ShapedRecipe shown){
  if(!player.hasPermission("rareitemhunter.hunter.craft")){player.sendMessage("No tienes permiso para fabricar.");return;}
  if(!RecipePresentation.visible(shown.getKey().getKey())||!(Bukkit.getRecipe(shown.getKey()) instanceof ShapedRecipe recipe)){player.closeInventory();return;}
  items.migrateCurrencies(player);ItemStack[] original=player.getInventory().getStorageContents();ItemStack[] planned=Arrays.stream(original).map(i->i==null?null:i.clone()).toArray(ItemStack[]::new);
  Map<Character,RecipeChoice> choices=recipe.getChoiceMap();List<RecipeChoice> costs=new ArrayList<>();for(String row:recipe.getShape())for(char symbol:row.toCharArray())if(choices.get(symbol)!=null)costs.add(choices.get(symbol));costs.sort(Comparator.comparingInt(c->c instanceof RecipeChoice.ExactChoice?0:1));
  List<java.util.function.IntPredicate> requirements=new ArrayList<>();for(RecipeChoice choice:costs)requirements.add(slot->original[slot]!=null&&choice.test(original[slot])&&(!(choice instanceof RecipeChoice.MaterialChoice)||items.customId(original[slot])==null));
  int[] counts=Arrays.stream(original).mapToInt(i->i==null?0:i.getAmount()).toArray();int[] remainder=CraftPlan.remaining(counts,requirements);if(remainder==null){player.sendMessage("Faltan ingredientes. Los objetos de jefe deben coincidir con la receta.");return;}
  for(int slot=0;slot<planned.length;slot++){if(remainder[slot]==0)planned[slot]=null;else planned[slot].setAmount(remainder[slot]);}
  ItemStack result=recipe.getResult().clone();int remaining=result.getAmount();for(int slot=0;slot<planned.length&&remaining>0;slot++){ItemStack stack=planned[slot];if(stack!=null&&!stack.isSimilar(result))continue;int amount=Math.min(remaining,result.getMaxStackSize()-(stack==null?0:stack.getAmount()));if(amount<=0)continue;if(stack==null){stack=result.clone();stack.setAmount(amount);planned[slot]=stack;}else stack.setAmount(stack.getAmount()+amount);remaining-=amount;}
  if(remaining>0){player.sendMessage("No hay espacio para el resultado.");return;}player.getInventory().setStorageContents(planned);player.playSound(player.getLocation(),Sound.BLOCK_ANVIL_USE,0.5f,1.2f);player.sendMessage("Objeto forjado correctamente.");
 }
}
