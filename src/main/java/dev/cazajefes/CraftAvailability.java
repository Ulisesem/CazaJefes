package dev.cazajefes;

import java.util.*;
import org.bukkit.Material;

/** Craft restrictions only: item factories, loot and administrator grants remain available. */
final class CraftAvailability {
 static final Set<String> LEGACY_WEAPONS=Set.of("espada_brezal","hacha_sismica","arco_ceniciento","lanza_magma","mandoble_vacio");
 private static final Set<String> TRIBUTES=Set.of("tributo_cazador","tributo_grieta","tributo","tribute");
 private CraftAvailability(){}
 private static String key(String input){if(input==null)return "";String key=input.trim().toLowerCase(Locale.ROOT);int namespace=key.indexOf(':');return namespace<0?key:key.substring(namespace+1);}
 static boolean weapon(Material material){return material!=null&&(material.name().endsWith("_SWORD")||material.name().endsWith("_AXE")||switch(material){case BOW,CROSSBOW,TRIDENT,MACE->true;default->false;});}
 static boolean retired(String input){
  String id=key(input);
  if(LEGACY_WEAPONS.contains(id)||TRIBUTES.contains(id)||id.startsWith("armadura_")||id.startsWith("arma_set_")||id.startsWith("armas_set_"))return true;
  if(id.startsWith("pvp_gear_")||id.startsWith("pvp_material_")){String prefix=id.startsWith("pvp_gear_")?"pvp_gear_":"pvp_material_";var relic=DuelCatalog.get(id.substring(prefix.length()));return relic!=null&&!relic.armor();}
  return false;
 }
 static boolean allowed(String input){return !key(input).isEmpty()&&!retired(input);}
 /** The result check also covers newly configured weapons with unfamiliar recipe IDs. */
 static boolean allowed(String input,Material result){return allowed(input)&&!weapon(result);}
}
