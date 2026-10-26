package dev.cazajefes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

public final class BossManager implements Listener {
   private final JavaPlugin plugin;
   private final NamespacedKey bossKey;
   private final NamespacedKey encounterKey;
   private final NamespacedKey typeKey;
   private final NamespacedKey awakenerKey;
   private final NamespacedKey minionKey;
   private final RelicItems relicItems;
   private final Map<UUID, BossManager.Encounter> encounters = new HashMap<>();
   private final Map<String, BossManager.Encounter> eggsByBlock = new HashMap<>();
   private final Map<UUID, BukkitTask> attacks = new HashMap<>();
   private final Map<UUID, BukkitTask> barTasks = new HashMap<>();
   private final Map<UUID, BukkitTask> eggEffects = new HashMap<>();
   private final Map<UUID, BossBar> healthBars = new HashMap<>();

   public BossManager(JavaPlugin var1, NamespacedKey var2, RelicItems var3) {
      this.plugin = var1;
      this.relicItems = var3;
      this.bossKey = new NamespacedKey(var1, BossKeys.BOSS);
      this.encounterKey = new NamespacedKey(var1, "encounter_id");
      this.typeKey = new NamespacedKey(var1, "boss_type");
      this.awakenerKey = new NamespacedKey(var1, "awakened_by");
      this.minionKey = new NamespacedKey(var1, "boss_minion");
      this.restoreEggs();
      var1.getServer().getScheduler().runTask(var1, this::normalizeLoadedCombatants);
   }

