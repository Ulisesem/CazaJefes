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

   public static Definition get(String id) {return id==null?null:DEFINITIONS.get(id.toLowerCase(java.util.Locale.ROOT));}

   public static List<BossCatalog.Definition> all() {
      return List.copyOf(DEFINITIONS.values());
   }

   public static List<String> ids() {
      return List.copyOf(DEFINITIONS.keySet());
   }

   private static Map<String, BossCatalog.Definition> build() {
      LinkedHashMap var0 = new LinkedHashMap();
      ArrayList<Definition> var1 = new ArrayList<>();
      add(var1, "Gorath, Quebrantador de Mundos", BossCatalog.Tier.LEGENDARY, EntityType.ZOMBIE, false);
      add(var1, "Nyxara, Tejedora del Ocaso", BossCatalog.Tier.EPIC, EntityType.SPIDER, false);
      add(var1, "Varkhos, Vigía de las Cenizas", BossCatalog.Tier.RARE, EntityType.SKELETON, false);
      add(var1, "Melisara, Emperatriz del Enjambre", BossCatalog.Tier.COMMON, EntityType.BEE, true);
      add(var1, "Kitsar, Acechador Carmesí", BossCatalog.Tier.COMMON, EntityType.FOX, true);
      add(var1, "Brakk, Colmillo del Averno", BossCatalog.Tier.COMMON, EntityType.HOGLIN, true);
      add(var1, "Volkar, Corazón del Cataclismo", BossCatalog.Tier.COMMON, EntityType.SNIFFER, true);
      add(var1, "Boreon, Garra de la Ventisca", BossCatalog.Tier.COMMON, EntityType.POLAR_BEAR, true);
      add(var1, "Fenrak, Aullido de Ceniza", BossCatalog.Tier.COMMON, EntityType.WOLF, true);
      add(var1, "Kragnar, Rompecumbres", BossCatalog.Tier.COMMON, EntityType.GOAT, true);
      add(var1, "Aegora, Bastión Viviente", BossCatalog.Tier.COMMON, EntityType.TURTLE, true);
      add(var1, "Zalira, Saqueadora del Yermo", BossCatalog.Tier.COMMON, EntityType.LLAMA, true);
      add(var1, "Corredor Sepulcral", BossCatalog.Tier.RARE, EntityType.ZOMBIE, false);
      add(var1, "Hueso Viejo", BossCatalog.Tier.RARE, EntityType.SKELETON, false);
      add(var1, "Arañuelo de Cueva", BossCatalog.Tier.EPIC, EntityType.CAVE_SPIDER, false);
      add(var1, "Brasa Vagabunda", BossCatalog.Tier.EPIC, EntityType.BLAZE, false);
      add(var1, "Saqueador Gris", BossCatalog.Tier.RARE, EntityType.PILLAGER, false);
      add(var1, "Moho Muerto", BossCatalog.Tier.RARE, EntityType.HUSK, false);
      add(var1, "Cubo Turbio", BossCatalog.Tier.EPIC, EntityType.SLIME, false);
      add(var1, "Murciélago de la Fosa", BossCatalog.Tier.RARE, EntityType.PHANTOM, false);
      add(var1, "Ferrun, Colmillo de Hierro", BossCatalog.Tier.RARE, EntityType.WOLF, true);
      add(var1, "Tejedora de la Cripta", BossCatalog.Tier.RARE, EntityType.SPIDER, false);
      add(var1, "Arquero Invernal", BossCatalog.Tier.RARE, EntityType.STRAY, false);
      add(var1, "Bruto Piglin", BossCatalog.Tier.RARE, EntityType.PIGLIN_BRUTE, false);
      add(var1, "Sapo de Fuego", BossCatalog.Tier.RARE, EntityType.MAGMA_CUBE, false);
      add(var1, "Bruja del Musgo", BossCatalog.Tier.RARE, EntityType.WITCH, false);
      add(var1, "Ahogado Abisal", BossCatalog.Tier.RARE, EntityType.DROWNED, false);
      add(var1, "Vex Cobrizo", BossCatalog.Tier.RARE, EntityType.VINDICATOR, false);
      add(var1, "Caballero Wither", BossCatalog.Tier.EPIC, EntityType.WITHER_SKELETON, false);
      add(var1, "Vigía Prismarina", BossCatalog.Tier.EPIC, EntityType.GUARDIAN, false);
      add(var1, "Cuervo Carmesí", BossCatalog.Tier.EPIC, EntityType.PHANTOM, false);
      add(var1, "Devastador Hambriento", BossCatalog.Tier.EPIC, EntityType.RAVAGER, false);
      add(var1, "Bestia del Lodazal", BossCatalog.Tier.EPIC, EntityType.HOGLIN, false);
      add(var1, "Brisa Cortante", BossCatalog.Tier.EPIC, EntityType.BREEZE, false);
      add(var1, "Evocador del Umbral", BossCatalog.Tier.EPIC, EntityType.EVOKER, false);
      add(var1, "Gólem de la Grieta", BossCatalog.Tier.EPIC, EntityType.IRON_GOLEM, false);
      add(var1, "Ghast Azabache", BossCatalog.Tier.EPIC, EntityType.GHAST, false);
      add(var1, "Arácnido de Cristal", BossCatalog.Tier.EPIC, EntityType.CAVE_SPIDER, false);
      add(var1, "Hongo Infernal", BossCatalog.Tier.EPIC, EntityType.MOOSHROOM, false);
      add(var1, "Desollador Bogged", BossCatalog.Tier.EPIC, EntityType.BOGGED, false);
      add(var1, "Anciano Coral", BossCatalog.Tier.LEGENDARY, EntityType.ELDER_GUARDIAN, false);
      add(var1, "Espectro del Fin", BossCatalog.Tier.LEGENDARY, EntityType.ENDERMAN, false);
      add(var1, "Dragón de Ceniza", BossCatalog.Tier.LEGENDARY, EntityType.BLAZE, false);
      add(var1, "Reina del Brezal", BossCatalog.Tier.LEGENDARY, EntityType.CAVE_SPIDER, false);
      add(var1, "Carcelero Carmesí", BossCatalog.Tier.LEGENDARY, EntityType.PIGLIN_BRUTE, false);
      add(var1, "Creaking Ancestral", BossCatalog.Tier.LEGENDARY, EntityType.CREAKING, false);
      add(var1, "Tormenta Breeze", BossCatalog.Tier.LEGENDARY, EntityType.BREEZE, false);
      add(var1, "Quimera Endermite", BossCatalog.Tier.LEGENDARY, EntityType.ENDERMITE, false);
      add(var1, "Verdugo Necrótico", BossCatalog.Tier.LEGENDARY, EntityType.WITHER_SKELETON, false);
      add(var1, "Serpiente Saqueadora", BossCatalog.Tier.LEGENDARY, EntityType.PILLAGER, false);
      add(var1, "Colmillo del Vacío", BossCatalog.Tier.MYTHIC, EntityType.ZOGLIN, false);
      add(var1, "Coloso Fragmentado", BossCatalog.Tier.MYTHIC, EntityType.GIANT, false);
      add(var1, "Rey de las Brasas", BossCatalog.Tier.MYTHIC, EntityType.BLAZE, false);
      add(var1, "Titán de Barro", BossCatalog.Tier.MYTHIC, EntityType.RAVAGER, false);
      add(var1, "Leviatán del End", BossCatalog.Tier.MYTHIC, EntityType.SHULKER, false);
      add(var1, "Liche de la Noche", BossCatalog.Tier.MYTHIC, EntityType.WITHER_SKELETON, false);
      add(var1, "Colmillo Abismal", BossCatalog.Tier.ABYSSAL, EntityType.ZOGLIN, false);
      add(var1, "Nharzul, Soberano del Abismo", BossCatalog.Tier.ABYSSAL, EntityType.WARDEN, false);
      add(var1, "Maestro Creaking", BossCatalog.Tier.ABYSSAL, EntityType.CREAKING, false);
      add(var1, "Reina Piglin", BossCatalog.Tier.ABYSSAL, EntityType.PIGLIN_BRUTE, false);
      add(var1, "Guardián Ancestral", BossCatalog.Tier.ABYSSAL, EntityType.ELDER_GUARDIAN, false);
      add(var1, "Xalther, Emperador del Vacío", BossCatalog.Tier.ABYSSAL, EntityType.ZOMBIE, false);
      add(var1, "Morvex, Arquero del Ocaso", BossCatalog.Tier.ABYSSAL, EntityType.SKELETON, false);
      add(var1, "Kaelthar, Puño del Averno", BossCatalog.Tier.ABYSSAL, EntityType.PIGLIN_BRUTE, false);
      add(var1, "Vaelora, Oráculo de las Sombras", BossCatalog.Tier.ABYSSAL, EntityType.EVOKER, false);
      add(var1, "Draxion, Devastador Eterno", BossCatalog.Tier.ABYSSAL, EntityType.RAVAGER, false);
      add(var1, "Tharok, Segador de Almas", BossCatalog.Tier.ABYSSAL, EntityType.WITHER_SKELETON, false);
      add(var1, "Ignaroth, Sol Negro", BossCatalog.Tier.ABYSSAL, EntityType.BLAZE, false);
      add(var1, "Zyrakk, Custodio del Umbral", BossCatalog.Tier.ABYSSAL, EntityType.SHULKER, false);
      add(var1, "Thalassar, Monarca del Abismo", BossCatalog.Tier.ABYSSAL, EntityType.ELDER_GUARDIAN, false);
      add(var1, "Aerthos, Ciclón de la Ruina", BossCatalog.Tier.ABYSSAL, EntityType.BREEZE, false);
      add(var1, "Nyssara, Matriarca de la Penumbra", BossCatalog.Tier.ABYSSAL, EntityType.CAVE_SPIDER, false);
      add(var1, "Velkaris, Caminante del Vacío", BossCatalog.Tier.ABYSSAL, EntityType.ENDERMAN, false);
      add(var1, "Brumgor, Colmillo del Eclipse", BossCatalog.Tier.ABYSSAL, EntityType.HOGLIN, false);
      add(var1, "Korvath, Verdugo del Silencio", BossCatalog.Tier.ABYSSAL, EntityType.VINDICATOR, false);
      add(var1, "Ossyra, Plaga de la Medianoche", BossCatalog.Tier.ABYSSAL, EntityType.BOGGED, false);
      add(var1, "Thumra, Pezuña del Trueno", BossCatalog.Tier.COMMON, EntityType.COW, true);
      add(var1, "Porvax, Colmillo de Cobre", BossCatalog.Tier.COMMON, EntityType.PIG, true);
      add(var1, "Lanora, Vellón de Escarcha", BossCatalog.Tier.COMMON, EntityType.SHEEP, true);
      add(var1, "Plumak, Pico del Vendaval", BossCatalog.Tier.COMMON, EntityType.CHICKEN, true);
      add(var1, "Lepthor, Salto del Eclipse", BossCatalog.Tier.COMMON, EntityType.RABBIT, true);
      add(var1, "Equiron, Crin de Tormenta", BossCatalog.Tier.COMMON, EntityType.HORSE, true);
      add(var1, "Rokbur, Casco de Granito", BossCatalog.Tier.COMMON, EntityType.DONKEY, true);
      add(var1, "Mularis, Guardián del Desfiladero", BossCatalog.Tier.COMMON, EntityType.MULE, true);
      add(var1, "Dunarak, Joroba del Desierto", BossCatalog.Tier.COMMON, EntityType.CAMEL, true);
      add(var1, "Bambor, Abrazo del Bosque", BossCatalog.Tier.COMMON, EntityType.PANDA, true);
      add(var1, "Axoria, Branquia de Nácar", BossCatalog.Tier.COMMON, EntityType.AXOLOTL, true);
      add(var1, "Ocelkar, Garra del Dosel", BossCatalog.Tier.COMMON, EntityType.OCELOT, true);
      add(var1, "Feluna, Sombra de Medianoche", BossCatalog.Tier.COMMON, EntityType.CAT, true);
      add(var1, "Arakor, Pluma del Relámpago", BossCatalog.Tier.COMMON, EntityType.PARROT, true);
      add(var1, "Escudrax, Coraza del Yermo", BossCatalog.Tier.COMMON, EntityType.ARMADILLO, true);
      add(var1, "Delfyr, Aleta de la Marea", BossCatalog.Tier.COMMON, EntityType.DOLPHIN, true);
      add(var1, "Tintor, Abrazo de Tinta", BossCatalog.Tier.COMMON, EntityType.SQUID, true);
      add(var1, "Luminox, Faro de las Profundidades", BossCatalog.Tier.COMMON, EntityType.GLOW_SQUID, true);
      add(var1, "Sepulkris, Guardia del Osario", BossCatalog.Tier.RARE, EntityType.ZOMBIE, false);
      add(var1, "Dunvex, Errante de Arena", BossCatalog.Tier.RARE, EntityType.HUSK, false);
      add(var1, "Marvok, Ancla del Naufragio", BossCatalog.Tier.RARE, EntityType.DROWNED, false);
      add(var1, "Osserak, Flecha de Caliza", BossCatalog.Tier.RARE, EntityType.SKELETON, false);
      add(var1, "Nevrath, Vigía del Hielo", BossCatalog.Tier.RARE, EntityType.STRAY, false);
      add(var1, "Lodrakk, Flechero del Pantano", BossCatalog.Tier.RARE, EntityType.BOGGED, false);
      add(var1, "Silvarax, Hilandera del Sotobosque", BossCatalog.Tier.RARE, EntityType.SPIDER, false);
      add(var1, "Venorys, Aguijón del Mineral", BossCatalog.Tier.RARE, EntityType.CAVE_SPIDER, false);
      add(var1, "Grisvorn, Saqueador de Bruma", BossCatalog.Tier.RARE, EntityType.PILLAGER, false);
      add(var1, "Hachvex, Verdugo del Robledal", BossCatalog.Tier.RARE, EntityType.VINDICATOR, false);
      add(var1, "Morgalyn, Alquimista del Lodazal", BossCatalog.Tier.RARE, EntityType.WITCH, false);
      add(var1, "Cindrakk, Brasa Errante", BossCatalog.Tier.RARE, EntityType.BLAZE, false);
      add(var1, "Gelthos, Núcleo del Fango", BossCatalog.Tier.RARE, EntityType.SLIME, false);
      add(var1, "Magmoryx, Corazón de Basalto", BossCatalog.Tier.RARE, EntityType.MAGMA_CUBE, false);
      add(var1, "Velmorn, Peregrino del Umbral", BossCatalog.Tier.RARE, EntityType.ENDERMAN, false);
      add(var1, "Necrovar, Portador de Ceniza", BossCatalog.Tier.EPIC, EntityType.WITHER_SKELETON, false);
      add(var1, "Ignelys, Sacerdote de las Brasas", BossCatalog.Tier.EPIC, EntityType.BLAZE, false);
      add(var1, "Vendrak, Filo del Ciclón", BossCatalog.Tier.EPIC, EntityType.BREEZE, false);
      add(var1, "Rokmash, Ariete de Obsidiana", BossCatalog.Tier.EPIC, EntityType.RAVAGER, false);
      add(var1, "Prismorys, Ojo de la Marea", BossCatalog.Tier.EPIC, EntityType.GUARDIAN, false);
      add(var1, "Runeth, Invocador del Espejo", BossCatalog.Tier.EPIC, EntityType.EVOKER, false);
      add(var1, "Umbrel, Guardián de la Fractura", BossCatalog.Tier.EPIC, EntityType.ENDERMAN, false);
      add(var1, "Taloryx, Cofre de las Estrellas", BossCatalog.Tier.EPIC, EntityType.SHULKER, false);
      add(var1, "Fangrath, Embestida del Cráter", BossCatalog.Tier.EPIC, EntityType.HOGLIN, false);
      add(var1, "Nocthira, Ala del Presagio", BossCatalog.Tier.EPIC, EntityType.PHANTOM, false);
      add(var1, "Aurvok, Hacha del Tesoro", BossCatalog.Tier.EPIC, EntityType.PIGLIN_BRUTE, false);
      add(var1, "Ferrak, Puño de la Forja", BossCatalog.Tier.EPIC, EntityType.IRON_GOLEM, false);
      add(var1, "Thargran, Mariscal del Amanecer Roto", BossCatalog.Tier.LEGENDARY, EntityType.ZOMBIE, false);
      add(var1, "Velthar, Rey de la Cripta", BossCatalog.Tier.LEGENDARY, EntityType.WITHER_SKELETON, false);
      add(var1, "Solmara, Corona del Incendio", BossCatalog.Tier.LEGENDARY, EntityType.BLAZE, false);
      add(var1, "Aerion, Heraldo de los Cuatro Vientos", BossCatalog.Tier.LEGENDARY, EntityType.BREEZE, false);
      add(var1, "Nautheris, Trono del Océano", BossCatalog.Tier.LEGENDARY, EntityType.ELDER_GUARDIAN, false);
      add(var1, "Orvandel, Tejedor de Runas", BossCatalog.Tier.LEGENDARY, EntityType.EVOKER, false);
      add(var1, "Celoryn, Custodio del Firmamento", BossCatalog.Tier.LEGENDARY, EntityType.SHULKER, false);
      add(var1, "Dromgar, Ruina de las Murallas", BossCatalog.Tier.LEGENDARY, EntityType.RAVAGER, false);
      add(var1, "Vaelthos, Señor del Horizonte", BossCatalog.Tier.LEGENDARY, EntityType.ENDERMAN, false);
      add(var1, "Orothar, Soberano del Bastión", BossCatalog.Tier.LEGENDARY, EntityType.PIGLIN_BRUTE, false);
      add(var1, "Glacerys, Flecha del Invierno", BossCatalog.Tier.LEGENDARY, EntityType.STRAY, false);
      add(var1, "Miasvorn, Arquero de la Pestilencia", BossCatalog.Tier.LEGENDARY, EntityType.BOGGED, false);
      add(var1, "Althera, Bruja del Crepúsculo", BossCatalog.Tier.LEGENDARY, EntityType.WITCH, false);
      add(var1, "Argenthor, Centinela de Plata", BossCatalog.Tier.LEGENDARY, EntityType.IRON_GOLEM, false);
      add(var1, "Pyroth, Latido del Volcán", BossCatalog.Tier.LEGENDARY, EntityType.MAGMA_CUBE, false);
      add(var1, "Seryx, Reina de los Hilos Dorados", BossCatalog.Tier.LEGENDARY, EntityType.CAVE_SPIDER, false);
      add(var1, "Marethon, Capitán del Silencio", BossCatalog.Tier.LEGENDARY, EntityType.DROWNED, false);
      add(var1, "Nekrion, Emperador del Osario", BossCatalog.Tier.MYTHIC, EntityType.ZOMBIE, false);
      add(var1, "Xerathor, Segador del Horizonte", BossCatalog.Tier.MYTHIC, EntityType.WITHER_SKELETON, false);
      add(var1, "Astraphor, Estrella del Averno", BossCatalog.Tier.MYTHIC, EntityType.BLAZE, false);
      add(var1, "Cyrathis, Espiral del Cataclismo", BossCatalog.Tier.MYTHIC, EntityType.BREEZE, false);
      add(var1, "Ocealith, Oráculo de las Mareas", BossCatalog.Tier.MYTHIC, EntityType.ELDER_GUARDIAN, false);
      add(var1, "Arcaneth, Maestro del Vacío Azul", BossCatalog.Tier.MYTHIC, EntityType.EVOKER, false);
      add(var1, "Chronyx, Prisión del Tiempo", BossCatalog.Tier.MYTHIC, EntityType.SHULKER, false);
      add(var1, "Gravoryx, Colmillo del Fin", BossCatalog.Tier.MYTHIC, EntityType.RAVAGER, false);
      add(var1, "Elyndor, Peregrino de los Mil Mundos", BossCatalog.Tier.MYTHIC, EntityType.ENDERMAN, false);
      add(var1, "Aurumex, Tirano de la Corona", BossCatalog.Tier.MYTHIC, EntityType.PIGLIN_BRUTE, false);
      add(var1, "Nycterion, Ala del Sol Muerto", BossCatalog.Tier.MYTHIC, EntityType.PHANTOM, false);
      add(var1, "Coralyth, Ojo del Diluvio", BossCatalog.Tier.MYTHIC, EntityType.GUARDIAN, false);
      add(var1, "Bronthex, Bestia del Último Cráter", BossCatalog.Tier.MYTHIC, EntityType.HOGLIN, false);
      add(var1, "Hielvorn, Arquero del Cero Eterno", BossCatalog.Tier.MYTHIC, EntityType.STRAY, false);
      add(var1, "Putrathis, Heraldo del Miasma", BossCatalog.Tier.MYTHIC, EntityType.BOGGED, false);
      add(var1, "Ysmeria, Reina de la Alquimia", BossCatalog.Tier.MYTHIC, EntityType.WITCH, false);
      add(var1, "Titanorys, Puño de los Antiguos", BossCatalog.Tier.MYTHIC, EntityType.IRON_GOLEM, false);
      add(var1, "Calderyx, Corazón del Núcleo", BossCatalog.Tier.MYTHIC, EntityType.MAGMA_CUBE, false);
      add(var1, "Aranyth, Tejedora de Destinos", BossCatalog.Tier.MYTHIC, EntityType.CAVE_SPIDER, false);
      add(var1, "Nerathos, Almirante del Gran Abismo", BossCatalog.Tier.MYTHIC, EntityType.DROWNED, false);
      add(var1, "Valthruk, Hacha de los Juramentos", BossCatalog.Tier.MYTHIC, EntityType.VINDICATOR, false);
      add(var1, "Gelumor, Devorador de Minerales", BossCatalog.Tier.MYTHIC, EntityType.SLIME, false);
      add(var1, "Oblivar, Soberano de la Nada", BossCatalog.Tier.ABYSSAL, EntityType.WITHER_SKELETON, false);
      add(var1, "Asterion, Sepulcro de las Constelaciones", BossCatalog.Tier.ABYSSAL, EntityType.SHULKER, false);
      add(var1, "Maledryn, Profeta del Silencio Eterno", BossCatalog.Tier.ABYSSAL, EntityType.EVOKER, false);
      add(var1, "Vorthel, Huracán del Último Día", BossCatalog.Tier.ABYSSAL, EntityType.BREEZE, false);
      add(var1, "Thaloryx, Hambre del Océano Negro", BossCatalog.Tier.ABYSSAL, EntityType.ELDER_GUARDIAN, false);
      add(var1, "Erebhor, Antorcha del Sol Extinto", BossCatalog.Tier.ABYSSAL, EntityType.BLAZE, false);
      add(var1, "Morthrak, Devastador de Realidades", BossCatalog.Tier.ABYSSAL, EntityType.RAVAGER, false);
      add(var1, "Zerathiel, Guardián del Fin Absoluto", BossCatalog.Tier.ABYSSAL, EntityType.ENDERMAN, false);
      var1.forEach(var1x -> var0.put(var1x.id(), var1x));
      return Collections.unmodifiableMap(var0);
   }

   private static void add(List<Definition> definitions,String name,Tier tier,EntityType type,boolean mini) {
      definitions.add(new Definition(canonicalId(name),name,tier,type,mini,BossLore.describe(type,mini)));
   }

   public static String canonicalId(String name) {
      return java.text.Normalizer.normalize(name, java.text.Normalizer.Form.NFD)
         .replaceAll("\\p{M}", "").toLowerCase(java.util.Locale.ROOT).replaceAll("[^a-z0-9]+", "_").replaceAll("^_|_$", "");
   }

   public record Definition(String id, String name, BossCatalog.Tier tier, EntityType entityType, boolean mini, String description) {
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

