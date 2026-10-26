package dev.cazajefes;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.GameMode;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.RecipeChoice.ExactChoice;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public final class RelicItems implements Listener {
   private final JavaPlugin plugin;
   private final NamespacedKey itemKey;
   private final NamespacedKey dropEncounterKey;
   private final NamespacedKey rareBookKey;
   private final NamespacedKey rareEnchantKey;
   private final NamespacedKey rareEssenceLevelKey;
   private final NamespacedKey rareLevelsKey;
   private FileConfiguration rareConfig;
   private boolean breakingConnected;
   private final Map<String, Long> burstCooldowns = new HashMap<>();
   private final Map<UUID, Long> passiveRepair = new HashMap<>();
   private final Map<String, Long> passiveSaturation = new HashMap<>();
   private final Map<UUID, Long> flyDrain = new HashMap<>();
   private final Set<UUID> flyUsers = new HashSet<>();

   public RelicItems(JavaPlugin var1, NamespacedKey var2) {
      this.plugin = var1;
      this.itemKey = var2;
      this.dropEncounterKey = new NamespacedKey(var1, "loot_encounter");
      this.rareBookKey = new NamespacedKey(var1, "rare_book");
      this.rareEnchantKey = new NamespacedKey(var1, "rare_enchants");
      this.rareEssenceLevelKey = new NamespacedKey(var1, "rare_essence_level");
      this.rareLevelsKey = new NamespacedKey(var1, "rare_levels");
      File var3 = new File(var1.getDataFolder(), "rares.yml");
      if (!var3.exists()) {
         var1.saveResource("rares.yml", false);
      }

      this.rareConfig = YamlConfiguration.loadConfiguration(var3);
      var1.getServer().getScheduler().runTaskTimer(var1, this::restoreArmorSaturation, 100L, 100L);
      var1.getServer().getScheduler().runTaskTimer(var1, this::tickArmorProperties, 20L, 20L);
   }

   public List<String> enchantIds() {
      return List.of("vampirismo", "veneno", "guardian", "brasas", "paso_viento");
   }

   public List<String> spellIds() {
      return List.of("burst", "craftitem", "fireresistance", "greaterburst", "haste", "invisibility", "magicbag", "repairitem");
   }

   public int maxLevel(String var1) {
      return Math.max(1, Math.min(6, this.rareConfig.getInt(var1 + ".maxLvl", var1.equalsIgnoreCase("haste") ? 6 : 5)));
   }

   public List<String> catalogIds() {
      ArrayList var1 = new ArrayList<>(
         List.of(
            "poison",
            "backstab",
            "blinding",
            "calllighting",
            "confuse",
            "disarm",
            "slow",
            "vampiricregeneration",
            "weaken",
            "execute",
            "bleed",
            "fertilize",
            "meltobsidian",
            "smelt",
            "spore",
            "veinminer",
            "treefeller",
            "magneto",
            "burst",
            "craftitem",
            "fireresistance",
            "greaterburst",
            "haste",
            "invisibility",
            "magicbag",
            "repairitem",
            "durability",
            "regeneration",
            "nofalldamage",
            "doublejump",
            "fly",
            "hardy",
            "saturacion",
            "flame"
         )
      );
      var1.addAll(this.enchantIds());
      return var1;
   }

   public ItemStack rareBook(String var1) {
      return this.rareBook(var1, 1);
   }

   public ItemStack rareBook(String var1, int var2) {
      if (!this.catalogIds().contains(var1) && !this.enchantIds().contains(var1)) {
         return null;
      } else {
         String var3 = this.titleForRare(var1);
         int var4 = Math.max(1, Math.min(this.maxLevel(var1), var2));
         boolean var5 = this.spellIds().contains(var1);
         String var6 = var5
            ? this.spellDescription(var1)
            : (
               var1.equals("flame")
                  ? "Combínala en la mesa de crafteo con un casco."
                  : (var1.equals("magneto") ? "Atrae al inventario todo lo que extraes con un pico." : "Combínala en la mesa de crafteo con equipo compatible.")
            );
         String var7 = var5 ? "SpellBook de la Grieta" : "Esencia: " + var3 + " nivel " + var4;
         ItemStack var8 = this.named(
            var5 ? Material.BOOK : Material.MAGMA_CREAM,
            (var5 ? "rare_spell_book_" : "esencia_rare_") + var1 + "_" + var4,
            var7,
            NamedTextColor.GREEN,
            1,
            var6,
            "Rare: " + var3,
            "Nivel: " + var4
         );
         ItemMeta var9 = var8.getItemMeta();
         var9.displayName(
            var5
               ? ((TextComponent)Component.text(var7, NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD)).decoration(TextDecoration.ITALIC, false)
               : ((TextComponent)Component.text(var7, NamedTextColor.GREEN).decorate(TextDecoration.BOLD)).decoration(TextDecoration.ITALIC, false)
         );
         var9.lore(
            List.of(
               (TextComponent)Component.text(var6, NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false),
               (TextComponent)Component.text("Rare: " + var3, NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
               (TextComponent)Component.text("Nivel: " + var4, NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false),
               (TextComponent)((TextComponent)Component.text("Rare de la Grieta", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                  .decoration(TextDecoration.ITALIC, false)
            )
         );
         var9.getPersistentDataContainer().set(this.rareBookKey, PersistentDataType.STRING, var1);
         var9.getPersistentDataContainer().set(this.rareEssenceLevelKey, PersistentDataType.INTEGER, var4);
         var8.setItemMeta(var9);
         return var8;
      }
   }

   private String spellDescription(String var1) {
      return switch (var1) {
         case "burst" -> "Al usarlo, empuja hacia atrás a los enemigos cercanos.";
         case "craftitem" -> "Abre una mesa de crafteo portátil.";
         case "fireresistance" -> "Te protege temporalmente del fuego y la lava.";
         case "greaterburst" -> "Desata una onda que repele a varios enemigos.";
         case "haste" -> "Te otorga Velocidad temporal al activarlo.";
         case "invisibility" -> "Te vuelve invisible temporalmente.";
         case "magicbag" -> "Abre tu cofre de Ender al instante.";
         case "repairitem" -> "Repara el objeto dañado del primer espacio de la hotbar.";
         default -> "Activa su poder con clic derecho. Consume saturación.";
      };
   }

   private String titleForRare(String var1) {
      return switch (var1) {
         case "vampirismo" -> "Sello Vampírico";
         case "veneno" -> "Veneno Antiguo";
         case "guardian" -> "Piel del Guardián";
         case "brasas" -> "Marca de Brasas";
         case "paso_viento" -> "Paso del Viento";
         case "saturacion" -> "Saturación";
         default -> var1.replace('_', ' ').toUpperCase();
      };
   }

   private int activePropertyLevel(ItemStack item,String property){
      if(item==null||!item.hasItemMeta())return 0;
      String properties=item.getItemMeta().getPersistentDataContainer().get(rareEnchantKey,PersistentDataType.STRING);
      return properties!=null&&Arrays.asList(properties.split(",")).contains(property)?propertyLevel(item,property):0;
   }
   public ItemStack armorSet(BossCatalog.Tier tier,int slot){return EquipmentSets.armor(tier,slot,itemKey);}
   public ItemStack weaponSet(BossCatalog.Tier tier,int slot){return EquipmentSets.weapon(tier,slot,itemKey);}
   private void tickArmorProperties(){
      long now=System.currentTimeMillis();
      for(Player player:plugin.getServer().getOnlinePlayers()){
         ItemStack chest=player.getInventory().getChestplate();int hardy=activePropertyLevel(chest,"hardy");
         if(hardy>0&&consumePassiveSaturation(player,chest,"hardy"))player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,40,Math.min(5,hardy)-1,true,false,true));
         if(now-passiveRepair.getOrDefault(player.getUniqueId(),0L)<5000)continue;
         boolean repaired=false;
         for(ItemStack piece:player.getInventory().getArmorContents()){
            int level=activePropertyLevel(piece,"durability");
            if(level>0&&piece.getItemMeta() instanceof Damageable meta&&meta.getDamage()>0&&consumePassiveSaturation(player,piece,"durability")){
               meta.setDamage(Math.max(0,meta.getDamage()-ArmorPropertyRules.repair(level)));piece.setItemMeta(meta);repaired=true;
            }
         }
         if(repaired)passiveRepair.put(player.getUniqueId(),now);
      }
   }

   private void restoreArmorSaturation() {
      for (Player var2 : this.plugin.getServer().getOnlinePlayers()) {
         int var3 = 0;

         for (ItemStack var7 : var2.getInventory().getArmorContents()) {
            if (var7 != null && var7.hasItemMeta()) {
               String var8 = (String)var7.getItemMeta().getPersistentDataContainer().get(this.rareEnchantKey, PersistentDataType.STRING);
               if (var8 != null && var8.contains("saturacion")) {
                  var3 = Math.max(var3, this.propertyLevel(var7, "saturacion"));
               }
            }
         }

         if (var3 != 0 && var2.getFoodLevel() > 0) {
            float var9 = 0.5F + 0.5F * var3;
            var2.setSaturation(Math.min((float)var2.getFoodLevel(), var2.getSaturation() + var9));
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
   public void onSpellUse(PlayerInteractEvent var1) {
      if (var1.getAction() == Action.RIGHT_CLICK_AIR || var1.getAction() == Action.RIGHT_CLICK_BLOCK) {
         ItemStack var2 = var1.getItem();
         if (var2 == null || !var2.hasItemMeta()) {
            var2 = var1.getHand() == EquipmentSlot.OFF_HAND
               ? var1.getPlayer().getInventory().getItemInOffHand()
               : var1.getPlayer().getInventory().getItemInMainHand();
         }

         if (var2 != null && var2.hasItemMeta()) {
            String var3 = (String)var2.getItemMeta().getPersistentDataContainer().get(this.rareBookKey, PersistentDataType.STRING);
            if (var3 != null && this.spellIds().contains(var3)) {
               Player var4 = var1.getPlayer();
               var1.setCancelled(true);
               long var5 = System.currentTimeMillis();
               if (true) {
                  String var7 = var4.getUniqueId() + ":" + var3;
                  long var8 = this.burstCooldowns.getOrDefault(var7, 0L);
                  if (var8 > var5) {
                     String var24 = var3.equals("burst") ? "Burst" : "GreaterBurst";
                     var4.sendMessage(Component.text(var24 + " estará listo en " + (var8 - var5 + 999L) / 1000L + " segundos.", NamedTextColor.GRAY));
                     return;
                  }
               }

               Player var20 = var4;
               if (var4.isSneaking() && (var3.equals("haste") || var3.equals("invisibility"))) {
                  RayTraceResult var21 = var4.getWorld()
                     .rayTraceEntities(
                        var4.getEyeLocation(),
                        var4.getEyeLocation().getDirection(),
                        24.0,
                        0.65,
                        var1x -> var1x instanceof Player var2x && !var2x.getUniqueId().equals(var4.getUniqueId())
                     );
                  if (var21 == null || !(var21.getHitEntity() instanceof Player var9)) {
                     var4.sendMessage(Component.text("Apunta directamente a un jugador para darle este efecto.", NamedTextColor.YELLOW));
                     return;
                  }

                  var20 = var9;
               }

               double var22 = this.rareConfig.getDouble(var3 + ".useCost", 6.0);
               if (var22 < 0.0) {
                  var4.sendMessage(Component.text("Esta rare está desactivada.", NamedTextColor.RED));
               } else {
                  int var23 = Math.max(1, Math.min(maxLevel(var3), (Integer)var2.getItemMeta().getPersistentDataContainer().getOrDefault(this.rareEssenceLevelKey, PersistentDataType.INTEGER, 1)));
                  if (!this.consumeSaturation(var4, var2, var3)) {
                     var4.sendMessage(Component.text("No tienes suficiente saturación para activar esta rare.", NamedTextColor.RED));
                  } else {
                     this.burstCooldowns.put(var4.getUniqueId() + ":" + var3,
                        var5 + Math.max(0, Math.min(3600, rareConfig.getLong(var3 + ".cooldownSeconds", 10))) * 1000L);

                     switch (var3) {
                        case "haste":
                           var20.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600 * var23, Math.min(5, var23 - 1)));
                           break;
                        case "fireresistance":
                           var20.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 900 * var23, 0));
                           break;
                        case "invisibility":
                           var20.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 500 * var23, 0));
                           break;
                        case "burst":
                        case "greaterburst":
                           double var25 = (var3.equals("burst") ? 3 : 5) + var23 * 2;

                           for (Entity var16 : var4.getNearbyEntities(var25, var25, var25)) {
                              if (var16 instanceof LivingEntity var17 && !var17.getUniqueId().equals(var4.getUniqueId())) {
                                 Vector var18 = var17.getLocation().toVector().subtract(var4.getLocation().toVector());
                                 if (var18.lengthSquared() < 0.01) {
                                    var18 = var4.getLocation().getDirection().multiply(-1);
                                 }

                                 var18.normalize();
                                 Vector var19 = var18.multiply((var3.equals("burst") ? 1.0 : 1.25) + var23 * 0.35).setY(0.65 + var23 * 0.18);
                                 var17.setVelocity(var19);
                              }
                           }

                           var4.getWorld()
                              .spawnParticle(var3.equals("burst") ? Particle.EXPLOSION : Particle.CLOUD, var4.getLocation(), 22, 1.4, 0.6, 1.4, 0.12);
                           break;
                        case "magicbag":
                           var4.openInventory(var4.getEnderChest());
                           break;
                        case "craftitem":
                           var4.openWorkbench(var4.getLocation(), true);
                           break;
                        case "repairitem":
                           ItemStack var13 = var4.getInventory().getItem(0);
                           if (var13 != null && var13.getItemMeta() instanceof Damageable var14 && var14.getDamage() > 0) {
                              int var26 = Math.max(1, (int)Math.ceil(var14.getDamage() * (var23 / 5.0)));
                              var14.setDamage(Math.max(0, var14.getDamage() - var26));
                              var13.setItemMeta(var14);
                              var4.sendMessage(Component.text("El objeto del primer espacio de tu hotbar fue reparado.", NamedTextColor.GREEN));
                           } else {
                              var4.sendMessage(Component.text("El primer espacio de tu hotbar no contiene un objeto dañado reparable.", NamedTextColor.GRAY));
                           }
                     }

                     if (var20 != var4 && (var3.equals("haste") || var3.equals("invisibility"))) {
                        String var11 = var3.equals("haste") ? "Haste " + var23 : "Invisibilidad";
                        var4.sendMessage(Component.text("Le diste " + var11 + " a " + var20.getName() + ".", NamedTextColor.GREEN));
                        var20.sendMessage(Component.text("Has recibido " + var11 + " de " + var4.getName() + ".", NamedTextColor.AQUA));
                     }

                     var4.playSound(var4.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0F, 1.2F);
                     var4.sendActionBar(Component.text(this.titleForRare(var3) + " activada", NamedTextColor.LIGHT_PURPLE));
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void onPrepareRareCraft(PrepareItemCraftEvent var1) {
      if (var1.getRecipe() instanceof Keyed var2 && var2.getKey().getKey().startsWith("rare_essence_attach")) {
         if (var1.getInventory().getResult() != null) {
            String var23 = var2.getKey().getKey();
            int var4 = var23.endsWith("_3") ? 3 : (var23.endsWith("_2") ? 2 : 1);
            ItemStack[] var5 = var1.getInventory().getMatrix();
            ArrayList var6 = new ArrayList();
            ItemStack var7 = null;
            String var8 = null;
            int var9 = 0;

            for (int var10 = 0; var10 < var5.length; var10++) {
               ItemStack var11 = var5[var10];
               if (var11 != null && !var11.getType().isAir()) {
                  String var12 = var11.hasItemMeta()
                     ? (String)var11.getItemMeta().getPersistentDataContainer().get(this.rareBookKey, PersistentDataType.STRING)
                     : null;
                  if (var12 != null) {
                     if (var8 != null && !var8.equals(var12)) {
                        var1.getInventory().setResult(null);
                        return;
                     }

                     var8 = var12;
                     var6.add(var11);
                     var9 += var11.getItemMeta().getPersistentDataContainer().getOrDefault(this.rareEssenceLevelKey, PersistentDataType.INTEGER, 1);
                  } else {
                     if (var7 != null) {
                        var1.getInventory().setResult(null);
                        return;
                     }

                     var7 = var11;
                  }
               }
            }

            if (var7 != null && var6.size() == var4 && var8 != null && !this.spellIds().contains(var8) && !var7.getType().isAir()) {
               boolean var24 = var7.getType().name().endsWith("HELMET")
                  || var7.getType().name().endsWith("CHESTPLATE")
                  || var7.getType().name().endsWith("LEGGINGS")
                  || var7.getType().name().endsWith("BOOTS");
               boolean var25 = var7.getType().name().endsWith("SWORD")
                  || var7.getType().name().endsWith("AXE")
                  || var7.getType() == Material.BOW
                  || var7.getType() == Material.CROSSBOW
                  || var7.getType() == Material.TRIDENT;
               boolean var26 = var7.getType().name().endsWith("PICKAXE") || var7.getType().name().endsWith("SHOVEL") || var7.getType().name().endsWith("HOE");
               boolean var13 = List.of("fertilize", "meltobsidian", "smelt", "spore", "veinminer", "treefeller", "magneto").contains(var8);
               if (var8.equals("flame")) {
                  if (!var7.getType().name().endsWith("HELMET")) {
                     var1.getInventory().setResult(null);
                     return;
                  }
               } else if (!var8.equals("guardian")
                  && !var8.equals("paso_viento")
                  && !var8.equals("hardy")
                  && !var8.equals("durability")
                  && !var8.equals("regeneration")
                  && !var8.equals("nofalldamage")
                  && !var8.equals("doublejump")
                  && !var8.equals("fly")
                  && !var8.equals("saturacion")) {
                  if (var13 ? !var26 : !var25) {
                     var1.getInventory().setResult(null);
                     return;
                  }
               } else {
                  boolean var14 = var8.equals("hardy");
                  if (!var24 || var14 && !var7.getType().name().endsWith("CHESTPLATE")) {
                     var1.getInventory().setResult(null);
                     return;
                  }
               }

               if (var8.equals("magneto") && !var7.getType().name().endsWith("PICKAXE")) {
                  var1.getInventory().setResult(null);
               } else {
                  ItemStack var27 = var7.clone();
                  ItemMeta var15 = var27.getItemMeta();
                  String var16 = (String)var15.getPersistentDataContainer().get(this.rareEnchantKey, PersistentDataType.STRING);
                  Map<String, Integer> var17 = this.parseLevels((String)var15.getPersistentDataContainer().get(this.rareLevelsKey, PersistentDataType.STRING));
                  int var18 = this.maxLevel(var8);
                  int var19 = Math.min(var18, var17.getOrDefault(var8, 0) + var9);
                  if (var17.getOrDefault(var8, 0) >= var18) {
                     var1.getInventory().setResult(null);
                  } else {
                     var17.put(var8, var19);
                     LinkedHashSet var20 = new LinkedHashSet();
                     if (var16 != null && !var16.isBlank()) {
                        var20.addAll(List.of(var16.split(",")));
                     }

                     var20.add(var8);
                     var15.getPersistentDataContainer().set(this.rareEnchantKey, PersistentDataType.STRING, String.join(",", var20));
                     var15.getPersistentDataContainer().set(this.rareLevelsKey, PersistentDataType.STRING, this.serializeLevels(var17));
                     ArrayList<Component> var21 = var15.lore() == null ? new ArrayList<>() : new ArrayList<>(var15.lore());
                     String var22 = var8.replace('_', ' ').toUpperCase();
                     var21.removeIf(var1x -> PlainTextComponentSerializer.plainText().serialize(var1x).startsWith("Rare: " + var22));
                     var21.add(Component.text("Rare: " + var22 + " • Nivel " + var19 + "/" + var18, NamedTextColor.GRAY).decorate(TextDecoration.BOLD));
                     var15.lore(var21);
                     var27.setItemMeta(var15);
                     var1.getInventory().setResult(var27);
                  }
               }
            } else {
               var1.getInventory().setResult(null);
            }
         }
      }
   }

   private Map<String, Integer> parseLevels(String var1) {
      LinkedHashMap var2 = new LinkedHashMap();
      if (var1 != null) {
         for (String var6 : var1.split(",")) {
            String[] var7 = var6.split(":");
            if (var7.length == 2) {
               try {
                  var2.put(var7[0], Integer.parseInt(var7[1]));
               } catch (NumberFormatException var9) {
               }
            }
         }
      }

      return var2;
   }

   private String serializeLevels(Map<String, Integer> var1) {
      ArrayList var2 = new ArrayList();
      var1.forEach((var1x, var2x) -> var2.add(var1x + ":" + var2x));
      return String.join(",", var2);
   }

   private int propertyLevel(ItemStack var1, String var2) {
      if (var1 != null && var1.hasItemMeta()) {
         String var3 = (String)var1.getItemMeta().getPersistentDataContainer().get(this.rareLevelsKey, PersistentDataType.STRING);
         return this.parseLevels(var3).getOrDefault(var2, 1);
      } else {
         return 1;
      }
   }

   private float saturationCost(ItemStack var1, String var2) {
      double var3 = this.rareConfig.getDouble(var2 + ".useCost", 2.0);
      if (var3 < 0.0) {
         return -1.0F;
      } else {
         int var5 = var1 != null && var1.hasItemMeta()
            ? (Integer)var1.getItemMeta()
               .getPersistentDataContainer()
               .getOrDefault(this.rareEssenceLevelKey, PersistentDataType.INTEGER, this.propertyLevel(var1, var2))
            : 1;
         int var6 = Math.max(1, Math.min(5, var5));
         return (float)(var3 / (var6 * var6));
      }
   }

   private boolean consumeSaturation(Player var1, ItemStack var2, String var3) {
      float var4 = this.saturationCost(var2, var3);
      if (!(var4 < 0.0F) && !(var1.getSaturation() < var4)) {
         var1.setSaturation(Math.max(0.0F, var1.getSaturation() - var4));
         return true;
      } else {
         return false;
      }
   }

   private boolean consumePassiveSaturation(Player var1, ItemStack var2, String var3) {
      String var4 = var1.getUniqueId() + ":" + var3;
      long var5 = System.currentTimeMillis();
      if (var5 - this.passiveSaturation.getOrDefault(var4, 0L) < 5000L) {
         return true;
      } else if (!this.consumeSaturation(var1, var2, var3)) {
         return false;
      } else {
         this.passiveSaturation.put(var4, var5);
         return true;
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void onRareHit(EntityDamageByEntityEvent var1) {
      Player var2 = var1.getDamager() instanceof Player var5
         ? var5
         : (var1.getDamager() instanceof Projectile var4 && var4.getShooter() instanceof Player var3 ? var3 : null);
      if (var2 != null) {
         ItemStack var14 = var2.getInventory().getItemInMainHand();
         if (var14.hasItemMeta()) {
            String var15 = (String)var14.getItemMeta().getPersistentDataContainer().get(this.rareEnchantKey, PersistentDataType.STRING);
            if (var15 != null) {
               Map<String, Integer> var16 = this.parseLevels((String)var14.getItemMeta().getPersistentDataContainer().get(this.rareLevelsKey, PersistentDataType.STRING));
               if (var15.contains("vampirismo") || var15.contains("vampiricregeneration")) {
                  int var19 = Math.max(var16.getOrDefault("vampirismo", 1), var16.getOrDefault("vampiricregeneration", 1));
                  if (Math.random() < 0.1 + var19 * 0.03
                     && this.consumeSaturation(var2, var14, var15.contains("vampirismo") ? "vampirismo" : "vampiricregeneration")) {
                     var2.setHealth(Math.min(var2.getMaxHealth(), var2.getHealth() + 1.0 + var19));
                  }
               }

               if (var1.getEntity() instanceof LivingEntity var20) {
                  int var21 = Math.max(var16.getOrDefault("veneno", 1), var16.getOrDefault("poison", 1));
                  int var8 = var16.getOrDefault("slow", 1);
                  int var9 = var16.getOrDefault("weaken", 1);
                  if ((var15.contains("veneno") || var15.contains("poison"))
                     && Math.random() < 0.1 + var21 * 0.05
                     && this.consumeSaturation(var2, var14, var15.contains("veneno") ? "veneno" : "poison")) {
                     var20.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 40 + var21 * 24, Math.min(2, (var21 - 1) / 2)));
                  }

                  if (var15.contains("brasas")) {
                     int var10 = var16.getOrDefault("brasas", 1);
                     if (this.consumeSaturation(var2, var14, "brasas")) {
                        var20.setFireTicks(Math.max(var20.getFireTicks(), 40 + var10 * 24));
                     }
                  }

                  if (var15.contains("slow") && Math.random() < 0.1 + var8 * 0.05 && this.consumeSaturation(var2, var14, "slow")) {
                     var20.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40 + var8 * 20, Math.min(2, (var8 - 1) / 2)));
                  }

                  if (var15.contains("weaken") && Math.random() < 0.1 + var9 * 0.05 && this.consumeSaturation(var2, var14, "weaken")) {
                     var20.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 40 + var9 * 20, Math.min(2, (var9 - 1) / 2)));
                  }

                  if (var15.contains("blinding")) {
                     int var22 = var16.getOrDefault("blinding", 1);
                     if (Math.random() < 0.05 + var22 * 0.03 && this.consumeSaturation(var2, var14, "blinding")) {
                        var20.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 25 + var22 * 12, 0));
                     }
                  }

                  if (var15.contains("confuse")) {
                     int var23 = var16.getOrDefault("confuse", 1);
                     if (Math.random() < 0.08 + var23 * 0.04 && this.consumeSaturation(var2, var14, "confuse")) {
                        var20.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 35 + var23 * 18, 0));
                     }
                  }

                  if (var15.contains("calllighting")) {
                     int var24 = var16.getOrDefault("calllighting", 1);
                     if (Math.random() < 0.04 + var24 * 0.025 && this.consumeSaturation(var2, var14, "calllighting")) {
                        var20.getWorld().strikeLightningEffect(var20.getLocation());
                     }
                  }

                  if (var15.contains("bleed")) {
                     int var25 = var16.getOrDefault("bleed", 1);
                     if (Math.random() < 0.1 + var25 * 0.04 && this.consumeSaturation(var2, var14, "bleed")) {
                        var20.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 35 + var25 * 18, Math.min(1, (var25 - 1) / 3)));
                     }
                  }

                  if (var15.contains("execute") && var20.getHealth() / var20.getMaxHealth() < 0.25 && this.consumeSaturation(var2, var14, "execute")) {
                     SafeDamage.setBase(var1,var1.getDamage() * (1.2 + var16.getOrDefault("execute", 1).intValue() * 0.1));
                  }

                  if (var15.contains("backstab")) {
                     Vector var26 = var2.getLocation().toVector().subtract(var20.getLocation().toVector()).setY(0);
                     Vector var11 = var20.getLocation().getDirection().setY(0);
                     if (var26.lengthSquared() > 0.001
                        && var11.lengthSquared() > 0.001
                        && var11.normalize().dot(var26.normalize()) < -0.05
                        && this.consumeSaturation(var2, var14, "backstab")) {
                        int var12 = var16.getOrDefault("backstab", 1);
                        SafeDamage.setBase(var1,var1.getDamage() * (1.2 + var12 * 0.12));
                        var20.getWorld().spawnParticle(Particle.CRIT, var20.getLocation().add(0.0, 1.0, 0.0), 12, 0.35, 0.4, 0.35, 0.1);
                        var2.playSound(var20.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 0.8F, 1.3F);
                        var2.sendActionBar(Component.text("Backstab activado • daño aumentado", NamedTextColor.RED));
                     }
                  }

                  if (var15.contains("disarm")) {
                     int var27 = var16.getOrDefault("disarm", 1);
                     if (Math.random() < 0.08 + var27 * 0.06 && this.consumeSaturation(var2, var14, "disarm")) {
                        if (var20 instanceof Player var28) {
                           this.scrambleInventory(var28, var27);
                           var28.sendMessage(Component.text("Disarm revuelve parte de tu inventario.", NamedTextColor.RED));
                           var2.sendMessage(Component.text("Disarm activado contra " + var28.getName() + ".", NamedTextColor.GOLD));
                        } else if (var20 instanceof Mob var29 && var29.getEquipment() != null) {
                           ItemStack var13 = var29.getEquipment().getItemInMainHand();
                           if (var13 != null && !var13.getType().isAir()) {
                              if(!BossEquipment.isDisplay(var13))var20.getWorld().dropItemNaturally(var20.getLocation(),var13.clone());
                              else {ItemStack restore=var13.clone();plugin.getServer().getScheduler().runTaskLater(plugin,()->{if(var29.isValid()&&!var29.isDead()&&var29.getEquipment()!=null&&var29.getEquipment().getItemInMainHand().getType().isAir())var29.getEquipment().setItemInMainHand(restore);},60L);}
                              var29.getEquipment().setItemInMainHand(new ItemStack(Material.AIR));
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void scrambleInventory(Player var1, int var2) {
      PlayerInventory var3 = var1.getInventory();
      ArrayList<Integer> var4 = new ArrayList<>();

      for (int var5 = 0; var5 < 36; var5++) {
         var4.add(var5);
      }

      Collections.shuffle(var4, (Random)ThreadLocalRandom.current());
      int var9 = Math.min(var4.size(), 6 * Math.max(1, Math.min(5, var2)));
      ArrayList var6 = new ArrayList();

      for (int var7 = 0; var7 < var9; var7++) {
         ItemStack var8 = var3.getItem((Integer)var4.get(var7));
         var6.add(var8 == null ? null : var8.clone());
      }

      Collections.shuffle(var6, (Random)ThreadLocalRandom.current());

      for (int var10 = 0; var10 < var9; var10++) {
         var3.setItem((Integer)var4.get(var10), (ItemStack)var6.get(var10));
      }

      var1.getWorld().spawnParticle(Particle.WITCH, var1.getLocation().add(0.0, 1.0, 0.0), 24, 0.5, 0.7, 0.5, 0.08);
      var1.playSound(var1.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 0.8F, 0.8F);
   }

   @EventHandler(ignoreCancelled = true)
   public void onToolRare(BlockBreakEvent var1) {
      if (breakingConnected) return;
      ItemStack var2 = var1.getPlayer().getInventory().getItemInMainHand();
      if (var2.hasItemMeta()) {
         String var3 = (String)var2.getItemMeta().getPersistentDataContainer().get(this.rareEnchantKey, PersistentDataType.STRING);
         if (var3 != null) {
            Player var4 = var1.getPlayer();
            Material var5 = var1.getBlock().getType();
            if (var3.contains("meltobsidian") && var5 == Material.LAVA && this.consumeSaturation(var4, var2, "meltobsidian")) {
               var1.getBlock().setType(Material.OBSIDIAN);
               var1.setDropItems(false);
            } else {
               if (var3.contains("fertilize") && var5 == Material.DIRT && this.consumeSaturation(var4, var2, "fertilize")) {
                  var1.getBlock().setType(Material.GRASS_BLOCK);
               }

               if (var3.contains("spore") && var5 == Material.COBBLESTONE && this.consumeSaturation(var4, var2, "spore")) {
                  var1.getBlock().setType(Material.MOSSY_COBBLESTONE);
               }

               if (var3.contains("smelt")) {
                  Material var6 = switch (var1.getBlock().getType()) {
                     case IRON_ORE, DEEPSLATE_IRON_ORE -> Material.IRON_INGOT;
                     case GOLD_ORE, DEEPSLATE_GOLD_ORE, NETHER_GOLD_ORE -> Material.GOLD_INGOT;
                     case COPPER_ORE, DEEPSLATE_COPPER_ORE -> Material.COPPER_INGOT;
                     default -> null;
                  };
                  if (var6 != null && this.consumeSaturation(var4, var2, "smelt")) {
                     var1.setDropItems(false);
                     var1.getBlock().getWorld().dropItemNaturally(var1.getBlock().getLocation(), new ItemStack(var6));
                  }
               }

               if (var3.contains("treefeller") && Tag.LOGS.isTagged(var5) && this.consumeSaturation(var4, var2, "treefeller")) {
                  this.breakConnected(var1, var2, true);
               }

               if (var3.contains("veinminer") && this.isOre(var5) && this.consumeSaturation(var4, var2, "veinminer")) {
                  this.breakConnected(var1, var2, false);
               }
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
   public void onMagnetoDrops(BlockDropItemEvent var1) {
      Player var2 = var1.getPlayer();
      ItemStack var3 = var2.getInventory().getItemInMainHand();
      if (var3 != null && var3.getType().name().endsWith("PICKAXE") && var3.hasItemMeta()) {
         String var4 = (String)var3.getItemMeta().getPersistentDataContainer().get(this.rareEnchantKey, PersistentDataType.STRING);
         if (var4 != null && Arrays.asList(var4.split(",")).contains("magneto")) {
            var1.setCancelled(true);

            for (Item var6 : new ArrayList<Item>(var1.getItems())) {
               HashMap<Integer, ItemStack> var7 = var2.getInventory().addItem(new ItemStack[]{var6.getItemStack().clone()});

               for (ItemStack var9 : var7.values()) {
                  var2.getWorld().dropItemNaturally(var6.getLocation(), var9);
               }

               var6.remove();
            }

            var2.playSound(var2.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 1.5F);
         }
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void onToolRightClick(PlayerInteractEvent var1) {
      if (var1.getAction() == Action.RIGHT_CLICK_BLOCK && var1.getClickedBlock() != null && var1.getItem() != null && var1.getItem().hasItemMeta()) {
         String var2 = (String)var1.getItem().getItemMeta().getPersistentDataContainer().get(this.rareEnchantKey, PersistentDataType.STRING);
         if (var2 != null) {
            Material var3 = var1.getClickedBlock().getType();
            Material var4 = null;
            if (var2.contains("fertilize")
               && (var3 == Material.DIRT || var3 == Material.COARSE_DIRT || var3 == Material.ROOTED_DIRT)
               && this.consumeSaturation(var1.getPlayer(), var1.getItem(), "fertilize")) {
               var4 = Material.GRASS_BLOCK;
            }

            if (var2.contains("spore")
               && (var3 == Material.COBBLESTONE || var3 == Material.STONE)
               && this.consumeSaturation(var1.getPlayer(), var1.getItem(), "spore")) {
               var4 = var3 == Material.COBBLESTONE ? Material.MOSSY_COBBLESTONE : Material.MOSSY_STONE_BRICKS;
            }

            if (var2.contains("meltobsidian") && var3 == Material.LAVA && this.consumeSaturation(var1.getPlayer(), var1.getItem(), "meltobsidian")) {
               var4 = Material.OBSIDIAN;
            }

            if (var4 != null) {
               var1.setCancelled(true);
               var1.getClickedBlock().setType(var4);
               var1.getPlayer().getWorld().spawnParticle(Particle.WITCH, var1.getClickedBlock().getLocation().add(0.5, 0.8, 0.5), 12, 0.3, 0.3, 0.3, 0.03);
            }
         }
      }
   }

   private boolean isOre(Material var1) {
      return var1.name().endsWith("_ORE") || var1 == Material.ANCIENT_DEBRIS;
   }

   private void breakConnected(BlockBreakEvent var1, ItemStack var2, boolean var3) {
      if (breakingConnected) return;
      ArrayDeque var4 = new ArrayDeque();
      HashSet var5 = new HashSet();
      var4.add(var1.getBlock());
      var5.add(var1.getBlock().getX() + ":" + var1.getBlock().getY() + ":" + var1.getBlock().getZ());
      int var6 = 0;

      while (!var4.isEmpty() && var6 < 64) {
         Block var7 = (Block)var4.removeFirst();

         for (int var8 = -1; var8 <= 1; var8++) {
            for (int var9 = -1; var9 <= 1; var9++) {
               for (int var10 = -1; var10 <= 1; var10++) {
                  if (var8 != 0 || var9 != 0 || var10 != 0) {
                     Block var11 = var7.getRelative(var8, var9, var10);
                     String var12 = var11.getX() + ":" + var11.getY() + ":" + var11.getZ();
                     if (var5.add(var12)) {
                        boolean var13 = var3 ? Tag.LOGS.isTagged(var11.getType()) : this.isOre(var11.getType());
                        if (var13 && var6 < 64) {
                           breakingConnected = true;
                           try {
                              if (var1.getPlayer().breakBlock(var11)) { var4.add(var11); var6++; }
                           } finally { breakingConnected = false; }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void onRareDefense(EntityDamageEvent var1) {
      if (var1.getEntity() instanceof Player var2) {
         for (ItemStack var6 : var2.getInventory().getArmorContents()) {
            if (var6 != null && var6.hasItemMeta()) {
               String var7 = (String)var6.getItemMeta().getPersistentDataContainer().get(this.rareEnchantKey, PersistentDataType.STRING);
               if (var7 != null && var7.contains("guardian") && this.consumeSaturation(var2, var6, "guardian")) {
                  SafeDamage.setBase(var1,var1.getDamage() * 0.82);
                  return;
               }

               if (var7 != null && var7.contains("nofalldamage") && var1.getCause() == DamageCause.FALL && this.consumeSaturation(var2, var6, "nofalldamage")) {
                  SafeDamage.setBase(var1,var1.getDamage() * 0.35);
               }
            }
         }
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void onRareDoubleJump(PlayerToggleFlightEvent var1) {
      Player var2 = var1.getPlayer();
      if (var2.getGameMode() != GameMode.CREATIVE && var2.getGameMode() != GameMode.SPECTATOR) {
         ItemStack var3 = var2.getInventory().getBoots();
         if (var3 != null && var3.hasItemMeta()) {
            String var4 = (String)var3.getItemMeta().getPersistentDataContainer().get(this.rareEnchantKey, PersistentDataType.STRING);
            if (var4 != null && var4.contains("doublejump")) {
               var1.setCancelled(true);
               var2.setFlying(false);
               var2.setAllowFlight(false);
               if (!this.consumeSaturation(var2, var3, "doublejump")) {
                  var2.sendMessage(Component.text("El salto rare está desactivado o te falta saturación.", NamedTextColor.RED));
               } else {
                  var2.setVelocity(var2.getLocation().getDirection().multiply(0.75).setY(0.75));
                  this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                     if (var2.isOnline() && var2.isOnGround()) {
                        var2.setAllowFlight(true);
                     }
                  }, 10L);
               }
            }
         }
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void onWindStep(PlayerMoveEvent var1) {
      if (var1.getTo() != null
         && (
            var1.getFrom().getBlockX() != var1.getTo().getBlockX()
               || var1.getFrom().getBlockY() != var1.getTo().getBlockY()
               || var1.getFrom().getBlockZ() != var1.getTo().getBlockZ()
         )) {
         Player var2 = var1.getPlayer();
         ItemStack var3 = var2.getInventory().getBoots();
         String var4 = var3 != null && var3.hasItemMeta()
            ? (String)var3.getItemMeta().getPersistentDataContainer().get(this.rareEnchantKey, PersistentDataType.STRING)
            : null;
         if (var4 != null
            && var4.contains("doublejump")
            && var2.isOnGround()
            && var2.getGameMode() != GameMode.CREATIVE
            && var2.getGameMode() != GameMode.SPECTATOR) {
            var2.setAllowFlight(true);
         }

         boolean var5 = false;
         int var6 = 1;

         for (ItemStack var10 : var2.getInventory().getArmorContents()) {
            if (var10 != null && var10.hasItemMeta()) {
               String var11 = (String)var10.getItemMeta().getPersistentDataContainer().get(this.rareEnchantKey, PersistentDataType.STRING);
               if (var11 != null && var11.contains("fly")) {
                  var5 = true;
                  var6 = this.propertyLevel(var10, "fly");
               }
            }
         }

         if (var2.getGameMode() != GameMode.CREATIVE && var2.getGameMode() != GameMode.SPECTATOR) {
            if (var5) {
               var2.setAllowFlight(true);
               this.flyUsers.add(var2.getUniqueId());
               if (var2.isFlying() && System.currentTimeMillis() - this.flyDrain.getOrDefault(var2.getUniqueId(), 0L) > 10000L) {
                  ItemStack var19 = var2.getInventory().getChestplate();
                  if (var19 != null && this.consumeSaturation(var2, var19, "fly")) {
                     this.flyDrain.put(var2.getUniqueId(), System.currentTimeMillis());
                  } else {
                     var2.setFlying(false);
                     var2.setAllowFlight(false);
                     var2.sendMessage(Component.text("La rare de vuelo está desactivada o se quedó sin saturación.", NamedTextColor.RED));
                  }
               }
            } else if (this.flyUsers.remove(var2.getUniqueId())) {
               var2.setFlying(false);
               var2.setAllowFlight(false);
            }
         }

         for (ItemStack var25 : var1.getPlayer().getInventory().getArmorContents()) {
            if (var25 != null && var25.hasItemMeta()) {
               String var26 = (String)var25.getItemMeta().getPersistentDataContainer().get(this.rareEnchantKey, PersistentDataType.STRING);
               if (var26 != null && var26.contains("paso_viento") && this.consumePassiveSaturation(var1.getPlayer(), var25, "paso_viento")) {
                  var1.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30, 0, true, false, true));
               }

               if (var26 != null && var26.contains("regeneration") && this.consumePassiveSaturation(var1.getPlayer(), var25, "regeneration")) {
                  var1.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 40, 0, true, false, true));
               }




            }
         }

         ItemStack var21 = var1.getPlayer().getInventory().getHelmet();
         if (var21 != null && var21.hasItemMeta()) {
            String var23 = (String)var21.getItemMeta().getPersistentDataContainer().get(this.rareEnchantKey, PersistentDataType.STRING);
            if (var23 != null && var23.contains("flame") && this.consumePassiveSaturation(var1.getPlayer(), var21, "flame")) {
               var1.getPlayer().getWorld().spawnParticle(Particle.FLAME, var1.getPlayer().getLocation().add(0.0, 1.7, 0.0), 2, 0.18, 0.12, 0.18, 0.01);
            }
         }
      }
   }

   public ItemStack essence() {
      return this.plainGreenItem(
         Material.MAGMA_CREAM, "rare_essence", "Rare Essence", "Energía concentrada de la Grieta.", "Sirve para recetas y forjas de reliquias."
      );
   }

   public ItemStack primordialEssence() {
      return this.plainGreenItem(
         Material.MAGMA_CREAM,
         "rare_essence_primordial",
         "Esencia Primordial de la Grieta",
         "Rare Essence refinada con Polvo de la Grieta y Núcleos de jefe.",
         "Catalizador para recetas y forjas de alto rango."
      );
   }

   public ItemStack abyssalEssence() {
      return this.named(
         Material.DRAGON_BREATH,
         "rare_essence_abyssal",
         "Esencia Abisal",
         NamedTextColor.DARK_PURPLE,
         1,
         "Energía condensada de los jefes más poderosos.",
         "Requisito de forja para reliquias míticas.",
         "Se obtiene de jefes legendarios y míticos."
      );
   }

   public ItemStack abyssalEssenceFromBoss() {
      ItemStack var1 = this.abyssalEssence();
      this.addRiftDropClassification(var1, BossCatalog.Tier.MYTHIC);
      return var1;
   }

   private ItemStack plainGreenItem(Material var1, String var2, String var3, String... var4) {
      ItemStack var5 = this.named(var1, var2, var3, NamedTextColor.GREEN, 1, var4);
      ItemMeta var6 = var5.getItemMeta();
      var6.displayName(((TextComponent)Component.text(var3, NamedTextColor.GREEN).decorate(TextDecoration.BOLD)).decoration(TextDecoration.ITALIC, false));
      ArrayList var7 = new ArrayList<>(
         Arrays.stream(var4).map(var0 -> (TextComponent)Component.text(var0, NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)).toList()
      );
      var6.lore(var7);
      var5.setItemMeta(var6);
      return var5;
   }

   public ItemStack fragment(int var1) {
      return this.named(
         Material.DISC_FRAGMENT_5,
         "fragmento_grieta",
         "Fragmento de Tributo",
         NamedTextColor.LIGHT_PURPLE,
         var1,
         "Energía antigua cristalizada.",
         "Se utiliza en canjes y crafteos de reliquias."
      );
   }

   public ItemStack tribute(String var1) {
      return this.named(
         Material.EMERALD,
         "tributo_grieta",
         "Tributo del Cazador",
         NamedTextColor.AQUA,
         1,
         "Marca de una cacería victoriosa.",
         "Sirve para canjes y forjas especiales."
      );
   }

   public ItemStack compass() {
      ItemStack var1 = this.named(
         Material.COMPASS,
         "tracking_compass",
         "Brújula de Caza",
         NamedTextColor.AQUA,
         1,
         "Sigue el rastro del jefe más cercano.",
         "Actívala al seleccionarla en la hotbar."
      );
      // Visual glint only; no artificial Unbreaking level.
      return var1;
   }

   public ItemStack colossusSigil() {
      return named(Material.NETHER_STAR,"sello_coloso","✦ Sello del Renacido ✦",NamedTextColor.GOLD,1,
         "Divisa especial del Santuario del Renacer.","Cae cuando un boss activa Suspiro del Renacido.","Reúne sellos para conseguir el Amuleto del Fénix Astral.");
   }
   public String propertyId(ItemStack stack) {return stack!=null&&stack.hasItemMeta()?stack.getItemMeta().getPersistentDataContainer().get(rareBookKey,PersistentDataType.STRING):null;}

   public ItemStack amulet() {
      return this.named(
         Material.TOTEM_OF_UNDYING,
         "amuleto_coloso",
         "✦ Amuleto del Fénix Astral ✦",
         NamedTextColor.GOLD,
         1,
         "RELIQUIA DE RENACER · Fénix Astral",
         "Mantén el amuleto en una mano para usarlo como tótem.",
         "Se obtiene en el Santuario del Mercado de la Grieta."
      );
   }

   public ItemStack rare(String var1) {
      return this.rare(var1, 1);
   }

   public ItemStack rare(String var1, int var2) {
      var2 = Math.max(1, Math.min(5, var2));
      ItemStack var3;
      switch (var1) {
         case "hacha_sismica":
            var3 = this.named(
               Material.NETHERITE_AXE,
               var1,
               "Hacha Sísmica",
               NamedTextColor.RED,
               1,
               "Su golpe despierta la piedra.",
               "Arma rare de la Grieta con efecto Execute."
            );
            this.enchant(var3, Enchantment.EFFICIENCY, 5);
            this.enchant(var3, Enchantment.UNBREAKING, 4);
            this.enchant(var3, Enchantment.SHARPNESS, 5);
            break;
         case "espada_brezal":
            var3 = this.named(
               Material.NETHERITE_SWORD,
               var1,
               "Espada del Brezal",
               NamedTextColor.GREEN,
               1,
               "Un filo envuelto en enredaderas.",
               "Arma rare de la Grieta con efecto Veneno."
            );
            this.enchant(var3, Enchantment.SHARPNESS, 5);
            this.enchant(var3, Enchantment.FIRE_ASPECT, 2);
            this.enchant(var3, Enchantment.UNBREAKING, 4);
            break;
         case "lanza_magma":
            var3 = this.named(
               Material.TRIDENT, var1, "Lanza de Magma", NamedTextColor.GOLD, 1, "Atraviesa la tormenta ardiente.", "Arma rare de la Grieta con efecto Brasas."
            );
            this.enchant(var3, Enchantment.IMPALING, 5);
            this.enchant(var3, Enchantment.LOYALTY, 3);
            this.enchant(var3, Enchantment.UNBREAKING, 4);
            break;
         case "mandoble_vacio":
            var3 = this.named(
               Material.NETHERITE_SWORD,
               var1,
               "Mandoble del Vacío",
               NamedTextColor.DARK_PURPLE,
               1,
               "Un filo que devora la luz.",
               "Arma rare mítica de la Grieta con efecto Hemorragia."
            );
            this.enchant(var3, Enchantment.SHARPNESS, 7);
            this.enchant(var3, Enchantment.FIRE_ASPECT, 2);
            this.enchant(var3, Enchantment.UNBREAKING, 5);
            this.enchant(var3, Enchantment.MENDING, 1);
            break;
         default:
            var3 = this.named(
               Material.BOW,
               "arco_ceniciento",
               "Arco Ceniciento",
               NamedTextColor.GOLD,
               1,
               "Cada flecha dibuja una brasa.",
               "Arma rare de la Grieta con efecto Rayo."
            );
            this.enchant(var3, Enchantment.POWER, 5);
            this.enchant(var3, Enchantment.FLAME, 1);
            this.enchant(var3, Enchantment.UNBREAKING, 3);
      }

      if(var3.getType().name().endsWith("SWORD")||var3.getType().name().endsWith("AXE"))var3.addUnsafeEnchantment(Enchantment.SHARPNESS,EquipmentSets.sharpness(RecipeAlchemy.profile(var1).rank()));
      String var4 = this.rareTier(var1);
      if (var4 != null) {
         ItemMeta var11 = var3.getItemMeta();
         var11.displayName(Component.text("✦ " + var4 + " · ",NamedTextColor.GOLD).append(var11.displayName()==null?Component.text(var1):var11.displayName()).decoration(TextDecoration.ITALIC,false));
         ArrayList var6 = var11.lore() == null ? new ArrayList() : new ArrayList(var11.lore());
         var6.add(Component.text("Rare especial de la Grieta", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD));

         String var7 = switch (var1) {
            case "espada_brezal" -> "veneno";
            case "hacha_sismica" -> "execute";
            case "arco_ceniciento" -> "calllighting";
            case "lanza_magma" -> "brasas";
            default -> "bleed";
         };
         var11.getPersistentDataContainer().set(this.rareEnchantKey, PersistentDataType.STRING, var7);
         var11.getPersistentDataContainer().set(this.rareLevelsKey, PersistentDataType.STRING, var7 + ":" + var2);
         var6.add(Component.text("Rare " + var7 + " • Nivel " + var2, NamedTextColor.GREEN));
         var11.lore(var6);
         var3.setItemMeta(var11);
      }

      return var3;
   }

   public List<String> rareIds() {
      return List.of("espada_brezal", "hacha_sismica", "arco_ceniciento", "lanza_magma", "mandoble_vacio");
   }

   public String rareTier(String id){return id!=null&&rareIds().contains(id)?RecipeAlchemy.profile(id).rank().display.toUpperCase(Locale.ROOT):null;}

   public boolean isTracker(ItemStack var1) {
      return "tracking_compass".equals(this.customId(var1));
   }

   public String customId(ItemStack var1) {
      return var1 != null && var1.hasItemMeta() ? (String)var1.getItemMeta().getPersistentDataContainer().get(this.itemKey, PersistentDataType.STRING) : null;
   }

   public boolean hasPrefix(Player var1, String var2, int var3) {
      int var4 = 0;

      for (ItemStack var8 : var1.getInventory().getStorageContents()) {
         if (this.customId(var8) != null && this.customId(var8).startsWith(var2)) {
            var4 += var8.getAmount();
         }
      }

      return var4 >= var3;
   }

   public void consumePrefix(Player var1, String var2, int var3) {
      for (int var4 = 0; var4 < var1.getInventory().getStorageContents().length && var3 > 0; var4++) {
         ItemStack var5 = var1.getInventory().getItem(var4);
         if (var5 != null && this.customId(var5) != null && this.customId(var5).startsWith(var2)) {
            int var6 = Math.min(var3, var5.getAmount());
            var3 -= var6;
            var5.setAmount(var5.getAmount() - var6);
            if (var5.getAmount() <= 0) {
               var1.getInventory().setItem(var4, null);
            }
         }
      }
   }

   public void consumeId(Player var1, String var2, int var3) {
      this.consumePrefix(var1, var2, var3);
   }

   public ItemStack craftedArmor(String var1, int var2) {
      BossCatalog.Definition var3 = BossCatalog.get(var1);
      return var3 == null ? null : this.armor(var3, var2);
   }

   public ItemStack craftedWeapon(String var1, int var2) {
      BossCatalog.Definition var3 = BossCatalog.get(var1);
      return var3 == null ? null : this.weaponDrop(var3, var2);
   }

   public ItemStack gear(String var1, boolean var2) {
      String var4 = var1.toLowerCase();

      BossCatalog.Tier var3 = switch (var4) {
         case "comun", "común", "common" -> BossCatalog.Tier.COMMON;
         case "raro", "rare" -> BossCatalog.Tier.RARE;
         case "epico", "épico", "epic" -> BossCatalog.Tier.EPIC;
         case "legendario", "legendary" -> BossCatalog.Tier.LEGENDARY;
         case "mitico", "mítico", "mythic" -> BossCatalog.Tier.MYTHIC;
         case "abismal", "abyssal" -> BossCatalog.Tier.ABYSSAL;
         default -> null;
      };
      if (var3 == null) {
         return null;
      } else {
         BossCatalog.Definition var6 = BossCatalog.all().stream().filter(var1x -> var1x.tier() == var3 && !var1x.mini()).findFirst().orElse(null);
         return var6 == null
            ? null
            : (var2 ? this.armor(var6, ThreadLocalRandom.current().nextInt(4)) : this.weaponDrop(var6, ThreadLocalRandom.current().nextInt(2)));
      }
   }

   public record LootResult(int count,Component summary) {}

   public LootResult dropBossLoot(Location var1, BossCatalog.Definition var2, UUID var3) {
      ArrayList<ItemStack> var4 = new ArrayList<>();
      for (LootRules.Reward reward:LootRules.roll(var2,ThreadLocalRandom.current())) {
         ItemStack stack=switch(reward.kind()) {
            case FRAGMENT -> fragment(reward.amount());
            case TRIBUTE -> tribute("Cazador");
            case CRYSTAL -> crystal(BossCatalog.Tier.values()[reward.variant()],reward.amount());
            case CORE -> component(var2);
            case ARMOR -> armor(var2,reward.variant());
            case WEAPON -> weaponDrop(var2,reward.variant());
            case RARE -> rare(rareForBoss(var2));
            case ESSENCE -> var2.tier().ordinal()>=4 ? primordialEssence() : essence();
            case ABYSSAL_ESSENCE -> abyssalEssence();
            case BOOK -> rareBook(catalogIds().get(reward.variant()%catalogIds().size()),1);
            case MATERIAL -> riftMaterial(reward.variant());
         };
         if (stack!=null) { stack.setAmount(reward.amount()); var4.add(stack); }
      }
      if(LootRules.heartEligible(var2.mini(),Math.random(),plugin.getConfig().getDouble("loot.boss-heart-chance",0.01)))var4.add(bossHeart(var2));
      int var13 = var4.stream().mapToInt(ItemStack::getAmount).sum();
      Component summary=Component.empty();
      for(int i=0;i<var4.size();i++){
         ItemStack stack=var4.get(i);if(i>0)summary=summary.append(Component.newline());
         Component name=stack.getItemMeta().displayName();summary=summary.append(Component.text("  ▸ "+stack.getAmount()+"× ",NamedTextColor.GOLD).decorate(TextDecoration.BOLD)).append(Component.text(name==null?stack.getType().name():PlainTextComponentSerializer.plainText().serialize(name),NamedTextColor.AQUA).decorate(TextDecoration.BOLD));
      }

      Particle var9 = switch (var2.tier()) {
         case COMMON -> Particle.CRIT;
         case RARE -> Particle.WITCH;
         case EPIC -> Particle.ENCHANT;
         case LEGENDARY -> Particle.FLAME;
         case MYTHIC -> Particle.SOUL_FIRE_FLAME;
         case ABYSSAL -> Particle.DRAGON_BREATH;
      };

      for (ItemStack var11 : var4) {
         this.addRiftDropClassification(var11, var2.tier());
         this.dropWithAura(var1, var11, var9, var3);
      }

      var1.getWorld().playSound(var1, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0F, 0.85F);
      var1.getWorld().spawnParticle(Particle.FIREWORK, var1.clone().add(0.0, 1.0, 0.0), 50, 0.9, 0.8, 0.9, 0.12);
      return new LootResult(var13,summary);
   }

   private boolean chance(double var1) {
      return ThreadLocalRandom.current().nextDouble() < var1;
   }

   private void addRiftDropClassification(ItemStack var1, BossCatalog.Tier var2) {
      if (var1 != null && var1.hasItemMeta()) {
         ItemMeta var3 = var1.getItemMeta();
         ArrayList<Component> var4 = var3.lore() == null ? new ArrayList<>() : new ArrayList<>(var3.lore());
         var4.removeIf(var0 -> {
            String var1x = PlainTextComponentSerializer.plainText().serialize(var0);
            return var1x.toLowerCase(Locale.ROOT).contains("clasificación:") || var1x.toLowerCase(Locale.ROOT).contains("clasificacion:");
         });
         var3.lore(var4);
         var1.setItemMeta(var3);
      }
   }

   private ItemStack riftMaterial(int var1) {
      return switch (var1) {
         case 0 -> this.named(
            Material.GLOWSTONE_DUST,
            "material_grieta_polvo",
            "Polvo de la Grieta",
            NamedTextColor.LIGHT_PURPLE,
            1 + ThreadLocalRandom.current().nextInt(3),
            "Residuo de energía dimensional.",
            "Catalizador para crafteos de reliquias."
         );
         case 1 -> this.named(
            Material.PRISMARINE_CRYSTALS,
            "material_grieta_cristal",
            "Cristal de la Grieta",
            NamedTextColor.AQUA,
            1 + ThreadLocalRandom.current().nextInt(2),
            "Cristal cargado con energía antigua.",
            "Componente para forjas y mejoras especiales."
         );
         case 2 -> this.named(
            Material.PHANTOM_MEMBRANE,
            "material_grieta_membrana",
            "Membrana de la Grieta",
            NamedTextColor.DARK_PURPLE,
            1,
            "Membrana flexible de energía oscura.",
            "Ingrediente para equipo y artefactos de movilidad."
         );
         default -> this.named(
            Material.BLAZE_POWDER,
            "material_grieta_ceniza",
            "Ceniza de la Grieta",
            NamedTextColor.GOLD,
            1 + ThreadLocalRandom.current().nextInt(2),
            "Ceniza caliente que conserva su brillo.",
            "Ingrediente para consumibles y forjas de fuego."
         );
      };
   }

   private String rareForBoss(BossCatalog.Definition var1) {
      if (var1.legacyId().equals("coloso")) {
         return "hacha_sismica";
      } else if (var1.legacyId().equals("viuda")) {
         return "espada_brezal";
      } else if (var1.legacyId().equals("centinela")) {
         return "arco_ceniciento";
      } else {
         return switch (var1.tier()) {
            case COMMON, RARE -> "espada_brezal";
            case EPIC -> "hacha_sismica";
            case LEGENDARY -> "lanza_magma";
            case MYTHIC, ABYSSAL -> "mandoble_vacio";
         };
      }
   }

   static String coreName(BossCatalog.Tier tier){return "✦ Núcleo de la Grieta · "+tier.display;}
   private ItemStack component(BossCatalog.Definition var1) {
      return this.named(
         Material.ECHO_SHARD,
         "componente_" + var1.legacyId(),
         coreName(var1.tier()),
         var1.tier().color,
         1,
         "Núcleo de energía condensada.",
         "Componente para armas y armaduras forjadas."
      );
   }

   public ItemStack componentForRecipe() {
      BossCatalog.Definition var1 = BossCatalog.get("coloso");
      ItemStack var2 = this.component(var1);
      this.addRiftDropClassification(var2, var1.tier());
      return var2;
   }

   public ItemStack riftMaterialForRecipe(int var1) {
      ItemStack var2 = this.riftMaterial(var1);
      this.addRiftDropClassification(var2, BossCatalog.Tier.EPIC);
      return var2;
   }

   public ItemStack migrateCurrency(ItemStack stack) {
      String id=customId(stack);if(id==null)return stack;
      ItemStack replacement=null;
      if(id.equals("fragmento_grieta"))replacement=fragment(stack.getAmount());
      else if(id.equals("tributo_grieta"))replacement=tribute("Cazador");
      else if(id.startsWith("componente_")){
         BossCatalog.Definition definition=BossCatalog.get(id.substring("componente_".length()));if(definition!=null)replacement=component(definition);
      }
      if(id.startsWith("armadura_"))return EquipmentSets.migrateArmor(stack,id,itemKey);
      if(id.startsWith("arma_"))return EquipmentSets.migrateWeapon(stack,id,itemKey);
      if(id.equals("tracking_compass"))replacement=compass();
      if(id.equals("sello_coloso"))replacement=colossusSigil();
      if(id.equals("amuleto_coloso"))replacement=amulet();
      if(replacement!=null){replacement.setAmount(stack.getAmount());return replacement;}
      ItemStack glowing=stack.clone();ItemMeta meta=glowing.getItemMeta();meta.setEnchantmentGlintOverride(true);glowing.setItemMeta(meta);return glowing;
   }
   public void migrateCurrencies(Player player) {
      ItemStack[] storage=player.getInventory().getStorageContents();boolean changed=false;
      for(int slot=0;slot<storage.length;slot++){
         ItemStack old=storage[slot],updated=migrateCurrency(old);
         if(old!=null&&!old.equals(updated)){storage[slot]=updated;changed=true;}
      }
      if(changed)player.getInventory().setStorageContents(storage);
   }

   public ItemStack bossHeart(BossCatalog.Definition boss) {
      ItemStack stack=named(Material.HEART_OF_THE_SEA,"corazon_boss_"+boss.legacyId(),"✦ Corazón de "+boss.name(),boss.tier().color,1,
         "RARE · Trofeo "+boss.tier().display,"Solo puede caer de un boss principal.","Un corazón encantado de la Grieta.");
      ItemMeta meta=stack.getItemMeta();meta.getPersistentDataContainer().set(new NamespacedKey(plugin,"boss_rare"),PersistentDataType.BYTE,(byte)1);stack.setItemMeta(meta);return stack;
   }
   private boolean rareLoot(ItemStack stack) {
      if(stack==null||stack.getType().isAir()||!stack.hasItemMeta())return false;
      ItemMeta meta=stack.getItemMeta();
      return LootNotices.isRare(customId(stack),rareIds(),meta.getPersistentDataContainer().has(rareBookKey,PersistentDataType.STRING),meta.getPersistentDataContainer().has(new NamespacedKey(plugin,"boss_rare"),PersistentDataType.BYTE));
   }

   public ItemStack crystal(BossCatalog.Tier tier, int amount) {
      ItemStack stack=named(Material.AMETHYST_SHARD,"cristal_encantado_"+tier.name().toLowerCase(java.util.Locale.ROOT),
         "✦ Cristal Encantado · "+tier.display,tier.color,amount,"Forja de categoría "+tier.display+".","Recurso de progresión de los cazadores.");
      ItemMeta meta=stack.getItemMeta();meta.setEnchantmentGlintOverride(true);stack.setItemMeta(meta);return stack;
   }
   public void registerCrystalRecipes() {
      addRecipe("tributo_cazador",tribute("Cazador"),new String[]{"FFF","FFF","FFF"},Map.of('F',new ExactChoice(fragment(1))));
      for (BossCatalog.Tier tier:BossCatalog.Tier.values()) {
         if(tier==BossCatalog.Tier.COMMON)continue;
         RecipeChoice previous=tier.ordinal()==0 ? new ExactChoice(fragment(1)) : new ExactChoice(crystal(BossCatalog.Tier.values()[tier.ordinal()-1],1));
         addRecipe("cristal_"+tier.name().toLowerCase(java.util.Locale.ROOT),crystal(tier,1),
            tier.ordinal()==0 ? new String[]{" F ","FTF"," F "} : new String[]{" F ","FT ","   "},
            Map.of('F',previous,'T',new ExactChoice(tribute("Cazador"))));
      }
   }

   public void reloadRares() {
      this.rareConfig = YamlConfiguration.loadConfiguration(new File(this.plugin.getDataFolder(), "rares.yml"));
      registerRecipes();
   }

   public void registerRecipes() {
      java.util.List<NamespacedKey> stale = new java.util.ArrayList<>();
      this.plugin.getServer().recipeIterator().forEachRemaining(r -> {
         if (r instanceof Keyed keyed && keyed.getKey().getNamespace().equals("cazajefes")) stale.add(keyed.getKey());
      });
      stale.forEach(this.plugin.getServer()::removeRecipe);
      for (String var2 : this.rareIds()) {
         this.plugin.getServer().removeRecipe(new NamespacedKey(this.plugin, var2));
      }

      this.plugin.getServer().removeRecipe(new NamespacedKey(this.plugin, "amuleto_coloso"));
      this.plugin.getServer().removeRecipe(new NamespacedKey(this.plugin, "amuleto_grieta"));
      this.addRecipe(
         "brujula_caza",
         this.compass(),
         new String[]{" M ", "MCM", " M "},
         Map.of('M', new ExactChoice(this.essence()), 'C', new MaterialChoice(Material.COMPASS))
      );
      ExactChoice var29 = new ExactChoice(this.fragment(1));
      ExactChoice var30 = this.exactRiftMaterial(0);
      ExactChoice var3 = this.exactRiftMaterial(1);
      ExactChoice var4 = this.exactRiftMaterial(2);
      ExactChoice var5 = this.exactRiftMaterial(3);
      this.addRecipe("esencia_rara", this.essence(), new String[]{"FPF", "PCP", "FPF"}, Map.of('F', var30, 'P', var29, 'C', this.bossCoreChoice()));
      this.addRecipe(
         "esencia_primordial",
         this.primordialEssence(),
         new String[]{"RDR", "CDC", "RDR"},
         Map.of('R', new ExactChoice(this.essence()), 'D', this.exactRiftMaterial(0), 'C', this.bossCoreChoice())
      );
      this.addRecipe(
         "esencia_abisal",
         this.abyssalEssence(),
         new String[]{"PTP", "TNT", "PTP"},
         Map.of('P', new ExactChoice(this.primordialEssence()), 'T', new ExactChoice(this.tribute("Cazador")), 'N', new MaterialChoice(Material.NETHER_STAR))
      );
      ExactChoice var6 = this.bossCoreChoice();

      for (String var8 : this.catalogIds().stream().distinct().toList()) {
         ItemStack var9 = this.rareBook(var8, 1);
         if (var9 != null) {
            ExactChoice var10 = switch (this.rareCatalystVariant(var8)) {
               case 0 -> var30;
               case 1 -> var3;
               case 2 -> var4;
               default -> var5;
            };
            if (this.spellIds().contains(var8)) {
               this.addRecipe(
                  "crear_libro_rare_" + var8,
                  var9,
                  new String[]{"PCP", "MEM", " F "},
                  Map.of('P', new MaterialChoice(Material.PAPER), 'C', var6, 'M', var10, 'E', new ExactChoice(this.essence()), 'F', var29)
               );
            } else {
               this.addRecipe(
                  "crear_esencia_rare_" + var8,
                  var9,
                  new String[]{" M ", "CEC", " F "},
                  Map.of('M', var10, 'C', var6, 'E', new ExactChoice(this.essence()), 'F', var29)
               );
            }
         }
      }

      ExactChoice var31 = this.bossCoreChoice();
      ExactChoice var32 = this.exactRiftMaterial(0);
      ExactChoice var33 = this.exactRiftMaterial(1);
      ExactChoice var34 = this.exactRiftMaterial(2);
      ExactChoice var11 = this.exactRiftMaterial(3);
      MaterialChoice var12 = new MaterialChoice(Material.NETHERITE_INGOT);
      MaterialChoice var13 = new MaterialChoice(Material.STICK);
      ExactChoice var15 = new ExactChoice(this.tribute("Cazador"));
      this.addRecipe(
         "hacha_sismica",
         this.rare("hacha_sismica"),
         new String[]{"GMP", " NB", " S "},
         Map.of('G', var32, 'M', var31, 'P', var33, 'N', var12, 'B', var11, 'S', var13)
      );
      this.addRecipe(
         "espada_brezal", this.rare("espada_brezal"), new String[]{" P ", "NMB", " F "}, Map.of('P', var34, 'N', var12, 'M', var31, 'B', var11, 'F', var29)
      );
      this.addRecipe("arco_ceniciento", this.rare("arco_ceniciento"), new String[]{"PFB", "M B", "PFB"}, Map.of('P', var33, 'F', var29, 'B', var11, 'M', var31));
      this.addRecipe(
         "lanza_magma", this.rare("lanza_magma"), new String[]{" P ", "MFB", " T "}, Map.of('P', var34, 'M', var31, 'F', var29, 'B', var11, 'T', var15)
      );
      ExactChoice var16 = new ExactChoice(new ItemStack[]{this.abyssalEssence(), this.abyssalEssenceFromBoss()});
      this.addRecipe(
         "mandoble_vacio",
         this.rare("mandoble_vacio"),
         new String[]{"HAN", "MFM", " T "},
         Map.of('H', var34, 'A', var16, 'N', var12, 'M', var31, 'F', var29, 'T', var15)
      );
      for(BossCatalog.Tier rank:EquipmentSets.RANKS){
         BossCatalog.Definition boss=BossCatalog.all().stream().filter(d->d.tier()==rank&&!d.mini()).findFirst().orElseThrow();
         for(int slot=0;slot<4;slot++)addRecipe("armadura_set_"+rank.name().toLowerCase(Locale.ROOT)+"_"+slot,armor(boss,slot),new String[]{" K ","CBC"," F "},Map.of('K',new ExactChoice(crystal(rank,1)),'C',bossCoreChoice(),'B',new MaterialChoice(armor(boss,slot).getType()),'F',new ExactChoice(fragment(1))));
         for(int slot=0;slot<2;slot++)addRecipe("arma_set_"+rank.name().toLowerCase(Locale.ROOT)+"_"+slot,weaponDrop(boss,slot),new String[]{" K ","CBC"," F "},Map.of('K',new ExactChoice(crystal(rank,1)),'C',bossCoreChoice(),'B',new MaterialChoice(weaponDrop(boss,slot).getType()),'F',new ExactChoice(fragment(1))));
      }
      ArrayList var21 = new ArrayList();

      for (Material var25 : Material.values()) {
         if (var25.name().endsWith("HELMET")
            || var25.name().endsWith("CHESTPLATE")
            || var25.name().endsWith("LEGGINGS")
            || var25.name().endsWith("BOOTS")
            || var25.name().endsWith("SWORD")
            || var25.name().endsWith("AXE")
            || var25.name().endsWith("PICKAXE")
            || var25.name().endsWith("SHOVEL")
            || var25.name().endsWith("HOE")
            || var25 == Material.BOW
            || var25 == Material.CROSSBOW
            || var25 == Material.TRIDENT) {
            var21.add(var25);
         }
      }

      for (int var35 = 1; var35 <= 3; var35++) {
         String var36 = var35 == 1 ? "rare_essence_attach" : "rare_essence_attach_" + var35;
         NamespacedKey var37 = new NamespacedKey(this.plugin, var36);
         if (this.plugin.getServer().getRecipe(var37) == null) {
            ItemStack var38 = new ItemStack(Material.PAPER);
            ItemMeta var26 = var38.getItemMeta();
            var26.displayName(Component.text("Infusión de esencia", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD));
            var26.lore(List.of(Component.text("Combina equipo con " + var35 + " esencia(s) de la misma rare.", NamedTextColor.GRAY)));
            var38.setItemMeta(var26);
            ShapelessRecipe var27 = new ShapelessRecipe(var37, var38);
            var27.addIngredient(new MaterialChoice(var21));

            for (int var28 = 0; var28 < var35; var28++) {
               var27.addIngredient(Material.MAGMA_CREAM);
            }

            this.plugin.getServer().addRecipe(var27);
         }
      }
      registerCrystalRecipes();
   }

   private int rareCatalystVariant(String var1) {
      if (this.spellIds().contains(var1)) {
         return 3;
      } else if (this.enchantIds().contains(var1)
         || List.of("fertilize", "meltobsidian", "smelt", "spore", "veinminer", "treefeller", "magneto").contains(var1)) {
         return 0;
      } else if (List.of("durability", "regeneration", "nofalldamage", "doublejump", "fly", "hardy", "guardian", "paso_viento", "saturacion").contains(var1)) {
         return 2;
      } else {
         return var1.equals("flame") ? 3 : 1;
      }
   }

   BossCatalog.Tier equipmentTier(ItemStack stack){
      BossCatalog.Tier rank=EquipmentSets.tier(customId(stack));return rank==null?BossCatalog.Tier.COMMON:rank;
   }

   private void addRecipe(String var1, ItemStack var2, String[] var3, Map<Character, RecipeChoice> var4) {
      if (var1.startsWith("armadura_set_") || var1.startsWith("arma_set_")) {
         BossCatalog.Tier rank=equipmentTier(var2);
         var3=new String[]{" K ","CBC"," F "};
         var4=Map.of('K',new ExactChoice(crystal(rank,1)),'C',bossCoreChoice(),'B',new MaterialChoice(var2.getType()),'F',new ExactChoice(fragment(1)));
      }
      if(var1.startsWith("crear_esencia_rare_")||var1.startsWith("crear_libro_rare_")||rareIds().contains(var1)){
         String property=var1.startsWith("crear_esencia_rare_")?var1.substring("crear_esencia_rare_".length()):var1.startsWith("crear_libro_rare_")?var1.substring("crear_libro_rare_".length()):var1;
         RecipeAlchemy.Profile profile=RecipeAlchemy.profile(property);
         var3=new String[]{" P ","CKR"," FB"};
         var4=Map.of('P',RecipeAlchemy.potionChoice(profile.potion()),'C',bossCoreChoice(),'K',new ExactChoice(crystal(profile.rank(),1)),
            'R',new MaterialChoice(profile.reagent()),'F',new ExactChoice(fragment(1)),'B',rareIds().contains(var1)?new MaterialChoice(var2.getType()):new ExactChoice(essence()));
      }
      var3 = var3.clone();
      var4 = new HashMap<>(var4);
      try {
         if (!((CazaJefesPlugin)this.plugin).recipeSettings.apply(var1, var3, var4)) return;
      } catch (IllegalArgumentException invalid) {
         this.plugin.getLogger().warning("Receta deshabilitada: " + invalid.getMessage()); return;
      }
      NamespacedKey var5 = new NamespacedKey(this.plugin, var1);
      if (this.plugin.getServer().getRecipe(var5) == null) {
         ShapedRecipe var6 = new ShapedRecipe(var5, var2);
         var6.shape(var3);
         var4.forEach(var6::setIngredient);
         this.plugin.getServer().addRecipe(var6);
      }
   }

   private ExactChoice exactRiftMaterial(int var1) {
      ItemStack var2 = this.riftMaterial(var1);
      this.addRiftDropClassification(var2, BossCatalog.Tier.EPIC);
      return new ExactChoice(var2);
   }

   private ExactChoice bossCoreChoice() {
      ArrayList var1 = new ArrayList();

      for (BossCatalog.Definition var3 : BossCatalog.all()) {
         ItemStack var4 = this.component(var3);
         this.addRiftDropClassification(var4, var3.tier());
         var1.add(var4);
      }

      return new ExactChoice(var1);
   }

   private ItemStack armor(BossCatalog.Definition boss,int slot){return EquipmentSets.armor(boss.tier(),slot,itemKey);}

   private ItemStack weaponDrop(BossCatalog.Definition boss,int slot){return EquipmentSets.weapon(boss.tier(),slot,itemKey);}

   private void enchant(ItemStack var1, Enchantment var2, int var3) {
      var1.addUnsafeEnchantment(var2, var3);
   }

   private ItemStack named(Material var1, String var2, String var3, NamedTextColor var4, int var5, String... var6) {
      ItemStack var7 = new ItemStack(var1, Math.max(1, Math.min(64, var5)));
      ItemMeta var8 = var7.getItemMeta();
      var8.displayName(((TextComponent)Component.text(var3, var4).decorate(TextDecoration.BOLD)).decoration(TextDecoration.ITALIC, false));
      ArrayList var9 = new ArrayList();

      for (int var10 = 0; var10 < var6.length; var10++) {
         Component var11 = Component.text(var6[var10], var10 % 2 == 0 ? var4 : NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false);
         var9.add(var11);
      }

      var8.lore(var9);
      var8.setEnchantmentGlintOverride(true);
      if(var2.equals("fragmento_grieta")){
         var8.itemName(Component.text("Fragmento de Tributo",NamedTextColor.LIGHT_PURPLE));
         var8.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
      }
      var8.getPersistentDataContainer().set(this.itemKey, PersistentDataType.STRING, var2);
      var7.setItemMeta(var8);
      return var7;
   }

   private final Map<Item,BukkitTask> animatedLoot=new HashMap<>();
   void stopLootAnimations(){for(var entry:animatedLoot.entrySet()){entry.getValue().cancel();if(entry.getKey().isValid()){entry.getKey().setGravity(true);entry.getKey().setPickupDelay(0);}}animatedLoot.clear();}
   private void dropWithAura(final Location var1, ItemStack var2, final Particle var3, UUID var4) {
      final Item var5 = var1.getWorld().dropItem(var1.clone().add(0.0, 2.7, 0.0), var2);
      var5.getPersistentDataContainer().set(this.dropEncounterKey, PersistentDataType.STRING, var4.toString());
      var5.setPickupDelay(90);
      var5.setGravity(false);
      var5.setVelocity(new Vector(0,0,0));
      var5.setGlowing(true);
      final BukkitTask[] var6 = new BukkitTask[1];
      var6[0] = this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, new Runnable() {
         int ticks;

         @Override
         public void run() {
            try {
            if (var5.isValid() && !var5.isDead() && (this.ticks+=2) <= 120) {
               if (this.ticks == 12) {
                  var5.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, var5.getLocation(), 14, 0.25, 0.25, 0.25, 0.08);
               }

               if (this.ticks == 80) {
                  var5.setGravity(true);
                  var5.setVelocity(new Vector(var5.getVelocity().getX(), -0.22, var5.getVelocity().getZ()));
               }

               if(this.ticks<80){double a=this.ticks*.10+var5.getUniqueId().hashCode()%360;var5.teleport(var1.clone().add(Math.cos(a)*.9,2.7+Math.sin(this.ticks*.1)*.2,Math.sin(a)*.9));}
               if(this.ticks%4!=0)return;
               Location var1x = var5.getLocation().add(0.0, 0.25, 0.0);
               double var2x = this.ticks * 0.36;

               for (int var4x = 0; var4x < 3; var4x++) {
                  double var5x = var2x + var4x * Math.PI / 3.0;
                  ParticleData.spawn(var1.getWorld(), var3, var1x.clone().add(Math.cos(var5x) * 0.48, var4x * 0.025, Math.sin(var5x) * 0.48), 1, 0.0, 0.0, 0.0, 0.0);
               }

               if (this.ticks == 4 || this.ticks == 80 || this.ticks == 100) {
                  var1.getWorld().spawnParticle(Particle.FIREWORK, var1x, 12, 0.25, 0.3, 0.25, 0.08);
               }
            } else {
               if(var5.isValid())var5.setGravity(true);
               animatedLoot.remove(var5);var6[0].cancel();
            }
            }catch(IllegalArgumentException invalid){
               if(var5.isValid())var5.setGravity(true);
               animatedLoot.remove(var5);var6[0].cancel();plugin.getLogger().warning("Se canceló un efecto de botín inválido: "+invalid.getMessage());
            }
         }
      }, 2L, 2L);
      animatedLoot.put(var5,var6[0]);
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void onLootPickup(EntityPickupItemEvent event) {
      if(!(event.getEntity() instanceof Player player))return;
      Item item=event.getItem();
      if(!item.getPersistentDataContainer().has(dropEncounterKey,PersistentDataType.STRING))return;
      ItemStack stack=item.getItemStack();
      NamespacedKey announced=new NamespacedKey(plugin,"rare_announced");
      if(!LootNotices.announceRare(true,rareLoot(stack),stack.getAmount(),event.getRemaining(),item.getPersistentDataContainer().has(announced,PersistentDataType.BYTE)))return;
      item.getPersistentDataContainer().set(announced,PersistentDataType.BYTE,(byte)1);
      Component name=stack.getItemMeta().displayName();
      plugin.getServer().broadcast(Component.text("✦ "+player.getName()+" ha recogido una RARE: ",NamedTextColor.GOLD).append(name==null?Component.text(stack.getType().name()):name));
   }
}
