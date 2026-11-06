package dev.cazajefes.ai;

import org.bukkit.Location;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public final class Geometry {
    private Geometry() {}

    public static boolean sphereIntersects(Vector center,double radius,BoundingBox box){
        // Clamp the center to each axis of the box. That nearest point minimizes the squared
        // Euclidean distance, so this is an exact sphere/AABB test rather than a cube check.
        double x=center.getX()-Math.clamp(center.getX(),box.getMinX(),box.getMaxX());
        double y=center.getY()-Math.clamp(center.getY(),box.getMinY(),box.getMaxY());
        double z=center.getZ()-Math.clamp(center.getZ(),box.getMinZ(),box.getMaxZ());
        return x*x+y*y+z*z<=radius*radius;
    }

    public static double gapSquared(BoundingBox first,BoundingBox second){
        double x=Math.max(0,Math.max(first.getMinX()-second.getMaxX(),second.getMinX()-first.getMaxX()));
        double y=Math.max(0,Math.max(first.getMinY()-second.getMaxY(),second.getMinY()-first.getMaxY()));
        double z=Math.max(0,Math.max(first.getMinZ()-second.getMaxZ(),second.getMinZ()-first.getMaxZ()));
        return x*x+y*y+z*z;
    }

    public static boolean loaded(Location at,double margin){
        var world=at.getWorld();
        if(world==null||at.getY()<world.getMinHeight()||at.getY()>=world.getMaxHeight())return false;
        int fromX=(int)Math.floor(at.getX()-margin)>>4, toX=(int)Math.floor(at.getX()+margin)>>4;
        int fromZ=(int)Math.floor(at.getZ()-margin)>>4, toZ=(int)Math.floor(at.getZ()+margin)>>4;
        for(int x=fromX;x<=toX;x++)for(int z=fromZ;z<=toZ;z++)if(!world.isChunkLoaded(x,z))return false;
        return true;
    }
}
