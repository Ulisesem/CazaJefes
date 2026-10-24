package dev.cazajefes;
import org.bukkit.GameMode;
import org.junit.Test;
import static org.junit.Assert.*;
public class CombatAccessTest {
 @Test public void survivalAndAdventureDoNotRequireAdmin() {
  assertTrue(CombatAccess.canAttackBoss(GameMode.SURVIVAL,true,false,false,false));
  assertTrue(CombatAccess.canAttackBoss(GameMode.ADVENTURE,true,false,false,false));
 }
 @Test public void creativeTestsRequireAdminAndEnabledOption() {
  assertTrue(CombatAccess.canAttackBoss(GameMode.CREATIVE,true,false,true,true));
  assertFalse(CombatAccess.canAttackBoss(GameMode.CREATIVE,true,false,false,true));
  assertFalse(CombatAccess.canAttackBoss(GameMode.CREATIVE,true,false,true,false));
 }
 @Test public void spectatorDeadAndOfflineCannotEarnCredit() {
  assertFalse(CombatAccess.canAttackBoss(GameMode.SPECTATOR,true,false,true,true));
  assertFalse(CombatAccess.canAttackBoss(GameMode.CREATIVE,true,true,true,true));
  assertFalse(CombatAccess.canAttackBoss(GameMode.SURVIVAL,false,false,true,true));
 }
}
