package dev.cazajefes;

import dev.cazajefes.integration.PacketBridge;
import org.bukkit.*;
import org.bukkit.entity.Player;
/** Shared bounded audience for combat cosmetics; gameplay never depends on this budget. */
final class CosmeticFX {
 private static final AuraBudget budget=new AuraBudget(), warnings=new AuraBudget();private static long second;private static int sent,warningSent;
 static synchronized boolean allow(java.util.UUID viewer,int count,long now){roll(now);if(sent+count>3072||!budget.spend(viewer,count,now,320))return false;sent+=count;return true;}
 private static void roll(long now){if(now/1000!=second){second=now/1000;sent=0;warningSent=0;}}
 static synchronized boolean allowWarning(java.util.UUID viewer,long now){
  roll(now);
  if(warningSent>=2048||!warnings.spend(viewer,1,now,320))return false;
  warningSent++;
  return true;
 }
 static void warning(Player player,Location at,Particle.DustTransition color){
  warning(player,at,null,color);
 }
 static void warning(Player player,Location at,Particle particle,Particle.DustTransition color){
  if(player.getWorld().equals(at.getWorld())&&player.getLocation().distanceSquared(at)<=1024
      &&allowWarning(player.getUniqueId(),System.currentTimeMillis())) {
   if(particle==null)PacketBridge.particle(player,Particle.DUST_COLOR_TRANSITION,at,1,0,0,0,0,color);
   else PacketBridge.particle(player,particle,at,1,0,0,0,0,null);
  }
 }
 static <T> void to(Player p,Particle type,Location at,int count,double x,double y,double z,double speed,T data){if(!p.getWorld().equals(at.getWorld())||p.getLocation().distanceSquared(at)>1024||!allow(p.getUniqueId(),Math.max(1,count),System.currentTimeMillis()))return;PacketBridge.particle(p,type,at,count,x,y,z,speed,data);}
 static <T> void emit(Particle type,Location at,int count,double x,double y,double z,double speed,T data){for(Player p:at.getWorld().getNearbyPlayers(at,32))to(p,type,at,count,x,y,z,speed,data);}
 static void emit(Particle type,Location at,int count,double x,double y,double z,double speed){emit(type,at,count,x,y,z,speed,null);}
 static void emit(Particle type,Location at,int count){emit(type,at,count,0,0,0,0,null);}
 static void emit(Particle type,Location at,int count,double x,double y,double z){emit(type,at,count,x,y,z,0,null);}
 static void forget(java.util.UUID id){budget.remove(id);warnings.remove(id);}
 static void clear(){budget.clear();warnings.clear();sent=0;warningSent=0;second=0;}
}
