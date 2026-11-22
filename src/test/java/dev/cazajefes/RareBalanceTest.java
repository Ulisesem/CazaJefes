package dev.cazajefes;

import com.google.gson.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Logger;
import org.junit.Test;
import static org.junit.Assert.*;

public class RareBalanceTest {
 @Test public void everyEffectTagIsAUsableCommandAlias(){
  for(var definition:RareDefinitions.ALL.values()){
   String title=definition.title();int start=title.lastIndexOf('('),end=title.lastIndexOf(')');
   if(start>=0&&end>start){String tag=title.substring(start+1,end);assertEquals(title,definition.id(),RareAliases.canonical(tag));assertEquals(tag.toLowerCase(Locale.ROOT),RareAliases.commandName(definition.id()));}
   assertEquals(definition.id(),RareAliases.canonical(definition.id()));
  }
  assertEquals("ultimo_aliento",RareAliases.canonical("LASTSTAND"));
  assertEquals("carga_inestable",RareAliases.canonical("Unstable"));
  assertEquals("poison",RareAliases.canonical("veneno"));
  assertEquals("vampiricregeneration",RareAliases.canonical("vampirismo"));
  assertEquals("paso_viento",RareAliases.canonical("PasoViento"));
  assertEquals("saturacion",RareAliases.canonical("Saturation"));
  assertEquals("brasas",RareAliases.canonical("Embers"));
  assertEquals("spore",RareAliases.canonical("Spore"));
  assertEquals("aura_esporas",RareAliases.canonical("aura_esporas"));
  assertFalse(RareAliases.commandNames().stream().anyMatch(name->name.contains("(")||name.contains(")")));
 }
 @Test public void oldAndNewMultiLevelProcsImproveWithLevel(){
  for(String id:List.of("vampiricregeneration","slow","weaken","blinding","calllighting","ultimo_aliento","piel_adamantina","carga_inestable","cerrojo_vacio","pulso_retorno","impacto_ariete","brasas")){
   int maximum=ArmorRareRules.maximum(id);double previous=0;for(int n=1;n<=maximum;n++){double chance=RareBalance.defaultChance(id,n);assertTrue(id,chance>previous);assertTrue(id,chance<1);previous=chance;}
  }
  assertEquals(3,RareDefinitions.get("vampiricregeneration").maximum());
  assertEquals(3,RareDefinitions.get("slow").maximum());
  assertEquals(1,RareDefinitions.get("disarm").maximum());
 }
 @Test public void cleanseClearsBothCursesAndPreventsTheirImmediateReturn(){
  UUID target=UUID.randomUUID();long until=System.currentTimeMillis()+30000;
  try{
   assertTrue(RareCounterplay.curse(target,"hex",until));assertTrue(RareCounterplay.curse(target,"ruin",until));
   assertTrue(RareCounterplay.healingBlocked(target,"hex"));assertTrue(RareCounterplay.healingBlocked(target,"ruin"));
   RareCounterplay.cleanse(target,4);
   assertFalse(RareCounterplay.healingBlocked(target));assertTrue(RareCounterplay.protectedFromCurse(target));
   assertFalse(RareCounterplay.curse(target,"ruin",until));
   assertFalse(RareCounterplay.healingBlocked(target,"hex"));
  }finally{RareCounterplay.remove(target);}
 }
 @Test public void jsonOverridesChangeActualRulesAndInvalidReloadKeepsTheLastBalance() throws Exception {
  Path folder=Files.createTempDirectory("rare-balance-test"),file=folder.resolve("rare-balance.json");Logger logger=Logger.getLogger("RareBalanceTest");
  try{
   Files.writeString(file,"{\"rares\":{\"carga_inestable\":{\"chanceByLevel\":[0.4,0.5,0.6,0.8],\"cooldownSeconds\":19,\"damageMultiplier\":1.5,\"durationMultiplier\":2}}}");
   assertTrue(RareBalance.load(file,logger));
   assertEquals(.8,RangedProcRules.chance("carga_inestable",4),.00001);
   assertEquals(18,RangedProcRules.extraDamage("carga_inestable",4),.00001);
   assertEquals(19,RareDefinitions.get("carga_inestable").cooldown());
   assertEquals(80,RareBalance.ticks("carga_inestable",40));
   assertTrue(RareDefinitions.get("carga_inestable").description().contains("40/50/60/80%"));
   assertTrue(RareDefinitions.get("carga_inestable").description().contains("Recarga 19 s"));
   JsonObject saved=JsonParser.parseString(Files.readString(file)).getAsJsonObject();
   assertEquals(19,saved.getAsJsonObject("rares").getAsJsonObject("carga_inestable").get("cooldownSeconds").getAsInt());
   for(String id:RareCollections.ids())assertTrue(id,saved.getAsJsonObject("rares").has(id));
   Files.writeString(file,"{broken");assertFalse(RareBalance.load(file,logger));
   assertEquals(18,RangedProcRules.extraDamage("carga_inestable",4),.00001);
  }finally{
   Files.deleteIfExists(file);assertTrue(RareBalance.load(file,logger));
   assertEquals(12,RareDefinitions.get("carga_inestable").cooldown());
   Files.deleteIfExists(file);Files.deleteIfExists(folder);
  }
 }
}
