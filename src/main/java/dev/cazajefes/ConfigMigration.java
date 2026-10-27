package dev.cazajefes;
import java.io.*;
import java.nio.file.*;
import org.bukkit.configuration.file.YamlConfiguration;
final class ConfigMigration {
 static void canonicalBossIds(CazaJefesPlugin plugin){
  boolean changed=false;var config=plugin.getConfig();var bosses=config.getConfigurationSection("bosses");
  if(bosses!=null)for(String old:new java.util.ArrayList<>(bosses.getKeys(false))){var boss=BossCatalog.get(old);if(boss==null||old.equals(boss.id()))continue;var section=bosses.getConfigurationSection(old);if(section!=null)for(String field:section.getKeys(true))if(!section.isConfigurationSection(field)&&!config.isSet("bosses."+boss.id()+"."+field))config.set("bosses."+boss.id()+"."+field,section.get(field));config.set("bosses."+old,null);changed=true;}
  var points=config.getConfigurationSection("spawnpoints");if(points!=null)for(String name:points.getKeys(false)){String path="spawnpoints."+name+".auto.boss";var boss=BossCatalog.get(config.getString(path));if(boss!=null&&!boss.id().equals(config.getString(path))){config.set(path,boss.id());changed=true;}}
  var eggs=new java.util.ArrayList<java.util.Map<String,Object>>();boolean eggsChanged=false;for(var entry:config.getMapList("active-eggs")){java.util.Map<String,Object> copy=new java.util.LinkedHashMap<>();entry.forEach((k,v)->copy.put(String.valueOf(k),v));var boss=BossCatalog.get(String.valueOf(copy.get("type")));if(boss!=null&&!boss.id().equals(copy.get("type"))){copy.put("type",boss.id());eggsChanged=true;}eggs.add(copy);}if(eggsChanged){config.set("active-eggs",eggs);changed=true;}
  if(changed)plugin.saveConfig();
 }
 static void run(CazaJefesPlugin plugin){
  File config=new File(plugin.getDataFolder(),"config.yml");
  YamlConfiguration raw=YamlConfiguration.loadConfiguration(config);
  if(raw.getInt("schema-version",0)>=2)return;
  try{if(config.exists())Files.copy(config.toPath(),new File(plugin.getDataFolder(),"config-before-1.1.0.yml").toPath(),StandardCopyOption.REPLACE_EXISTING);}
  catch(IOException e){throw new IllegalStateException("No se pudo respaldar config.yml; se cancela la migración",e);}
  for(var boss:BossCatalog.all())for(String field:new String[]{"scale","health","damage"}){
   String old="bosses."+boss.migrationId()+"."+field,newKey="bosses."+boss.id()+"."+field;
   if(!old.equals(newKey)&&raw.contains(old)&&!raw.contains(newKey))plugin.getConfig().set(newKey,raw.get(old));
  }
  plugin.getConfig().set("schema-version",2);plugin.saveConfig();
 }
 static YamlConfiguration recipes(CazaJefesPlugin plugin,File file,YamlConfiguration config){
  if(config.getInt("schema-version",0)>=3)return config;
  try{Files.copy(file.toPath(),new File(plugin.getDataFolder(),"recipes-before-1.4.1.yml").toPath(),StandardCopyOption.REPLACE_EXISTING);}
  catch(IOException e){throw new IllegalStateException("No se pudo respaldar recipes.yml; se cancela la migración",e);}
  // Old weapon patterns use symbols removed by the crystal progression. Keep enabled flags; back up all old overrides.
  for(String key:new String[]{"hacha_sismica","espada_brezal","arco_ceniciento","lanza_magma","mandoble_vacio"}){
   config.set("recipes."+key+".shape",null);config.set("recipes."+key+".ingredients",null);
  }
  var section=config.getConfigurationSection("recipes");
  if(section!=null)for(String key:section.getKeys(false))if(key.startsWith("crear_libro_rare_")||key.startsWith("crear_esencia_rare_")){config.set("recipes."+key+".shape",null);config.set("recipes."+key+".ingredients",null);}
  config.set("schema-version",3);
  try{config.save(file);}catch(IOException e){throw new IllegalStateException("No se pudo guardar la migración de recetas",e);}return config;
 }
}
