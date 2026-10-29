package dev.cazajefes;
import net.kyori.adventure.text.format.TextColor;
final class RareAppearance {
 static final TextColor ORANGE=TextColor.color(0xFF9D24);
 static String roman(int value){int n=Math.max(1,Math.min(100,value));StringBuilder out=new StringBuilder();int[] numbers={100,90,50,40,10,9,5,4,1};String[] letters={"C","XC","L","XL","X","IX","V","IV","I"};for(int i=0;i<numbers.length;i++)while(n>=numbers[i]){out.append(letters[i]);n-=numbers[i];}return out.toString();}
 static String title(String id){var definition=RareDefinitions.get(id);if(definition!=null)return definition.title();String custom=ArmorRareRules.title(id);if(custom!=null)return custom;StringBuilder out=new StringBuilder();for(String word:id.replace('_',' ').split(" "))if(!word.isBlank()){if(!out.isEmpty())out.append(' ');out.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase(java.util.Locale.ROOT));}return out.toString();}
 static boolean equipment(org.bukkit.Material material){String n=material.name();return n.endsWith("_SWORD")||n.endsWith("_AXE")||n.endsWith("_HELMET")||n.endsWith("_CHESTPLATE")||n.endsWith("_LEGGINGS")||n.endsWith("_BOOTS")||n.endsWith("_PICKAXE")||n.endsWith("_SHOVEL")||n.endsWith("_HOE")||n.equals("BOW")||n.equals("CROSSBOW")||n.equals("TRIDENT")||n.equals("MACE")||n.equals("ELYTRA")||n.equals("SHIELD");}
}
