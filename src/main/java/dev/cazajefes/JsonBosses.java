package dev.cazajefes;

import com.google.gson.Strictness;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.StringReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import org.bukkit.entity.EntityType;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

final class JsonBosses {
   private static final int MAX_BYTES = 16 * 1024;
   private static final int MAX_FILES = 128;

   record Settings(double health, double damage, double scale, BossAbilityCatalog.Ultimate ultimate, String portrait) {}

   private JsonBosses() {}

   static Map<String, BossCatalog.Definition> builtIns() {
      try (var input = JsonBosses.class.getResourceAsStream("/bosses.json")) {
         if (input == null) throw new IOException("Falta bosses.json en el JAR");
         return readCatalog(new InputStreamReader(input, StandardCharsets.UTF_8));
      } catch (IOException failure) {
         throw new IllegalStateException("No se pudo cargar el catálogo incluido", failure);
      }
   }

   static Map<String, BossCatalog.Definition> readCatalog(Reader source) throws IOException {
      Map<String, BossCatalog.Definition> result = new LinkedHashMap<>();
      try (JsonReader reader = new JsonReader(source)) {
         reader.setStrictness(Strictness.STRICT);
         reader.beginArray();
         while (reader.hasNext()) {
            var fields = readFields(reader, Set.of("id", "name", "tier", "entity", "mini", "description",
               "health", "damage", "scale", "ultimate", "portrait"), true);
            String id = requiredText(fields, "id", 64);
            validateId(id);
            EntityType entity = option(fields, "entity", null, EntityType.class);
            BossCatalog.Tier tier = option(fields, "tier", null, BossCatalog.Tier.class);
            if (entity == null || entity.getEntityClass() == null
                || !org.bukkit.entity.Mob.class.isAssignableFrom(entity.getEntityClass())
                || !entity.isSpawnable() || tier == null || !(fields.get("mini") instanceof Boolean mini)) {
               throw new IllegalArgumentException(id + ": entity, tier o mini inválido");
            }
            var ultimate = readUltimate(ultimateFields(fields), null);
            var settings = new Settings(number(fields, "health", null, 1, 100000),
               number(fields, "damage", null, 0, 1000), number(fields, "scale", null, 0.1, 10),
               ultimate, portrait(fields));
            var definition = new BossCatalog.Definition(id, requiredText(fields, "name", 64), tier, entity, mini,
               optionalText(fields, "description", BossLore.describe(entity, mini), 180), settings);
            if (result.putIfAbsent(id, definition) != null) throw new IllegalArgumentException("ID duplicado: " + id);
            if (result.size() > 512) throw new IllegalArgumentException("Demasiados jefes en el catálogo");
         }
         reader.endArray();
         if (reader.peek() != JsonToken.END_DOCUMENT) throw new IllegalArgumentException("Contenido tras el catálogo");
      }
      return result;
   }

   private static Map<String, BossCatalog.Definition> loadOriginals(JavaPlugin plugin) {
      var original = BossCatalog.builtIns();
      Path file = plugin.getDataFolder().toPath().resolve("bosses.json");
      try {
         if (Files.notExists(file)) plugin.saveResource("bosses.json", false);
         if (Files.size(file) > 512 * 1024) throw new IllegalArgumentException("máximo 512 KiB");
         Map<String, BossCatalog.Definition> edited;
         try (var reader = Files.newBufferedReader(file)) {
            edited = readCatalog(reader);
         }
         if (!edited.keySet().equals(original.keySet())) {
            throw new IllegalArgumentException("debe conservar los " + original.size() + " IDs originales");
         }
         Map<String, BossCatalog.Definition> ordered = new LinkedHashMap<>();
         original.forEach((id, base) -> {
            var boss = edited.get(id);
            if (boss.entityType() != base.entityType() || boss.tier() != base.tier() || boss.mini() != base.mini()) {
               throw new IllegalArgumentException(id + ": la especie y categoría original se conservan");
            }
            ordered.put(id, boss);
         });
         return ordered;
      } catch (IOException | IllegalArgumentException | IllegalStateException failure) {
         plugin.getLogger().warning("bosses.json inválido; se usará el catálogo del JAR: " + failure.getMessage());
         return original;
      }
   }

