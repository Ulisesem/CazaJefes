package dev.cazajefes;
import java.util.*;
import org.bukkit.util.Vector;
final class CompassArrow {
 static List<Vector> points(Vector heading,double distance){Vector direction=heading.clone().setY(0);if(!Double.isFinite(direction.lengthSquared())||direction.lengthSquared()<.001||distance<.5)return List.of();direction.normalize();Vector side=new Vector(-direction.getZ(),0,direction.getX());List<Vector> points=new ArrayList<>();double previous=-1;for(double offset:new double[]{2.5,5,7.5}){double reach=Math.min(offset,distance);if(reach==previous)continue;previous=reach;Vector tip=direction.clone().multiply(reach);for(int i=0;i<=12;i++){double k=i*.12*Math.min(1,reach/1.8);points.add(tip.clone().subtract(direction.clone().multiply(k)));if(i>0&&i<=7)for(int sign:new int[]{-1,1})points.add(tip.clone().subtract(direction.clone().multiply(k)).add(side.clone().multiply(sign*k*.7)));}}return points;}
 static List<Vector> star(){return List.of(new Vector(.09,0,0),new Vector(-.09,0,0),new Vector(0,.09,0),new Vector(0,-.09,0),new Vector(0,0,.09),new Vector(0,0,-.09));}
}
