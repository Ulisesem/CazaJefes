package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
public class CompassRecipeTest {
 @Test public void hasFourExactFragmentCornersAndOneOfEachCardinalIngredient(){
  String[] rows=CompassRecipe.shape();assertArrayEquals(new String[]{"FDF","ECI","FRF"},rows);
  assertEquals(4,String.join("",rows).chars().filter(c->c=='F').count());
  assertEquals(org.bukkit.Material.DIAMOND,CompassRecipe.MATERIALS.get('D'));
  assertEquals(org.bukkit.Material.EMERALD,CompassRecipe.MATERIALS.get('E'));
  assertEquals(org.bukkit.Material.IRON_INGOT,CompassRecipe.MATERIALS.get('I'));
  assertEquals(org.bukkit.Material.REDSTONE,CompassRecipe.MATERIALS.get('R'));
  assertEquals(org.bukkit.Material.COMPASS,CompassRecipe.MATERIALS.get('C'));
 }
 @Test public void announcesEveryBossWithItsCategoryColor(){
  var plain=net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText();
  for(var boss:BossCatalog.all()){
   var message=BossAnnouncements.awakened("Cazador",boss);assertEquals(boss.tier().color,message.color());
   assertTrue(plain.serialize(message).contains(boss.name()));assertTrue(plain.serialize(message).contains(boss.tier().display));
  }
 }
}
