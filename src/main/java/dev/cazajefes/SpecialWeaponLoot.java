package dev.cazajefes;

import com.google.gson.*;
import java.util.*;
import java.util.random.RandomGenerator;
import org.bukkit.entity.EntityType;

/** One optional special weapon roll, independent of ordinary category loot. */
final class SpecialWeaponLoot {
 record Selection(String item,int level){}
 record Rule(boolean enabled,String item,double chance,int level){}
 private static final Rule DISABLED=new Rule(false,"",0,1);
 private SpecialWeaponLoot(){}
 static boolean eligible(BossCatalog.Tier tier,boolean mini){return tier!=null&&!mini&&tier.ordinal()>=BossCatalog.Tier.EPIC.ordinal();}
 static boolean supported(String item){
  if(item==null)return false;
  if(CraftAvailability.LEGACY_WEAPONS.contains(item))return true;
  if(!item.startsWith("pvp_gear_"))return false;
  var relic=DuelCatalog.get(item.substring("pvp_gear_".length()));return relic!=null&&!relic.armor();
 }
 private static String familyWeapon(EntityType type){return switch(type){
  case ZOMBIE,HUSK,VINDICATOR->"hacha_sismica";
  case WITCH,EVOKER,ILLUSIONER->"espada_brezal";
  case SKELETON,STRAY,BOGGED,PILLAGER->"arco_ceniciento";
  case BLAZE,MAGMA_CUBE->"lanza_magma";
  case WARDEN,ENDERMAN,WITHER_SKELETON->"mandoble_vacio";
  default->"";
 };}
 static JsonObject defaults(EntityType type,BossCatalog.Tier tier,boolean mini){
  String item=familyWeapon(type);boolean enabled=eligible(tier,mini)&&!item.isEmpty();
  JsonObject json=new JsonObject();json.addProperty("enabled",enabled);json.addProperty("item",item);
  json.addProperty("chance",enabled?.03+.01*(tier.ordinal()-BossCatalog.Tier.EPIC.ordinal()):0);
  json.addProperty("level",1);return json;
 }
 static JsonObject defaults(BossCatalog.Definition boss){return defaults(boss.entityType(),boss.tier(),boss.mini());}
 static Rule read(EntityType type,BossCatalog.Tier tier,boolean mini,JsonObject combat){
  if(!eligible(tier,mini))return DISABLED;
  JsonObject defaults=defaults(type,tier,mini),json=defaults;
  if(combat!=null&&combat.has("specialWeaponLoot")){
   JsonElement field=combat.get("specialWeaponLoot");if(!field.isJsonObject())return DISABLED;
   json=field.getAsJsonObject();
  }
  try{
   JsonElement enabled=field(json,defaults,"enabled"),item=field(json,defaults,"item"),chance=field(json,defaults,"chance"),level=field(json,defaults,"level");
   if(!enabled.isJsonPrimitive()||!enabled.getAsJsonPrimitive().isBoolean()||!enabled.getAsBoolean())return DISABLED;
   if(!item.isJsonPrimitive()||!item.getAsJsonPrimitive().isString()||!chance.isJsonPrimitive()||!chance.getAsJsonPrimitive().isNumber()||!level.isJsonPrimitive()||!level.getAsJsonPrimitive().isNumber())return DISABLED;
   String id=item.getAsString();double probability=chance.getAsDouble(),power=level.getAsDouble();
   if(!supported(id)||!Double.isFinite(probability)||probability<0||probability>1||!Double.isFinite(power)||power<1||power>5||Math.rint(power)!=power)return DISABLED;
   return new Rule(true,id,probability,(int)power);
  }catch(RuntimeException ignored){return DISABLED;}
 }
 private static JsonElement field(JsonObject json,JsonObject fallback,String key){return json.has(key)?json.get(key):fallback.get(key);}
 static Selection roll(BossCatalog.Definition boss,RandomGenerator random){return roll(read(boss.entityType(),boss.tier(),boss.mini(),boss.custom().options().json()),random.nextDouble());}
 static Selection roll(Rule rule,double random){return rule.enabled()&&Double.isFinite(random)&&random>=0&&random<rule.chance()?new Selection(rule.item(),rule.level()):null;}
}
