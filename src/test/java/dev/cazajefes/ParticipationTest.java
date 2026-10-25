package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
import java.lang.reflect.Proxy;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;
public class ParticipationTest {
 @Test public void participantsSurviveRoundTripAndIgnoreBadData(){UUID a=UUID.randomUUID(),b=UUID.randomUUID();var ids=BossParticipation.parse(a+",invalid,"+b+","+a);assertEquals(Set.of(a,b),ids);assertEquals(ids,BossParticipation.parse(String.join(",",ids.stream().map(UUID::toString).toList())));assertTrue(BossParticipation.parse(null).isEmpty());}
 @Test public void stageCannotResetWhenHealthIncreasesOrControllerRestarts(){Map<NamespacedKey,Object> state=new HashMap<>();PersistentDataContainer data=(PersistentDataContainer)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{PersistentDataContainer.class},(o,m,args)->switch(m.getName()){case "getOrDefault"->state.getOrDefault(args[0],args[2]);case "set"->{state.put((NamespacedKey)args[0],args[2]);yield null;}default->throw new UnsupportedOperationException(m.getName());});double[] health={60};LivingEntity entity=(LivingEntity)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{LivingEntity.class},(o,m,args)->switch(m.getName()){case "getPersistentDataContainer"->data;case "getHealth"->health[0];case "getMaxHealth"->100.0;default->throw new UnsupportedOperationException(m.getName());});assertEquals(2,BossStages.phase(entity));health[0]=20;assertEquals(3,BossStages.phase(entity));health[0]=100;assertEquals(3,BossStages.phase(entity));}
}
