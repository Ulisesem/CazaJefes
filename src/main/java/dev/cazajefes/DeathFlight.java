package dev.cazajefes;
final class DeathFlight {
 static final int PEAK=60,END=80;
 static double height(int tick,double rise){if(tick<=0||tick>=END)return 0;double t=tick<=PEAK?tick/(double)PEAK:(END-tick)/(double)(END-PEAK);return Math.max(0,rise)*t;}
 static double damage(double roll){return 6+Math.max(0,Math.min(1,roll));}
 static boolean inRange(double squared){return Double.isFinite(squared)&&squared>=0&&squared<=36;}
}
