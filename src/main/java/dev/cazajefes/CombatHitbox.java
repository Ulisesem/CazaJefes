package dev.cazajefes;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.util.*;
final class CombatHitbox {
 static Vector nearest(Vector from,BoundingBox box){return new Vector(Math.max(box.getMinX(),Math.min(box.getMaxX(),from.getX())),Math.max(box.getMinY(),Math.min(box.getMaxY(),from.getY())),Math.max(box.getMinZ(),Math.min(box.getMaxZ(),from.getZ())));}
 static double distanceSquared(Location from,Entity target){return from.toVector().distanceSquared(nearest(from.toVector(),target.getBoundingBox()));}
 static boolean visible(Player player,Entity target){if(!player.getWorld().equals(target.getWorld()))return false;Location origin=player.getEyeLocation();BoundingBox box=target.getBoundingBox();Vector near=nearest(origin.toVector(),box);Vector low=new Vector(box.getCenterX(),box.getMinY()+box.getHeight()*.15,box.getCenterZ());Vector middle=new Vector(box.getCenterX(),box.getMinY()+box.getHeight()*.45,box.getCenterZ());
  for(Vector point:new Vector[]{near,low,middle}){Vector delta=point.clone().subtract(origin.toVector());double distance=delta.length();if(distance<=.15)return true;if(distance>24)continue;if(origin.getWorld().rayTraceBlocks(origin,delta.multiply(1/distance),Math.max(.01,distance-.08),FluidCollisionMode.NEVER,true)==null)return true;}return false;
 }
}
