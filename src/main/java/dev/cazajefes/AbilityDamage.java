package dev.cazajefes;
import org.bukkit.entity.*;
final class AbilityDamage {
 private record Reflected(java.util.UUID boss,java.util.UUID player) {}
 private static final ThreadLocal<Reflected> REFLECTED = new ThreadLocal<>();
 static boolean reflected(Entity source, Entity target) {
  Reflected pair=REFLECTED.get();
  return pair!=null && pair.boss().equals(source.getUniqueId()) && pair.player().equals(target.getUniqueId());
 }
 static boolean hit(Player player,LivingEntity boss,double amount,boolean reflected){
  if(!reflected)return hit(player,boss,amount);
  Reflected previous=REFLECTED.get();
  REFLECTED.set(new Reflected(boss.getUniqueId(),player.getUniqueId()));
  try{return hit(player,boss,amount);}
  finally{if(previous==null)REFLECTED.remove();else REFLECTED.set(previous);}
 }
 private static final ThreadLocal<Boolean> ACTIVE=new ThreadLocal<>();
 static boolean active(){return Boolean.TRUE.equals(ACTIVE.get());}
 static boolean hit(Player player,LivingEntity boss,double amount){double health=player.getHealth(),absorption=player.getAbsorptionAmount();Boolean previous=ACTIVE.get();ACTIVE.set(true);try{player.damage(amount,boss);return player.getHealth()<health||player.getAbsorptionAmount()<absorption;}finally{if(previous==null)ACTIVE.remove();else ACTIVE.set(previous);}}
}
