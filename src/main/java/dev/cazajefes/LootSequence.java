package dev.cazajefes;
import java.util.*;
/** FIFO of rewards not yet spawned; drained on shutdown without repeating issued entries. */
final class LootSequence<T> {
 private final ArrayDeque<T> pending;
 LootSequence(Collection<T> rewards){pending=new ArrayDeque<>(rewards);}
 T next(){return pending.pollFirst();}
 boolean empty(){return pending.isEmpty();}
 List<T> drain(){var result=new ArrayList<T>(pending);pending.clear();return result;}
}
