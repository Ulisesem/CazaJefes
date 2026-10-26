package dev.cazajefes;
import java.util.Locale;
enum AuraQuality {
 LOW(12,2,"baja"),MEDIUM(24,1,"media"),HIGH(36,1,"alta"),OFF(0,1,"off");
 final int points,every;final String label;AuraQuality(int points,int every,String label){this.points=points;this.every=every;this.label=label;}
 int sends(){return this==OFF?0:points+1;}
 static AuraQuality parse(String text){if(text==null)return null;return switch(text.toLowerCase(Locale.ROOT)){case "baja","low"->LOW;case "media","medium"->MEDIUM;case "alta","high"->HIGH;case "off","no","ninguna"->OFF;default->null;};}
 static AuraQuality fit(AuraQuality requested,int available){if(requested==OFF)return OFF;for(AuraQuality quality:new AuraQuality[]{HIGH,MEDIUM,LOW})if(quality.ordinal()<=requested.ordinal()&&quality.sends()<=available)return quality;return OFF;}
}
