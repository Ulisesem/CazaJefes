package dev.cazajefes;
import java.util.*;
final class AuraShape {
 record Point(double x,double y,double z,boolean accent){}
 static List<Point> points(double width,double height,double angle){double radius=Math.max(.8,width*.5+.75),h=Math.max(1,height);List<Point> out=new ArrayList<>();
  for(int i=0;i<24;i++){double a=angle+i*Math.PI/12;out.add(new Point(Math.cos(a)*radius,.22,Math.sin(a)*radius,false));}
  for(int arm=0;arm<2;arm++)for(int i=0;i<12;i++){double y=(i+.5)/12*h,a=angle*1.7+i*.65+arm*Math.PI;out.add(new Point(Math.cos(a)*radius,y,Math.sin(a)*radius,arm==1));}
  for(int i=0;i<12;i++){double a=-angle+i*Math.PI/6;out.add(new Point(Math.cos(a)*radius*.7,h+.3,Math.sin(a)*radius*.7,true));}for(int i=0;i<12;i++){double a=-angle*1.3+i*Math.PI/6;out.add(new Point(Math.cos(a)*radius*1.18,h*.48,Math.sin(a)*radius*1.18,true));}return out;
 }
}
