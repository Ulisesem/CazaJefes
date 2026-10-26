package dev.cazajefes;
import java.util.*;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
final class BossSpawnpoints implements Listener {
 private final CazaJefesPlugin plugin;private final BukkitTask task;
 private final NamespacedKey pointKey=new NamespacedKey("cazajefes","scheduled_spawnpoint");
 BossSpawnpoints(CazaJefesPlugin plugin){this.plugin=plugin;task=plugin.getServer().getScheduler().runTaskTimer(plugin,this::tick,100,100);}
 Location location(String name){String path="spawnpoints."+name.toLowerCase(Locale.ROOT);String world=plugin.getConfig().getString(path+".world");World w=world==null?null:plugin.getServer().getWorld(world);if(w==null)return null;int x=plugin.getConfig().getInt(path+".x"),y=plugin.getConfig().getInt(path+".y"),z=plugin.getConfig().getInt(path+".z");double r=Math.max(0,Math.min(128,plugin.getConfig().getDouble(path+".radius",0)));if(r>0){double angle=Math.random()*Math.PI*2,distance=Math.sqrt(Math.random())*r;x+=(int)Math.round(Math.cos(angle)*distance);z+=(int)Math.round(Math.sin(angle)*distance);y=w.getHighestBlockYAt(x,z)+1;}return new Location(w,x+.5,y,z+.5);}
 private Set<String> names(){var section=plugin.getConfig().getConfigurationSection("spawnpoints");return section==null?Set.of():new LinkedHashSet<>(section.getKeys(false));}
 private void tick(){for(String name:names()){String path="spawnpoints."+name;if(!plugin.getConfig().getBoolean(path+".auto.enabled",false))continue;String active=plugin.getConfig().getString(path+".auto.active");if(active!=null){try{Entity entity=plugin.getServer().getEntity(UUID.fromString(active));if(entity!=null&&entity.isDead())reset(name);}catch(IllegalArgumentException ignored){}continue;}if(AdminArguments.due(System.currentTimeMillis(),plugin.getConfig().getLong(path+".auto.next"),false))spawn(name);}}
 private void spawn(String name){String path="spawnpoints."+name;Location at=location(name);if(at==null)return;String id=plugin.getConfig().getString(path+".auto.boss","");BossCatalog.Definition boss=id.equalsIgnoreCase("random")?BossCatalog.all().get(java.util.concurrent.ThreadLocalRandom.current().nextInt(BossCatalog.all().size())):BossCatalog.get(id);if(boss==null)return;LivingEntity entity=plugin.bosses().spawnBoss(boss.id(),at,"La Grieta");if(entity==null)return;entity.getPersistentDataContainer().set(pointKey,PersistentDataType.STRING,name);plugin.getConfig().set(path+".auto.active",entity.getUniqueId().toString());plugin.getConfig().set(path+".auto.next",0);plugin.saveConfig();}
 private void reset(String name){String path="spawnpoints."+name;plugin.getConfig().set(path+".auto.active",null);plugin.getConfig().set(path+".auto.next",System.currentTimeMillis()+Math.max(10,plugin.getConfig().getLong(path+".auto.seconds",60))*1000);plugin.saveConfig();}
 @EventHandler(priority=EventPriority.MONITOR)public void death(EntityDeathEvent event){String name=event.getEntity().getPersistentDataContainer().get(pointKey,PersistentDataType.STRING);if(name!=null&&event.getEntity().getUniqueId().toString().equals(plugin.getConfig().getString("spawnpoints."+name+".auto.active")))reset(name);}
 void command(CommandSender sender,String[] args){
  if(args.length<2){sender.sendMessage("/ri spawnpoint <nombre> [radio 0-128] | list | delete <nombre> | timer <nombre> <jefe|random> <10s|5m|1h> | off <nombre>");return;}
  String action=args[1].toLowerCase(Locale.ROOT);
  if(action.equals("list")){if(names().isEmpty())sender.sendMessage("No hay puntos guardados.");for(String name:names()){String p="spawnpoints."+name;sender.sendMessage(name+" · "+plugin.getConfig().getString(p+".world")+" · "+(plugin.getConfig().getBoolean(p+".auto.enabled")?plugin.getConfig().getString(p+".auto.boss")+" cada "+plugin.getConfig().getLong(p+".auto.seconds")+"s"+(plugin.getConfig().getString(p+".auto.active")==null?" · esperando":" · boss activo"):"manual"));}return;}
  if(Set.of("delete","off","timer").contains(action)){
   if(args.length<3||!AdminArguments.name(args[2])||!names().contains(args[2].toLowerCase(Locale.ROOT))){sender.sendMessage("Indica un punto existente: /ri spawnpoint list");return;}
   String name=args[2].toLowerCase(Locale.ROOT),p="spawnpoints."+name;
   if(action.equals("delete")){plugin.getConfig().set(p,null);plugin.saveConfig();sender.sendMessage("Punto eliminado: "+name+". El boss activo no se elimina.");return;}
   if(action.equals("off")){plugin.getConfig().set(p+".auto.enabled",false);plugin.saveConfig();sender.sendMessage("Reaparición desactivada: "+name);return;}
   if(args.length!=5){sender.sendMessage("/ri spawnpoint timer <nombre> <id-jefe|random> <10s|5m|1h>");return;}
   String id=args[3].toLowerCase(Locale.ROOT);long seconds=AdminArguments.seconds(args[4]);var boss=BossCatalog.get(id);
   if((boss==null&&!id.equals("random"))||seconds<0){sender.sendMessage("Usa un ID válido o random y un intervalo entre 10 segundos y 24 horas.");return;}
   plugin.getConfig().set(p+".auto.boss",boss==null?"random":boss.id());plugin.getConfig().set(p+".auto.seconds",seconds);plugin.getConfig().set(p+".auto.enabled",true);plugin.getConfig().set(p+".auto.next",System.currentTimeMillis()+seconds*1000);plugin.saveConfig();sender.sendMessage("✦ "+name+": "+id+" · "+seconds+"s. Primera aparición al terminar el tiempo; las siguientes, después de morir el boss.");return;
  }
  if(!(sender instanceof Player player)){sender.sendMessage("Crea el punto desde un jugador; timer, list, off y delete también funcionan desde consola.");return;}
  if(!AdminArguments.name(action)){sender.sendMessage("Nombre: letras, números, guion o guion bajo; máximo 32 caracteres.");return;}
  double radius=0;try{if(args.length>2)radius=Double.parseDouble(args[2]);}catch(NumberFormatException error){sender.sendMessage("Radio numérico entre 0 y 128.");return;}if(!Double.isFinite(radius)||radius<0||radius>128||args.length>3){sender.sendMessage("Radio numérico entre 0 y 128.");return;}
  Location at=player.getLocation();String p="spawnpoints."+action;plugin.getConfig().set(p+".world",at.getWorld().getName());plugin.getConfig().set(p+".x",at.getBlockX());plugin.getConfig().set(p+".y",at.getBlockY());plugin.getConfig().set(p+".z",at.getBlockZ());plugin.getConfig().set(p+".radius",radius);plugin.saveConfig();sender.sendMessage("Punto guardado: "+action+" · radio "+radius+". Usa timer para activar reapariciones.");
 }
 void stop(){task.cancel();}
}
