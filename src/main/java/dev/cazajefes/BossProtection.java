package dev.cazajefes;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
final class BossProtection {
 static final NamespacedKey KEY=new NamespacedKey("cazajefes","boss_protection");
 static int level(BossCatalog.Definition boss){return boss.mini()?5:(boss.tier().ordinal()+2)*5;}
 static double incoming(int level){return 1-Math.max(5,Math.min(35,level))*.01;}
 static double incoming(LivingEntity boss){return incoming(boss.getPersistentDataContainer().getOrDefault(KEY,PersistentDataType.INTEGER,5));}
}
