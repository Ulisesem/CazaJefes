package dev.cazajefes;
import java.util.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitTask;
/** Persistent final-blow victories. Read API is registered in Bukkit ServicesManager. */
public final class BossStatistics {
 public record PlayerStats(UUID uuid,String name,long total,Map<String,Long> bosses,Map<String,Long> categories,long lastVictory){public PlayerStats{bosses=Map.copyOf(bosses);categories=Map.copyOf(categories);}}
 record Snapshot(int schema,List<PlayerStats> players,List<UUID> encounters){}
 private static final Gson JSON=new GsonBuilder().setPrettyPrinting().create();
 private final CazaJefesPlugin plugin;private final Map<UUID,PlayerStats> players=new HashMap<>();private final LinkedHashSet<UUID> encounters=new LinkedHashSet<>();
 private final ExecutorService writer=Executors.newSingleThreadExecutor(r->{Thread t=new Thread(r,"CazaJefes-statistics");t.setDaemon(true);return t;});
 private final BukkitTask timer;private volatile boolean dirty;
 BossStatistics(CazaJefesPlugin plugin){this.plugin=plugin;Path file=plugin.getDataFolder().toPath().resolve("boss-statistics.json");if(Files.exists(file))try{Snapshot snapshot=JSON.fromJson(Files.readString(file),Snapshot.class);if(snapshot==null||snapshot.schema()!=1)throw new IllegalArgumentException("Formato no compatible");for(PlayerStats row:snapshot.players())players.put(row.uuid(),row);encounters.addAll(snapshot.encounters());}catch(Exception error){writer.shutdown();throw new IllegalStateException("No se pudo leer boss-statistics.json; no se sobrescribió el registro",error);}timer=plugin.getServer().getScheduler().runTaskTimer(plugin,this::flush,20,20);}
 public long total(UUID player){PlayerStats row=players.get(player);return row==null?0:row.total();}
 public PlayerStats get(UUID player){return players.get(player);}
 public List<PlayerStats> ranking(int limit){return players.values().stream().sorted(Comparator.comparingLong(PlayerStats::total).reversed().thenComparing(PlayerStats::name).thenComparing(p->p.uuid().toString())).limit(Math.max(0,limit)).toList();}
 static PlayerStats increment(PlayerStats previous,UUID id,String name,String boss,String category,long now){Map<String,Long> bosses=new HashMap<>(previous==null?Map.of():previous.bosses()),categories=new HashMap<>(previous==null?Map.of():previous.categories());bosses.merge(boss,1L,Long::sum);categories.merge(category,1L,Long::sum);return new PlayerStats(id,name,previous==null?1:previous.total()+1,bosses,categories,now);}
 static Player finalPlayer(org.bukkit.event.entity.EntityDamageEvent event){if(!(event instanceof EntityDamageByEntityEvent hit)||hit.isCancelled()||hit.getFinalDamage()<=0)return null;Entity source=hit.getDamager();if(source instanceof Projectile projectile&&projectile.getShooter() instanceof Entity shooter)source=shooter;return source instanceof Player player?player:null;}
 void defeated(LivingEntity boss,BossCatalog.Definition definition,UUID encounter){
  Player player=finalPlayer(boss.getLastDamageCause());
  if(player==null||!CombatGuard.eligible(player)||!plugin.guard.canAttackBoss(player)||!encounters.add(encounter))return;
  while(encounters.size()>1000)encounters.remove(encounters.iterator().next());String category=definition.mini()?"MINI":definition.tier().name();players.put(player.getUniqueId(),increment(players.get(player.getUniqueId()),player.getUniqueId(),player.getName(),definition.id(),category,System.currentTimeMillis()));dirty=true;
 }
 public void command(org.bukkit.command.CommandSender sender,String[] args){if(args[0].equalsIgnoreCase("ranking")){sender.sendMessage("§6§l✦ CAZADORES · VICTORIAS");int rank=1;for(PlayerStats row:ranking(10))sender.sendMessage("§e§l"+(rank++)+". §f"+row.name()+" §a"+row.total());return;}PlayerStats row=args.length>1?players.values().stream().filter(p->p.name().equalsIgnoreCase(args[1])).findFirst().orElse(null):sender instanceof Player p?players.get(p.getUniqueId()):null;sender.sendMessage("§6§l✦ Bosses derrotados · §f"+(row==null?0:row.total())+" §7"+(row==null?"":row.name()));}
 private void flush(){if(!dirty)return;dirty=false;Snapshot snapshot=new Snapshot(1,ranking(Integer.MAX_VALUE),List.copyOf(encounters));String json=JSON.toJson(snapshot)+"\n";StringBuilder csv=new StringBuilder("uuid,jugador,bosses_derrotados,ultima_victoria_ms\n");for(PlayerStats row:snapshot.players())csv.append(row.uuid()).append(',').append(row.name()).append(',').append(row.total()).append(',').append(row.lastVictory()).append('\n');Path folder=plugin.getDataFolder().toPath();writer.submit(()->{try{Files.createDirectories(folder);atomic(folder.resolve("boss-statistics.json"),json);atomic(folder.resolve("boss-statistics.csv"),csv.toString());}catch(Exception error){dirty=true;plugin.getLogger().severe("No se pudo guardar el registro de victorias: "+error.getMessage());}});}
 private static void atomic(Path file,String content)throws java.io.IOException{Path temporary=file.resolveSibling(file.getFileName()+".tmp");Files.writeString(temporary,content,StandardCharsets.UTF_8);try{Files.move(temporary,file,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);}catch(AtomicMoveNotSupportedException ignored){Files.move(temporary,file,StandardCopyOption.REPLACE_EXISTING);}}
 void stop(){timer.cancel();flush();writer.shutdown();try{if(!writer.awaitTermination(5,TimeUnit.SECONDS))plugin.getLogger().warning("El registro de victorias sigue guardándose");}catch(InterruptedException error){Thread.currentThread().interrupt();}}
}
