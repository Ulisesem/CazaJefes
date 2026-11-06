package dev.cazajefes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.cazajefes.ai.BossProfile;

final class BossFamilyAttacks {
    private BossFamilyAttacks(){}

    static JsonObject withDefaults(BossCatalog.Definition boss,boolean aquatic){
        var original=boss.custom().ai();
        var config=original==null?BossProfile.defaults(boss.custom().damage(),aquatic):original.deepCopy();
        if(!boss.custom().options().legacyAttacks())return config;
        var attacks=config.has("attacks")?config.getAsJsonArray("attacks")
            :BossProfile.defaults(boss.custom().damage(),aquatic).getAsJsonArray("attacks");
        if(attacks.isEmpty())return config;
        double damage=boss.custom().damage();
        switch(boss.entityType()){
            case PILLAGER -> {
                add(attacks,volley("andanada_del_saqueador","Andanada del Saqueador","ARROW",6,5,6,damage*.65,0,true));
                add(attacks,volley("bateria_de_cohetes","Batería de Cohetes","FIREWORK",6,1,12,damage*.85,3.5,true));
            }
            case SHULKER -> add(attacks,volley("orbita_del_umbral","Órbita del Umbral","SHULKER",5,4,5,8,0,true));
            case SKELETON, STRAY, BOGGED -> add(attacks,volley("triada_del_eclipse","Tríada del Eclipse","ARROW",1,3,5,damage,0,true));
            case EVOKER, ILLUSIONER, WITCH -> {
                add(attacks,volley("runas_de_entropia","Runas de Entropía","SPELL",3,1,9,damage,0,false));
                if(boss.entityType()==org.bukkit.entity.EntityType.EVOKER){
                    var storm=spec("juicio_de_las_seis_tormentas","Juicio de las Seis Tormentas","LIGHTNING_CHAIN",6);
                    storm.addProperty("radius",24);
                    storm.addProperty("minimumPhase",3);
                    add(attacks,storm);
                }
            }
            default -> {}
        }
        config.add("attacks",attacks);
        return config;
    }

    private static void add(JsonArray attacks,JsonObject spec){
        if(attacks.size()<24&&attacks.asList().stream().noneMatch(row->spec.get("id").equals(row.getAsJsonObject().get("id"))))
            attacks.add(spec);
    }

    private static JsonObject volley(String id,String name,String kind,int salvos,int shots,int interval,double damage,double area,boolean guided){
        var row=spec(id,name,"FAMILY_VOLLEY",damage);
        row.addProperty("projectile",kind);
        row.addProperty("salvos",salvos);
        row.addProperty("shots",shots);
        row.addProperty("intervalTicks",interval);
        row.addProperty("speed",kind.equals("SHULKER")?.75:1.8);
        row.addProperty("area",area);
        row.addProperty("guided",guided);
        return row;
    }

    private static JsonObject spec(String id,String name,String type,double damage){
        var row=new JsonObject();
        row.addProperty("id",id);
        row.addProperty("name",name);
        row.addProperty("type",type);
        row.addProperty("category","MIXED");
        row.addProperty("radius",.42);
        row.addProperty("weight",4);
        row.addProperty("damage",Math.min(500,damage));
        row.addProperty("cooldownTicks",260);
        row.addProperty("warningTicks",32);
        return row;
    }
}
