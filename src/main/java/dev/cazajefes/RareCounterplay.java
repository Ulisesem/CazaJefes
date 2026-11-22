package dev.cazajefes;

import java.util.*;

/** Shared curse lifetime lets Cleanse remove both melee and ranged healing suppression. */
final class RareCounterplay {
 private static final Map<UUID,Map<String,Long>> healingBlocks=new HashMap<>();
 private static final Map<UUID,Long> purified=new HashMap<>();
 private RareCounterplay(){}
 static boolean curse(UUID target,String power,long until){prune();if(protectedFromCurse(target)||healingBlocks.size()>=4096&&!healingBlocks.containsKey(target))return false;healingBlocks.computeIfAbsent(target,key->new HashMap<>()).merge(power,until,Math::max);return true;}
 static boolean healingBlocked(UUID target){var powers=healingBlocks.get(target);return powers!=null&&powers.values().stream().anyMatch(until->until>System.currentTimeMillis());}
 static boolean healingBlocked(UUID target,String power){var powers=healingBlocks.get(target);return powers!=null&&powers.getOrDefault(power,0L)>System.currentTimeMillis();}
 static boolean protectedFromCurse(UUID target){return purified.getOrDefault(target,0L)>System.currentTimeMillis();}
 static void cleanse(UUID target,int level){healingBlocks.remove(target);prune();if(purified.size()<4096||purified.containsKey(target))purified.put(target,System.currentTimeMillis()+1000L*(2+Math.max(1,Math.min(4,level))));}
 static void remove(UUID target){healingBlocks.remove(target);purified.remove(target);}
 static void prune(){long now=System.currentTimeMillis();healingBlocks.values().forEach(powers->powers.values().removeIf(until->until<=now));healingBlocks.values().removeIf(Map::isEmpty);purified.values().removeIf(until->until<=now);}
 static void clear(){healingBlocks.clear();purified.clear();}
}
