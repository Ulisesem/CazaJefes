package dev.cazajefes;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.*;
import org.bukkit.util.Vector;
final class AquaticCombat implements Listener {
 private static final NamespacedKey BOSS=new NamespacedKey("cazajefes",BossKeys.BOSS);
 static boolean aquatic(EntityType type){return switch(type){case DROWNED,GUARDIAN,ELDER_GUARDIAN,DOLPHIN,SQUID,GLOW_SQUID,AXOLOTL,TURTLE,COD,SALMON,PUFFERFISH,TROPICAL_FISH,TADPOLE,NAUTILUS->true;default->false;};}
 static boolean swimming(LivingEntity boss){return boss.getLocation().getBlock().isLiquid()||boss.getEyeLocation().getBlock().isLiquid();}
 static void maintain(LivingEntity boss){if(!swimming(boss))return;boss.setRemainingAir(boss.getMaximumAir());var movement=boss.getAttribute(Attribute.WATER_MOVEMENT_EFFICIENCY);if(movement!=null)movement.setBaseValue(1);for(var type:new PotionEffectType[]{PotionEffectType.WATER_BREATHING,PotionEffectType.DOLPHINS_GRACE}){var effect=boss.getPotionEffect(type);if(effect==null||effect.getDuration()<40)boss.addPotionEffect(new PotionEffect(type,100,0,true,false));}}
 static Vector pursuit(Vector delta,boolean fox){if(delta.lengthSquared()<.01)return new Vector();double speed=fox?.4:.65;Vector result=delta.clone().normalize().multiply(speed);result.setY(Math.max(-.55,Math.min(.45,delta.getY()*.22)));return result;}
 static Vector motion(Vector delta,boolean fox,boolean aquatic){Vector motion=motion(delta,fox);return aquatic?motion.multiply(1.8):motion;}
 static Vector motion(Vector delta,boolean fox){double limit=fox?.30:.42;if(delta.lengthSquared()<.01)return new Vector();Vector result=delta.clone().normalize().multiply(limit);result.setY(Math.max(-.25,Math.min(.25,result.getY())));return result;}
 static boolean water(Location at){
  if(!at.isChunkLoaded())return false;
  var block=at.getBlock();
  return block.getType()==Material.WATER || block.getType()==Material.KELP
      || block.getType()==Material.KELP_PLANT || block.getType()==Material.SEAGRASS
      || block.getType()==Material.TALL_SEAGRASS || block.getType()==Material.BUBBLE_COLUMN
      || block.getBlockData() instanceof org.bukkit.block.data.Waterlogged data && data.isWaterlogged();
 }
 static boolean openWater(Location at,double margin){
  return water(at)&&water(at.clone().add(margin,0,0))&&water(at.clone().add(-margin,0,0))
      &&water(at.clone().add(0,0,margin))&&water(at.clone().add(0,0,-margin));
 }
 static void avoidShore(LivingEntity boss, Player target){
  Location origin=boss.getLocation();
  double margin=Math.clamp(boss.getWidth()*.5,.6,2);
  var velocity=boss.getVelocity();
  boolean targetInWater=water(target.getLocation())&&openWater(target.getLocation(),margin);
  if(boss instanceof Mob mob){
   mob.getPathfinder().stopPathfinding();
   mob.setTarget(targetInWater?target:null);
  }
  Vector wanted=target.getLocation().toVector().subtract(origin.toVector());
  Vector swim=pursuit(wanted,false);
  if(targetInWater&&openWater(origin.clone().add(swim.clone().multiply(2)),margin)
      &&EncounterSafety.clear(boss,origin.clone().add(swim))){
   boss.setVelocity(swim);return;
  }
  // Find a direction back into the pool even when knockback has reached the shore.
  for (int i=0;i<8;i++){
   double angle=i*Math.PI/4;
   Vector inward=new Vector(Math.cos(angle),-.12,Math.sin(angle));
   if(openWater(origin.clone().add(inward.clone().multiply(2)),margin)
       &&EncounterSafety.clear(boss,origin.clone().add(inward))){
    boss.setVelocity(inward.multiply(.36));return;
   }
  }
  if(!openWater(origin.clone().add(velocity.clone().multiply(2)),margin))boss.setVelocity(new Vector());
 }
 @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
 public void rejectShoreTarget(EntityTargetLivingEntityEvent event){
  if(event.getEntity() instanceof LivingEntity boss&&aquatic(boss.getType())&&boss.isInWater()
      &&boss.getPersistentDataContainer().has(BOSS,PersistentDataType.BYTE)&&event.getTarget() instanceof Player player
      &&!openWater(player.getLocation(),Math.clamp(boss.getWidth()*.5,.6,2)))event.setCancelled(true);
 }
 @EventHandler(priority=EventPriority.LOWEST,ignoreCancelled=true)public void waterDamage(EntityDamageEvent event){if(event.getEntity() instanceof LivingEntity boss&&boss.getPersistentDataContainer().has(BOSS,PersistentDataType.BYTE)&&(event.getCause()==EntityDamageEvent.DamageCause.DROWNING||(event.getCause()==EntityDamageEvent.DamageCause.CUSTOM&&!(event instanceof EntityDamageByEntityEvent)&&swimming(boss))))event.setCancelled(true);}
 @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)public void waterTeleport(EntityTeleportEvent event){if(event.getEntity() instanceof LivingEntity boss&&boss.getPersistentDataContainer().has(BOSS,PersistentDataType.BYTE)&&swimming(boss)&&(event.getTo()==null||!water(event.getTo())))event.setCancelled(true);}
}
