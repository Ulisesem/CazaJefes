package dev.cazajefes;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
final class AnimalBossSizing {
 private static final NamespacedKey KEY=new NamespacedKey("cazajefes","animal_size_revision");
 static double scaled(double base,boolean applied){if(applied)return base;return Math.max(.1,Math.min(10,Double.isFinite(base)?base*1.25:1.75));}
 static void apply(LivingEntity boss,BossCatalog.Definition definition){if(!AnimalMinions.animal(definition.entityType()))return;var scale=boss.getAttribute(Attribute.SCALE);if(scale==null)return;boolean done=boss.getPersistentDataContainer().getOrDefault(KEY,PersistentDataType.INTEGER,0)>=1;if(done)return;scale.setBaseValue(scaled(scale.getBaseValue(),false));boss.getPersistentDataContainer().set(KEY,PersistentDataType.INTEGER,1);}
}
