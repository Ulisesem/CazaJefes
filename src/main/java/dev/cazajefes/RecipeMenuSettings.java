package dev.cazajefes;

import com.google.gson.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.*;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

/** Player-facing recipe presentation; invalid edits retain the last valid layout. */
final class RecipeMenuSettings {
 record Layout(String title,Map<String,String> labels,List<Integer> homeSlots,int pageSize,Material accent,List<String> materialOrder) {
  Layout{labels=Map.copyOf(labels);homeSlots=List.copyOf(homeSlots);materialOrder=List.copyOf(materialOrder);}
 }
 private final JavaPlugin plugin;
 private final Path file;
 private long modified=Long.MIN_VALUE;
 private Layout layout=read(new JsonObject());

 RecipeMenuSettings(JavaPlugin plugin) {
  this.plugin=plugin;
  file=plugin.getDataFolder().toPath().resolve("interfaces/recetas.json");
  if(Files.notExists(file))plugin.saveResource("interfaces/recetas.json",false);
  refresh();
 }
 void refresh() {
  try {
   long stamp=Files.getLastModifiedTime(file).toMillis();
   if(stamp==modified)return;
   modified=stamp;
   if(Files.size(file)>65536)throw new IllegalArgumentException("máximo 64 KiB");
   JsonObject document=JsonParser.parseString(Files.readString(file,StandardCharsets.UTF_8)).getAsJsonObject();
   boolean upgraded=upgrade(document);
   Layout candidate=read(document);
   if(upgraded) {
    Path temporary=Files.createTempFile(file.getParent(),".recetas-",".tmp");
    try {
     Files.writeString(temporary,new GsonBuilder().setPrettyPrinting().create().toJson(document)+"\n",StandardCharsets.UTF_8);
     try{Files.move(temporary,file,StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING);}
     catch(AtomicMoveNotSupportedException unsupported){Files.move(temporary,file,StandardCopyOption.REPLACE_EXISTING);}
    }finally{Files.deleteIfExists(temporary);}
    modified=Files.getLastModifiedTime(file).toMillis();
   }
   layout=candidate;
  }catch(IOException|RuntimeException error) {
   plugin.getLogger().warning("interfaces/recetas.json: "+error.getMessage()+"; se conserva el diseño válido.");
  }
 }
 Layout layout(){return layout;}
 String label(RaresMenu.Category category){return layout.labels().getOrDefault(category.name(),category.title);}

 /** Only change previous shipped defaults; administrators keep their custom labels and colors. */
 static boolean upgrade(JsonObject data) {
  int version=data.has("schemaVersion")?data.get("schemaVersion").getAsInt():0;
  boolean added=false;
  if(version<3&&data.has("homeSlots")&&data.get("homeSlots").toString().equals("[20,22,24,29,31,33]")){var slots=new JsonArray();for(int slot:RecipePresentation.CATEGORIES)slots.add(slot);data.add("homeSlots",slots);added=true;}
  if(!data.has("materialOrder")){var order=new JsonArray();for(String group:List.of("catalysts","defense","projectiles","other"))order.add(group);data.add("materialOrder",order);added=true;}
  if(version>=3)return added;
  if(version>=2){data.addProperty("schemaVersion",3);return true;}
  if(data.has("accent")&&"CYAN_STAINED_GLASS_PANE".equals(data.get("accent").getAsString()))
   data.addProperty("accent","BLUE_STAINED_GLASS_PANE");
  if(data.get("labels") instanceof JsonObject labels&&labels.has("CURRENCIES")
      &&"Tributos".equals(labels.get("CURRENCIES").getAsString()))labels.addProperty("CURRENCIES","Brújula");
  if(data.get("labels") instanceof JsonObject labels&&labels.has("PVP")
      &&"Reliquias PvP".equals(labels.get("PVP").getAsString()))labels.addProperty("PVP","Defensas PvP");
  data.addProperty("schemaVersion",3);
  return true;
 }
 static Layout read(JsonObject source) {
  JsonObject data=source.deepCopy();
  upgrade(data);
  String title=data.has("title")?data.get("title").getAsString().strip():"Forja de la Grieta";
  if(title.isBlank()||title.length()>64)throw new IllegalArgumentException("título inválido");
  Map<String,String> labels=new HashMap<>();
  if(data.get("labels") instanceof JsonObject names)for(var entry:names.entrySet()) {
   String text=entry.getValue().getAsString().strip();
   if(text.isBlank()||text.length()>48)throw new IllegalArgumentException("nombre de apartado inválido");
   labels.put(entry.getKey(),text);
  }
  List<Integer> slots=new ArrayList<>();
  if(data.has("homeSlots"))for(var element:data.getAsJsonArray("homeSlots"))slots.add(element.getAsInt());
  else for(int slot:RecipePresentation.CATEGORIES)slots.add(slot);
  if(slots.size()!=RecipePresentation.HOME.size()||new HashSet<>(slots).size()!=slots.size()
      ||slots.stream().anyMatch(slot->slot<10||slot>43||slot%9==0||slot%9==8))
   throw new IllegalArgumentException("los seis apartados necesitan casillas interiores distintas");
  int count=data.has("pageSize")?data.get("pageSize").getAsInt():28;
  if(count<1||count>28)throw new IllegalArgumentException("pageSize debe estar entre 1 y 28");
  Material accent=data.has("accent")?Material.matchMaterial(data.get("accent").getAsString()):Material.BLUE_STAINED_GLASS_PANE;
  if(accent==null||!accent.name().endsWith("STAINED_GLASS_PANE"))
   throw new IllegalArgumentException("accent debe ser un panel de cristal tintado");
  List<String> order=new ArrayList<>(List.of("catalysts","defense","projectiles","other"));
  if(data.has("materialOrder")){var edited=data.getAsJsonArray("materialOrder").asList().stream().map(e->e.getAsString()).toList();if(edited.size()!=4||!new HashSet<>(edited).equals(new HashSet<>(order)))throw new IllegalArgumentException("materialOrder requiere los cuatro grupos únicos");order=new ArrayList<>(edited);}
  return new Layout(title,labels,slots,count,accent,order);
 }
}