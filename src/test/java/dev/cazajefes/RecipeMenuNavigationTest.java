package dev.cazajefes;

import com.google.gson.*;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;

public class RecipeMenuNavigationTest {
 @Test public void homeRoutesFollowConfiguredIconsWithoutExposingBossArmor() {
  JsonObject config=JsonParser.parseString("{\"homeSlots\":[33,31,29,24,22,20]}").getAsJsonObject();
  var layout=RecipeMenuSettings.read(config);
  var routes=RecipePresentation.homeCategories(layout.homeSlots());
  assertEquals(RaresMenu.Category.ESSENCES,routes.get(33));
  assertEquals(RaresMenu.Category.SPELLS,routes.get(31));
  assertEquals(RaresMenu.Category.CRYSTALS,routes.get(29));
  assertEquals(RaresMenu.Category.MATERIALS,routes.get(24));
  assertEquals(RaresMenu.Category.CURRENCIES,routes.get(22));
  assertEquals(RaresMenu.Category.ALL,routes.get(20));
  assertFalse(routes.containsValue(RaresMenu.Category.ARMOR));
  assertFalse(routes.containsValue(RaresMenu.Category.WEAPONS));
  assertNull(routes.get(49));
 }
 @Test public void invalidLayoutsCannotReplaceNavigationOrCollapseCategories() {
  for(String slots:List.of("[4,22,24,29,31,33]","[45,22,24,29,31,33]",
      "[18,22,24,29,31,33]","[20,20,24,29,31,33]","[20,22,24]")) {
   JsonObject config=JsonParser.parseString("{\"homeSlots\":"+slots+"}").getAsJsonObject();
   assertThrows(IllegalArgumentException.class,()->RecipeMenuSettings.read(config));
  }
  for(int count:new int[]{0,29}) {
   JsonObject config=new JsonObject();config.addProperty("pageSize",count);
   assertThrows(IllegalArgumentException.class,()->RecipeMenuSettings.read(config));
  }
 }
 @Test public void returnsPreserveTheListThenReachTheCorrectParent() {
  for(var group:List.of(RareCompatibility.Group.ARMOR,RareCompatibility.Group.BOW,RareCompatibility.Group.CROSSBOW)) {
   assertEquals(RecipePresentation.Parent.LIST,RecipePresentation.parent(true,false,false,null,group));
   assertEquals(RecipePresentation.Parent.GROUPS,RecipePresentation.parent(false,false,false,null,group));
   assertEquals(RecipePresentation.Parent.ROOT,RecipePresentation.parent(false,true,false,null,group));
  }
  for(var tier:BossCatalog.Tier.values()) {
   assertEquals(RecipePresentation.Parent.LIST,RecipePresentation.parent(true,false,false,tier,null));
   assertEquals(RecipePresentation.Parent.RANKS,RecipePresentation.parent(false,false,false,tier,null));
   assertEquals(RecipePresentation.Parent.ROOT,RecipePresentation.parent(false,false,true,tier,null));
  }
  assertEquals(RecipePresentation.Parent.LIST,RecipePresentation.parent(true,false,false,null,null));
  assertEquals(RecipePresentation.Parent.ROOT,RecipePresentation.parent(false,false,false,null,null));
 }
 @Test public void removedCraftsStayHiddenWhileTheirEssencesAndMaterialsRemainAvailable() {
  assertFalse(RecipePresentation.visible("armadura_set_abyssal_0"));
  assertFalse(RecipePresentation.visible("armadura_gorath_2"));
  assertFalse(RecipePresentation.visible("sello_resonancia_primigenia"));
  assertTrue(RecipePresentation.visible("crear_esencia_rare_hardy"));
  assertTrue(RecipePresentation.visible("crear_esencia_rare_durability"));
  assertTrue(RecipePresentation.visible("pvp_gear_vigia"));
  assertTrue(RecipePresentation.visible("pvp_gear_quitina"));
  assertFalse(RecipePresentation.visible("pvp_gear_hemorragia"));
  assertFalse(RecipePresentation.visible("arma_set_abyssal_0"));
  assertFalse(RecipePresentation.visible("tributo_cazador"));
  assertFalse(RecipePresentation.visible("mandoble_vacio"));
  assertTrue(RecipePresentation.visible("ranged_material_resonador"));
  assertFalse(RecipePresentation.visible("pvp_material_hemorragia"));
 }
 @Test public void compassSectionContainsOnlyTheCompassAndBaseEssencesKeepTheirOwnHome() {
  assertTrue(RaresMenu.Category.CURRENCIES.matches("brujula_caza"));
  for(String id:List.of("tributo_cazador","esencia_rara","esencia_primordial","esencia_abisal"))
   assertFalse(RaresMenu.Category.CURRENCIES.matches(id));
  for(String id:List.of("esencia_rara","esencia_primordial","esencia_abisal")) {
   assertTrue(RecipePresentation.visible(id));
   assertTrue(RaresMenu.Category.MATERIALS.matches(id));
  }
 }
 @Test public void previousDefaultsUpgradeToBlueWithoutReplacingCustomLabelsOrColors() {
  JsonObject old=JsonParser.parseString("{\"accent\":\"CYAN_STAINED_GLASS_PANE\",\"labels\":{\"CURRENCIES\":\"Tributos\",\"ESSENCES\":\"Mis habilidades\"},\"pageSize\":14}").getAsJsonObject();
  assertTrue(RecipeMenuSettings.upgrade(old));
  var layout=RecipeMenuSettings.read(old);
  assertEquals(org.bukkit.Material.BLUE_STAINED_GLASS_PANE,layout.accent());
  assertEquals("Brújula",layout.labels().get("CURRENCIES"));
  assertEquals("Mis habilidades",layout.labels().get("ESSENCES"));
  assertEquals(14,layout.pageSize());
  assertFalse(RecipeMenuSettings.upgrade(old));
  JsonObject custom=JsonParser.parseString("{\"accent\":\"PURPLE_STAINED_GLASS_PANE\",\"labels\":{\"CURRENCIES\":\"Navegación\"}}").getAsJsonObject();
  assertEquals(org.bukkit.Material.PURPLE_STAINED_GLASS_PANE,RecipeMenuSettings.read(custom).accent());
  assertEquals("Navegación",RecipeMenuSettings.read(custom).labels().get("CURRENCIES"));
  assertEquals(org.bukkit.Material.BLUE_STAINED_GLASS_PANE,RecipeMenuSettings.read(new JsonObject()).accent());
 }
 @Test public void everyPageIsBalancedAndKeepsNavigationFree() {
  for(int count=1;count<=28;count++) {
   int[] slots=RareMenuStyle.slots(count);
   assertEquals(count,slots.length);
   Map<Integer,List<Integer>> rows=new HashMap<>();
   for(int slot:slots) {
    assertTrue(slot>=10&&slot<=43&&slot%9>=1&&slot%9<=7);
    rows.computeIfAbsent(slot/9,ignored->new ArrayList<>()).add(slot%9);
   }
   for(List<Integer> row:rows.values())for(int column:row)assertTrue(row.contains(8-column));
   assertEquals(count,Arrays.stream(slots).distinct().count());
  }
  assertEquals(1,RecipePresentation.pages(0,28));
  assertEquals(1,RecipePresentation.pages(28,28));
  assertEquals(2,RecipePresentation.pages(29,28));
  assertEquals(5,RecipePresentation.pages(28,6));
 }
}
