package dev.cazajefes;
import java.util.*;
import org.bukkit.Material;
import org.bukkit.potion.PotionType;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.PotionMeta;
final class RecipeAlchemy {
 record Profile(PotionType potion,Material reagent,BossCatalog.Tier rank){}
 static final Map<String,Profile> PROFILES=Map.ofEntries(
  Map.entry("poison",new Profile(PotionType.POISON,Material.SPIDER_EYE,BossCatalog.Tier.COMMON)),
  Map.entry("backstab",new Profile(PotionType.SWIFTNESS,Material.FLINT,BossCatalog.Tier.RARE)),
  Map.entry("blinding",new Profile(PotionType.NIGHT_VISION,Material.INK_SAC,BossCatalog.Tier.RARE)),
  Map.entry("calllighting",new Profile(PotionType.STRENGTH,Material.LIGHTNING_ROD,BossCatalog.Tier.MYTHIC)),
  Map.entry("confuse",new Profile(PotionType.WEAKNESS,Material.FERMENTED_SPIDER_EYE,BossCatalog.Tier.EPIC)),
  Map.entry("disarm",new Profile(PotionType.WEAKNESS,Material.IRON_NUGGET,BossCatalog.Tier.EPIC)),
  Map.entry("slow",new Profile(PotionType.SLOWNESS,Material.COBWEB,BossCatalog.Tier.COMMON)),
  Map.entry("vampiricregeneration",new Profile(PotionType.REGENERATION,Material.GHAST_TEAR,BossCatalog.Tier.LEGENDARY)),
  Map.entry("weaken",new Profile(PotionType.WEAKNESS,Material.BONE,BossCatalog.Tier.COMMON)),
  Map.entry("execute",new Profile(PotionType.STRENGTH,Material.DIAMOND,BossCatalog.Tier.MYTHIC)),
  Map.entry("bleed",new Profile(PotionType.POISON,Material.SWEET_BERRIES,BossCatalog.Tier.EPIC)),
  Map.entry("fertilize",new Profile(PotionType.REGENERATION,Material.BONE_MEAL,BossCatalog.Tier.COMMON)),
  Map.entry("meltobsidian",new Profile(PotionType.FIRE_RESISTANCE,Material.OBSIDIAN,BossCatalog.Tier.RARE)),
  Map.entry("smelt",new Profile(PotionType.FIRE_RESISTANCE,Material.COAL,BossCatalog.Tier.COMMON)),
  Map.entry("spore",new Profile(PotionType.POISON,Material.BROWN_MUSHROOM,BossCatalog.Tier.RARE)),
  Map.entry("veinminer",new Profile(PotionType.STRENGTH,Material.RAW_IRON,BossCatalog.Tier.EPIC)),
  Map.entry("treefeller",new Profile(PotionType.SWIFTNESS,Material.OAK_LOG,BossCatalog.Tier.RARE)),
  Map.entry("magneto",new Profile(PotionType.SWIFTNESS,Material.IRON_INGOT,BossCatalog.Tier.EPIC)),
  Map.entry("burst",new Profile(PotionType.STRENGTH,Material.GUNPOWDER,BossCatalog.Tier.RARE)),
  Map.entry("craftitem",new Profile(PotionType.SWIFTNESS,Material.CRAFTING_TABLE,BossCatalog.Tier.RARE)),
  Map.entry("fireresistance",new Profile(PotionType.FIRE_RESISTANCE,Material.MAGMA_CREAM,BossCatalog.Tier.RARE)),
  Map.entry("greaterburst",new Profile(PotionType.STRENGTH,Material.FIRE_CHARGE,BossCatalog.Tier.LEGENDARY)),
  Map.entry("haste",new Profile(PotionType.SWIFTNESS,Material.SUGAR,BossCatalog.Tier.EPIC)),
  Map.entry("invisibility",new Profile(PotionType.INVISIBILITY,Material.GOLDEN_CARROT,BossCatalog.Tier.LEGENDARY)),
  Map.entry("magicbag",new Profile(PotionType.INVISIBILITY,Material.ENDER_CHEST,BossCatalog.Tier.MYTHIC)),
  Map.entry("repairitem",new Profile(PotionType.REGENERATION,Material.ANVIL,BossCatalog.Tier.EPIC)),
  Map.entry("durability",new Profile(PotionType.REGENERATION,Material.PHANTOM_MEMBRANE,BossCatalog.Tier.EPIC)),
  Map.entry("regeneration",new Profile(PotionType.REGENERATION,Material.GLISTERING_MELON_SLICE,BossCatalog.Tier.LEGENDARY)),
  Map.entry("nofalldamage",new Profile(PotionType.SLOW_FALLING,Material.FEATHER,BossCatalog.Tier.RARE)),
  Map.entry("doublejump",new Profile(PotionType.LEAPING,Material.RABBIT_FOOT,BossCatalog.Tier.EPIC)),
  Map.entry("fly",new Profile(PotionType.SLOW_FALLING,Material.ELYTRA,BossCatalog.Tier.ABYSSAL)),
  Map.entry("hardy",new Profile(PotionType.STRENGTH,Material.NETHERITE_SCRAP,BossCatalog.Tier.EPIC)),
  Map.entry("saturacion",new Profile(PotionType.REGENERATION,Material.GOLDEN_APPLE,BossCatalog.Tier.MYTHIC)),
  Map.entry("flame",new Profile(PotionType.FIRE_RESISTANCE,Material.BLAZE_POWDER,BossCatalog.Tier.EPIC)),
  Map.entry("vampirismo",new Profile(PotionType.REGENERATION,Material.CRIMSON_FUNGUS,BossCatalog.Tier.MYTHIC)),
  Map.entry("veneno",new Profile(PotionType.POISON,Material.FERMENTED_SPIDER_EYE,BossCatalog.Tier.RARE)),
  Map.entry("guardian",new Profile(PotionType.TURTLE_MASTER,Material.NAUTILUS_SHELL,BossCatalog.Tier.LEGENDARY)),
  Map.entry("brasas",new Profile(PotionType.FIRE_RESISTANCE,Material.BLAZE_ROD,BossCatalog.Tier.LEGENDARY)),
  Map.entry("paso_viento",new Profile(PotionType.SWIFTNESS,Material.BREEZE_ROD,BossCatalog.Tier.MYTHIC)),
  Map.entry("espada_brezal",new Profile(PotionType.POISON,Material.MOSS_BLOCK,BossCatalog.Tier.EPIC)),
  Map.entry("hacha_sismica",new Profile(PotionType.STRENGTH,Material.ECHO_SHARD,BossCatalog.Tier.LEGENDARY)),
  Map.entry("arco_ceniciento",new Profile(PotionType.FIRE_RESISTANCE,Material.CHARCOAL,BossCatalog.Tier.LEGENDARY)),
  Map.entry("lanza_magma",new Profile(PotionType.FIRE_RESISTANCE,Material.MAGMA_BLOCK,BossCatalog.Tier.MYTHIC)),
  Map.entry("mandoble_vacio",new Profile(PotionType.INVISIBILITY,Material.ENDER_EYE,BossCatalog.Tier.ABYSSAL)));
 static Profile profile(String id){Profile p=PROFILES.get(id);if(p==null)throw new IllegalArgumentException("Rare desconocida: "+id);return p;}
 static RecipeChoice potionChoice(PotionType type){ItemStack potion=new ItemStack(Material.POTION);PotionMeta meta=(PotionMeta)potion.getItemMeta();meta.setBasePotionType(type);potion.setItemMeta(meta);return new RecipeChoice.ExactChoice(potion);}
}
