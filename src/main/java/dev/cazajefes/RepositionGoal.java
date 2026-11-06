package dev.cazajefes;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;

import java.util.EnumSet;

final class RepositionGoal implements Goal<Mob> {
   private static final GoalKey<Mob> KEY = GoalKey.of(Mob.class,new NamespacedKey("cazajefes","reposition"));
   private final Mob mob;
   private Location waypoint;
   private int expires;

   private RepositionGoal(Mob mob) { this.mob=mob; }

   static boolean request(Mob mob,Location waypoint) {
      if (!waypoint.isChunkLoaded() || !waypoint.getBlock().isPassable()
          || !waypoint.clone().subtract(0,1,0).getBlock().getType().isSolid()) return false;
      var goals=Bukkit.getMobGoals();
      var registered=goals.getGoal(mob,KEY);
      if (registered instanceof RepositionGoal goal && goal.shouldActivate()) return true;
      if (registered != null) goals.removeGoal(mob,KEY);
      var goal=new RepositionGoal(mob);
      goal.waypoint=waypoint;
      goal.expires=Bukkit.getCurrentTick()+18;
      goals.addGoal(mob,0,goal);
      return true;
   }

   static void remove(Mob mob) { Bukkit.getMobGoals().removeGoal(mob,KEY); }

   @Override public GoalKey<Mob> getKey() { return KEY; }
   @Override public EnumSet<GoalType> getTypes() { return EnumSet.of(GoalType.MOVE); }

   @Override public boolean shouldActivate() {
      return mob.isValid() && mob.getTarget()!=null && !mob.isInvulnerable() && waypoint!=null
         && waypoint.getWorld()==mob.getWorld() && waypoint.isChunkLoaded() && Bukkit.getCurrentTick()<expires
         && mob.getLocation().distanceSquared(waypoint)>.64;
   }

   @Override public boolean shouldStayActive() { return shouldActivate(); }

   @Override public void start() { mob.getPathfinder().moveTo(waypoint,1.2); }

   @Override public void stop() { mob.getPathfinder().stopPathfinding(); }
}
