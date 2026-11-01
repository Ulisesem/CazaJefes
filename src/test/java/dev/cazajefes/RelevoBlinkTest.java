package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
import org.bukkit.Location;
import org.bukkit.util.Vector;
public class RelevoBlinkTest {
 @Test public void interveningWallDoesNotBlockFreeDestination(){Location to=RelevoRoute.destination(new Location(null,0,0,0),new Vector(1,0,0),4,at->at.getX()>3);assertNotNull(to);assertEquals(4,to.getX(),.00001);}
 @Test public void blockedRightSideFindsAnotherDirectionWithoutIncreasingHorizontalRange(){Location to=RelevoRoute.destination(new Location(null,0,0,0),new Vector(1,0,0),3,at->at.getX()<-2.9);assertNotNull(to);assertEquals(3,Math.hypot(to.getX(),to.getZ()),.00001);assertNull(RelevoRoute.destination(new Location(null,0,0,0),new Vector(1,0,0),3,at->false));}
}
