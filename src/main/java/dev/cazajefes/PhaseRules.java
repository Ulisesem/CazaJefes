package dev.cazajefes;
final class PhaseRules {
 static int phase(double ratio) {return ratio<=0.30?3:ratio<=0.65?2:1;}
 static double outgoing(int phase) {return phase>=3?1.80:phase==2?1.35:1.0;}
 static double incoming(int phase) {return phase>=3?0.65:phase==2?0.82:1.0;}
 static boolean mayDisrupt(int phase,boolean survival,boolean inventoryFree,long now,long nextRoll,long cooldown,double roll,double chance) {
  return phase==3&&survival&&inventoryFree&&now>=nextRoll&&now>=cooldown&&roll<LootRules.probability(chance);
 }
}