   private void normalizeLoadedCombatants() {
      for (World var2 : this.plugin.getServer().getWorlds()) {
         for (Entity var4 : var2.getEntities()) {
            if (var4 instanceof LivingEntity var5) {
               PersistentDataContainer var6 = var5.getPersistentDataContainer();
               if (var6.has(this.bossKey, PersistentDataType.BYTE)) {
                  var5.removePotionEffect(PotionEffectType.RESISTANCE);
                  AttributeInstance var7 = var5.getAttribute(Attribute.ARMOR);
                  if (var7 != null) {
                     var7.setBaseValue(0.0);
                  }

                  resumeBoss(var5);
                  var5.setInvulnerable(false);
                  if (var5 instanceof Mob var8) {
                     var8.setAI(true);
                  }
               } else if (var6.has(this.minionKey, PersistentDataType.BYTE)) {
                  var5.removePotionEffect(PotionEffectType.RESISTANCE);
                  AttributeInstance var9 = var5.getAttribute(Attribute.ARMOR);
                  if (var9 != null) {
                     var9.setBaseValue(0.0);
                  }

                  AttributeInstance var10 = var5.getAttribute(Attribute.KNOCKBACK_RESISTANCE);
                  if (var10 != null) {
                     var10.setBaseValue(0.1);
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void onChunkLoad(ChunkLoadEvent event) {
      for (Entity entity:event.getChunk().getEntities()) if (entity instanceof LivingEntity living
          && entity.getPersistentDataContainer().has(bossKey,PersistentDataType.BYTE)) resumeBoss(living);
   }
   @EventHandler
   public void onChunkUnload(org.bukkit.event.world.ChunkUnloadEvent event) {
      for (Entity entity:event.getChunk().getEntities()) if (entity instanceof LivingEntity living
          && entity.getPersistentDataContainer().has(bossKey,PersistentDataType.BYTE)) {
         try {
            UUID id=UUID.fromString(entity.getPersistentDataContainer().get(encounterKey,PersistentDataType.STRING));
            BukkitTask task=attacks.remove(id);if(task!=null)task.cancel();removeBossBar(id);encounters.remove(id);
            living.setInvulnerable(false);living.setAI(true);
         } catch (IllegalArgumentException invalid) { plugin.getLogger().warning("Boss con encuentro inválido al descargar chunk"); }
      }
   }
   private void resumeBoss(LivingEntity boss) {
      PersistentDataContainer data=boss.getPersistentDataContainer();
      BossCatalog.Definition definition=BossCatalog.get(data.get(typeKey,PersistentDataType.STRING));
      if (definition==null) return;
      try {
         UUID id=UUID.fromString(data.get(encounterKey,PersistentDataType.STRING));
         data.set(typeKey,PersistentDataType.STRING,definition.id()); boss.customName(bossDisplay(definition));
         encounters.put(id,new Encounter(id,definition,boss.getLocation(),boss,data.getOrDefault(awakenerKey,PersistentDataType.STRING,"")));
         startBossSystems(id,boss,definition,plugin.getConfig().getLong("bosses.attack-interval-ticks",10));
      } catch (IllegalArgumentException invalid) { plugin.getLogger().warning("Boss con identificador de encuentro inválido: " + boss.getUniqueId()); }
   }

   public List<String> bossIds() {
      return BossCatalog.ids();
   }

   public String bossName(String var1) {
      BossCatalog.Definition var2 = BossCatalog.get(var1);
      return var2 == null ? null : var2.name();
   }

   public boolean placeEggNear(String id,Player player) {
      Block target=player.getTargetBlockExact(8);
      if(target!=null&&placeEgg(id,target.getRelative(BlockFace.UP).getLocation()))return true;
      if(target==null&&placeEgg(id,player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(4)).getBlock().getLocation()))return true;
      Location base=player.getLocation().getBlock().getLocation();
      for(int[] offset:new int[][]{{2,0},{-2,0},{0,2},{0,-2},{2,2},{-2,-2}})
         for(int dy:new int[]{0,-1,1})if(placeEgg(id,base.clone().add(offset[0],dy,offset[1])))return true;
      return false;
   }

   public boolean placeEgg(String var1, Location var2) {
      if(var2.getWorld()==null||var2.getY()<=var2.getWorld().getMinHeight()||var2.getY()+1>=var2.getWorld().getMaxHeight())return false;
      BossCatalog.Definition var3 = BossCatalog.get(var1);
      if (var3 != null && var2.getWorld() != null) {
         Block var4 = var2.getBlock();
         if (!var4.isEmpty() || !var4.getRelative(BlockFace.UP).isEmpty() || this.eggsByBlock.containsKey(this.key(var2))) {
            return false;
         } else {
            Block var5 = var4.getRelative(BlockFace.DOWN);
            String var6 = var5.getBlockData().getAsString();
            if (!EggPlacement.allowed(var4.isEmpty(),var4.getRelative(BlockFace.UP).isEmpty(),EggPlacement.support(var5.isEmpty(),var5.getType().isOccluding(),var5.getState() instanceof org.bukkit.block.TileState),var5.getState() instanceof org.bukkit.block.TileState,
                this.eggsByBlock.containsKey(this.key(var5.getLocation())) || this.eggsByBlock.containsKey(this.key(var4.getRelative(BlockFace.UP).getLocation())))) return false;
            var5.setType(Material.BEDROCK,false);
            var4.setType(Material.DRAGON_EGG, false);
            UUID var7 = UUID.randomUUID();
            BossManager.Encounter var8 = new BossManager.Encounter(var7, var3, var4.getLocation(), null, "", var6);
            this.encounters.put(var7, var8);
            this.eggsByBlock.put(this.key(var4.getLocation()), var8);
            this.saveEggs();
            this.startEggEffects(var8);
            var4.getWorld().spawnParticle(Particle.FIREWORK, var4.getLocation().add(0.5, 1.0, 0.5), 35, 0.7, 0.8, 0.7, 0.16);
            var4.getWorld().playSound(var4.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.2F, 0.6F);
            return true;
         }
      } else {
         return false;
      }
   }

   public void spawnBoss(String var1, Location var2, String var3) {
      BossCatalog.Definition var4 = BossCatalog.get(var1);
      if (var4 != null && var2.getWorld() != null) {
         LivingEntity var5;
         try {
            Entity var6 = var2.getWorld().spawnEntity(var2, var4.entityType());
            if (!(var6 instanceof LivingEntity var7)) {
               var6.remove();
               return;
            }

            var5 = var7;
         } catch (IllegalArgumentException var18) {
            this.plugin.getLogger().warning("No se pudo crear el jefe " + var4.id() + " en este servidor.");
            return;
         }

         UUID var19 = UUID.randomUUID();
         var5.customName(this.bossDisplay(var4));
         var5.setCustomNameVisible(true);
         var5.setPersistent(true);
         var5.setRemoveWhenFarAway(false);
         PersistentDataContainer var20 = var5.getPersistentDataContainer();
         var20.set(this.bossKey, PersistentDataType.BYTE, (byte)1);
         var20.set(this.encounterKey, PersistentDataType.STRING, var19.toString());
         var20.set(this.typeKey, PersistentDataType.STRING, var4.id());
         var20.set(this.awakenerKey, PersistentDataType.STRING, var3);
         double var8 = var4.mini() ? 1.35 + var4.tier().ordinal() * 0.12 : var4.tier().scale;
         double var10 = var4.tier().health * (var4.mini() ? 0.28 : 1.0);
         double var12 = var4.tier().damage * (var4.mini() ? 0.72 : 1.0);
         if (var4.legacyId().equals("reina_abeja")) {
            var8 = 1.45;
            var10 = 520.0;
            var12 = 16.0;
         }

         if (var4.legacyId().equals("zorro_salvaje")) {
            var8 = 1.38;
            var10 = 530.0;
            var12 = 25.0;
         }

         if (var4.legacyId().equals("jabali_infernal")) {
            var8 = 1.22;
            var10 = 680.0;
            var12 = 30.0;
         }

         if (var4.legacyId().equals("volcano")) {
            var8 = 1.6;
            var10 = 2000.0;
            var12 = 38.0;
         }

         if (var4.legacyId().equals("oso_tundra")) {
            var8 = 1.55;
            var10 = 620.0;
            var12 = 27.0;
         }

         if (var4.legacyId().equals("lobo_ceniza")) {
            var8 = 1.28;
            var10 = 480.0;
            var12 = 25.0;
         }

         if (var4.legacyId().equals("cabra_quebrada")) {
            var8 = 1.45;
            var10 = 540.0;
            var12 = 28.0;
         }

         if (var4.legacyId().equals("tortuga_baluarte")) {
            var8 = 1.8;
            var10 = 900.0;
            var12 = 30.0;
         }

         if (var4.legacyId().equals("llama_saqueadora")) {
            var8 = 1.42;
            var10 = 560.0;
            var12 = 26.0;
         }

         var8 = Math.max(0.1, Math.min(10, this.plugin.getConfig().getDouble("bosses." + var4.id() + ".scale", var8)));
         var10 = Math.max(1, Math.min(100000, this.plugin.getConfig().getDouble("bosses." + var4.id() + ".health", var10)));
         var12 = Math.max(0, Math.min(1000, this.plugin.getConfig().getDouble("bosses." + var4.id() + ".damage", var12)));
         if (var4.entityType()==EntityType.WARDEN) { var10=Math.max(var10, var4.legacyId().equals("warden_abismal") ? 14000 : 10000); var12=Math.max(var12,90); }
         this.setAttribute(var5, Attribute.MAX_HEALTH, var10);
         this.setAttribute(var5, Attribute.ATTACK_DAMAGE, var12);
         this.setAttribute(var5, Attribute.KNOCKBACK_RESISTANCE, Math.min(1.0, 0.45 + var4.tier().ordinal() * 0.12));
         double var14 = var4.legacyId().equals("zorro_salvaje")
            ? 0.28
            : (
               var4.legacyId().equals("reina_abeja")
                  ? 0.38
                  : (var4.legacyId().equals("jabali_infernal") ? 0.43 : (var4.legacyId().equals("volcano") ? 0.27 : 0.28 + var4.tier().ordinal() * 0.025))
            );
         this.setAttribute(var5, Attribute.MOVEMENT_SPEED, var14);
         if (var5 instanceof Bee var16 && var4.legacyId().equals("reina_abeja")) {
            var16.setAnger(24000);
         }

         if (var5 instanceof Piglin var21) {
            var21.setImmuneToZombification(true);
         }

         var5.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, var4.legacyId().equals("zorro_salvaje")?0:1, true, false));
         this.setScale(var5, var8);
         var5.setHealth(Math.min(var10, var5.getAttribute(Attribute.MAX_HEALTH).getValue()));
         this.encounters.put(var19, new BossManager.Encounter(var19, var4, var5.getLocation(), var5, var3));
         long var22 = this.plugin.getConfig().getLong("bosses.attack-interval-ticks", 50L);
         this.startBossSystems(var19, var5, var4, var22);
         var5.getWorld().playSound(var5.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.5F, 0.65F);
         var5.getWorld().spawnParticle(Particle.REVERSE_PORTAL, var5.getLocation().add(0.0, 1.0, 0.0), 80, 1.2, 1.5, 1.2, 0.1);
         this.plugin.getServer().broadcast(Component.text(var3 + " despertó a " + var4.name(), var4.tier().color).decorate(TextDecoration.BOLD));
      }
   }

   private Component bossDisplay(BossCatalog.Definition var1) {
      String var2 = var1.mini() ? "MINI JEFE • " : "";
      return ((TextComponent)Component.text(var2 + var1.name(), var1.tier().color).decorate(TextDecoration.BOLD)).decoration(TextDecoration.ITALIC, false);
   }

   private void startBossSystems(UUID var1, LivingEntity var2, BossCatalog.Definition var3, long var4) {
      ((CazaJefesPlugin)plugin).auras.track(var2,var3);BossEquipment.apply(var2,var3);
      if (!this.attacks.containsKey(var1)) {
         if (var2 instanceof Mob var6) {
            var6.setAware(true);
         }

         long var9 = Math.max(5L, Math.min(10L, var4));
         BossBuffs.apply(var2);
         this.attacks
            .put(
               var1,
               this.plugin
                  .getServer()
                  .getScheduler()
                  .runTaskTimer(this.plugin, new BossManager.BossAttackTask(this.plugin, var2, var3, this.minionKey), 10L, var9)
            );
         BossBar var8 = Bukkit.createBossBar("", this.barColor(var3.tier()), BarStyle.SEGMENTED_12, new BarFlag[0]);
         var8.setVisible(true);
         this.healthBars.put(var1, var8);
         this.barTasks.put(var1, this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, () -> this.updateBossBar(var1, var2, var3, var8), 1L, 5L));
      }
   }

   private BarColor barColor(BossCatalog.Tier var1) {
      return switch (var1) {
         case COMMON -> BarColor.GREEN;
         case RARE -> BarColor.BLUE;
         case EPIC -> BarColor.PURPLE;
         case LEGENDARY -> BarColor.YELLOW;
         case MYTHIC -> BarColor.RED;
         case ABYSSAL -> BarColor.PURPLE;
      };
   }

   private void updateBossBar(UUID var1, LivingEntity var2, BossCatalog.Definition var3, BossBar var4) {
      if (var2.isValid() && !var2.isDead()) {
         double var5 = Math.max(1.0, var2.getAttribute(Attribute.MAX_HEALTH).getValue());
         var4.setProgress(Math.max(0.0, Math.min(1.0, var2.getHealth() / var5)));
         var4.setTitle(var3.name()+"  "+String.format(java.util.Locale.ROOT,"%.1f / %.0f HP",var2.getHealth(),var5));
         HashSet<Player> var7 = new HashSet<>();

         for (Player var9 : var2.getWorld().getPlayers()) {
            if (var9.getLocation().distanceSquared(var2.getLocation()) <= 4096.0) {
               var7.add(var9);
            }
         }

         for (Player var12 : new HashSet<Player>(var4.getPlayers())) {
            if (!var7.contains(var12)) {
               var12.sendExperienceChange(var12.getExp(), var12.getLevel());
               var4.removePlayer(var12);
            }
         }

         for (Player var13 : var7) {
            if (!var4.getPlayers().contains(var13)) {
               var4.addPlayer(var13);
            }

            var13.sendExperienceChange((float)Math.max(0.0, Math.min(1.0, var2.getHealth() / var5)), (int)Math.ceil(var2.getHealth()));
         }
      } else {
         this.removeBossBar(var1);
      }
   }

   private void removeBossBar(UUID var1) {
      BukkitTask var2 = this.barTasks.remove(var1);
      if (var2 != null) {
         var2.cancel();
      }

      BossBar var3 = this.healthBars.remove(var1);
      if (var3 != null) {
         for (Player var5 : var3.getPlayers()) {
            var5.sendExperienceChange(var5.getExp(), var5.getLevel());
         }

         var3.removeAll();
         var3.setVisible(false);
      }
   }

   public List<BossManager.TrackedEncounter> trackedEncounters() {
      ArrayList var1 = new ArrayList();

      for (BossManager.Encounter var3 : this.encounters.values()) {
         if (var3.boss == null || var3.boss.isValid() && !var3.boss.isDead()) {
            Location var4 = var3.boss == null ? var3.location : var3.boss.getLocation();
            double var5 = var3.boss == null ? 0.0 : var3.boss.getHealth();
            double var7 = var3.boss == null ? 0.0 : var3.boss.getAttribute(Attribute.MAX_HEALTH).getValue();
            var1.add(
               new BossManager.TrackedEncounter(
                  var3.id,
                  var3.definition.id(),
                  var3.definition.name(),
                  var3.definition.tier().display,
                  var3.definition.mini(),
                  var4,
                  var3.boss == null,
                  var5,
                  var7,
                  var3.awakenedBy
               )
            );
         }
      }

      return var1;
   }

   @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
   public void onEggInteract(PlayerInteractEvent var1) {
      if (var1.getAction() == Action.RIGHT_CLICK_BLOCK && var1.getClickedBlock() != null) {
         BossManager.Encounter var2 = this.eggsByBlock.get(this.key(var1.getClickedBlock().getLocation()));
         if (var2 != null) {
            var1.setCancelled(true);
            Player var3 = var1.getPlayer();
            if (!var3.hasPermission("rareitemhunter.hunter.hatch") || !((CazaJefesPlugin)plugin).guard.canAttackBoss(var3)) return;
            this.eggsByBlock.remove(this.key(var2.location));
            this.encounters.remove(var2.id);
            this.stopEggEffects(var2.id);
            var2.location.getBlock().setType(Material.AIR, false);
            this.restoreSupport(var2);
            this.saveEggs();
            var2.location.getWorld().spawnParticle(Particle.EXPLOSION, var2.location.clone().add(0.5, 0.7, 0.5), 1);
            var2.location.getWorld().spawnParticle(Particle.PORTAL, var2.location.clone().add(0.5, 0.6, 0.5), 110, 0.9, 1.0, 0.9, 0.16);
            var2.location.getWorld().playSound(var2.location, Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1.4F, 0.7F);
            var3.showTitle(
               Title.title(
                  Component.text(var3.getName() + " despertó al jefe", var2.definition.tier().color).decorate(TextDecoration.BOLD),
                  Component.text(var2.definition.name(), var2.definition.tier().color)
               )
            );
            this.spawnBoss(var2.definition.id(), var2.location.clone().add(0.5, 0.0, 0.5), var3.getName());
         }
      }
   }

   @EventHandler
   public void onlyTargetPlayers(EntityTargetLivingEntityEvent var1) {
      PersistentDataContainer var2 = var1.getEntity().getPersistentDataContainer();
      if (!var2.has(this.minionKey, PersistentDataType.BYTE)
         || var1.getTarget() == null
         || !var1.getTarget().getPersistentDataContainer().has(this.bossKey, PersistentDataType.BYTE)
            && !var1.getTarget().getPersistentDataContainer().has(this.minionKey, PersistentDataType.BYTE)) {
         if (var2.has(this.minionKey, PersistentDataType.BYTE)
            && "hoglin_guard".equals(var2.get(new NamespacedKey(this.plugin, "minion_kind"), PersistentDataType.STRING))
            && !var2.has(new NamespacedKey(this.plugin, "guard_active"), PersistentDataType.BYTE)) {
            var1.setCancelled(true);
         } else if (var1.getEntity().getPersistentDataContainer().has(this.bossKey, PersistentDataType.BYTE)) {
            if (var1.getTarget() != null && !(var1.getTarget() instanceof Player)) {
               var1.setCancelled(true);
            }
         }
      } else {
         var1.setCancelled(true);
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void preventBossTransformation(EntityTransformEvent var1) {
      if (var1.getEntity().getPersistentDataContainer().has(this.bossKey, PersistentDataType.BYTE)) {
         var1.setCancelled(true);
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void bossesOnlyDamagePlayers(EntityDamageByEntityEvent var1) {
      if (var1.getEntity() instanceof LivingEntity var2 && var2.getPersistentDataContainer().has(this.bossKey, PersistentDataType.BYTE)) {
         var2.setFireTicks(0);
         String var10 = (String)var2.getPersistentDataContainer().get(this.typeKey, PersistentDataType.STRING);
         if ("jabali_infernal".equals(var10)) {
            Player var4 = var1.getDamager() instanceof Player var7
               ? var7
               : (var1.getDamager() instanceof Projectile var6 && var6.getShooter() instanceof Player var5 ? var5 : null);
            if (var4 != null) {
               for (Entity var19 : var2.getNearbyEntities(30.0, 16.0, 30.0)) {
                  PersistentDataContainer var23 = var19.getPersistentDataContainer();
                  if (var23.has(this.minionKey, PersistentDataType.BYTE)
                     && "hoglin_guard".equals(var23.get(new NamespacedKey(this.plugin, "minion_kind"), PersistentDataType.STRING))) {
                     var23.set(new NamespacedKey(this.plugin, "guard_active"), PersistentDataType.BYTE, (byte)1);
                     if (var19 instanceof Mob var26) {
                        var26.setTarget(var4);
                     }

                     if (var19 instanceof LivingEntity var27) {
                        var27.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1200, 1, true, false));
                     }
                  }
               }
            }
         }
      }

      LivingEntity var9 = var1.getDamager() instanceof LivingEntity var17
         ? var17
         : (var1.getDamager() instanceof Projectile var14 && var14.getShooter() instanceof LivingEntity var11 ? var11 : null);
      if (var1.getEntity() instanceof Player && var9 != null && var9.getPersistentDataContainer().has(this.bossKey, PersistentDataType.BYTE)) {
         String var12 = (String)var9.getPersistentDataContainer().get(this.typeKey, PersistentDataType.STRING);
         BossCatalog.Definition var15 = BossCatalog.get(var12);

         double var18 = switch (var15 == null ? BossCatalog.Tier.COMMON : var15.tier()) {
            case COMMON -> 4.0;
            case RARE -> 6.0;
            case EPIC -> 8.0;
            case LEGENDARY -> 10.0;
            case MYTHIC -> 12.0;
            case ABYSSAL -> 16.0;
         };
         if(!LightningRetaliation.isActive()&&!AbilityDamage.active())SafeDamage.setBase(var1,var18+(var15==null?6:BattleRules.strengthBonus(var15)));
      }

      if (var9 != null && var9.getPersistentDataContainer().has(this.bossKey, PersistentDataType.BYTE) && !(var1.getEntity() instanceof Player)) {
         boolean var13 = "zorro_salvaje".equals(var9.getPersistentDataContainer().get(this.typeKey, PersistentDataType.STRING))
            && var1.getEntity().getPersistentDataContainer().has(this.minionKey, PersistentDataType.BYTE)
            && "fox_sheep".equals(var1.getEntity().getPersistentDataContainer().get(new NamespacedKey(this.plugin, "minion_kind"), PersistentDataType.STRING));
         if (!var13) {
            var1.setCancelled(true);
         }
      }

      if (var9 != null
         && var9.getPersistentDataContainer().has(this.minionKey, PersistentDataType.BYTE)
         && (
            var1.getEntity().getPersistentDataContainer().has(this.bossKey, PersistentDataType.BYTE)
               || var1.getEntity().getPersistentDataContainer().has(this.minionKey, PersistentDataType.BYTE)
         )) {
         var1.setCancelled(true);
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void bossesCannotBurn(EntityCombustEvent var1) {
      if (var1.getEntity().getPersistentDataContainer().has(this.bossKey, PersistentDataType.BYTE)) {
         var1.setCancelled(true);
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void bossesIgnoreFireDamage(EntityDamageEvent var1) {
      if (var1.getEntity().getPersistentDataContainer().has(this.bossKey, PersistentDataType.BYTE)) {
         switch (var1.getCause()) {
            case FIRE:
            case FIRE_TICK:
            case LAVA:
            case HOT_FLOOR:
            case CAMPFIRE:
               var1.setCancelled(true);
         }
      }
   }

   @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
   public void minionsAreDurableAndStayHostile(EntityDamageEvent var1) {
      if (var1.getEntity() instanceof LivingEntity var2) {
         PersistentDataContainer var10 = var2.getPersistentDataContainer();
         if (var10.has(this.minionKey, PersistentDataType.BYTE)) {
            String var4 = (String)var10.get(new NamespacedKey(this.plugin, "minion_kind"), PersistentDataType.STRING);
            if (!"fox_sheep".equals(var4)) {
               if (var2 instanceof Bee var5) {
                  var5.setAnger(24000);
                  Player var6 = var1 instanceof EntityDamageByEntityEvent var8 && var8.getDamager() instanceof Player var7 ? var7 : null;
                  if (var6 != null) {
                     var5.setTarget(var6);
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void onBossDeath(EntityDeathEvent var1) {
      LivingEntity var2 = var1.getEntity();
      PersistentDataContainer var3 = var2.getPersistentDataContainer();
      if (var3.has(this.minionKey, PersistentDataType.BYTE)) {
         String var12 = (String)var3.get(new NamespacedKey(this.plugin, "minion_parent"), PersistentDataType.STRING);
         String var13 = (String)var3.get(new NamespacedKey(this.plugin, "minion_kind"), PersistentDataType.STRING);
         if ("fox_sheep".equals(var13) && var12 != null) {
            try {
               BossManager.Encounter var14 = this.encounters.get(UUID.fromString(var12));
               if (var14 != null && var14.boss != null && var14.boss.isValid()) {
                  double var15 = var14.boss.getAttribute(Attribute.MAX_HEALTH).getValue();
                  var14.boss.setHealth(Math.min(var15, var14.boss.getHealth() + var15 * 0.05));
                  var14.boss.getWorld().spawnParticle(Particle.HEART, var14.boss.getLocation().add(0.0, 1.0, 0.0), 5, 0.3, 0.4, 0.3, 0.02);
               }
            } catch (Exception var10) {
            }
         }

         var1.getDrops().clear();
         var1.setDroppedExp(0);
         if (var2.getKiller()!=null && ((CazaJefesPlugin)plugin).guard.canAttackBoss(var2.getKiller())
             && Math.random()<LootRules.probability(plugin.getConfig().getDouble("loot.minion-fragment-chance",0.005))) var1.getDrops().add(relicItems.fragment(1));
      } else if (var3.has(this.bossKey, PersistentDataType.BYTE)) {
         UUID var4;
         try {
            var4 = UUID.fromString((String)var3.get(this.encounterKey, PersistentDataType.STRING));
         } catch (Exception var11) {
            return;
         }

         BossManager.Encounter var5 = this.encounters.remove(var4);
         BukkitTask var6 = this.attacks.remove(var4);
         if (var6 != null) {
            var6.cancel();
         }

         this.removeBossBar(var4);
         var1.getDrops().clear();
         var1.setDroppedExp(0);
         BossCatalog.Definition var7 = var5 == null ? BossCatalog.get((String)var3.get(this.typeKey, PersistentDataType.STRING)) : var5.definition;
         if (var7 == null) {
            var7 = BossCatalog.get("coloso");
         }

         String var8 = var2.getKiller() == null ? (String)var3.get(this.awakenerKey, PersistentDataType.STRING) : var2.getKiller().getName();
         if (var8 == null || var8.isBlank()) {
            var8 = "un aventurero";
         }

         NamespacedKey paid = new NamespacedKey(plugin,"loot_paid");
         if (var3.has(paid, PersistentDataType.BYTE)) return;
         var3.set(paid, PersistentDataType.BYTE,(byte)1);
         final boolean credited=((CazaJefesPlugin)plugin).guard.canDrop(var2);
         final BossCatalog.Definition defeated=var7;
         final String winner=var8;
         final Location dropLocation=var2.getLocation().clone();
         ((CazaJefesPlugin)plugin).deathVisuals.show(var2,defeated,()->{
            RelicItems.LootResult result=credited?this.relicItems.dropBossLoot(dropLocation,defeated,var4):new RelicItems.LootResult(0,Component.text("sin drops: no hay crédito de jugador válido"));
            this.plugin.getServer().broadcast(DamageLedger.report(var2,defeated,winner,result));
         });
         var2.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, var2.getLocation().add(0.0, 1.0, 0.0), 65, 1.1, 1.2, 1.1, 0.1);
         var2.getWorld().playSound(var2.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1.0F, 1.25F);
      }
   }

   private void setAttribute(LivingEntity var1, Attribute var2, double var3) {
      AttributeInstance var5 = var1.getAttribute(var2);
      if (var5 != null) {
         var5.setBaseValue(var3);
      }
   }

   private void setScale(LivingEntity var1, double var2) {
      Attribute var4 = (Attribute)Registry.ATTRIBUTE.get(NamespacedKey.minecraft("scale"));
      if (var4 == null) {
         var4 = (Attribute)Registry.ATTRIBUTE.get(NamespacedKey.minecraft("generic.scale"));
      }

      if (var4 != null) {
         this.setAttribute(var1, var4, var2);
      }
   }

   private void restoreEggs() {
      for (Map var2 : this.plugin.getConfig().getMapList("active-eggs")) {
         try {
            World var3 = this.plugin.getServer().getWorld(String.valueOf(var2.get("world")));
            if (var3 != null) {
               Location var4 = new Location(var3, ((Number)var2.get("x")).intValue(), ((Number)var2.get("y")).intValue(), ((Number)var2.get("z")).intValue());
               BossCatalog.Definition var5 = BossCatalog.get(String.valueOf(var2.get("type")));
               if (var5 != null && var4.getBlock().getType() == Material.DRAGON_EGG) {
                  Object var6 = var2.get("support");
                  String var7 = var6 == null ? "minecraft:air" : String.valueOf(var6);
                  UUID var8 = UUID.randomUUID();
                  BossManager.Encounter var9 = new BossManager.Encounter(var8, var5, var4, null, "", var7);
                  this.encounters.put(var8, var9);
                  this.eggsByBlock.put(this.key(var4), var9);
                  this.startEggEffects(var9);
               }
            }
         } catch (Exception var10) {
            this.plugin.getLogger().warning("Se omitió un huevo guardado con datos inválidos.");
         }
      }
   }

   private void saveEggs() {
      ArrayList var1 = new ArrayList();

      for (BossManager.Encounter var3 : this.eggsByBlock.values()) {
         HashMap var4 = new HashMap();
         var4.put("world", var3.location.getWorld().getName());
         var4.put("x", var3.location.getBlockX());
         var4.put("y", var3.location.getBlockY());
         var4.put("z", var3.location.getBlockZ());
         var4.put("type", var3.definition.id());
         var4.put("support", var3.supportData);
         var1.add(var4);
      }

      this.plugin.getConfig().set("active-eggs", var1);
      this.plugin.saveConfig();
   }

   private String key(Location var1) {
      return var1.getWorld().getUID() + ":" + var1.getBlockX() + ":" + var1.getBlockY() + ":" + var1.getBlockZ();
   }

   private void startEggEffects(final BossManager.Encounter var1) {
      if (var1.location.getWorld() != null && !this.eggEffects.containsKey(var1.id)) {
         this.eggEffects
            .put(
               var1.id,
               Bukkit.getScheduler()
                  .runTaskTimer(
                     this.plugin,
                     new Runnable() {
                        double angle;

                        @Override
                        public void run() {
                           if (BossManager.this.eggsByBlock.containsKey(BossManager.this.key(var1.location))
                              && var1.location.getBlock().getType() == Material.DRAGON_EGG) {
                              Location var1x = var1.location.clone().add(0.5, 0.7, 0.5);
                              this.angle += Math.PI / 12;
                              CombatVisuals.egg(var1x,var1.definition.tier(),this.angle);
                              var1.location.getWorld().spawnParticle(Particle.PORTAL, var1x, 10, 0.45, 0.55, 0.45, 0.05);
                              var1.location
                                 .getWorld()
                                 .spawnParticle(
                                    Particle.ENCHANT, var1x.clone().add(Math.cos(this.angle), 0.35, Math.sin(this.angle)), 3, 0.08, 0.08, 0.08, 0.01
                                 );
                           } else {
                              BossManager.this.stopEggEffects(var1.id);
                           }
                        }
                     },
                     1L,
                     5L
                  )
            );
      }
   }

   private void stopEggEffects(UUID var1) {
      BukkitTask var2 = this.eggEffects.remove(var1);
      if (var2 != null) {
         var2.cancel();
      }
   }

   private void restoreSupport(BossManager.Encounter encounter) {
      if(encounter.location.getWorld()==null||encounter.supportData==null)return;
      Block support=encounter.location.getBlock().getRelative(BlockFace.DOWN);
      if(support.getType()==Material.BEDROCK) {
         try { support.setBlockData(Bukkit.createBlockData(encounter.supportData),false); }
         catch(IllegalArgumentException invalid) { plugin.getLogger().warning("No se pudo restaurar el pedestal: datos guardados inválidos."); }
      }
   }

   @EventHandler(ignoreCancelled = true)
   public void onEggBreak(BlockBreakEvent var1) {
      BossManager.Encounter var2 = this.eggsByBlock.remove(this.key(var1.getBlock().getLocation()));
      if (var2 != null) {
         this.encounters.remove(var2.id);
         this.stopEggEffects(var2.id);
         this.restoreSupport(var2);
         this.saveEggs();
      }
   }

   public boolean isEggBlock(org.bukkit.block.Block block) { return eggsByBlock.containsKey(key(block.getLocation())); }

   public void stopAllAttacks() {
      this.attacks.values().forEach(BukkitTask::cancel);
      this.attacks.clear();
      new ArrayList<>(this.healthBars.keySet()).forEach(this::removeBossBar);
   }

   private static final class BossAttackTask implements Runnable {
      private final JavaPlugin plugin;
      private final LivingEntity boss;
      private final BossCatalog.Definition definition;
      private final NamespacedKey minionKey;
      private int attacks;
      private int specialRotation;
      private long nextSpecialAt;
      private int phase = 1;
      private final BossTactics tactics;
      private final BossStages stages;
      private final Map<UUID, Long> meleeCooldowns = new HashMap<>();
      private final Map<UUID, Long> hotbarCooldowns = new HashMap<>();

      private BossAttackTask(JavaPlugin var1, LivingEntity var2, BossCatalog.Definition var3, NamespacedKey var4) {
         this.plugin = var1;
         this.boss = var2;
         this.definition = var3;
         this.minionKey = var4;
         this.tactics=new BossTactics(var2,var3);this.stages=new BossStages(var2,var3);this.phase=BossStages.phase(var2);
      }

      @Override
      public void run() {
         if (this.boss.isValid() && !this.boss.isDead()) {
            if (this.boss.isInvulnerable()) return;
            BossBuffs.apply(boss);
            AquaticCombat.maintain(boss);
            int speed=definition.legacyId().equals("zorro_salvaje")?0:1;
            PotionEffect effect=boss.getPotionEffect(PotionEffectType.SPEED);
            if(effect==null||effect.getAmplifier()!=speed||effect.getDuration()<40){boss.removePotionEffect(PotionEffectType.SPEED);boss.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,100,speed,true,false));}
            stages.update();
            Player var2=tactics.pickTarget();
            if (var2 != null) {
               if (boss instanceof org.bukkit.entity.Warden warden) { warden.setAnger(var2,150); warden.setDisturbanceLocation(var2.getLocation()); }
               int nextPhase = BossStages.phase(boss);
               if (nextPhase > phase) {
                  phase = nextPhase;
                  BossBuffs.apply(boss);
            AquaticCombat.maintain(boss);
                  summonMinions(var2);

               }
               if(((CazaJefesPlugin)plugin).abilities.tick(boss,definition,var2,phase))return;
               if(boss instanceof org.bukkit.entity.Shulker){
                  long now=System.currentTimeMillis();if(now>=nextSpecialAt){((CazaJefesPlugin)plugin).abilities.special(boss,definition,var2,specialRotation++%3);nextSpecialAt=now+10000;return;}
                  ((CazaJefesPlugin)plugin).shulkers.tick(boss,var2,phase);return;}
               long var3 = System.currentTimeMillis();
                  if (this.boss instanceof Mob var5) {
                     var5.setTarget(var2);
                  }

                  if (this.definition.mini()) {
                     this.attacks++;
                     this.pursue(var2);
                     this.attackNearby(var2);
                     if (var3 >= this.nextSpecialAt) {
                        this.miniSpecial(var2, this.specialRotation++ % 8);
                        this.nextSpecialAt = var3 + 10000L;
                     }
                  } else {
                     this.pursue(var2);
                     this.attackNearby(var2);
                     this.attacks++;
                     if (var3 >= this.nextSpecialAt) {
                        this.miniSpecial(var2, this.specialRotation++ % 8);
                        this.nextSpecialAt = var3 + 10000L;
                     }

                     if (this.attacks % 300 == 0) {
                        this.summonMinions(var2);
                     }

                     // Final-phase inventory disruption is handled centrally by CombatPhases.
                  }
            }
         }
      }

      private void pursue(Player target){tactics.tick(target,phase);}

      private void attackNearby(Player var1) {
         long var2 = System.currentTimeMillis();
         if (boss.hasLineOfSight(var1) && !(var1.getLocation().distanceSquared(this.boss.getLocation()) > 14.44) && var2 >= this.meleeCooldowns.getOrDefault(var1.getUniqueId(), 0L)) {
            String var6 = this.definition.legacyId();

            double var4 = switch (var6) {
               case "reina_abeja" -> 9.0;
               case "zorro_salvaje" -> 12.0;
               case "jabali_infernal" -> 16.0;
               case "volcano" -> 18.0;
               default -> 10.0;
            };
            var1.damage(var4, this.boss);
            var1.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 45, 0));
            this.meleeCooldowns.put(var1.getUniqueId(), var2 + BattleRules.MELEE_COOLDOWN_MS);
            var1.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, var1.getLocation().add(0.0, 1.0, 0.0), 6, 0.3, 0.35, 0.3, 0.04);
         }
      }

      private boolean huntSpawnedSheep() {
         Entity var1 = this.boss
            .getNearbyEntities(22.0, 12.0, 22.0)
            .stream()
            .filter(
               var1x -> var1x instanceof LivingEntity
                  && var1x.getPersistentDataContainer().has(this.minionKey, PersistentDataType.BYTE)
                  && "fox_sheep".equals(var1x.getPersistentDataContainer().get(new NamespacedKey(this.plugin, "minion_kind"), PersistentDataType.STRING))
            )
            .min(
               (var1x, var2x) -> Double.compare(
                  var1x.getLocation().distanceSquared(this.boss.getLocation()), var2x.getLocation().distanceSquared(this.boss.getLocation())
               )
            )
            .orElse(null);
         if (var1 instanceof LivingEntity var2) {
            Vector var3 = var2.getLocation().toVector().subtract(this.boss.getLocation().toVector()).setY(0);
            if (this.boss instanceof Mob var4) {
               var4.setTarget(var2);
            }

            if (var3.lengthSquared() > 0.2) {
               this.boss.setVelocity(var3.normalize().multiply(0.72).setY(this.boss.getVelocity().getY()));
            }

            long var6 = System.currentTimeMillis();
            if (var2.getLocation().distanceSquared(this.boss.getLocation()) <= 9.0 && var6 >= this.meleeCooldowns.getOrDefault(var2.getUniqueId(), 0L)) {
               var2.damage(100.0, this.boss);
               this.meleeCooldowns.put(var2.getUniqueId(), var6 + 900L);
            }

            return true;
         } else {
            return false;
         }
      }

      private void miniSpecial(Player target,int rotation){
         if(rotation>=5){((CazaJefesPlugin)plugin).abilities.special(boss,definition,target,rotation-5);return;}
         if(BossWarnings.active(target))return;
         target.sendActionBar(Component.text("¡" + definition.name() + " prepara su ataque! · Aléjate", NamedTextColor.RED));
         Location warning = target.getLocation().clone();
         boss.getWorld().spawnParticle(Particle.FLAME, warning, 24, 0.8, 0.1, 0.8, 0);
         plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!((CazaJefesPlugin)plugin).abilities.busy(boss)&&boss.isValid() && !boss.isDead() && !boss.isInvulnerable() && CombatGuard.eligible(target)
                && target.getWorld().equals(boss.getWorld()) && target.getLocation().distanceSquared(boss.getLocation()) <= 576
                && target.getLocation().distanceSquared(warning) <= 9 && boss.hasLineOfSight(target)) executeSpecial(target, rotation);
         }, 20L);
      }

      private void executeSpecial(Player var1, int var2) {
         CombatVisuals.special(boss,definition);
         switch (var2) {
            case 0:
               this.shockwave(var1);
               break;
            case 1:
               this.huntLunge(var1);
               break;
            case 2:
               this.targetedBurst(var1);
               break;
            case 3:
               this.summonMinions(var1);
               this.boss.getWorld().playSound(this.boss.getLocation(), Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1.0F, 0.7F);
               break;
            default:
               this.specialDebuffWave(var1);
         }

         if (var2 == 4) {
            this.signatureSpecial(var1);
         }
      }

      private void specialDebuffWave(Player var1) {
         Location var2 = this.boss.getLocation();
         Particle var3 = this.definition.tier() == BossCatalog.Tier.ABYSSAL
            ? Particle.DRAGON_BREATH
            : (this.definition.tier().ordinal() >= BossCatalog.Tier.MYTHIC.ordinal() ? Particle.SOUL_FIRE_FLAME : Particle.WITCH);
         this.boss.getWorld().playSound(var2, Sound.ENTITY_WARDEN_ATTACK_IMPACT, 1.3F, 0.75F);
         ParticleData.spawn(this.boss.getWorld(), var3, var2.clone().add(0.0, 1.0, 0.0), 100, 2.5, 1.1, 2.5, 0.08);
         double var4 = this.definition.mini() ? 5.0 : 8.0;

         for (Player var7 : this.boss.getWorld().getPlayers()) {
            if (!CombatGuard.eligible(var7)) continue;
            if (var7.getLocation().distanceSquared(var2) <= var4 * var4) {
               var7.damage(Math.max(3.0, this.definition.tier().damage * 0.16), this.boss);
               this.debuff(var7);
            }
         }

         var1.sendActionBar(Component.text("¡" + this.definition.name() + " desata su poder!", this.definition.tier().color));
      }

      private void signatureSpecial(Player var1) {
         Location var2 = this.boss.getLocation();
         String var3 = this.definition.legacyId();
         Particle var4 = var3.equals("reina_abeja")
            ? Particle.HAPPY_VILLAGER
            : (var3.equals("zorro_salvaje") ? Particle.CRIT : (var3.equals("jabali_infernal") ? Particle.FLAME : Particle.CLOUD));
         this.boss.getWorld().playSound(var2, var3.equals("jabali_infernal") ? Sound.ENTITY_RAVAGER_ROAR : Sound.ENTITY_WARDEN_ATTACK_IMPACT, 1.5F, 0.8F);
         ParticleData.spawn(this.boss.getWorld(), var4, var2.clone().add(0.0, 1.0, 0.0), 85, 1.3, 0.8, 1.3, 0.15);
         if (var3.equals("zorro_salvaje")) {
            this.summonMinions(var1);

            for (int var16 = 0; var16 < 3; var16++) {
               this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                  if (this.boss.isValid() && !this.boss.isDead()) {
            if (this.boss.isInvulnerable()) return;
                     Vector var2x = var1.getLocation().toVector().subtract(this.boss.getLocation().toVector()).setY(0);
                     if (var2x.lengthSquared() > 0.01) {
                        this.boss.setVelocity(var2x.normalize().multiply(0.60).setY(0.20));
                     }

                     if (var1.getLocation().distanceSquared(this.boss.getLocation()) < 20.25) {
                        var1.damage(8.0, this.boss);
                        var1.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 45, 0));
                     }
                  }
               }, var16 * 5L);
            }
         } else if (var3.equals("jabali_infernal")) {
            Vector var15 = var1.getLocation().toVector().subtract(var2.toVector()).setY(0);
            if (var15.lengthSquared() > 0.01) {
               this.boss.setVelocity(var15.normalize().multiply(1.3).setY(0.35));
            }

            for (Player var7 : this.boss.getWorld().getPlayers()) {
            if (!CombatGuard.eligible(var7)) continue;
               if (var7.getLocation().distanceSquared(var2) <= 30.25) {
                  var7.damage(12.0, this.boss);
                  var7.setFireTicks(Math.max(var7.getFireTicks(), 100));
                  var7.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 80, 0));
               }
            }

            this.boss.getWorld().spawnParticle(Particle.EXPLOSION, var2, 2, 0.7, 0.4, 0.7, 0.05);
         } else if (var3.equals("volcano")) {
            for (Player var21 : this.boss.getWorld().getPlayers()) {
            if (!CombatGuard.eligible(var21)) continue;
               if (var21.getLocation().distanceSquared(var2) <= 49.0) {
                  var21.damage(14.0, this.boss);
                  var21.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 1));
                  var21.setVelocity(var21.getLocation().toVector().subtract(var2.toVector()).normalize().multiply(0.7).setY(0.75));
               }
            }

