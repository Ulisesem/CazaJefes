package dev.cazajefes;
import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Mob;
final class PassiveHostility {
 static boolean retreatGoal(String key){return key.equals("panic")||key.equals("avoid_entity")||key.equals("tempt")||key.equals("breed")||key.equals("follow_parent");}
 static void prepare(Mob mob){var goals=Bukkit.getMobGoals();for(var goal:new ArrayList<>(goals.getAllGoals(mob)))if(retreatGoal(goal.getKey().getNamespacedKey().getKey()))goals.removeGoal(mob,goal);mob.setAware(true);}
}
