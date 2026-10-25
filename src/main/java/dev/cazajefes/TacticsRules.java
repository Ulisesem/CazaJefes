package dev.cazajefes;
final class TacticsRules {
 enum Move { APPROACH, FLANK, RETREAT, RUSH }
 static Move move(boolean ranged,double distance,int beat,int phase){if(ranged&&distance<5)return Move.RETREAT;if(distance>10)return Move.APPROACH;if(phase>=2&&beat%4==3&&distance>3)return Move.RUSH;return distance<7&&beat%3!=0?Move.FLANK:Move.APPROACH;}
 static double speed(boolean fox,Move move,int phase){return fox?(move==Move.RUSH?.40:.24):(move==Move.RUSH?.52:.24+.035*phase);}
}