            this.boss.getWorld().spawnParticle(Particle.BLOCK, var2, 55, 2.0, 0.3, 2.0, 0.1, Material.MOSS_BLOCK.createBlockData());
         } else {
            switch (this.definition.entityType()) {
               case ZOMBIE:
               case HUSK:
                  for (Player var20 : this.boss.getWorld().getPlayers()) {
            if (!CombatGuard.eligible(var20)) continue;
                     if (var20.getLocation().distanceSquared(var2) <= 25.0) {
                        var20.damage(11.0, this.boss);
                        var20.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 0));
                     }
                  }

                  this.boss.getWorld().spawnParticle(Particle.SOUL, var2, 60, 1.5, 0.7, 1.5, 0.08);
                  return;
               case SKELETON:
               case STRAY:
               case PILLAGER:
                  Vector var12 = var1.getLocation().toVector().subtract(var2.toVector()).normalize();

                  for (int var19 = 1; var19 <= 12; var19++) {
                     this.boss
                        .getWorld()
                        .spawnParticle(Particle.CRIT, var2.clone().add(var12.clone().multiply(var19)).add(0.0, 1.0, 0.0), 5, 0.15, 0.15, 0.15, 0.02);
                  }

                  if (var1.getLocation().distanceSquared(var2) <= 169.0) {
                     var1.damage(14.0, this.boss);
                     var1.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 75, 0));
                  }

                  return;
               case CAVE_SPIDER:
               case SPIDER:
                  var1.damage(10.0, this.boss);
                  var1.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 120, 1));
                  this.boss.getWorld().spawnParticle(Particle.SPORE_BLOSSOM_AIR, var1.getLocation(), 50, 0.8, 0.7, 0.8, 0.06);
                  return;
               case BLAZE:
                  for (Player var18 : this.boss.getWorld().getPlayers()) {
            if (!CombatGuard.eligible(var18)) continue;
                     if (var18.getLocation().distanceSquared(var2) <= 36.0) {
                        var18.damage(13.0, this.boss);
                        var18.setFireTicks(Math.max(var18.getFireTicks(), 80));
                     }
                  }

                  this.boss.getWorld().spawnParticle(Particle.FLAME, var2, 90, 1.8, 1.0, 1.8, 0.1);
                  return;
               case SLIME:
                  for (Player var17 : this.boss.getWorld().getPlayers()) {
            if (!CombatGuard.eligible(var17)) continue;
                     if (var17.getLocation().distanceSquared(var2) <= 36.0) {
                        var17.damage(12.0, this.boss);
                        var17.setVelocity(var17.getLocation().toVector().subtract(var2.toVector()).normalize().multiply(0.8).setY(0.9));
                     }
                  }

                  this.boss.getWorld().spawnParticle(Particle.ITEM_SLIME, var2, 70, 1.6, 0.7, 1.6, 0.1);
                  return;
               case PHANTOM:
                  Vector var9 = var1.getLocation().toVector().subtract(var2.toVector()).normalize();
                  this.boss.setVelocity(var9.multiply(1.4));
                  var1.damage(12.0, this.boss);
                  var1.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 55, 0));
                  this.boss.getWorld().spawnParticle(Particle.SOUL, var2, 60, 0.8, 0.5, 0.8, 0.1);
                  return;
               case VINDICATOR:
                  Vector var8 = var1.getLocation().toVector().subtract(var2.toVector()).setY(0);
                  if (var8.lengthSquared() > 0.01) {
                     this.boss.setVelocity(var8.normalize().multiply(1.15).setY(0.3));
                  }

                  if (var1.getLocation().distanceSquared(var2) <= 36.0) {
                     var1.damage(16.0, this.boss);
                  }

                  this.boss.getWorld().spawnParticle(Particle.SWEEP_ATTACK, var2, 18, 0.6, 0.6, 0.6, 0.1);
                  return;
               default:
                  for (Player var6 : this.boss.getWorld().getPlayers()) {
            if (!CombatGuard.eligible(var6)) continue;
                     if (var6.getLocation().distanceSquared(var2) <= 36.0) {
                        var6.damage(8.0, this.boss);
                        var6.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 1));
                        var6.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 45, 0));
                     }
                  }

                  this.summonMinions(var1);
            }
         }
      }

      private void summonMinions(Player var1) {
         if (this.definition.legacyId().equals("reina_abeja")) {
            this.summonCustom(EntityType.BEE, var1, 2, "abeja_enfurecida");
         } else if (this.definition.legacyId().equals("zorro_salvaje")) {
            this.summonCustom(EntityType.SHEEP, var1, 2, "fox_sheep");
         } else if (this.definition.legacyId().equals("jabali_infernal")) {
            this.summonCustom(EntityType.PIGLIN, var1, 2, "hoglin_guard");
         } else if (this.definition.legacyId().equals("volcano")) {
            this.summonCustom(EntityType.SNIFFER, var1, 2, "sniffer_young");
         } else if (this.definition.entityType() != EntityType.SPIDER && this.definition.entityType() != EntityType.CAVE_SPIDER) {
            int var2 = this.definition.mini() ? 1 : 2 + this.definition.tier().ordinal();
            long var3 = this.boss
               .getWorld().getEntities()
               .stream()
               .filter(var1x -> var1x.getPersistentDataContainer().has(this.minionKey, PersistentDataType.BYTE))
            .filter(e -> java.util.Objects.equals(e.getPersistentDataContainer().get(new NamespacedKey(plugin,"minion_parent"), PersistentDataType.STRING), boss.getPersistentDataContainer().get(new NamespacedKey(plugin,"encounter_id"), PersistentDataType.STRING)))
               .count();
            if (var3 < var2) {
               int var5 = this.definition.mini() ? 1 : 1 + this.definition.tier().ordinal() / 2;

               EntityType[] var6 = switch (this.definition.tier()) {
                  case COMMON -> new EntityType[]{EntityType.ZOMBIE, EntityType.HUSK, EntityType.SPIDER};
                  case RARE -> new EntityType[]{EntityType.SPIDER, EntityType.CAVE_SPIDER, EntityType.STRAY};
                  case EPIC -> new EntityType[]{EntityType.SKELETON, EntityType.VINDICATOR, EntityType.BREEZE};
                  case LEGENDARY -> new EntityType[]{EntityType.PILLAGER, EntityType.BLAZE, EntityType.WITCH};
                  case MYTHIC -> new EntityType[]{EntityType.PIGLIN_BRUTE, EntityType.BLAZE, EntityType.EVOKER};
                  case ABYSSAL -> new EntityType[]{EntityType.WITHER_SKELETON, EntityType.PIGLIN_BRUTE, EntityType.BLAZE};
               };

               for (int var7 = 0; var7 < var5 && var3 + var7 < var2; var7++) {
                  double var8 = var7 * Math.PI * 2.0 / Math.max(1, var5) + this.attacks;
                  Location var10 = this.boss.getLocation().clone().add(Math.cos(var8) * 3.0, 0.2, Math.sin(var8) * 3.0);

                  try {
                     Entity var11 = var10.getWorld().spawnEntity(var10, var6[NumberConversions.floor(Math.random() * var6.length)]);
                     var11.getPersistentDataContainer().set(this.minionKey, PersistentDataType.BYTE, (byte)1);
                     var11.getPersistentDataContainer().set(new NamespacedKey(plugin, "minion_parent"), PersistentDataType.STRING,
                        boss.getPersistentDataContainer().get(new NamespacedKey(plugin, "encounter_id"), PersistentDataType.STRING));
                     this.strengthenMinion(var11, "generic");
                     CombatVisuals.summon(var10,this.definition);
                     if (var11 instanceof Mob var12) {
                        var12.setTarget(var1);
                        var12.setPersistent(false);
                        var12.setRemoveWhenFarAway(true);
                     }

                     var11.customName(Component.text("Custodio del Eclipse · " + this.definition.name(), this.definition.tier().color));
                     var11.setCustomNameVisible(true);
                     this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                        if (var11.isValid()) {
                           var11.remove();
                        }
                     }, 700L);
                  } catch (IllegalArgumentException var13) {
                  }
               }

               this.boss.getWorld().spawnParticle(Particle.REVERSE_PORTAL, this.boss.getLocation(), 30, 1.0, 0.8, 1.0, 0.1);
               this.boss.getWorld().playSound(this.boss.getLocation(), Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1.0F, 0.8F);
            }
         } else {
            this.summonCustom(EntityType.CAVE_SPIDER, var1, 3, "spider_guard");
         }
      }

      private void summonCustom(EntityType var1, Player var2, int var3, String var4) {
         long var5 = this.boss
            .getWorld().getEntities()
            .stream()
            .filter(var1x -> var1x.getPersistentDataContainer().has(this.minionKey, PersistentDataType.BYTE))
            .filter(e -> java.util.Objects.equals(e.getPersistentDataContainer().get(new NamespacedKey(plugin,"minion_parent"), PersistentDataType.STRING), boss.getPersistentDataContainer().get(new NamespacedKey(plugin,"encounter_id"), PersistentDataType.STRING)))
            .count();
         if (var5 < 5L) {
            for (int var7 = 0; var7 < var3 && var5 + var7 < 5L; var7++) {
               try {
                  Location var8 = this.boss.getLocation().clone().add((var7 == 0 ? 1 : -1) * 2, 0.2, 1.5);
                  final Entity var9 = var8.getWorld().spawnEntity(var8, var1);
                  PersistentDataContainer var10 = var9.getPersistentDataContainer();
                  var10.set(this.minionKey, PersistentDataType.BYTE, (byte)1);
                  var10.set(
                     new NamespacedKey(this.plugin, "minion_parent"),
                     PersistentDataType.STRING,
                     (String)this.boss.getPersistentDataContainer().get(new NamespacedKey(this.plugin, "encounter_id"), PersistentDataType.STRING)
                  );
                  var10.set(new NamespacedKey(this.plugin, "minion_kind"), PersistentDataType.STRING, var4);
                  if (var9 instanceof Mob var11) {
                     var11.setPersistent(false);
                     var11.setRemoveWhenFarAway(true);
                     if (var4.equals("hoglin_guard")) {
                        var11.setTarget(null);
                        if (var11 instanceof Piglin var12) {
                           var12.setImmuneToZombification(true);
                        }

                        var11.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1200, 1, true, false));
                     } else if (var4.equals("spider_guard")) {
                        var11.setTarget(var2);
                        var11.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1200, 4, true, false));
                        var11.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 1200, 0, true, false));
                     } else {
                        var11.setTarget(var2);
                        if (var11 instanceof Bee var16) {
                           var16.setAnger(24000);
                        }
                     }
                  }

                  if(var9 instanceof LivingEntity minion){minion.removePotionEffect(PotionEffectType.STRENGTH);minion.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,1200,0,true,false));}
                  CombatVisuals.summon(var8,this.definition);
                  if (!var4.equals("fox_sheep")) {
                     this.strengthenMinion(var9, var4);
                  }

                  if (var9 instanceof Bee var14) {
                     BukkitTask[] var17 = new BukkitTask[1];
                     var17[0] = this.plugin
                        .getServer()
                        .getScheduler()
                        .runTaskTimer(
                           this.plugin,
                           () -> {
                              if (var14.isValid() && !var14.isDead()) {
                                 Player var2x = var14.getWorld()
                                    .getPlayers()
                                    .stream()
                                    .filter(var1xx -> var1xx.getLocation().distanceSquared(var14.getLocation()) < 1296.0)
                                    .min(
                                       (var1xx, var2xx) -> Double.compare(
                                          var1xx.getLocation().distanceSquared(var14.getLocation()), var2xx.getLocation().distanceSquared(var14.getLocation())
                                       )
                                    )
                                    .orElse(null);
                                 if (var2x != null) {
                                    var14.setAnger(24000);
                                    var14.setTarget(var2x);
                                 }
                              } else {
                                 var17[0].cancel();
                              }
                           },
                           1L,
                           20L
                        );
                  }

                  if (var9 instanceof Ageable var15 && var4.equals("sniffer_young")) {
                     var15.setBaby();
                     final BukkitTask[] var18 = new BukkitTask[1];
                     var18[0] = this.plugin
                        .getServer()
                        .getScheduler()
                        .runTaskTimer(
                           this.plugin,
                           new Runnable() {
                              int ticks;

                              @Override
                              public void run() {
                                 if (var9.isValid() && !var9.isDead() && this.ticks++ <= 900) {
                                    Player var1x = var9.getWorld()
                                       .getPlayers()
                                       .stream()
                                       .filter(var1xx -> var1xx.getLocation().distanceSquared(var9.getLocation()) < 324.0)
                                       .min(
                                          (var1xx, var2x) -> Double.compare(
                                             var1xx.getLocation().distanceSquared(var9.getLocation()), var2x.getLocation().distanceSquared(var9.getLocation())
                                          )
                                       )
                                       .orElse(null);
                                    if (var1x != null) {
                                       Vector var2x = var1x.getLocation().toVector().subtract(var9.getLocation().toVector()).setY(0);
                                       if (var2x.lengthSquared() > 0.2) {
                                          var9.setVelocity(var2x.normalize().multiply(0.32).setY(var9.getVelocity().getY()));
                                       }

                                       if (var1x.getLocation().distanceSquared(var9.getLocation()) < 7.839999999999999) {
                                          var1x.damage(8.0, (LivingEntity)var9);
                                          var1x.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 45, 0));
                                       }
                                    }
                                 } else {
                                    var18[0].cancel();
                                 }
                              }
                           },
                           10L,
                           10L
                        );
                  }

                  var9.customName(
                     Component.text(var4.equals("fox_sheep") ? "Presa del Zorro" : "Custodio del Eclipse · " + this.definition.name(), this.definition.tier().color)
                  );
                  var9.setCustomNameVisible(true);
                  this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                     if (var9.isValid()) {
                        var9.remove();
                     }
                  }, 900L);
               } catch (IllegalArgumentException var13) {
               }
            }
         }
      }

      private void strengthenMinion(Entity var1, String var2) {
         if (var1 instanceof LivingEntity var3) {
            var3.removePotionEffect(PotionEffectType.STRENGTH);var3.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH,1200,0,true,false));
            if(var2.equals("spider_guard")){var size=var3.getAttribute(Attribute.SCALE);if(size!=null)size.setBaseValue(0.55);var damage=var3.getAttribute(Attribute.ATTACK_DAMAGE);if(damage!=null)damage.setBaseValue(6+definition.tier().ordinal()*1.5);}
            double var4 = var2.equals("abeja_enfurecida") ? 100.0 : (var2.equals("sniffer_young") ? 100.0 : (var2.equals("spider_guard") ? 120.0 : 70.0));
            AttributeInstance var6 = var3.getAttribute(Attribute.MAX_HEALTH);
            if (var6 != null) {
               var6.setBaseValue(var4);
               var3.setHealth(var4);
            }

            AttributeInstance var7 = var3.getAttribute(Attribute.ARMOR);
            if (var7 != null) {
               var7.setBaseValue(var2.equals("spider_guard")?8:0);
            }

            AttributeInstance var8 = var3.getAttribute(Attribute.KNOCKBACK_RESISTANCE);
            if (var8 != null) {
               var8.setBaseValue(0.1);
            }
         }
      }

      private void shockwave(Player var1) {
         Location var2 = this.boss.getLocation();
         double var3 = this.definition.tier().scale * (this.definition.mini() ? 2.6 : 3.3) + 3.0;

         Particle var5 = switch (this.definition.tier()) {
            case COMMON, RARE -> Particle.CRIT;
            case EPIC -> Particle.WITCH;
            case LEGENDARY -> Particle.FLAME;
            case MYTHIC -> Particle.SOUL_FIRE_FLAME;
            case ABYSSAL -> Particle.DRAGON_BREATH;
         };
         this.boss.getWorld().playSound(var2, Sound.ENTITY_IRON_GOLEM_ATTACK, 1.6F, 0.55F);
         ParticleData.spawn(this.boss.getWorld(), var5, var2, 95, var3 / 3.0, 0.5, var3 / 3.0, 0.08);

         for (Player var7 : this.boss.getWorld().getPlayers()) {
            if (!CombatGuard.eligible(var7)) continue;
            if (var7.getLocation().distanceSquared(var2) <= var3 * var3) {
               var7.damage(Math.max(4.0, this.definition.tier().damage * 0.18), this.boss);
               this.debuff(var7);
               Vector var8 = var7.getLocation()
                  .toVector()
                  .subtract(var2.toVector())
                  .normalize()
                  .multiply(1.0 + this.definition.tier().ordinal() * 0.12)
                  .setY(1.05 + this.definition.tier().ordinal() * 0.12);
               var7.setVelocity(var8);
            }
         }

         var1.sendActionBar(Component.text("¡" + this.definition.name() + " desata una onda de choque!", this.definition.tier().color));
      }

      private void huntLunge(Player var1) {
         Location var2 = this.boss.getLocation();
         Vector var3 = var1.getLocation().toVector().subtract(var2.toVector()).setY(0).normalize();
         this.boss.getWorld().playSound(var2, Sound.ENTITY_RAVAGER_ROAR, 1.2F, 0.7F);
         this.boss.getWorld().spawnParticle(Particle.CLOUD, var2, 32, 0.7, 0.4, 0.7, 0.12);
         this.boss.setVelocity(var3.multiply(1.12).setY(0.34));
         if (var1.getLocation().distanceSquared(var2) <= 49.0) {
            var1.damage(Math.max(4.0, this.definition.tier().damage * 0.22), this.boss);
            this.debuff(var1);
            var1.setVelocity(var3.multiply(-0.75).setY(0.92 + this.definition.tier().ordinal() * 0.1));
         }

         var1.sendActionBar(Component.text("¡" + this.definition.name() + " se lanza hacia ti!", this.definition.tier().color));
      }

      private void targetedBurst(Player var1) {
         Location var2 = var1.getLocation();
         Vector var3 = var2.toVector().subtract(this.boss.getLocation().toVector()).normalize();
         Particle var4 = this.definition.tier() == BossCatalog.Tier.ABYSSAL
            ? Particle.DRAGON_BREATH
            : (
               this.definition.tier() == BossCatalog.Tier.MYTHIC
                  ? Particle.SOUL_FIRE_FLAME
                  : (this.definition.tier() == BossCatalog.Tier.LEGENDARY ? Particle.FLAME : Particle.WITCH)
            );
         this.boss.getWorld().playSound(this.boss.getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1.4F, 0.65F);
         HashSet var5 = new HashSet();

         for (int var6 = 1; var6 <= 14; var6++) {
            Location var7 = this.boss.getLocation().clone().add(var3.clone().multiply(var6)).add(0.0, 0.8, 0.0);
            ParticleData.spawn(this.boss.getWorld(), var4, var7, 8, 0.25, 0.25, 0.25, 0.04);

            for (Entity var9 : this.boss.getWorld().getNearbyEntities(var7, 1.0, 1.1, 1.0)) {
               if (var9 instanceof Player var10 && var5.add(var10.getUniqueId())) {
                  var10.damage(Math.max(4.0, this.definition.tier().damage * 0.14), this.boss);
                  this.debuff(var10);
                  Vector var11 = var10.getLocation()
                     .toVector()
                     .subtract(this.boss.getLocation().toVector())
                     .normalize()
                     .multiply(0.65)
                     .setY(0.78 + this.definition.tier().ordinal() * 0.08);
                  var10.setVelocity(var11);
               }
            }
         }

         var1.sendActionBar(Component.text("¡Ataque especial de " + this.definition.name() + "!", this.definition.tier().color));
      }

      private void debuff(Player var1) {
         switch (this.definition.tier()) {
            case COMMON:
               var1.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 50, 0));
               break;
            case RARE:
               var1.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 70, 0));
               break;
            case EPIC:
               var1.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 70, 0));
               break;
            case LEGENDARY:
               var1.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 75, 1));
               var1.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 35, 0));
               break;
            case MYTHIC:
               var1.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 90, 1));
               var1.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 90, 1));
               break;
            case ABYSSAL:
               var1.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 120, 1));
               var1.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 2));
         }
      }
   }

   private static final class Encounter {
      private final UUID id;
      private final BossCatalog.Definition definition;
      private final Location location;
      private final LivingEntity boss;
      private final String awakenedBy;
      private final String supportData;

      private Encounter(UUID var1, BossCatalog.Definition var2, Location var3, LivingEntity var4, String var5) {
         this(var1, var2, var3, var4, var5, null);
      }

      private Encounter(UUID var1, BossCatalog.Definition var2, Location var3, LivingEntity var4, String var5, String var6) {
         this.id = var1;
         this.definition = var2;
         this.location = var3;
         this.boss = var4;
         this.awakenedBy = var5;
         this.supportData = var6;
      }
   }

   public record TrackedEncounter(
      UUID id, String idKey, String name, String tier, boolean mini, Location location, boolean egg, double health, double maxHealth, String awakenedBy
   ) {
   }
}


