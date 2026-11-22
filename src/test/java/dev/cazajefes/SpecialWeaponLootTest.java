package dev.cazajefes;

import com.google.gson.*;
import java.util.*;
import org.bukkit.entity.EntityType;
import org.junit.Test;
import static org.junit.Assert.*;

public class SpecialWeaponLootTest {
 private JsonObject combat(String rule){return JsonParser.parseString("{\"specialWeaponLoot\":"+rule+"}").getAsJsonObject();}
 @Test public void representativeFamiliesGetTheCorrectDefaultWeapon(){
  Map<EntityType,String> expected=Map.of(EntityType.ZOMBIE,"hacha_sismica",EntityType.HUSK,"hacha_sismica",EntityType.EVOKER,"espada_brezal",EntityType.BOGGED,"arco_ceniciento",EntityType.PILLAGER,"arco_ceniciento",EntityType.BLAZE,"lanza_magma",EntityType.MAGMA_CUBE,"lanza_magma",EntityType.WARDEN,"mandoble_vacio",EntityType.WITHER_SKELETON,"mandoble_vacio");
  expected.forEach((type,item)->{var rule=SpecialWeaponLoot.read(type,BossCatalog.Tier.EPIC,false,null);assertTrue(rule.enabled());assertEquals(item,rule.item());assertEquals(.03,rule.chance(),.000001);assertEquals(1,rule.level());});
 }
 @Test public void chancesScaleWithCategoryAndOnlyHigherBossesGetExtraLoot(){
  for(var tier:EquipmentSets.RANKS){var rule=SpecialWeaponLoot.read(EntityType.ZOMBIE,tier,false,null);assertEquals(.03+.01*(tier.ordinal()-2),rule.chance(),.000001);assertNotNull(SpecialWeaponLoot.roll(rule,0));assertNull(SpecialWeaponLoot.roll(rule,rule.chance()));}
  assertFalse(SpecialWeaponLoot.read(EntityType.ZOMBIE,BossCatalog.Tier.RARE,false,null).enabled());
  assertFalse(SpecialWeaponLoot.read(EntityType.ZOMBIE,BossCatalog.Tier.ABYSSAL,true,null).enabled());
  assertFalse(SpecialWeaponLoot.read(EntityType.BEE,BossCatalog.Tier.ABYSSAL,false,null).enabled());
 }
 @Test public void explicitConfigurationCanSelectEveryExistingWeaponButNoArmor(){
  List<String> weapons=new ArrayList<>(CraftAvailability.LEGACY_WEAPONS);for(var relic:DuelCatalog.ALL)if(!relic.armor())weapons.add("pvp_gear_"+relic.id());
  assertEquals(9,weapons.size());
  for(String item:weapons){var rule=SpecialWeaponLoot.read(EntityType.ZOMBIE,BossCatalog.Tier.EPIC,false,combat("{\"enabled\":true,\"item\":\""+item+"\",\"chance\":1,\"level\":5}"));assertEquals(new SpecialWeaponLoot.Selection(item,5),SpecialWeaponLoot.roll(rule,.999));}
  assertFalse(SpecialWeaponLoot.supported("pvp_gear_vigia"));assertFalse(SpecialWeaponLoot.supported("arma_set_abyssal_0"));
 }
 @Test public void malformedConfigurationFailsClosedRatherThanGrantingDefaultGear(){
  for(String bad:List.of("null","[]","true","{\"enabled\":\"true\"}","{\"item\":\"unknown\"}","{\"chance\":-1}","{\"chance\":1.01}","{\"chance\":\"0.5\"}","{\"level\":0}","{\"level\":6}","{\"level\":1.5}","{\"enabled\":false}")){
   var rule=SpecialWeaponLoot.read(EntityType.ZOMBIE,BossCatalog.Tier.EPIC,false,combat(bad));assertFalse(bad,rule.enabled());assertNull(SpecialWeaponLoot.roll(rule,0));
  }
 }
}
