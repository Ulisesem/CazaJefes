package dev.cazajefes;
import com.google.gson.JsonParser;
import org.junit.Test;
import static org.junit.Assert.*;
public class EggAuraSettingsTest {
 @Test public void threeExactCircumferencesRotateWithoutChangingRadiusOrBudget(){
  var config=EggAuraSettings.read(EggAuraSettings.defaults());
  for(double angle:new double[]{0,.18,2.8}) {
   var dots=config.dots(angle);assertEquals(48,dots.size());int[] count=new int[3];
   for(var dot:dots){count[dot.ring()]++;var p=dot.offset();assertEquals(3,Math.sqrt(p.getX()*p.getX()+(p.getY()-.5)*(p.getY()-.5)+p.getZ()*p.getZ()),1e-9);}
   assertArrayEquals(new int[]{48,0,0},count);
  }
 }
 @Test public void invalidRadiiFallBackTogetherAndOverridesStayBounded(){
  var bad=EggAuraSettings.read(JsonParser.parseString("{\"radii\":[3,1,2],\"points\":900}").getAsJsonObject());
  assertEquals(java.util.List.of(1d,2d,3d),bad.radii());assertEquals(64,bad.dots(0).size());
  var custom=EggAuraSettings.read(JsonParser.parseString("{\"radii\":[0.5,1.5,4],\"points\":24}").getAsJsonObject());
  assertEquals(java.util.List.of(.5,1.5,4d),custom.radii());assertEquals(24,custom.dots(0).size());
 }
}
