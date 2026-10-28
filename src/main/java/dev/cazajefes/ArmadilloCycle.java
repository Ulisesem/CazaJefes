package dev.cazajefes;
final class ArmadilloCycle {
 static final long COOLDOWN=15000,DURATION=5000;private final long cooldown,duration;private long next,end;private boolean initialized;
 ArmadilloCycle(){this(COOLDOWN,DURATION);}
 ArmadilloCycle(long cooldown,long duration){this.cooldown=cooldown;this.duration=duration;}
 int tick(long now,boolean mayBegin){if(!initialized){next=now+cooldown;initialized=true;}if(end>0&&now>=end){end=0;return 2;}if(end==0&&mayBegin&&now>=next){end=now+duration;next=now+cooldown;return 1;}return 0;}
 boolean active(){return end>0;}
 static double healing(double maximum){return Double.isFinite(maximum)?Math.max(0,Math.min(8,maximum*.005)):0;}
}
