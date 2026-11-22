package dev.cazajefes;
import com.google.gson.JsonObject;
/** Validated per-boss knobs for pursuit and egg presentation. */
final class BossTuning {
 static JsonObject section(BossCatalog.Definition boss,String name){var root=boss.custom().options().json();return root.has(name)&&root.get(name).isJsonObject()?root.getAsJsonObject(name):new JsonObject();}
 static double number(JsonObject json,String key,double fallback,double min,double max){try{double value=json.has(key)?json.get(key).getAsDouble():fallback;return Double.isFinite(value)?Math.clamp(value,min,max):fallback;}catch(RuntimeException ex){return fallback;}}
 static boolean flag(JsonObject json,String key,boolean fallback){try{return json.has(key)?json.get(key).getAsBoolean():fallback;}catch(RuntimeException ex){return fallback;}}
 static JsonObject mobilityDefaults(){var j=new JsonObject();j.addProperty("pursueFlyingPlayers",false);j.addProperty("climbSpeed",.35);j.addProperty("climbRange",6);j.addProperty("waterWalking",true);return j;}
 static JsonObject eggDefaults(){var j=new JsonObject();j.addProperty("radius",1);j.addProperty("height",2);j.addProperty("points",32);j.addProperty("removeGeneratedPedestal",true);return j;}
 /** Add only missing options; keep every administrator override and attack definition. */
 static boolean complete(JsonObject document){
  boolean changed=false;
  if(!document.has("combat")){document.add("combat",new JsonObject());changed=true;}
  if(!document.get("combat").isJsonObject())return changed;
  var combat=document.getAsJsonObject("combat");
  changed|=merge(combat,"mobility",mobilityDefaults());
  changed|=merge(combat,"eggParticles",eggDefaults());
  changed|=merge(combat,"axeThrow",AxeThrowSettings.defaults());
  changed|=merge(combat,"crossbowSupport",SupportHordeSettings.defaults());
  return changed;
 }
 static boolean merge(JsonObject parent,String name,JsonObject defaults){
  if(!parent.has(name)){parent.add(name,defaults.deepCopy());return true;}
  if(!parent.get(name).isJsonObject())return false;
  boolean changed=false;var target=parent.getAsJsonObject(name);
  for(var entry:defaults.entrySet())if(!target.has(entry.getKey())){target.add(entry.getKey(),entry.getValue().deepCopy());changed=true;}
  return changed;
 }
}
