package dev.cazajefes.ai.attack;
import dev.cazajefes.ai.*;
import org.bukkit.entity.Player;
public final class MagnetismAttack extends BossAttack {
 public MagnetismAttack(String id,String name,int weight,long cooldown,double radius,int warning){super(id,name,AttackCategory.MIXED,weight,cooldown,radius,0,warning);}
 public boolean inRange(CustomBoss boss,Player target,double distance){return distance<=radius();}
 public AttackMotion execute(CustomBoss boss,Player target){boss.environment().impact(boss,this,boss.body().getLocation());return new AttackMotion(){int age;public boolean tick(){if(++age>20)return true;for(Player player:boss.body().getWorld().getNearbyPlayers(boss.body().getLocation(),radius())){if(!boss.environment().validTarget(boss,player)||!boss.environment().mayHit(boss,player))continue;var pull=pull(boss.body().getLocation().toVector(),player.getLocation().toVector(),radius());if(pull!=null)player.setVelocity(pull);}return false;}public void close(){}};}
 public static org.bukkit.util.Vector pull(org.bukkit.util.Vector center,org.bukkit.util.Vector target,double radius){var pull=center.clone().subtract(target);double distance=pull.length();if(distance<=2||distance>radius)return null;pull.normalize().multiply(Math.min(1.1,.3+distance*.025));pull.setY(Math.max(-.35,Math.min(.45,pull.getY()+.12)));return pull;}

}
