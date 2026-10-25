package dev.cazajefes;
import org.bukkit.entity.*;
final class AbilityDamage {
 private static final ThreadLocal<Boolean> ACTIVE=new ThreadLocal<>();
 static boolean active(){return Boolean.TRUE.equals(ACTIVE.get());}
 static boolean hit(Player player,LivingEntity boss,double amount){double health=player.getHealth(),absorption=player.getAbsorptionAmount();Boolean previous=ACTIVE.get();ACTIVE.set(true);try{player.damage(amount,boss);return player.getHealth()<health||player.getAbsorptionAmount()<absorption;}finally{if(previous==null)ACTIVE.remove();else ACTIVE.set(previous);}}
}
