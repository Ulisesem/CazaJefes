package dev.cazajefes;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;
final class BossTactics {
 private final LivingEntity boss;private final BossCatalog.Definition definition;private final boolean ranged,fox;private int ticks;private long nextPath;private java.util.UUID focus;private long nextFocus;
 BossTactics(LivingEntity boss,BossCatalog.Definition definition){this.boss=boss;this.definition=definition;fox=definition.id().equals("kitsar_acechador_carmesi");ranged=switch(boss.getType()){case SKELETON,STRAY,BOGGED,PILLAGER,WITCH,EVOKER,BLAZE,GHAST->true;default->false;};}
 Player pickTarget(){long now=System.currentTimeMillis();String aggressive=boss.getPersistentDataContainer().get(new NamespacedKey("cazajefes","aggro_target"),org.bukkit.persistence.PersistentDataType.STRING);if(aggressive!=null)try{Player aggressor=Bukkit.getPlayer(java.util.UUID.fromString(aggressive));if(aggressor!=null&&CombatGuard.eligible(aggressor)&&aggressor.getWorld().equals(boss.getWorld())&&CombatHitbox.distanceSquared(aggressor.getEyeLocation(),boss)<=4096){focus=aggressor.getUniqueId();nextFocus=now+2000;return aggressor;}}catch(IllegalArgumentException ignored){}Player prior=focus==null?null:org.bukkit.Bukkit.getPlayer(focus);if(now<nextFocus&&prior!=null&&CombatGuard.eligible(prior)&&prior.getWorld().equals(boss.getWorld())&&prior.getLocation().distanceSquared(boss.getLocation())<=576)return prior;
  var participants=BossParticipation.participants(boss);var candidates=boss.getWorld().getPlayers().stream().filter(p->CombatGuard.eligible(p)&&p.getLocation().distanceSquared(boss.getLocation())<=576).toList();boolean fighting=candidates.stream().anyMatch(p->participants.contains(p.getUniqueId()));Player chosen=candidates.stream().filter(p->!fighting||participants.contains(p.getUniqueId())).min(java.util.Comparator.comparingDouble(p->p.getLocation().distanceSquared(boss.getLocation())*(boss.hasLineOfSight(p)?.7:1))).orElse(null);focus=chosen==null?null:chosen.getUniqueId();nextFocus=now+2000;return chosen;
 }
 void tick(Player target,int phase){
  if(boss instanceof Shulker)return;
  if(boss instanceof Bee bee){bee.setHasStung(false);bee.setAnger(24000);bee.setTarget(target);Vector delta=target.getEyeLocation().toVector().subtract(bee.getLocation().toVector());if(delta.lengthSquared()>.01&&bee.hasLineOfSight(target)){double angle=System.currentTimeMillis()/350.0;Vector forward=delta.clone().normalize();Vector side=new Vector(-forward.getZ(),0,forward.getX());if(side.lengthSquared()>.01)side.normalize();Vector dance=forward.multiply(.40).add(side.multiply(Math.sin(angle)*.25));dance.setY(Math.max(-.4,Math.min(.4,dance.getY()+Math.cos(angle)*.18)));bee.setVelocity(dance);}else bee.getPathfinder().moveTo(target.getLocation(),1.3);return;}
  if(AquaticCombat.swimming(boss)){if(boss instanceof Mob mob)mob.setTarget(target);Vector destination=target.getLocation().add(0,.3,0).toVector().subtract(boss.getLocation().toVector());if(boss.hasLineOfSight(target))boss.setVelocity(AquaticCombat.motion(destination,fox,true));return;}
  ticks++;Location origin=boss.getLocation();Vector lead=target.getVelocity().clone().setY(0).multiply(4);if(lead.lengthSquared()>2)lead.normalize().multiply(1.4);
  Vector delta=target.getLocation().toVector().add(lead).subtract(origin.toVector()).setY(0);double distance=delta.length();if(distance<.2)return;
  Vector forward=delta.normalize();int beat=ticks/5;var move=TacticsRules.move(ranged,distance,beat,phase);Vector direction=forward.clone();
  if(move==TacticsRules.Move.RETREAT)direction.multiply(-1);else if(move==TacticsRules.Move.FLANK){int side=((definition.id().hashCode()+(beat/3))&1)==0?1:-1;direction=new Vector(-forward.getZ()*side,0,forward.getX()*side).add(forward.clone().multiply(distance>4?.45:-.15)).normalize();}
  Location waypoint=origin.clone().add(direction.clone().multiply(2));
  long now=System.currentTimeMillis();if(boss instanceof Mob mob&&now>=nextPath){mob.setTarget(target);mob.getPathfinder().moveTo(move==TacticsRules.Move.APPROACH?target.getLocation():waypoint,fox?1.05:1.2);nextPath=now+700;}
  if(!boss.hasLineOfSight(target))return;
  Block ahead=origin.clone().add(direction.clone().multiply(.9)).getBlock();boolean water=origin.getBlock().isLiquid();boolean obstacle=!ahead.isPassable();
  if(!water&&!obstacle&&origin.clone().add(direction.clone().multiply(1.3)).getBlock().getRelative(0,-1,0).isPassable()&&boss.isOnGround())return;
  double y=boss.getVelocity().getY();if(water)y=Math.max(y,.20);else if(obstacle&&ahead.getRelative(0,1,0).isPassable()&&boss.isOnGround())y=.38;
  double speed=TacticsRules.speed(fox,move,phase);Vector motion=direction.multiply(speed);boss.setVelocity(new Vector(motion.getX(),y,motion.getZ()));
  if(move==TacticsRules.Move.RUSH&&ticks%5==0)CombatVisuals.special(boss,definition);
 }
}
