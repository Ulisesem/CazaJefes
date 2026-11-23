package dev.cazajefes;
import java.util.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
/** Grants exactly the materials currently available in the recipe menu. */
final class MaterialsCommand implements CommandExecutor,TabCompleter {
 private final RelicItems items;
 MaterialsCommand(RelicItems items){this.items=items;}
 private Map<String,ItemStack> materials(){var out=new TreeMap<String,ItemStack>();Bukkit.recipeIterator().forEachRemaining(recipe->{
  if(recipe instanceof ShapedRecipe shaped&&shaped.getKey().getNamespace().equals("cazajefes")
      &&RaresMenu.Category.category(shaped.getKey().getKey())==RaresMenu.Category.MATERIALS
      &&CraftAvailability.allowed(shaped.getKey().getKey(),shaped.getResult().getType())){
   String id=items.customId(shaped.getResult());if(id!=null)out.put(id,items.refreshAppearance(shaped.getResult().clone()));
  }});return out;}
 public boolean onCommand(CommandSender sender,Command command,String label,String[] args){
  if(!AdminPermissions.allows(sender::hasPermission,"rareitemhunter.admin.equipment")){sender.sendMessage("Necesitas permiso cazajefes.admin.");return true;}
  if(args.length<1||args.length>3){sender.sendMessage("/ri materiales <id|todo> [cantidad 1-64] [jugador]");return true;}
  int amount=1;try{if(args.length>1)amount=Integer.parseInt(args[1]);}catch(NumberFormatException invalid){amount=0;}
  if(amount<1||amount>64){sender.sendMessage("La cantidad debe estar entre 1 y 64.");return true;}
  Player target=args.length>2?Bukkit.getPlayerExact(args[2]):sender instanceof Player p?p:null;
  if(target==null){sender.sendMessage("Indica un jugador conectado.");return true;}
  var catalog=materials();String id=args[0].toLowerCase(Locale.ROOT);var selected=new ArrayList<ItemStack>();
  if(id.equals("todo"))selected.addAll(catalog.values());else if(catalog.containsKey(id))selected.add(catalog.get(id));
  if(selected.isEmpty()){sender.sendMessage("Material inválido. Usa TAB para consultar los IDs.");return true;}
  Inventory planned=Bukkit.createInventory(null,36);planned.setContents(Arrays.stream(target.getInventory().getStorageContents()).map(i->i==null?null:i.clone()).toArray(ItemStack[]::new));
  for(ItemStack material:selected){var gift=material.clone();gift.setAmount(amount);if(!planned.addItem(gift).isEmpty()){sender.sendMessage("No hay espacio suficiente; no se entregó nada.");return true;}}
  target.getInventory().setStorageContents(planned.getContents());sender.sendMessage("Materiales entregados a "+target.getName()+".");return true;
 }
 public List<String> onTabComplete(CommandSender sender,Command command,String label,String[] args){
  if(!AdminPermissions.allows(sender::hasPermission,"rareitemhunter.admin.equipment"))return List.of();
  var values=new ArrayList<String>();if(args.length==1){values.add("todo");values.addAll(materials().keySet());}else if(args.length==2)values.addAll(List.of("1","16","32","64"));else if(args.length==3)Bukkit.getOnlinePlayers().forEach(p->values.add(p.getName()));
  String prefix=args.length==0?"":args[args.length-1].toLowerCase(Locale.ROOT);return values.stream().filter(v->v.toLowerCase(Locale.ROOT).startsWith(prefix)).toList();
 }
}
