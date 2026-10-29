package dev.cazajefes;
import java.util.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
/** Converts actual drops after accepted breaks; terrain edits publish a build event first. */
final class RareTools implements Listener {
 private final CazaJefesPlugin plugin;private final RelicItems items;
 RareTools(CazaJefesPlugin plugin,RelicItems items){this.plugin=plugin;this.items=items;}
 static String treeFamily(Material type){return type.name().replace("STRIPPED_","").replace("_LOG","").replace("_WOOD","").replace("_STEM","").replace("_HYPHAE","");}
 static Material smelt(Material from){return switch(from){case COBBLESTONE->Material.STONE;case COBBLED_DEEPSLATE->Material.DEEPSLATE;case RAW_IRON->Material.IRON_INGOT;case RAW_GOLD->Material.GOLD_INGOT;case RAW_COPPER->Material.COPPER_INGOT;default->null;};}
 @EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)public void breakTree(BlockBreakEvent event){if(items.connectedBreak())return;Player p=event.getPlayer();if(!CombatGuard.eligible(p))return;ItemStack tool=p.getInventory().getItemInMainHand();Material type=event.getBlock().getType();boolean tree=Tag.LOGS.isTagged(type)&&items.activePropertyLevel(tool,"treefeller")>0,ore=type.name().endsWith("_ORE")||type==Material.ANCIENT_DEBRIS;if(tree&&items.consumeSaturation(p,tool,"treefeller"))items.breakConnected(event,tool,true);else if(ore&&items.activePropertyLevel(tool,"veinminer")>0&&items.consumeSaturation(p,tool,"veinminer"))items.breakConnected(event,tool,false);}
 @EventHandler(priority=EventPriority.HIGH,ignoreCancelled=true)public void drops(BlockDropItemEvent event){Player p=event.getPlayer();if(!CombatGuard.eligible(p))return;ItemStack tool=p.getInventory().getItemInMainHand();boolean moss=items.activePropertyLevel(tool,"spore")>0,forge=items.activePropertyLevel(tool,"smelt")>0;String id=null;for(var item:event.getItems()){Material type=item.getItemStack().getType();if(moss&&type==Material.COBBLESTONE){id="spore";break;}if(forge&&smelt(type)!=null){id="smelt";break;}}if(id==null||!items.consumeSaturation(p,tool,id))return;for(var item:event.getItems()){ItemStack stack=item.getItemStack();Material target=id.equals("spore")?(stack.getType()==Material.COBBLESTONE?Material.MOSSY_COBBLESTONE:null):smelt(stack.getType());if(target!=null){ItemStack converted=new ItemStack(target,stack.getAmount());item.setItemStack(converted);}}}
 @EventHandler(priority=EventPriority.HIGHEST)public void use(PlayerInteractEvent event){if(!SpellInteraction.allowed(event.getAction(),event.isCancelled(),event.useItemInHand())||event.getHand()!=EquipmentSlot.HAND||event.useItemInHand()==Event.Result.DENY||(event.getAction()!=Action.RIGHT_CLICK_BLOCK&&event.getAction()!=Action.RIGHT_CLICK_AIR))return;Player p=event.getPlayer();if(!CombatGuard.eligible(p))return;ItemStack tool=event.getItem();if(tool==null)return;Block block=event.getClickedBlock();if(items.activePropertyLevel(tool,"meltobsidian")>0){var trace=p.getWorld().rayTraceBlocks(p.getEyeLocation(),p.getEyeLocation().getDirection(),6,FluidCollisionMode.ALWAYS,true);if(trace!=null&&trace.getHitBlock()!=null&&trace.getHitBlock().getType()==Material.LAVA)block=trace.getHitBlock();}if(block==null)return;Material from=block.getType(),to=null;String id=null;
  int growth=items.activePropertyLevel(tool,"fertilize");if(growth>0&&(from==Material.DIRT||growth>=2&&(from==Material.COARSE_DIRT||from==Material.ROOTED_DIRT))){to=Material.GRASS_BLOCK;id="fertilize";}
  if(items.activePropertyLevel(tool,"spore")>0&&from==Material.COBBLESTONE){to=Material.MOSSY_COBBLESTONE;id="spore";}
  if(items.activePropertyLevel(tool,"meltobsidian")>0&&from==Material.LAVA){to=Material.OBSIDIAN;id="meltobsidian";}
  if(to==null||items.saturationCost(tool,id)<0||p.getSaturation()<items.saturationCost(tool,id))return;var build=new BlockPlaceEvent(block,block.getState(),block.getRelative(0,-1,0),tool,p,true,event.getHand());plugin.getServer().getPluginManager().callEvent(build);if(build.isCancelled()||!build.canBuild()||!items.consumeSaturation(p,tool,id))return;event.setCancelled(true);block.setType(to);p.playSound(block.getLocation(),Sound.BLOCK_ENCHANTMENT_TABLE_USE,.4f,1.3f);
 }
}
