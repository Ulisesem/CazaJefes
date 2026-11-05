package dev.cazajefes;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;

import java.util.Locale;

enum BossStyle {
   EXECUTIONER(Material.NETHERITE_AXE, Color.fromRGB(255, 103, 52), Color.YELLOW, Particle.CRIT,
      "minecraft:item.armor.equip_netherite", "minecraft:block.anvil.land", "minecraft:block.chain.fall"),
   ARCHER(Material.SPECTRAL_ARROW, Color.fromRGB(173, 237, 112), Color.YELLOW, Particle.CRIT,
      "minecraft:item.crossbow.loading_start", "minecraft:item.crossbow.shoot", "minecraft:item.crossbow.loading_end"),
   ARCANE(Material.AMETHYST_SHARD, Color.fromRGB(181, 84, 255), Color.AQUA, Particle.ENCHANT,
      "minecraft:entity.evoker.prepare_attack", "minecraft:entity.evoker.cast_spell", "minecraft:block.amethyst_block.chime"),
   TIDE(Material.PRISMARINE_CRYSTALS, Color.fromRGB(65, 222, 234), Color.WHITE, Particle.SPLASH,
      "minecraft:block.conduit.ambient.short", "minecraft:entity.guardian.attack", "minecraft:block.conduit.deactivate"),
   EMBER(Material.BLAZE_ROD, Color.fromRGB(255, 88, 29), Color.YELLOW, Particle.FLAME,
      "minecraft:entity.blaze.ambient", "minecraft:entity.blaze.shoot", "minecraft:block.fire.extinguish"),
   VOID(Material.ECHO_SHARD, Color.fromRGB(134, 78, 250), Color.AQUA, Particle.SOUL,
      "minecraft:block.respawn_anchor.charge", "minecraft:entity.warden.sonic_boom", "minecraft:block.amethyst_block.resonate"),
   SKY(Material.FEATHER, Color.fromRGB(164, 205, 255), Color.WHITE, Particle.CLOUD,
      "minecraft:entity.breeze.inhale", "minecraft:entity.breeze.wind_burst", "minecraft:item.elytra.flying"),
   WILD(Material.HONEYCOMB, Color.fromRGB(129, 226, 89), Color.YELLOW, Particle.SPORE_BLOSSOM_AIR,
      "minecraft:block.moss.place", "minecraft:entity.ravager.attack", "minecraft:block.azalea_leaves.fall"),
   VENOM(Material.FERMENTED_SPIDER_EYE, Color.fromRGB(168, 233, 71), Color.FUCHSIA, Particle.WITCH,
      "minecraft:entity.spider.ambient", "minecraft:entity.spider.hurt", "minecraft:block.slime_block.fall"),
   BASTION(Material.IRON_BLOCK, Color.fromRGB(251, 184, 98), Color.WHITE, Particle.ELECTRIC_SPARK,
      "minecraft:block.iron_door.open", "minecraft:entity.iron_golem.attack", "minecraft:block.iron_door.close");

   enum Beat { PREPARE, IMPACT, RECOVERY }

   final Material prop;
   final Color primary, accent;
   final Particle particle;
   final String prepare, impact, recovery;

   BossStyle(Material prop, Color primary, Color accent, Particle particle, String prepare, String impact, String recovery) {
      this.prop = prop;
      this.primary = primary;
      this.accent = accent;
      this.particle = particle;
      this.prepare = prepare;
      this.impact = impact;
      this.recovery = recovery;
   }

   static BossStyle of(EntityType type) {
      return switch (type) {
         case ZOMBIE, HUSK, ZOMBIFIED_PIGLIN, VINDICATOR, PIGLIN_BRUTE, PIGLIN, WITHER_SKELETON, GIANT -> EXECUTIONER;
         case SKELETON, STRAY, BOGGED, PILLAGER, PARROT -> ARCHER;
         case WITCH, EVOKER, ILLUSIONER -> ARCANE;
         case GUARDIAN, ELDER_GUARDIAN, DROWNED, DOLPHIN, SQUID, GLOW_SQUID, AXOLOTL, TURTLE, NAUTILUS -> TIDE;
         case BLAZE, GHAST, MAGMA_CUBE, STRIDER, HOGLIN, ZOGLIN -> EMBER;
         case WARDEN, SHULKER, ENDERMAN, ENDERMITE, CREAKING, WITHER, ENDER_DRAGON -> VOID;
         case BREEZE, PHANTOM, VEX, BAT, ALLAY -> SKY;
         case SPIDER, CAVE_SPIDER, SILVERFISH, SLIME -> VENOM;
         case IRON_GOLEM, RAVAGER, ARMADILLO, POLAR_BEAR, GOAT -> BASTION;
         default -> WILD;
      };
   }

   String key(Beat beat) {
      return "cazajefes:" + name().toLowerCase(Locale.ROOT) + "." + beat.name().toLowerCase(Locale.ROOT);
   }

   String sound(Beat beat) {
      return switch (beat) {
         case PREPARE -> prepare;
         case IMPACT -> impact;
         case RECOVERY -> recovery;
      };
   }
}
