package dev.cazajefes;

import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import org.bukkit.plugin.java.JavaPlugin;

/** Main-thread, reloadable balance values. Invalid values retain the safe built-in default. */
final class RareBalance {
 private static volatile JsonObject configured=new JsonObject();
 private static final Gson JSON=new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
 private RareBalance(){}
 static void load(JavaPlugin plugin){
  load(plugin.getDataFolder().toPath().resolve("rare-balance.json"),plugin.getLogger());
 }
 static boolean load(Path path,java.util.logging.Logger logger){
  try{
   JsonObject root;
   if(Files.exists(path))try(Reader reader=Files.newBufferedReader(path,StandardCharsets.UTF_8)){root=JsonParser.parseReader(reader).getAsJsonObject();}
   else root=new JsonObject();
   if(root.has("rares")&&!root.get("rares").isJsonObject())throw new IllegalArgumentException("rares debe ser un objeto JSON");
   JsonObject entries=root.has("rares")?root.getAsJsonObject("rares"):new JsonObject();
   root.addProperty("schemaVersion",1);
   root.addProperty("nota","Probabilidades 0–1 por nivel; cooldownSeconds en segundos; parámetros de efectos por nivel. Los cambios se aplican con /ri reload.");
   // Merge missing entries only: a reload never replaces administrator choices.
   for(var definition:RareDefinitions.ALL.values())merge(entries,definition.id(),definition.maximum(),definition.baseCooldown());
   for(String id:RareCollections.ids())if(RareDefinitions.get(id)==null)merge(entries,id,ArmorRareRules.maximum(id),0);
   root.add("rares",entries);
   Files.createDirectories(path.getParent());
   Path temporary=path.resolveSibling(path.getFileName()+".tmp");
   Files.writeString(temporary,JSON.toJson(root)+System.lineSeparator(),StandardCharsets.UTF_8);
   try{Files.move(temporary,path,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);}catch(AtomicMoveNotSupportedException ignored){Files.move(temporary,path,StandardCopyOption.REPLACE_EXISTING);}
   configured=entries.deepCopy();
   return true;
  }catch(IOException|RuntimeException error){logger.warning("No se pudo cargar rare-balance.json; se conserva el balance anterior: "+error.getMessage());return false;}
 }
 private static void merge(JsonObject entries,String id,int maximum,int cooldown){
  if(entries.has(id)&&!entries.get(id).isJsonObject())throw new IllegalArgumentException("La rare "+id+" debe ser un objeto JSON");
  JsonObject entry=entries.has(id)?entries.getAsJsonObject(id):new JsonObject();
  if(!entry.has("chanceByLevel")){JsonArray chances=new JsonArray();for(int level=1;level<=maximum;level++)chances.add(defaultChance(id,level));entry.add("chanceByLevel",chances);}
  if(!entry.has("cooldownSeconds"))entry.addProperty("cooldownSeconds",cooldown);
  for(String key:List.of("damageMultiplier","durationMultiplier","forceMultiplier","healingMultiplier"))if(!entry.has(key))entry.addProperty(key,1);
  if((id.equals("regeneration")||id.equals("aura_esporas"))&&!entry.has("intervalSeconds"))entry.addProperty("intervalSeconds",id.equals("regeneration")?10:5);
  entries.add(id,entry);
 }
 static double defaultChance(String id,int level){
  if(level<=0)return 0;
  var jail=JailRareCatalog.get(id);
  if(jail!=null)return jail.effect().equals("combo")||jail.effect().equals("breaker")?1:JailRareCatalog.chance(level);
  if(RangedProcRules.direct(id))return RangedProcRules.defaultChance(id,level);
  return switch(id){case "garlic"->.2+.1*Math.min(5,level);case "turtle"->.1+.1*Math.min(5,level);case "vampiricregeneration","vampirismo"->.25+.1*(Math.min(3,level)-1);case "calllighting"->.15+.05*(Math.min(5,level)-1);case "blinding"->.25+.15*(Math.min(2,level)-1);case "slow","weaken"->.30+.15*(Math.min(3,level)-1);case "brasas"->.30+.10*(Math.min(5,level)-1);case "confuse","disarm"->.30;default->1;};
 }
 static double chance(String id,int level){if(level<=0)return 0;double fallback=defaultChance(id,level),value=levelValue(id,"chanceByLevel",level,fallback);return value>=0&&value<=1?value:fallback;}
 static int cooldown(String id,int fallback){return (int)Math.round(number(id,"cooldownSeconds",fallback,0,3600));}
 static long interval(String id,int fallback){return Math.round(number(id,"intervalSeconds",fallback,1,3600)*1000);}
 static double damage(String id,double base){return Math.max(0,base*number(id,"damageMultiplier",1,0,10));}
 static double healing(String id,double base){return Math.max(0,base*number(id,"healingMultiplier",1,0,10));}
 static double force(String id,double base){return Math.max(0,Math.min(4,base*number(id,"forceMultiplier",1,0,5)));}
 static int ticks(String id,int base){return Math.max(1,(int)Math.round(Math.min(12000,base*number(id,"durationMultiplier",1,0.05,10))));}
 private static JsonObject entry(String id){JsonElement raw=configured.get(id);return raw!=null&&raw.isJsonObject()?raw.getAsJsonObject():new JsonObject();}
 private static double number(String id,String field,double fallback,double minimum,double maximum){try{JsonElement raw=entry(id).get(field);double value=raw==null?fallback:raw.getAsDouble();return Double.isFinite(value)&&value>=minimum&&value<=maximum?value:fallback;}catch(RuntimeException ignored){return fallback;}}
 private static double levelValue(String id,String field,int level,double fallback){try{JsonElement raw=entry(id).get(field);if(raw==null||!raw.isJsonArray()||raw.getAsJsonArray().isEmpty())return fallback;JsonArray array=raw.getAsJsonArray();double value=array.get(Math.min(array.size(),Math.max(1,level))-1).getAsDouble();return Double.isFinite(value)?value:fallback;}catch(RuntimeException ignored){return fallback;}}
 static String description(String id,String original,int maximum,int cooldown){
  String result=original.replaceAll("Recarga \\d+ s\\.","Recarga "+cooldown(id,cooldown)+" s.");
  if(defaultChance(id,1)<1){StringJoiner values=new StringJoiner("/");for(int n=1;n<=maximum;n++)values.add(java.math.BigDecimal.valueOf(chance(id,n)*100).setScale(2,java.math.RoundingMode.HALF_UP).stripTrailingZeros().toPlainString());String chances=values+"%";result=result.matches("(?s).*\\d+/\\d+(?:/\\d+)*%.*")?result.replaceFirst("\\d+/\\d+(?:/\\d+)*%",chances):result.replaceFirst("\\d+%",chances);}
  StringJoiner changes=new StringJoiner(" · ");
  for(var multiplier:Map.of("damageMultiplier","daño","durationMultiplier","duración","forceMultiplier","impulso","healingMultiplier","curación").entrySet()){String key=multiplier.getKey();double scale=number(id,key,1,key.equals("durationMultiplier")?.05:0,key.equals("forceMultiplier")?5:10);if(scale!=1)changes.add(multiplier.getValue()+" ×"+String.format(Locale.ROOT,"%.2f",scale));}
  return changes.length()==0?result:result+" Balance configurado: "+changes+".";
 }
}
