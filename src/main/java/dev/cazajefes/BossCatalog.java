package dev.cazajefes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.EntityType;

public final class BossCatalog {
   private static final Map<String, BossCatalog.Definition> DEFINITIONS = build();

   private BossCatalog() {
   }

   public static BossCatalog.Definition get(String var0) {
      if (var0 == null) {
         return null;
      } else {
         String var1 = var0.toLowerCase(java.util.Locale.ROOT);
         if (var1.equals("sniffer_tanque")) {
            var1 = "volcano";
         }

         if (var1.equals("dragon_grieta")) {
            var1 = "warden_abismal";
         }

         if (var1.equals("wither_corrupto")) {
            var1 = "guardian_abismal";
         }

         Definition exact=DEFINITIONS.get(var1);
         if (exact!=null) return exact;
         final String alias=var1;
         return DEFINITIONS.values().stream().filter(d -> d.legacyId().equals(alias)).findFirst().orElse(null);
      }
   }

   public static List<BossCatalog.Definition> all() {
      return List.copyOf(DEFINITIONS.values());
   }

   public static List<String> ids() {
      return List.copyOf(DEFINITIONS.keySet());
   }

   private static Map<String, BossCatalog.Definition> build() {
      LinkedHashMap var0 = new LinkedHashMap();
      ArrayList<Definition> var1 = new ArrayList<>();
      add(var1, "coloso", "Gorath, Quebrantador de Mundos", BossCatalog.Tier.LEGENDARY, EntityType.ZOMBIE, false);
      add(var1, "viuda", "Nyxara, Tejedora del Ocaso", BossCatalog.Tier.EPIC, EntityType.SPIDER, false);
      add(var1, "centinela", "Varkhos, Vigía de las Cenizas", BossCatalog.Tier.RARE, EntityType.SKELETON, false);
      add(var1, "reina_abeja", "Melisara, Emperatriz del Enjambre", BossCatalog.Tier.COMMON, EntityType.BEE, true);
      add(var1, "zorro_salvaje", "Kitsar, Acechador Carmesí", BossCatalog.Tier.COMMON, EntityType.FOX, true);
      add(var1, "jabali_infernal", "Brakk, Colmillo del Averno", BossCatalog.Tier.COMMON, EntityType.HOGLIN, true);
      add(var1, "volcano", "Volkar, Corazón del Cataclismo", BossCatalog.Tier.COMMON, EntityType.SNIFFER, true);
      add(var1, "oso_tundra", "Boreon, Garra de la Ventisca", BossCatalog.Tier.COMMON, EntityType.POLAR_BEAR, true);
      add(var1, "lobo_ceniza", "Fenrak, Aullido de Ceniza", BossCatalog.Tier.COMMON, EntityType.WOLF, true);
      add(var1, "cabra_quebrada", "Kragnar, Rompecumbres", BossCatalog.Tier.COMMON, EntityType.GOAT, true);
      add(var1, "tortuga_baluarte", "Aegora, Bastión Viviente", BossCatalog.Tier.COMMON, EntityType.TURTLE, true);
      add(var1, "llama_saqueadora", "Zalira, Saqueadora del Yermo", BossCatalog.Tier.COMMON, EntityType.LLAMA, true);
      add(var1, "corredor_sepulcral", "Corredor Sepulcral", BossCatalog.Tier.RARE, EntityType.ZOMBIE, false);
      add(var1, "hueso_viejo", "Hueso Viejo", BossCatalog.Tier.RARE, EntityType.SKELETON, false);
      add(var1, "aranhuelo", "Arañuelo de Cueva", BossCatalog.Tier.EPIC, EntityType.CAVE_SPIDER, false);
      add(var1, "brasa_vagabunda", "Brasa Vagabunda", BossCatalog.Tier.EPIC, EntityType.BLAZE, false);
      add(var1, "saqueador_gris", "Saqueador Gris", BossCatalog.Tier.RARE, EntityType.PILLAGER, false);
      add(var1, "moho_muerto", "Moho Muerto", BossCatalog.Tier.RARE, EntityType.HUSK, false);
      add(var1, "cubo_turbio", "Cubo Turbio", BossCatalog.Tier.EPIC, EntityType.SLIME, false);
      add(var1, "murcielago_fosa", "Murciélago de la Fosa", BossCatalog.Tier.RARE, EntityType.PHANTOM, false);
      add(var1, "cazador_oxidado", "Cazador Oxidado", BossCatalog.Tier.RARE, EntityType.VINDICATOR, true);
      add(var1, "tejedora_cripta", "Tejedora de la Cripta", BossCatalog.Tier.RARE, EntityType.SPIDER, false);
      add(var1, "arquero_invernal", "Arquero Invernal", BossCatalog.Tier.RARE, EntityType.STRAY, false);
      add(var1, "bruto_piglin", "Bruto Piglin", BossCatalog.Tier.RARE, EntityType.PIGLIN_BRUTE, false);
      add(var1, "sapo_fuego", "Sapo de Fuego", BossCatalog.Tier.RARE, EntityType.MAGMA_CUBE, false);
      add(var1, "bruja_musgo", "Bruja del Musgo", BossCatalog.Tier.RARE, EntityType.WITCH, false);
      add(var1, "ahogado_abisal", "Ahogado Abisal", BossCatalog.Tier.RARE, EntityType.DROWNED, false);
      add(var1, "vex_cobrizo", "Vex Cobrizo", BossCatalog.Tier.RARE, EntityType.VINDICATOR, false);
      add(var1, "caballero_wither", "Caballero Wither", BossCatalog.Tier.EPIC, EntityType.WITHER_SKELETON, false);
      add(var1, "vigia_prismarina", "Vigía Prismarina", BossCatalog.Tier.EPIC, EntityType.GUARDIAN, false);
      add(var1, "cuervo_carmesi", "Cuervo Carmesí", BossCatalog.Tier.EPIC, EntityType.PHANTOM, false);
      add(var1, "devastador_hambriento", "Devastador Hambriento", BossCatalog.Tier.EPIC, EntityType.RAVAGER, false);
      add(var1, "bestia_lodazal", "Bestia del Lodazal", BossCatalog.Tier.EPIC, EntityType.HOGLIN, false);
      add(var1, "brisa_cortante", "Brisa Cortante", BossCatalog.Tier.EPIC, EntityType.BREEZE, false);
      add(var1, "evocador_umbral", "Evocador del Umbral", BossCatalog.Tier.EPIC, EntityType.EVOKER, false);
      add(var1, "golem_grieta", "Gólem de la Grieta", BossCatalog.Tier.EPIC, EntityType.IRON_GOLEM, false);
      add(var1, "ghast_azabache", "Ghast Azabache", BossCatalog.Tier.EPIC, EntityType.GHAST, false);
      add(var1, "aracnido_cristal", "Arácnido de Cristal", BossCatalog.Tier.EPIC, EntityType.CAVE_SPIDER, false);
      add(var1, "hongo_infernal", "Hongo Infernal", BossCatalog.Tier.EPIC, EntityType.MOOSHROOM, false);
      add(var1, "desollador_bogged", "Desollador Bogged", BossCatalog.Tier.EPIC, EntityType.BOGGED, false);
      add(var1, "anciano_coral", "Anciano Coral", BossCatalog.Tier.LEGENDARY, EntityType.ELDER_GUARDIAN, false);
      add(var1, "devorador_ecos", "Azrath, Devorador de Ecos", BossCatalog.Tier.ABYSSAL, EntityType.WARDEN, false);
      add(var1, "espectro_fin", "Espectro del Fin", BossCatalog.Tier.LEGENDARY, EntityType.ENDERMAN, false);
      add(var1, "dragon_ceniza", "Dragón de Ceniza", BossCatalog.Tier.LEGENDARY, EntityType.BLAZE, false);
      add(var1, "reina_brezal", "Reina del Brezal", BossCatalog.Tier.LEGENDARY, EntityType.CAVE_SPIDER, false);
      add(var1, "carcelero_carmesi", "Carcelero Carmesí", BossCatalog.Tier.LEGENDARY, EntityType.PIGLIN_BRUTE, false);
      add(var1, "creaking_ancestral", "Creaking Ancestral", BossCatalog.Tier.LEGENDARY, EntityType.CREAKING, false);
      add(var1, "tormenta_breeze", "Tormenta Breeze", BossCatalog.Tier.LEGENDARY, EntityType.BREEZE, false);
      add(var1, "quimera_endermite", "Quimera Endermite", BossCatalog.Tier.LEGENDARY, EntityType.ENDERMITE, false);
      add(var1, "verdugo_necrotico", "Verdugo Necrótico", BossCatalog.Tier.LEGENDARY, EntityType.WITHER_SKELETON, false);
      add(var1, "serpiente_pillager", "Serpiente Saqueadora", BossCatalog.Tier.LEGENDARY, EntityType.PILLAGER, false);
      add(var1, "colmillo_vacio", "Colmillo del Vacío", BossCatalog.Tier.MYTHIC, EntityType.ZOGLIN, false);
      add(var1, "coloso_fragmentado", "Coloso Fragmentado", BossCatalog.Tier.MYTHIC, EntityType.GIANT, false);
      add(var1, "rey_brasas", "Rey de las Brasas", BossCatalog.Tier.MYTHIC, EntityType.BLAZE, false);
      add(var1, "titan_barro", "Titán de Barro", BossCatalog.Tier.MYTHIC, EntityType.RAVAGER, false);
      add(var1, "leviatan_ender", "Leviatán del End", BossCatalog.Tier.MYTHIC, EntityType.SHULKER, false);
      add(var1, "liche_noche", "Liche de la Noche", BossCatalog.Tier.MYTHIC, EntityType.WITHER_SKELETON, false);
      add(var1, "colmillo_abismal", "Colmillo Abismal", BossCatalog.Tier.ABYSSAL, EntityType.ZOGLIN, false);
      add(var1, "warden_abismal", "Nharzul, Soberano del Abismo", BossCatalog.Tier.ABYSSAL, EntityType.WARDEN, false);
      add(var1, "maestro_creaking", "Maestro Creaking", BossCatalog.Tier.ABYSSAL, EntityType.CREAKING, false);
      add(var1, "reina_piglin", "Reina Piglin", BossCatalog.Tier.ABYSSAL, EntityType.PIGLIN_BRUTE, false);
      add(var1, "guardian_abismal", "Guardián Ancestral", BossCatalog.Tier.ABYSSAL, EntityType.ELDER_GUARDIAN, false);
      var1.forEach(var1x -> var0.put(var1x.id(), var1x));
      return Collections.unmodifiableMap(var0);
   }

