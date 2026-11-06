package dev.cazajefes.ai.attack;

import dev.cazajefes.ai.*;
import dev.cazajefes.ai.visual.BossVisuals.Projectile;
import java.util.ArrayList;
import org.bukkit.entity.Player;

public final class FamilyVolleyAttack extends BossAttack {
    private final Projectile kind;
    private final int salvos,shots,interval;
    private final LinearProjectileAttack projectile;
    private final double area;
    private final boolean guided;

    public FamilyVolleyAttack(String id,String name,AttackCategory category,int weight,long cooldown,double radius,
                              double damage,int warning,Projectile kind,int salvos,int shots,int interval,
                              double speed,double area,boolean guided){
        super(id,name,category,weight,cooldown,radius,damage,warning);
        this.kind=kind;
        this.salvos=salvos;
        this.shots=shots;
        this.interval=interval;
        this.area=area;
        this.guided=guided;
        projectile=new LinearProjectileAttack(id,name,category,weight,cooldown,radius,damage,warning,speed,45);
    }

    @Override public boolean inRange(CustomBoss boss,Player target,double distance){
        return distance<=30&&super.inRange(boss,target,distance);
    }

    @Override public AttackMotion execute(CustomBoss boss,Player target){
        return new AttackMotion(){
            final ArrayList<AttackMotion> active=new ArrayList<>();
            int tick,fired;

            @Override public boolean tick(){
                if(fired<salvos&&tick++%interval==0){
                    fired++;
                    var origin=boss.origin();
                    var facing=target.getLocation().toVector().subtract(origin.toVector()).setY(0);
                    if(facing.lengthSquared()<.001)facing.setX(1);
                    var side=facing.normalize().rotateAroundY(Math.PI/2);
                    for(int i=0;i<shots;i++){
                        var from=origin.clone().add(side.clone().multiply((i-(shots-1)*.5)*.27));
                        active.add(projectile.launch(boss,target,from,kind,guided,area));
                    }
                }
                for(var iterator=active.iterator();iterator.hasNext();){
                    var flight=iterator.next();
                    if(flight.tick()){flight.close();iterator.remove();}
                }
                return fired>=salvos&&active.isEmpty();
            }

            @Override public void close(){active.forEach(AttackMotion::close);active.clear();}
        };
    }
}
