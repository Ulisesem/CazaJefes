package dev.cazajefes;
import org.bukkit.GameMode;
final class CombatAccess {
 static boolean canAttackBoss(GameMode mode, boolean online, boolean dead, boolean admin, boolean creativeTests) {
  if (!online || dead) return false;
  return mode==GameMode.SURVIVAL || mode==GameMode.ADVENTURE || mode==GameMode.CREATIVE && admin && creativeTests;
 }
}
