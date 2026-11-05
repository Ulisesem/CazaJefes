package dev.cazajefes;

import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.net.URI;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

final class CinematicCues implements Listener {
   private static final org.bukkit.NamespacedKey MINION = new org.bukkit.NamespacedKey("cazajefes", "boss_minion");
   private static final org.bukkit.NamespacedKey HIT_FX = new org.bukkit.NamespacedKey("cazajefes", "minion_hit_fx_tick");
   private final HashSet<UUID> loaded = new HashSet<>();
   private final ModelEngineCues models;
   private final String url;
   private final byte[] sha1;
   private final UUID packId;

   CinematicCues(CazaJefesPlugin plugin) {
      File file = new File(plugin.getDataFolder(), "cinematics.yml");
      if (!file.exists()) plugin.saveResource("cinematics.yml", false);
      var config = YamlConfiguration.loadConfiguration(file);
      models = new ModelEngineCues(plugin, config.getConfigurationSection("models"));
      String configuredUrl = config.getString("audio-pack.url", "");
      String hash = config.getString("audio-pack.sha1", "");
      if (validPack(configuredUrl, hash)) {
         url = configuredUrl;
         sha1 = HexFormat.of().parseHex(hash);
         packId = UUID.nameUUIDFromBytes(sha1);
         for (Player player : plugin.getServer().getOnlinePlayers()) offer(player);
      } else {
         url = null;
         sha1 = null;
         packId = null;
         if (!configuredUrl.isBlank() || !hash.isBlank()) plugin.getLogger().warning("cinematics.yml: audio-pack requiere URL HTTPS y SHA-1.");
      }
   }

   static boolean validPack(String url, String hash) {
      if (hash == null || !hash.matches("(?i)[0-9a-f]{40}") || url == null) return false;
      try {
         URI uri = URI.create(url);
         return "https".equalsIgnoreCase(uri.getScheme()) && uri.getHost() != null && uri.getUserInfo() == null;
      } catch (IllegalArgumentException invalid) {
         return false;
      }
   }

   boolean play(LivingEntity boss, BossCatalog.Definition definition, BossStyle.Beat beat, List<Player> audience) {
      return play(boss, definition, beat, audience, true);
   }

   boolean play(LivingEntity boss, BossCatalog.Definition definition, BossStyle.Beat beat, List<Player> audience, boolean sounds) {
      Location at = boss.getLocation();
      BossStyle style = BossStyle.of(boss.getType());
      float pitch = switch (beat) { case PREPARE -> .86f; case IMPACT -> .98f; case RECOVERY -> 1.28f; };
      if (sounds) for (Player player : audience) {
         if (loaded.contains(player.getUniqueId())) player.playSound(at, style.key(beat), SoundCategory.HOSTILE, .72f, pitch);
         else player.playSound(at, style.sound(beat), SoundCategory.HOSTILE, .72f, pitch);
      }
      return models.play(boss, definition, beat);
   }

   boolean play(LivingEntity boss, BossCatalog.Definition definition, BossStyle.Beat beat) {
      return play(boss, definition, beat, CinematicFX.audience(boss.getLocation(), 32));
   }

   void remove(UUID boss) {
      models.remove(boss);
   }

   void stop() {
      models.stop();
      loaded.clear();
   }

   private void offer(Player player) {
      if (packId != null) player.addResourcePack(packId, url, sha1, "Audio de combate de CazaJefes", false);
   }

   @EventHandler public void join(PlayerJoinEvent event) { offer(event.getPlayer()); }

   @EventHandler public void status(PlayerResourcePackStatusEvent event) {
      if (packId == null || !packId.equals(event.getID())) return;
      if (event.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) loaded.add(event.getPlayer().getUniqueId());
      else loaded.remove(event.getPlayer().getUniqueId());
   }

   @EventHandler public void quit(PlayerQuitEvent event) { loaded.remove(event.getPlayer().getUniqueId()); }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void minionHit(EntityDamageByEntityEvent event) {
      if (!(event.getEntity() instanceof Player target) || !CombatGuard.eligible(target) || event.getFinalDamage() <= 0) return;
      var source = event.getDamager();
      if (source instanceof org.bukkit.entity.Projectile projectile
          && projectile.getShooter() instanceof org.bukkit.entity.Entity shooter) source = shooter;
      if (!(source instanceof LivingEntity minion) || !minion.getPersistentDataContainer().has(MINION, PersistentDataType.BYTE)) return;
      var data = minion.getPersistentDataContainer();
      int tick = org.bukkit.Bukkit.getCurrentTick();
      Integer last = data.get(HIT_FX, PersistentDataType.INTEGER);
      if (last != null && tick - last < 15) return;
      data.set(HIT_FX, PersistentDataType.INTEGER, tick);
      CinematicFX.minionHit(minion, target);
   }
}
