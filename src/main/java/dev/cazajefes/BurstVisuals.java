package dev.cazajefes;
import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;
final class BurstVisuals {
 record Point(double x,double y,double z){}
 static List<Point> sphere(double radius){List<Point> out=new ArrayList<>();for(int i=0;i<64;i++){double y=1-2*(i+.5)/64,r=Math.sqrt(1-y*y),angle=i*2.399963229728653;out.add(new Point(radius*Math.cos(angle)*r,radius*y,radius*Math.sin(angle)*r));}return out;}
 static boolean movable(boolean op){return !op;}
 static String name(BossCatalog.Definition boss){return "Nova de la Grieta · "+boss.name();}
 static double waveRadius(double maximum,int frame){return 1.05+(Math.max(1.05,maximum)-1.05)*Math.max(0,Math.min(1,frame/7.0));}
 private static final Set<org.bukkit.scheduler.BukkitTask> tasks=new HashSet<>();
 static void show(org.bukkit.plugin.java.JavaPlugin plugin,Location center,double radius){Location start=center.clone();org.bukkit.util.Vector forward=start.getDirection().setY(0);if(forward.lengthSquared()<.001)forward=new org.bukkit.util.Vector(0,0,1);else forward.normalize();final org.bukkit.util.Vector heading=forward;final org.bukkit.scheduler.BukkitTask[] timer={null};timer[0]=plugin.getServer().getScheduler().runTaskTimer(plugin,new Runnable(){int frame;public void run(){if(frame>=8){tasks.remove(timer[0]);timer[0].cancel();return;}double progress=frame/7.0;Location at=start.clone().add(heading.clone().multiply(progress*.8));var points=sphere(waveRadius(radius,frame++));var dust=new Particle.DustOptions(Color.WHITE,1.4f);for(Player viewer:at.getWorld().getPlayers())if(viewer.getLocation().distanceSquared(at)<=1024)for(var point:points)viewer.spawnParticle(Particle.DUST,at.clone().add(point.x(),point.y(),point.z()),1,0,0,0,0,dust,false);}},1,2);tasks.add(timer[0]);}
 static void stop(){for(var task:tasks)task.cancel();tasks.clear();}
}
