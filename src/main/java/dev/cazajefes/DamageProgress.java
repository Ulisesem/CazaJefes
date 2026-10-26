package dev.cazajefes;
final class DamageProgress {
 static double floor(double base,double finalDamage){return Double.isFinite(base)&&Double.isFinite(finalDamage)&&base>0&&finalDamage>0?Math.max(finalDamage,Math.min(1,base*.15)):finalDamage;}
}
