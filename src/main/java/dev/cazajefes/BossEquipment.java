package dev.cazajefes;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.*;
import org.bukkit.persistence.PersistentDataType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.*;
final class BossEquipment {
 private static final NamespacedKey WEAPON=new NamespacedKey("cazajefes","random_weapon");
 private static final NamespacedKey DISPLAY=new NamespacedKey("cazajefes","boss_display_gear");
 static boolean zombie(EntityType type){return switch(type){case ZOMBIE,HUSK,DROWNED,ZOMBIE_VILLAGER,ZOMBIFIED_PIGLIN,GIANT->true;default->false;};}
 private static boolean piglin(EntityType type){return type==EntityType.PIGLIN||type==EntityType.PIGLIN_BRUTE||type==EntityType.ZOMBIFIED_PIGLIN;}
 private static final NamespacedKey PIGLIN_HAND=new NamespacedKey("cazajefes","piglin_main_weapon");
 static void maintainPiglin(LivingEntity entity,BossCatalog.Definition boss){
  if(!piglin(entity.getType()))return;
  var equipment=entity.getEquipment();
  if(equipment==null)return;
  String material=material(boss.tier());
  for(var slot:new EquipmentSlot[]{EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}){
   ItemStack current=equipment.getItem(slot);
   if(isDisplay(current)&&current.getItemMeta().isUnbreakable())continue;
   String piece=switch(slot){case HEAD->"HELMET";case CHEST->"CHESTPLATE";case LEGS->"LEGGINGS";case FEET->"BOOTS";default->throw new IllegalStateException();};
   var item=gear(Material.valueOf(material+"_"+piece),boss,false);
   if(slot==EquipmentSlot.FEET)item.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER,10);
   equipment.setItem(slot,item,true);
  }
  if(ThrownAxeHand.inUse(entity))return;
  ItemStack main=equipment.getItemInMainHand();
  var data=entity.getPersistentDataContainer();
  String kind=main.getType()==Material.CROSSBOW?"crossbow":main.getType().name().endsWith("_AXE")?"axe"
      :main.getType().name().endsWith("_SWORD")?"sword":data.get(PIGLIN_HAND,PersistentDataType.STRING);
  if(kind==null)kind=entity.getType()==EntityType.PIGLIN_BRUTE
      ||"AXE".equals(data.get(WEAPON,PersistentDataType.STRING))?"axe":"sword";
  if(!kind.equals(data.get(PIGLIN_HAND,PersistentDataType.STRING)))data.set(PIGLIN_HAND,PersistentDataType.STRING,kind);
  if(isDisplay(main)&&main.getItemMeta().isUnbreakable())return;
  ItemStack weapon=kind.equals("crossbow")?crossbow(boss)
      :gear(Material.valueOf(material+(kind.equals("axe")?"_AXE":"_SWORD")),boss,true);
  equipment.setItemInMainHand(weapon,true);
  dev.cazajefes.integration.PacketBridge.equipment(entity);
 }
 static String material(BossCatalog.Tier tier){return tier.ordinal()>=4?"NETHERITE":tier.ordinal()>=2?"DIAMOND":"IRON";}
 static boolean isDisplay(ItemStack stack){return stack!=null&&stack.hasItemMeta()&&stack.getItemMeta().getPersistentDataContainer().has(DISPLAY,PersistentDataType.BYTE);}
 private static ItemStack gear(Material material,BossCatalog.Definition boss,boolean weapon){ItemStack item=new ItemStack(material);var meta=item.getItemMeta();meta.displayName(Component.text("Guardia de la Grieta · "+boss.tier().display,boss.tier().color).decoration(TextDecoration.ITALIC,false));meta.setEnchantmentGlintOverride(true);if(piglin(boss.entityType()))meta.setUnbreakable(true);meta.getPersistentDataContainer().set(DISPLAY,PersistentDataType.BYTE,(byte)1);item.setItemMeta(meta);item.addUnsafeEnchantment(weapon?Enchantment.SHARPNESS:Enchantment.PROTECTION,weapon||SkeletonCombat.archer(boss.entityType())?10:BossProtection.level(boss));item.addUnsafeEnchantment(Enchantment.UNBREAKING,weapon||SkeletonCombat.archer(boss.entityType())?10:3);return item;}
 static ItemStack crossbow(BossCatalog.Definition boss){ItemStack item=new ItemStack(Material.CROSSBOW);var meta=item.getItemMeta();meta.displayName(Component.text("Ballesta del Destierro · "+boss.tier().display,boss.tier().color).decoration(TextDecoration.ITALIC,false));meta.setEnchantmentGlintOverride(true);meta.getPersistentDataContainer().set(DISPLAY,PersistentDataType.BYTE,(byte)1);meta.setUnbreakable(true);for(var entry:PillagerRules.ENCHANTMENTS.entrySet()){var enchantment=Registry.ENCHANTMENT.get(NamespacedKey.minecraft(entry.getKey()));if(enchantment!=null)meta.addEnchant(enchantment,entry.getValue(),true);}item.setItemMeta(meta);return item;}
 static ItemStack bow(BossCatalog.Definition boss){var item=new ItemStack(Material.BOW);var meta=item.getItemMeta();meta.displayName(Component.text("Arco del Eclipse · "+boss.tier().display,boss.tier().color).decoration(TextDecoration.ITALIC,false));meta.setEnchantmentGlintOverride(true);meta.setUnbreakable(true);meta.getPersistentDataContainer().set(DISPLAY,PersistentDataType.BYTE,(byte)1);item.setItemMeta(meta);for(var enchant:new Enchantment[]{Enchantment.POWER,Enchantment.PUNCH,Enchantment.FLAME,Enchantment.UNBREAKING})item.addUnsafeEnchantment(enchant,10);item.addUnsafeEnchantment(Enchantment.INFINITY,1);item.addUnsafeEnchantment(Enchantment.MENDING,1);return item;}
 static ItemStack totem(){ItemStack item=new ItemStack(Material.TOTEM_OF_UNDYING);var meta=item.getItemMeta();meta.displayName(Component.text("Suspiro del Renacido",NamedTextColor.GOLD).decorate(TextDecoration.BOLD));meta.setEnchantmentGlintOverride(true);meta.getPersistentDataContainer().set(DISPLAY,PersistentDataType.BYTE,(byte)1);item.setItemMeta(meta);return item;}
 static double virtualSharpness(LivingEntity entity){var gear=entity.getEquipment();int nativeLevel=gear==null?0:gear.getItemInMainHand().getEnchantmentLevel(Enchantment.SHARPNESS);return SharpnessRules.missingBonus(nativeLevel);}
 static void apply(LivingEntity entity,BossCatalog.Definition boss){entity.getPersistentDataContainer().set(BossProtection.KEY,PersistentDataType.INTEGER,BossProtection.level(boss));if(entity.getEquipment()==null)return;EntityEquipment equipment=entity.getEquipment();String material=SkeletonCombat.archer(boss.entityType())?(boss.tier().ordinal()>=4?"NETHERITE":"DIAMOND"):material(boss.tier());equipment.setHelmet(gear(Material.valueOf(material+"_HELMET"),boss,false),true);equipment.setChestplate(gear(Material.valueOf(material+"_CHESTPLATE"),boss,false),true);equipment.setLeggings(gear(Material.valueOf(material+"_LEGGINGS"),boss,false),true);equipment.setBoots(gear(Material.valueOf(material+"_BOOTS"),boss,false),true);if(zombie(entity.getType())){String choice=entity.getPersistentDataContainer().get(WEAPON,PersistentDataType.STRING);if(choice==null){choice=java.util.concurrent.ThreadLocalRandom.current().nextBoolean()?"AXE":"SWORD";entity.getPersistentDataContainer().set(WEAPON,PersistentDataType.STRING,choice);}boolean axe=choice.equals("AXE");equipment.setItemInMainHand(gear(Material.valueOf(material+(axe?"_AXE":"_SWORD")),boss,true),true);equipment.setItemInOffHand(BossRebirth.used(entity)?new ItemStack(Material.AIR):totem(),true);}
  {var boots=equipment.getBoots();if(boots!=null&&!boots.getType().isAir()){boots.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER,10);equipment.setBoots(boots,true);}}
  var held=equipment.getItemInMainHand();if(held.getType().name().endsWith("_SWORD")||held.getType().name().endsWith("_AXE"))equipment.setItemInMainHand(gear(held.getType(),boss,true),true);
  if(SkeletonCombat.archer(entity.getType()))equipment.setItemInMainHand(bow(boss),true);
  if(entity instanceof Pillager)equipment.setItemInMainHand(crossbow(boss),true);
  maintainPiglin(entity,boss);
  for(EquipmentSlot slot:new EquipmentSlot[]{EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.HAND,EquipmentSlot.OFF_HAND})equipment.setDropChance(slot,0);entity.setCanPickupItems(false);
 }
}
