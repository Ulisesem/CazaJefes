package dev.cazajefes;
import net.kyori.adventure.text.format.TextColor;
final class RareAppearance {
 static final TextColor ORANGE=TextColor.color(0xFF9D24);
 static String roman(int value){int n=Math.max(1,Math.min(100,value));StringBuilder out=new StringBuilder();int[] numbers={100,90,50,40,10,9,5,4,1};String[] letters={"C","XC","L","XL","X","IX","V","IV","I"};for(int i=0;i<numbers.length;i++)while(n>=numbers[i]){out.append(letters[i]);n-=numbers[i];}return out.toString();}
 static String title(String id){id=RareAliases.canonical(id);var definition=RareDefinitions.get(id);if(definition!=null)return definition.title();String custom=ArmorRareRules.title(id);if(custom!=null)return RareNames.title(id,custom);StringBuilder out=new StringBuilder();for(String word:id.replace('_',' ').split(" "))if(!word.isBlank()){if(!out.isEmpty())out.append(' ');out.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase(java.util.Locale.ROOT));}return out.toString();}
 static String description(String id){id=RareAliases.canonical(id);var definition=RareDefinitions.get(id);if(definition!=null)return definition.description();String armor=ArmorRareRules.description(id);if(armor!=null)return armor;return switch(id){
 case "guardian"->"Reduce un 18% el daño recibido al consumir saturación de la pieza equipada.";
 case "brasas"->"25% de prender fuego al objetivo durante 2 segundos más 1 segundo por nivel al golpear.";
 case "paso_viento"->"Renueva Velocidad I durante 1,5 segundos al moverte con la pieza equipada; consume saturación.";
 case "saturacion"->"Recupera 0,5 más 0,5 de saturación por nivel cada 5 segundos. Cuenta la pieza de mayor nivel.";
 case "flame"->"Crea un efecto visual de llamas sobre tu casco al moverte. Es cosmético y consume saturación.";
 case "magneto"->"Envía al inventario los objetos extraídos con tu pico; el excedente cae al suelo.";
 case "despertar"->"Desbloquea las infusiones de una pieza de equipo de boss para aplicar rares compatibles.";
 default->"Consulta la compatibilidad y combina esta esencia con el equipo indicado.";};}
 static java.util.List<String> powerLines(String id){return powerText(description(id));}
 static java.util.List<String> powerText(String text){java.util.List<String> out=new java.util.ArrayList<>();String row="Poder:";for(String word:text.split(" ")){if(row.length()+word.length()+1>44){out.add(row);row="";}row+=(row.isEmpty()?"":" ")+word;}if(!row.isEmpty())out.add(row);return out;}
 static String cooldownMessage(String id,long milliseconds){return title(id)+" estará lista en "+Math.max(1,(milliseconds+999)/1000)+" segundos.";}
 static boolean magmaEssence(String id,boolean commandBook){return magmaRare(id)&&!commandBook;}
 static boolean magmaRare(String id){return RangedRareCatalog.get(id)!=null;}
 static boolean equipment(org.bukkit.Material material){String n=material.name();return n.endsWith("_SWORD")||n.endsWith("_AXE")||n.endsWith("_HELMET")||n.endsWith("_CHESTPLATE")||n.endsWith("_LEGGINGS")||n.endsWith("_BOOTS")||n.endsWith("_PICKAXE")||n.endsWith("_SHOVEL")||n.endsWith("_HOE")||n.equals("BOW")||n.equals("CROSSBOW")||n.equals("TRIDENT")||n.equals("MACE")||n.equals("ELYTRA")||n.equals("SHIELD");}
}
