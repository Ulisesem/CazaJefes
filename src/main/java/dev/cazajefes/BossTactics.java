package dev.cazajefes;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;
final class BossTactics {
 private final LivingEntity boss;private final BossCatalog.Definition definition;private final boolean ranged,fox;private int ticks;private long nextPath;
 BossTactics(LivingEntity boss,BossCatalog.Definition definition){this.boss=boss;this.definition=definition;fox=definition.legacyId().equals("zorro_salvaje");ranged=switch(boss.getType()){case SKELETON,STRAY,BOGGED,PILLAGER,WITCH,EVOKER,BLAZE,GHAST->true;default->false;};}
 void tick(Player target,int phase){
  if(boss instanceof Shulker)return;
  if(AquaticCombat.swimming(boss)){if(boss instanceof Mob mob)mob.setTarget(target);Vector destination=target.getLocation().add(0,.3,0).toVector().subtract(boss.getLocation().toVector());if(boss.hasLineOfSight(target))boss.setVelocity(AquaticCombat.motion(destination,fox));return;}
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
