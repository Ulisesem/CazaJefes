package dev.cazajefes;
final class SharpnessRules {
 static double bonus(int level){return level<=0?0:.5*level+.5;}
 static double missingBonus(int nativeLevel){return Math.max(0,bonus(10)-bonus(nativeLevel));}
}
