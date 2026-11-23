package dev.cazajefes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
final class FeatureSettings {
 static double number(ConfigurationSection c,String key,double fallback,double min,double max){double n=c.getDouble(key,fallback);return Double.isFinite(n)?Math.clamp(n,min,max):fallback;}
 static int[] marketSlots(ConfigurationSection c){int a=c.getInt("presentation.sanctuary.sigil-slot",21),b=c.getInt("presentation.sanctuary.amulet-slot",23);if(!slot(a)||!slot(b)||a==b)return new int[]{21,23};return new int[]{a,b};}
 private static boolean slot(int n){return n>=10&&n<=43&&n%9!=0&&n%9!=8;}
 static void install(JavaPlugin plugin){boolean changed=false;var defaults=plugin.getConfig().getDefaults();if(defaults!=null)for(String path:defaults.getKeys(true))if((path.startsWith("presentation.")||path.startsWith("books."))&&!defaults.isConfigurationSection(path)&&!plugin.getConfig().contains(path,true)){plugin.getConfig().set(path,defaults.get(path));changed=true;}if(changed)plugin.saveConfig();}
}
