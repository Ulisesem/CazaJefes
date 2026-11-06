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

 static List<Point> themed(BossStyle style,double width,double height,double angle,int count,int phase) {
  if(count<=0)return List.of();
  int total=Math.min(72,count);
  var out=new ArrayList<Point>(total);
  double radius=Math.clamp(width*.5+.65,.8,5), h=Math.clamp(height,1,8);
  for(int i=0;i<total;i++){
   double fraction=i/(double)total;
   double theta=angle+Math.PI*2*fraction;
   double r=radius, y=.25;
   boolean accent=i%3==0;
   switch(style){
    case EXECUTIONER, BASTION -> {
     if(phase==3)r*=.82+.22*Math.cos(theta*6);
     y+=accent ? h*(.16+.18*Math.sin(theta*3+angle)) : .08;
    }
    case ARCANE, VOID -> {
     int band=i%2;
     theta=(band==0?angle:-angle)+Math.PI*2*(i/2)/(total/2.0);
     r*=.83+.16*Math.cos(theta*(style==BossStyle.ARCANE?4:3));
     y=band==0?.22:h*(phase==3?.77:.54)+Math.sin(theta)*.32;
    }
    case TIDE -> {
     y=.25+h*fraction;
     r*=.75+.22*Math.cos(theta*2+angle);
     theta*=2;
    }
    case SKY, ARCHER -> {
     y=h*(.44+.36*Math.sin(theta));
     r*=.72+.18*Math.cos(theta*2);
    }
    case WILD, VENOM, EMBER -> {
     y=.18+h*(.12+.35*(i%4)/3.0);
     r*=.86+.18*Math.cos(theta*3+angle);
    }
   }
   out.add(new Point(Math.cos(theta)*r,Math.max(.12,y),Math.sin(theta)*r,accent));
  }
  return out;
 }
}
