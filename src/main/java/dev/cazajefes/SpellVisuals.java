package dev.cazajefes;
import org.bukkit.*;
import org.bukkit.entity.*;
final class SpellVisuals {
 static boolean mage(EntityType type){return type==EntityType.EVOKER||type==EntityType.ILLUSIONER||type==EntityType.WITCH;}
 static String name(int phase,int slot){String[] names=phase==1?new String[]{"Hexágono de Ruina","Agujas del Éter","Abanico Maldito"}:phase==2?new String[]{"Sellos Encadenados","Eclosión Arcana","Vórtice de Ceniza"}:new String[]{"Cometas del Vacío"};return names[Math.floorMod(slot,names.length)];}
 static void charge(LivingEntity boss,BossCatalog.Definition d,int tick){double radius=Math.max(1,boss.getWidth()*.6);for(int i=0;i<12;i++){double angle=i*Math.PI/6+tick*.12;Location at=boss.getLocation().add(Math.cos(angle)*radius,.5+(i%3)*.6,Math.sin(angle)*radius);CosmeticFX.emit(mage(boss.getType())?Particle.ENCHANT:Particle.CRIT,at,1,0,0,0,0);}}
 static void release(LivingEntity boss,BossCatalog.Definition d,java.util.List<Location> marks){boss.swingMainHand();if(!mage(boss.getType()))return;boss.getWorld().playSound(boss.getLocation(),Sound.ENTITY_EVOKER_CAST_SPELL,1,.7f);int count=0;for(Location mark:marks){if(count++>=4)break;Location from=boss.getEyeLocation();var delta=mark.clone().add(0,1,0).toVector().subtract(from.toVector());for(int i=0;i<16;i++)CosmeticFX.emit(Particle.WITCH,from.clone().add(delta.clone().multiply(i/15.0)),1,0,0,0,0);}}
}
