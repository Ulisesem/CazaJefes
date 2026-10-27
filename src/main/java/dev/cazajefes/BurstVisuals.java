package dev.cazajefes;
import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;
final class BurstVisuals {
 record Point(double x,double y,double z){}
 static List<Point> sphere(double radius){List<Point> out=new ArrayList<>();for(int i=0;i<64;i++){double y=1-2*(i+.5)/64,r=Math.sqrt(1-y*y),angle=i*2.399963229728653;out.add(new Point(radius*Math.cos(angle)*r,radius*y,radius*Math.sin(angle)*r));}return out;}
 static boolean movable(boolean op){return !op;}
 static String name(BossCatalog.Definition boss){return "Nova de la Grieta · "+boss.name();}
 static void show(Location center,double radius){var points=sphere(radius);var dust=new Particle.DustOptions(Color.WHITE,1.35f);for(Player viewer:center.getWorld().getPlayers())if(viewer.getLocation().distanceSquared(center)<=1024)for(var point:points)viewer.spawnParticle(Particle.DUST,center.clone().add(point.x(),point.y(),point.z()),1,0,0,0,0,dust,false);}
}
