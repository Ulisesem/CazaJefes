package dev.cazajefes;
import java.util.*;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.*;
final class DamageLedger {
 static final NamespacedKey KEY=new NamespacedKey("cazajefes","battle_damage");
 record Entry(UUID id,String name,double damage){}
 static Map<UUID,Entry> parse(String text){Map<UUID,Entry> out=new HashMap<>();if(text!=null)for(String row:text.split(";"))try{String[] cells=row.split(":");if(cells.length!=3)continue;UUID id=UUID.fromString(cells[0]);double damage=Double.parseDouble(cells[2]);if(Double.isFinite(damage)&&damage>0&&cells[1].matches("[a-zA-Z0-9_]{1,16}"))out.put(id,new Entry(id,cells[1],damage));}catch(IllegalArgumentException ignored){}return out;}
 static double accepted(double damage,double health){return Double.isFinite(damage)&&Double.isFinite(health)?Math.max(0,Math.min(damage,health)):0;}
 static List<Entry> sorted(Map<UUID,Entry> data){return data.values().stream().sorted(Comparator.comparingDouble(Entry::damage).reversed().thenComparing(Entry::name).thenComparing(e->e.id().toString())).toList();}
 static String encode(Map<UUID,Entry> data){return String.join(";",sorted(data).stream().map(e->e.id()+":"+e.name()+":"+e.damage()).toList());}
 static void record(LivingEntity boss,Player player,double damage){double value=accepted(damage,boss.getHealth());if(value<=0||boss.isInvulnerable())return;Map<UUID,Entry> data=parse(boss.getPersistentDataContainer().get(KEY,PersistentDataType.STRING));Entry old=data.get(player.getUniqueId());data.put(player.getUniqueId(),new Entry(player.getUniqueId(),player.getName(),value+(old==null?0:old.damage())));boss.getPersistentDataContainer().set(KEY,PersistentDataType.STRING,encode(data));}
 static Component report(LivingEntity boss,BossCatalog.Definition definition,String winner,RelicItems.LootResult loot){
  Component out=gold("━━━━━━━━ ✦ VICTORIA ✦ ━━━━━━━━").append(Component.newline()).append(aqua(winner)).append(gold(" derrotó a ")).append(aqua(definition.name())).append(Component.newline()).append(gold("✧ BOTÍN · ")).append(loot.summary().color(NamedTextColor.AQUA)).append(Component.newline()).append(gold("⚔ DAÑO DE LOS CAZADORES"));
  List<Entry> entries=sorted(parse(boss.getPersistentDataContainer().get(KEY,PersistentDataType.STRING)));double total=entries.stream().mapToDouble(Entry::damage).sum();int rank=1;
  for(Entry e:entries)out=out.append(Component.newline()).append(gold("  "+(rank++)+". ")).append(aqua(e.name())).append(gold(" » ")).append(aqua(String.format(Locale.ROOT,"%,.1f",e.damage()))).append(gold(" daño · ")).append(aqua(String.format(Locale.ROOT,"%.1f%%",e.damage()/total*100)));
  if(entries.isEmpty())out=out.append(Component.newline()).append(aqua("  Sin daño de jugador registrado."));
  return out.append(Component.newline()).append(gold("━━━━━━━━━━━━━━━━━━━━━━━━━━"));
 }
 private static Component gold(String text){return Component.text(text,NamedTextColor.GOLD).decoration(TextDecoration.ITALIC,false);}
 private static Component aqua(String text){return Component.text(text,NamedTextColor.AQUA).decoration(TextDecoration.ITALIC,false);}
}
