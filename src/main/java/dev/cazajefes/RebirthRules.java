package dev.cazajefes;
final class RebirthRules {
 static boolean mayRebirth(boolean zombie,boolean used,boolean totem,double health,double damage){return zombie&&!used&&totem&&Double.isFinite(health)&&Double.isFinite(damage)&&health>0&&damage>=health;}
 static double restoredHealth(double maximum){return Math.min(maximum,Math.max(1,maximum*.65));}
 static double damage(double base,boolean reborn){return base*(reborn?2:1);}
}
