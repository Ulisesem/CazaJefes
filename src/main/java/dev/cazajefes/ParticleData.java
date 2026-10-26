package dev.cazajefes;
import org.bukkit.*;
final class ParticleData {
 static Object simple(Particle particle){Class<?> type=particle.getDataType();if(type==Void.class)return null;if(type==Float.class)return Float.valueOf(1.0f);throw new IllegalArgumentException("La partícula "+particle+" requiere datos explícitos de tipo "+type.getSimpleName());}
 static void spawn(World world,Particle particle,Location location,int count,double x,double y,double z,double speed){world.spawnParticle(particle,location,count,x,y,z,speed,simple(particle));}
}
