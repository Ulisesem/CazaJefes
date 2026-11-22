package dev.cazajefes;

import dev.cazajefes.integration.PacketBridge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/** Owns only the main hand. Armor and the off-hand never participate in an axe throw. */
final class ThrownAxeHand {
   private static final NamespacedKey SAVED = new NamespacedKey("cazajefes","axe_throw_weapon");
   private static final Map<UUID,Hand> active = new HashMap<>();

   private static final class Hand {
      final LivingEntity boss;
      final ItemStack axe;
      final AxeThrowSettings settings;
      Runnable removeVisual;
      BukkitTask timer;
      boolean lodged;
      Hand(LivingEntity boss,ItemStack axe,AxeThrowSettings settings) {
         this.boss=boss; this.axe=axe; this.settings=settings;
      }
   }

   private ThrownAxeHand() {}

   static boolean isWeapon(Material type) {
      String name=type.name();
      return name.endsWith("_SWORD") || name.endsWith("_AXE") || name.endsWith("_SPEAR")
         || type==Material.BOW || type==Material.CROSSBOW || type==Material.TRIDENT || type==Material.MACE;
   }

   static boolean inUse(LivingEntity boss) {
      return boss.getPersistentDataContainer().has(SAVED,PersistentDataType.BYTE_ARRAY);
   }

   static boolean usesPhysicalHand(LivingEntity boss) {
      var equipment = boss.getEquipment();
      return equipment != null && (isWeapon(equipment.getItemInMainHand().getType())
         || boss.getPersistentDataContainer().has(SAVED,PersistentDataType.BYTE_ARRAY));
   }

   static boolean prepare(LivingEntity boss,AxeThrowSettings settings) {
      var equipment=boss.getEquipment();
      if(!settings.enabled() || equipment==null) return false;
      var data=boss.getPersistentDataContainer();
      if(data.has(SAVED,PersistentDataType.BYTE_ARRAY)) return false;
      var original=equipment.getItemInMainHand();
      if(!isWeapon(original.getType())) return false;
      data.set(SAVED,PersistentDataType.BYTE_ARRAY,original.serializeAsBytes());
      // Reuse an actual axe verbatim, including its material, name and enchantments.
      var axe=original.getType().name().endsWith("_AXE") ? original.clone()
         : new ItemStack(AxeThrowSettings.axe(original.getType(),settings.fallbackAxe()));
      axe.setAmount(1);
      if(!axe.hasItemMeta()) {
         var meta=axe.getItemMeta();meta.setEnchantmentGlintOverride(true);axe.setItemMeta(meta);
      }
      active.put(boss.getUniqueId(),new Hand(boss,axe,settings));
      equipment.setItemInMainHand(axe.clone(),true);
      PacketBridge.equipment(boss);
      return true;
   }

   static ItemStack axe(LivingEntity boss) {
      Hand hand=active.get(boss.getUniqueId());
      return hand==null?null:hand.axe.clone();
   }

   static double meleeBonus(LivingEntity boss) {
      Hand hand=active.get(boss.getUniqueId());
      return hand!=null&&hand.lodged?SharpnessRules.bonus(hand.settings.swordSharpness()):0;
   }

   static void launch(LivingEntity boss) {
      var equipment=boss.getEquipment();
      if(equipment!=null && active.containsKey(boss.getUniqueId())) {
         equipment.setItemInMainHand(new ItemStack(Material.AIR),true);
         PacketBridge.equipment(boss);
      }
   }

   /** The cast can finish while the boss fights with a sword and its axe stays lodged. */
   static boolean lodge(LivingEntity boss,Plugin plugin,Runnable removeVisual) {
      Hand hand=active.get(boss.getUniqueId());
      if(hand==null || hand.lodged || boss.getEquipment()==null) { removeVisual.run();return false; }
      hand.lodged=true;
      hand.removeVisual=removeVisual;
      var sword=new ItemStack(AxeThrowSettings.sword(hand.axe.getType()));
      var meta=sword.getItemMeta();
      meta.displayName(Component.text("Filo de Respaldo",NamedTextColor.GOLD));
      meta.setUnbreakable(true);
      meta.setEnchantmentGlintOverride(true);
      meta.getPersistentDataContainer().set(BossEquipment.DISPLAY,PersistentDataType.BYTE,(byte)1);
      meta.addEnchant(Enchantment.SHARPNESS,hand.settings.swordSharpness(),true);
      meta.addEnchant(Enchantment.UNBREAKING,hand.settings.swordUnbreaking(),true);
      sword.setItemMeta(meta);
      boss.getEquipment().setItemInMainHand(sword,true);
      PacketBridge.equipment(boss);
      hand.timer=new BukkitRunnable() {
         int elapsed;
         @Override public void run() {
            elapsed+=5;
            if(!boss.isValid() || boss.isDead() || elapsed>=hand.settings.lodgedTicks()) restore(boss,plugin);
         }
      }.runTaskTimer(plugin,5,5);
      return true;
   }

   static void restore(LivingEntity boss,Plugin plugin) {
      Hand hand=active.remove(boss.getUniqueId());
      if(hand!=null) {
         if(hand.timer!=null) hand.timer.cancel();
         if(hand.removeVisual!=null) hand.removeVisual.run();
      }
      var data=boss.getPersistentDataContainer();
      byte[] original=data.get(SAVED,PersistentDataType.BYTE_ARRAY);
      if(original==null) return;
      var equipment=boss.getEquipment();
      try {
         if(equipment!=null && boss.isValid() && !boss.isDead()) {
            equipment.setItemInMainHand(ItemStack.deserializeBytes(original),true);
            PacketBridge.equipment(boss);
         }
      } catch(IllegalArgumentException invalid) {
         plugin.getLogger().warning("No se pudo restaurar el arma del jefe "+boss.getUniqueId());
      } finally {
         data.remove(SAVED);
      }
   }

   static void stop(Plugin plugin) {
      for(Hand hand:new ArrayList<>(active.values())) restore(hand.boss,plugin);
   }
}
