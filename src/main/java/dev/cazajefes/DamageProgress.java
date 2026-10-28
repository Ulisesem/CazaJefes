package dev.cazajefes;
final class DamageProgress {
 static double finishing(double health,double accepted){return Double.isFinite(health)&&Double.isFinite(accepted)&&health>0&&health<=1&&accepted>0?Math.max(accepted,health+.0001):accepted;}
 static double floor(double base,double finalDamage){return Double.isFinite(base)&&Double.isFinite(finalDamage)&&base>0&&finalDamage>0?Math.max(finalDamage,Math.min(1,base*.15)):finalDamage;}
}
