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

         if(var1.equals("devorador_ecos")||var1.equals("azrath_devorador_de_ecos"))var1="nharzul_soberano_del_abismo";
         Definition exact=DEFINITIONS.get(var1);
         if (exact!=null) return exact;
         final String alias=var1;
         return DEFINITIONS.values().stream().filter(d -> d.migrationId().equals(alias)).findFirst().orElse(null);
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
      add(var1, "cazador_oxidado", "Ferrun, Colmillo de Hierro", BossCatalog.Tier.RARE, EntityType.WOLF, true);
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
      add(var1, "xalther", "Xalther, Emperador del Vacío", BossCatalog.Tier.ABYSSAL, EntityType.ZOMBIE, false);
      add(var1, "morvex", "Morvex, Arquero del Ocaso", BossCatalog.Tier.ABYSSAL, EntityType.SKELETON, false);
      add(var1, "kaelthar", "Kaelthar, Puño del Averno", BossCatalog.Tier.ABYSSAL, EntityType.PIGLIN_BRUTE, false);
      add(var1, "vaelora", "Vaelora, Oráculo de las Sombras", BossCatalog.Tier.ABYSSAL, EntityType.EVOKER, false);
      add(var1, "draxion", "Draxion, Devastador Eterno", BossCatalog.Tier.ABYSSAL, EntityType.RAVAGER, false);
      add(var1, "tharok", "Tharok, Segador de Almas", BossCatalog.Tier.ABYSSAL, EntityType.WITHER_SKELETON, false);
      add(var1, "ignaroth", "Ignaroth, Sol Negro", BossCatalog.Tier.ABYSSAL, EntityType.BLAZE, false);
      add(var1, "zyrakk", "Zyrakk, Custodio del Umbral", BossCatalog.Tier.ABYSSAL, EntityType.SHULKER, false);
      add(var1, "thalassar", "Thalassar, Monarca del Abismo", BossCatalog.Tier.ABYSSAL, EntityType.ELDER_GUARDIAN, false);
      add(var1, "aerthos", "Aerthos, Ciclón de la Ruina", BossCatalog.Tier.ABYSSAL, EntityType.BREEZE, false);
      add(var1, "nyssara", "Nyssara, Matriarca de la Penumbra", BossCatalog.Tier.ABYSSAL, EntityType.CAVE_SPIDER, false);
      add(var1, "velkaris", "Velkaris, Caminante del Vacío", BossCatalog.Tier.ABYSSAL, EntityType.ENDERMAN, false);
      add(var1, "brumgor", "Brumgor, Colmillo del Eclipse", BossCatalog.Tier.ABYSSAL, EntityType.HOGLIN, false);
      add(var1, "korvath", "Korvath, Verdugo del Silencio", BossCatalog.Tier.ABYSSAL, EntityType.VINDICATOR, false);
      add(var1, "ossyra", "Ossyra, Plaga de la Medianoche", BossCatalog.Tier.ABYSSAL, EntityType.BOGGED, false);
      add(var1, "nuevo_thumra", "Thumra, Pezuña del Trueno", BossCatalog.Tier.COMMON, EntityType.COW, true);
      add(var1, "nuevo_porvax", "Porvax, Colmillo de Cobre", BossCatalog.Tier.COMMON, EntityType.PIG, true);
      add(var1, "nuevo_lanora", "Lanora, Vellón de Escarcha", BossCatalog.Tier.COMMON, EntityType.SHEEP, true);
      add(var1, "nuevo_plumak", "Plumak, Pico del Vendaval", BossCatalog.Tier.COMMON, EntityType.CHICKEN, true);
      add(var1, "nuevo_lepthor", "Lepthor, Salto del Eclipse", BossCatalog.Tier.COMMON, EntityType.RABBIT, true);
      add(var1, "nuevo_equiron", "Equiron, Crin de Tormenta", BossCatalog.Tier.COMMON, EntityType.HORSE, true);
      add(var1, "nuevo_rokbur", "Rokbur, Casco de Granito", BossCatalog.Tier.COMMON, EntityType.DONKEY, true);
      add(var1, "nuevo_mularis", "Mularis, Guardián del Desfiladero", BossCatalog.Tier.COMMON, EntityType.MULE, true);
      add(var1, "nuevo_dunarak", "Dunarak, Joroba del Desierto", BossCatalog.Tier.COMMON, EntityType.CAMEL, true);
      add(var1, "nuevo_bambor", "Bambor, Abrazo del Bosque", BossCatalog.Tier.COMMON, EntityType.PANDA, true);
      add(var1, "nuevo_axoria", "Axoria, Branquia de Nácar", BossCatalog.Tier.COMMON, EntityType.AXOLOTL, true);
      add(var1, "nuevo_ocelkar", "Ocelkar, Garra del Dosel", BossCatalog.Tier.COMMON, EntityType.OCELOT, true);
      add(var1, "nuevo_feluna", "Feluna, Sombra de Medianoche", BossCatalog.Tier.COMMON, EntityType.CAT, true);
      add(var1, "nuevo_arakor", "Arakor, Pluma del Relámpago", BossCatalog.Tier.COMMON, EntityType.PARROT, true);
      add(var1, "nuevo_escudrax", "Escudrax, Coraza del Yermo", BossCatalog.Tier.COMMON, EntityType.ARMADILLO, true);
      add(var1, "nuevo_delfyr", "Delfyr, Aleta de la Marea", BossCatalog.Tier.COMMON, EntityType.DOLPHIN, true);
      add(var1, "nuevo_tintor", "Tintor, Abrazo de Tinta", BossCatalog.Tier.COMMON, EntityType.SQUID, true);
      add(var1, "nuevo_luminox", "Luminox, Faro de las Profundidades", BossCatalog.Tier.COMMON, EntityType.GLOW_SQUID, true);
      add(var1, "nuevo_sepulkris", "Sepulkris, Guardia del Osario", BossCatalog.Tier.RARE, EntityType.ZOMBIE, false);
      add(var1, "nuevo_dunvex", "Dunvex, Errante de Arena", BossCatalog.Tier.RARE, EntityType.HUSK, false);
      add(var1, "nuevo_marvok", "Marvok, Ancla del Naufragio", BossCatalog.Tier.RARE, EntityType.DROWNED, false);
      add(var1, "nuevo_osserak", "Osserak, Flecha de Caliza", BossCatalog.Tier.RARE, EntityType.SKELETON, false);
      add(var1, "nuevo_nevrath", "Nevrath, Vigía del Hielo", BossCatalog.Tier.RARE, EntityType.STRAY, false);
      add(var1, "nuevo_lodrakk", "Lodrakk, Flechero del Pantano", BossCatalog.Tier.RARE, EntityType.BOGGED, false);
      add(var1, "nuevo_silvarax", "Silvarax, Hilandera del Sotobosque", BossCatalog.Tier.RARE, EntityType.SPIDER, false);
      add(var1, "nuevo_venorys", "Venorys, Aguijón del Mineral", BossCatalog.Tier.RARE, EntityType.CAVE_SPIDER, false);
      add(var1, "nuevo_grisvorn", "Grisvorn, Saqueador de Bruma", BossCatalog.Tier.RARE, EntityType.PILLAGER, false);
      add(var1, "nuevo_hachvex", "Hachvex, Verdugo del Robledal", BossCatalog.Tier.RARE, EntityType.VINDICATOR, false);
      add(var1, "nuevo_morgalyn", "Morgalyn, Alquimista del Lodazal", BossCatalog.Tier.RARE, EntityType.WITCH, false);
      add(var1, "nuevo_cindrakk", "Cindrakk, Brasa Errante", BossCatalog.Tier.RARE, EntityType.BLAZE, false);
      add(var1, "nuevo_gelthos", "Gelthos, Núcleo del Fango", BossCatalog.Tier.RARE, EntityType.SLIME, false);
      add(var1, "nuevo_magmoryx", "Magmoryx, Corazón de Basalto", BossCatalog.Tier.RARE, EntityType.MAGMA_CUBE, false);
      add(var1, "nuevo_velmorn", "Velmorn, Peregrino del Umbral", BossCatalog.Tier.RARE, EntityType.ENDERMAN, false);
      add(var1, "nuevo_necrovar", "Necrovar, Portador de Ceniza", BossCatalog.Tier.EPIC, EntityType.WITHER_SKELETON, false);
      add(var1, "nuevo_ignelys", "Ignelys, Sacerdote de las Brasas", BossCatalog.Tier.EPIC, EntityType.BLAZE, false);
      add(var1, "nuevo_vendrak", "Vendrak, Filo del Ciclón", BossCatalog.Tier.EPIC, EntityType.BREEZE, false);
      add(var1, "nuevo_rokmash", "Rokmash, Ariete de Obsidiana", BossCatalog.Tier.EPIC, EntityType.RAVAGER, false);
      add(var1, "nuevo_prismorys", "Prismorys, Ojo de la Marea", BossCatalog.Tier.EPIC, EntityType.GUARDIAN, false);
      add(var1, "nuevo_runeth", "Runeth, Invocador del Espejo", BossCatalog.Tier.EPIC, EntityType.EVOKER, false);
      add(var1, "nuevo_umbrel", "Umbrel, Guardián de la Fractura", BossCatalog.Tier.EPIC, EntityType.ENDERMAN, false);
      add(var1, "nuevo_taloryx", "Taloryx, Cofre de las Estrellas", BossCatalog.Tier.EPIC, EntityType.SHULKER, false);
      add(var1, "nuevo_fangrath", "Fangrath, Embestida del Cráter", BossCatalog.Tier.EPIC, EntityType.HOGLIN, false);
      add(var1, "nuevo_nocthira", "Nocthira, Ala del Presagio", BossCatalog.Tier.EPIC, EntityType.PHANTOM, false);
      add(var1, "nuevo_aurvok", "Aurvok, Hacha del Tesoro", BossCatalog.Tier.EPIC, EntityType.PIGLIN_BRUTE, false);
      add(var1, "nuevo_ferrak", "Ferrak, Puño de la Forja", BossCatalog.Tier.EPIC, EntityType.IRON_GOLEM, false);
      add(var1, "nuevo_thargran", "Thargran, Mariscal del Amanecer Roto", BossCatalog.Tier.LEGENDARY, EntityType.ZOMBIE, false);
      add(var1, "nuevo_velthar", "Velthar, Rey de la Cripta", BossCatalog.Tier.LEGENDARY, EntityType.WITHER_SKELETON, false);
      add(var1, "nuevo_solmara", "Solmara, Corona del Incendio", BossCatalog.Tier.LEGENDARY, EntityType.BLAZE, false);
      add(var1, "nuevo_aerion", "Aerion, Heraldo de los Cuatro Vientos", BossCatalog.Tier.LEGENDARY, EntityType.BREEZE, false);
      add(var1, "nuevo_nautheris", "Nautheris, Trono del Océano", BossCatalog.Tier.LEGENDARY, EntityType.ELDER_GUARDIAN, false);
      add(var1, "nuevo_orvandel", "Orvandel, Tejedor de Runas", BossCatalog.Tier.LEGENDARY, EntityType.EVOKER, false);
      add(var1, "nuevo_celoryn", "Celoryn, Custodio del Firmamento", BossCatalog.Tier.LEGENDARY, EntityType.SHULKER, false);
      add(var1, "nuevo_dromgar", "Dromgar, Ruina de las Murallas", BossCatalog.Tier.LEGENDARY, EntityType.RAVAGER, false);
      add(var1, "nuevo_vaelthos", "Vaelthos, Señor del Horizonte", BossCatalog.Tier.LEGENDARY, EntityType.ENDERMAN, false);
      add(var1, "nuevo_orothar", "Orothar, Soberano del Bastión", BossCatalog.Tier.LEGENDARY, EntityType.PIGLIN_BRUTE, false);
      add(var1, "nuevo_glacerys", "Glacerys, Flecha del Invierno", BossCatalog.Tier.LEGENDARY, EntityType.STRAY, false);
      add(var1, "nuevo_miasvorn", "Miasvorn, Arquero de la Pestilencia", BossCatalog.Tier.LEGENDARY, EntityType.BOGGED, false);
      add(var1, "nuevo_althera", "Althera, Bruja del Crepúsculo", BossCatalog.Tier.LEGENDARY, EntityType.WITCH, false);
      add(var1, "nuevo_argenthor", "Argenthor, Centinela de Plata", BossCatalog.Tier.LEGENDARY, EntityType.IRON_GOLEM, false);
      add(var1, "nuevo_pyroth", "Pyroth, Latido del Volcán", BossCatalog.Tier.LEGENDARY, EntityType.MAGMA_CUBE, false);
      add(var1, "nuevo_seryx", "Seryx, Reina de los Hilos Dorados", BossCatalog.Tier.LEGENDARY, EntityType.CAVE_SPIDER, false);
      add(var1, "nuevo_marethon", "Marethon, Capitán del Silencio", BossCatalog.Tier.LEGENDARY, EntityType.DROWNED, false);
      add(var1, "nuevo_nekrion", "Nekrion, Emperador del Osario", BossCatalog.Tier.MYTHIC, EntityType.ZOMBIE, false);
      add(var1, "nuevo_xerathor", "Xerathor, Segador del Horizonte", BossCatalog.Tier.MYTHIC, EntityType.WITHER_SKELETON, false);
      add(var1, "nuevo_astraphor", "Astraphor, Estrella del Averno", BossCatalog.Tier.MYTHIC, EntityType.BLAZE, false);
      add(var1, "nuevo_cyrathis", "Cyrathis, Espiral del Cataclismo", BossCatalog.Tier.MYTHIC, EntityType.BREEZE, false);
      add(var1, "nuevo_ocealith", "Ocealith, Oráculo de las Mareas", BossCatalog.Tier.MYTHIC, EntityType.ELDER_GUARDIAN, false);
      add(var1, "nuevo_arcaneth", "Arcaneth, Maestro del Vacío Azul", BossCatalog.Tier.MYTHIC, EntityType.EVOKER, false);
      add(var1, "nuevo_chronyx", "Chronyx, Prisión del Tiempo", BossCatalog.Tier.MYTHIC, EntityType.SHULKER, false);
      add(var1, "nuevo_gravoryx", "Gravoryx, Colmillo del Fin", BossCatalog.Tier.MYTHIC, EntityType.RAVAGER, false);
      add(var1, "nuevo_elyndor", "Elyndor, Peregrino de los Mil Mundos", BossCatalog.Tier.MYTHIC, EntityType.ENDERMAN, false);
      add(var1, "nuevo_aurumex", "Aurumex, Tirano de la Corona", BossCatalog.Tier.MYTHIC, EntityType.PIGLIN_BRUTE, false);
      add(var1, "nuevo_nycterion", "Nycterion, Ala del Sol Muerto", BossCatalog.Tier.MYTHIC, EntityType.PHANTOM, false);
      add(var1, "nuevo_coralyth", "Coralyth, Ojo del Diluvio", BossCatalog.Tier.MYTHIC, EntityType.GUARDIAN, false);
      add(var1, "nuevo_bronthex", "Bronthex, Bestia del Último Cráter", BossCatalog.Tier.MYTHIC, EntityType.HOGLIN, false);
      add(var1, "nuevo_hielvorn", "Hielvorn, Arquero del Cero Eterno", BossCatalog.Tier.MYTHIC, EntityType.STRAY, false);
      add(var1, "nuevo_putrathis", "Putrathis, Heraldo del Miasma", BossCatalog.Tier.MYTHIC, EntityType.BOGGED, false);
      add(var1, "nuevo_ysmeria", "Ysmeria, Reina de la Alquimia", BossCatalog.Tier.MYTHIC, EntityType.WITCH, false);
      add(var1, "nuevo_titanorys", "Titanorys, Puño de los Antiguos", BossCatalog.Tier.MYTHIC, EntityType.IRON_GOLEM, false);
      add(var1, "nuevo_calderyx", "Calderyx, Corazón del Núcleo", BossCatalog.Tier.MYTHIC, EntityType.MAGMA_CUBE, false);
      add(var1, "nuevo_aranyth", "Aranyth, Tejedora de Destinos", BossCatalog.Tier.MYTHIC, EntityType.CAVE_SPIDER, false);
      add(var1, "nuevo_nerathos", "Nerathos, Almirante del Gran Abismo", BossCatalog.Tier.MYTHIC, EntityType.DROWNED, false);
      add(var1, "nuevo_valthruk", "Valthruk, Hacha de los Juramentos", BossCatalog.Tier.MYTHIC, EntityType.VINDICATOR, false);
      add(var1, "nuevo_gelumor", "Gelumor, Devorador de Minerales", BossCatalog.Tier.MYTHIC, EntityType.SLIME, false);
      add(var1, "nuevo_oblivar", "Oblivar, Soberano de la Nada", BossCatalog.Tier.ABYSSAL, EntityType.WITHER_SKELETON, false);
      add(var1, "nuevo_asterion", "Asterion, Sepulcro de las Constelaciones", BossCatalog.Tier.ABYSSAL, EntityType.SHULKER, false);
      add(var1, "nuevo_maledryn", "Maledryn, Profeta del Silencio Eterno", BossCatalog.Tier.ABYSSAL, EntityType.EVOKER, false);
      add(var1, "nuevo_vorthel", "Vorthel, Huracán del Último Día", BossCatalog.Tier.ABYSSAL, EntityType.BREEZE, false);
      add(var1, "nuevo_thaloryx", "Thaloryx, Hambre del Océano Negro", BossCatalog.Tier.ABYSSAL, EntityType.ELDER_GUARDIAN, false);
      add(var1, "nuevo_erebhor", "Erebhor, Antorcha del Sol Extinto", BossCatalog.Tier.ABYSSAL, EntityType.BLAZE, false);
      add(var1, "nuevo_morthrak", "Morthrak, Devastador de Realidades", BossCatalog.Tier.ABYSSAL, EntityType.RAVAGER, false);
      add(var1, "nuevo_zerathiel", "Zerathiel, Guardián del Fin Absoluto", BossCatalog.Tier.ABYSSAL, EntityType.ENDERMAN, false);
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
         default -> BossLore.describe(var4,var5);
      };
      var0.add(new BossCatalog.Definition(canonicalId(var2), var2, var3, var4, var5, var6, var1));
   }

   public static String canonicalId(String name) {
      return java.text.Normalizer.normalize(name, java.text.Normalizer.Form.NFD)
         .replaceAll("\\p{M}", "").toLowerCase(java.util.Locale.ROOT).replaceAll("[^a-z0-9]+", "_").replaceAll("^_|_$", "");
   }

   public record Definition(String id, String name, BossCatalog.Tier tier, EntityType entityType, boolean mini, String description, String migrationId) {
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

