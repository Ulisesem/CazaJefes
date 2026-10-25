package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
public class UltimateTest {
 @Test public void everyBossHasItsOwnUltimateNameAndCombination(){assertEquals(76,BossAbilityCatalog.ALL.size());Set<String> names=new HashSet<>(),mechanics=new HashSet<>();for(var boss:BossCatalog.all()){var p=BossAbilityCatalog.ultimate(boss);assertTrue(names.add(p.name()));assertTrue(mechanics.add(p.pattern()+":"+p.effect()+":"+p.radius()+":"+p.pulses()+":"+p.angle()));assertTrue(p.radius()>=6&&p.radius()<=8);assertTrue(p.pulses()>=2&&p.pulses()<=4);assertNotNull(p.effect());}}
 @Test public void finalAttacksOnlyInThirdStageAndRespectCooldownAndCasting(){assertFalse(BossAbilities.ready(1,20000,0,false));assertFalse(BossAbilities.ready(2,20000,0,false));assertTrue(BossAbilities.ready(3,15000,15000,false));assertFalse(BossAbilities.ready(3,14999,15000,false));assertFalse(BossAbilities.ready(3,20000,0,true));assertEquals(10000,BossAbilities.SPECIAL_COOLDOWN);assertEquals(15000,BossAbilities.ULTIMATE_COOLDOWN);}
 @Test public void ringAndNovaHaveGapsToDodge(){var ring=BossAbilityCatalog.Pattern.RINGS;assertFalse(BossAbilityCatalog.hits(ring,0,0,0,3,8,0));assertTrue(BossAbilityCatalog.hits(ring,8,0,2,3,8,0));assertFalse(BossAbilityCatalog.hits(BossAbilityCatalog.Pattern.NOVA,0,0,0,3,8,0));assertTrue(BossAbilityCatalog.hits(BossAbilityCatalog.Pattern.NOVA,5,0,0,3,8,0));}
 @Test public void linesAndConesRespectTheirTelegraphedDirection(){assertTrue(BossAbilityCatalog.hits(BossAbilityCatalog.Pattern.CONE,4,0,0,2,8,0));assertFalse(BossAbilityCatalog.hits(BossAbilityCatalog.Pattern.CONE,-4,0,0,2,8,0));assertTrue(BossAbilityCatalog.hits(BossAbilityCatalog.Pattern.CROSS,0,4,0,2,8,0));assertFalse(BossAbilityCatalog.hits(BossAbilityCatalog.Pattern.CROSS,4,4,0,2,8,0));for(var pattern:BossAbilityCatalog.Pattern.values())assertFalse(BossAbilityCatalog.hits(pattern,100,100,0,3,8,0));}
}
