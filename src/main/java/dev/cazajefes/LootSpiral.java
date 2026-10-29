package dev.cazajefes;
final class LootSpiral {
 static final int DURATION=160;
 record Point(double x,double y,double z){}
 static Point at(int ticks,double phase){return at(ticks,phase,16);}
 static Point at(int ticks,double phase,double height){double t=Math.max(0,Math.min(1,ticks/(double)DURATION));double radius=2.2*(1-t),angle=phase+t*Math.PI*6;return new Point(Math.cos(angle)*radius,.3+(Math.max(.3,Math.min(16,height))-.3)*(1-t),Math.sin(angle)*radius);}
}
