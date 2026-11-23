package dev.cazajefes.ai;

import com.google.gson.JsonObject;

/** Basic attacks follow one shared clock; native player invulnerability is never reset. */
public record MeleeSettings(int intervalTicks,int warningTicks,boolean duringMobileSpecials,int comboHits,int comboIntervalTicks,int comboWindowTicks,double comboBonus) {
    public MeleeSettings(int intervalTicks,int warningTicks,boolean pressure){this(intervalTicks,warningTicks,pressure,1,10,60,0);}
    public MeleeSettings {
        if(comboHits<1||comboHits>5||comboIntervalTicks<10||comboIntervalTicks>200||comboWindowTicks<20||comboWindowTicks>400||!Double.isFinite(comboBonus)||comboBonus<0||comboBonus>1)throw new IllegalArgumentException("Combo fuera de rango");
        if(intervalTicks<10||intervalTicks>200||warningTicks<1||warningTicks>40)
            throw new IllegalArgumentException("Cadencia de melee fuera de rango");
    }
    public static MeleeSettings read(JsonObject ai) {
        var json=ai.has("melee")?ai.getAsJsonObject("melee"):new JsonObject();
        return new MeleeSettings(integer(json,"intervalTicks",10,10,200),
            integer(json,"warningTicks",1,1,40),
            !json.has("duringMobileSpecials")||json.get("duringMobileSpecials").getAsBoolean(),
            integer(json,"comboHits",3,1,5),integer(json,"comboIntervalTicks",10,10,200),integer(json,"comboWindowTicks",60,20,400),
            json.has("comboBonus")?json.get("comboBonus").getAsDouble():.12);
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
