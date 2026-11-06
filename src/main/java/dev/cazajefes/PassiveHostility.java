package dev.cazajefes;
import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Mob;
import org.bukkit.entity.memory.MemoryKey;
final class PassiveHostility {
 static boolean retreatGoal(String key){return key.equals("panic")||key.equals("avoid_entity")||key.equals("tempt")||key.equals("breed")||key.equals("follow_parent")||key.equals("run_around_like_crazy")
    ||key.equals("look_at_player")||key.equals("random_look_around")||key.contains("flee")||key.contains("panic");}
 static void clearFear(Mob mob){
  mob.setMemory(MemoryKey.IS_PANICKING,null);
  mob.setMemory(MemoryKey.DANGER_DETECTED_RECENTLY,null);
  mob.setMemory(MemoryKey.PACIFIED,null);
  mob.setMemory(MemoryKey.HIDING_PLACE,null);
  mob.setMemory(MemoryKey.PLAY_DEAD_TICKS,null);
  if(mob instanceof org.bukkit.entity.Piglin piglin){
   piglin.setImmuneToZombification(true);
   piglin.setIsAbleToHunt(false);
   piglin.setMemory(MemoryKey.ADMIRING_ITEM,null);
   piglin.setMemory(MemoryKey.ADMIRING_DISABLED,true);
   piglin.setMemory(MemoryKey.DISABLE_WALK_TO_ADMIRE_ITEM,true);
  }
 }
 static void prepare(Mob mob){var goals=Bukkit.getMobGoals();for(var goal:new ArrayList<>(goals.getAllGoals(mob)))if(retreatGoal(goal.getKey().getNamespacedKey().getKey()))goals.removeGoal(mob,goal);mob.setAware(true);}
}
