package dev.cazajefes;
final class LootNotices {
 static boolean isRare(String id,java.util.Collection<String> rareIds,boolean book,boolean trophy){return (id!=null&&rareIds.contains(id))||book||trophy;}
 static boolean announceRare(boolean bossDrop,boolean rare,int amount,int remaining,boolean announced){return bossDrop&&rare&&amount>0&&remaining<amount&&!announced;}
}
