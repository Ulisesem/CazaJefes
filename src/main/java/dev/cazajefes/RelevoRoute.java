package dev.cazajefes;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import java.util.function.Predicate;
/** A blink checks its destination; intervening obstacles do not block activation. */
final class RelevoRoute {
 static Location destination(Location from,Vector side,double distance,Predicate<Location> clear){for(double angle:new double[]{0,30,-30,60,-60,90,-90,180}){Vector direction=side.clone().rotateAroundY(Math.toRadians(angle));for(double offset:new double[]{0,.25,-.25,.5,-.5,.75,-.75,1,-1,1.5,-1.5,2,-2,2.5,-2.5,3,-3}){Location at=from.clone().add(direction.clone().multiply(distance)).add(0,offset,0);if(clear.test(at))return at;}}return null;}
}
