package dev.cazajefes;
import java.util.Map;
import org.bukkit.Material;
/** Cardinal gemstones/metals and four exact tribute fragments around a normal compass. */
final class CompassRecipe {
 static final Map<Character,Material> MATERIALS=Map.of('D',Material.DIAMOND,'E',Material.EMERALD,
     'I',Material.IRON_INGOT,'R',Material.REDSTONE,'C',Material.COMPASS);
 private CompassRecipe(){}
 static String[] shape(){return new String[]{"FDF","ECI","FRF"};}
}
