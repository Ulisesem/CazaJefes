package dev.cazajefes;
import java.io.*;
import java.nio.file.*;
import org.bukkit.configuration.file.YamlConfiguration;
final class ConfigMigration {
 static void run(CazaJefesPlugin plugin){
  File config=new File(plugin.getDataFolder(),"config.yml");
  YamlConfiguration raw=YamlConfiguration.loadConfiguration(config);
  if(raw.getInt("schema-version",0)>=2)return;
  try{if(config.exists())Files.copy(config.toPath(),new File(plugin.getDataFolder(),"config-before-1.1.0.yml").toPath(),StandardCopyOption.REPLACE_EXISTING);}
  catch(IOException e){throw new IllegalStateException("No se pudo respaldar config.yml; se cancela la migración",e);}
  for(var boss:BossCatalog.all())for(String field:new String[]{"scale","health","damage"}){
   String old="bosses."+boss.legacyId()+"."+field,newKey="bosses."+boss.id()+"."+field;
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
