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
   private final ArmorRares armorRares;

   public RelicItems(JavaPlugin var1, NamespacedKey var2) {
      this.plugin = var1;this.armorRares=new ArmorRares(this,var1);
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

      this.rareConfig = RareConfigUpgrade.load(var1,var3);
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
      var definition=RareDefinitions.get(var1);if(definition!=null)return definition.maximum();return var1.equals("despertar")?1:ArmorRareRules.title(var1)!=null?ArmorRareRules.maximum(var1):Math.max(1, Math.min(6, this.rareConfig.getInt(var1 + ".maxLvl", var1.equalsIgnoreCase("haste") ? 6 : 5)));
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
            "hardy", "strength", "turtle", "garlic",
            "saturacion",
            "flame"
         )
      );
      var1.addAll(this.enchantIds());var1.add("despertar");for(String id:RareDefinitions.ALL.keySet())if(!var1.contains(id))var1.add(id);var1.add("aura_esporas");
      var1.removeIf(id->RareAliases.duplicate((String)id));return var1;
   }

   public ItemStack rareBook(String var1) {
      return this.rareBook(var1, 1);
   }

   public ItemStack rareBook(String var1, int var2) {
      var1=RareAliases.canonical(var1);
      if (!this.catalogIds().contains(var1) && !this.enchantIds().contains(var1)) {
         return null;
      } else {
         String var3 = this.titleForRare(var1);
         int var4 = Math.max(1, Math.min(this.maxLevel(var1), var2));
         boolean var5 = this.spellIds().contains(var1);
         String var6 = RareDefinitions.get(var1)!=null?RareDefinitions.get(var1).description():var5
            ? this.spellDescription(var1)
            : (
               var1.equals("flame")
                  ? "Combínala en la mesa de crafteo con un casco."
                  : (var1.equals("magneto") ? "Atrae al inventario todo lo que extraes con un pico." : "Combínala en la mesa de crafteo con equipo compatible.")
            );
         String var7 = var1.equals("despertar")?"Resonancia Primigenia":var5 ? var3 : "Rare Essence";
         ItemStack var8 = this.named(
            var5 ? Material.BOOK : Material.MAGMA_CREAM,
            (var5 ? "rare_spell_book_" : "esencia_rare_") + var1 + "_" + var4,
            var7,
            NamedTextColor.GREEN,
            1,
            var6,
            "Tipo de Rare: " + RareAppearance.title(var1),
            "Nivel: " + RareAppearance.roman(var4)
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
               (TextComponent)Component.text("Tipo de Rare: " + RareAppearance.title(var1), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
               (TextComponent)Component.text("Nivel: " + RareAppearance.roman(var4), NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false),
               (TextComponent)((TextComponent)Component.text("Rare de la Grieta", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD))
                  .decoration(TextDecoration.ITALIC, false)
            )
         );
         var9.getPersistentDataContainer().set(this.rareBookKey, PersistentDataType.STRING, var1);
         var9.getPersistentDataContainer().set(this.rareEssenceLevelKey, PersistentDataType.INTEGER, var4);
         if(var5&&RareDefinitions.get(var1)!=null){var definition=RareDefinitions.get(var1);List<Component> lines=new ArrayList<>();lines.add(Component.text("Nivel "+RareAppearance.roman(var4)+" · Máximo "+RareAppearance.roman(definition.maximum()),NamedTextColor.AQUA).decoration(TextDecoration.ITALIC,false));lines.add(Component.text("Costo de uso: "+definition.cost()+" de saturación",NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false));lines.add(Component.text("Recarga: "+definition.cooldown()+" s",NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false));for(String line:RareAppearance.powerLines(var1))lines.add(Component.text(line,NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false));lines.add(Component.text("Clic derecho en el aire para activar",NamedTextColor.GOLD).decoration(TextDecoration.ITALIC,false));if(definition.maximum()>1)lines.add(Component.text("Une dos libros iguales para subir nivel",NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false));var9.lore(lines);}
         var8.setItemMeta(var9);
         return var1.equals("despertar")?awakeningSeal():var5?var8:refreshAppearance(var8);
      }
   }

   public ItemStack commandBook(String id,int level){ItemStack stack=rareBook(id,level);if(stack==null)return null;if(!spellIds().contains(RareAliases.canonical(id))){var meta=stack.getItemMeta();meta.getPersistentDataContainer().set(new NamespacedKey(plugin,"admin_rare_book"),PersistentDataType.BYTE,(byte)1);stack.setItemMeta(meta);}return decorateCommandBook(stack);}
   private ItemStack decorateCommandBook(ItemStack stack){var meta=stack.getItemMeta();if(!meta.getPersistentDataContainer().has(new NamespacedKey(plugin,"admin_rare_book"),PersistentDataType.BYTE))return stack;String id=meta.getPersistentDataContainer().get(rareBookKey,PersistentDataType.STRING);int level=meta.getPersistentDataContainer().getOrDefault(rareEssenceLevelKey,PersistentDataType.INTEGER,1);stack.setType(Material.ENCHANTED_BOOK);meta.displayName(Component.text(RareAppearance.title(id)+" "+RareAppearance.roman(level),NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC,false));List<Component> lore=new ArrayList<>();lore.add(Component.text("Nivel: "+RareAppearance.roman(level)+" · Máximo: "+RareAppearance.roman(maxLevel(id)),NamedTextColor.AQUA).decoration(TextDecoration.ITALIC,false));for(String line:RareAppearance.powerLines(id))lore.add(Component.text(line,NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false));lore.add(Component.text("Compatible: "+RareCompatibility.label(id),NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false));lore.add(Component.text("Mesa: libro + equipo compatible",NamedTextColor.GOLD).decoration(TextDecoration.ITALIC,false));meta.lore(lore);meta.setEnchantmentGlintOverride(true);stack.setItemMeta(meta);return stack;}

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
      var definition=RareDefinitions.get(var1);if(definition!=null)return definition.title();
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

   int activePropertyLevel(ItemStack item,String property){
      if(rareConfig!=null&&rareConfig.getDouble(property+".useCost",0)<0)return 0;
      if(item==null||!item.hasItemMeta())return 0;
      String properties=item.getItemMeta().getPersistentDataContainer().get(rareEnchantKey,PersistentDataType.STRING);
      return properties!=null&&Arrays.stream(properties.split(",")).map(RareAliases::canonical).anyMatch(property::equals)&&RareCompatibility.accepts(property,item.getType())?Math.min(maxLevel(property),Math.max(0,propertyLevel(item,property))):0;
   }
   public ItemStack armorSet(BossCatalog.Tier tier,int slot){return EquipmentSets.armor(tier,slot,itemKey);}
   public ItemStack weaponSet(BossCatalog.Tier tier,int slot){return EquipmentSets.weapon(tier,slot,itemKey);}
   int armorLevel(Player player,String id){return armorRares.level(player,id);}
   boolean armorCounter(Player player,String id){return armorRares.counter(player,id);}
   void stopArmorRares(){armorRares.stop();}
   boolean armorFuel(Player p,ItemStack item,String id){return consumePassiveSaturation(p,item,id);}
   private void registerSpellUpgrades(){for(int count=2;count<=8;count++){var key=new NamespacedKey(plugin,"rare_spell_upgrade_"+count);plugin.getServer().removeRecipe(key);var recipe=new ShapelessRecipe(key,new ItemStack(Material.BOOK));recipe.addIngredient(count,Material.BOOK);plugin.getServer().addRecipe(recipe);}}
   private boolean prepareSpellUpgrade(PrepareItemCraftEvent event){if(!(event.getRecipe() instanceof Keyed key)||!key.getKey().getNamespace().equals("cazajefes")||!key.getKey().getKey().startsWith("rare_spell_upgrade_"))return false;String id=null;List<Integer> levels=new ArrayList<>();for(ItemStack piece:event.getInventory().getMatrix()){if(piece==null||piece.getType().isAir())continue;if(!piece.hasItemMeta()){event.getInventory().setResult(null);return true;}String power=piece.getItemMeta().getPersistentDataContainer().get(rareBookKey,PersistentDataType.STRING);if(power==null||!spellIds().contains(power)||(id!=null&&!id.equals(power))){event.getInventory().setResult(null);return true;}id=power;levels.add(piece.getItemMeta().getPersistentDataContainer().getOrDefault(rareEssenceLevelKey,PersistentDataType.INTEGER,1));}int total=id==null?0:RareDefinitions.upgradedLevel(id,levels);event.getInventory().setResult(total>0?rareBook(id,total):null);return true;}
   @EventHandler(priority=EventPriority.HIGHEST)public void validateSuppliedIngredients(PrepareItemCraftEvent event){if(!(event.getRecipe() instanceof ShapedRecipe recipe)||!recipe.getKey().getNamespace().equals("cazajefes"))return;String id=recipe.getKey().getKey();if(!id.startsWith("crear_esencia_rare_")&&!id.startsWith("crear_libro_rare_"))return;for(ItemStack piece:event.getInventory().getMatrix())if(customId(piece)!=null&&recipe.getChoiceMap().values().stream().noneMatch(choice->choice instanceof ExactChoice&&choice.test(piece))){event.getInventory().setResult(null);return;}}
   private void tickArmorProperties(){
      long now=System.currentTimeMillis();
      for(Player player:plugin.getServer().getOnlinePlayers()){
         armorRares.tick(player);
         if(CombatGuard.eligible(player)&&!((CazaJefesPlugin)plugin).burstLaunch.lifting(player)){ItemStack wings=armorRares.strongest(player,"fly");if(wings!=null){player.setAllowFlight(true);flyUsers.add(player.getUniqueId());}else if(flyUsers.remove(player.getUniqueId())){player.setFlying(false);player.setAllowFlight(player.isOnGround()&&activePropertyLevel(player.getInventory().getBoots(),"doublejump")>0);}}

         if(player.isFlying()&&(player.getGameMode()==GameMode.SURVIVAL||player.getGameMode()==GameMode.ADVENTURE)&&flyUsers.contains(player.getUniqueId())&&now-flyDrain.getOrDefault(player.getUniqueId(),0L)>=10000){ItemStack wings=armorRares.strongest(player,"fly");if(wings==null||!consumeSaturation(player,wings,"fly")){player.setFlying(false);player.setAllowFlight(false);}flyDrain.put(player.getUniqueId(),now);}

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

   @EventHandler(priority=EventPriority.LOWEST)
   public void prepareSpellAir(PlayerInteractEvent event){if(event.getAction()!=Action.RIGHT_CLICK_AIR||event.getItem()==null||!event.getItem().hasItemMeta())return;String id=event.getItem().getItemMeta().getPersistentDataContainer().get(rareBookKey,PersistentDataType.STRING);if(id!=null&&spellIds().contains(id)){event.setUseItemInHand(org.bukkit.event.Event.Result.ALLOW);}}
   @EventHandler(priority = EventPriority.HIGHEST)
   public void onSpellUse(PlayerInteractEvent var1) {
      if(var1.getHand()==null||!SpellInteraction.allowed(var1.getAction(),var1.isCancelled(),var1.useItemInHand()))return;
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

               double var22 = saturationCost(var2,var3);
               if (var22 < 0.0) {
                  var4.sendMessage(Component.text("Esta rare está desactivada.", NamedTextColor.RED));
               } else {
                  int var23 = Math.max(1, Math.min(maxLevel(var3), (Integer)var2.getItemMeta().getPersistentDataContainer().getOrDefault(this.rareEssenceLevelKey, PersistentDataType.INTEGER, 1)));
                  if(var3.equals("repairitem")){ItemStack repair=var4.getInventory().getItem(0);if(repair==null||!(repair.getItemMeta() instanceof Damageable damage)||damage.getDamage()<=0){var4.sendMessage(Component.text("El primer espacio necesita un objeto dañado reparable.",NamedTextColor.GRAY));return;}}
                  if (!this.consumeSaturation(var4, var2, var3)) {
                     var4.sendMessage(Component.text("No tienes suficiente saturación para activar esta rare.", NamedTextColor.RED));
                  } else {
                     this.burstCooldowns.put(var4.getUniqueId()+":"+var3,var5+(RareDefinitions.get(var3)==null?10000:RareDefinitions.get(var3).cooldown()*1000L));

                     switch (var3) {
                        case "haste":
                           var20.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,600*var23,Math.min(6,var23-1)));
                           break;
                        case "fireresistance":
                           var20.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 900 * var23, 0));
                           break;
                        case "invisibility":
                           var20.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 1200, 0));
                           break;
                        case "burst":
                        case "greaterburst":
                           double var25 = (var3.equals("burst") ? 3 : 5) + var23 * 2;

                           var targets=var4.getNearbyEntities(var25,var25,var25).stream().filter(entity->entity instanceof LivingEntity living&&!living.isDead()&&!entity.equals(var4)&&var4.hasLineOfSight(entity)&&entity.getLocation().distanceSquared(var4.getLocation())<=var25*var25&&(!(entity instanceof Player player)||BurstVisuals.movable(player.isOp())&&RareCombat.enemy(var4,player))).sorted(java.util.Comparator.comparingDouble(entity->entity.getLocation().distanceSquared(var4.getLocation()))).limit(var3.equals("burst")?1:16).toList();
                           for(Entity entity:targets){Vector away=entity.getLocation().toVector().subtract(var4.getLocation().toVector()).setY(0);if(away.lengthSquared()<.01)away=var4.getLocation().getDirection().setY(0);if(away.lengthSquared()<.01)away=new Vector(1,0,0);Vector impulse=away.normalize().multiply((var3.equals("burst")?1:1.25)+var23*.35).setY(.65+var23*.18);if(entity instanceof Player target)((CazaJefesPlugin)plugin).burstLaunch.launch(target,impulse,false);else entity.setVelocity(impulse);}

                           BurstVisuals.showBook(plugin,var4);
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
                              int var26 = RareDefinitions.repairAmount(var13.getType().getMaxDurability(),var23);
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
      if(prepareSpellUpgrade(var1))return;
      if(!(var1.getRecipe() instanceof Keyed key)||!key.getKey().getKey().startsWith("rare_essence_attach"))var1.getInventory().setResult(carryBossSeal(var1.getInventory().getResult(),var1.getInventory().getMatrix()));
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
                  var12=RareAliases.canonical(var12);if (var12 != null) {
                     if (var8 != null && !var8.equals(var12)) {
                        var1.getInventory().setResult(null);
                        return;
                     }

                     var8 = RareAliases.canonical(var12);
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
               if(var8.equals("despertar")){
                  if(!RareCompatibility.canAwaken(bossInfusionGear(var7),!infusionBlocked(var7),var6.size())){var1.getInventory().setResult(null);return;}
                  ItemStack opened=var7.clone();var meta=opened.getItemMeta();meta.getPersistentDataContainer().set(new NamespacedKey(plugin,"infusion_unlocked"),PersistentDataType.BYTE,(byte)1);opened.setItemMeta(meta);var1.getInventory().setResult(refreshAppearance(opened));return;
               }
               if(infusionBlocked(var7)||!RareCompatibility.accepts(var8,var7.getType())){var1.getInventory().setResult(null);return;}else{
                  ItemStack var27 = var7.clone();
                  ItemMeta var15 = var27.getItemMeta();
                  String var16 = (String)var15.getPersistentDataContainer().get(this.rareEnchantKey, PersistentDataType.STRING);
                  Map<String, Integer> var17 = this.parseLevels((String)var15.getPersistentDataContainer().get(this.rareLevelsKey, PersistentDataType.STRING));
                  int var18 = this.maxLevel(var8);
                  int var19 = Math.min(var18, var17.getOrDefault(var8, 0) + var9);
                  if (!RareDefinitions.canInfuse(var17.getOrDefault(var8,0),var9,var18)) {
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
                     var21.add(Component.text("✦ "+RareAppearance.title(var8)+" "+RareAppearance.roman(var19),RareAppearance.ORANGE).decoration(TextDecoration.ITALIC,false));
                     var15.lore(var21);
                     var27.setItemMeta(var15);
                     var1.getInventory().setResult(refreshAppearance(var27));
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
                  var2.merge(RareAliases.canonical(var7[0]),Integer.parseInt(var7[1]),(a,b)->Math.max((Integer)a,(Integer)b));
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

   int propertyLevel(ItemStack var1, String var2) {
      if (var1 != null && var1.hasItemMeta()) {
         String var3 = (String)var1.getItemMeta().getPersistentDataContainer().get(this.rareLevelsKey, PersistentDataType.STRING);
         return this.parseLevels(var3).getOrDefault(var2, 1);
      } else {
         return 1;
      }
   }

   float saturationCost(ItemStack var1, String var2) {
      double var3 = this.rareConfig.getDouble(var2 + ".useCost", 2.0);var definition=RareDefinitions.get(var2);if(definition!=null&&var3>=0)return definition.cost();
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

   boolean consumeSaturation(Player var1, ItemStack var2, String var3) {
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

   boolean connectedBreak(){return breakingConnected;}
   void breakConnected(BlockBreakEvent var1, ItemStack var2, boolean var3) {
      if (breakingConnected) return;
      ArrayDeque var4 = new ArrayDeque();
      HashSet var5 = new HashSet();
      var4.add(var1.getBlock());
      var5.add(var1.getBlock().getX() + ":" + var1.getBlock().getY() + ":" + var1.getBlock().getZ());
      int var6 = 0;

      int cap=16*Math.max(1,Math.min(3,activePropertyLevel(var2,var3?"treefeller":"veinminer")))-1;
      Material original=var1.getBlock().getType();
      while (!var4.isEmpty() && var6 < cap) {
         Block var7 = (Block)var4.removeFirst();

         for (int var8 = -1; var8 <= 1; var8++) {
            for (int var9 = -1; var9 <= 1; var9++) {
               for (int var10 = -1; var10 <= 1; var10++) {
                  if (var8 != 0 || var9 != 0 || var10 != 0) {
                     Block var11 = var7.getRelative(var8, var9, var10);
                     String var12 = var11.getX() + ":" + var11.getY() + ":" + var11.getZ();
                     if (var5.add(var12)) {
                        if(!var11.getWorld().isChunkLoaded(var11.getX()>>4,var11.getZ()>>4))continue;
                        boolean var13 = var3 ? Tag.LOGS.isTagged(var11.getType())&&RareTools.treeFamily(var11.getType()).equals(RareTools.treeFamily(original)) : var11.getType()==original;
                        if (var13 && var6 < cap) {
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

   @EventHandler(ignoreCancelled=true,priority=EventPriority.HIGH)
   public void onRareDefense(EntityDamageEvent event){if(!(event.getEntity() instanceof Player p))return;int fall=armorRares.level(p,"nofalldamage");if(event.getCause()==DamageCause.FALL&&fall>0){SafeDamage.scale(event,RareDefinitions.fallMultiplier(fall));return;}for(ItemStack piece:p.getInventory().getArmorContents())if(activePropertyLevel(piece,"guardian")>0&&consumeSaturation(p,piece,"guardian")){SafeDamage.scale(event,.82);break;}}

   private final Map<UUID,Integer> jumpsUsed=new HashMap<>();
   @EventHandler(ignoreCancelled=true)
   public void onRareDoubleJump(PlayerToggleFlightEvent e){Player p=e.getPlayer();if(p.getGameMode()==GameMode.CREATIVE||p.getGameMode()==GameMode.SPECTATOR)return;if(((CazaJefesPlugin)plugin).burstLaunch.lifting(p)){e.setCancelled(true);return;}if(armorRares.level(p,"fly")>0)return;int maximum=activePropertyLevel(p.getInventory().getBoots(),"doublejump");if(maximum==0)return;e.setCancelled(true);p.setFlying(false);p.setAllowFlight(false);int used=jumpsUsed.getOrDefault(p.getUniqueId(),0);if(used>=maximum)return;jumpsUsed.put(p.getUniqueId(),used+1);p.setVelocity(p.getLocation().getDirection().multiply(.75).setY(.75));plugin.getServer().getScheduler().runTask(plugin,()->{if(p.isOnline()&&!p.isDead()&&jumpsUsed.getOrDefault(p.getUniqueId(),0)<maximum&&!((CazaJefesPlugin)plugin).burstLaunch.lifting(p))p.setAllowFlight(true);});}

   @EventHandler(ignoreCancelled = true)
   public void onWindStep(PlayerMoveEvent var1) {
      if(var1.getPlayer().isOnGround())jumpsUsed.remove(var1.getPlayer().getUniqueId());
      if(((CazaJefesPlugin)plugin).burstLaunch.lifting(var1.getPlayer())){var1.getPlayer().setFlying(false);var1.getPlayer().setAllowFlight(false);return;}
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
            && activePropertyLevel(var3,"doublejump")>0
            && var2.isOnGround()
            && var2.getGameMode() != GameMode.CREATIVE
            && var2.getGameMode() != GameMode.SPECTATOR) {
            var2.setAllowFlight(true);
         }

         if((var4==null||!var4.contains("doublejump"))&&!flyUsers.contains(var2.getUniqueId())&&var2.getGameMode()!=GameMode.CREATIVE&&var2.getGameMode()!=GameMode.SPECTATOR&&jumpsUsed.remove(var2.getUniqueId())!=null)var2.setAllowFlight(false);
         boolean var5 = false;
         int var6 = 1;

         for (ItemStack var10 : var2.getInventory().getArmorContents()) {
            if (var10 != null && var10.hasItemMeta()) {
               String var11 = (String)var10.getItemMeta().getPersistentDataContainer().get(this.rareEnchantKey, PersistentDataType.STRING);
               if (var11 != null && activePropertyLevel(var10,"fly")>0) {
                  var5 = true;
                  var6 = Math.max(var6,this.propertyLevel(var10, "fly"));
               }
            }
         }

         if (var2.getGameMode() != GameMode.CREATIVE && var2.getGameMode() != GameMode.SPECTATOR) {
            if (var5) {
               var2.setAllowFlight(true);
               this.flyUsers.add(var2.getUniqueId());

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
         Material.MAGMA_CREAM, "rare_essence", "Rare Essence", "Tipo de Rare: Esencia de la Grieta", "Energía concentrada de la Grieta.", "Sirve para recetas y forjas de reliquias."
      );
   }

   public ItemStack primordialEssence() {
      return this.plainGreenItem(
         Material.MAGMA_CREAM,
         "rare_essence_primordial",
         "Rare Essence",
         "Tipo de Rare: Primordial",
         "Rare Essence refinada con Polvo de la Grieta y Núcleos de jefe.",
         "Catalizador para recetas y forjas de alto rango."
      );
   }

   public ItemStack abyssalEssence() {
      return this.named(
         Material.DRAGON_BREATH,
         "rare_essence_abyssal",
         "Rare Essence",
         NamedTextColor.GREEN,
         1,
         "Tipo de Rare: Abismal",
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
         var6.add(Component.text("✦ "+RareAppearance.title(var7)+" "+RareAppearance.roman(var2),RareAppearance.ORANGE).decoration(TextDecoration.ITALIC,false));
         var11.lore(var6);
         var3.setItemMeta(var11);
      }

      return refreshAppearance(var3);
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
            case BASE_ESSENCE -> essence();
            case ESSENCE -> var2.tier().ordinal()>=4 ? primordialEssence() : essence();
            case ABYSSAL_ESSENCE -> abyssalEssence();
            case MATERIAL -> riftMaterial(reward.variant());
         };
         if (stack!=null) { stack.setAmount(reward.amount()); var4.add(prepareBossDrop(stack)); }
      }
      String rangedMaterial=RangedRareCatalog.drop(var2.entityType());if(rangedMaterial!=null&&Math.random()<(var2.mini()?.12:.25))var4.add(rangedMaterial(rangedMaterial));
      if(LootRules.heartEligible(var2.mini(),Math.random(),plugin.getConfig().getDouble("loot.boss-heart-chance",0.01)))var4.add(bossHeart(var2));
      List<ItemStack> merged=new ArrayList<>();for(ItemStack stack:var4){ItemStack same=merged.stream().filter(existing->existing.isSimilar(stack)).findFirst().orElse(null);if(same==null)merged.add(stack);else same.setAmount(same.getAmount()+stack.getAmount());}var4.clear();var4.addAll(merged);
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

      List<QueuedLoot> sequence=new ArrayList<>();
      for (ItemStack var11 : var4) {
         this.addRiftDropClassification(var11, var2.tier());
         for(ItemStack unit:LootPresentation.units(var11))sequence.add(new QueuedLoot(var1.clone(),unit,var9,var3));
      }

      queueLoot(sequence);
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
      if (var1.id().equals("gorath_quebrantador_de_mundos")) {
         return "hacha_sismica";
      } else if (var1.id().equals("nyxara_tejedora_del_ocaso")) {
         return "espada_brezal";
      } else if (var1.id().equals("varkhos_vigia_de_las_cenizas")) {
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
         "componente_" + var1.id(),
         coreName(var1.tier()),
         var1.tier().color,
         1,
         "Núcleo de energía condensada.",
         "Componente para armas y armaduras forjadas."
      );
   }

   public ItemStack componentForRecipe() {
      BossCatalog.Definition var1 = BossCatalog.get("gorath_quebrantador_de_mundos");
      ItemStack var2 = this.component(var1);
      this.addRiftDropClassification(var2, var1.tier());
      return var2;
   }

   public ItemStack riftMaterialForRecipe(int var1) {
      ItemStack var2 = this.riftMaterial(var1);
      this.addRiftDropClassification(var2, BossCatalog.Tier.EPIC);
      return var2;
   }

   ItemStack awakeningSeal(){ItemStack stack=named(Material.MAGMA_CREAM,"sello_resonancia_primigenia","✦ Resonancia Primigenia ✦",NamedTextColor.GOLD,1,"Rare de desbloqueo · Nivel I","Abre las infusiones de UNA pieza de boss.","Mesa: pieza sellada + una Resonancia Primigenia","Conserva encantamientos, desgaste y mejoras existentes.");var meta=stack.getItemMeta();meta.getPersistentDataContainer().set(rareBookKey,PersistentDataType.STRING,"despertar");meta.getPersistentDataContainer().set(rareEssenceLevelKey,PersistentDataType.INTEGER,1);stack.setItemMeta(meta);return stack;}
   private boolean bossInfusionGear(ItemStack stack){return stack!=null&&stack.hasItemMeta()&&RareAppearance.equipment(stack.getType())&&RareCompatibility.bossGear(customId(stack),stack.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin,"boss_reward"),PersistentDataType.BYTE));}
   private boolean infusionBlocked(ItemStack stack){return RareCompatibility.blocked(bossInfusionGear(stack),stack!=null&&stack.hasItemMeta()&&stack.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin,"infusion_unlocked"),PersistentDataType.BYTE));}
   private ItemStack carryBossSeal(ItemStack result,ItemStack... inputs){if(result==null||!RareAppearance.equipment(result.getType())||java.util.Arrays.stream(inputs).noneMatch(this::bossInfusionGear))return result;ItemStack out=result.clone();var meta=out.getItemMeta();meta.getPersistentDataContainer().set(new NamespacedKey(plugin,"boss_reward"),PersistentDataType.BYTE,(byte)1);ItemStack base=java.util.Arrays.stream(inputs).filter(i->i!=null&&!i.getType().isAir()).findFirst().orElse(null);if(base==null||!base.hasItemMeta()||!base.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin,"infusion_unlocked"),PersistentDataType.BYTE))meta.getPersistentDataContainer().remove(new NamespacedKey(plugin,"infusion_unlocked"));else meta.getPersistentDataContainer().set(new NamespacedKey(plugin,"infusion_unlocked"),PersistentDataType.BYTE,(byte)1);out.setItemMeta(meta);return refreshAppearance(out);}
   @EventHandler(priority=EventPriority.HIGHEST)public void sealAnvil(org.bukkit.event.inventory.PrepareAnvilEvent e){e.setResult(carryBossSeal(e.getResult(),e.getInventory().getItem(0),e.getInventory().getItem(1)));}
   @EventHandler(priority=EventPriority.HIGHEST)public void sealGrindstone(org.bukkit.event.inventory.PrepareGrindstoneEvent e){e.setResult(carryBossSeal(e.getResult(),e.getInventory().getItem(0),e.getInventory().getItem(1)));}
   @EventHandler(priority=EventPriority.HIGHEST)public void sealSmithing(org.bukkit.event.inventory.PrepareSmithingEvent e){e.setResult(carryBossSeal(e.getResult(),e.getInventory().getItem(1)));}
   private ItemStack prepareBossDrop(ItemStack stack){if(!RareAppearance.equipment(stack.getType()))return stack;ItemStack out=stack.clone();var meta=out.getItemMeta();meta.getPersistentDataContainer().remove(rareEnchantKey);meta.getPersistentDataContainer().remove(rareLevelsKey);meta.getPersistentDataContainer().remove(new NamespacedKey(plugin,"infusion_unlocked"));meta.getPersistentDataContainer().set(new NamespacedKey(plugin,"boss_reward"),PersistentDataType.BYTE,(byte)1);out.setItemMeta(meta);return refreshAppearance(out);}

   ItemStack refreshAppearance(ItemStack stack){
      if(stack==null||!stack.hasItemMeta())return stack;String id=customId(stack);var meta=stack.getItemMeta();String property=RareAliases.canonical(meta.getPersistentDataContainer().get(rareBookKey,PersistentDataType.STRING));if(property!=null)meta.getPersistentDataContainer().set(rareBookKey,PersistentDataType.STRING,property);if(property!=null&&spellIds().contains(property)){ItemStack renewed=rareBook(property,meta.getPersistentDataContainer().getOrDefault(rareEssenceLevelKey,PersistentDataType.INTEGER,1));renewed.setAmount(stack.getAmount());return renewed;}Map<String,Integer> levels=parseLevels(meta.getPersistentDataContainer().get(rareLevelsKey,PersistentDataType.STRING));
      if(RareCompatibility.material(stack.getType())==RareCompatibility.Group.ARMOR&&levels.containsKey("spore")){levels.put("aura_esporas",levels.remove("spore"));meta.getPersistentDataContainer().set(rareLevelsKey,PersistentDataType.STRING,serializeLevels(levels));String stored=meta.getPersistentDataContainer().get(rareEnchantKey,PersistentDataType.STRING);if(stored!=null)meta.getPersistentDataContainer().set(rareEnchantKey,PersistentDataType.STRING,Arrays.stream(stored.split(",")).map(key->key.equals("spore")?"aura_esporas":key).collect(java.util.stream.Collectors.joining(",")));}

      boolean essence=id!=null&&(id.startsWith("rare_essence")||id.startsWith("esencia_rare_"));boolean gear=RareAppearance.equipment(stack.getType());if(!essence&&levels.isEmpty()&&!bossInfusionGear(stack))return decorateCommandBook(stack);ItemStack out=stack.clone();
      List<Component> lore=new ArrayList<>(meta.lore()==null?List.of():meta.lore());
      if(essence){String type=property!=null?RareAppearance.title(property):id.equals("rare_essence_primordial")?"Primordial":id.equals("rare_essence_abyssal")?"Abismal":"Esencia de la Grieta";meta.displayName(Component.text(property==null?"Rare Essence":"Rare Essence · "+type,NamedTextColor.GREEN).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC,false));lore.removeIf(line->{String text=PlainTextComponentSerializer.plainText().serialize(line);return text.startsWith("Rare:")||text.startsWith("Tipo de Rare:")||text.startsWith("Nivel:")||text.startsWith("Compatible:");});lore.add(0,Component.text("Tipo de Rare: "+type,NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false));if(property!=null){lore.removeIf(line->!PlainTextComponentSerializer.plainText().serialize(line).startsWith("Tipo de Rare:"));String detail=RareAppearance.description(property);if(detail!=null){lore.removeIf(line->{String value=PlainTextComponentSerializer.plainText().serialize(line);return value.startsWith("Poder: ")||value.startsWith("Dominio máximo: ")||value.startsWith("Costo de uso: ")||value.startsWith("Recarga: ")||value.equals(detail);});lore.add(Component.text("Dominio máximo: "+RareAppearance.roman(maxLevel(property)),RareAppearance.ORANGE).decoration(TextDecoration.ITALIC,false));if(RareDefinitions.get(property)!=null){var rule=RareDefinitions.get(property);lore.add(Component.text("Costo de uso: "+(rule.cost()==0?"gratuito":rule.cost()+" de saturación"),NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false));if(rule.cooldown()>0)lore.add(Component.text("Recarga: "+rule.cooldown()+" s",NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false));}for(String line:RareAppearance.powerLines(property))lore.add(Component.text(line,NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false));}lore.add(1,Component.text("Nivel: "+RareAppearance.roman(meta.getPersistentDataContainer().getOrDefault(rareEssenceLevelKey,PersistentDataType.INTEGER,1)),NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false));lore.add(2,Component.text("Compatible: "+RareCompatibility.label(property),NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false));}}
      if(!levels.isEmpty()){lore.removeIf(line->{String text=PlainTextComponentSerializer.plainText().serialize(line);return text.startsWith("✦ Veneno ")||text.startsWith("✦ Vampirismo ")||text.startsWith("✦ Veneno Antiguo ")||text.startsWith("✦ Sello Vampírico ")||text.startsWith("Rare:")||text.startsWith("Rare ")&&text.contains("Nivel")||levels.keySet().stream().anyMatch(key->(text.startsWith("✦ "+RareAppearance.title(key)+" ")||text.startsWith("✦ "+RareNames.bare(RareAppearance.title(key))+" ")||text.startsWith("✦ "+RareNames.original(key)+" ")));});for(var entry:levels.entrySet())lore.add(Component.text("✦ "+RareAppearance.title(entry.getKey())+" "+RareAppearance.roman(Math.min(maxLevel(entry.getKey()),Math.max(1,entry.getValue()))),gear?RareAppearance.ORANGE:NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false));if(gear&&meta.displayName()!=null)meta.displayName(Component.text(PlainTextComponentSerializer.plainText().serialize(meta.displayName()),RareAppearance.ORANGE).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC,false));}
      if(bossInfusionGear(stack)){lore.removeIf(line->{String text=PlainTextComponentSerializer.plainText().serialize(line);return text.startsWith("✧ Infusiones:")||text.startsWith("Desbloquea con:");});lore.add(Component.text(infusionBlocked(stack)?"✧ Infusiones: SELLADAS":"✧ Infusiones: DESPERTADAS",infusionBlocked(stack)?NamedTextColor.DARK_PURPLE:RareAppearance.ORANGE).decoration(TextDecoration.ITALIC,false));if(infusionBlocked(stack))lore.add(Component.text("Desbloquea con: Resonancia Primigenia",NamedTextColor.GRAY).decoration(TextDecoration.ITALIC,false));}
      meta.lore(lore);out.setItemMeta(meta);return decorateCommandBook(out);
   }

   public ItemStack migrateCurrency(ItemStack stack) {
      stack=refreshAppearance(stack);String id=customId(stack);if(id==null)return stack;
      ItemStack replacement=null;
      if(id.equals("fragmento_grieta"))replacement=fragment(stack.getAmount());
      else if(id.equals("tributo_grieta"))replacement=tribute("Cazador");
      if(id.startsWith("armadura_"))return refreshAppearance(EquipmentSets.migrateArmor(stack,id,itemKey));
      if(id.startsWith("arma_"))return refreshAppearance(EquipmentSets.migrateWeapon(stack,id,itemKey));
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
      ItemStack[] armor=player.getInventory().getArmorContents();for(int i=0;i<armor.length;i++)armor[i]=migrateCurrency(armor[i]);player.getInventory().setArmorContents(armor);var off=player.getInventory().getItemInOffHand();var updated=migrateCurrency(off);if(!off.equals(updated))player.getInventory().setItemInOffHand(updated);
   }

   public ItemStack bossHeart(BossCatalog.Definition boss) {
      ItemStack stack=named(Material.HEART_OF_THE_SEA,"corazon_boss_"+boss.id(),"✦ Corazón de "+boss.name(),boss.tier().color,1,
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
      this.rareConfig = RareConfigUpgrade.load(plugin,new File(this.plugin.getDataFolder(), "rares.yml"));
      registerRecipes();
   }

   public void registerRecipes() {
      for(String duplicate:List.of("veneno","vampirismo"))plugin.getServer().removeRecipe(new NamespacedKey(plugin,"crear_esencia_rare_"+duplicate));
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

      for (String var8 : this.catalogIds().stream().distinct().filter(id->!id.equals("despertar")).toList()) {
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
               var27.addIngredient(new MaterialChoice(Material.MAGMA_CREAM,Material.ENCHANTED_BOOK));
            }

            this.plugin.getServer().addRecipe(var27);
         }
      }
      addRecipe("sello_resonancia_primigenia",awakeningSeal(),new String[]{"KPK","NSN","FTF"},Map.of('K',new ExactChoice(crystal(BossCatalog.Tier.EPIC,1)),'P',new ExactChoice(primordialEssence()),'N',bossCoreChoice(),'S',new MaterialChoice(Material.NETHER_STAR),'F',new ExactChoice(fragment(1)),'T',new ExactChoice(tribute("Cazador"))));
      for(String material:RangedRareCatalog.MATERIALS)addRecipe("ranged_material_"+material,rangedMaterial(material),new String[]{" R ","CEC"," F "},Map.of('R',new MaterialChoice(RangedRareCatalog.materialIcon(material)),'C',bossCoreChoice(),'E',new ExactChoice(essence()),'F',new ExactChoice(fragment(1))));
      registerSpellUpgrades();registerCrystalRecipes();if(((CazaJefesPlugin)plugin).duel!=null)((CazaJefesPlugin)plugin).duel.registerRecipes();
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

   private static List<String> wrapRareText(String text,int width){List<String> lines=new ArrayList<>();StringBuilder line=new StringBuilder();for(String word:text.split(" ")){if(line.length()+word.length()>width){lines.add(line.toString().trim());line.setLength(0);}line.append(word).append(' ');}if(!line.isEmpty())lines.add(line.toString().trim());return lines;}
   BossCatalog.Tier equipmentTier(ItemStack stack){
      BossCatalog.Tier rank=EquipmentSets.tier(customId(stack));return rank==null?BossCatalog.Tier.COMMON:rank;
   }

   void addDuelRecipe(String id,ItemStack result,String[] shape,Map<Character,RecipeChoice> choices){addRecipe(id,result,shape,choices);}
   RecipeChoice coreChoice(){return bossCoreChoice();}
   private void addRecipe(String var1, ItemStack var2, String[] var3, Map<Character, RecipeChoice> var4) {
      if (var1.startsWith("armadura_set_") || var1.startsWith("arma_set_")) {
         BossCatalog.Tier rank=equipmentTier(var2);
         var3=new String[]{" K ","CBC"," F "};
         var4=Map.of('K',new ExactChoice(crystal(rank,1)),'C',bossCoreChoice(),'B',new MaterialChoice(var2.getType()),'F',new ExactChoice(fragment(1)));
      }
      if(RecipeAlchemy.usesLegacyProfile(var1,rareIds().contains(var1))){
         String property=var1.startsWith("crear_esencia_rare_")?var1.substring("crear_esencia_rare_".length()):var1.startsWith("crear_libro_rare_")?var1.substring("crear_libro_rare_".length()):var1;
         RecipeAlchemy.Profile profile=RecipeAlchemy.profile(property);
         var3=new String[]{" P ","CKR"," FB"};
         var4=Map.of('P',RecipeAlchemy.potionChoice(profile.potion()),'C',bossCoreChoice(),'K',new ExactChoice(crystal(profile.rank(),1)),
            'R',new MaterialChoice(profile.reagent()),'F',new ExactChoice(fragment(1)),'B',rareIds().contains(var1)?new MaterialChoice(var2.getType()):new ExactChoice(essence()));
      }
      String supplied=RecipeAlchemy.propertyForRecipe(var1);
      var definition=supplied==null?null:RareDefinitions.get(supplied);
      if(definition!=null){var3=new String[]{"ABC","DEF","GHI"};var4=new HashMap<>();for(int index=0;index<9;index++){Material material=definition.ingredients().get(index);var4.put((char)('A'+index),material==Material.AIR?new ExactChoice(essence()):new MaterialChoice(material));}}
      if(supplied!=null&&RangedRareCatalog.get(supplied)!=null){var spec=RangedRareCatalog.get(supplied);var4.put('B',new ExactChoice(rangedMaterial(spec.material())));var4.put('H',new ExactChoice(rangedMaterial(spec.material())));var4.put('D',bossCoreChoice());var4.put('F',new ExactChoice(crystal(BossCatalog.Tier.EPIC,1)));}
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

   ItemStack rangedMaterial(String id){ItemStack stack=named(RangedRareCatalog.materialIcon(id),"ranged_material_"+id,RangedRareCatalog.materialName(id),NamedTextColor.AQUA,1,"Material de forja para rares de proyectiles.","Origen: "+RangedRareCatalog.origin(id),"Probabilidad: minijefes 12%; bosses 25%.","También se fabrica con núcleo, esencia y fragmento.");var meta=stack.getItemMeta();meta.setEnchantmentGlintOverride(true);stack.setItemMeta(meta);return stack;}
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

   private record QueuedLoot(Location location,ItemStack stack,Particle particle,UUID encounter){}
   private static final class LootAnimation {
      final org.bukkit.entity.ItemDisplay display;final DeathCompletion delivery;BukkitTask task;
      LootAnimation(org.bukkit.entity.ItemDisplay display,DeathCompletion delivery){this.display=display;this.delivery=delivery;}
   }
   private final Map<UUID,LootAnimation> animatedLoot=new HashMap<>();
   private void releaseLoot(QueuedLoot reward,boolean animate){
      if(animate&&plugin.isEnabled())dropWithAura(reward.location(),reward.stack(),reward.particle(),reward.encounter());
      else{Item item=reward.location().getWorld().dropItem(reward.location().clone().add(0,.3,0),reward.stack().clone());item.getPersistentDataContainer().set(dropEncounterKey,PersistentDataType.STRING,reward.encounter().toString());item.setVelocity(new Vector(0,0,0));item.setPickupDelay(0);item.setGlowing(true);}
   }
   private final Set<BukkitTask> lootSounds=new HashSet<>();
   private void queueLoot(List<QueuedLoot> rewards){
      for(int i=0;i<rewards.size();i++){
         QueuedLoot reward=rewards.get(i);var offset=LootScatter.at(i,rewards.size());Location candidate=reward.location().clone().add(offset.x(),0,offset.z());Location landing=reward.location().clone();
         for(int dy:new int[]{0,1,2,-1,-2,-3}){Location check=candidate.clone().add(0,dy,0);if(check.getBlock().isPassable()&&check.clone().add(0,1,0).getBlock().isPassable()&&check.clone().add(0,-1,0).getBlock().getType().isSolid()){landing=check;break;}}
         releaseLoot(new QueuedLoot(landing,reward.stack(),reward.particle(),reward.encounter()),true);
      }
      if(!plugin.isEnabled()||rewards.isEmpty())return;
      Location center=rewards.get(0).location().clone();final BukkitTask[] timer={null};timer[0]=plugin.getServer().getScheduler().runTaskTimer(plugin,new Runnable(){int pulse;public void run(){if(pulse>=8){lootSounds.remove(timer[0]);timer[0].cancel();return;}center.getWorld().playSound(center,Sound.BLOCK_AMETHYST_BLOCK_CHIME,.5f,.8f+pulse++*.12f);}},1,20);lootSounds.add(timer[0]);
   }
   void stopLootAnimations(){
      for(var animation:new ArrayList<>(animatedLoot.values()))animation.delivery.finish();animatedLoot.clear();
      for(var sound:lootSounds)sound.cancel();lootSounds.clear();
   }
   private void dropWithAura(final Location origin, ItemStack stack, final Particle particle, UUID encounter) {
      QueuedLoot reward=new QueuedLoot(origin.clone(),stack.clone(),particle,encounter);
      if(!plugin.isEnabled()){releaseLoot(reward,false);return;}
      final org.bukkit.entity.ItemDisplay display;
      double phase=ThreadLocalRandom.current().nextDouble(Math.PI*2);
      double dropHeight=Math.max(.3,Math.min(16,origin.getWorld().getMaxHeight()-origin.getY()-1));
      var first=LootSpiral.at(0,phase,dropHeight);
      try{
         display=origin.getWorld().spawn(origin.clone().add(first.x(),first.y(),first.z()),org.bukkit.entity.ItemDisplay.class,entity->{
            entity.setPersistent(false);entity.setInvulnerable(true);entity.setGravity(false);
            entity.setItemStack(stack.clone());entity.setItemDisplayTransform(org.bukkit.entity.ItemDisplay.ItemDisplayTransform.FIXED);
            entity.setTeleportDuration(2);entity.setInterpolationDuration(2);entity.setInterpolationDelay(0);
            entity.setBrightness(new org.bukkit.entity.Display.Brightness(15,15));entity.setGlowing(true);
            entity.getPersistentDataContainer().set(new NamespacedKey(plugin,"loot_visual"),PersistentDataType.BYTE,(byte)1);
         });
      }catch(IllegalArgumentException error){releaseLoot(reward,false);plugin.getLogger().warning("Botín entregado sin animación: "+error.getMessage());return;}
      UUID id=display.getUniqueId();
      DeathCompletion completion=new DeathCompletion(()->{
         LootAnimation active=animatedLoot.remove(id);if(active!=null&&active.task!=null)active.task.cancel();
         boolean landed=display.isValid()&&plugin.isEnabled();display.remove();releaseLoot(reward,false);
         if(landed&&LootPresentation.essence(customId(reward.stack())))essenceLanding(origin);
      });
      LootAnimation animation=new LootAnimation(display,completion);animatedLoot.put(id,animation);
      animation.task=plugin.getServer().getScheduler().runTaskTimer(plugin,new Runnable(){int ticks;
         public void run(){
            ticks+=2;
            if(!display.isValid()||ticks>=LootSpiral.DURATION+2){completion.finish();return;}
            try{
               var point=LootSpiral.at(ticks,phase,dropHeight);Location at=origin.clone().add(point.x(),point.y(),point.z());at.setYaw(ticks*4.5f);display.teleport(at);
               if(ticks%8==0)for(Player viewer:origin.getWorld().getPlayers())if(viewer.getLocation().distanceSquared(at)<=1024){
                  viewer.spawnParticle(Particle.END_ROD,at,2,.13,.18,.13,.015,null,true);
                  viewer.spawnParticle(Particle.DUST,at.clone().add(0,.2,0),1,.12,.12,.12,0,new Particle.DustOptions(org.bukkit.Color.fromRGB(110,255,210),1.1f),true);
               }
               if(ticks==LootSpiral.DURATION)origin.getWorld().spawnParticle(Particle.FIREWORK,at,12,.2,.25,.2,.03);
            }catch(IllegalArgumentException error){completion.finish();plugin.getLogger().warning("Botín liberado tras cancelar animación: "+error.getMessage());}
         }
      },2L,2L);
   }

   private void essenceLanding(Location origin){
      origin.getWorld().strikeLightningEffect(origin);
      origin.getWorld().playSound(origin,Sound.ENTITY_LIGHTNING_BOLT_THUNDER,.7f,1.4f);
      origin.getWorld().playSound(origin,Sound.UI_TOAST_CHALLENGE_COMPLETE,.8f,1.2f);
      origin.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING,origin.clone().add(0,.5,0),20,.4,.6,.4,.04);
      origin.getWorld().spawnParticle(Particle.FIREWORK,origin.clone().add(0,.5,0),16,.3,.5,.3,.03);
   }
   @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
   public void separateBossLoot(org.bukkit.event.entity.ItemMergeEvent event){
      if(event.getEntity().getPersistentDataContainer().has(dropEncounterKey,PersistentDataType.STRING)||event.getTarget().getPersistentDataContainer().has(dropEncounterKey,PersistentDataType.STRING))event.setCancelled(true);
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
