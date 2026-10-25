package dev.cazajefes;
import java.util.*;
final class AuraShape {
 record Point(double x,double y,double z,boolean accent){}
 static List<Point> points(double width,double height,double angle){double radius=Math.max(.8,width*.5+.45),h=Math.max(1,height);List<Point> out=new ArrayList<>();
  for(int i=0;i<16;i++){double a=angle+i*Math.PI/8;out.add(new Point(Math.cos(a)*radius,.22,Math.sin(a)*radius,false));}
  for(int arm=0;arm<2;arm++)for(int i=0;i<8;i++){double y=(i+.5)/8*h,a=angle*1.7+i*.65+arm*Math.PI;out.add(new Point(Math.cos(a)*radius,y,Math.sin(a)*radius,arm==1));}
  for(int i=0;i<8;i++){double a=-angle+i*Math.PI/4;out.add(new Point(Math.cos(a)*radius*.7,h+.3,Math.sin(a)*radius*.7,true));}return out;
 }
}
