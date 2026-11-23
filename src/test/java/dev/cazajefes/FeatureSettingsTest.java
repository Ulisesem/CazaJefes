package dev.cazajefes;
import org.junit.Test;import static org.junit.Assert.*;
public class FeatureSettingsTest {
 @Test public void sanctuaryPositionsRejectCollisionsAndBorderSlots(){var c=new org.bukkit.configuration.MemoryConfiguration();c.set("presentation.sanctuary.sigil-slot",20);c.set("presentation.sanctuary.amulet-slot",24);assertArrayEquals(new int[]{20,24},FeatureSettings.marketSlots(c));c.set("presentation.sanctuary.amulet-slot",20);assertArrayEquals(new int[]{21,23},FeatureSettings.marketSlots(c));c.set("presentation.sanctuary.sigil-slot",9);assertArrayEquals(new int[]{21,23},FeatureSettings.marketSlots(c));}
 @Test public void presentationNumbersAreFiniteAndBounded(){var c=new org.bukkit.configuration.MemoryConfiguration();c.set("x",Double.NaN);assertEquals(2,FeatureSettings.number(c,"x",2,1,8),0);c.set("x",99);assertEquals(8,FeatureSettings.number(c,"x",2,1,8),0);}
 @Test public void materialGroupsCanBeReorderedWithoutLosingOne(){var data=com.google.gson.JsonParser.parseString("{\"materialOrder\":[\"projectiles\",\"other\",\"defense\",\"catalysts\"]}").getAsJsonObject();assertEquals("projectiles",RecipeMenuSettings.read(data).materialOrder().getFirst());data.getAsJsonArray("materialOrder").set(0,new com.google.gson.JsonPrimitive("other"));assertThrows(IllegalArgumentException.class,()->RecipeMenuSettings.read(data));}
}
