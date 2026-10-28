package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
import java.lang.reflect.Proxy;
import org.bukkit.entity.*;
import org.bukkit.damage.DamageSource;
import org.bukkit.event.entity.*;
public class BossStatisticsTest {
 private Object proxy(Class<?> type,java.util.function.BiFunction<String,Object[],Object> body){return Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{type},(o,m,a)->body.apply(m.getName(),a));}
 @Test public void onlyFinalDamageSourceWinsAndProjectileUsesShooter(){Player winner=(Player)proxy(Player.class,(n,a)->null);LivingEntity boss=(LivingEntity)proxy(LivingEntity.class,(n,a)->null);DamageSource source=(DamageSource)proxy(DamageSource.class,(n,a)->null);var hit=new EntityDamageByEntityEvent(winner,boss,EntityDamageEvent.DamageCause.ENTITY_ATTACK,source,5);assertSame(winner,BossStatistics.finalPlayer(hit));hit.setCancelled(true);assertNull(BossStatistics.finalPlayer(hit));var environmental=new EntityDamageEvent(boss,EntityDamageEvent.DamageCause.LAVA,source,5);assertNull(BossStatistics.finalPlayer(environmental));Arrow arrow=(Arrow)proxy(Arrow.class,(n,a)->n.equals("getShooter")?winner:null);var shot=new EntityDamageByEntityEvent(arrow,boss,EntityDamageEvent.DamageCause.PROJECTILE,source,5);assertSame(winner,BossStatistics.finalPlayer(shot));}
 @Test public void countersSurviveJsonRoundTripAndKeepCategoryAndBossTotals(){UUID id=UUID.randomUUID();var first=BossStatistics.increment(null,id,"Cazador","boss_uno","MINI",1);var second=BossStatistics.increment(first,id,"NuevoNombre","boss_uno","MINI",2);var third=BossStatistics.increment(second,id,"NuevoNombre","boss_dos","ABYSSAL",3);assertEquals(3,third.total());assertEquals(Long.valueOf(2),third.bosses().get("boss_uno"));assertEquals(Long.valueOf(1),third.categories().get("ABYSSAL"));var gson=new com.google.gson.Gson();var state=new BossStatistics.Snapshot(1,List.of(third),List.of(UUID.randomUUID()));var restored=gson.fromJson(gson.toJson(state),BossStatistics.Snapshot.class);assertEquals(third,restored.players().get(0));assertEquals(id,restored.players().get(0).uuid());}
 @Test public void miniBossesGrowThirtyPercentWithinSafeScaleLimit(){assertEquals(2.6,MiniBossSizing.scaled(2),.000001);assertEquals(10,MiniBossSizing.scaled(10),0);assertEquals(1.3,MiniBossSizing.scaled(Double.NaN),0);}
}
