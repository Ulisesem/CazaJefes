package dev.cazajefes;
import java.util.*;
final class AuraShape {
 record Point(double x,double y,double z,boolean accent){}
 static List<Point> points(double width,double height,double angle){return sampled(width,height,angle,72);}
 static List<Point> sampled(double width,double height,double angle,int count){if(count<=0)return List.of();count=Math.min(72,count);List<Point> out=new ArrayList<>(count);double r=Math.max(.8,width*.5+.75),h=Math.max(1,height);
  for(int n=0;n<count;n++){int i=n*72/count;if(i<24){double a=angle+i*Math.PI/12;out.add(new Point(Math.cos(a)*r,.22,Math.sin(a)*r,false));}
   else if(i<48){int arm=(i-24)/12,k=(i-24)%12;double y=(k+.5)/12*h,a=angle*1.7+k*.65+arm*Math.PI;out.add(new Point(Math.cos(a)*r,y,Math.sin(a)*r,arm==1));}
   else if(i<60){double a=-angle+(i-48)*Math.PI/6;out.add(new Point(Math.cos(a)*r*.7,h+.3,Math.sin(a)*r*.7,true));}
   else{double a=-angle*1.3+(i-60)*Math.PI/6;out.add(new Point(Math.cos(a)*r*1.18,h*.48,Math.sin(a)*r*1.18,true));}
  }return out;
 }
}
