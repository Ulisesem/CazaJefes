package dev.cazajefes;
final class RangedProcRules {
 static double chance(String id,int level){int n=Math.max(1,level);return switch(id){case "carga_inestable"->.15+.10*(Math.min(4,n)-1);case "cerrojo_vacio"->.20+.10*(Math.min(5,n)-1);case "relevo_fantasma"->.20+.15*(Math.min(4,n)-1);case "flecha_silencio","impacto_ariete","municion_necrotica","destello_umbral","grillete_hierro","pulso_retorno","ojo_tormenta"->.20+.15*(Math.min(3,n)-1);case "marca_cazador"->.20+.10*(Math.min(4,n)-1);case "estela_boreal","quebranto"->.25+.15*(Math.min(3,n)-1);default->1;};}
 static boolean rolls(String id,int level,double roll){return roll>=0&&roll<chance(id,level);}
 static boolean direct(String id){return switch(id){case "flecha_silencio","impacto_ariete","municion_necrotica","destello_umbral","grillete_hierro","carga_inestable","cerrojo_vacio","relevo_fantasma","pulso_retorno","ojo_tormenta","marca_cazador","estela_boreal","quebranto"->true;default->false;};}
 static double extraDamage(String id,int level){return switch(id){case "carga_inestable"->3*Math.max(1,Math.min(4,level));case "pulso_retorno","ojo_tormenta"->Math.max(1,Math.min(3,level));case "marca_cazador"->Math.max(1,Math.min(4,level));default->0;};}
 static double blastDamage(int level){return 3+3*Math.max(1,Math.min(3,level));}
 static double blastRadius(int level){return 2+Math.max(1,Math.min(3,level));}
 static double stepDistance(int level,double roll){return Math.max(1,Math.min(4,level))+Math.max(0,Math.min(1,roll));}
}
