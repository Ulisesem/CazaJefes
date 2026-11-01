package dev.cazajefes;
import java.util.*;
final class RareMenuStyle {
 static int[] slots(int count){if(count<=7){int start=22-count/2;int[] out=new int[count];for(int i=0;i<count;i++)out[i]=start+i;return out;}if(count<=14){int first=Math.min(7,(count+1)/2),second=count-first;int[] out=new int[count];for(int i=0;i<first;i++)out[i]=22-first/2+i;for(int i=0;i<second;i++)out[first+i]=31-second/2+i;return out;}return PremiumStyle.GRID;}
 static String section(RareCompatibility.Group group){return group==null?"COLECCIÓN DE RARES":group.title.toUpperCase(Locale.ROOT);}
}
