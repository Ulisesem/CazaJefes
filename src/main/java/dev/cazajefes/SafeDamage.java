package dev.cazajefes;
import java.util.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
/** Edits the current mitigation snapshot without invoking modifier callbacks repeatedly. */
@SuppressWarnings("deprecation")
final class SafeDamage {
 static void setBase(EntityDamageEvent event,double desired){if(event.isCancelled())return;double base=event.getDamage();if(!Double.isFinite(base)||base<=0||!Double.isFinite(desired)||desired<0){zero(event);return;}scale(event,desired/base);}
 static void scale(EntityDamageEvent event,double factor){if(event.isCancelled())return;if(!Double.isFinite(factor)||factor<0){zero(event);return;}Map<DamageModifier,Double> values=new EnumMap<>(DamageModifier.class);for(var modifier:DamageModifier.values())if(event.isApplicable(modifier))values.put(modifier,event.getDamage(modifier)*factor);for(var entry:values.entrySet())event.setDamage(entry.getKey(),entry.getValue());normalize(event);}
 static void zero(EntityDamageEvent event){if(event.isCancelled())return;for(var modifier:DamageModifier.values())if(event.isApplicable(modifier))event.setDamage(modifier,0);}
 static void normalize(EntityDamageEvent event){
  if(event.isCancelled())return;double base=event.getDamage();if(!Double.isFinite(base)||base<=0){zero(event);return;}
  double remaining=base;for(var modifier:DamageModifier.values()){
   if(modifier==DamageModifier.BASE||!event.isApplicable(modifier))continue;double value=event.getDamage(modifier);if(!Double.isFinite(value))value=0;
   if(value<0){double limit=remaining;if(modifier==DamageModifier.ABSORPTION&&event.getEntity() instanceof LivingEntity living)limit=Math.min(limit,Math.max(0,living.getAbsorptionAmount()));value=-Math.min(-value,Math.max(0,limit));}
   if(!Double.isFinite(remaining+value))value=0;event.setDamage(modifier,value);remaining+=value;
  }
  double result=event.getFinalDamage();if(result<0&&Double.isFinite(result))event.setDamage(DamageModifier.BASE,base-result+Math.ulp(base));else if(!Double.isFinite(result))zero(event);
 }
 static void floor(EntityDamageEvent event){if(event.isCancelled())return;normalize(event);double result=event.getFinalDamage(),desired=DamageProgress.floor(event.getDamage(),result);if(result>0&&desired>result)event.setDamage(DamageModifier.BASE,event.getDamage()+desired-result);normalize(event);}
}
