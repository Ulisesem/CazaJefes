package dev.cazajefes;
final class MageRules {static final int RAYS=6;static final long HEAL_COOLDOWN=15000;static double healing(double max){return Double.isFinite(max)?Math.max(0,max*.03):0;}static double rayDamage(int phase,boolean reborn){return 6*PhaseRules.finalBonus(phase,reborn);}}
