package dev.cazajefes;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.*;

final class CombatScripts {
   record Skill(String id, BossAbilityCatalog.Ultimate profile, long cooldown, int prepare, int warning,
                int interval, int recovery, double damage, boolean particles, boolean sounds, boolean combo,
                boolean ultimate, boolean summon, boolean dodgeable) {}
   enum Role { GUARD, TANK, HEALER, ARCHER, EXPLOSIVE }
   record Minion(EntityType type, Role role) {}
   record Plan(double phase2, double phase3, Map<Integer,List<Skill>> phases, List<Minion> horde) {}
   private static final Plan EMPTY = new Plan(.65,.30, Map.of(), List.of());
   private static Map<String, Plan> plans = Map.of();

   static void load(CazaJefesPlugin plugin) {
      File file = new File(plugin.getDataFolder(), "combate.yml");
      if (!file.exists()) plugin.saveResource("combate.yml", false);
      var root = new YamlConfiguration();
      try {
         if (file.length() > 512 * 1024) throw new IllegalArgumentException("máximo 512 KiB");
         root.load(file);
      } catch (Exception error) {
         plugin.getLogger().warning("combate.yml: " + error.getMessage());
         return;
      }
      Map<String, Skill> skills = new HashMap<>();
      Map<String, Plan> loaded = new HashMap<>();
      var definitions = root.getConfigurationSection("skills");
      if (definitions != null) for (String id : definitions.getKeys(false)) {
         try { skills.put(id, readSkill(id, root.getInt("schema-version",1) < 2 ? upgraded(id, definitions.getConfigurationSection(id)) : definitions.getConfigurationSection(id))); }
         catch (RuntimeException invalid) { plugin.getLogger().warning("combate.yml skills." + id + ": " + invalid.getMessage()); }
      }
      var bosses = root.getConfigurationSection("bosses");
      if (bosses != null) {
         for (String id : bosses.getKeys(false)) {
            try {
               if (!id.equals("default") && BossCatalog.get(id) == null) throw new IllegalArgumentException("ID de jefe desconocido");
               loaded.put(id, readPlan(bosses.getConfigurationSection(id), skills));
            } catch (RuntimeException invalid) { plugin.getLogger().warning("combate.yml bosses." + id + ": " + invalid.getMessage()); }
         }
      }
      for(var boss:BossCatalog.all()){
         try{
            Plan previous=loaded.getOrDefault(boss.id(),loaded.getOrDefault("default",EMPTY));
            Plan configured=boss.custom().options().plan(previous,skills);
            if(configured!=previous)loaded.put(boss.id(),configured);
         }catch(RuntimeException invalid){
            plugin.getLogger().warning("JSON "+boss.id()+" combat: "+invalid.getMessage());
         }
      }
      plans = Map.copyOf(loaded);
   }

   // Earlier installations already have combate.yml. Upgrade only intact old preset values.
   private static ConfigurationSection upgraded(String id, ConfigurationSection existing) {
      if (existing == null) return null;
      var copy = new YamlConfiguration();
      existing.getValues(false).forEach(copy::set);
      switch (id) {
         case "corte_del_presidio" -> {
            if (copy.getDouble("radius") == 7) copy.set("radius",11);
            if (copy.getDouble("damage") == 26) copy.set("damage",32);
         }
         case "fractura_del_vacio" -> {
            if (copy.getDouble("radius") == 9) copy.set("radius",15);
            if (copy.getDouble("damage") == 34) copy.set("damage",40);
         }
         case "lluvia_de_obsidiana" -> {
            if ("METEORS".equals(copy.getString("pattern")) && copy.getDouble("radius") == 8
                && copy.getInt("pulses") == 3 && copy.getDouble("damage") == 36 && !copy.contains("interval-ticks")) {
               copy.set("pattern","OBSIDIAN");
               copy.set("radius",10);
               copy.set("pulses",4);
               copy.set("damage",42);
               copy.set("interval-ticks",25);
            }
         }
         case "llamada_del_presidio" -> {
            if (copy.getDouble("radius") == 6) {
               copy.set("radius",12);
               if (copy.getDouble("damage") == 0 && copy.getBoolean("summon")) copy.set("damage",16);
            }
         }
         default -> {}
      }
      return copy;
   }

