package dev.cazajefes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.configuration.MemoryConfiguration;

record BossOptions(JsonObject json) {
    static final BossOptions DEFAULT=new BossOptions(new JsonObject());
    private static final Set<String> PLAN_KEYS=Set.of("phase-2-health","phase-3-health","phase-1","phase-2","phase-3","horde");

    boolean fearless(){return bool("fearless",true);}
    boolean loot(){return bool("loot",true);}
    boolean caveBreach(){return bool("caveBreach",true);}
    boolean aura(){return bool("aura",true);}
    long attackInterval(long fallback){return (long)number("attackIntervalTicks",fallback,5,40);}
    double speed(double fallback){return number("speed",fallback,.05,1);}
    int warningTicks(){return (int)Math.round(number("warningTicks",25,20,120)/5)*5;}
    int recoveryTicks(){return (int)Math.round(number("recoveryTicks",20,5,120)/5)*5;}
    int rocketCharges(int fallback){return (int)number("rocketCharges",PillagerRules.rocketCharges(fallback),1,9);}
    double heartChance(double fallback){return number("heartChance",fallback,0,1);}

    CombatScripts.Plan plan(CombatScripts.Plan fallback, Map<String,CombatScripts.Skill> shared){
        boolean override=json.keySet().stream().anyMatch(PLAN_KEYS::contains);
        if(!override)return fallback;
        var skills=new HashMap<>(shared);
        if(json.has("skills")){
            for(var entry:json.getAsJsonObject("skills").entrySet()){
                var fields=new MemoryConfiguration();
                for(var parameter:entry.getValue().getAsJsonObject().entrySet()){
                    fields.set(parameter.getKey(),value(parameter.getValue()));
                }
                skills.put(entry.getKey(),CombatScripts.readSkill(entry.getKey(),fields));
            }
        }
        var fields=new MemoryConfiguration();
        fields.set("phase-2-health",fallback.phase2()*100);
        fields.set("phase-3-health",fallback.phase3()*100);
        for(var key:PLAN_KEYS)if(json.has(key))fields.set(key,value(json.get(key)));
        var edited=CombatScripts.readPlan(fields,skills);
        var phases=new HashMap<>(fallback.phases());
        phases.putAll(edited.phases());
        return new CombatScripts.Plan(edited.phase2(),edited.phase3(),Map.copyOf(phases),
            json.has("horde")?edited.horde():fallback.horde());
    }

    private static Object value(JsonElement value){
        if(value.isJsonArray())return value.getAsJsonArray().asList().stream().map(BossOptions::value).toList();
        if(!value.isJsonPrimitive())throw new IllegalArgumentException("se esperaba un número, texto, booleano o lista");
        var primitive=value.getAsJsonPrimitive();
        if(primitive.isBoolean())return primitive.getAsBoolean();
        if(primitive.isNumber())return primitive.getAsDouble();
        return primitive.getAsString();
    }

    private boolean bool(String key,boolean fallback){
        var field=json.get(key);
        return field!=null&&field.isJsonPrimitive()&&field.getAsJsonPrimitive().isBoolean()?field.getAsBoolean():fallback;
    }

    private double number(String key,double fallback,double min,double max){
        var field=json.get(key);
        if(field==null||!field.isJsonPrimitive()||!field.getAsJsonPrimitive().isNumber())return fallback;
        double value=field.getAsDouble();
        return Double.isFinite(value)?Math.clamp(value,min,max):fallback;
    }
}
