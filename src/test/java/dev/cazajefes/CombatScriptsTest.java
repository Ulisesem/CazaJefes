package dev.cazajefes;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Test;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class CombatScriptsTest {
   @Test public void gorathExampleHasThreePhasesAndAllHordeRoles() throws Exception {
      var config = new YamlConfiguration();
      config.load(Path.of("src/main/resources/combate.yml").toFile());
      var section = config.getConfigurationSection("skills");
      var skills = new HashMap<String,CombatScripts.Skill>();
      for (String id : section.getKeys(false)) skills.put(id,CombatScripts.readSkill(id,section.getConfigurationSection(id)));
      var plan = CombatScripts.readPlan(config.getConfigurationSection("bosses.gorath_quebrantador_de_mundos"), skills);
      assertEquals(.7,plan.phase2(),.001);
      assertEquals(.3,plan.phase3(),.001);
      assertEquals(3,plan.phases().size());
      assertTrue(plan.phases().get(3).stream().anyMatch(CombatScripts.Skill::ultimate));
      assertTrue(plan.phases().get(1).stream().anyMatch(CombatScripts.Skill::summon));
      assertEquals(6,plan.horde().size());
      assertEquals(5,plan.horde().stream().map(CombatScripts.Minion::role).distinct().count());
   }

   @Test public void rotationsShareSkillCooldownAcrossPhaseChanges() {
      var section = new YamlConfiguration();
      section.set("cooldown",20);
      var skill = CombatScripts.readSkill("test_skill",section);
      var rotation = new CombatScripts.Rotation();
      assertNull(rotation.next(List.of(skill),1,0,true));
      assertSame(skill,rotation.next(List.of(skill),1,4500,true));
      assertNull(rotation.next(List.of(skill),2,5000,true));
      assertNull(rotation.next(List.of(skill),2,10000,true));
      assertSame(skill,rotation.next(List.of(skill),2,24500,true));
   }

   @Test public void invalidConfigIsRejectedAndThreatDecays() {
      var section = new YamlConfiguration();
      section.set("warning-ticks",11);
      assertThrows(IllegalArgumentException.class,() -> CombatScripts.readSkill("bad_skill",section));
      assertThrows(IllegalArgumentException.class,() -> CombatScripts.readHorde(List.of("ZOMBIE:TANK")));
      assertEquals(20,ThreatTable.score(40,12000),.0001);
      assertEquals(0,ThreatTable.score(40,60000),.0001);
   }
}