   static Map<String, BossCatalog.Definition> load(JavaPlugin plugin) {
      var originals = loadOriginals(plugin);
      Path directory = plugin.getDataFolder().toPath().resolve("mobs");
      Map<String, BossCatalog.Definition> custom = new LinkedHashMap<>();
      try {
         Files.createDirectories(directory);
         try (var paths = Files.list(directory)) {
            var files = paths.filter(path -> path.getFileName().toString().endsWith(".json"))
               .filter(path -> Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS))
               .sorted().limit(MAX_FILES + 1).toList();
            if (files.size() > MAX_FILES) {
               plugin.getLogger().warning("mobs: se cargarán como máximo " + MAX_FILES + " archivos JSON.");
            }
            for (Path file : files.stream().limit(MAX_FILES).toList()) {
               try {
                  if (Files.size(file) > MAX_BYTES) {
                     throw new IllegalArgumentException("el límite por archivo es de 16 KiB");
                  }
                  var definition = parse(Files.readString(file), originals);
                  if (custom.putIfAbsent(definition.id(), definition) != null) {
                     throw new IllegalArgumentException("ID duplicado: " + definition.id());
                  }
               } catch (IllegalArgumentException | IOException failure) {
                  plugin.getLogger().warning("mobs/" + file.getFileName() + ": " + failure.getMessage());
               }
            }
         }
      } catch (IOException | java.io.UncheckedIOException failure) {
         plugin.getLogger().warning("No se pudo leer la carpeta mobs: " + failure.getMessage());
      }
      plugin.getLogger().info("Jefes JSON cargados: " + custom.size());
      var combined = new LinkedHashMap<>(originals);
      combined.putAll(custom);
      return combined;
   }

   static BossCatalog.Definition parse(String json) throws IOException {
      return parse(json, BossCatalog.builtIns());
   }

   private static BossCatalog.Definition parse(String json, Map<String, BossCatalog.Definition> originals) throws IOException {
      try (JsonReader reader = new JsonReader(new StringReader(json))) {
         reader.setStrictness(Strictness.STRICT);
         var fields = readFields(reader,
            Set.of("id", "template", "name", "description", "health", "damage", "scale", "ultimate", "portrait"), true);
         if (reader.peek() != JsonToken.END_DOCUMENT) throw new IllegalArgumentException("se esperaba un solo objeto JSON");
         String id = requiredText(fields, "id", 48);
         validateId(id);
         if (originals.containsKey(id)) throw new IllegalArgumentException("ID reservado: " + id);
         String templateId = requiredText(fields, "template", 100);
         var template = originals.get(templateId);
         if (template == null) throw new IllegalArgumentException("template debe ser un jefe original: " + templateId);
         var profile = readUltimate(ultimateFields(fields), template.custom().ultimate());
         var settings = new Settings(number(fields, "health", null, 1, 1024),
            number(fields, "damage", null, 0, 120), number(fields, "scale", null, 0.25, 4), profile,
            fields.containsKey("portrait") ? portrait(fields) : template.custom().portrait());
         return new BossCatalog.Definition(id, requiredText(fields, "name", 48), template.tier(),
            template.entityType(), template.mini(), optionalText(fields, "description", template.description(), 140), settings);
      } catch (IllegalStateException failure) {
         throw new IllegalArgumentException("se esperaba un objeto JSON válido", failure);
      }
   }

   private static void validateId(String id) {
      if (!id.matches("[a-z][a-z0-9_]{2,63}") || id.equals("random")) {
         throw new IllegalArgumentException("ID inválido o reservado: " + id);
      }
   }

   private static String portrait(Map<String, Object> fields) {
      if (!fields.containsKey("portrait")) return null;
      String hash = requiredText(fields, "portrait", 64);
      if (!hash.matches("[a-f0-9]{60,64}")) throw new IllegalArgumentException("portrait debe ser un hash de textura de 60 a 64 caracteres");
      return hash;
   }

   private static BossAbilityCatalog.Ultimate readUltimate(Map<String, Object> fields, BossAbilityCatalog.Ultimate base) {
      double pulses = number(fields, "pulses", base == null ? null : base.pulses(), 2, 4);
      if (pulses != Math.rint(pulses)) throw new IllegalArgumentException("pulses debe ser un entero");
      var pattern = option(fields, "pattern", base == null ? null : base.pattern(), BossAbilityCatalog.Pattern.class);
      var effect = option(fields, "effect", base == null ? null : base.effect(), BossAbilityCatalog.Debuff.class);
      if (pattern == null || effect == null) throw new IllegalArgumentException("ultimate requiere pattern y effect");
      return new BossAbilityCatalog.Ultimate(base == null ? requiredText(fields, "name", 64) : optionalText(fields, "name", base.name(), 64),
         pattern, effect, number(fields, "radius", base == null ? null : base.radius(), 6, 8), (int) pulses,
         number(fields, "angle", base == null ? 0 : base.angle(), -2 * Math.PI, 2 * Math.PI));
   }

   private static Map<String, Object> readFields(JsonReader reader, Set<String> allowed, boolean root) throws IOException {
      Map<String, Object> values = new LinkedHashMap<>();
      reader.beginObject();
      while (reader.hasNext()) {
         String field = reader.nextName();
         if (!allowed.contains(field) || values.containsKey(field)) {
            throw new IllegalArgumentException("campo desconocido o repetido: " + field);
         }
         Object value;
         if (root && field.equals("ultimate")) {
            value = readFields(reader, Set.of("name", "pattern", "effect", "radius", "pulses", "angle"), false);
         } else {
            value = switch (reader.peek()) {
               case STRING -> reader.nextString();
               case NUMBER -> reader.nextDouble();
               case BOOLEAN -> reader.nextBoolean();
               default -> throw new IllegalArgumentException("valor inválido en " + field);
            };
         }
         values.put(field, value);
      }
      reader.endObject();
      return values;
   }

   private static Map<String, Object> ultimateFields(Map<String, Object> root) {
      if (!(root.get("ultimate") instanceof Map<?, ?> input)) return Map.of();
      Map<String, Object> fields = new LinkedHashMap<>();
      input.forEach((key, value) -> fields.put((String) key, value));
      return fields;
   }

   private static String requiredText(Map<String, Object> fields, String key, int max) {
      if (!(fields.get(key) instanceof String value) || value.isBlank() || value.length() > max
          || value.codePoints().anyMatch(Character::isISOControl)) {
         throw new IllegalArgumentException(key + " debe ser texto de 1 a " + max + " caracteres, sin controles");
      }
      return value.strip();
   }

   private static String optionalText(Map<String, Object> fields, String key, String fallback, int max) {
      return fields.containsKey(key) ? requiredText(fields, key, max) : fallback;
   }

   private static double number(Map<String, Object> fields, String key, Number fallback, double min, double max) {
      if (!(fields.getOrDefault(key, fallback) instanceof Number value)
          || !Double.isFinite(value.doubleValue()) || value.doubleValue() < min || value.doubleValue() > max) {
         throw new IllegalArgumentException(key + " debe ser un número entre " + min + " y " + max);
      }
      return value.doubleValue();
   }

   private static <E extends Enum<E>> E option(Map<String, Object> fields, String key, E fallback, Class<E> type) {
      if (!fields.containsKey(key)) return fallback;
      String value = requiredText(fields, key, 32);
      try {
         return Enum.valueOf(type, value);
      } catch (IllegalArgumentException failure) {
         throw new IllegalArgumentException(key + " no admite " + value, failure);
      }
   }
}
