package dev.cazajefes;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import java.util.function.Predicate;
final class RelevoRoute {
 static Location destination(Location from,Vector side,double distance,Predicate<Location> clear){for(double rise=0;rise<=1.25;rise+=.25){boolean valid=true;for(double y=0;y<=rise;y+=.25)if(!clear.test(from.clone().add(0,y,0))){valid=false;break;}if(!valid)continue;for(double d=.2;d<=distance+.19;d+=.2)if(!clear.test(from.clone().add(side.clone().multiply(Math.min(d,distance))).add(0,rise,0))){valid=false;break;}if(valid)return from.clone().add(side.clone().multiply(distance)).add(0,rise,0);}return null;}
}
