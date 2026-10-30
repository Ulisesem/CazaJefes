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
 static List<Point> scaledSphere(double horizontal,double vertical){List<Point> out=new ArrayList<>();for(var point:sphere(1))out.add(new Point(point.x()*horizontal,point.y()*vertical,point.z()*horizontal));return out;}
 static void show(org.bukkit.plugin.java.JavaPlugin plugin,Location center,double radius){animate(plugin,center,1.05,1.05,Math.max(1.05,radius));}
 static void showBoss(org.bukkit.plugin.java.JavaPlugin plugin,LivingEntity boss){var box=boss.getBoundingBox();Location center=new Location(boss.getWorld(),box.getCenterX(),box.getCenterY(),box.getCenterZ());center.setDirection(boss.getLocation().getDirection());double horizontal=Math.max(.8,Math.max(box.getWidthX(),box.getWidthZ())/2+.25),vertical=Math.max(.8,box.getHeight()/2+.25);follow(plugin,boss,horizontal,vertical);}
 static void showBook(org.bukkit.plugin.java.JavaPlugin plugin,Player player){var box=player.getBoundingBox();double r=Math.max(box.getHeight()/2,Math.max(box.getWidthX(),box.getWidthZ())/2)+.35;follow(plugin,player,r,r);}
 private static void animate(org.bukkit.plugin.java.JavaPlugin plugin,Location center,double bodyRadius,double bodyHeight,double maximum){render(plugin,center,null,bodyRadius,bodyHeight);}
 private static void follow(org.bukkit.plugin.java.JavaPlugin plugin,LivingEntity entity,double radius,double height){render(plugin,entity.getLocation(),entity,radius,height);}
 private static void render(org.bukkit.plugin.java.JavaPlugin plugin,Location center,LivingEntity entity,double radius,double height){final org.bukkit.scheduler.BukkitTask[] timer={null};timer[0]=plugin.getServer().getScheduler().runTaskTimer(plugin,new Runnable(){int frame;public void run(){if(frame>=4||(entity!=null&&(!entity.isValid()||entity.isDead()||!entity.getWorld().equals(center.getWorld())))){tasks.remove(timer[0]);timer[0].cancel();return;}Location at=center.clone();if(entity!=null){var box=entity.getBoundingBox();at=new Location(entity.getWorld(),box.getCenterX(),box.getCenterY(),box.getCenterZ());}double expansion=1+frame++*.40;for(Player viewer:at.getWorld().getPlayers())if(viewer.getLocation().distanceSquared(at)<=1024)for(Point point:scaledSphere(radius*expansion,height*expansion))CosmeticFX.to(viewer,Particle.END_ROD,at.clone().add(point.x(),point.y(),point.z()),1,0,0,0,0,null);}},1,4);tasks.add(timer[0]);}
 static List<Point> microSphere(double radius,double height){List<Point> points=new ArrayList<>();for(int i=0;i<24;i++){double y=1-2*(i+.5)/24,r=Math.sqrt(1-y*y),a=i*2.399963229728653;points.add(new Point(Math.cos(a)*r*radius,y*height,Math.sin(a)*r*radius));}return points;}
 static List<Point> bodyRings(double radius,double halfHeight,double angle){List<Point> out=new ArrayList<>();for(int band=0;band<4;band++)for(int i=0;i<8;i++){double a=i*Math.PI/4+angle+band*.2;out.add(new Point(Math.cos(a)*radius,-halfHeight+2*halfHeight*band/3,Math.sin(a)*radius));}return out;}
 static List<Point> star(double size){return List.of(new Point(-size,0,0),new Point(size,0,0),new Point(0,-size,0),new Point(0,size,0));}
 static void stop(){for(var task:tasks)task.cancel();tasks.clear();}
}