   private static void add(List<BossCatalog.Definition> var0, String var1, String var2, BossCatalog.Tier var3, EntityType var4, boolean var5) {
      String var6 = switch (var1) {
         case "reina_abeja" -> "Una reina colosal que protege su colmena con abejas furiosas, picaduras venenosas y maniobras de combate agresivas.";
         case "zorro_salvaje" -> "Depredador veloz que hostiga sin descanso, invoca ovejas como presa y recupera parte de su vigor al cazarlas.";
         case "jabali_infernal" -> "Bestia del Nether que embiste envuelta en fuego y llama piglins a defenderla cuando recibe un golpe.";
         case "volcano" -> "Volcano, el tanque ancestral de la Grieta: golpea el suelo con fuerza y llama crías agresivas para acorralar a sus presas.";
         case "oso_tundra" -> "Un oso polar territorial que carga contra el cazador y lo derriba con zarpazos helados.";
         case "lobo_ceniza" -> "Depredador de manada, rápido y persistente; acorrala a quien invade su territorio.";
         case "cabra_quebrada" -> "Una cabra de montaña endurecida que embiste desde lejos y lanza a sus enemigos por el aire.";
         case "tortuga_baluarte" -> "Tanque lento y resistente que protege su caparazón y golpea el suelo con ondas expansivas.";
         case "llama_saqueadora" -> "Llama hostil que escupe a sus presas y convoca llamas de apoyo para cerrarles el paso.";
         default -> var5
            ? "Mini jefe de rango " + var3.display + ", especializado en perseguir y presionar a los cazadores."
            : "Jefe de rango " + var3.display + " que protege la Grieta con ataques y habilidades propias.";
      };
      var0.add(new BossCatalog.Definition(canonicalId(var2), var2, var3, var4, var5, var6, var1));
   }

   public static String canonicalId(String name) {
      return java.text.Normalizer.normalize(name, java.text.Normalizer.Form.NFD)
         .replaceAll("\\p{M}", "").toLowerCase(java.util.Locale.ROOT).replaceAll("[^a-z0-9]+", "_").replaceAll("^_|_$", "");
   }

   public record Definition(String id, String name, BossCatalog.Tier tier, EntityType entityType, boolean mini, String description, String legacyId) {
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

