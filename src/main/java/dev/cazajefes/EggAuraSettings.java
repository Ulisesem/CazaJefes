package dev.cazajefes;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.util.Vector;

/** Three bounded orbital rings share one particle allowance, rather than one allowance each. */
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
  return new EggAuraSettings(List.copyOf(radii),(int)BossTuning.number(json,"points",48,24,64),
      BossTuning.number(json,"ringHeight",.12,0,3),BossTuning.number(json,"ringSpacing",.16,0,1));
 }
 List<Dot> dots(double angle) {
  var out=new ArrayList<Dot>(points);double sum=radii.stream().mapToDouble(Double::doubleValue).sum();int used=0;
  for(int ring=0;ring<3;ring++) {
   int count=ring==2?points-used:Math.max(4,(int)Math.round(points*radii.get(ring)/sum));
   count=Math.min(count,points-used-(2-ring)*4);used+=count;
   double rotation=(ring%2==0?1:-1)*angle*(1+.15*ring),radius=radii.get(ring);
   for(int index=0;index<count;index++) {
    double a=rotation+2*Math.PI*index/count;
    out.add(new Dot(new Vector(Math.cos(a)*radius,ringHeight+ringSpacing*ring,Math.sin(a)*radius),ring,index));
   }
  }
  return out;
 }
 static JsonObject defaults() {
  var json=new JsonObject();var rings=new com.google.gson.JsonArray();for(int radius=1;radius<=3;radius++)rings.add(radius);
  json.add("radii",rings);json.addProperty("points",48);json.addProperty("ringHeight",.12);json.addProperty("ringSpacing",.16);
  json.addProperty("removeGeneratedPedestal",true);return json;
 }
}
