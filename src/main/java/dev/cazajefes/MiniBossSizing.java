package dev.cazajefes;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
final class MiniBossSizing {
 private static final NamespacedKey KEY=new NamespacedKey("cazajefes","mini_size_revision");
 static double scaled(double value){return Math.max(.1,Math.min(10,Double.isFinite(value)?value*1.3:1.3));}
 static void apply(LivingEntity boss,BossCatalog.Definition definition){if(!definition.mini()||boss.getPersistentDataContainer().has(KEY,PersistentDataType.BYTE))return;var scale=boss.getAttribute(Attribute.SCALE);if(scale!=null){scale.setBaseValue(scaled(scale.getBaseValue()));boss.getPersistentDataContainer().set(KEY,PersistentDataType.BYTE,(byte)1);}}
}
