package dev.cazajefes;
import java.util.*;
import org.bukkit.util.Vector;
final class PillagerRules {
 static final int VOLLEY_SIZE=15;
 static final long CHARGE=500,COOLDOWN=12000,HIT_WINDOW=8000,ARROW_LIFETIME=5000;
 static final Map<String,Integer> ENCHANTMENTS=Map.of("quick_charge",10,"multishot",15,"unbreaking",10,"mending",1,"knockback",10,"piercing",10,"infinity",1);
 static List<Vector> fan(Vector forward){Vector aim=forward.clone();if(aim.lengthSquared()<.0001)aim=new Vector(0,0,1);aim.normalize();List<Vector> directions=new ArrayList<>();for(int i=0;i<VOLLEY_SIZE;i++)directions.add(aim.clone().rotateAroundY(Math.toRadians(-24.5+49.0*i/(VOLLEY_SIZE-1))));return directions;}
 static final class Hits {
  private int hits;private long last;private final int threshold;
  Hits(int threshold){this.threshold=Math.max(3,Math.min(20,threshold));}
  void add(long now){if(now-last>HIT_WINDOW)hits=0;last=now;hits=Math.min(threshold,hits+1);}
  boolean charged(long now){if(now-last>HIT_WINDOW)hits=0;return hits>=threshold;}
  void consume(){hits=0;}
 }
}
