package dev.cazajefes;

import com.google.gson.Strictness;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

final class JsonBosses {
   private static final int MAX_BYTES = 16 * 1024;
   private static final int MAX_FILES = 128;

   record Settings(double health, double damage, double scale, BossAbilityCatalog.Ultimate ultimate) {}

   private JsonBosses() {}

   static Map<String, BossCatalog.Definition> load(JavaPlugin plugin) {
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
                  var definition = parse(Files.readString(file));
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
      return custom;
   }

   static BossCatalog.Definition parse(String json) throws IOException {
      try (JsonReader reader = new JsonReader(new StringReader(json))) {
         reader.setStrictness(Strictness.STRICT);
         Map<String, Object> fields = readFields(reader,
            Set.of("id", "template", "name", "description", "health", "damage", "scale", "ultimate"), true);
         if (reader.peek() != JsonToken.END_DOCUMENT) {
            throw new IllegalArgumentException("se esperaba un solo objeto JSON");
         }
         String id = requiredText(fields, "id", 48);
         if (!id.matches("[a-z][a-z0-9_]{2,47}") || id.equals("random") || BossCatalog.builtIns().containsKey(id)) {
            throw new IllegalArgumentException("ID inválido o reservado: " + id);
         }
         String templateId = requiredText(fields, "template", 100);
         var template = BossCatalog.builtIns().get(templateId);
         if (template == null) {
            throw new IllegalArgumentException("template debe ser el ID de un jefe original: " + templateId);
         }

         var base = BossAbilityCatalog.ultimate(template);
         Map<String, Object> ultimate = fields.get("ultimate") instanceof Map<?, ?> values
            ? ultimateFields(values) : Map.of();
         double pulses = number(ultimate, "pulses", base.pulses(), 2, 4);
         if (pulses != Math.rint(pulses)) {
            throw new IllegalArgumentException("pulses debe ser un entero");
         }
         var profile = new BossAbilityCatalog.Ultimate(
            optionalText(ultimate, "name", base.name(), 64),
            option(ultimate, "pattern", base.pattern(), BossAbilityCatalog.Pattern.class),
            option(ultimate, "effect", base.effect(), BossAbilityCatalog.Debuff.class),
            number(ultimate, "radius", base.radius(), 6, 8),
            (int) pulses, base.angle());
         var settings = new Settings(number(fields, "health", null, 1, 1024),
            number(fields, "damage", null, 0, 120), number(fields, "scale", null, 0.25, 4), profile);
         return new BossCatalog.Definition(id, requiredText(fields, "name", 48), template.tier(),
            template.entityType(), template.mini(), optionalText(fields, "description", template.description(), 140), settings);
      } catch (IllegalStateException failure) {
         throw new IllegalArgumentException("se esperaba un objeto JSON válido", failure);
      }
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
            value = readFields(reader, Set.of("name", "pattern", "effect", "radius", "pulses"), false);
         } else {
            value = switch (reader.peek()) {
               case STRING -> reader.nextString();
               case NUMBER -> reader.nextDouble();
               default -> throw new IllegalArgumentException("valor inválido en " + field);
            };
         }
         values.put(field, value);
      }
      reader.endObject();
      return values;
   }

   private static Map<String, Object> ultimateFields(Map<?, ?> input) {
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
