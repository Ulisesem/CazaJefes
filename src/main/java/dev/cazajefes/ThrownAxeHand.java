package dev.cazajefes;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

final class ThrownAxeHand {
   private static final NamespacedKey SAVED = new NamespacedKey("cazajefes","axe_throw_weapon");

   private ThrownAxeHand() {}

   static boolean isWeapon(Material type) {
      String name=type.name();
      return name.endsWith("_SWORD") || name.endsWith("_AXE") || name.endsWith("_SPEAR")
         || type==Material.BOW || type==Material.CROSSBOW || type==Material.TRIDENT || type==Material.MACE;
   }

   static boolean prepare(LivingEntity boss) {
      var equipment=boss.getEquipment();
      if(equipment==null) return false;
      var data=boss.getPersistentDataContainer();
      if(data.has(SAVED,PersistentDataType.BYTE_ARRAY)) return false;
      var original=equipment.getItemInMainHand();
      if(!isWeapon(original.getType())) return false;
      data.set(SAVED,PersistentDataType.BYTE_ARRAY,original.serializeAsBytes());
      equipment.setItemInMainHand(new ItemStack(Material.NETHERITE_AXE),true);
      return true;
   }

   static void launch(LivingEntity boss) {
      var equipment=boss.getEquipment();
      if(equipment!=null && boss.getPersistentDataContainer().has(SAVED,PersistentDataType.BYTE_ARRAY)) {
         equipment.setItemInMainHand(null,true);
      }
   }

   static void restore(LivingEntity boss,Plugin plugin) {
      var data=boss.getPersistentDataContainer();
      byte[] original=data.get(SAVED,PersistentDataType.BYTE_ARRAY);
      if(original==null) return;
      var equipment=boss.getEquipment();
      if(equipment==null) return;
      try {
         if(!boss.isDead()) equipment.setItemInMainHand(ItemStack.deserializeBytes(original),true);
      } catch(IllegalArgumentException invalid) {
         plugin.getLogger().warning("No se pudo restaurar el arma del jefe "+boss.getUniqueId());
      } finally {
         data.remove(SAVED);
      }
   }
}
