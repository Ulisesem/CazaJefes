package dev.cazajefes;
import org.bukkit.entity.Armadillo;
import org.bukkit.entity.memory.MemoryKey;
import java.lang.reflect.Method;
/** Keep physics enabled outside the scripted shell and suppress the native passive fear brain. */
final class ArmadilloMotion {
 private record Bridge(Method handle,Method brain,Method clear,Method state,Object idle){}
 private static final java.util.Map<Armadillo,Boolean> prepared=new java.util.WeakHashMap<>();
 private static final ClassValue<Bridge> bridges=new ClassValue<>(){protected Bridge computeValue(Class<?> type){try{Method handle=type.getMethod("getHandle");Class<?> nativeType=handle.getReturnType();Method brain=nativeType.getMethod("getBrain");Method clear=brain.getReturnType().getMethod("removeAllBehaviors");for(Method state:nativeType.getMethods())if(state.getName().equals("switchToState")&&state.getParameterCount()==1){Class<?> enumType=state.getParameterTypes()[0];for(Object value:enumType.getEnumConstants())if(((Enum<?>)value).name().equals("IDLE"))return new Bridge(handle,brain,clear,state,value);}return null;}catch(ReflectiveOperationException|RuntimeException unavailable){return null;}}};
 static boolean startUnroll(Armadillo.State state){return state!=Armadillo.State.IDLE&&state!=Armadillo.State.UNROLLING;}
 static boolean open(Armadillo entity){entity.setAI(true);entity.setMemory(MemoryKey.DANGER_DETECTED_RECENTLY,null);Bridge bridge=bridges.get(entity.getClass());if(bridge!=null)try{Object handle=bridge.handle().invoke(entity);if(!prepared.containsKey(entity)){bridge.clear().invoke(bridge.brain().invoke(handle));prepared.put(entity,true);}if(entity.getState()!=Armadillo.State.IDLE)bridge.state().invoke(handle,bridge.idle());return true;}catch(ReflectiveOperationException|RuntimeException unavailable){/* API fallback keeps native unrolling physics enabled. */}var state=entity.getState();if(startUnroll(state))entity.rollOut();return state==Armadillo.State.IDLE;}
}
