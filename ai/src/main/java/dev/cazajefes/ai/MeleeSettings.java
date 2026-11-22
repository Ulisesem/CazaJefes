package dev.cazajefes.ai;

import com.google.gson.JsonObject;

/** Basic attacks follow one shared clock; native player invulnerability is never reset. */
public record MeleeSettings(int intervalTicks,int warningTicks,boolean duringMobileSpecials) {
    public MeleeSettings {
        if(intervalTicks<10||intervalTicks>200||warningTicks<1||warningTicks>40)
            throw new IllegalArgumentException("Cadencia de melee fuera de rango");
    }
    public static MeleeSettings read(JsonObject ai) {
        var json=ai.has("melee")?ai.getAsJsonObject("melee"):new JsonObject();
        return new MeleeSettings(integer(json,"intervalTicks",10,10,200),
            integer(json,"warningTicks",1,1,40),
            !json.has("duringMobileSpecials")||json.get("duringMobileSpecials").getAsBoolean());
    }

    private static int integer(JsonObject json,String key,int fallback,int min,int max) {
        if(!json.has(key))return fallback;
        var field=json.get(key);
        if(!field.isJsonPrimitive()||!field.getAsJsonPrimitive().isNumber())
            throw new IllegalArgumentException("melee."+key+" requiere número");
        double value=field.getAsDouble();
        if(!Double.isFinite(value)||value!=Math.rint(value)||value<min||value>max)
            throw new IllegalArgumentException("melee."+key+" fuera de rango");
        return (int)value;
    }
}
