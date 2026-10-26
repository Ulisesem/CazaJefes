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
 static String material(BossCatalog.Tier tier){return tier.ordinal()>=4?"NETHERITE":tier.ordinal()>=2?"DIAMOND":"IRON";}
 static boolean isDisplay(ItemStack stack){return stack!=null&&stack.hasItemMeta()&&stack.getItemMeta().getPersistentDataContainer().has(DISPLAY,PersistentDataType.BYTE);}
 private static ItemStack gear(Material material,BossCatalog.Definition boss,boolean weapon){ItemStack item=new ItemStack(material);var meta=item.getItemMeta();meta.displayName(Component.text("Guardia de la Grieta · "+boss.tier().display,boss.tier().color).decoration(TextDecoration.ITALIC,false));meta.setEnchantmentGlintOverride(true);meta.getPersistentDataContainer().set(DISPLAY,PersistentDataType.BYTE,(byte)1);item.setItemMeta(meta);item.addUnsafeEnchantment(weapon?Enchantment.SHARPNESS:Enchantment.PROTECTION,10);item.addUnsafeEnchantment(Enchantment.UNBREAKING,3);return item;}
 static ItemStack totem(){ItemStack item=new ItemStack(Material.TOTEM_OF_UNDYING);var meta=item.getItemMeta();meta.displayName(Component.text("Suspiro del Renacido",NamedTextColor.GOLD).decorate(TextDecoration.BOLD));meta.setEnchantmentGlintOverride(true);meta.getPersistentDataContainer().set(DISPLAY,PersistentDataType.BYTE,(byte)1);item.setItemMeta(meta);return item;}
 static void apply(LivingEntity entity,BossCatalog.Definition boss){if(!zombie(entity.getType())||entity.getEquipment()==null)return;EntityEquipment equipment=entity.getEquipment();String material=material(boss.tier());equipment.setHelmet(gear(Material.valueOf(material+"_HELMET"),boss,false),true);equipment.setChestplate(gear(Material.valueOf(material+"_CHESTPLATE"),boss,false),true);equipment.setLeggings(gear(Material.valueOf(material+"_LEGGINGS"),boss,false),true);equipment.setBoots(gear(Material.valueOf(material+"_BOOTS"),boss,false),true);String choice=entity.getPersistentDataContainer().get(WEAPON,PersistentDataType.STRING);if(choice==null){choice=java.util.concurrent.ThreadLocalRandom.current().nextBoolean()?"AXE":"SWORD";entity.getPersistentDataContainer().set(WEAPON,PersistentDataType.STRING,choice);}boolean axe=choice.equals("AXE");equipment.setItemInMainHand(gear(Material.valueOf(material+(axe?"_AXE":"_SWORD")),boss,true),true);equipment.setItemInOffHand(BossRebirth.used(entity)?new ItemStack(Material.AIR):totem(),true);
  for(EquipmentSlot slot:new EquipmentSlot[]{EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.HAND,EquipmentSlot.OFF_HAND})equipment.setDropChance(slot,0);entity.setCanPickupItems(false);
 }
}
