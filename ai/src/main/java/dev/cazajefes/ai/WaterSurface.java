package dev.cazajefes.ai;
import org.bukkit.*;
/** Virtual support: no blocks are placed, replaced or frozen. */
public final class WaterSurface {
 private static boolean water(org.bukkit.block.Block block){return block.getType()==Material.WATER||block.getType()==Material.BUBBLE_COLUMN;}
 public static Double level(Location at){if(!at.isChunkLoaded())return null;var block=at.getBlock();if(!water(block))block=at.clone().subtract(0,.2,0).getBlock();if(!water(block))return null;for(int i=0;i<16&&block.getY()+1<at.getWorld().getMaxHeight()&&water(block.getRelative(0,1,0));i++)block=block.getRelative(0,1,0);return block.getY()+1.0;}
 public static double rise(double feet,double surface){return Math.max(-.12,Math.min(.3,surface-feet));}
}
