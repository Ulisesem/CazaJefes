package dev.cazajefes;
import java.util.*;
final class BossAbilityCatalog {
 enum Pattern {RINGS,CROSS,CONE,SPIRAL,METEORS,VORTEX,LANES,CHECKER,GEYSERS,NOVA,RUSH,CAGE}
 enum Debuff {SLOWNESS,POISON,WEAKNESS,WITHER,BLINDNESS,HUNGER,LEVITATION,DARKNESS;
  org.bukkit.potion.PotionEffectType potion(){return switch(this){case SLOWNESS->org.bukkit.potion.PotionEffectType.SLOWNESS;case POISON->org.bukkit.potion.PotionEffectType.POISON;case WEAKNESS->org.bukkit.potion.PotionEffectType.WEAKNESS;case WITHER->org.bukkit.potion.PotionEffectType.WITHER;case BLINDNESS->org.bukkit.potion.PotionEffectType.BLINDNESS;case HUNGER->org.bukkit.potion.PotionEffectType.HUNGER;case LEVITATION->org.bukkit.potion.PotionEffectType.LEVITATION;case DARKNESS->org.bukkit.potion.PotionEffectType.DARKNESS;};}
 }
 record Ultimate(String name,Pattern pattern,Debuff effect,double radius,int pulses,double angle){}
 static final Map<String,Ultimate> ALL=Map.ofEntries(
  Map.entry("coloso",new Ultimate("Juicio de los Mundos",Pattern.RINGS,Debuff.SLOWNESS,6,2,0.0)),
  Map.entry("viuda",new Ultimate("Telar del Último Ocaso",Pattern.CROSS,Debuff.SLOWNESS,7,3,0.392699)),
  Map.entry("centinela",new Ultimate("Sentencia de Ceniza",Pattern.CONE,Debuff.SLOWNESS,8,4,0.785398)),
  Map.entry("reina_abeja",new Ultimate("Coronación del Enjambre",Pattern.SPIRAL,Debuff.SLOWNESS,6,2,1.178097)),
  Map.entry("zorro_salvaje",new Ultimate("Danza de Nueve Sombras",Pattern.METEORS,Debuff.SLOWNESS,7,3,1.570796)),
  Map.entry("jabali_infernal",new Ultimate("Estampida del Averno",Pattern.VORTEX,Debuff.SLOWNESS,8,4,1.963495)),
  Map.entry("volcano",new Ultimate("Latido del Cataclismo",Pattern.LANES,Debuff.SLOWNESS,6,2,2.356194)),
  Map.entry("oso_tundra",new Ultimate("Réquiem de la Ventisca",Pattern.CHECKER,Debuff.SLOWNESS,7,3,2.748893)),
  Map.entry("lobo_ceniza",new Ultimate("Aullido de la Luna Rota",Pattern.GEYSERS,Debuff.SLOWNESS,8,4,0.0)),
  Map.entry("cabra_quebrada",new Ultimate("Caída de las Cumbres",Pattern.NOVA,Debuff.SLOWNESS,6,2,0.392699)),
  Map.entry("tortuga_baluarte",new Ultimate("Bastión del Último Mar",Pattern.RUSH,Debuff.SLOWNESS,7,3,0.785398)),
  Map.entry("llama_saqueadora",new Ultimate("Caravana de la Perdición",Pattern.CAGE,Debuff.SLOWNESS,8,4,1.178097)),
  Map.entry("corredor_sepulcral",new Ultimate("Marcha de los Olvidados",Pattern.RINGS,Debuff.POISON,6,2,1.570796)),
  Map.entry("hueso_viejo",new Ultimate("Osario de las Mil Flechas",Pattern.CROSS,Debuff.POISON,7,3,1.963495)),
  Map.entry("aranhuelo",new Ultimate("Nido de la Ruina",Pattern.CONE,Debuff.POISON,8,4,2.356194)),
  Map.entry("brasa_vagabunda",new Ultimate("Cremación Errante",Pattern.SPIRAL,Debuff.POISON,6,2,2.748893)),
  Map.entry("saqueador_gris",new Ultimate("Ejecución del Yermo",Pattern.METEORS,Debuff.POISON,7,3,0.0)),
  Map.entry("moho_muerto",new Ultimate("Sepultura del Pantano",Pattern.VORTEX,Debuff.POISON,8,4,0.392699)),
  Map.entry("cubo_turbio",new Ultimate("Marea del Fango Vivo",Pattern.LANES,Debuff.POISON,6,2,0.785398)),
  Map.entry("murcielago_fosa",new Ultimate("Eclipse de la Fosa",Pattern.CHECKER,Debuff.POISON,7,3,1.178097)),
  Map.entry("cazador_oxidado",new Ultimate("Cacería del Acero Enfermo",Pattern.GEYSERS,Debuff.POISON,8,4,1.570796)),
  Map.entry("tejedora_cripta",new Ultimate("Sudario de la Cripta",Pattern.NOVA,Debuff.POISON,6,2,1.963495)),
  Map.entry("arquero_invernal",new Ultimate("Cero Absoluto",Pattern.RUSH,Debuff.POISON,7,3,2.356194)),
  Map.entry("bruto_piglin",new Ultimate("Mandato de la Forja Negra",Pattern.CAGE,Debuff.POISON,8,4,2.748893)),
  Map.entry("sapo_fuego",new Ultimate("Erupción del Sapo Carmesí",Pattern.RINGS,Debuff.WEAKNESS,6,2,0.0)),
  Map.entry("bruja_musgo",new Ultimate("Jardín de Espinas Muertas",Pattern.CROSS,Debuff.WEAKNESS,7,3,0.392699)),
  Map.entry("ahogado_abisal",new Ultimate("Naufragio del Alma",Pattern.CONE,Debuff.WEAKNESS,8,4,0.785398)),
  Map.entry("vex_cobrizo",new Ultimate("Corte del Cobre Maldito",Pattern.SPIRAL,Debuff.WEAKNESS,6,2,1.178097)),
  Map.entry("caballero_wither",new Ultimate("Cabalgata del Sol Extinto",Pattern.METEORS,Debuff.WEAKNESS,7,3,1.570796)),
  Map.entry("vigia_prismarina",new Ultimate("Prisión de Prismarina",Pattern.VORTEX,Debuff.WEAKNESS,8,4,1.963495)),
  Map.entry("cuervo_carmesi",new Ultimate("Vuelo de la Sangre Nueva",Pattern.LANES,Debuff.WEAKNESS,6,2,2.356194)),
  Map.entry("devastador_hambriento",new Ultimate("Banquete del Hambre Eterna",Pattern.CHECKER,Debuff.WEAKNESS,7,3,2.748893)),
  Map.entry("bestia_lodazal",new Ultimate("Despertar del Lodazal",Pattern.GEYSERS,Debuff.WEAKNESS,8,4,0.0)),
  Map.entry("brisa_cortante",new Ultimate("Cuchillas del Horizonte",Pattern.NOVA,Debuff.WEAKNESS,6,2,0.392699)),
  Map.entry("evocador_umbral",new Ultimate("Liturgia del Umbral",Pattern.RUSH,Debuff.WEAKNESS,7,3,0.785398)),
  Map.entry("golem_grieta",new Ultimate("Fractura del Núcleo",Pattern.CAGE,Debuff.WEAKNESS,8,4,1.178097)),
  Map.entry("ghast_azabache",new Ultimate("Lamento de Azabache",Pattern.RINGS,Debuff.WITHER,6,2,1.570796)),
  Map.entry("aracnido_cristal",new Ultimate("Prisma de la Viuda Blanca",Pattern.CROSS,Debuff.WITHER,7,3,1.963495)),
  Map.entry("hongo_infernal",new Ultimate("Bosque de las Esporas Rojas",Pattern.CONE,Debuff.WITHER,8,4,2.356194)),
  Map.entry("desollador_bogged",new Ultimate("Cosecha del Arco Fúnebre",Pattern.SPIRAL,Debuff.WITHER,6,2,2.748893)),
  Map.entry("anciano_coral",new Ultimate("Sepulcro de Coral",Pattern.METEORS,Debuff.WITHER,7,3,0.0)),
  Map.entry("espectro_fin",new Ultimate("Horizonte Invertido",Pattern.VORTEX,Debuff.WITHER,8,4,0.392699)),
  Map.entry("dragon_ceniza",new Ultimate("Lluvia del Dragón Ceniciento",Pattern.LANES,Debuff.WITHER,6,2,0.785398)),
  Map.entry("reina_brezal",new Ultimate("Corazón del Brezal Negro",Pattern.CHECKER,Debuff.WITHER,7,3,1.178097)),
  Map.entry("carcelero_carmesi",new Ultimate("Cadalso Carmesí",Pattern.GEYSERS,Debuff.WITHER,8,4,1.570796)),
  Map.entry("creaking_ancestral",new Ultimate("Raíces del Primer Crepúsculo",Pattern.NOVA,Debuff.WITHER,6,2,1.963495)),
  Map.entry("tormenta_breeze",new Ultimate("Ojo de la Tempestad",Pattern.RUSH,Debuff.WITHER,7,3,2.356194)),
  Map.entry("quimera_endermite",new Ultimate("Multiplicación del Vacío",Pattern.CAGE,Debuff.WITHER,8,4,2.748893)),
  Map.entry("verdugo_necrotico",new Ultimate("Decapitación del Firmamento",Pattern.RINGS,Debuff.BLINDNESS,6,2,0.0)),
  Map.entry("serpiente_pillager",new Ultimate("Escamas del Tirador Silente",Pattern.CROSS,Debuff.BLINDNESS,7,3,0.392699)),
  Map.entry("colmillo_vacio",new Ultimate("Mandíbula del Espacio Roto",Pattern.CONE,Debuff.BLINDNESS,8,4,0.785398)),
  Map.entry("coloso_fragmentado",new Ultimate("Despiece del Titán Caído",Pattern.SPIRAL,Debuff.BLINDNESS,6,2,1.178097)),
  Map.entry("rey_brasas",new Ultimate("Corona del Último Incendio",Pattern.METEORS,Debuff.BLINDNESS,7,3,1.570796)),
  Map.entry("titan_barro",new Ultimate("Diluvio de Arcilla",Pattern.VORTEX,Debuff.BLINDNESS,8,4,1.963495)),
  Map.entry("leviatan_ender",new Ultimate("Catedral de los Mil Ojos",Pattern.LANES,Debuff.BLINDNESS,6,2,2.356194)),
  Map.entry("liche_noche",new Ultimate("Noche sin Amanecer",Pattern.CHECKER,Debuff.BLINDNESS,7,3,2.748893)),
  Map.entry("colmillo_abismal",new Ultimate("Fauces del Abismo Profundo",Pattern.GEYSERS,Debuff.BLINDNESS,8,4,0.0)),
  Map.entry("warden_abismal",new Ultimate("Silencio de los Nueve Ecos",Pattern.NOVA,Debuff.BLINDNESS,6,2,0.392699)),
  Map.entry("maestro_creaking",new Ultimate("Bosque que Devora la Luz",Pattern.RUSH,Debuff.BLINDNESS,7,3,0.785398)),
  Map.entry("reina_piglin",new Ultimate("Decreto de la Reina Carmesí",Pattern.CAGE,Debuff.BLINDNESS,8,4,1.178097)),
  Map.entry("guardian_abismal",new Ultimate("Mausoleo del Océano Antiguo",Pattern.RINGS,Debuff.HUNGER,6,2,1.570796)),
  Map.entry("xalther",new Ultimate("Trono de la Nada",Pattern.CROSS,Debuff.HUNGER,7,3,1.963495)),
  Map.entry("morvex",new Ultimate("Constelación del Arquero Caído",Pattern.CONE,Debuff.HUNGER,8,4,2.356194)),
  Map.entry("kaelthar",new Ultimate("Puño de la Estrella Muerta",Pattern.SPIRAL,Debuff.HUNGER,6,2,2.748893)),
  Map.entry("vaelora",new Ultimate("Profecía de las Sombras Vivas",Pattern.METEORS,Debuff.HUNGER,7,3,0.0)),
  Map.entry("draxion",new Ultimate("Devastación del Horizonte Eterno",Pattern.VORTEX,Debuff.HUNGER,8,4,0.392699)),
  Map.entry("tharok",new Ultimate("Cosecha de las Almas Gemelas",Pattern.LANES,Debuff.HUNGER,6,2,0.785398)),
  Map.entry("ignaroth",new Ultimate("Nacimiento del Sol Negro",Pattern.CHECKER,Debuff.HUNGER,7,3,1.178097)),
  Map.entry("zyrakk",new Ultimate("Sello del Umbral Quebrado",Pattern.GEYSERS,Debuff.HUNGER,8,4,1.570796)),
  Map.entry("thalassar",new Ultimate("Coronación de la Fosa Marina",Pattern.NOVA,Debuff.HUNGER,6,2,1.963495)),
  Map.entry("aerthos",new Ultimate("Ciclón de las Siete Ruinas",Pattern.RUSH,Debuff.HUNGER,7,3,2.356194)),
  Map.entry("nyssara",new Ultimate("Maternidad de la Penumbra",Pattern.CAGE,Debuff.HUNGER,8,4,2.748893)),
  Map.entry("velkaris",new Ultimate("Paso entre los Mundos Muertos",Pattern.RINGS,Debuff.LEVITATION,6,2,0.0)),
  Map.entry("brumgor",new Ultimate("Colmillo del Eclipse Sangriento",Pattern.CROSS,Debuff.LEVITATION,7,3,0.392699)),
  Map.entry("korvath",new Ultimate("Veredicto del Silencio Total",Pattern.CONE,Debuff.LEVITATION,8,4,0.785398)),
  Map.entry("ossyra",new Ultimate("Medianoche de las Mil Plagas",Pattern.SPIRAL,Debuff.LEVITATION,6,2,1.178097)));
 static Ultimate ultimate(BossCatalog.Definition boss){Ultimate profile=ALL.get(boss.legacyId());if(profile==null)throw new IllegalArgumentException("Falta definitivo de "+boss.id());return profile;}
 static boolean hits(Pattern pattern,double x,double z,int pulse,int pulses,double radius,double angle){
  double c=Math.cos(angle),s=Math.sin(angle),a=x*c+z*s,b=-x*s+z*c,d=Math.hypot(x,z);if(d>radius+1)return false;
  return switch(pattern){
   case RINGS->Math.abs(d-radius*(pulse+1)/pulses)<1.25;
   case CROSS->Math.abs(a)<1||Math.abs(b)<1;
   case CONE->a>0&&Math.abs(b)<a*.65+1;
   case SPIRAL->Math.abs(Math.sin(Math.atan2(z,x)-angle-pulse*1.05))*d<1;
   case METEORS->Math.hypot(a-(pulse%2==0?-2:2),b-((pulse/2)%2==0?-2:2))<2;
   case VORTEX->pulse==pulses-1?d<radius*.7:d<radius;
   case LANES->Math.abs(b-((pulse%3)-1)*3)<1.1;
   case CHECKER->Math.floorMod((int)Math.floor(a/2)+(int)Math.floor(b/2),2)==pulse%2;
   case GEYSERS->Math.abs(d-(2+pulse*1.4))<1.1&&Math.abs(Math.sin(Math.atan2(z,x)*3-angle))<.65;
   case NOVA->d>2+Math.sin(pulse)*.6;
   case RUSH->Math.abs(b)<1.3&&a>=-1;
   case CAGE->Math.abs(d-(radius-pulse*1.6))<1;
  };
 }
}
