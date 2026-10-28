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
   PillagerCombat pillagers;
   BossAbilities abilities;
   BossAuras auras;
   AnimalMinions animalMinions;
   BurstLaunch burstLaunch;
   BossSpawnpoints spawnpoints;
   BossRebirth rebirth;
   BossDeathVisuals deathVisuals;
   DamageDiagnostics damageDiagnostics;
   BossParticipation participation;BossStatistics statistics;EncounterSafety safety;DuelRelics duel;BossSpecies species;SkeletonCombat archers;MageCombat mages;
   public BossStatistics statistics(){return statistics;}
   private NamespacedKey itemKey;
   private BossManager bossManager;
   private RelicItems relicItems;
   private HuntTracker tracker;

   public void onEnable() {
      this.saveDefaultConfig();BossWarnings.start(this);
      ConfigMigration.run(this);
      this.recipeSettings = new RecipeSettings(this);
      statistics=new BossStatistics(this);getServer().getServicesManager().register(BossStatistics.class,statistics,this,org.bukkit.plugin.ServicePriority.Normal);
      this.guard = new CombatGuard(this);
      getServer().getPluginManager().registerEvents(guard, this);
      this.participation=new BossParticipation(this);getServer().getPluginManager().registerEvents(participation,this);
      this.itemKey = new NamespacedKey(this, "relic_id");
      this.relicItems = new RelicItems(this, this.itemKey);
      getServer().getPluginManager().registerEvents(new AquaticCombat(),this);
      getServer().getPluginManager().registerEvents(new BossIntegrity(),this);
      burstLaunch=new BurstLaunch(this);getServer().getPluginManager().registerEvents(burstLaunch,this);
      animalMinions=new AnimalMinions(this);getServer().getPluginManager().registerEvents(animalMinions,this);
      species=new BossSpecies();getServer().getPluginManager().registerEvents(species,this);
      this.abilities=new BossAbilities(this);getServer().getPluginManager().registerEvents(abilities,this);
      pillagers=new PillagerCombat(this);getServer().getPluginManager().registerEvents(pillagers,this);
      archers=new SkeletonCombat(this);getServer().getPluginManager().registerEvents(archers,this);
      this.shulkers=new ShulkerCombat(this);getServer().getPluginManager().registerEvents(shulkers,this);
      this.deathVisuals=new BossDeathVisuals(this);getServer().getPluginManager().registerEvents(deathVisuals,this);
      this.rebirth=new BossRebirth(this);getServer().getPluginManager().registerEvents(rebirth,this);
      this.auras=new BossAuras(this);getServer().getPluginManager().registerEvents(auras,this);
      mages=new MageCombat(this);getServer().getPluginManager().registerEvents(mages,this);
      safety=new EncounterSafety(this);getServer().getPluginManager().registerEvents(safety,this);
      this.bossManager = new BossManager(this, this.itemKey, this.relicItems);
      this.getServer().getPluginManager().registerEvents(this.bossManager, this);
      this.getServer().getPluginManager().registerEvents(this.relicItems, this);
      this.tracker = new HuntTracker(this, this.bossManager, this.relicItems);
      this.getServer().getPluginManager().registerEvents(this.tracker, this);
      spawnpoints=new BossSpawnpoints(this);getServer().getPluginManager().registerEvents(spawnpoints,this);
      CazaJefesCommand var1 = new CazaJefesCommand(this, this.bossManager, this.relicItems);
      this.market=new MarketMenu(this,relicItems,var1);
      this.getServer().getPluginManager().registerEvents(market,this);
      this.getCommand("mercado").setExecutor(market);
      EquipmentCommand equipmentCommand=new EquipmentCommand(relicItems);
      this.getCommand("riequipo").setExecutor(equipmentCommand);this.getCommand("riequipo").setTabCompleter(equipmentCommand);
      this.getCommand("ri").setExecutor(var1);
      this.getCommand("ri").setTabCompleter(var1);
      this.getServer().getPluginManager().registerEvents(var1, this);
      duel=new DuelRelics(this,relicItems);getServer().getPluginManager().registerEvents(duel,this);
      this.relicItems.registerRecipes();
      this.bestiary=new BestiaryMenu(this,bossManager,relicItems);
      this.getCommand("bestiario").setExecutor(bestiary);
      this.getServer().getPluginManager().registerEvents(bestiary,this);
      this.getServer().getPluginManager().registerEvents(new CombatPhases(this),this);
      getServer().getPluginManager().registerEvents(new BossDamageProgress(this),this);
      damageDiagnostics=new DamageDiagnostics(this);getServer().getPluginManager().registerEvents(damageDiagnostics,this);
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

   RelicItems items(){return relicItems;}

   BossManager bosses() { return bossManager; }

   public void onDisable() {
      BossEffects.clear();BossWarnings.clear();if(relicItems!=null)relicItems.stopArmorRares();if(mages!=null)mages.stop();if(archers!=null)archers.stop();if(species!=null)species.stop();if(duel!=null)duel.stop();if(safety!=null)safety.stop();if(statistics!=null)statistics.stop();if(pillagers!=null)pillagers.stop();
      if(burstLaunch!=null)burstLaunch.stop();
      if(animalMinions!=null)animalMinions.stop();
      if(spawnpoints!=null)spawnpoints.stop();
      if(deathVisuals!=null)deathVisuals.stop();
      if(relicItems!=null)relicItems.stopLootAnimations();
      CosmeticFX.clear();if(auras!=null)auras.stop();
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
