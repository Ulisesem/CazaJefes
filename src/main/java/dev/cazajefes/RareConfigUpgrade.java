package dev.cazajefes;
import java.io.*;
import java.nio.file.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
final class RareConfigUpgrade {
 static YamlConfiguration load(JavaPlugin plugin,File file){var config=YamlConfiguration.loadConfiguration(file);if(config.getInt("catalog-version",0)>=1220)return config;try{if(file.exists())Files.copy(file.toPath(),new File(file.getParentFile(),"rares-before-1.22.0.yml").toPath());}catch(FileAlreadyExistsException ignored){}catch(IOException e){throw new IllegalStateException("No se pudo respaldar el catálogo de rares",e);}for(var d:RareDefinitions.ALL.values()){config.set(d.id()+".maxLvl",d.maximum());if(config.getDouble(d.id()+".useCost",0)>=0)config.set(d.id()+".useCost",d.cost());config.set(d.id()+".cooldownSeconds",d.cooldown());config.set(d.id()+".displayName",d.title());config.set(d.id()+".description",d.description());}config.set("catalog-version",1220);try{config.save(file);}catch(IOException e){throw new IllegalStateException("No se pudo guardar el catálogo de rares",e);}return config;}
}
