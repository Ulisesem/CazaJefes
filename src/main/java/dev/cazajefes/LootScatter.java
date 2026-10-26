package dev.cazajefes;
final class LootScatter {
 record Offset(double x,double z){}
 static Offset at(int index,int total){double angle=index*2.399963229728653;double radius=total<=1?0:2.7*Math.sqrt((index+.5)/total);return new Offset(Math.cos(angle)*radius,Math.sin(angle)*radius);}
}
