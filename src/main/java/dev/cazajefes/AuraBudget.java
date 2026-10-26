package dev.cazajefes;
import java.util.*;
final class AuraBudget {
 private static final class Bucket {double tokens;long time;Bucket(long now,int capacity){tokens=capacity;time=now;}}
 private final Map<UUID,Bucket> buckets=new HashMap<>();
 int available(UUID player,long now,int rate){Bucket b=buckets.computeIfAbsent(player,id->new Bucket(now,rate));long elapsed=Math.max(0,now-b.time);b.tokens=Math.min(rate,b.tokens+elapsed*rate/1000.0);b.time=Math.max(b.time,now);return (int)Math.floor(b.tokens);}
 boolean spend(UUID player,int amount,long now,int rate){if(amount<0||available(player,now,rate)<amount)return false;buckets.get(player).tokens-=amount;return true;}
 void remove(UUID player){buckets.remove(player);}void clear(){buckets.clear();}
}
