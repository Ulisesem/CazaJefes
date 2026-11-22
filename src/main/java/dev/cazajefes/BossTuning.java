package dev.cazajefes;
import com.google.gson.JsonObject;
/** Validated per-boss knobs for pursuit and egg presentation. */
final class BossTuning {
 static JsonObject section(BossCatalog.Definition boss,String name){var root=boss.custom().options().json();return root.has(name)&&root.get(name).isJsonObject()?root.getAsJsonObject(name):new JsonObject();}
 static double number(JsonObject json,String key,double fallback,double min,double max){try{double value=json.has(key)?json.get(key).getAsDouble():fallback;return Double.isFinite(value)?Math.clamp(value,min,max):fallback;}catch(RuntimeException ex){return fallback;}}
 static boolean flag(JsonObject json,String key,boolean fallback){try{return json.has(key)?json.get(key).getAsBoolean():fallback;}catch(RuntimeException ex){return fallback;}}
 static JsonObject mobilityDefaults(){var j=new JsonObject();j.addProperty("pursueFlyingPlayers",false);j.addProperty("climbSpeed",.35);j.addProperty("climbRange",6);j.addProperty("waterWalking",true);return j;}
 static JsonObject eggDefaults(){return EggAuraSettings.defaults();}
 /** Add only missing options; keep every administrator override and attack definition. */
 static boolean complete(JsonObject document){
  boolean changed=false;
  if(!document.has("combat")){document.add("combat",new JsonObject());changed=true;}
  if(!document.get("combat").isJsonObject())return changed;
  var combat=document.getAsJsonObject("combat");
  changed|=merge(combat,"mobility",mobilityDefaults());
  if(combat.has("eggParticles")&&combat.get("eggParticles").isJsonObject()){
   var old=combat.getAsJsonObject("eggParticles");
   if(!old.has("radii")&&old.has("points")&&number(old,"points",48,24,64)==32){old.addProperty("points",48);changed=true;}
  }
  changed|=merge(combat,"eggParticles",eggDefaults());
  changed|=merge(combat,"axeThrow",AxeThrowSettings.defaults());
  changed|=merge(combat,"crossbowSupport",SupportHordeSettings.defaults());
  BossCatalog.Tier tier=BossCatalog.Tier.COMMON;boolean mini=true;org.bukkit.entity.EntityType entity=org.bukkit.entity.EntityType.ZOMBIE;
  try{if(document.has("tier"))tier=BossCatalog.Tier.valueOf(document.get("tier").getAsString());
      if(document.has("mini"))mini=document.get("mini").getAsBoolean();
      if(document.has("entity"))entity=org.bukkit.entity.EntityType.valueOf(document.get("entity").getAsString());
  }catch(RuntimeException invalid){/* Catalog validation handles malformed identities. */}
  changed|=merge(combat,"difficulty",BossDifficulty.defaults(tier,mini));
  changed|=merge(combat,"specialWeaponLoot",SpecialWeaponLoot.defaults(entity,tier,mini));
  if(!document.has("ai")){document.add("ai",new JsonObject());changed=true;}
  if(document.get("ai").isJsonObject())changed|=merge(document.getAsJsonObject("ai"),"melee",BossDifficulty.meleeDefaults(tier,mini));
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
