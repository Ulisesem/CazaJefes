package dev.cazajefes;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.EntityType;

public final class BossCatalog {
   private static final Map<String, Definition> BUILT_INS = JsonBosses.builtIns();
   private static Map<String, Definition> definitions = BUILT_INS;

   static Map<String, Definition> builtIns() {
      return BUILT_INS;
   }

   static void installCustom(Map<String, Definition> custom) {
      Map<String, Definition> combined = new LinkedHashMap<>(BUILT_INS);
      combined.putAll(custom);
      definitions = Collections.unmodifiableMap(combined);
   }

   private BossCatalog() {
   }

   public static Definition get(String id) {return id==null?null:definitions.get(id.toLowerCase(java.util.Locale.ROOT));}

   public static List<BossCatalog.Definition> all() {
      return List.copyOf(definitions.values());
   }

   public static List<String> ids() {
      return List.copyOf(definitions.keySet());
   }

   public static String canonicalId(String name) {
      return java.text.Normalizer.normalize(name, java.text.Normalizer.Form.NFD)
         .replaceAll("\\p{M}", "").toLowerCase(java.util.Locale.ROOT).replaceAll("[^a-z0-9]+", "_").replaceAll("^_|_$", "");
   }

   public record Definition(String id, String name, BossCatalog.Tier tier, EntityType entityType, boolean mini, String description,
                            JsonBosses.Settings custom) {
      public Definition(String id, String name, Tier tier, EntityType entityType, boolean mini, String description) {
         this(id, name, tier, entityType, mini, description, null);
      }
   }

   public static enum Tier {
      COMMON("Común", NamedTextColor.GRAY, 1.3, 700.0, 22.0),
      RARE("Raro", NamedTextColor.GREEN, 1.6, 1100.0, 32.0),
      EPIC("Épico", NamedTextColor.LIGHT_PURPLE, 2.0, 1700.0, 44.0),
      LEGENDARY("Legendario", NamedTextColor.GOLD, 2.5, 2500.0, 60.0),
      MYTHIC("Mítico", NamedTextColor.DARK_PURPLE, 3.1, 3600.0, 82.0),
      ABYSSAL("Abismal", NamedTextColor.DARK_RED, 4.0, 5200.0, 110.0);

      public final String display;
      public final NamedTextColor color;
      public final double scale;
      public final double health;
      public final double damage;

      private Tier(String nullxx, NamedTextColor nullxxx, double nullxxxx, double nullxxxxx, double nullxxxxxx) {
         this.display = nullxx;
         this.color = nullxxx;
         this.scale = nullxxxx;
         this.health = nullxxxxx;
         this.damage = nullxxxxxx;
      }
   }
}

