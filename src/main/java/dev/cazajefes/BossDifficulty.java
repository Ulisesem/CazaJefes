package dev.cazajefes;

import com.google.gson.JsonObject;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

/** Deterministic rarity scaling; no random roll is needed for an ordinary hit. */
record BossDifficulty(double meleeDamageMultiplier,double specialDamageMultiplier) {
    private static final NamespacedKey TYPE=new NamespacedKey("cazajefes","boss_type");
    private static final double[] MELEE={1,1.06,1.12,1.18,1.25,1.33};
    private static final double[] SPECIAL={1,1.05,1.10,1.15,1.20,1.25};
    private static final int[] INTERVAL={12,12,11,10,10,10};

    static BossDifficulty of(BossCatalog.Definition boss) {
        int rank=rank(boss.tier(),boss.mini());
        var json=boss.custom()==null?new JsonObject():BossTuning.section(boss,"difficulty");
        return new BossDifficulty(BossTuning.number(json,"meleeDamageMultiplier",MELEE[rank],0,5),
            BossTuning.number(json,"specialDamageMultiplier",SPECIAL[rank],0,5));
    }

    static double multiplier(LivingEntity entity,boolean melee) {
        var boss=BossCatalog.get(entity.getPersistentDataContainer().get(TYPE,PersistentDataType.STRING));
        if(boss==null)return 1;
        var settings=of(boss);
        return melee?settings.meleeDamageMultiplier():settings.specialDamageMultiplier();
    }

    static JsonObject defaults(BossCatalog.Tier tier,boolean mini) {
        int rank=rank(tier,mini);
        var json=new JsonObject();
        json.addProperty("meleeDamageMultiplier",MELEE[rank]);
        json.addProperty("specialDamageMultiplier",SPECIAL[rank]);
        return json;
    }

    static JsonObject meleeDefaults(BossCatalog.Tier tier,boolean mini) {
        var json=new JsonObject();
        json.addProperty("intervalTicks",INTERVAL[rank(tier,mini)]);
        json.addProperty("warningTicks",1);
        json.addProperty("duringMobileSpecials",true);
        return json;
    }

    private static int rank(BossCatalog.Tier tier,boolean mini) {
        return mini?0:tier.ordinal();
    }
}
