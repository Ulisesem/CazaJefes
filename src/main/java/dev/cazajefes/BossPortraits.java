package dev.cazajefes;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.entity.EntityType;
import org.bukkit.Bukkit;
import net.kyori.adventure.text.format.NamedTextColor;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

final class BossPortraits {
   private BossPortraits() {}

   static ItemStack tile(BossCatalog.Definition boss, NamedTextColor color, String... lore) {
      String portrait = boss.custom().portrait();
      Material icon = portrait == null ? nativeHead(boss.entityType()) : Material.PLAYER_HEAD;
      ItemStack tile = PremiumStyle.tile(icon, boss.name(), color, true, lore);
      if (portrait != null && tile.getItemMeta() instanceof SkullMeta meta) {
         var profile = Bukkit.createPlayerProfile(UUID.nameUUIDFromBytes(
            ("cazajefes:" + portrait).getBytes(StandardCharsets.UTF_8)));
         var textures = profile.getTextures();
         try {
            textures.setSkin(URI.create("https://textures.minecraft.net/texture/" + portrait).toURL());
         } catch (java.net.MalformedURLException impossible) {
            throw new IllegalStateException(impossible);
         }
         profile.setTextures(textures);
         meta.setOwnerProfile(profile);
         tile.setItemMeta(meta);
      }
      return tile;
   }

   static Material nativeHead(EntityType species) {
      return switch (species) {
         case ZOMBIE, GIANT -> Material.ZOMBIE_HEAD;
         case SKELETON -> Material.SKELETON_SKULL;
         case WITHER, WITHER_SKELETON -> Material.WITHER_SKELETON_SKULL;
         case CREEPER -> Material.CREEPER_HEAD;
         case PIGLIN, PIGLIN_BRUTE -> Material.PIGLIN_HEAD;
         case ENDER_DRAGON -> Material.DRAGON_HEAD;
         default -> {
            Material egg = Material.matchMaterial(species.name() + "_SPAWN_EGG");
            yield egg == null ? Material.ENDER_EYE : egg;
         }
      };
   }
}
