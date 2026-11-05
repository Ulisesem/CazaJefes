package dev.cazajefes;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;
final class BossTactics {
 private final LivingEntity boss;private final BossCatalog.Definition definition;private final boolean ranged,fox;private int ticks;private long nextPath;
 BossTactics(LivingEntity boss,BossCatalog.Definition definition){this.boss=boss;this.definition=definition;if(definition.mini()&&boss instanceof Mob mob)PassiveHostility.prepare(mob);fox=definition.id().equals("kitsar_acechador_carmesi");ranged=switch(boss.getType()){case SKELETON,STRAY,BOGGED,PILLAGER,WITCH,EVOKER,BLAZE,GHAST->true;default->false;};}
 Player pickTarget(){return ThreatTable.pick(boss, 64, player -> true);}
 void tick(Player target,int phase){
  if(boss instanceof Shulker)return;
  if(boss instanceof Bee bee){bee.setHasStung(false);bee.setAnger(24000);bee.setTarget(target);Vector delta=target.getEyeLocation().toVector().subtract(bee.getLocation().toVector());if(delta.lengthSquared()>.01&&bee.hasLineOfSight(target)){double angle=System.currentTimeMillis()/350.0;Vector forward=delta.clone().normalize();Vector side=new Vector(-forward.getZ(),0,forward.getX());if(side.lengthSquared()>.01)side.normalize();Vector dance=forward.multiply(.40).add(side.multiply(Math.sin(angle)*.25));dance.setY(Math.max(-.4,Math.min(.4,dance.getY()+Math.cos(angle)*.18)));bee.setVelocity(dance);}else bee.getPathfinder().moveTo(target.getLocation(),1.3);return;}
  if(AquaticCombat.swimming(boss)){if(boss instanceof Mob mob){mob.setTarget(target);if(System.currentTimeMillis()>=nextPath){mob.getPathfinder().moveTo(target.getLocation(),1.5);nextPath=System.currentTimeMillis()+500;}}boss.setSwimming(true);Vector destination=target.getEyeLocation().toVector().subtract(boss.getEyeLocation().toVector());Vector swim=AquaticCombat.pursuit(destination,fox);if(CombatHitbox.visible(target,boss)&&EncounterSafety.clear(boss,boss.getLocation().add(swim)))boss.setVelocity(swim);return;}
  ticks++;Location origin=boss.getLocation();Vector lead=target.getVelocity().clone().setY(0).multiply(4);if(lead.lengthSquared()>2)lead.normalize().multiply(1.4);
  Vector delta=target.getLocation().toVector().add(lead).subtract(origin.toVector()).setY(0);double distance=delta.length();if(distance<.2)return;
  Vector forward=delta.normalize();int beat=ticks/5;var move=TacticsRules.move(ranged,distance,beat,phase);if(definition.mini()&&!ranged)move=distance>3?TacticsRules.Move.RUSH:TacticsRules.Move.APPROACH;if(!ranged&&target.isBlocking()&&distance<5)move=TacticsRules.Move.FLANK;Vector direction=forward.clone();
  if(move==TacticsRules.Move.RETREAT)direction.multiply(-1);else if(move==TacticsRules.Move.FLANK){int side=((definition.id().hashCode()+(beat/3))&1)==0?1:-1;direction=new Vector(-forward.getZ()*side,0,forward.getX()*side).add(forward.clone().multiply(distance>4?.45:-.15)).normalize();}
  Location waypoint=origin.clone().add(direction.clone().multiply(2));
  long now=System.currentTimeMillis();if(boss instanceof Mob mob&&now>=nextPath){mob.setTarget(target);mob.getPathfinder().moveTo(move==TacticsRules.Move.APPROACH?target.getLocation():waypoint,fox?1.05:1.2);nextPath=now+500;}
  if(!CombatHitbox.visible(target,boss))return;
  Block ahead=origin.clone().add(direction.clone().multiply(.9)).getBlock();boolean water=origin.getBlock().isLiquid();boolean obstacle=!ahead.isPassable();
  if(!water&&!obstacle&&origin.clone().add(direction.clone().multiply(1.3)).getBlock().getRelative(0,-1,0).isPassable()&&boss.isOnGround())return;
  double y=boss.getVelocity().getY();if(water)y=Math.max(y,.20);else if(obstacle&&ahead.getRelative(0,1,0).isPassable()&&boss.isOnGround())y=.38;
  double speed=TacticsRules.speed(fox,move,phase);if(definition.mini()&&!fox)speed=Math.max(speed,.34+phase*.02);Vector motion=direction.multiply(speed);boss.setVelocity(new Vector(motion.getX(),y,motion.getZ()));
  if(move==TacticsRules.Move.RUSH&&ticks%5==0)CombatVisuals.special(boss,definition);
 }
}
