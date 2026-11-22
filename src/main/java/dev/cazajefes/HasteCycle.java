package dev.cazajefes;
final class HasteCycle {
 static int remaining(int age,int duration,int cooldown){duration=Math.max(20,Math.min(2400,duration));cooldown=Math.max(20,Math.min(12000,cooldown));int phase=Math.floorMod(age,duration+cooldown);return phase<duration?duration-phase:0;}
}
