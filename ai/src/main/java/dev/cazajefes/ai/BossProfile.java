package dev.cazajefes.ai;

import com.google.gson.JsonObject;
import dev.cazajefes.ai.attack.LinearProjectileAttack;
import dev.cazajefes.ai.attack.MeteoriteAoEAttack;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public record BossProfile(double speed,double gravity,double detectionRange,int globalCooldown,
                          boolean aquatic,List<BossAttack> attacks) {
    public BossProfile {
        attacks=List.copyOf(attacks);
    }

    public static BossProfile read(JsonObject json,double baseDamage,boolean aquatic) {
        if(json==null)json=defaults(baseDamage,aquatic);
        double speed=number(json,"speed",.29,.02,1);
        double gravity=number(json,"gravity",.08,0,.35);
        double range=number(json,"detectionRange",32,3,48);
        int gcd=integer(json,"globalCooldownTicks",30,1,600);
        var entries=new ArrayList<BossAttack>();
        {
            var attacks=json.has("attacks")?json.getAsJsonArray("attacks"):defaults(baseDamage,aquatic).getAsJsonArray("attacks");
            if(attacks.size()>24)throw new IllegalArgumentException("máximo 24 ataques");
            var ids=new HashSet<String>();
            for(var element:attacks){
                var row=element.getAsJsonObject();
                String id=text(row,"id","");
                if(!ids.add(id))throw new IllegalArgumentException("ataque duplicado: "+id);
                String name=text(row,"name",id);
                var category=AttackCategory.valueOf(text(row,"category","MIXED"));
                int weight=integer(row,"weight",1,0,10000);
                long cooldown=integer(row,"cooldownTicks",120,1,72000);
                int warn=integer(row,"warningTicks",25,1,200);
                double radius=number(row,"radius",2,0,24);
                double damage=number(row,"damage",Math.min(baseDamage,500),0,500);
                entries.add(switch(text(row,"type","")){
                    case "LINEAR_PROJECTILE" -> new LinearProjectileAttack(id,name,category,weight,cooldown,
                        radius,damage,warn,number(row,"speed",1.4,.1,4),integer(row,"lifetimeTicks",30,1,100));
                    case "METEORITE_AOE" -> new MeteoriteAoEAttack(id,name,category,weight,cooldown,radius,damage,warn,
                        number(row,"height",10,2,24),number(row,"speed",.85,.1,3),
                        text(row,"material","MAGMA_BLOCK"),text(row,"texture",""));
                    default -> throw new IllegalArgumentException("tipo de ataque desconocido: "+id);
                });
            }
        }
        return new BossProfile(speed,gravity,range,gcd,json.has("aquatic")?json.get("aquatic").getAsBoolean():aquatic,entries);
    }

    public static JsonObject defaults(double baseDamage,boolean aquatic){
        var json=new JsonObject();
        json.addProperty("speed",aquatic?.32:.29);
        json.addProperty("gravity",.08);
        json.addProperty("detectionRange",32);
        json.addProperty("globalCooldownTicks",30);
        json.addProperty("aquatic",aquatic);
        json.add("equipment",new JsonObject());
        var attacks=new com.google.gson.JsonArray();
        var shot=defaultAttack("descarga","Descarga de la Grieta","LINEAR_PROJECTILE","MIXED",4,105,25,1.1,baseDamage*.65);
        shot.addProperty("speed",1.65);
        shot.addProperty("lifetimeTicks",30);
        attacks.add(shot);
        var meteor=defaultAttack("meteoro","Meteorito de la Grieta","METEORITE_AOE","MID_RANGE",2,200,34,4,baseDamage*1.25);
        meteor.addProperty("height",10);
        meteor.addProperty("speed",.85);
        meteor.addProperty("material","MAGMA_BLOCK");
        attacks.add(meteor);
        var far=defaultAttack("meteoro_distante","Lluvia Incandescente","METEORITE_AOE","LONG_RANGE",2,220,40,4.5,baseDamage*1.35);
        far.addProperty("height",12);
        far.addProperty("speed",1);
        far.addProperty("material","MAGMA_BLOCK");
        attacks.add(far);
        json.add("attacks",attacks);
        return json;
    }

    private static JsonObject defaultAttack(String id,String name,String type,String category,
        int weight,int cooldown,int warning,double radius,double damage){
        var row=new JsonObject();
        row.addProperty("id",id);
        row.addProperty("name",name);
        row.addProperty("type",type);
        row.addProperty("category",category);
        row.addProperty("weight",weight);
        row.addProperty("cooldownTicks",cooldown);
        row.addProperty("warningTicks",warning);
        row.addProperty("radius",radius);
        row.addProperty("damage",Math.clamp(damage,0,500));
        return row;
    }

    private static int integer(JsonObject json,String key,int fallback,int min,int max){
        double n=number(json,key,fallback,min,max);
        if(n!=Math.rint(n))throw new IllegalArgumentException(key+" requiere entero");
        return (int)n;
    }

    private static double number(JsonObject json,String key,double fallback,double min,double max){
        if(!json.has(key))return fallback;
        var value=json.get(key);
        if(!value.isJsonPrimitive()||!value.getAsJsonPrimitive().isNumber())throw new IllegalArgumentException(key+" requiere número");
        double n=value.getAsDouble();
        if(!Double.isFinite(n)||n<min||n>max)throw new IllegalArgumentException(key+" fuera de rango");
        return n;
    }

    private static String text(JsonObject json,String key,String fallback){
        return json.has(key)?json.get(key).getAsString():fallback;
    }
}
