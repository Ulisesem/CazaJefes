package dev.cazajefes;

import java.util.*;
import org.bukkit.Material;
import org.junit.Test;
import static org.junit.Assert.*;

public class CraftAvailabilityTest {
 @Test public void allPreviousBossWeaponRecipesAndTributeConversionsAreRetired(){
  for(String id:CraftAvailability.LEGACY_WEAPONS){assertTrue(id,CraftAvailability.retired(id));assertFalse(CraftAvailability.allowed("cazajefes:"+id));}
  for(String id:List.of("tributo_cazador","tributo_grieta","arma_set_epic_0","arma_set_abyssal_1","armas_set_mythic_0","armadura_epic_helmet","armadura_set_abyssal_3"))assertTrue(id,CraftAvailability.retired(id));
 }
 @Test public void duelArmorRecipesRemainAvailableWhileDuelWeaponsDoNot(){
  for(var relic:DuelCatalog.ALL){String key="pvp_gear_"+relic.id();assertEquals(key,relic.armor(),CraftAvailability.allowed(key));assertEquals(key,relic.armor(),CraftAvailability.allowed(key,relic.gear()));assertEquals(relic.armor(),CraftAvailability.allowed("pvp_material_"+relic.id()));}
 }
 @Test public void essencesAndInfusionsForWeaponsAreStillCraftable(){
  for(String id:List.of("crear_esencia_rare_poison","crear_esencia_rare_carga_inestable","crear_esencia_rare_calllighting","crear_libro_rare_burst","rare_essence_attach","rare_essence_attach_3","rare_spell_upgrade_8","cristal_epic","esencia_rara","esencia_abisal","ranged_material_polvora_resonante","brujula_caza"))assertTrue(id,CraftAvailability.allowed(id));
  assertTrue(CraftAvailability.allowed("crear_esencia_rare_carga_inestable",Material.MAGMA_CREAM));
  assertTrue(CraftAvailability.allowed("rare_essence_attach",Material.PAPER));
 }
 @Test public void unfamiliarWeaponRecipeCannotBypassTheOutputGuard(){
  for(Material weapon:List.of(Material.IRON_SWORD,Material.NETHERITE_AXE,Material.BOW,Material.CROSSBOW,Material.TRIDENT,Material.MACE))assertFalse(weapon.name(),CraftAvailability.allowed("admin_custom_recipe",weapon));
  assertTrue(CraftAvailability.allowed("custom_pickaxe",Material.DIAMOND_PICKAXE));
  assertTrue(CraftAvailability.allowed("custom_duel_armor",Material.NETHERITE_CHESTPLATE));
  assertFalse(CraftAvailability.allowed(null));assertFalse(CraftAvailability.allowed(" "));
 }
}
