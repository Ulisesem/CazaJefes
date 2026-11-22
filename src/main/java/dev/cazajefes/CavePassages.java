package dev.cazajefes;
import java.util.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
/** Permanent, protected excavation in the direction of pursuit; no restoration journal. */
final class CavePassages {
 private final CazaJefesPlugin plugin;
 CavePassages(CazaJefesPlugin plugin){this.plugin=plugin;}
 boolean open(LivingEntity boss,Player target,Location attempted){
  if(!attempted.isChunkLoaded()||target.getWorld()!=boss.getWorld()||attempted.getWorld()!=boss.getWorld())return false;
  if(plugin.getConfig().getBoolean("combat.respect-mob-griefing",false)&&!Boolean.TRUE.equals(boss.getWorld().getGameRuleValue(GameRule.MOB_GRIEFING)))return false;
  Set<Block> batch=new LinkedHashSet<>();var box=boss.getBoundingBox().shift(attempted.toVector().subtract(boss.getLocation().toVector())).expand(-.001);
  for(int x=(int)Math.floor(box.getMinX());x<=Math.floor(box.getMaxX())&&batch.size()<32;x++)
   for(int y=(int)Math.floor(box.getMinY());y<=Math.floor(box.getMaxY())&&batch.size()<32;y++)
    for(int z=(int)Math.floor(box.getMinZ());z<=Math.floor(box.getMaxZ())&&batch.size()<32;z++){
     if(y<boss.getWorld().getMinHeight()||y>=boss.getWorld().getMaxHeight()||!boss.getWorld().isChunkLoaded(x>>4,z>>4))continue;
     Block block=boss.getWorld().getBlockAt(x,y,z);if(!block.isPassable())batch.add(block);
    }
  var delta=target.getEyeLocation().toVector().subtract(boss.getEyeLocation().toVector());if(delta.lengthSquared()>.01){var hit=boss.getWorld().rayTraceBlocks(boss.getEyeLocation(),delta.clone().normalize(),Math.min(3,delta.length()),FluidCollisionMode.NEVER,true);if(hit!=null&&hit.getHitBlock()!=null)batch.add(hit.getHitBlock());}
  boolean changed=false;
  for(Block block:batch){if(block.getType()==Material.BEDROCK||!ArenaBoundary.allowsTerrain(boss,block.getLocation()))continue;var data=block.getBlockData();if(!new EntityChangeBlockEvent(boss,block,Material.CAVE_AIR.createBlockData()).callEvent()||!data.equals(block.getBlockData()))continue;
   if(block.getState() instanceof org.bukkit.block.Container)block.breakNaturally();else block.setType(Material.CAVE_AIR,false);
   CosmeticFX.emit(Particle.BLOCK,block.getLocation().add(.5,.5,.5),4,.2,.2,.2,0,data);changed=true;
  }
  if(changed)boss.getWorld().playSound(attempted,Sound.BLOCK_STONE_BREAK,.8f,1.25f);return changed;
 }
 void release(UUID ignored){}
 void stop(){}
}
