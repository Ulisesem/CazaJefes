package dev.cazajefes;
import org.bukkit.*;
import org.bukkit.entity.Player;
/** Shared bounded audience for combat cosmetics; gameplay never depends on this budget. */
final class CosmeticFX {
 private static final AuraBudget budget=new AuraBudget();private static long second;private static int sent;
 static synchronized boolean allow(java.util.UUID viewer,int count,long now){if(now/1000!=second){second=now/1000;sent=0;}if(sent+count>4096||!budget.spend(viewer,count,now,384))return false;sent+=count;return true;}
 static <T> void to(Player p,Particle type,Location at,int count,double x,double y,double z,double speed,T data){if(!p.getWorld().equals(at.getWorld())||p.getLocation().distanceSquared(at)>1024||!allow(p.getUniqueId(),Math.max(1,count),System.currentTimeMillis()))return;p.spawnParticle(type,at,count,x,y,z,speed,data,false);}
 static <T> void emit(Particle type,Location at,int count,double x,double y,double z,double speed,T data){for(Player p:at.getWorld().getPlayers())to(p,type,at,count,x,y,z,speed,data);}
 static void emit(Particle type,Location at,int count,double x,double y,double z,double speed){emit(type,at,count,x,y,z,speed,null);}
 static void emit(Particle type,Location at,int count){emit(type,at,count,0,0,0,0,null);}
 static void emit(Particle type,Location at,int count,double x,double y,double z){emit(type,at,count,x,y,z,0,null);}
 static void forget(java.util.UUID id){budget.remove(id);}
 static void clear(){budget.clear();sent=0;second=0;}
}
