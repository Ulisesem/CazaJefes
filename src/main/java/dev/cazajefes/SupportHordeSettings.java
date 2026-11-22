package dev.cazajefes;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

/** A crossbow squad combines covering fire and melee guards instead of tier-random creatures. */
record SupportHordeSettings(boolean enabled, int count, int cooldownTicks, int firstDelayTicks,
                            int lifetimeTicks, int minimumPhase) {
    static SupportHordeSettings of(BossCatalog.Definition boss) {
        return read(boss.custom()==null?new JsonObject():BossTuning.section(boss,"crossbowSupport"));
    }

    static SupportHordeSettings read(JsonObject json) {
        return new SupportHordeSettings(BossTuning.flag(json,"enabled",true),
            (int)BossTuning.number(json,"count",6,1,HordeRules.PER_BOSS),
            (int)BossTuning.number(json,"cooldownTicks",500,100,72000),
            (int)BossTuning.number(json,"firstDelayTicks",100,0,72000),
            (int)BossTuning.number(json,"lifetimeTicks",800,100,72000),
            (int)BossTuning.number(json,"minimumPhase",1,1,3));
    }

    static boolean matches(LivingEntity body,BossCatalog.Definition boss) {
        return boss.entityType()==EntityType.PILLAGER || body.getEquipment()!=null
            && body.getEquipment().getItemInMainHand().getType()==Material.CROSSBOW;
    }

    static boolean room(long existing,long global,int requested) {
        return requested>0 && existing>=0 && global>=0
            && existing+requested<=HordeRules.PER_BOSS && global+requested<=HordeRules.GLOBAL;
    }

    static List<CombatScripts.Minion> squad(int count) {
        var squad=new ArrayList<CombatScripts.Minion>(count);
        for(int index=0;index<count;index++) squad.add(index%3==2
            ? new CombatScripts.Minion(EntityType.VINDICATOR,CombatScripts.Role.GUARD)
            : new CombatScripts.Minion(EntityType.PILLAGER,CombatScripts.Role.ARCHER));
        return List.copyOf(squad);
    }

    static JsonObject defaults() {
        var json=new JsonObject();
        json.addProperty("enabled",true);
        json.addProperty("count",6);
        json.addProperty("cooldownTicks",500);
        json.addProperty("firstDelayTicks",100);
        json.addProperty("lifetimeTicks",800);
        json.addProperty("minimumPhase",1);
        return json;
    }
}
