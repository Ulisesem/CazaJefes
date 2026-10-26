package dev.cazajefes;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

public final class CazaJefesCommand implements CommandExecutor, TabCompleter, Listener {
   private final JavaPlugin plugin;
   private final BossManager bosses;
   private final RelicItems items;

   public CazaJefesCommand(JavaPlugin var1, BossManager var2, RelicItems var3) {
      this.plugin = var1;
      this.bosses = var2;
      this.items = var3;
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (var4.length != 0 && !var4[0].equalsIgnoreCase("help")) {
         String var5 = var4[0].toLowerCase();
         switch (var5) {
            case "diagnostico":
               ((CazaJefesPlugin)plugin).damageDiagnostics.inspect(var1);break;
            case "spawnpoint":
            case "sp":
               this.spawnpoint(var1, var4);
               break;
            case "egg":
               this.egg(var1, var4);
               break;
            case "boss":
               this.boss(var1, var4);
               break;
            case "compass":
            case "c":
               this.compass(var1, var4);
               break;
            case "essence":
            case "esencia":
               this.essence(var1, var4);
               break;
            case "book":
            case "libro":
               this.giveBook(var1, var4);
               break;
            case "fragment":
               this.giveMaterial(var1, var4, "fragment");
               break;
            case "tribute":
               this.giveMaterial(var1, var4, "tribute");
               break;
            case "armor":
            case "armadura":
               this.giveGear(var1, var4, true);
               break;
            case "weapon":
            case "arma":
               this.giveGear(var1, var4, false);
               break;
            case "enchants":
               var1.sendMessage("Hechizos y propiedades: " + String.join(", ", this.items.catalogIds()) + " • /ri essence <id> [jugador]");
               break;
            case "get":
               this.get(var1, var4);
               break;
            case "rare":
               this.giveRare(var1, var4, 1);
               break;
            case "rares":
               this.plugin.getServer().dispatchCommand(var1, "rares");
               break;
            case "give":
               if (var4.length > 1 && var4[1].equalsIgnoreCase("rare")) {
                  this.giveRare(var1, var4, 2);
               } else {
                  var1.sendMessage("Uso: /ri give rare <id> [jugador]");
               }
               break;
            case "trade":
            case "canjear":
               this.trade(var1);
               break;
            case "recipes":
            case "recetas":
            case "crafts":
               this.plugin.getServer().dispatchCommand(var1, "rares");
               break;
            case "catalog":
            case "catalogo":
            case "esencias":
               this.catalog(var1);
               break;
            case "reload":
               this.reload(var1);
               break;
            case "eggs":
               this.listEggs(var1);
               break;
            case "bosses":
            case "jefes":
            case "bestiario":
               this.bossCatalog(var1);
               break;
            case "help":
               this.help(var1);
               break;
            default:
               this.help(var1);
         }

         return true;
      } else {
         this.help(var1);
         return true;
      }
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      ArrayList<String> var5 = new ArrayList<>();
      if (var4.length == 1) {
         var5.addAll(List.of("help", "bosses", "bestiario", "recipes", "recetas", "canjear", "catalogo", "compass", "rares", "enchants"));
         if (this.isAdmin(var1, "rareitemhunter.admin.egg")) {
            var5.addAll(List.of("egg", "eggs"));
         }

         if (this.isAdmin(var1, "rareitemhunter.admin.boss")) {
            var5.add("boss");
         }

         if (this.isAdmin(var1, "rareitemhunter.admin.spawnpoint")) {
            var5.add("spawnpoint");
         }

         if (this.isAdmin(var1, "rareitemhunter.admin.essence")) {
            var5.addAll(List.of("essence", "esencia", "book", "fragment", "tribute", "armor", "weapon", "get", "rare", "give"));
         }

         if (this.isAdmin(var1, "rareitemhunter.admin.reload")) {
            var5.add("reload");
         }
      } else {
         String var6 = var4[0].toLowerCase();
         if ((var6.equals("egg") || var6.equals("boss"))
            && var4.length == 2
            && this.isAdmin(var1, var6.equals("egg") ? "rareitemhunter.admin.egg" : "rareitemhunter.admin.boss")) {
            var5.addAll(BossCatalog.ids());
         } else if ((var6.equals("egg") || var6.equals("boss")) && var4.length == 3) {
            var5.add("here");
            var5.addAll(
               (Collection)(this.plugin.getConfig().getConfigurationSection("spawnpoints") == null
                  ? List.of()
                  : new ArrayList(this.plugin.getConfig().getConfigurationSection("spawnpoints").getKeys(false)))
            );
         } else if (var6.equals("book") || var6.equals("essence") || var6.equals("esencia") || var6.equals("rare") || var6.equals("give")) {
            if (var4.length != 2 && (!var6.equals("give") || var4.length != 3)) {
               if (var4.length >= 3) {
                  var5.addAll(List.of("1", "2", "3", "4", "5", "6"));
               }
            } else {
               var5.addAll(this.items.catalogIds());
            }
         } else if (var6.equals("armor") || var6.equals("armadura") || var6.equals("weapon") || var6.equals("arma")) {
            if (var4.length == 2) {
               var5.addAll(List.of("comun", "raro", "epico", "legendario", "mitico", "abismal"));
            } else {
               var5.addAll(this.onlinePlayerNames());
            }
         } else if (var6.equals("compass") || var6.equals("fragment") || var6.equals("tribute") || var6.equals("spawnpoint")) {
            if (var4.length > 1) {
               var5.addAll(this.onlinePlayerNames());
            }

            if (var6.equals("spawnpoint") && var4.length == 2) {
               var5.add("delete");
            }
         }
      }

      String var7 = var4[var4.length - 1].toLowerCase();
      return var5.stream().distinct().filter(var1x -> var1x.toLowerCase().startsWith(var7)).sorted(String.CASE_INSENSITIVE_ORDER).toList();
   }

   private List<String> onlinePlayerNames() {
      return this.plugin.getServer().getOnlinePlayers().stream().<String>map(Player::getName).toList();
   }

   private void spawnpoint(CommandSender var1, String[] var2) {
      if (!this.isAdmin(var1, "rareitemhunter.admin.spawnpoint")) {
         this.deny(var1);
      } else if (var1 instanceof Player var3) {
         if (var2.length > 2 && var2[1].equalsIgnoreCase("delete")) {
            this.plugin.getConfig().set("spawnpoints." + var2[2].toLowerCase(), null);
            this.plugin.saveConfig();
            var1.sendMessage(Component.text("Punto de aparición eliminado.", NamedTextColor.YELLOW));
         } else if (var2.length < 2) {
            var1.sendMessage("Uso: /ri spawnpoint <nombre> [radio]");
         } else {
            String var4 = var2[1].toLowerCase();
            double var5 = var2.length > 2 ? this.parseDouble(var2[2], 80.0) : 80.0;
            Location var7 = var3.getLocation();
            String var8 = "spawnpoints." + var4;
            this.plugin.getConfig().set(var8 + ".world", var7.getWorld().getName());
            this.plugin.getConfig().set(var8 + ".x", var7.getBlockX());
            this.plugin.getConfig().set(var8 + ".y", var7.getBlockY());
            this.plugin.getConfig().set(var8 + ".z", var7.getBlockZ());
            this.plugin.getConfig().set(var8 + ".radius", Math.max(8.0, var5));
            this.plugin.saveConfig();
            var1.sendMessage(Component.text("Punto de aparición guardado: " + var4 + " (radio " + (int)var5 + ").", NamedTextColor.GREEN));
         }
      } else {
         var1.sendMessage("Solo un jugador puede marcar un punto de aparición.");
      }
   }

   private void egg(CommandSender var1, String[] var2) {
      if (!this.isAdmin(var1, "rareitemhunter.admin.egg")) {
         this.deny(var1);
      } else if (var2.length < 2) {
         var1.sendMessage("Uso: /ri egg <id-del-jefe> [here|punto]");
      } else {
         String var3 = var2[1].toLowerCase();
         if (this.bosses.bossName(var3) == null) {
            var1.sendMessage("Ese ID no existe. Usa /ri bosses para ver el catálogo completo.");
         } else if (var1 instanceof Player var4) {
            Location var5 = this.locationArg(var4, var2.length > 2 ? var2[2] : "here", true);
            if (var5 == null) {
               var1.sendMessage("No encontré ese punto. Usa /ri spawnpoint <nombre> [radio].");
            } else if (!this.bosses.placeEgg(var3, var5)) {
               var1.sendMessage("No se pudo colocar el huevo: el bloque está ocupado.");
            } else {
               var1.sendMessage(
                  Component.text("Huevo de " + this.bosses.bossName(var3) + " colocado. La Brújula de Caza puede rastrearlo.", NamedTextColor.LIGHT_PURPLE)
               );
            }
         } else {
            var1.sendMessage("Invoca desde un jugador para elegir el mundo.");
         }
      }
   }

   private void boss(CommandSender var1, String[] var2) {
      if (!this.isAdmin(var1, "rareitemhunter.admin.boss")) {
         this.deny(var1);
      } else if (var2.length < 2) {
         var1.sendMessage("Uso: /ri boss <id-del-jefe> [here|punto]");
      } else {
         String var3 = var2[1].toLowerCase();
         if (this.bosses.bossName(var3) == null) {
            var1.sendMessage("Ese ID no existe. Usa /ri bosses para ver el catálogo completo.");
         } else if (var1 instanceof Player var4) {
            Location var5 = this.locationArg(var4, var2.length > 2 ? var2[2] : "here", false);
            if (var5 == null) {
               var1.sendMessage("No encontré ese punto de aparición.");
            } else {
               this.bosses.spawnBoss(var3, var5, var1.getName());
            }
         } else {
            var1.sendMessage("Invoca desde un jugador para elegir el mundo.");
         }
      }
   }

