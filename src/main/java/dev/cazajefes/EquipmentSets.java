package dev.cazajefes;
import java.util.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.*;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
final class EquipmentSets {
 static final List<BossCatalog.Tier> RANKS=List.of(BossCatalog.Tier.EPIC,BossCatalog.Tier.LEGENDARY,BossCatalog.Tier.MYTHIC,BossCatalog.Tier.ABYSSAL);
 static BossCatalog.Tier rank(BossCatalog.Tier t){return t.ordinal()<2?BossCatalog.Tier.EPIC:t;}
 static int sharpness(BossCatalog.Tier t){return rank(t).ordinal()+2;}
 static int protection(BossCatalog.Tier t){return rank(t).ordinal()>=4?8:7;}
 static String setName(BossCatalog.Tier t){return switch(rank(t)){case EPIC->"Velo Astral";case LEGENDARY->"Juramento Solar";case MYTHIC->"Corona del Vacío";default->"Eclipse Eterno";};}
 static ItemStack armor(BossCatalog.Tier tier,int slot,NamespacedKey key){return make(tier,slot,true,key);}
 static ItemStack weapon(BossCatalog.Tier tier,int slot,NamespacedKey key){return make(tier,slot,false,key);}
 private static ItemStack make(BossCatalog.Tier tier,int slot,boolean armor,NamespacedKey key){
  tier=rank(tier);String[] pieces=armor?new String[]{"HELMET","CHESTPLATE","LEGGINGS","BOOTS"}:new String[]{"SWORD","AXE"};
  if(slot<0||slot>=pieces.length)throw new IllegalArgumentException("Pieza inválida");
  ItemStack item=new ItemStack(Material.valueOf((tier.ordinal()>=4?"NETHERITE_":"DIAMOND_")+pieces[slot]));ItemMeta meta=item.getItemMeta();
  String[] names=armor?new String[]{"Yelmo","Coraza","Grebas","Botas"}:new String[]{"Espada","Hacha"};
  meta.displayName(Component.text("["+tier.display.toUpperCase(Locale.ROOT)+"] "+names[slot]+" · "+setName(tier),tier.ordinal()==5?NamedTextColor.DARK_PURPLE:NamedTextColor.GOLD).decoration(TextDecoration.ITALIC,false));
  meta.lore(List.of(gray("Colección "+setName(tier)),gray(armor?"Forjada para resistir la furia de la grieta.":"Su filo resuena con el poder de la grieta."),gray((armor?"Protección ":"Filo ")+(armor?protection(tier):sharpness(tier))+" • Categoría "+tier.display)));
  meta.getPersistentDataContainer().set(key,PersistentDataType.STRING,(armor?"armadura_set_":"arma_set_")+tier.name().toLowerCase(Locale.ROOT)+"_"+slot);meta.setEnchantmentGlintOverride(true);item.setItemMeta(meta);
  item.addUnsafeEnchantment(armor?Enchantment.PROTECTION:Enchantment.SHARPNESS,armor?protection(tier):sharpness(tier));item.addUnsafeEnchantment(Enchantment.UNBREAKING,3);if(tier.ordinal()>=4)item.addUnsafeEnchantment(Enchantment.MENDING,1);return item;
 }
 private static Component gray(String text){return Component.text(text,NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false);}
 static BossCatalog.Tier tier(String id){
  if(id==null)return null;
  for(var t:BossCatalog.Tier.values())if(id.startsWith("armadura_set_"+t.name().toLowerCase(Locale.ROOT)+"_")||id.startsWith("arma_set_"+t.name().toLowerCase(Locale.ROOT)+"_")||id.equals("cristal_encantado_"+t.name().toLowerCase(Locale.ROOT)))return t;
  for(String prefix:new String[]{"armadura_","arma_"})if(id.startsWith(prefix)){int end=id.lastIndexOf('_');if(end>prefix.length()){var boss=BossCatalog.get(id.substring(prefix.length(),end));if(boss!=null)return rank(boss.tier());}}
  if(List.of("espada_brezal","hacha_sismica","arco_ceniciento","lanza_magma","mandoble_vacio").contains(id))return RecipeAlchemy.profile(id).rank();return null;
 }
 static ItemStack migrateArmor(ItemStack item,String id,NamespacedKey key){return migrate(item,id,key,true);}
 static ItemStack migrateWeapon(ItemStack item,String id,NamespacedKey key){return migrate(item,id,key,false);}
 private static ItemStack migrate(ItemStack item,String id,NamespacedKey key,boolean armor){
  var tier=tier(id);if(tier==null)return item;int slot;try{slot=Integer.parseInt(id.substring(id.lastIndexOf('_')+1));}catch(NumberFormatException e){return item;}
  if(slot<0||slot>=(armor?4:2))return item;ItemStack model=make(tier,slot,armor,key),out=item.clone();ItemMeta meta=out.getItemMeta(),template=model.getItemMeta();
  meta.displayName(template.displayName());List<Component> lore=new ArrayList<>(template.lore());if(meta.lore()!=null)for(Component line:meta.lore()){String text=net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(line);if(text.contains("Rare")||text.contains("RARE"))lore.add(gray(text));}
  meta.lore(lore);meta.setEnchantmentGlintOverride(true);meta.getPersistentDataContainer().set(key,PersistentDataType.STRING,template.getPersistentDataContainer().get(key,PersistentDataType.STRING));out.setItemMeta(meta);out.addUnsafeEnchantment(armor?Enchantment.PROTECTION:Enchantment.SHARPNESS,armor?protection(tier):sharpness(tier));return out;
 }
}
