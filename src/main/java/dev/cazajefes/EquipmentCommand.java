package dev.cazajefes;
import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
final class EquipmentCommand implements CommandExecutor,TabCompleter {
 private final RelicItems items;EquipmentCommand(RelicItems items){this.items=items;}
 static BossCatalog.Tier parse(String name){return switch(name.toLowerCase(Locale.ROOT)){case "epico"->BossCatalog.Tier.EPIC;case "legendario"->BossCatalog.Tier.LEGENDARY;case "mitico"->BossCatalog.Tier.MYTHIC;case "abismal"->BossCatalog.Tier.ABYSSAL;default->null;};}
 public boolean onCommand(CommandSender sender,Command command,String label,String[] args){
  if(!AdminPermissions.allows(sender::hasPermission,"rareitemhunter.admin.equipment")){sender.sendMessage("Necesitas permiso cazajefes.admin.");return true;}
  if(args.length<1||args.length>3||parse(args[0])==null){sender.sendMessage("/riequipo <epico|legendario|mitico|abismal> [armadura|espada|hacha|todo] [jugador]");return true;}
  String part=args.length>1?args[1].toLowerCase(Locale.ROOT):"todo";if(!List.of("armadura","espada","hacha","todo").contains(part)){sender.sendMessage("Pieza: armadura, espada, hacha o todo.");return true;}
  Player player=args.length==3?Bukkit.getPlayerExact(args[2]):sender instanceof Player p?p:null;if(player==null){sender.sendMessage("Indica un jugador conectado.");return true;}
  var rank=parse(args[0]);List<ItemStack> gifts=new ArrayList<>();if(part.equals("todo")||part.equals("armadura"))for(int i=0;i<4;i++)gifts.add(items.armorSet(rank,i));if(part.equals("todo")||part.equals("espada"))gifts.add(items.weaponSet(rank,0));if(part.equals("todo")||part.equals("hacha"))gifts.add(items.weaponSet(rank,1));
  ItemStack[] storage=Arrays.stream(player.getInventory().getStorageContents()).map(i->i==null?null:i.clone()).toArray(ItemStack[]::new);
  for(ItemStack gift:gifts){int free=-1;for(int i=0;i<storage.length;i++)if(storage[i]==null||storage[i].getType().isAir()){free=i;break;}if(free<0){sender.sendMessage("El jugador necesita "+gifts.size()+" espacios libres. No se entregó nada.");return true;}storage[free]=gift;}
  player.getInventory().setStorageContents(storage);sender.sendMessage("✦ Equipo "+rank.display+" entregado a "+player.getName()+".");return true;
 }
 public List<String> onTabComplete(CommandSender sender,Command command,String alias,String[] args){if(!AdminPermissions.allows(sender::hasPermission,"rareitemhunter.admin.equipment"))return List.of();List<String> options=args.length==1?List.of("epico","legendario","mitico","abismal"):args.length==2?List.of("armadura","espada","hacha","todo"):args.length==3?Bukkit.getOnlinePlayers().stream().map(Player::getName).toList():List.of();String prefix=args[args.length-1].toLowerCase(Locale.ROOT);return options.stream().filter(s->s.toLowerCase(Locale.ROOT).startsWith(prefix)).toList();}
}
