package dev.cazajefes;
import java.util.*;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.*;
final class PremiumStyle {
 static final int[] GRID={10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34,37,38,39,40,41,42,43};
 static ItemStack tile(Material material,String title,NamedTextColor color,boolean glow,String... lines){
  ItemStack item=new ItemStack(material);ItemMeta meta=item.getItemMeta();meta.displayName(Component.text(title,color).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC,false));
  List<Component> lore=new ArrayList<>();for(String line:lines){
   StringBuilder row=new StringBuilder();for(String word:line.split(" ")){
    if(row.length()>0&&row.length()+word.length()+1>46){lore.add(Component.text(row.toString(),NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false));row.setLength(0);}
    if(row.length()>0)row.append(' ');row.append(word);
   }
   lore.add(Component.text(row.toString(),NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false));
  }
  meta.lore(lore);meta.setEnchantmentGlintOverride(glow);item.setItemMeta(meta);return item;
 }
 static void frame(Inventory inv,Material accent){
  ItemStack dark=tile(Material.BLACK_STAINED_GLASS_PANE," ",NamedTextColor.DARK_GRAY,false);
  for(int i=0;i<inv.getSize();i++)inv.setItem(i,dark);
  for(int slot:GRID)inv.setItem(slot,null);
  for(int i:new int[]{0,1,7,8,45,46,52,53})inv.setItem(i,tile(accent,"✦",NamedTextColor.LIGHT_PURPLE,false));
 }
 static Material rankIcon(BossCatalog.Tier tier){return switch(tier){case COMMON->Material.AMETHYST_SHARD;case RARE->Material.EMERALD;case EPIC->Material.AMETHYST_CLUSTER;case LEGENDARY->Material.BLAZE_POWDER;case MYTHIC->Material.NETHER_STAR;case ABYSSAL->Material.SCULK_SHRIEKER;};}
}
