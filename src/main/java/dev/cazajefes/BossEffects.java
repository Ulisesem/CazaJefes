package dev.cazajefes;
import java.util.Set;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.*;
final class BossEffects {
 private static final Set<String> NEGATIVE=Set.of("slowness","weakness","poison","wither","blindness","nausea","mining_fatigue","hunger","levitation","darkness","unluck","bad_omen","infested","oozing","weaving","wind_charged");
 static int duration(String key,int original){return (key.equals("blindness")||key.equals("nausea"))?200:NEGATIVE.contains(key)?600:original;}
 static boolean blockedOnBoss(String key){return key.equals("blindness")||key.equals("nausea")||key.equals("mining_fatigue");}
 static PotionEffect effect(PotionEffectType type,int duration,int amplifier){return effect(type,duration,amplifier,false,true,true);}
 static PotionEffect effect(PotionEffectType type,int duration,int amplifier,boolean ambient,boolean particles){return effect(type,duration,amplifier,ambient,particles,particles);}
 static PotionEffect effect(PotionEffectType type,int duration,int amplifier,boolean ambient,boolean particles,boolean icon){return new PotionEffect(type,duration(type.getKey().getKey(),duration),amplifier,ambient,particles,icon);}
 private static final java.util.Map<java.util.UUID,java.util.Map<PotionEffectType,Long>> sensory=new java.util.HashMap<>();
 static void prune(){long now=System.currentTimeMillis();sensory.values().forEach(m->m.values().removeIf(end->end<=now));sensory.values().removeIf(java.util.Map::isEmpty);int tick=org.bukkit.Bukkit.getCurrentTick();ownership.values().forEach(m->m.values().removeIf(owned->owned.expires()<tick));ownership.values().removeIf(java.util.Map::isEmpty);}
 static boolean ready(long now,long until){return now>=until;}
 private record Owned(java.util.UUID boss,PotionEffect effect,int expires){}
 private static final java.util.Map<java.util.UUID,java.util.Map<PotionEffectType,Owned>> ownership=new java.util.HashMap<>();
 static boolean applyFrom(LivingEntity boss,LivingEntity target,PotionEffect effect){if(boss==null||boss.isDead()||!boss.isValid())return false;boolean changed=apply(target,effect);if(changed&&target instanceof org.bukkit.entity.Player&&NEGATIVE.contains(effect.getType().getKey().getKey()))ownership.computeIfAbsent(target.getUniqueId(),id->new java.util.HashMap<>()).put(effect.getType(),new Owned(boss.getUniqueId(),effect,org.bukkit.Bukkit.getCurrentTick()+effect.getDuration()));return changed;}
 static void finish(LivingEntity boss){var players=ownership.entrySet().iterator();while(players.hasNext()){var playerEntry=players.next();var player=org.bukkit.Bukkit.getPlayer(playerEntry.getKey());var effects=playerEntry.getValue().entrySet().iterator();while(effects.hasNext()){var entry=effects.next();var owned=entry.getValue();if(!owned.boss().equals(boss.getUniqueId()))continue;if(player!=null){var active=player.getPotionEffect(entry.getKey());int remaining=owned.expires()-org.bukkit.Bukkit.getCurrentTick();if(active!=null&&active.getAmplifier()==owned.effect().getAmplifier()&&Math.abs(active.getDuration()-remaining)<=10)player.removePotionEffect(entry.getKey());}effects.remove();}if(playerEntry.getValue().isEmpty())players.remove();}}
 static void clear(){sensory.clear();ownership.clear();}
 static boolean apply(LivingEntity target,PotionEffect effect){
  String key=effect.getType().getKey().getKey();boolean negative=NEGATIVE.contains(key);long now=System.currentTimeMillis();
  if(target instanceof org.bukkit.entity.Player&&negative){if(key.equals("blindness")||key.equals("nausea")){var cds=sensory.computeIfAbsent(target.getUniqueId(),id->new java.util.HashMap<>());if(!ready(now,cds.getOrDefault(effect.getType(),0L)))return false;var prior=target.getPotionEffect(effect.getType());if(prior!=null&&prior.getDuration()>effect.getDuration())return false;boolean changed=target.addPotionEffect(effect);if(changed){cds.put(effect.getType(),now+25000);}return changed;}
  }
var old=target.getPotionEffect(effect.getType());if(NEGATIVE.contains(effect.getType().getKey().getKey())&&old!=null&&old.getAmplifier()==effect.getAmplifier()&&old.getDuration()>=Math.max(1,effect.getDuration()-20))return false;boolean changed=target.addPotionEffect(effect);return changed;}
}