   private void compass(CommandSender var1, String[] var2) {
      Player var3 = var2.length > 1 ? this.plugin.getServer().getPlayerExact(var2[1]) : (var1 instanceof Player var4 ? var4 : null);
      if (var3 == null) {
         var1.sendMessage("Uso: /ri compass [jugador]");
      } else if ((var2.length <= 1 || this.isAdmin(var1, "rareitemhunter.admin.compass"))
         && (var2.length != 1 || var1.hasPermission("rareitemhunter.hunter.compass") || this.isAdmin(var1, "rareitemhunter.admin.compass"))) {
         var3.getInventory()
            .addItem(new ItemStack[]{this.items.compass()})
            .values()
            .forEach(var1x -> var3.getWorld().dropItemNaturally(var3.getLocation(), var1x));
         var1.sendMessage(Component.text("Brújula de Caza entregada a " + var3.getName() + ".", NamedTextColor.AQUA));
      } else {
         this.deny(var1);
      }
   }

   private void essence(CommandSender var1, String[] var2) {
      if (!this.isAdmin(var1, "rareitemhunter.admin.essence")) {
         this.deny(var1);
      } else {
         boolean var3 = var2.length > 1 && this.items.catalogIds().contains(var2[1].toLowerCase());
         int var4 = 1;
         byte var5 = 2;
         if (var3 && var2.length > 2) {
            try {
               var4 = Integer.parseInt(var2[2]);
               var5 = 3;
            } catch (NumberFormatException var9) {
            }
         }

         Player var6 = var3
            ? (var2.length > var5 ? this.plugin.getServer().getPlayerExact(var2[var5]) : (var1 instanceof Player var8 ? var8 : null))
            : (var2.length > 1 ? this.plugin.getServer().getPlayerExact(var2[1]) : (var1 instanceof Player var7 ? var7 : null));
         if (var6 == null) {
            var1.sendMessage("Uso: /ri essence <id> [nivel 1-5; Haste 1-6] [jugador] • también: /ri essence [jugador]");
         } else if (!var3 || var4 >= 1 && var4 <= this.items.maxLevel(var2[1])) {
            ItemStack var10 = var3 ? this.items.rareBook(var2[1].toLowerCase(), var4) : this.items.essence();
            var6.getInventory().addItem(new ItemStack[]{var10}).values().forEach(var1x -> var6.getWorld().dropItemNaturally(var6.getLocation(), var1x));
            var1.sendMessage(
               Component.text(
                  (var3 ? (this.items.spellIds().contains(var2[1].toLowerCase()) ? "Libro de " : "Esencia de ") + var2[1] + " nivel " + var4 : "Esencia Rara")
                     + " entregada a "
                     + var6.getName()
                     + ".",
                  NamedTextColor.GOLD
               )
            );
         } else {
            var1.sendMessage("Nivel máximo para " + var2[1] + ": " + this.items.maxLevel(var2[1]) + ".");
         }
      }
   }

   private void giveBook(CommandSender var1, String[] var2) {
      if (!this.isAdmin(var1, "rareitemhunter.admin.essence")) {
         this.deny(var1);
      } else if (var2.length >= 3 && this.items.catalogIds().contains(var2[1].toLowerCase())) {
         int var3;
         try {
            var3 = Integer.parseInt(var2[2]);
         } catch (NumberFormatException var7) {
            var1.sendMessage("El nivel debe ser un número del 1 al 5; Haste llega a 6.");
            return;
         }

         int var4 = this.items.maxLevel(var2[1]);
         if (var3 >= 1 && var3 <= var4) {
            Player var5 = var2.length > 3 ? this.plugin.getServer().getPlayerExact(var2[3]) : (var1 instanceof Player var6 ? var6 : null);
            if (var5 == null) {
               var1.sendMessage("Jugador no encontrado. Uso: /ri book <id-rare> <nivel 1-5> [jugador].");
            } else {
               ItemStack var8 = this.items.rareBook(var2[1].toLowerCase(), var3);
               var5.getInventory().addItem(new ItemStack[]{var8}).values().forEach(var1x -> var5.getWorld().dropItemNaturally(var5.getLocation(), var1x));
               var1.sendMessage(
                  Component.text(
                     (this.items.spellIds().contains(var2[1].toLowerCase()) ? "Libro de " : "Esencia de ")
                        + var2[1]
                        + " nivel "
                        + var3
                        + " entregado a "
                        + var5.getName()
                        + ".",
                     NamedTextColor.GREEN
                  )
               );
            }
         } else {
            var1.sendMessage("El nivel máximo para " + var2[1] + " es " + var4 + ".");
         }
      } else {
         var1.sendMessage(
            "Uso: /ri book <id-rare> <nivel 1-5; Haste 1-6> [jugador]. Entrega un libro normal para efectos activables o una esencia para fusionar."
         );
      }
   }

   private void giveMaterial(CommandSender var1, String[] var2, String var3) {
      if (!this.isAdmin(var1, "rareitemhunter.admin.essence")) {
         this.deny(var1);
      } else {
         Player var4 = var2.length > 1 ? this.plugin.getServer().getPlayerExact(var2[1]) : (var1 instanceof Player var5 ? var5 : null);
         if (var4 == null) {
            var1.sendMessage("Uso: /ri " + var3 + " [jugador]");
         } else {
            ItemStack var6 = var3.equals("fragment") ? this.items.fragment(1) : this.items.tribute("Cazador");
            var4.getInventory().addItem(new ItemStack[]{var6}).values().forEach(var1x -> var4.getWorld().dropItemNaturally(var4.getLocation(), var1x));
            var1.sendMessage("Entregado " + var3 + " a " + var4.getName() + ".");
         }
      }
   }

   private void giveGear(CommandSender var1, String[] var2, boolean var3) {
      if (!this.isAdmin(var1, "rareitemhunter.admin.essence")) {
         this.deny(var1);
      } else if (var2.length < 2) {
         var1.sendMessage("Uso: /ri " + (var3 ? "armor" : "weapon") + " <comun|raro|epico|legendario|mitico> [jugador]");
      } else {
         ItemStack var4 = this.items.gear(var2[1], var3);
         if (var4 == null) {
            var1.sendMessage("Rango no válido. Usa comun, raro, epico, legendario o mitico.");
         } else {
            Player var5 = var2.length > 2 ? this.plugin.getServer().getPlayerExact(var2[2]) : (var1 instanceof Player var6 ? var6 : null);
            if (var5 == null) {
               var1.sendMessage("Jugador no encontrado.");
            } else {
               var5.getInventory().addItem(new ItemStack[]{var4}).values().forEach(var1x -> var5.getWorld().dropItemNaturally(var5.getLocation(), var1x));
               var1.sendMessage("Equipo entregado a " + var5.getName() + ".");
            }
         }
      }
   }

   private void trade(CommandSender var1) {
      if (var1 instanceof Player var2) {
         this.trade(var2, 0);
      } else {
         var1.sendMessage("Abre los canjes desde el juego.");
      }
   }

   private void trade(Player player, int page) { ((CazaJefesPlugin)plugin).market.open(player); }

   List<CazaJefesCommand.TradeOffer> createTradeOffers() {
      ArrayList var1 = new ArrayList();
      var1.add(new CazaJefesCommand.TradeOffer(this.items.essence(), 3, 1, "Esencia de la Grieta"));
      var1.add(new CazaJefesCommand.TradeOffer(this.items.primordialEssence(), 8, 3, "Esencia Primordial"));
      var1.add(new CazaJefesCommand.TradeOffer(this.items.abyssalEssence(), 20, 8, "Esencia Abisal"));
      var1.add(new CazaJefesCommand.TradeOffer(this.items.amulet(), 32, 0, "Amuleto del Coloso"));
      int var2 = 0;

      for (String var4 : this.items.rareIds()) {
         ItemStack var5 = this.items.rare(var4);
         var1.add(new CazaJefesCommand.TradeOffer(var5, 7 + var2 * 2, 2 + var2, var4));
         var2++;
      }

      for (String var10 : this.items.catalogIds().stream().distinct().toList()) {
         if (this.items.rareBook(var10) != null) {
            boolean var12 = this.items.spellIds().contains(var10);
            ItemStack var6 = this.items.rareBook(var10, 1);
            String var7 = (var12 ? "SpellBook " : "Esencia ") + var10;
            var1.add(new CazaJefesCommand.TradeOffer(var6, var10.equals("magneto") ? 12 : (var12 ? 5 : 4), var10.equals("magneto") ? 3 : (var12 ? 1 : 2), var7));
         }
      }

      for (CazaJefesCommand.GearTierSpec var11 : this.gearTierSpecs()) {
         BossCatalog.Definition var13 = this.firstBoss(var11.tier());
         if (var13 != null) {
            for (int var14 = 0; var14 < 4; var14++) {
               var1.add(
                  new CazaJefesCommand.TradeOffer(
                     this.items.craftedArmor(var13.id(), var14),
                     var11.fragments(),
                     var11.tributes(),
                     "componente_",
                     var11.cores(),
                     this.armorSlotName(var14) + " de rango " + var11.display()
                  )
               );
            }

            for (int var15 = 0; var15 < 2; var15++) {
               var1.add(
                  new CazaJefesCommand.TradeOffer(
                     this.items.craftedWeapon(var13.id(), var15),
                     var11.fragments(),
                     var11.tributes(),
                     "componente_",
                     var11.cores(),
                     (var15 == 0 ? "Espada" : "Hacha") + " de rango " + var11.display()
                  )
               );
            }
         }
      }

      return var1;
   }

