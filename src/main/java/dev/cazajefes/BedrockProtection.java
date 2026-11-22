package dev.cazajefes;
import org.bukkit.Material;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
/** Protects bedrock in standard block, entity and explosion events, including creative mining. */
final class BedrockProtection implements Listener {
 @EventHandler(priority=EventPriority.HIGHEST)public void mine(BlockBreakEvent event){if(event.getBlock().getType()==Material.BEDROCK){event.setCancelled(true);event.getPlayer().sendActionBar(net.kyori.adventure.text.Component.text("La bedrock está protegida."));}}
 @EventHandler(priority=EventPriority.HIGHEST)public void change(EntityChangeBlockEvent event){if(event.getBlock().getType()==Material.BEDROCK)event.setCancelled(true);}
 @EventHandler(priority=EventPriority.HIGHEST)public void replace(BlockPlaceEvent event){if(event.getBlockReplacedState().getType()==Material.BEDROCK)event.setCancelled(true);}
 @EventHandler(priority=EventPriority.HIGHEST)public void entityExplosion(EntityExplodeEvent event){event.blockList().removeIf(block->block.getType()==Material.BEDROCK);}
 @EventHandler(priority=EventPriority.HIGHEST)public void blockExplosion(BlockExplodeEvent event){event.blockList().removeIf(block->block.getType()==Material.BEDROCK);}
}
