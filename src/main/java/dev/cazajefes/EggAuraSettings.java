package dev.cazajefes;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.util.Vector;

/** Animated spherical shell with a bounded particle allowance. */
record EggAuraSettings(List<Double> radii, int points, double ringHeight, double ringSpacing) {
 record Dot(Vector offset, int ring, int index) {}
 static EggAuraSettings read(JsonObject json) {
  List<Double> radii=new ArrayList<>(List.of(1d,2d,3d));
  try {
   var values=json.getAsJsonArray("radii");
   if(values!=null && values.size()==3) {
    var edited=values.asList().stream().map(e->e.getAsDouble()).toList();
    if(edited.stream().allMatch(r->Double.isFinite(r)&&r>=.5&&r<=6)
        &&edited.get(0)<edited.get(1)&&edited.get(1)<edited.get(2))radii=new ArrayList<>(edited);
   }
  }catch(RuntimeException invalid){/* Keep all three valid default rings together. */}
  if(json.has("radius")){double radius=BossTuning.number(json,"radius",3,.5,6);radii=new ArrayList<>(List.of(radius/3,radius*2/3,radius));}
  return new EggAuraSettings(List.copyOf(radii),(int)BossTuning.number(json,"points",48,24,64),
      BossTuning.number(json,"ringHeight",.12,0,3),BossTuning.number(json,"ringSpacing",.16,0,1));
 }
 List<Dot> dots(double angle) {
  var out=new ArrayList<Dot>(points);double radius=radii.get(2);
  for(int i=0;i<points;i++){
   double y=1-2*(i+.5)/points,radial=Math.sqrt(1-y*y),a=i*2.399963229728653+angle;
   double x=Math.cos(a)*radial,z=Math.sin(a)*radial;
   double tilt=.25*Math.sin(angle*.7),yy=y*Math.cos(tilt)-z*Math.sin(tilt),zz=y*Math.sin(tilt)+z*Math.cos(tilt);
   out.add(new Dot(new Vector(radius*x,.5+radius*yy,radius*zz),0,i));
  }return out;
 }
 static JsonObject defaults() {
  var json=new JsonObject();var rings=new com.google.gson.JsonArray();for(int radius=1;radius<=3;radius++)rings.add(radius);
  json.addProperty("shape","SPHERE");json.addProperty("radius",3);json.addProperty("points",48);
  json.addProperty("primaryColor","#0C0014");json.addProperty("accentColor","#AF23F5");json.addProperty("particleSize",.65);json.addProperty("rotationSpeed",1);json.addProperty("removeGeneratedPedestal",true);return json;
 }
}