   private ItemStack tradeDisplay(CazaJefesCommand.TradeOffer var1) {
      ItemStack var2 = var1.reward().clone();
      ItemMeta var3 = var2.getItemMeta();
      ArrayList var4 = var3.lore() == null ? new ArrayList() : new ArrayList(var3.lore());
      var4.add(Component.text(" "));
      var4.add(Component.text("COSTE DE CANJE", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
      var4.add(Component.text(var1.fragments() + " Fragmentos de la Grieta", NamedTextColor.LIGHT_PURPLE));
      if (var1.tributes() > 0) {
         var4.add(Component.text(var1.tributes() + " Tributo(s) de jefe", NamedTextColor.AQUA));
      }

      if (var1.itemAmount() > 0) {
         var4.add(Component.text(var1.itemAmount() + " Núcleo(s) de jefe", NamedTextColor.GRAY));
      }

      var4.add(Component.text("Clic para canjear", NamedTextColor.GRAY));
      var3.lore(var4);
      var2.setItemMeta(var3);
      return var2;
   }

   private List<CazaJefesCommand.GearTierSpec> gearTierSpecs() {
      return List.of(
         new CazaJefesCommand.GearTierSpec("epico","Épico",BossCatalog.Tier.EPIC,12,3,1),
         new CazaJefesCommand.GearTierSpec("legendario", "Legendario", BossCatalog.Tier.LEGENDARY, 20, 4, 1),
         new CazaJefesCommand.GearTierSpec("mitico", "Mítico", BossCatalog.Tier.MYTHIC, 30, 6, 2),
         new CazaJefesCommand.GearTierSpec("abismal", "Abismal", BossCatalog.Tier.ABYSSAL, 45, 9, 3)
      );
   }

   private BossCatalog.Definition firstBoss(BossCatalog.Tier var1) {
      return BossCatalog.all().stream().filter(var1x -> var1x.tier() == var1 && !var1x.mini()).findFirst().orElse(null);
   }

   private String armorSlotName(int var1) {
      return switch (var1) {
         case 1 -> "Pechera";
         case 2 -> "Grebas";
         case 3 -> "Botas";
         default -> "Yelmo";
      };
   }

   private void recipes(CommandSender var1) {
      if (var1 instanceof Player var2) {
         this.recipes(var2, 0);
      } else {
         var1.sendMessage("Abre el recetario desde el juego.");
      }
   }

   private void recipes(Player player, int page) { this.plugin.getServer().dispatchCommand(player,"rares"); }

   private int recipeCategory(CazaJefesCommand.VisualRecipe var1) {
      String var2 = var1.id();
      if (var2.startsWith("rarecraft:")) {
         return this.items.spellIds().contains(var2.substring("rarecraft:".length())) ? 3 : 0;
      } else if (var2.startsWith("currency:") || var2.equals("brujula_caza")) {
         return 4;
      } else if (var2.startsWith("tradegear:")) {
         return var2.contains(":armor:") ? 2 : 1;
      } else if (var2.startsWith("armadura_")) {
         return 2;
      } else if (var2.startsWith("essence:")) {
         String var3 = var2.substring("essence:".length());
         if (this.items.spellIds().contains(var3)) {
            return 3;
         } else {
            String var4 = this.catalogType(var3);
            return !var4.equals("ABILITY") && !var4.equals("VISUAL") ? 1 : 2;
         }
      } else {
         return List.of("hacha_sismica", "espada_brezal", "arco_ceniciento", "lanza_magma", "mandoble_vacio").contains(var2) ? 1 : 0;
      }
   }

   private List<CazaJefesCommand.VisualRecipe> visualRecipes() {
      ArrayList var1 = new ArrayList();
      ItemStack var2 = this.items.componentForRecipe();
      ItemStack var3 = this.items.riftMaterialForRecipe(0);
      ItemStack var4 = this.items.riftMaterialForRecipe(1);
      ItemStack var5 = this.items.riftMaterialForRecipe(2);
      ItemStack var6 = this.items.riftMaterialForRecipe(3);
      var1.add(
         new CazaJefesCommand.VisualRecipe(
            "hacha_sismica",
            "Hacha Sísmica",
            this.items.rare("hacha_sismica"),
            this.items.rare("hacha_sismica"),
            new String[]{"GMP", " NB", " S "},
            Map.of('G', var3, 'M', var2, 'P', var4, 'N', new ItemStack(Material.NETHERITE_INGOT), 'B', var6, 'S', new ItemStack(Material.STICK))
         )
      );
      var1.add(
         new CazaJefesCommand.VisualRecipe(
            "espada_brezal",
            "Espada del Brezal",
            this.items.rare("espada_brezal"),
            this.items.rare("espada_brezal"),
            new String[]{" P ", "NMB", " F "},
            Map.of('P', var5, 'N', new ItemStack(Material.NETHERITE_INGOT), 'M', var2, 'B', var6, 'F', this.items.fragment(1))
         )
      );
      var1.add(
         new CazaJefesCommand.VisualRecipe(
            "arco_ceniciento",
            "Arco Ceniciento",
            this.items.rare("arco_ceniciento"),
            this.items.rare("arco_ceniciento"),
            new String[]{"PFB", "M B", "PFB"},
            Map.of('P', var4, 'F', this.items.fragment(1), 'B', var6, 'M', var2)
         )
      );
      var1.add(
         new CazaJefesCommand.VisualRecipe(
            "lanza_magma",
            "Lanza de Magma",
            this.items.rare("lanza_magma"),
            this.items.rare("lanza_magma"),
            new String[]{" P ", "MFB", " T "},
            Map.of('P', var5, 'M', var2, 'F', this.items.fragment(1), 'B', var6, 'T', this.items.tribute("Cazador"))
         )
      );
      var1.add(
         new CazaJefesCommand.VisualRecipe(
            "mandoble_vacio",
            "Mandoble del Vacío",
            this.items.rare("mandoble_vacio"),
            this.items.rare("mandoble_vacio"),
            new String[]{"HAN", "MFM", " T "},
            Map.of(
               'H',
               var5,
               'A',
               this.items.abyssalEssenceFromBoss(),
               'N',
               new ItemStack(Material.NETHERITE_INGOT),
               'M',
               var2,
               'F',
               this.items.fragment(1),
               'T',
               this.items.tribute("Cazador")
            )
         )
      );
      var1.add(
         new CazaJefesCommand.VisualRecipe(
            "armadura_grieta_yelmo",
            "Yelmo: Warden de la Grieta",
            this.items.craftedArmor("warden_abismal", 0),
            this.items.craftedArmor("warden_abismal", 0),
            new String[]{"MHM", "N N", "F F"},
            Map.of('M', var2, 'H', var5, 'N', new ItemStack(Material.NETHERITE_INGOT), 'F', this.items.fragment(1))
         )
      );
      var1.add(
         new CazaJefesCommand.VisualRecipe(
            "armadura_grieta_pechera",
            "Pechera: Coloso",
            this.items.craftedArmor("coloso", 1),
            this.items.craftedArmor("coloso", 1),
            new String[]{"N N", "MGM", "NPN"},
            Map.of('N', new ItemStack(Material.NETHERITE_INGOT), 'M', var2, 'G', var3, 'P', var4)
         )
      );
      var1.add(
         new CazaJefesCommand.VisualRecipe(
            "armadura_grieta_grebas",
            "Grebas: Leviatán del End",
            this.items.craftedArmor("leviatan_ender", 2),
            this.items.craftedArmor("leviatan_ender", 2),
            new String[]{"NPN", "M M", "F F"},
            Map.of('N', new ItemStack(Material.NETHERITE_INGOT), 'P', var4, 'M', var2, 'F', this.items.fragment(1))
         )
      );
      var1.add(
         new CazaJefesCommand.VisualRecipe(
            "armadura_grieta_botas",
            "Botas: Rey de las Brasas",
            this.items.craftedArmor("rey_brasas", 3),
            this.items.craftedArmor("rey_brasas", 3),
            new String[]{"P P", "M M", "B B"},
            Map.of('P', var4, 'M', var2, 'B', var6)
         )
      );
      var1.add(
         new CazaJefesCommand.VisualRecipe(
            "brujula_caza",
            "Brújula de Caza",
            this.items.compass(),
            this.items.compass(),
            new String[]{" M ", "MCM", " M "},
            Map.of('M', this.items.essence(), 'C', new ItemStack(Material.COMPASS))
         )
      );
      var1.add(
         new CazaJefesCommand.VisualRecipe(
            "currency:rare",
            "Rare Essence",
            this.items.essence(),
            this.items.essence(),
            new String[]{"FPF", "PCP", "FPF"},
            Map.of('F', var3, 'P', this.items.fragment(1), 'C', var2)
         )
      );
      var1.add(
         new CazaJefesCommand.VisualRecipe(
            "currency:primordial",
            "Esencia Primordial de la Grieta",
            this.items.primordialEssence(),
            this.items.primordialEssence(),
            new String[]{"RDR", "CDC", "RDR"},
            Map.of('R', this.items.essence(), 'D', var3, 'C', var2)
         )
      );
      var1.add(
         new CazaJefesCommand.VisualRecipe(
            "currency:abyssal",
            "Esencia Abisal",
            this.items.abyssalEssence(),
            this.items.abyssalEssence(),
            new String[]{"PTP", "TNT", "PTP"},
            Map.of('P', this.items.primordialEssence(), 'T', this.items.tribute("Cazador"), 'N', new ItemStack(Material.NETHER_STAR))
         )
      );

      for (CazaJefesCommand.GearTierSpec var8 : this.gearTierSpecs()) {
         BossCatalog.Definition var9 = this.firstBoss(var8.tier());
         if (var9 != null) {
            for (int var10 = 0; var10 < 4; var10++) {
               ItemStack var11 = this.items.craftedArmor(var9.id(), var10);
               var1.add(
                  new CazaJefesCommand.VisualRecipe(
                     "tradegear:" + var8.key() + ":armor:" + var10,
                     this.armorSlotName(var10) + " de " + var8.display(),
                     var11,
                     var11,
                     new String[]{"   ", "   ", "   "},
                     Map.of()
                  )
               );
            }

            for (int var18 = 0; var18 < 2; var18++) {
               ItemStack var20 = this.items.craftedWeapon(var9.id(), var18);
               var1.add(
                  new CazaJefesCommand.VisualRecipe(
                     "tradegear:" + var8.key() + ":weapon:" + var18,
                     (var18 == 0 ? "Espada" : "Hacha") + " de rango " + var8.display(),
                     var20,
                     var20,
                     new String[]{"   ", "   ", "   "},
                     Map.of()
                  )
               );
            }
         }
      }

      for (String var16 : this.items.catalogIds().stream().distinct().toList()) {
         ItemStack var17 = this.items.rareBook(var16, 1);
         if (var17 != null) {
            boolean var19 = this.items.spellIds().contains(var16);

            ItemStack var21 = switch (this.recipeCatalystVariant(var16)) {
               case 0 -> var3;
               case 1 -> var4;
               case 2 -> var5;
               default -> var6;
            };
            if (var19) {
               var1.add(
                  new CazaJefesCommand.VisualRecipe(
                     "rarecraft:" + var16,
                     "Libro de " + var16,
                     var17,
                     var17,
                     new String[]{"PCP", "MEM", " F "},
                     Map.of('P', new ItemStack(Material.PAPER), 'C', var2, 'M', var21, 'E', this.items.essence(), 'F', this.items.fragment(1))
                  )
               );
            } else {
               var1.add(
                  new CazaJefesCommand.VisualRecipe(
                     "rarecraft:" + var16,
                     "Esencia de " + var16,
                     var17,
                     var17,
                     new String[]{" M ", "CEC", " F "},
                     Map.of('M', var21, 'C', var2, 'E', this.items.essence(), 'F', this.items.fragment(1))
                  )
               );
            }

            String var13 = this.catalogType(var16);

            Material var12 = switch (var13) {
               case "SKILL" -> Material.DIAMOND_SWORD;
               case "ENCHANTMENT" -> Material.DIAMOND_PICKAXE;
               case "ABILITY" -> Material.DIAMOND_CHESTPLATE;
               case "SPELL" -> Material.BOOK;
               default -> Material.IRON_HELMET;
            };
            if (var19) {
               var1.add(new CazaJefesCommand.VisualRecipe("essence:" + var16, "Libro: " + var16, var17, var17, new String[]{"   ", "   ", "   "}, Map.of()));
            } else {
               ItemStack var22 = new ItemStack(var12);
               ItemMeta var23 = var22.getItemMeta();
               var23.displayName(
                  ((TextComponent)Component.text("Equipo con " + var16, NamedTextColor.GREEN).decorate(TextDecoration.BOLD))
                     .decoration(TextDecoration.ITALIC, false)
               );
               var23.lore(
                  List.of(
                     (TextComponent)Component.text("Vista previa de la fusión.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                     (TextComponent)Component.text("En la mesa: equipo compatible + esencia.", NamedTextColor.LIGHT_PURPLE)
                        .decoration(TextDecoration.ITALIC, false)
                  )
               );
               var22.setItemMeta(var23);
               var1.add(
                  new CazaJefesCommand.VisualRecipe(
                     "essence:" + var16, "Esencia: " + var16, var17, var22, new String[]{" E ", " Q ", "   "}, Map.of('E', var17, 'Q', new ItemStack(var12))
                  )
               );
            }
         }
      }

      return var1;
   }

   private int recipeCatalystVariant(String var1) {
      if (!this.items.spellIds().contains(var1) && !var1.equals("flame")) {
         if (this.items.enchantIds().contains(var1)
            || List.of("fertilize", "meltobsidian", "smelt", "spore", "veinminer", "treefeller", "magneto").contains(var1)) {
            return 0;
         } else {
            return List.of("durability", "regeneration", "nofalldamage", "doublejump", "fly", "hardy", "guardian", "paso_viento", "saturacion").contains(var1)
               ? 2
               : 1;
         }
      } else {
         return 3;
      }
   }

   private void openRecipeDetail(Player var1, String var2, int var3) {
      if (var2.startsWith("tradegear:")) {
         this.openTradeGearRecipeDetail(var1, var2, var3);
      } else {
         CazaJefesCommand.VisualRecipe var4 = this.visualRecipes().stream().filter(var1x -> var1x.id().equals(var2)).findFirst().orElse(null);
         if (var4 != null) {
            CazaJefesCommand.MenuHolder var5 = new CazaJefesCommand.MenuHolder("recipe_detail", var3, var2);
            String var6 = "RECETA: " + var4.title();
            if (var6.length() > 32) {
               var6 = var6.substring(0, 32);
            }

            Inventory var7 = Bukkit.createInventory(var5, 54, Component.text(var6, NamedTextColor.LIGHT_PURPLE));
            var5.inventory = var7;
            this.fillPremiumFrame(var7);
            String var8 = var4.id().startsWith("rarecraft:")
               ? "§7Craftea esta esencia con botín de jefes en una mesa de crafteo."
               : (
                  var4.id().startsWith("currency:")
                     ? "§7Receta de una moneda de progresión de CazaJefes."
                     : (
                        var4.id().startsWith("essence:") && this.items.spellIds().contains(var2.substring("essence:".length()))
                           ? "§7Libro activable; se obtiene como botín o mediante canje."
                           : (
                              var4.id().startsWith("essence:")
                                 ? "§7Equipo compatible + esencia del mismo tipo; el orden no importa."
                                 : (
                                    var4.id().equals("brujula_caza")
                                       ? "§7Usa una brújula y cuatro esencias."
                                       : "§7Coloca los materiales exactamente como aparecen."
                                 )
                           )
                     )
               );
            var7.setItem(4, this.menuItem(Material.CRAFTING_TABLE, "§d§l" + var4.title(), var8));
            int[] var9 = new int[]{10, 11, 12, 19, 20, 21, 28, 29, 30};

            for (int var10 = 0; var10 < 3; var10++) {
               for (int var11 = 0; var11 < 3; var11++) {
                  char var12 = var4.shape()[var10].charAt(var11);
                  if (var12 != ' ') {
                     ItemStack var13 = var4.ingredients().get(var12).clone();
                     var13.setAmount(1);
                     ItemMeta var14 = var13.getItemMeta();
                     var14.lore(
                        List.of((TextComponent)Component.text("Ingrediente de " + var4.title(), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false))
                     );
                     var13.setItemMeta(var14);
                     var7.setItem(var9[var10 * 3 + var11], var13);
                  }
               }
            }

            for (int var21 : new int[]{14, 23, 32}) {
               var7.setItem(var21, this.menuItem(Material.LIME_STAINED_GLASS_PANE, "§a§l»", "§7Resultado de la receta"));
            }

            ItemStack var16 = var4.result().clone();
            ItemMeta var18 = var16.getItemMeta();
            ArrayList var20 = var18.lore() == null ? new ArrayList() : new ArrayList(var18.lore());
            var20.add(Component.text(" "));
            var20.add(Component.text("RESULTADO", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
            var20.add(Component.text("Se fabrica en una mesa de crafteo.", NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false));
            var18.lore(var20);
            var16.setItemMeta(var18);
            var7.setItem(25, var16);
            var7.setItem(49, this.menuItem(Material.ARROW, "§d§lVOLVER A RECETAS", "§7Regresar al índice visual."));
            var1.openInventory(var7);
         }
      }
   }

   private void openTradeGearRecipeDetail(Player var1, String var2, int var3) {
      String[] var4 = var2.split(":");
      if (var4.length == 4) {
         CazaJefesCommand.GearTierSpec var5 = this.gearTierSpecs().stream().filter(var1x -> var1x.key().equals(var4[1])).findFirst().orElse(null);
         BossCatalog.Definition var6 = var5 == null ? null : this.firstBoss(var5.tier());
         if (var5 != null && var6 != null) {
            int var7;
            try {
               var7 = Integer.parseInt(var4[3]);
            } catch (NumberFormatException var15) {
               return;
            }

            boolean var8 = var4[2].equals("armor");
            ItemStack var9 = var8 ? this.items.craftedArmor(var6.id(), var7) : this.items.craftedWeapon(var6.id(), var7);
            if (var9 != null) {
               CazaJefesCommand.MenuHolder var10 = new CazaJefesCommand.MenuHolder("recipe_detail", var3, var2);
               Inventory var11 = Bukkit.createInventory(var10, 54, Component.text("CANJE: " + var5.display().toUpperCase(), NamedTextColor.DARK_PURPLE));
               var10.inventory = var11;
               this.fillPremiumFrame(var11);
               var11.setItem(
                  4,
                  this.menuItem(
                     Material.ANVIL,
                     "§d§lEQUIPO " + var5.display().toUpperCase(),
                     "§7Se obtiene en el Mercado de la Grieta.",
                     "§7Cada pieza usa objetos que dejan los jefes."
                  )
               );
               ItemStack var12 = this.items.fragment(var5.fragments());
               ItemStack var13 = this.items.componentForRecipe();
               var13.setAmount(var5.cores());
               ItemStack var14 = this.items.tribute("Cazador");
               var14.setAmount(var5.tributes());
               var11.setItem(20, var12);
               var11.setItem(22, var13);
               var11.setItem(24, var14);
               var11.setItem(25, var9);
               var11.setItem(30, this.menuItem(Material.EMERALD, "§a§lABRIR MERCADO", "§7Canjea esta pieza por los materiales indicados."));
               var11.setItem(49, this.menuItem(Material.ARROW, "§d§lVOLVER A RECETAS", "§7Regresar a armas y armaduras."));
               var1.openInventory(var11);
            }
         }
      }
   }

   private void fillPremiumFrame(Inventory var1) {
      ItemStack var2 = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
      ItemMeta var3 = var2.getItemMeta();
      var3.displayName(Component.text(" "));
      var2.setItemMeta(var3);

      for (int var4 = 0; var4 < var1.getSize(); var4++) {
         if (var4 < 9 || var4 >= var1.getSize() - 9 || var4 % 9 == 0 || var4 % 9 == 8) {
            var1.setItem(var4, var2);
         }
      }
   }

   private ItemStack menuItem(Material var1, String var2, String... var3) {
      ItemStack var4 = new ItemStack(var1);
      ItemMeta var5 = var4.getItemMeta();
      var5.displayName(
         ((TextComponent)LegacyComponentSerializer.legacySection().deserialize(var2).decorate(TextDecoration.BOLD)).decoration(TextDecoration.ITALIC, false)
      );
      ArrayList var6 = new ArrayList();

      for (String var10 : var3) {
         var6.add(LegacyComponentSerializer.legacySection().deserialize(var10).decoration(TextDecoration.ITALIC, false));
      }

      var5.lore(var6);
      var4.setItemMeta(var5);
      return var4;
   }

   private void catalog(CommandSender var1) {
      if (var1 instanceof Player var2) {
         this.catalog(var2, 0);
      } else {
         var1.sendMessage("Abre el catálogo desde el juego.");
      }
   }

   private void catalog(Player var1, int var2) {
      ArrayList var3 = new ArrayList<>(this.items.catalogIds().stream().distinct().toList());
      var3.add("material:primordial");
      var3.add("material:abyssal");
      byte var4 = 28;
      int var5 = Math.max(1, (var3.size() + var4 - 1) / var4);
      int var6 = Math.max(0, Math.min(var5 - 1, var2));
      CazaJefesCommand.MenuHolder var7 = new CazaJefesCommand.MenuHolder("catalog", var6);
      Inventory var8 = Bukkit.createInventory(var7, 54, Component.text("CATÁLOGO DE ESENCIAS · " + (var6 + 1) + "/" + var5, NamedTextColor.DARK_PURPLE));
      var7.inventory = var8;
      this.fillPremiumFrame(var8);
      var8.setItem(
         4, this.menuItem(Material.NETHER_STAR, "§d§lARCHIVO DE LA GRIETA", "§7Propiedades, compatibilidad y uso.", "§8Pulsa una esencia para ver su ficha.")
      );
      var8.setItem(
         49,
         this.menuItem(
            Material.AMETHYST_SHARD,
            "§b§lSISTEMA DE PROGRESIÓN",
            "§7Obtén materiales al derrotar jefes.",
            "§7Canjea fragmentos con §f/ri canjear§7.",
            "§7Recetas visuales: §f/ri recipes"
         )
      );
      if (var6 > 0) {
         var8.setItem(45, this.menuItem(Material.ARROW, "§d§lPÁGINA ANTERIOR", "§7Volver a la página " + var6));
      }

      if (var6 + 1 < var5) {
         var8.setItem(53, this.menuItem(Material.ARROW, "§d§lSIGUIENTE PÁGINA", "§7Ir a la página " + (var6 + 2)));
      }

      int[] var9 = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};

      for (int var10 = var6 * var4; var10 < Math.min((var6 + 1) * var4, var3.size()); var10++) {
         String var11 = (String)var3.get(var10);
         String var12 = var11.startsWith("material:") ? "MATERIAL" : this.catalogType(var11);
         ItemStack var13 = this.catalogItem(var11);
         if (var13 != null) {
            ItemMeta var14 = var13.getItemMeta();
            boolean var15 = var12.equals("SPELL");
            String var16 = var11.equals("material:primordial")
               ? "Esencia Primordial"
               : (var11.equals("material:abyssal") ? "Esencia Abisal" : (var15 ? "Libro de la Grieta" : "Esencia: " + var11));
            String var17 = var11.equals("material:primordial")
               ? "Catalizador para recetas y forjas de alto rango."
               : (var11.equals("material:abyssal") ? "Material mítico de jefes legendarios y míticos." : this.catalogDescription(var11));
            String var18 = var12.equals("MATERIAL") ? "Canjes y forjas especiales" : this.catalogBase(var12, var11);
            var14.displayName(
               ((TextComponent)Component.text(
                        var16, var12.equals("MATERIAL") ? NamedTextColor.DARK_PURPLE : (var15 ? NamedTextColor.LIGHT_PURPLE : NamedTextColor.GREEN)
                     )
                     .decorate(TextDecoration.BOLD))
                  .decoration(TextDecoration.ITALIC, false)
            );
            var14.lore(
               List.of(
                  (TextComponent)((TextComponent)Component.text("REGISTRO  ·  " + var12, NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                     .decoration(TextDecoration.ITALIC, false),
                  (TextComponent)Component.text(var17, NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
                  (TextComponent)Component.text("Uso: " + var18, NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false),
                  (TextComponent)Component.text("Pulsa para abrir la ficha", NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false)
               )
            );
            var13.setItemMeta(var14);
            int var19 = var9[var10 - var6 * var4];
            var8.setItem(var19, var13);
            var7.catalogIdsBySlot.put(var19, var11);
         }
      }

      var1.openInventory(var8);
   }

   private void openRareDetail(Player var1, String var2, int var3) {
      boolean var4 = var2.startsWith("material:");
      String var5 = var4 ? "MATERIAL" : this.catalogType(var2);
      boolean var6 = var5.equals("SPELL");
      ItemStack var7 = this.catalogItem(var2);
      if (var7 != null) {
         CazaJefesCommand.MenuHolder var8 = new CazaJefesCommand.MenuHolder("catalog_detail", var3, var2);
         String var9 = "FICHA · " + (var6 ? "LIBRO" : "ESENCIA");
         Inventory var10 = Bukkit.createInventory(var8, 54, Component.text(var9, NamedTextColor.DARK_PURPLE));
         var8.inventory = var10;
         this.fillPremiumFrame(var10);
         String var11 = var2.equals("material:primordial")
            ? "Esencia Primordial"
            : (var2.equals("material:abyssal") ? "Esencia Abisal" : (var6 ? "Libro de la Grieta" : "Esencia: " + var2));
         String var12 = var2.equals("material:primordial")
            ? "Catalizador para recetas y forjas de alto rango."
            : (
               var2.equals("material:abyssal")
                  ? "Requisito de forja para el Mandoble del Vacío. La dejan jefes legendarios y míticos."
                  : this.catalogDescription(var2)
            );
         String var13 = var4 ? "Canjes y recetas especiales" : this.catalogBase(var5, var2);
         var10.setItem(4, this.menuItem(Material.AMETHYST_SHARD, "§d§l" + var11.toUpperCase(), "§7Registro de la Grieta."));
         ItemMeta var14 = var7.getItemMeta();
         var14.displayName(
            ((TextComponent)Component.text(var11, var4 ? NamedTextColor.DARK_PURPLE : (var6 ? NamedTextColor.LIGHT_PURPLE : NamedTextColor.GREEN))
                  .decorate(TextDecoration.BOLD))
               .decoration(TextDecoration.ITALIC, false)
         );
         ArrayList var15 = new ArrayList<>(
            List.of(
               Component.text(var12, NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
               Component.text("Categoría: " + var5, NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false),
               Component.text("Uso: " + var13, NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false)
            )
         );
         if (!var4) {
            var15.add(Component.text("Nivel máximo: " + this.items.maxLevel(var2), NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false));
         }

         var14.lore(var15);
         var7.setItemMeta(var14);
         var10.setItem(22, var7);
         var10.setItem(30, this.menuItem(Material.CRAFTING_TABLE, "§b§lVER RECETAS", "§7Abre el recetario completo."));
         var10.setItem(32, this.menuItem(Material.EMERALD, "§6§lCANJEAR", "§7Busca esta propiedad en §f/ri canjear§7."));
         var10.setItem(49, this.menuItem(Material.ARROW, "§d§lVOLVER AL CATÁLOGO", "§7Regresar a la página " + (var3 + 1)));
         var1.openInventory(var10);
      }
   }

   private ItemStack catalogItem(String var1) {
      return switch (var1) {
         case "material:primordial" -> this.items.primordialEssence();
         case "material:abyssal" -> this.items.abyssalEssenceFromBoss();
         default -> this.items.rareBook(var1);
      };
   }

   private String catalogType(String var1) {
      if (List.of(
            "poison",
            "veneno",
            "backstab",
            "blinding",
            "calllighting",
            "confuse",
            "disarm",
            "slow",
            "vampirismo",
            "vampiricregeneration",
            "weaken",
            "execute",
            "bleed",
            "brasas"
         )
         .contains(var1)) {
         return "SKILL";
      } else if (List.of("fertilize", "meltobsidian", "smelt", "spore", "veinminer", "treefeller", "magneto").contains(var1)) {
         return "ENCHANTMENT";
      } else if (this.items.spellIds().contains(var1)) {
         return "SPELL";
      } else {
         return List.of("durability", "regeneration", "nofalldamage", "doublejump", "fly", "hardy", "guardian", "paso_viento", "saturacion").contains(var1)
            ? "ABILITY"
            : "VISUAL";
      }
   }

   private String catalogBase(String var1, String var2) {
      if (var2.equals("hardy")) {
         return "pechera únicamente";
      } else if (var2.equals("flame")) {
         return "casco, combinar en mesa";
      } else if (var2.equals("spore")) {
         return "picos, azadas, espadas y hachas";
      } else {
         return switch (var1) {
            case "SKILL" -> "espadas, arcos y hachas";
            case "ENCHANTMENT" -> "picos, azadas y palas";
            case "SPELL" -> "libro normal, clic derecho";
            case "ABILITY" -> "armadura y equipo compatible";
            default -> "casco / cosmético";
         };
      }
   }

   private String catalogDescription(String var1) {
      return switch (var1) {
         case "poison" -> "Envenena al objetivo al golpear.";
         case "backstab" -> "Más daño por la espalda.";
         case "blinding" -> "Probabilidad de causar ceguera.";
         case "calllighting" -> "Puede invocar un rayo sobre el enemigo.";
         case "confuse" -> "Desorienta al objetivo.";
         case "disarm" -> "Puede hacer que el enemigo suelte su arma.";
         case "slow" -> "Ralentiza al rival.";
         case "vampiricregeneration" -> "Roba vida con probabilidad.";
         case "weaken" -> "Debilita al enemigo.";
         case "execute" -> "Daño extra a enemigos heridos.";
         case "bleed" -> "Daño periódico que ignora parte de la armadura.";
         case "fertilize" -> "Convierte tierra en césped.";
         case "meltobsidian" -> "Convierte lava en obsidiana.";
         case "smelt" -> "Funde drops compatibles al extraer.";
         case "spore" -> "Crea variantes musgosas.";
         case "veinminer" -> "Extrae una veta completa.";
         case "treefeller" -> "Tala el tronco entero.";
         case "magneto" -> "Envía al inventario lo que extraes con un pico.";
         case "burst" -> "Empuja a un enemigo cercano.";
         case "craftitem" -> "Abre la mesa de crafteo.";
         case "fireresistance" -> "Resistencia al fuego temporal.";
         case "greaterburst" -> "Empuja a varios enemigos.";
         case "haste" -> "Otorga velocidad temporal.";
         case "invisibility" -> "Invisibilidad temporal.";
         case "magicbag" -> "Abre el Ender Chest.";
         case "repairitem" -> "Repara el objeto en la mano.";
         case "durability" -> "Repara armadura equipada.";
         case "regeneration" -> "Regeneración pasiva.";
         case "nofalldamage" -> "Reduce daño de caída.";
         case "doublejump" -> "Salto adicional en el aire.";
         case "fly" -> "Vuelo con coste configurable.";
         case "hardy" -> "Fuerza según nivel; solo pechera.";
         case "saturacion" -> "Recupera saturación pasivamente cada cinco segundos.";
         default -> "Efecto visual de llamas en casco.";
      };
   }

   @org.bukkit.event.EventHandler
   public void preventMenuDrag(org.bukkit.event.inventory.InventoryDragEvent event) {
      if (event.getView().getTopInventory().getHolder() instanceof MenuHolder) event.setCancelled(true);
   }

   @EventHandler
   public void onMenuClick(InventoryClickEvent event) {
      if (!(event.getView().getTopInventory().getHolder() instanceof MenuHolder menu)) return;
      event.setCancelled(true);
      int slot=event.getRawSlot();
      if (!(event.getWhoClicked() instanceof Player player) || !MenuActions.isActivation(event.getClick(),slot,event.getView().getTopInventory().getSize())) return;
      MenuActions.dispatch(plugin, player, menu, menu.gate, () -> handleMenuClick(menu,player,slot));
   }

   private void handleMenuClick(MenuHolder var2, Player var8, int var9) {
            if (var2.mode.equals("catalog")) {
               if (var9 == 45 && var2.page > 0) {
                  this.catalog(var8, var2.page - 1);
               } else if (var9 == 53) {
                  this.catalog(var8, var2.page + 1);
               } else {
                  String var13 = var2.catalogIdsBySlot.get(var9);
                  if (var13 != null) {
                     this.openRareDetail(var8, var13, var2.page);
                  }
               }
            } else if (var2.mode.equals("catalog_detail")) {
               if (var9 == 49) {
                  this.catalog(var8, var2.page);
               } else if (var9 == 30) {
                  this.recipes(var8);
               } else if (var9 == 32) {
                  this.trade(var8);
               }
            } else if (var2.mode.equals("boss_catalog")) {
               if (var9 == 45 && var2.page > 0) {
                  this.bossCatalog(var8, var2.page - 1);
               } else if (var9 == 53) {
                  this.bossCatalog(var8, var2.page + 1);
               } else {
                  String var12 = var2.bossIdsBySlot.get(var9);
                  if (var12 != null) {
                     this.openBossDetail(var8, var12, var2.page);
                  }
               }
            } else if (var2.mode.equals("boss_detail")) {
               if (var9 == 49) {
                  this.bossCatalog(var8, var2.page);
               } else {
                  if (var9 == 40 && var2.detailId != null) {
                     if (!this.isAdmin(var8, "rareitemhunter.admin.egg")) {
                        this.deny(var8);
                        return;
                     }

                     Location var11 = this.locationArg(var8, "here", true);
                     if (var11 != null && this.bosses.placeEgg(var2.detailId, var11)) {
                        var8.closeInventory();
                        var8.sendMessage(Component.text("Huevo de prueba colocado. Rastréalo con la Brújula de Caza.", NamedTextColor.LIGHT_PURPLE));
                     } else {
                        var8.sendMessage(Component.text("No se pudo colocar el huevo en esta ubicación.", NamedTextColor.RED));
                     }
                  }
               }
            } else if (var2.mode.equals("recipes")) {
               int var10 = var2.page / 100;
               int var6 = var2.page % 100;
               if (var9 >= 46 && var9 <= 50) {
                  this.recipes(var8, (var9 - 46) * 100);
               } else if (var9 == 45 && var6 > 0) {
                  this.recipes(var8, var10 * 100 + var6 - 1);
               } else if (var9 == 53) {
                  this.recipes(var8, var10 * 100 + var6 + 1);
               } else {
                  String var7 = var2.recipeIdsBySlot.get(var9);
                  if (var7 != null) {
                     this.openRecipeDetail(var8, var7, var2.page);
                  }
               }
            } else if (!var2.mode.equals("recipe_detail")) {
               if (var2.mode.equals("trade")) {
                  if (var9 == 45 && var2.page > 0) {
                     this.trade(var8, var2.page - 1);
                     return;
                  }

                  if (var9 == 53) {
                     this.trade(var8, var2.page + 1);
                     return;
                  }

                  CazaJefesCommand.TradeOffer var5 = var2.offersBySlot.get(var9);
                  if (var5 == null) {
                     return;
                  }

                  if (!this.items.hasPrefix(var8, "fragmento_grieta", var5.fragments())
                     || !this.items.hasPrefix(var8, "tributo_", var5.tributes())
                     || var5.itemAmount() > 0 && !this.items.hasPrefix(var8, var5.itemPrefix(), var5.itemAmount())) {
                     var8.sendMessage(Component.text("Te faltan monedas de cazador para ese canje.", NamedTextColor.RED));
                     return;
                  }

                  this.items.consumePrefix(var8, "fragmento_grieta", var5.fragments());
                  this.items.consumePrefix(var8, "tributo_", var5.tributes());
                  if (var5.itemAmount() > 0) {
                     this.items.consumePrefix(var8, var5.itemPrefix(), var5.itemAmount());
                  }

                  var8.getInventory()
                     .addItem(new ItemStack[]{var5.reward().clone()})
                     .values()
                     .forEach(var1x -> var8.getWorld().dropItemNaturally(var8.getLocation(), var1x));
                  var8.sendMessage(Component.text("Canje completado: ", NamedTextColor.LIGHT_PURPLE).append(Component.text(var5.label(), NamedTextColor.AQUA)));
               }
            } else if (var9 == 30 && var2.detailId != null && var2.detailId.startsWith("tradegear:")) {
               this.trade(var8);
            } else {
               if (var9 == 49) {
                  this.recipes(var8, var2.page);
               }
            }
   }

   private void get(CommandSender var1, String[] var2) {
      if (var2.length > 1 && var2[1].equalsIgnoreCase("rare")) {
         this.giveRare(var1, var2, 2);
      } else if (var1 instanceof Player var3) {
         if (!this.isAdmin(var1, "rareitemhunter.admin.essence")) {
            this.deny(var1);
         } else if (var2.length < 2) {
            var1.sendMessage("Uso: /ri get <compass|essence|fragment|tribute|rare <id>>");
         } else {
            String var4 = var2[1].toLowerCase();
            switch (var4) {
               case "compass":
               case "brujula":
                  var3.getInventory().addItem(new ItemStack[]{this.items.compass()});
                  break;
               case "essence":
               case "esencia":
                  var3.getInventory().addItem(new ItemStack[]{this.items.essence()});
                  break;
               case "fragment":
               case "fragmento":
                  var3.getInventory().addItem(new ItemStack[]{this.items.fragment(1)});
                  break;
               case "tribute":
               case "tributo":
                  var3.getInventory().addItem(new ItemStack[]{this.items.tribute("Coloso")});
                  break;
               case "hacha":
                  var3.getInventory().addItem(new ItemStack[]{this.items.rare("hacha_sismica")});
                  break;
               case "espada":
                  var3.getInventory().addItem(new ItemStack[]{this.items.rare("espada_brezal")});
                  break;
               case "arco":
                  var3.getInventory().addItem(new ItemStack[]{this.items.rare("arco_ceniciento")});
                  break;
               default:
                  var1.sendMessage("Objetos: compass, essence, fragment, tribute, rare <id>.");
            }
         }
      } else {
         var1.sendMessage("Solo un jugador puede recibir objetos.");
      }
   }

   private void giveRare(CommandSender var1, String[] var2, int var3) {
      if (!this.isAdmin(var1, "rareitemhunter.admin.essence")) {
         this.deny(var1);
      } else if (var2.length <= var3) {
         var1.sendMessage("Rares: " + String.join(", ", this.items.rareIds()) + ". Uso: /ri rare <id> [nivel 1-5] [jugador]");
      } else {
         String var4 = var2[var3].toLowerCase();
         String var5 = this.items.rareTier(var4);
         if (var5 == null) {
            var1.sendMessage("Rare no encontrada. Usa /ri rares.");
         } else {
            int var6 = var3 + 1;
            int var7 = 1;
            if (var2.length > var6) {
               try {
                  var7 = Integer.parseInt(var2[var6]);
                  var6++;
               } catch (NumberFormatException var10) {
               }
            }

            if (var7 >= 1 && var7 <= 5) {
               Player var8 = var2.length > var6 ? this.plugin.getServer().getPlayerExact(var2[var6]) : (var1 instanceof Player var9 ? var9 : null);
               if (var8 == null) {
                  var1.sendMessage("Uso: /ri rare <id> [nivel 1-5] [jugador]");
               } else {
                  HashMap<Integer, ItemStack> var11 = var8.getInventory().addItem(new ItemStack[]{this.items.rare(var4, var7)});
                  var11.values().forEach(var1x -> var8.getWorld().dropItemNaturally(var8.getLocation(), var1x));
                  var1.sendMessage(
                     Component.text("Entregaste la rare " + var4 + " nivel " + var7 + " (" + var5 + ") a " + var8.getName() + ".", NamedTextColor.GOLD)
                  );
               }
            } else {
               var1.sendMessage("El nivel de la rare debe ser del 1 al 5.");
            }
         }
      }
   }

   private void listRares(CommandSender var1) {
      var1.sendMessage(Component.text("Rares disponibles • /ri rare <id> [nivel 1-5] [jugador]", NamedTextColor.LIGHT_PURPLE));

      for (String var3 : this.items.rareIds()) {
         var1.sendMessage(Component.text(var3 + " — " + this.items.rareTier(var3), NamedTextColor.GOLD));
      }
   }

   private void reload(CommandSender var1) {
      if (!this.isAdmin(var1, "rareitemhunter.admin.reload")) {
         this.deny(var1);
      } else {
         this.plugin.reloadConfig();
         ((CazaJefesPlugin)this.plugin).recipeSettings.reload();
         this.items.reloadRares();
         var1.sendMessage(Component.text("Configuración recargada.", NamedTextColor.GREEN));
      }
   }

   private void listEggs(CommandSender var1) {
      List<BossManager.TrackedEncounter> var2 = this.bosses.trackedEncounters().stream().filter(BossManager.TrackedEncounter::egg).toList();
      if (var2.isEmpty()) {
         var1.sendMessage("No hay huevos activos.");
      } else {
         var1.sendMessage(Component.text("Huevos activos: " + var2.size(), NamedTextColor.LIGHT_PURPLE));

         for (BossManager.TrackedEncounter var4 : var2) {
            var1.sendMessage(
               Component.text(
                  var4.name()
                     + " — "
                     + var4.location().getWorld().getName()
                     + " "
                     + var4.location().getBlockX()
                     + ", "
                     + var4.location().getBlockY()
                     + ", "
                     + var4.location().getBlockZ(),
                  NamedTextColor.GRAY
               )
            );
         }
      }
   }

   private void bossCatalog(CommandSender var1) {
      if (var1 instanceof Player var2) {
         this.bossCatalog(var2, 0);
      } else {
         var1.sendMessage("Jefes disponibles: " + BossCatalog.all().size() + ". Abre /ri bosses dentro del juego para ver el catálogo visual.");
      }
   }

   private void bossCatalog(Player player, int page) { ((CazaJefesPlugin)plugin).bestiary.open(player); }

   private Material bossIcon(EntityType var1) {
      return switch (var1) {
         case ZOMBIE, DROWNED, HUSK, ZOMBIFIED_PIGLIN, ZOGLIN -> Material.ZOMBIE_HEAD;
         case SKELETON, WITHER_SKELETON, STRAY, BOGGED -> Material.SKELETON_SKULL;
         case SPIDER, CAVE_SPIDER, ENDERMAN, ENDERMITE, BLAZE, MAGMA_CUBE, WARDEN, PHANTOM, GUARDIAN, ELDER_GUARDIAN, RAVAGER, SHULKER, SLIME, GHAST, WITCH, PILLAGER, VINDICATOR, EVOKER, IRON_GOLEM, BREEZE, MOOSHROOM, CREAKING -> Material.PLAYER_HEAD;
         case ENDER_DRAGON -> Material.DRAGON_HEAD;
         case WITHER -> Material.WITHER_SKELETON_SKULL;
         case CREEPER -> Material.CREEPER_HEAD;
         case PIGLIN_BRUTE, PIGLIN, HOGLIN -> Material.PIGLIN_HEAD;
         case BEE, FOX, SNIFFER -> Material.PLAYER_HEAD;
         default -> Material.PLAYER_HEAD;
      };
   }

   private ItemStack bossHead(BossCatalog.Definition var1) {
      boolean var2 = var1.entityType() == EntityType.BEE
         || var1.entityType() == EntityType.FOX
         || var1.entityType() == EntityType.HOGLIN
         || var1.entityType() == EntityType.SNIFFER
         || var1.entityType() == EntityType.SLIME
         || var1.entityType() == EntityType.MAGMA_CUBE
         || var1.entityType() == EntityType.HUSK
         || var1.entityType() == EntityType.DROWNED
         || var1.entityType() == EntityType.PILLAGER
         || var1.entityType() == EntityType.VINDICATOR
         || var1.entityType() == EntityType.WITCH;
      ItemStack var3 = new ItemStack(var2 ? Material.PLAYER_HEAD : this.bossIcon(var1.entityType()));
      if (var3.getType() == Material.PLAYER_HEAD) {
         String var4 = switch (var1.entityType()) {
            case DROWNED -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzgyMWYwZjMxZmY4Njg2ZGJmYWJhNTZkZTQxNzRkMWVkZDQ4MTU5ZDZiYWVkODc0NmZjOWUyYTJiM2RhZjhmZCJ9fX0=";
            case HUSK -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDY3NGM2M2M4ZGI1ZjRjYTYyOGQ2OWEzYjFmOGEzNmUyOWQ4ZmQ3NzVlMWE2YmRiNmNhYmI0YmU0ZGIxMjEifX19";
            default -> null;
            case SPIDER, CAVE_SPIDER -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjkyOWEzZWRkYjllMGM3MmZiOWZlZDAyZDgzYzhlY2Y1YWE1Mjk4M2Q3YTM1NzBhMjYxYjk3NTFjMTFiODFhZCJ9fX0=";
            case ENDERMAN, ENDERMITE -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGExMDhhMGE3YTM4Nzg1OWYyYzQ0ZmI5NzAyY2Y3M2RiYWZlZTNlY2ZkYzRmNWRlZjQ2YzBkNjUxYjdhNDlmNyJ9fX0=";
            case BLAZE -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzM3NjIzZjc5ZjdlYjRmM2Y4MGRhNjViNjUyY2M0NGIyMTQ4ZWVhNDFmOWZmZTJlODZhMjNiZGY0OWFiNzdiMSJ9fX0=";
            case MAGMA_CUBE -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWFhOTIyOGJjN2FjOTRiMzYzMWZmMGY0M2RiNmI0ZTJkMGY1MGExMDdmNTkyNTM4YWU3MDRlMTk3ZmUxMjM5ZCJ9fX0=";
            case WARDEN -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTNjNDk3MDUxMGZiMGY5OWJlM2YwYzNkNWE1OTE5YzZlZWVmMDRlNDMzMTIwZTIwMTA3YzY2YWJhNjc1YTliNyJ9fX0=";
            case PHANTOM -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2U5NTE1M2VjMjMyODRiMjgzZjAwZDE5ZDI5NzU2ZjI0NDMxM2EwNjFiNzBhYzAzYjk3ZDIzNmVlNTdiZDk4MiJ9fX0=";
            case GUARDIAN, ELDER_GUARDIAN -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzI4ZGI2YjA1NDIzNWU0NTFkNjY2ZmM2NDRhMDg2NjMyYzZhYjIyYzdjZDUzNTY1YWU4MjZlMWQ1Y2MwYjE3In19fQ==";
            case RAVAGER -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2QzNjZlYTMyNWY1NmFlMmY3MjE1NTM1NTkzZWQ2NjcxMTY0NzAzZDM4ZTYwMGQ4MGQyYmFlYzU5NWI5NGU4MyJ9fX0=";
            case SHULKER -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjJjNjQyY2ZlMzM4MTQ3NjdkNjkyZDI3NTk5YzhiZWY0ZjVhMzA2ZmMyMTBkNGI1MGE1ODBiNzA0MGYwMmIxOCJ9fX0=";
            case SLIME -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2U5Mzk1NGYyZWRlYmQzZmQxNjNlMjA5NzRiNDU0MGU1MWI4NzBjY2UyMTYzMmMzNWNiNTViZDcxZmMzMDUzZSJ9fX0=";
            case GHAST -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGU4YTM4ZTlhZmJkM2RhMTBkMTliNTc3YzU1YzdiZmQ2YjRmMmU0MDdlNDRkNDAxN2IyM2JlOTE2N2FiZmYwMiJ9fX0=";
            case WITCH -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmNlNjYwNDE1N2ZjNGFiNTA5MWU0YmNmNTA3YTc0OTkxOGVlOWM0MWUzNTdkNDczNzZlMGVlNzM0MjA3NGM5MCJ9fX0=";
            case PILLAGER -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGFlZTZiYjM3Y2JmYzkyYjBkODZkYjVhZGE0NzkwYzY0ZmY0NDY4ZDY4Yjg0OTQyZmRlMDQ0MDVlOGVmNTMzMyJ9fX0=";
            case VINDICATOR -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGFlZWQ5ZDhlZDE3NjllNzdlM2NmZTExZGMxNzk2NjhlZDBkYjFkZTZjZTI5ZjFjOGUwZDVmZTVlNjU3M2I2MCJ9fX0=";
            case EVOKER -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQzMzMyMmUyY2NiZDljNTVlZjQxZDk2ZjM4ZGJjNjY2YzgwMzA0NWIyNDM5MWFjOTM5MWRjY2FkN2NkIn19fQ==";
            case BREEZE -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTI3NTcyOGFmN2U2YTI5Yzg4MTI1YjY3NWEzOWQ4OGFlOTkxOWJiNjFmZGMyMDAzMzdmZWQ2YWIwYzQ5ZDY1YyJ9fX0=";
            case CREAKING -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWM4NGNhZWZkOWZkOTBiYWM2OGQyOTU1ZjMxZjgyNTMwZTA4MGQ4Mzk2OWZlM2Q2MmY0NzY0ZWFmOTZmYTVlIn19fQ==";
            case HOGLIN -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWJiOWJjMGYwMWRiZDc2MmEwOGQ5ZTc3YzA4MDY5ZWQ3Yzk1MzY0YWEzMGNhMTA3MjIwODU2MWI3MzBlOGQ3NSJ9fX0=";
            case BEE -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmQxNjU4ZmQ0OTRmOGUyMjkwZGI0MWRjNGQxMWQ0NjdjMjU5NjFlZGNhNjMzMTdlOGY5OTcxZWIyOGE0N2NjNSJ9fX0=";
            case FOX -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzRjNzU4Zjc0OTUyN2M3NzYyZTNmOTg2MTNkYTk2MWMwYmQyMzEzNjAwMWVhOWRhZmI5ZjYxN2RhN2MxMTQ0ZSJ9fX0=";
            case SNIFFER -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDNmM2JlMDlhNzM1M2VlYWU5NGQ4ODMyMGNiNWIyNDJkZTJmNzE5YzBlNWMxNmE0ODYzMjdjNjA1ZGIxZDQ2MyJ9fX0=";
         };
         if (var4 != null && var3.getItemMeta() instanceof SkullMeta var5) {
            PlayerProfile var7 = Bukkit.createProfile(
               UUID.nameUUIDFromBytes(var1.entityType().name().getBytes(StandardCharsets.UTF_8)), var1.entityType().name()
            );
            var7.setProperty(new ProfileProperty("textures", var4));
            var5.setPlayerProfile(var7);
            var3.setItemMeta(var5);
         }
      }

      return var3;
   }

   private void openBossDetail(Player var1, String var2, int var3) {
      BossCatalog.Definition var4 = BossCatalog.get(var2);
      if (var4 != null) {
         CazaJefesCommand.MenuHolder var5 = new CazaJefesCommand.MenuHolder("boss_detail", var3, var2);
         Inventory var6 = Bukkit.createInventory(var5, 54, Component.text("FICHA DE JEFE · " + var4.tier().display.toUpperCase(), NamedTextColor.DARK_PURPLE));
         var5.inventory = var6;
         this.fillPremiumFrame(var6);
         var6.setItem(4, this.menuItem(Material.NETHER_STAR, "§d§lREGISTRO DE CACERÍA", "§7Consulta la clasificación y los datos base."));
         ItemStack var7 = this.bossHead(var4);
         ItemMeta var8 = var7.getItemMeta();
         var8.displayName(
            ((TextComponent)Component.text(var4.name(), var4.tier().color).decorate(TextDecoration.BOLD)).decoration(TextDecoration.ITALIC, false)
         );
         var8.lore(
            List.of(
               (TextComponent)Component.text("Rango: " + var4.tier().display, var4.tier().color).decoration(TextDecoration.ITALIC, false),
               (TextComponent)Component.text("Criatura: " + var4.entityType().name(), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
               (TextComponent)Component.text(var4.description(), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false),
               (TextComponent)Component.text(var4.mini() ? "Encuentro: mini jefe" : "Encuentro: jefe principal", NamedTextColor.AQUA)
                  .decoration(TextDecoration.ITALIC, false)
            )
         );
         var7.setItemMeta(var8);
         var6.setItem(22, var7);
         var6.setItem(30, this.menuItem(Material.WRITABLE_BOOK, "§b§lDESCRIPCIÓN", "§7" + var4.description()));
         if (this.isAdmin(var1, "rareitemhunter.admin.egg")) {
            var6.setItem(
               32, this.menuItem(Material.WRITABLE_BOOK, "§b§lCÓDIGO DEL ENCUENTRO", "§7ID: §f" + var4.id(), "§7Huevo: §f/ri egg " + var4.id() + " here")
            );
            var6.setItem(40, this.menuItem(Material.DRAGON_EGG, "§c§lINVOCAR HUEVO DE PRUEBA", "§7Pulsa para colocarlo junto a ti."));
         } else {
            var6.setItem(
               32,
               this.menuItem(Material.WRITABLE_BOOK, "§b§lDATOS DEL ENCUENTRO", "§7Rango: §f" + var4.tier().display, "§7Consulta su descripción y criatura.")
            );
         }

         var6.setItem(49, this.menuItem(Material.ARROW, "§d§lVOLVER AL BESTIARIO", "§7Regresar a la página " + (var3 + 1)));
         var1.openInventory(var6);
      }
   }

   private Location locationArg(Player var1, String var2, boolean var3) {
      if (var2.equalsIgnoreCase("here")) {
         Block var17 = var1.getTargetBlockExact(10);
         if (var17 != null) {
            return var17.getRelative(BlockFace.UP).getLocation();
         } else {
            Location var18 = var1.getLocation().getBlock().getLocation();
            return var3 ? var18.add(0.0, 1.0, 0.0) : var1.getLocation().add(0.0, 0.2, 0.0);
         }
      } else {
         String var4 = "spawnpoints." + var2.toLowerCase();
         String var5 = this.plugin.getConfig().getString(var4 + ".world");
         World var6 = var5 == null ? null : this.plugin.getServer().getWorld(var5);
         if (var6 == null) {
            return null;
         } else {
            int var7 = this.plugin.getConfig().getInt(var4 + ".x");
            int var8 = this.plugin.getConfig().getInt(var4 + ".z");
            int var9 = Math.max(8, this.plugin.getConfig().getInt(var4 + ".radius", 80));
            double var10 = ThreadLocalRandom.current().nextDouble(0.0, Math.PI * 2);
            double var12 = Math.sqrt(ThreadLocalRandom.current().nextDouble()) * var9;
            int var14 = var7 + (int)Math.round(Math.cos(var10) * var12);
            int var15 = var8 + (int)Math.round(Math.sin(var10) * var12);
            Location var16 = var6.getHighestBlockAt(var14, var15).getRelative(BlockFace.UP).getLocation();
            if (!var3) {
               var16.add(0.0, 0.2, 0.0);
            }

            return var16;
         }
      }
   }

   private boolean isAdmin(CommandSender var1, String var2) {
      return var1.hasPermission(var2) || var1.hasPermission("rareitemhunter.admin.*") || var1.hasPermission("cazajefes.admin");
   }

   private double parseDouble(String var1, double var2) {
      try {
         return Double.parseDouble(var1);
      } catch (NumberFormatException var5) {
         return var2;
      }
   }

   private void deny(CommandSender var1) {
      var1.sendMessage(Component.text("No tienes permiso para usar ese comando.", NamedTextColor.RED));
   }

   private void help(CommandSender var1) {
      var1.sendMessage(
         ((TextComponent)Component.text("━━━━━━━━━━ ", NamedTextColor.DARK_PURPLE)
               .append(Component.text("CAZAJEFES · COMANDOS", NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD)))
            .append(Component.text(" ━━━━━━━━━━", NamedTextColor.DARK_PURPLE))
      );
      var1.sendMessage(Component.text("JUGADOR", NamedTextColor.AQUA).decorate(TextDecoration.BOLD));
      var1.sendMessage(
         Component.text("/ri bestiario", NamedTextColor.WHITE).append(Component.text("  Consulta los jefes, rangos y descripciones", NamedTextColor.GRAY))
      );
      var1.sendMessage(Component.text("/ri recipes", NamedTextColor.WHITE).append(Component.text("  Abre recetas de equipo y esencias", NamedTextColor.GRAY)));
      var1.sendMessage(Component.text("/ri canjear", NamedTextColor.WHITE).append(Component.text("  Intercambia botín por recompensas", NamedTextColor.GRAY)));
      var1.sendMessage(Component.text("/ri catalogo", NamedTextColor.WHITE).append(Component.text("  Consulta esencias y reliquias", NamedTextColor.GRAY)));
      var1.sendMessage(Component.text("/ri compass", NamedTextColor.WHITE).append(Component.text("  Recibe o usa la Brújula de Caza", NamedTextColor.GRAY)));
      var1.sendMessage(
         Component.text("/ri rares · /ri enchants", NamedTextColor.WHITE).append(Component.text("  Lista de propiedades disponibles", NamedTextColor.GRAY))
      );
      if (this.isAdmin(var1, "rareitemhunter.admin.essence")) {
         var1.sendMessage(Component.text("OBJETOS Y FORJA", NamedTextColor.GOLD).decorate(TextDecoration.BOLD));
         var1.sendMessage(Component.text("/ri book <rare> <nivel> [jugador]", NamedTextColor.WHITE));
         var1.sendMessage(Component.text("/ri rare <id> [nivel] [jugador]", NamedTextColor.WHITE));
         var1.sendMessage(Component.text("/ri essence · /ri fragment · /ri tribute", NamedTextColor.WHITE));
         var1.sendMessage(Component.text("/ri armor <rango> [jugador] · /ri weapon <rango> [jugador]", NamedTextColor.WHITE));
         var1.sendMessage(Component.text("/ri get <objeto> · /ri give rare <id> [nivel] [jugador]", NamedTextColor.WHITE));
      }

      if (this.isAdmin(var1, "rareitemhunter.admin.egg")
         || this.isAdmin(var1, "rareitemhunter.admin.boss")
         || this.isAdmin(var1, "rareitemhunter.admin.spawnpoint")) {
         var1.sendMessage(Component.text("ADMINISTRACIÓN", NamedTextColor.RED).decorate(TextDecoration.BOLD));
         if (this.isAdmin(var1, "rareitemhunter.admin.egg")) {
            var1.sendMessage(Component.text("/ri egg <jefe> [here|punto] · /ri eggs", NamedTextColor.WHITE));
         }

         if (this.isAdmin(var1, "rareitemhunter.admin.boss")) {
            var1.sendMessage(Component.text("/ri boss <jefe> [here|punto]", NamedTextColor.WHITE));
         }

         if (this.isAdmin(var1, "rareitemhunter.admin.spawnpoint")) {
            var1.sendMessage(Component.text("/ri spawnpoint <nombre> [radio] · /ri spawnpoint delete <nombre>", NamedTextColor.WHITE));
         }

         if (this.isAdmin(var1, "rareitemhunter.admin.reload")) {
            var1.sendMessage(Component.text("/ri reload", NamedTextColor.WHITE));
         }
      }

      var1.sendMessage(Component.text("Usa Tab para autocompletar subcomandos, jefes, rares, rangos y jugadores.", NamedTextColor.DARK_GRAY));
   }

   private record GearTierSpec(String key, String display, BossCatalog.Tier tier, int fragments, int tributes, int cores) {
   }

   private static final class MenuHolder implements InventoryHolder {
      final MenuActionGate gate = new MenuActionGate();
      private final String mode;
      private final int page;
      private final String detailId;
      private final Map<Integer, CazaJefesCommand.TradeOffer> offersBySlot = new HashMap<>();
      private final Map<Integer, String> recipeIdsBySlot = new HashMap<>();
      private final Map<Integer, String> catalogIdsBySlot = new HashMap<>();
      private final Map<Integer, String> bossIdsBySlot = new HashMap<>();
      private Inventory inventory;

      private MenuHolder(String var1) {
         this(var1, 0, null);
      }

      private MenuHolder(String var1, int var2) {
         this(var1, var2, null);
      }

      private MenuHolder(String var1, int var2, String var3) {
         this.mode = var1;
         this.page = var2;
         this.detailId = var3;
      }

      public Inventory getInventory() {
         return this.inventory;
      }
   }

   record TradeOffer(ItemStack reward, int fragments, int tributes, String itemPrefix, int itemAmount, String label) {
      private TradeOffer(ItemStack var1, int var2, int var3, String var4) {
         this(var1, var2, var3, "", 0, var4);
      }
   }

   private record VisualRecipe(String id, String title, ItemStack icon, ItemStack result, String[] shape, Map<Character, ItemStack> ingredients) {
   }
}
