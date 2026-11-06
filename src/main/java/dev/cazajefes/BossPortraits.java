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
      return tile(boss,boss.name(),color,lore);
   }

   static ItemStack tile(BossCatalog.Definition boss, String name, NamedTextColor color, String... lore) {
      String portrait = boss.custom().portrait();
      Material icon = portrait == null ? nativeHead(boss.entityType()) : Material.PLAYER_HEAD;
      ItemStack tile = PremiumStyle.tile(icon, name, color, true, lore);
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

   static String textureHash(String base64) {
      try {
         var root=com.google.gson.JsonParser.parseString(new String(java.util.Base64.getDecoder().decode(base64),StandardCharsets.UTF_8)).getAsJsonObject();
         var url=root.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
         var match=java.util.regex.Pattern.compile("https?://textures\\.minecraft\\.net/texture/([a-f0-9]{60,64})").matcher(url);
         if(match.matches()) return match.group(1);
      } catch (RuntimeException ignored) {
         // Invalid payload is reported by the individual boss loader.
      }
      throw new IllegalArgumentException("meteorTexture requiere una textura Base64 de textures.minecraft.net");
   }

   static ItemStack meteorHead(BossCatalog.Definition boss) {
      String hash=boss.custom().meteorTexture()==null?boss.custom().portrait():textureHash(boss.custom().meteorTexture());
      return texturedHead(hash);
   }

   static ItemStack texturedHead(String hash) {
      if(hash==null) return new ItemStack(Material.PLAYER_HEAD);
      var head=new ItemStack(Material.PLAYER_HEAD);
      var meta=(SkullMeta)head.getItemMeta();
      var profile=Bukkit.createPlayerProfile(UUID.nameUUIDFromBytes(hash.getBytes(StandardCharsets.UTF_8)));
      var textures=profile.getTextures();
      try {
         textures.setSkin(URI.create("https://textures.minecraft.net/texture/"+hash).toURL());
      } catch(java.net.MalformedURLException impossible) {
         throw new IllegalStateException(impossible);
      }
      profile.setTextures(textures);
      meta.setOwnerProfile(profile);
      head.setItemMeta(meta);
      return head;
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
