package dev.cazajefes.ai;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public final class KinematicBoss extends AbstractCustomBoss {
    private double fall;
    private Vector impulse;
    private boolean hasEnteredWater,grounded;
    private int idleTicks,blockedTicks,verticalBlockedTicks,strafe=1,pursuitTicks;
    private double jump;
    private int farmingResponseTicks; // tracks how long we've been trying to reach an elevated player

    // Ranged-melee weapon swap controller
    private final RangedMeleeSwap weaponSwap = new RangedMeleeSwap();

    // Magnetism controller: pulls players toward the boss under certain conditions
    private final BossMagnetism magnetism = new BossMagnetism();

    // Retreat run-away: tracks whether we are running away facing forward
    private int retreatRunTicks; // how long we've been in retreat-run mode
    private double retreatYaw;   // target yaw when running away (180° from target)

    public KinematicBoss(LivingEntity body,BossProfile profile,BossEnvironment environment){
        super(body,profile,environment);
        // Configure weapon swap from the boss profile
        weaponSwap.configure(profile.rangedMeleeSwap());
        // Configure magnetism from the boss profile
        magnetism.configure(profile.magnetism());
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
        if(pursuitTicks>0)pursuitTicks--;

        // Evaluate weapon swap (ranged ↔ melee) based on distance
        if(target!=null&&approach) weaponSwap.evaluate(this,target);

        // Advance magnetism tick and evaluate pull
        magnetism.tick();

        Vector direction=target==null?new Vector():target.getEyeLocation().toVector().subtract(body().getEyeLocation().toVector());
        boolean wet=body().isInWater()||water(origin);
        if(wet)hasEnteredWater=true;
        boolean swim=profile().aquatic()&&wet;
        double distance=direction.length();
        Vector horizontal=new Vector(direction.getX(),0,direction.getZ());
        if(!approach||horizontal.lengthSquared()<.0001)horizontal.zero();
        else {
            var tactics=profile().tactics();
            double groundDistance=horizontal.length();
            horizontal.normalize().multiply(profile().speed());
            CombatStyle.Tactics.Move moveDecision=tactics.move(groundDistance,pursuitTicks==0&&environment().canSee(this,target));

            if(moveDecision==CombatStyle.Tactics.Move.RETREAT){
                // Run-away retreat: turn 180° and run forward instead of backing up
                retreatRunTicks++;
                // Direction away from target (same as backward, but we rotate to face it)
                horizontal.multiply(-1.0);
            } else {
                // Not retreating — reset retreat state
                if(retreatRunTicks>0) retreatRunTicks=0;
                switch(moveDecision){
                    case APPROACH -> {}
                    case RETREAT -> {} // handled above
                    case ORBIT -> horizontal.rotateAroundY(strafe*Math.PI/2).multiply(tactics.orbit());
                }
            }
        }
        
        // Detect farming: player is elevated and out of melee range
        boolean playerElevated=approach&&target!=null&&target.getY()-origin.getY()>1.5
            &&origin.distanceSquared(target.getLocation())<=64;
        if(playerElevated)farmingResponseTicks++;
        else farmingResponseTicks=Math.max(0,farmingResponseTicks-2);
        
        // When farming detected, increase jump frequency and try to break blocks beneath player
        double jumpThreshold=farmingResponseTicks>30?2.0:1.0; // jump more aggressively when farming
        if(approach&&target!=null&&!swim&&grounded&&pursuitTicks>0&&target.getY()-origin.getY()>jumpThreshold
            &&origin.distanceSquared(target.getLocation())<=49)jump=.58;
        
        // Also try jumping toward elevated players even without pursuitTicks
        if(approach&&target!=null&&!swim&&grounded&&farmingResponseTicks>20
            &&target.getY()-origin.getY()>1.5&&origin.distanceSquared(target.getLocation())<=36
            &&jump==0)jump=.52;
        
        fall=jump>0?jump:swim?(approach?Math.clamp(direction.getY()*.12,-.18,.18):0)
            :Math.max(-.9,fall-profile().gravity());
        if(jump>0)jump=Math.max(0,jump-.10);
        Location next=origin.clone();
        Location y=origin.clone().add(0,fall,0);
        if(clear(y)){
            next=y;
            grounded=swim||fall==0;
        }else{
            grounded=true;
            fall=0;
            jump=0;
        }
        Location horizontalMove=next.clone().add(horizontal);
        if(horizontal.lengthSquared()>.0001){
            if(clear(horizontalMove)){next=horizontalMove;blockedTicks=0;}
            else if(!swim&&grounded&&clear(horizontalMove.clone().add(0,1.02,0))){
                next=horizontalMove.add(0,1.02,0);
                blockedTicks=0;
            }
            else {
                var side=new Vector(-horizontal.getZ(),0,horizontal.getX());
                if(++blockedTicks%3==0&&target!=null&&(pursuitTicks>0||!environment().canSee(this,target))
                    &&environment().blocked(this,target,horizontalMove))pursuitTicks=50;
                if(clear(horizontalMove))next=horizontalMove;
                else if(clear(next.clone().add(side.clone().multiply(strafe))))next.add(side.multiply(strafe));
                else if(clear(next.clone().subtract(side.clone().multiply(strafe)))){
                    next.subtract(side.multiply(strafe));
                    strafe=-strafe;
                }
            }
        }
        // Aggressive vertical block breaking when player is farming above
        if(approach&&target!=null&&!swim&&Math.abs(target.getY()-origin.getY())>=1.5
            &&origin.distanceSquared(target.getLocation())<=49){
            // More frequent block breaking attempts when farming is detected
            int breakInterval=farmingResponseTicks>30?2:4;
            if(++verticalBlockedTicks%breakInterval==0){
                Location above=origin.clone().add(0,Math.copySign(.75,target.getY()-origin.getY()),0);
                if(environment().blocked(this,target,above)){
                    pursuitTicks=50;
                    // Also try to break blocks directly under the player to collapse their platform
                    if(farmingResponseTicks>40){
                        Location underPlayer=target.getLocation().clone().subtract(0,.5,0);
                        environment().blocked(this,target,underPlayer);
                    }
                }
            }
        }else verticalBlockedTicks=0;
        // Rotation: when retreating, face away from target (run forward, not backward)
        if(distance>.01){
            if(retreatRunTicks>0&&target!=null){
                // Calculate yaw facing away from target (180° from target direction)
                Vector away=origin.toVector().subtract(target.getLocation().toVector()).setY(0);
                if(away.lengthSquared()>.0001){
                    double yaw=Math.toDegrees(Math.atan2(away.getX(),away.getZ()));
                    next.setYaw((float)yaw);
                }
            } else {
                next.setDirection(direction);
            }
        }
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

    /**
     * Apply magnetism pull toward the boss if conditions are met.
     * Called from AbstractCustomBoss with the detected player behavior.
     */
    @Override protected void applyMagnetism(Player target,BehaviorTracker.PlayerBehavior behavior,int phase){
        if(target!=null) magnetism.evaluate(this,target,behavior,phase);
    }
}
