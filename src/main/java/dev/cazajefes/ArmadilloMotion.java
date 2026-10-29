package dev.cazajefes;
import org.bukkit.entity.Armadillo;
import org.bukkit.entity.memory.MemoryKey;
/** Do not reset UNROLLING every update or freeze its native state machine with NoAI. */
final class ArmadilloMotion {
 static boolean startUnroll(Armadillo.State state){return state!=Armadillo.State.IDLE&&state!=Armadillo.State.UNROLLING;}
 static boolean open(Armadillo entity){entity.setMemory(MemoryKey.DANGER_DETECTED_RECENTLY,null);var state=entity.getState();entity.setAI(state!=Armadillo.State.IDLE);if(startUnroll(state))entity.rollOut();return state==Armadillo.State.IDLE;}
}
