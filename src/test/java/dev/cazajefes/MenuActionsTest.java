package dev.cazajefes;
import org.bukkit.event.inventory.ClickType;
import org.junit.Test;
import static org.junit.Assert.*;
public class MenuActionsTest {
 @Test public void acceptsNormalButtonsInTopInventory() {
  assertTrue(MenuActions.isActivation(ClickType.LEFT,0,54));
  assertTrue(MenuActions.isActivation(ClickType.RIGHT,53,54));
 }
 @Test public void bottomInventoryAndOutsideDoNotActivate() {
  assertFalse(MenuActions.isActivation(ClickType.LEFT,-999,54));
  assertFalse(MenuActions.isActivation(ClickType.LEFT,54,54));
  assertFalse(MenuActions.isActivation(ClickType.LEFT,89,54));
 }
 @Test public void alternateActionsDoNotActivate() {
  for (ClickType c:ClickType.values()) if (c!=ClickType.LEFT && c!=ClickType.RIGHT) assertFalse(MenuActions.isActivation(c,40,54));
 }
 @Test public void repeatedClicksCannotQueueTwoPurchases() {
  MenuActionGate gate=new MenuActionGate();
  assertTrue(gate.reserve()); assertFalse(gate.reserve()); gate.release(); assertTrue(gate.reserve());
 }
}
