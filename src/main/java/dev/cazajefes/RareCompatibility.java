package dev.cazajefes;
import java.util.*;
import org.bukkit.Material;
final class RareCompatibility {
 enum Group {ARMOR("Armaduras",Material.NETHERITE_CHESTPLATE),MELEE("Espadas y hachas",Material.NETHERITE_SWORD),RANGED("Arcos, ballestas y tridentes",Material.BOW),TOOLS("Picos y herramientas",Material.NETHERITE_PICKAXE),UNLOCK("Resonancia Primigenia",Material.NETHER_STAR),OTHER("Utilidad",Material.BOOK);final String title;final Material icon;Group(String title,Material icon){this.title=title;this.icon=icon;}}
 private static final Set<String> ARMOR=Set.of("guardian","paso_viento","hardy","durability","regeneration","nofalldamage","doublejump","fly","saturacion","flame");
 private static final Set<String> TOOLS=Set.of("fertilize","meltobsidian","smelt","spore","veinminer","treefeller","magneto");
 static Group property(String id){if(id.equals("despertar"))return Group.UNLOCK;if(ARMOR.contains(id))return Group.ARMOR;if(id.equals("treefeller"))return Group.MELEE;if(TOOLS.contains(id))return Group.TOOLS;if(Set.of("calllighting","slow","blinding").contains(id))return Group.RANGED;return Group.MELEE;}
 static Group material(Material m){String n=m.name();if(n.endsWith("_HELMET")||n.endsWith("_CHESTPLATE")||n.endsWith("_LEGGINGS")||n.endsWith("_BOOTS"))return Group.ARMOR;if(n.endsWith("_PICKAXE")||n.endsWith("_SHOVEL")||n.endsWith("_HOE"))return Group.TOOLS;if(n.endsWith("_SWORD")||n.endsWith("_AXE"))return Group.MELEE;if(m==Material.BOW||m==Material.CROSSBOW||m==Material.TRIDENT)return Group.RANGED;return Group.OTHER;}
 static String label(String id){return id.equals("treefeller")?"Solo hachas":id.equals("hardy")?"Solo pecheras":id.equals("flame")?"Solo cascos":property(id).title;}
 static boolean accepts(String id,Material material){if(id.equals("despertar"))return RareAppearance.equipment(material);if(id.equals("flame"))return material.name().endsWith("_HELMET");if(id.equals("hardy"))return material.name().endsWith("_CHESTPLATE");if(id.equals("treefeller"))return material.name().endsWith("_AXE");return property(id)==material(material);}
 static Group recipe(String key){if(key.equals("sello_resonancia_primigenia"))return Group.UNLOCK;if(key.startsWith("crear_esencia_rare_"))return property(key.substring("crear_esencia_rare_".length()));if(key.startsWith("pvp_gear_")){var relic=DuelCatalog.get(key.substring(9));return relic==null?Group.OTHER:material(relic.gear());}return Group.OTHER;}
 static boolean bossGear(String id,boolean marked){return marked||id!=null&&(id.startsWith("armadura_")||id.startsWith("arma_")||Set.of("espada_brezal","hacha_sismica","arco_ceniciento","lanza_magma","mandoble_vacio").contains(id));}
 static boolean canAwaken(boolean boss,boolean unlocked,int count){return boss&&!unlocked&&count==1;}
 static boolean blocked(boolean boss,boolean unlocked){return boss&&!unlocked;}
}