   static Skill readSkill(String id, ConfigurationSection s) {
      if (s == null || !id.matches("[a-z][a-z0-9_]{1,47}") || (id.equals("definitivo") || id.startsWith("nativo_"))) throw new IllegalArgumentException("ID inválido");
      String name = s.getString("name", id);
      if (name.isBlank() || name.length() > 64) throw new IllegalArgumentException("name requiere 1–64 caracteres");
      var pattern = BossAbilityCatalog.Pattern.valueOf(s.getString("pattern", "NOVA").toUpperCase(Locale.ROOT));
      var effect = BossAbilityCatalog.Debuff.valueOf(s.getString("effect", "WEAKNESS").toUpperCase(Locale.ROOT));
      var profile = new BossAbilityCatalog.Ultimate(name, pattern, effect, number(s,"radius",6,2,16),
         pattern == BossAbilityCatalog.Pattern.OBSIDIAN ? 4 : integer(s,"pulses",1,1,6,1), 0);
      return new Skill(id, profile, integer(s,"cooldown",12,2,180,1)*1000L,
         integer(s,"prepare-ticks",20,5,100,5), integer(s,"warning-ticks",30,10,120,5),
         integer(s,"interval-ticks",20,15,100,5), integer(s,"recovery-ticks",20,5,100,5),
         number(s,"damage",12,0,180), s.getBoolean("particles",true), s.getBoolean("sounds",true),
         s.getBoolean("combo",false), s.getBoolean("ultimate",false), s.getBoolean("summon",false), s.getBoolean("dodgeable",true));
   }

   static Plan readPlan(ConfigurationSection s, Map<String,Skill> skills) {
      double phase2 = number(s,"phase-2-health",65,1,99) / 100;
      double phase3 = number(s,"phase-3-health",30,1,99) / 100;
      if (phase3 >= phase2) throw new IllegalArgumentException("fase III debe empezar por debajo de fase II");
      var phases = new HashMap<Integer,List<Skill>>();
      for (int phase = 1; phase <= 3; phase++) {
         String key = "phase-" + phase;
         if (!s.contains(key)) continue;
         var ids = s.getStringList(key);
         if (ids.isEmpty() || ids.size() > 8 || new HashSet<>(ids).size() != ids.size()) throw new IllegalArgumentException(key + " requiere 1–8 habilidades distintas");
         var selected = new ArrayList<Skill>();
         for (String id : ids) {
            if (id.equals("definitivo") && phase == 3) selected.add(ultimatePlaceholder());
            else if (id.matches("nativo_[1-4]") && nativeIndex(id) < StageAttackRules.groundCount(phase)
                && (phase != 3 || nativeIndex(id) != 1)) selected.add(nativeSkill(id,phase,null));
            else if (skills.containsKey(id)) selected.add(skills.get(id));
            else throw new IllegalArgumentException("habilidad inválida: " + id);
         }
         long ultimates = selected.stream().filter(Skill::ultimate).count();
         if (ultimates > (phase == 3 ? 1 : 0)) throw new IllegalArgumentException("se admite un definitivo solo en fase III");
         phases.put(phase,List.copyOf(selected));
      }
      List<Minion> horde = s.contains("horde") ? readHorde(s.getStringList("horde")) : List.of();
      return new Plan(phase2,phase3,Map.copyOf(phases),horde);
   }

   static List<Minion> readHorde(List<String> entries) {
      if (entries.size() != HordeRules.SIZE) throw new IllegalArgumentException("horde requiere seis entradas");
      var result = new ArrayList<Minion>();
      for (String entry : entries) {
         String[] pair = entry.toUpperCase(Locale.ROOT).split(":", -1);
         if (pair.length != 2) throw new IllegalArgumentException("usa ESPECIE:ROL");
         EntityType type = EntityType.valueOf(pair[0].strip());
         if (!type.isSpawnable() || type.getEntityClass() == null || !org.bukkit.entity.Mob.class.isAssignableFrom(type.getEntityClass())
             || type == EntityType.ENDER_DRAGON || type == EntityType.WITHER || type == EntityType.WARDEN) {
            throw new IllegalArgumentException("especie inválida para horde: " + type);
         }
         result.add(new Minion(type,Role.valueOf(pair[1].strip())));
      }
      return List.copyOf(result);
   }

