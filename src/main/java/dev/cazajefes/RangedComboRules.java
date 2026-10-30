package dev.cazajefes;
/** Pure progression rules; callers commit only accepted, non-cancelled impacts. */
final class RangedComboRules {
 static boolean trigger(String id,int count,boolean previousRanged,boolean ranged,boolean charged,double distanceSquared,boolean shield){return switch(id){
 case "marca_cazador"->ranged&&charged&&count>0;
 case "ancla_abisal"->!ranged&&count>0&&previousRanged;
 case "marea_ascendente"->count>0&&previousRanged!=ranged;
 case "cerrojo_vacio"->ranged&&count>=3;
 case "estela_boreal","conductor_tempestades"->ranged&&count>=2;
 case "pulso_retorno"->ranged&&distanceSquared>=144&&count>0;
 case "relevo_fantasma"->ranged;
 case "quebranto"->ranged&&shield;
 default->false;};}
 static boolean stores(String id,boolean ranged,boolean charged,double distanceSquared){return switch(id){case "marca_cazador"->ranged&&charged;case "ancla_abisal"->ranged;case "marea_ascendente"->true;case "cerrojo_vacio","estela_boreal","conductor_tempestades"->ranged;case "pulso_retorno"->ranged&&distanceSquared>=144;default->false;};}
}
