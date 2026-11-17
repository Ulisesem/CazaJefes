package dev.cazajefes;

import com.google.gson.JsonObject;
import org.bukkit.entity.EntityType;

/** Family mechanics and bounded balance settings shared by the full boss catalog. */
final class EncounterDesign {
    enum Family {
        BRUISER("Martillo del Asedio","Rompe el sello del arma",1),
        HUNTER("Emboscada de Colmillos","Rompe las huellas del cazador",2),
        SWARM("Corona del Enjambre","Rompe los focos del enjambre",2),
        CASTER("Convergencia de Runas","Rompe dos runas",2),
        ARCHER("Cruce de Saetas","Rompe los puntos de mira",2),
        AQUATIC("Resaca de la Fosa","Rompe los nudos de corriente",2),
        SHELL("Bastión Quebradizo","Rompe el sello del caparazón",1),
        VOID("Eclipse de Fracturas","Rompe los anclajes del vacío",2);

        final String name,hint;
        final int required;
        Family(String name,String hint,int required){this.name=name;this.hint=hint;this.required=required;}
    }

    record Rules(boolean enabled,boolean scaling,boolean weakpoints,int cooldown,int charge,
                 int vulnerabilityTicks,double vulnerabilityBonus,int maximumPlayers){}

    static Family family(EntityType type){
        if(AquaticCombat.aquatic(type))return Family.AQUATIC;
        return switch(type){
            case SPIDER,CAVE_SPIDER,WOLF,FOX,CAT,OCELOT,RABBIT,PHANTOM -> Family.HUNTER;
            case BEE,VEX,SILVERFISH,ENDERMITE,BAT,PARROT -> Family.SWARM;
            case EVOKER,ILLUSIONER,WITCH,BLAZE,GHAST,BREEZE -> Family.CASTER;
            case SKELETON,STRAY,BOGGED,PILLAGER -> Family.ARCHER;
            case ARMADILLO,SNIFFER,IRON_GOLEM,SNOW_GOLEM,SLIME,MAGMA_CUBE -> Family.SHELL;
            case WARDEN,ENDERMAN,SHULKER,WITHER,WITHER_SKELETON,ENDER_DRAGON -> Family.VOID;
            default -> Family.BRUISER;
        };
    }

    static Family family(BossCatalog.Definition boss){
        var config=boss.custom().options().encounter();
        if(!config.has("family")||config.get("family").getAsString().equalsIgnoreCase("AUTO"))
            return family(boss.entityType());
        return Family.valueOf(config.get("family").getAsString().toUpperCase(java.util.Locale.ROOT));
    }

    static JsonObject defaults(){
        var json=new JsonObject();
        json.addProperty("enabled",true);
        json.addProperty("family","AUTO");
        json.addProperty("groupScaling",true);
        json.addProperty("weakpoints",true);
        json.addProperty("cooldownTicks",440);
        json.addProperty("chargeTicks",60);
        json.addProperty("vulnerabilityTicks",40);
        json.addProperty("vulnerabilityBonus",.15);
        json.addProperty("maximumPlayers",6);
        return json;
    }

    static Rules rules(JsonObject json){
        return new Rules(bool(json,"enabled",true),bool(json,"groupScaling",true),bool(json,"weakpoints",true),
            integer(json,"cooldownTicks",440,120,2400),integer(json,"chargeTicks",60,40,120),
            integer(json,"vulnerabilityTicks",40,10,100),number(json,"vulnerabilityBonus",.15,0,.5),
            integer(json,"maximumPlayers",6,1,12));
    }

    static double healthMultiplier(int players){return players<=1?1:players<=3?1.5:2.2;}
    static int targetCount(int players){return players<=1?1:players<=3?2:3;}

    static boolean shape(Family family,double x,double z,int step,double radius){
        return switch(family){
            case BRUISER -> x*x+z*z<=radius*radius&&(step==0?z>=0:z<=0);
            case HUNTER,SWARM,CASTER,AQUATIC -> x*x+z*z<=radius*radius;
            case ARCHER -> Math.abs(x)<1.15&&Math.abs(z)<=radius;
            case SHELL -> {
                double distance=Math.hypot(x,z);
                yield step==0?distance<=radius:distance>=radius*.45&&distance<=radius;
            }
            case VOID -> (Math.abs(x)<1||Math.abs(z)<1)&&x*x+z*z<=radius*radius;
        };
    }

    private static boolean bool(JsonObject json,String key,boolean fallback){
        var value=json.get(key);
        if(value==null)return fallback;
        if(!value.isJsonPrimitive()||!value.getAsJsonPrimitive().isBoolean())throw new IllegalArgumentException(key+" debe ser booleano");
        return value.getAsBoolean();
    }

    private static int integer(JsonObject json,String key,int fallback,int min,int max){
        double value=number(json,key,fallback,min,max);
        if(value!=Math.rint(value))throw new IllegalArgumentException(key+" debe ser entero");
        return (int)value;
    }

    private static double number(JsonObject json,String key,double fallback,double min,double max){
        if(!json.has(key))return fallback;
        var value=json.get(key);
        if(!value.isJsonPrimitive()||!value.getAsJsonPrimitive().isNumber())throw new IllegalArgumentException(key+" debe ser número");
        double n=value.getAsDouble();
        if(!Double.isFinite(n)||n<min||n>max)throw new IllegalArgumentException(key+" fuera de rango");
        return n;
    }
}
