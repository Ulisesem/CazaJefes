package dev.cazajefes;
import java.util.*;
final class PhaseImpulse {
 private record State(int phase,long ready){}
 private final Map<UUID,State> states=new HashMap<>();
 static long cooldown(int phase){return phase==2?10000:phase==3?10000:0;}
 boolean ready(UUID boss,int phase,long now){long delay=cooldown(phase);if(delay==0){states.remove(boss);return false;}State old=states.get(boss);if(old==null||old.phase()!=phase){states.put(boss,new State(phase,now+delay));return false;}if(now<old.ready())return false;states.put(boss,new State(phase,now+delay));return true;}
 record Motion(double x,double y,double z){}
 static Motion motion(int phase,double dx,double dz){double length=Math.hypot(dx,dz);if(!Double.isFinite(length)||length<.0001){dx=0;dz=1;length=1;}double horizontal=phase==2?2.6:.35;return new Motion(dx/length*horizontal,phase==2?.45:1.35,dz/length*horizontal);}
 void remove(UUID id){states.remove(id);}void clear(){states.clear();}
}
