package dev.cazajefes;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitTask;
/** Append-before-break journal. Deadlines survive restart; chunk unload is not defeat. */
final class TerrainRestoration {
 private record Saved(UUID world,int x,int y,int z,String data,String tile,Set<UUID> owners){}
 private final CazaJefesPlugin plugin;private final Path file;
 private final Map<String,Saved> blocks=new LinkedHashMap<>();private final Map<UUID,Long> deaths=new HashMap<>();private final BukkitTask task;
 private boolean available=true;
 static long deadline(long defeatedAt){return Math.addExact(defeatedAt,20*60*1000L);}
 static boolean due(long now,long scheduled){return scheduled>0&&now>=scheduled;}
 TerrainRestoration(CazaJefesPlugin plugin){this.plugin=plugin;file=plugin.getDataFolder().toPath().resolve("terrain-restoration.log");
  try{if(Files.exists(file)){if(Files.size(file)>64*1024*1024)throw new IOException("registro de restauración demasiado grande");for(String line:Files.readAllLines(file,StandardCharsets.UTF_8))read(line);}}catch(IOException|RuntimeException ex){available=false;plugin.getLogger().warning("Restauración suspendida: "+ex.getMessage());}
  task=plugin.getServer().getScheduler().runTaskTimer(plugin,()->restoreDue(System.currentTimeMillis()),20,20);
 }
 private static String key(UUID world,int x,int y,int z){return world+":"+x+":"+y+":"+z;}
 private static String key(Block block){return key(block.getWorld().getUID(),block.getX(),block.getY(),block.getZ());}
 private static String encoded(String value){return value==null?"":Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));}
 private void read(String line){try{String[] r=line.split("\t",-1);switch(r[0]){
  case "B"->{UUID owner=UUID.fromString(r[1]),world=UUID.fromString(r[2]);int x=Integer.parseInt(r[3]),y=Integer.parseInt(r[4]),z=Integer.parseInt(r[5]);if(r[6].startsWith("minecraft:bedrock"))return;blocks.put(key(world,x,y,z),new Saved(world,x,y,z,r[6],r[7].isEmpty()?null:new String(Base64.getDecoder().decode(r[7]),StandardCharsets.UTF_8),new HashSet<>(Set.of(owner))));}
  case "O"->{var saved=blocks.get(r[1]);if(saved!=null)saved.owners().add(UUID.fromString(r[2]));}
  case "D"->deaths.put(UUID.fromString(r[1]),Long.parseLong(r[2]));
  case "R"->blocks.remove(r[1]);default->{}
 }}catch(RuntimeException bad){plugin.getLogger().warning("Se omitió una entrada incompleta de restauración.");}}
 private boolean append(List<String> lines){if(lines.isEmpty())return true;try{Files.createDirectories(file.getParent());String text=String.join("\n",lines)+"\n";if(Files.exists(file)&&Files.size(file)+text.length()>64*1024*1024)throw new IOException("límite del registro alcanzado");Files.writeString(file,text,StandardCharsets.UTF_8,StandardOpenOption.CREATE,StandardOpenOption.APPEND);return true;}catch(IOException ex){plugin.getLogger().warning("No se pudo guardar la restauración: "+ex.getMessage());return false;}}
 boolean remember(LivingEntity boss,Collection<Block> requested){if(!available||blocks.size()+requested.size()>65536)return false;UUID owner=boss.getUniqueId();Map<String,Saved> added=new LinkedHashMap<>();List<String> rows=new ArrayList<>();Set<String> ownership=new HashSet<>();
  for(Block block:requested){if(block.getType()==Material.BEDROCK)return false;String key=key(block);var existing=blocks.get(key);if(existing!=null){if(!existing.owners().contains(owner)){rows.add("O\t"+key+"\t"+owner);ownership.add(key);}continue;}
   String tile=null;if(block.getState() instanceof org.bukkit.block.TileState){tile=dev.cazajefes.integration.NbtItems.snapshotTile(block.getState());if(tile==null||tile.length()>131072)return false;}
   Saved saved=new Saved(block.getWorld().getUID(),block.getX(),block.getY(),block.getZ(),block.getBlockData().getAsString(),tile,new HashSet<>(Set.of(owner)));added.put(key,saved);rows.add("B\t"+owner+"\t"+saved.world()+"\t"+saved.x()+"\t"+saved.y()+"\t"+saved.z()+"\t"+saved.data()+"\t"+encoded(tile));
  }
  if(!append(rows))return false;blocks.putAll(added);for(String key:ownership)blocks.get(key).owners().add(owner);return true;
 }
 void defeated(UUID boss){if(deaths.containsKey(boss)||blocks.values().stream().noneMatch(saved->saved.owners().contains(boss)))return;long when=deadline(System.currentTimeMillis());deaths.put(boss,when);append(List.of("D\t"+boss+"\t"+when));}
 long scheduled(UUID boss){return deaths.getOrDefault(boss,0L);}
 int pending(){return blocks.size();}
 void restoreDue(long now){if(!available)return;int remaining=128;List<String> removed=new ArrayList<>();for(var iterator=blocks.entrySet().iterator();iterator.hasNext()&&remaining>0;){var entry=iterator.next();Saved saved=entry.getValue();long ready=0;boolean waiting=false;for(UUID owner:saved.owners()){long time=deaths.getOrDefault(owner,0L);if(!due(now,time)){waiting=true;break;}ready=Math.max(ready,time);}if(waiting||!due(now,ready))continue;
   World world=Bukkit.getWorld(saved.world());if(world==null||!world.isChunkLoaded(saved.x()>>4,saved.z()>>4))continue;Block block=world.getBlockAt(saved.x(),saved.y(),saved.z());
   if(block.getType()==Material.BEDROCK){removed.add("R\t"+entry.getKey());iterator.remove();continue;}
   if(!ArenaBoundary.terrainUnclaimed(block.getLocation()))continue;
   Material current=block.getType();if(!(current.isAir()||current==Material.WATER||current==Material.LAVA||current==Material.BUBBLE_COLUMN)){removed.add("R\t"+entry.getKey());iterator.remove();continue;}
   if(!world.getNearbyEntities(org.bukkit.util.BoundingBox.of(block),entity->entity instanceof LivingEntity&&!entity.isDead()).isEmpty())continue;
   try{var data=Bukkit.createBlockData(saved.data());if(data.getMaterial()==Material.BEDROCK){removed.add("R\t"+entry.getKey());iterator.remove();continue;}block.setBlockData(data,false);if(saved.tile()!=null&&!dev.cazajefes.integration.NbtItems.restoreTile(block.getState(),saved.tile())){block.setType(Material.CAVE_AIR,false);continue;}}catch(RuntimeException error){continue;}
   removed.add("R\t"+entry.getKey());iterator.remove();remaining--;
  }
  append(removed);if(blocks.isEmpty()&&!removed.isEmpty())try{Files.writeString(file,"",StandardCharsets.UTF_8);deaths.clear();}catch(IOException ignored){}
 }
 void stop(){task.cancel();}
}
