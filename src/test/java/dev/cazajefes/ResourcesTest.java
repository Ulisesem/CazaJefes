package dev.cazajefes;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;
import static org.junit.Assert.*;
public class ResourcesTest {
 private YamlConfiguration load(String name){InputStream stream=getClass().getResourceAsStream("/"+name);assertNotNull(stream);return YamlConfiguration.loadConfiguration(new InputStreamReader(stream,StandardCharsets.UTF_8));}
 @Test public void descriptorsAndDefaultConfigsParse(){
  var descriptor=load("plugin.yml");assertEquals("1.40.0",descriptor.getString("version"));assertNotNull(descriptor.getConfigurationSection("commands.bestiario"));assertNotNull(descriptor.getConfigurationSection("commands.rares"));assertNotNull(descriptor.getConfigurationSection("commands.mercado"));assertTrue(descriptor.getStringList("commands.bestiario.aliases").contains("bestirio"));
  assertEquals(3,load("config.yml").getInt("schema-version"));assertEquals(6,load("config.yml").getInt("combat.pillager-rocket-charges"));assertEquals(3,load("recipes.yml").getInt("schema-version"));assertNotNull(load("rares.yml").getConfigurationSection("magneto"));
 }
 @Test public void defaultBossOverridesUseResolvableCanonicalIds(){
  var bosses=load("config.yml").getConfigurationSection("bosses");assertNotNull(bosses);
  for(String id:bosses.getKeys(false))if(!id.equals("attack-interval-ticks")){var boss=BossCatalog.get(id);assertNotNull(id,boss);assertEquals(boss.id(),id);}
 }
 @Test public void contentGridHasNoDuplicateOrFooterSlots(){
  Set<Integer> seen=new HashSet<>();assertEquals(28,PremiumStyle.GRID.length);
  for(int slot:PremiumStyle.GRID){assertTrue(seen.add(slot));assertTrue(slot>=9&&slot<45);assertNotEquals(0,slot%9);assertNotEquals(8,slot%9);}
 }
 @org.junit.Test public void languageFilesProvideThreeDistinctTexturedHeadsAndMatchingKeys() throws Exception {
  var hashes=new java.util.HashSet<String>();
  java.util.Set<String> keys=null;
  for(String id:java.util.List.of("es","en","pt")){
   try(var input=getClass().getResourceAsStream("/lenguajes/"+id+".json")){
    org.junit.Assert.assertNotNull(input);
    var data=com.google.gson.JsonParser.parseReader(new java.io.InputStreamReader(input,java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
    var language=Languages.parse(id,data,data);
    org.junit.Assert.assertEquals(id,language.id());
    org.junit.Assert.assertTrue(hashes.add(language.textureHash()));
    if(keys==null)keys=data.getAsJsonObject("messages").keySet();
    else org.junit.Assert.assertEquals(keys,data.getAsJsonObject("messages").keySet());
   }
  }
 }
}
