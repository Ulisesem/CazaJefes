package dev.cazajefes;
import java.util.*;
import org.bukkit.util.Vector;
final class PillagerRules {
 static final int VOLLEY_SIZE=30;
 static final long CHARGE=500,COOLDOWN=12000,HIT_WINDOW=8000,ARROW_LIFETIME=3000;
 static final Map<String,Integer> ENCHANTMENTS=Map.of("quick_charge",10,"multishot",15,"unbreaking",10,"mending",1,"knockback",10,"piercing",10,"infinity",1,"power",10);
 static final int BURST_COUNT=6,MAX_LIVE_ARROWS=192;
 static final long BURST_GAP=250;
 static final double ARROW_SPEED=3.2;
 static List<Vector> fan(Vector forward){Vector aim=forward.clone();if(aim.lengthSquared()<.0001)aim=new Vector(0,0,1);aim.normalize();Vector spreadAxis=Math.abs(aim.getY())>.95?new Vector(1,0,0):new Vector(0,1,0);List<Vector> out=new ArrayList<>();out.add(aim.clone());for(int i=1;i<29;i++){int pair=(i-1)/2+1;double sign=i%2==1?-1:1;out.add(aim.clone().rotateAroundAxis(spreadAxis,Math.toRadians(sign*pair*24.5/14)));}Vector axis=aim.clone().crossProduct(new Vector(0,1,0));if(axis.lengthSquared()<.001)axis=new Vector(1,0,0);out.add(aim.clone().rotateAroundAxis(axis.normalize(),.012));return out;}
 static Vector predict(Vector position,Vector velocity,double ticks,int levitationLevel,boolean airborne){Vector at=position.clone(),v=velocity.clone();if(!Double.isFinite(v.lengthSquared()))v=new Vector();if(v.length()>3)v.normalize().multiply(3);int steps=(int)Math.ceil(Math.max(0,Math.min(24,ticks)));for(int i=0;i<steps;i++){double fraction=Math.min(1,ticks-i);at.add(v.clone().multiply(fraction));if(airborne){double y=levitationLevel>0?(v.getY()+(.05*levitationLevel-v.getY())*.2)*.98:(v.getY()-.08)*.98;v.multiply(.91).setY(y);}}return at;}
 static double arrowDrop(double ticks){double fall=0,vertical=0;for(int i=0;i<Math.ceil(ticks);i++){double fraction=Math.min(1,ticks-i);fall+=vertical*fraction;vertical=vertical*.99-.05;}return -fall;}
 static Vector aim(Vector from,Vector target,Vector velocity,double speed,int levitationLevel,boolean airborne){double time=Math.max(.1,Math.min(20,from.distance(target)/speed));Vector delta=target.clone().subtract(from);for(int i=0;i<6;i++){delta=predict(target,velocity,time,levitationLevel,airborne).subtract(from);time=Math.max(.1,Math.min(20,delta.length()/(speed*Math.max(.85,1-.005*time))));}delta=predict(target,velocity,time,levitationLevel,airborne).subtract(from);delta.setY(delta.getY()+arrowDrop(time));return delta.lengthSquared()<.0001?new Vector(0,0,1):delta.normalize();}
 static final class Burst {
  private int remaining,fired;private long next;
  void start(long now){remaining=BURST_COUNT;fired=0;next=now;}
  boolean take(long now){if(remaining==0||now<next)return false;remaining--;fired++;next=now+BURST_GAP;return true;}
  boolean active(){return remaining>0;}int fired(){return fired;}void cancel(){remaining=0;}
 }
 static final class Hits {
  private int hits;private long last;private final int threshold;
  Hits(int threshold){this.threshold=Math.max(3,Math.min(20,threshold));}
  void add(long now){if(now-last>HIT_WINDOW)hits=0;last=now;hits=Math.min(threshold,hits+1);}
  boolean charged(long now){if(now-last>HIT_WINDOW)hits=0;return hits>=threshold;}
  void consume(){hits=0;}
 }
}
