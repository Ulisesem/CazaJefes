package dev.cazajefes;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
final class VectorBack {
 static Vector motion(double dx,double dz){Vector delta=new Vector(dx,0,dz);if(delta.lengthSquared()<.001)delta=new Vector(0,0,1);return delta.normalize().multiply(1.35).setY(1.2);}
 static void push(Player player,Location center){player.setVelocity(motion(player.getLocation().getX()-center.getX(),player.getLocation().getZ()-center.getZ()));}
}
