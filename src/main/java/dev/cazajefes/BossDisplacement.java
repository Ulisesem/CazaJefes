package dev.cazajefes;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.*;
final class BossDisplacement {
 private final PhaseImpulse clock=new PhaseImpulse();
 void tick(LivingEntity boss,BossCatalog.Definition definition,int phase,long now){
  if(!clock.ready(boss.getUniqueId(),phase,now))return;
  Location center=boss.getLocation();boolean hit=false;
  for(Player player:boss.getWorld().getPlayers()){
   if(!CombatGuard.eligible(player)||player.getLocation().distanceSquared(center)>64||!boss.hasLineOfSight(player))continue;
   hit=true;if(!BurstVisuals.movable(player.isOp()))continue;
   Location at=player.getLocation();var motion=PhaseImpulse.motion(phase,at.getX()-center.getX(),at.getZ()-center.getZ());
   player.setVelocity(new Vector(motion.x(),motion.y(),motion.z()));hit=true;
   player.spawnParticle(Particle.GUST,at.clone().add(0,.5,0),6,.35,.4,.35,.015);
   if(phase==3&&!BossWarnings.active(player))BossWarnings.send(player,Component.text("⚠ ATAQUE ESPECIAL · "+BurstVisuals.name(definition),NamedTextColor.WHITE).decorate(TextDecoration.BOLD));
  }
  if(hit){BurstVisuals.show(center.clone().add(0,1,0),3);boss.getWorld().playSound(center,Sound.ENTITY_BREEZE_WIND_BURST,1,phase==2?1.2f:.7f);}
 }
 void remove(java.util.UUID id){clock.remove(id);}void clear(){clock.clear();}
}
