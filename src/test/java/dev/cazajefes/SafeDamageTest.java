package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
import java.lang.reflect.Proxy;
import com.google.common.base.Function;
import org.bukkit.entity.LivingEntity;
import org.bukkit.damage.DamageSource;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
@SuppressWarnings("deprecation")
public class SafeDamageTest {
 private EntityDamageEvent event(double base,double armor,double magic,double absorption,int[] callbacks){
  LivingEntity victim=(LivingEntity)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{LivingEntity.class},(o,m,args)->{if(m.getName().equals("getAbsorptionAmount"))return Math.max(0,-absorption);throw new UnsupportedOperationException(m.getName());});
  DamageSource source=(DamageSource)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{DamageSource.class},(o,m,args)->null);
  Map<DamageModifier,Double> values=new EnumMap<>(DamageModifier.class);values.put(DamageModifier.BASE,base);values.put(DamageModifier.INVULNERABILITY_REDUCTION,0D);values.put(DamageModifier.ARMOR,armor);values.put(DamageModifier.MAGIC,magic);values.put(DamageModifier.ABSORPTION,absorption);
  Map<DamageModifier,Function<? super Double,Double>> functions=new EnumMap<>(DamageModifier.class);for(var entry:values.entrySet()){double value=entry.getValue();functions.put(entry.getKey(),d->{callbacks[0]++;return value;});}
  return new EntityDamageEvent(victim,EntityDamageEvent.DamageCause.ENTITY_ATTACK,source,values,functions);
 }
 @Test public void reproducesLoggedNegativeAndStableScalingPreventsIt(){int[] calls={0};double oldBase=1067.15,newBase=875.063,magic=-805.2847241210938;var old=event(oldBase,-100,magic,0,calls);assertTrue(old.getFinalDamage()>0);old.setDamage(newBase);assertEquals(-30.221724121093757,old.getFinalDamage(),.000001);assertTrue(calls[0]>0);calls[0]=0;var fixed=event(oldBase,-100,magic,0,calls);double before=fixed.getFinalDamage();SafeDamage.setBase(fixed,newBase);assertEquals(before*.82,fixed.getFinalDamage(),.000001);assertTrue(fixed.getFinalDamage()>0);assertEquals(0,calls[0]);double health=982.948974609375;assertTrue(health-fixed.getFinalDamage()<health);}
 @Test public void corruptSnapshotsNeverHealAcrossStrengthAndPhaseRanges(){Random random=new Random(175);for(int i=0;i<1000;i++){double base=random.nextDouble()*5000;var event=event(base,-random.nextDouble()*base*2,-random.nextDouble()*base*2,0,new int[]{0});SafeDamage.scale(event,PhaseRules.incoming(1+i%3));assertTrue(Double.isFinite(event.getFinalDamage()));assertTrue(event.getFinalDamage()>=0);}}
 @Test public void strength100WithArmorAndProtectionRetainsPositiveDamage(){for(int phase=1;phase<=3;phase++)for(double base:new double[]{1,10,315,875.063,1067.15,5000}){var event=event(base,-base*.176,-base*.824*.8,0,new int[]{0});double before=event.getFinalDamage();SafeDamage.scale(event,PhaseRules.incoming(phase));assertEquals(before*PhaseRules.incoming(phase),event.getFinalDamage(),.000001);assertTrue(event.getFinalDamage()>0);}}
 @Test public void tinyNegativeOnZeroBaseBecomesZero(){var event=event(0,0,-1.7881393432617188E-7,0,new int[]{0});SafeDamage.normalize(event);assertEquals(0,event.getFinalDamage(),0);}
 @Test public void cancellationIsNeverUndoneOrMutated(){var event=event(875.063,-100,-805.2847241210938,0,new int[]{0});event.setCancelled(true);double before=event.getFinalDamage();SafeDamage.setBase(event,1);SafeDamage.floor(event);assertTrue(event.isCancelled());assertEquals(before,event.getFinalDamage(),0);}
 @Test public void shieldsAndCooldownAreNotBypassed(){var shield=event(10,0,0,0,new int[]{0});shield.setDamage(DamageModifier.INVULNERABILITY_REDUCTION,-10);SafeDamage.floor(shield);assertEquals(0,shield.getFinalDamage(),0);var blocked=event(10,-10,0,0,new int[]{0});SafeDamage.scale(blocked,2);assertEquals(0,blocked.getFinalDamage(),0);}
 @Test public void absorptionCannotConsumeMoreThanAvailable(){var event=event(10,0,0,-8,new int[]{0});SafeDamage.scale(event,2);assertEquals(-8,event.getDamage(DamageModifier.ABSORPTION),0);assertEquals(12,event.getFinalDamage(),0);}
 @Test public void floorAdjustsSnapshotWithoutInvokingCallbacks(){int[] calls={0};var event=event(10,-8,-1.8,0,calls);SafeDamage.floor(event);assertEquals(1,event.getFinalDamage(),.000001);assertEquals(0,calls[0]);}
 @Test public void finalBlastIgnoresNotchAbsorptionButHonorsCancellation(){var e=event(7,-3,-3,0,new int[]{0});BossDeathVisuals.fixedBlastDamage(e,6.5);assertEquals(6.5,e.getFinalDamage(),0);var shield=event(7,-3,-3,-4,new int[]{0});BossDeathVisuals.fixedBlastDamage(shield,6.5);assertEquals(6.5,shield.getFinalDamage(),0);var cancelled=event(7,-3,-3,0,new int[]{0});cancelled.setCancelled(true);BossDeathVisuals.fixedBlastDamage(cancelled,6.5);assertEquals(1,cancelled.getFinalDamage(),0);}
}
