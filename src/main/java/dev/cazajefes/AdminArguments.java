package dev.cazajefes;
import java.util.Locale;
final class AdminArguments {
 static String root(String value){return switch(value.toLowerCase(Locale.ROOT)){case "huevo"->"egg";case "jefe"->"boss";case "punto","spawnpoints","sp"->"spawnpoint";case "recargar"->"reload";case "fragmento"->"fragment";case "tributo"->"tribute";case "brujula","c"->"compass";case "libro"->"book";case "armadura"->"armor";case "arma"->"weapon";case "esencia"->"essence";default->value.toLowerCase(Locale.ROOT);};}
 static boolean name(String value){return value!=null&&value.matches("[a-zA-Z0-9_-]{1,32}");}
 static long seconds(String value){try{String s=value.toLowerCase(Locale.ROOT);long factor=s.endsWith("m")?60:s.endsWith("h")?3600:1;if(s.endsWith("s")||factor!=1)s=s.substring(0,s.length()-1);long seconds=Math.multiplyExact(Long.parseLong(s),factor);return seconds>=10&&seconds<=86400?seconds:-1;}catch(RuntimeException error){return -1;}}
 static boolean due(long now,long next,boolean active){return !active&&next>0&&now>=next;}
}
