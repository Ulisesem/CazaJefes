package dev.cazajefes;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class CazaJefesPlugin extends JavaPlugin {
   RecipeSettings recipeSettings;
   CombatGuard guard;
   private NamespacedKey itemKey;
   private BossManager bossManager;
   private RelicItems relicItems;
   private HuntTracker tracker;

   public void onEnable() {
      this.saveDefaultConfig();
      this.recipeSettings = new RecipeSettings(this);
      this.guard = new CombatGuard(this);
      getServer().getPluginManager().registerEvents(guard, this);
      this.itemKey = new NamespacedKey(this, "relic_id");
      this.relicItems = new RelicItems(this, this.itemKey);
      this.bossManager = new BossManager(this, this.itemKey, this.relicItems);
      this.getServer().getPluginManager().registerEvents(this.bossManager, this);
      this.getServer().getPluginManager().registerEvents(this.relicItems, this);
      this.tracker = new HuntTracker(this, this.bossManager, this.relicItems);
      this.getServer().getPluginManager().registerEvents(this.tracker, this);
      CazaJefesCommand var1 = new CazaJefesCommand(this, this.bossManager, this.relicItems);
      this.getCommand("ri").setExecutor(var1);
      this.getCommand("ri").setTabCompleter(var1);
      this.getServer().getPluginManager().registerEvents(var1, this);
      this.relicItems.registerRecipes();
      RaresMenu raresMenu = new RaresMenu(this, this.relicItems);
      this.getCommand("rares").setExecutor(raresMenu);
      this.getServer().getPluginManager().registerEvents(raresMenu, this);
      this.getLogger().info("CazaJefes está listo.");
   }

   BossManager bosses() { return bossManager; }

   public void onDisable() {
      if (guard != null) guard.cleanup();
      if (this.bossManager != null) {
         this.bossManager.stopAllAttacks();
      }

      if (this.tracker != null) {
         this.tracker.stop();
      }
   }
}
