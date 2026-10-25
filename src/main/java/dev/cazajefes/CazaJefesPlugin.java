package dev.cazajefes;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class CazaJefesPlugin extends JavaPlugin {
   BestiaryMenu bestiary;
   TrophyMenu trophies;
   MarketMenu market;
   RecipeSettings recipeSettings;
   CombatGuard guard;
   ShulkerCombat shulkers;
   BossAbilities abilities;
   BossParticipation participation;
   private NamespacedKey itemKey;
   private BossManager bossManager;
   private RelicItems relicItems;
   private HuntTracker tracker;

   public void onEnable() {
      this.saveDefaultConfig();
      ConfigMigration.run(this);
      this.recipeSettings = new RecipeSettings(this);
      this.guard = new CombatGuard(this);
      getServer().getPluginManager().registerEvents(guard, this);
      this.participation=new BossParticipation(this);getServer().getPluginManager().registerEvents(participation,this);
      this.itemKey = new NamespacedKey(this, "relic_id");
      this.relicItems = new RelicItems(this, this.itemKey);
      getServer().getPluginManager().registerEvents(new AquaticCombat(),this);
      this.abilities=new BossAbilities(this);getServer().getPluginManager().registerEvents(abilities,this);
      this.shulkers=new ShulkerCombat(this);getServer().getPluginManager().registerEvents(shulkers,this);
      this.bossManager = new BossManager(this, this.itemKey, this.relicItems);
      this.getServer().getPluginManager().registerEvents(this.bossManager, this);
      this.getServer().getPluginManager().registerEvents(this.relicItems, this);
      this.tracker = new HuntTracker(this, this.bossManager, this.relicItems);
      this.getServer().getPluginManager().registerEvents(this.tracker, this);
      CazaJefesCommand var1 = new CazaJefesCommand(this, this.bossManager, this.relicItems);
      this.market=new MarketMenu(this,relicItems,var1);
      this.getServer().getPluginManager().registerEvents(market,this);
      this.getCommand("mercado").setExecutor(market);
      EquipmentCommand equipmentCommand=new EquipmentCommand(relicItems);
      this.getCommand("riequipo").setExecutor(equipmentCommand);this.getCommand("riequipo").setTabCompleter(equipmentCommand);
      this.getCommand("ri").setExecutor(var1);
      this.getCommand("ri").setTabCompleter(var1);
      this.getServer().getPluginManager().registerEvents(var1, this);
      this.relicItems.registerRecipes();
      this.bestiary=new BestiaryMenu(this,bossManager,relicItems);
      this.getCommand("bestiario").setExecutor(bestiary);
      this.getServer().getPluginManager().registerEvents(bestiary,this);
      this.getServer().getPluginManager().registerEvents(new CombatPhases(this),this);
      this.getServer().getPluginManager().registerEvents(new LightningRetaliation(this),this);
      this.getServer().getPluginManager().registerEvents(new WorldMobLoot(this,relicItems),this);
      this.getServer().getPluginManager().registerEvents(new CurrencyMigration(this,relicItems),this);
      this.trophies=new TrophyMenu(this,relicItems);
      this.getServer().getPluginManager().registerEvents(trophies,this);
      RaresMenu raresMenu = new RaresMenu(this, this.relicItems);
      this.getCommand("rares").setExecutor(raresMenu);
      this.getServer().getPluginManager().registerEvents(raresMenu, this);
      this.getLogger().info("CazaJefes está listo.");
   }

   BossManager bosses() { return bossManager; }

   public void onDisable() {
      if(abilities!=null)abilities.stop();
      if (shulkers!=null)shulkers.stop();
      if (guard != null) guard.cleanup();
      if (this.bossManager != null) {
         this.bossManager.stopAllAttacks();
      }

      if (this.tracker != null) {
         this.tracker.stop();
      }
   }
}
