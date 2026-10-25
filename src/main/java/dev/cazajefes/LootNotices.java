package dev.cazajefes;
final class LootNotices {
 static boolean announceRare(boolean bossDrop,boolean rare,int amount,int remaining,boolean announced){return bossDrop&&rare&&amount>0&&remaining<amount&&!announced;}
}
