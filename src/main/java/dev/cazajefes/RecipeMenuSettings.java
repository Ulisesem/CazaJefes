package dev.cazajefes;
import com.google.gson.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.*;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
/** Small player-facing menu configuration; invalid edits keep the last valid layout. */
final class RecipeMenuSettings {
 record Layout(String title,Map<String,String> labels,List<Integer> homeSlots,int pageSize,Material accent){
  Layout{labels=Map.copyOf(labels);homeSlots=List.copyOf(homeSlots);}
 }
 private final JavaPlugin plugin;private final Path file;private long modified=Long.MIN_VALUE;
 private Layout layout=read(new JsonObject());
 RecipeMenuSettings(JavaPlugin plugin){this.plugin=plugin;file=plugin.getDataFolder().toPath().resolve("interfaces/recetas.json");
  if(Files.notExists(file))plugin.saveResource("interfaces/recetas.json",false);refresh();}
 void refresh(){try{long stamp=Files.getLastModifiedTime(file).toMillis();if(stamp==modified)return;modified=stamp;
  if(Files.size(file)>65536)throw new IllegalArgumentException("máximo 64 KiB");
  layout=read(JsonParser.parseString(Files.readString(file,StandardCharsets.UTF_8)).getAsJsonObject());
 }catch(IOException|RuntimeException error){plugin.getLogger().warning("interfaces/recetas.json: "+error.getMessage()+"; se conserva el diseño válido.");}}
 Layout layout(){return layout;}
 String label(RaresMenu.Category category){return layout.labels().getOrDefault(category.name(),category.title);}
 static Layout read(JsonObject data){
  String title=data.has("title")?data.get("title").getAsString().strip():"Forja de la Grieta";
  if(title.isBlank()||title.length()>64)throw new IllegalArgumentException("título inválido");
  Map<String,String> labels=new HashMap<>();if(data.get("labels") instanceof JsonObject names)for(var entry:names.entrySet()){
   String text=entry.getValue().getAsString().strip();if(text.isBlank()||text.length()>48)throw new IllegalArgumentException("nombre de apartado inválido");labels.put(entry.getKey(),text);
  }
  List<Integer> slots=new ArrayList<>();if(data.has("homeSlots"))for(var element:data.getAsJsonArray("homeSlots"))slots.add(element.getAsInt());else for(int slot:RecipePresentation.CATEGORIES)slots.add(slot);
  if(slots.size()!=RecipePresentation.HOME.size()||new HashSet<>(slots).size()!=slots.size()
      ||slots.stream().anyMatch(slot->slot<10||slot>43||slot%9==0||slot%9==8))throw new IllegalArgumentException("los seis apartados necesitan casillas interiores distintas");
  int count=data.has("pageSize")?data.get("pageSize").getAsInt():28;if(count<1||count>28)throw new IllegalArgumentException("pageSize debe estar entre 1 y 28");
  Material accent=data.has("accent")?Material.matchMaterial(data.get("accent").getAsString()):Material.CYAN_STAINED_GLASS_PANE;
  if(accent==null||!accent.name().endsWith("STAINED_GLASS_PANE"))throw new IllegalArgumentException("accent debe ser un panel de cristal tintado");
  return new Layout(title,labels,slots,count,accent);
 }
}
