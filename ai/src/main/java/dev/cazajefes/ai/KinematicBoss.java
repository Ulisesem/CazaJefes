package dev.cazajefes.ai;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class KinematicBoss extends AbstractCustomBoss {
    private double fall;
    private Vector impulse;
    private boolean hasEnteredWater,grounded;
    private int idleTicks;

    public KinematicBoss(LivingEntity body,BossProfile profile,BossEnvironment environment){
        super(body,profile,environment);
    }

    public void push(Vector velocity){
        impulse=velocity.clone();
        grounded=false;
        stun(16);
    }

    private void applyImpulse(){
        Location next=body().getLocation();
        int steps=Math.max(1,(int)Math.ceil(impulse.length()/.25));
        Vector step=impulse.clone().multiply(1.0/steps);
        for(int i=0;i<steps;i++){
            Location vertical=next.clone().add(0,step.getY(),0);
            if(clear(vertical))next=vertical;
            else {
                impulse.setY(0);
                step.setY(0);
            }
            Location lateral=next.clone().add(step.getX(),0,step.getZ());
            if(clear(lateral))next=lateral;
            else {
                impulse.setX(0).setZ(0);
                step.setX(0).setZ(0);
            }
        }
        body().teleport(next);
        impulse.setX(impulse.getX()*.82).setZ(impulse.getZ()*.82);
        if(Math.abs(impulse.getY())<.001&&impulse.lengthSquared()<.004){
            impulse=null;
            fall=0;
        }else impulse.setY(Math.max(-.9,impulse.getY()-.15));
    }

    @Override protected void move(Player target,boolean approach){
        if(body().getVelocity().lengthSquared()>.0001)body().setVelocity(new Vector());
        if(impulse!=null){applyImpulse();return;}
        if(target==null&&grounded&&++idleTicks%10!=0)return;
        Location origin=body().getLocation();
        Vector direction=target==null?new Vector():target.getEyeLocation().toVector().subtract(body().getEyeLocation().toVector());
        boolean wet=body().isInWater()||water(origin);
        if(wet)hasEnteredWater=true;
        boolean swim=profile().aquatic()&&wet;
        double distance=direction.length();
        Vector horizontal=new Vector(direction.getX(),0,direction.getZ());
        if(!approach||distance<2.1||horizontal.lengthSquared()<.0001)horizontal.zero();
        else horizontal.normalize().multiply(profile().speed());
        fall=swim?(approach?Math.clamp(direction.getY()*.12,-.18,.18):0)
            :Math.max(-.9,fall-profile().gravity());
        Location next=origin.clone();
        Location y=origin.clone().add(0,fall,0);
        if(clear(y)){
            next=y;
            grounded=swim||fall==0;
        }else{
            grounded=true;
            fall=0;
        }
        Location horizontalMove=next.clone().add(horizontal);
        if(horizontal.lengthSquared()>.0001){
            if(clear(horizontalMove))next=horizontalMove;
            else if(!swim&&clear(horizontalMove.clone().add(0,.56,0)))next=horizontalMove.add(0,.56,0);
            else {
                var side=new Vector(-horizontal.getZ(),0,horizontal.getX());
                if(clear(next.clone().add(side)))next.add(side);
                else if(clear(next.clone().subtract(side)))next.subtract(side);
            }
        }
        if(distance>.01)next.setDirection(direction);
        if(next.distanceSquared(origin)>.000001){
            body().teleport(next);
            body().setVelocity(new Vector());
        }else if(Math.abs(next.getYaw()-origin.getYaw())>1||Math.abs(next.getPitch()-origin.getPitch())>1){
            body().setRotation(next.getYaw(),next.getPitch());
        }
    }

    private boolean clear(Location destination){
        return Geometry.loaded(destination,body().getWidth()*.5) && environment().mayMove(this,destination)
            && (!profile().aquatic()||!hasEnteredWater||water(destination))
            && !body().collidesAt(destination);
    }

    private static boolean water(Location at){
        if(!at.isChunkLoaded())return false;
        var block=at.getBlock();
        return switch(block.getType()){
            case WATER, BUBBLE_COLUMN, KELP, KELP_PLANT, SEAGRASS, TALL_SEAGRASS -> true;
            default -> block.getBlockData() instanceof org.bukkit.block.data.Waterlogged waterlogged && waterlogged.isWaterlogged();
        };
    }
}
