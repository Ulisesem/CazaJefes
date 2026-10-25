package dev.cazajefes;
import java.util.*;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import net.kyori.adventure.text.Component;
final class BossStages {
 private static final NamespacedKey KEY=new NamespacedKey("cazajefes","battle_stage");
 private final LivingEntity boss;private final BossCatalog.Definition definition;private final Map<UUID,Integer> seen=new HashMap<>();
 BossStages(LivingEntity boss,BossCatalog.Definition definition){this.boss=boss;this.definition=definition;}
 static int phase(LivingEntity boss){int current=Math.max(boss.getPersistentDataContainer().getOrDefault(KEY,PersistentDataType.INTEGER,1),PhaseRules.phase(boss.getHealth()/Math.max(1,boss.getMaxHealth())));current=Math.min(3,current);boss.getPersistentDataContainer().set(KEY,PersistentDataType.INTEGER,current);return current;}
 void update(){int stage=phase(boss);Set<UUID> online=new HashSet<>();for(UUID id:BossParticipation.participants(boss)){Player player=org.bukkit.Bukkit.getPlayer(id);if(player==null||!player.isOnline())continue;online.add(id);if(seen.getOrDefault(id,0)!=stage){player.sendMessage(Component.text("✦ "+definition.name()+" · ETAPA "+stage+"/3 · "+(stage==1?"Acecho":stage==2?"Ruptura":"Furia final"),definition.tier().color));seen.put(id,stage);}}seen.keySet().retainAll(online);}
}
