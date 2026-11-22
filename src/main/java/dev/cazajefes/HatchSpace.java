package dev.cazajefes;
import java.util.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityChangeBlockEvent;
/** Preflights the actual scaled body before an egg can clear its chamber. */
final class HatchSpace {
 static boolean clear(LivingEntity boss,double width,double height,TerrainRestoration terrain){
  var at=boss.getLocation();var box=new org.bukkit.util.BoundingBox(at.getX()-width/2-.15,at.getY(),at.getZ()-width/2-.15,at.getX()+width/2+.15,at.getY()+height+.05,at.getZ()+width/2+.15);var world=boss.getWorld();Map<Block,org.bukkit.block.data.BlockData> blocks=new LinkedHashMap<>();
  long volume=(long)(Math.ceil(box.getMaxX())-Math.floor(box.getMinX())+1)*(long)(Math.ceil(box.getMaxY())-Math.floor(box.getMinY())+1)*(long)(Math.ceil(box.getMaxZ())-Math.floor(box.getMinZ())+1);if(volume>4096)return false;
  for(int x=(int)Math.floor(box.getMinX());x<=Math.floor(box.getMaxX());x++)
   for(int y=Math.max(boss.getLocation().getBlockY(),(int)Math.floor(box.getMinY()));y<=Math.floor(box.getMaxY());y++)
    for(int z=(int)Math.floor(box.getMinZ());z<=Math.floor(box.getMaxZ());z++){
     if(y<world.getMinHeight()||y>=world.getMaxHeight()||!world.isChunkLoaded(x>>4,z>>4))return false;Block block=world.getBlockAt(x,y,z);if(block.isPassable())continue;
     if(block.getType()==Material.BEDROCK||!ArenaBoundary.allowsTerrain(boss,block.getLocation()))return false;blocks.put(block,block.getBlockData());
    }
  for(var entry:blocks.entrySet())if(!new EntityChangeBlockEvent(boss,entry.getKey(),Material.CAVE_AIR.createBlockData()).callEvent()||!entry.getKey().getBlockData().equals(entry.getValue()))return false;
  if(!blocks.isEmpty()&&!terrain.remember(boss,blocks.keySet()))return false;
  for(var entry:blocks.entrySet()){Block block=entry.getKey();block.setType(Material.CAVE_AIR,false);CosmeticFX.emit(Particle.BLOCK,block.getLocation().add(.5,.5,.5),2,.2,.2,.2,0,entry.getValue());}
  if(!blocks.isEmpty()){CosmeticFX.emit(Particle.EXPLOSION_EMITTER,boss.getLocation().add(0,.8,0),1,0,0,0,0);world.playSound(boss.getLocation(),Sound.ENTITY_GENERIC_EXPLODE,1.5f,.65f);}return true;
 }
}
