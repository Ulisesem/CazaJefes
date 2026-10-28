package dev.cazajefes;
final class BurstCadence {
 static final long COOLDOWN=25000;
 private long next;private boolean initialized;
 void initialize(long now){if(!initialized){initialized=true;next=now+COOLDOWN;}}
 void released(long now){initialized=true;next=now+COOLDOWN;}
 long next(){return next;}
 boolean ready(long now){return initialized&&now>=next;}
}
