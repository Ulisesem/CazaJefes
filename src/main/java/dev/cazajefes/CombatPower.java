package dev.cazajefes;
final class CombatPower {
 static final int BURST_LEVEL=10;
 static double lift(int phase){return 2.35+Math.max(1,Math.min(3,phase))*.1;}
 static double repel(){return 3.1;}
 static double special(double attack,int tier,int phase,boolean ultimate){double baseline=Double.isFinite(attack)?Math.max(18,attack):18;return Math.min(200,Math.max(baseline,18+tier*5)*(ultimate?2.15:1.6+phase*.1));}
 static double minionHealth(int tier){return 120+Math.max(0,Math.min(5,tier))*20;}
 static double minionHitLimit(double health){return Math.max(1,health*.4);}
}