   static int phase(String id, double ratio) {
      Plan plan = plans.getOrDefault(id, plans.getOrDefault("default", EMPTY));
      return ratio <= plan.phase3() ? 3 : ratio <= plan.phase2() ? 2 : 1;
   }

   static List<Minion> horde(String id) {
      Plan plan = plans.get(id);
      return plan != null && !plan.horde().isEmpty() ? plan.horde() : plans.getOrDefault("default", EMPTY).horde();
   }

   static List<Skill> rotation(BossCatalog.Definition boss, int phase) {
      Plan plan = plans.getOrDefault(boss.id(), EMPTY);
      var configured = plan.phases().getOrDefault(phase, plans.getOrDefault("default",EMPTY).phases().get(phase));
      if (configured != null) return configured.stream().map(skill -> skill.id().startsWith("nativo_")
         ? nativeSkill(skill.id(),phase,boss) : skill).toList();
      var fallback = new ArrayList<Skill>();
      for (int i = 0; i < StageAttackRules.groundCount(phase); i++) {
         if (phase == 3 && i == 1) fallback.add(ultimatePlaceholder());
         else fallback.add(nativeSkill("nativo_" + (i + 1), phase, boss));
      }
      return List.copyOf(fallback);
   }

   private static int nativeIndex(String id) {
      return id.charAt(id.length() - 1) - '1';
   }

   private static Skill nativeSkill(String id, int phase, BossCatalog.Definition boss) {
      int slot = nativeIndex(id);
      var profile=boss == null ? null : StageAttackRules.profile(boss,phase,slot);
      boolean anchors=profile!=null && profile.pattern()==BossAbilityCatalog.Pattern.ANCHORS;
      return new Skill(id,profile,StageAttackRules.groundCooldown(phase,slot),20,anchors?70:30,
         20,60,-1,true,true,!anchors,false,false,true);
   }

   private static Skill ultimatePlaceholder() {
      return new Skill("definitivo",null,15000,20,30,20,60,-1,true,true,true,true,false,true);
   }

   private static double number(ConfigurationSection s, String key, double def, double min, double max) {
      Object v = s.get(key,def);
      if (!(v instanceof Number n) || !Double.isFinite(n.doubleValue()) || n.doubleValue() < min || n.doubleValue() > max)
         throw new IllegalArgumentException(key + " fuera de rango");
      return n.doubleValue();
   }

   private static int integer(ConfigurationSection s, String key, int def, int min, int max, int step) {
      double v = number(s,key,def,min,max);
      if (v % step != 0) throw new IllegalArgumentException(key + " debe ser múltiplo de " + step);
      return (int)v;
   }

   static final class Rotation {
      private final Map<String,Long> ready = new HashMap<>();
      private int phase, nextSlot;
      private long gap;
      Skill next(List<Skill> skills,int stage,long now,boolean ultimateReady){
         return next(skills,stage,now,ultimateReady,null);
      }

      Skill next(List<Skill> skills,int stage,long now,boolean ultimateReady,String requested){
         if (stage != phase) {
            phase = stage;
            nextSlot = 0;
            gap = now + 4500;
         }
         if (now < gap) return null;
         for (int i = 0; i < skills.size(); i++) {
            int index = (nextSlot + i) % skills.size();
            Skill skill = skills.get(index);
            if (requested!=null&&!requested.equals(skill.id())||skill.ultimate()&&!ultimateReady
               ||now<ready.getOrDefault(skill.id(),0L))continue;
            nextSlot = (index + 1) % skills.size();
            gap = now + StageAttackRules.gap(stage);
            ready.put(skill.id(),now + skill.cooldown());
            return skill;
         }
         return null;
      }
   }
}
