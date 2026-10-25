package dev.cazajefes;
final class BattleRules {
 static final int HASTE_AMPLIFIER=5;
 static int strength(BossCatalog.Definition boss){return boss.mini()?2:boss.tier().ordinal()+2;}
 static double strengthBonus(BossCatalog.Definition boss){return 3.0*strength(boss);}
 static final long MELEE_COOLDOWN_MS=500;
 static double threshold(double configured){return Double.isFinite(configured)?Math.max(1,Math.min(2000,configured)):40;}
 static double charge(double current,double damage){return Math.max(0,Math.min(2000,(Double.isFinite(current)?Math.max(0,current):0)+(Double.isFinite(damage)?Math.max(0,damage):0)));}
 static boolean retaliates(int phase,boolean eligible,double charge,double threshold,long now,long next){return phase==3&&eligible&&charge>=threshold(threshold)&&now>=next;}
 static double lightningDamage(double charge,double configuredMax){double max=Double.isFinite(configuredMax)?Math.max(1,Math.min(80,configuredMax)):30;return Math.min(max,12+(Double.isFinite(charge)?Math.max(0,charge):0)*0.3);}
}
