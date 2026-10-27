package dev.cazajefes;
import org.bukkit.event.block.Action;
import org.bukkit.event.Event.Result;
final class SpellInteraction {
 static boolean allowed(Action action,boolean cancelled,Result itemUse){return itemUse!=Result.DENY&&(action==Action.RIGHT_CLICK_AIR||action==Action.RIGHT_CLICK_BLOCK&&!cancelled);}
}
