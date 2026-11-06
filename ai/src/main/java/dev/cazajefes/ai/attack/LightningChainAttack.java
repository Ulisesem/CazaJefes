package dev.cazajefes.ai.attack;

import dev.cazajefes.ai.*;
import org.bukkit.entity.Player;

public final class LightningChainAttack extends BossAttack {
    private final int strikes,interval,minimumPhase;
    public LightningChainAttack(String id,String name,AttackCategory category,int weight,long cooldown,
                                double radius,double damage,int warning,int strikes,int interval,int minimumPhase){
        super(id,name,category,weight,cooldown,radius,damage,warning);
        this.strikes=strikes;
        this.interval=interval;
        this.minimumPhase=minimumPhase;
    }

    @Override public boolean inRange(CustomBoss boss,Player target,double distance){
        return boss.environment().phase(boss)>=minimumPhase&&distance<=radius()&&super.inRange(boss,target,distance);
    }

    @Override public AttackMotion execute(CustomBoss boss,Player target){
        return new AttackMotion(){
            int tick,fired;
            @Override public boolean tick(){
                if(tick++%interval!=0)return false;
                if(inRange(boss,target,boss.body().getLocation().distance(target.getLocation()))
                    &&boss.environment().canSee(boss,target)&&boss.environment().mayHit(boss,target)){
                    boss.environment().lightning(boss,target.getLocation());
                    boss.environment().damage(boss,target,damage());
                }
                return ++fired>=strikes;
            }
            @Override public void close(){}
        };
    }
}
