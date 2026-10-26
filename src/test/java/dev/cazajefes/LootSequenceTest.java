package dev.cazajefes;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;
public class LootSequenceTest {
 @Test public void rewardsIssueOneAtATimeInOrder(){var queue=new LootSequence<>(List.of("arma","cristal","tributo"));assertEquals("arma",queue.next());assertFalse(queue.empty());assertEquals("cristal",queue.next());assertEquals("tributo",queue.next());assertTrue(queue.empty());assertNull(queue.next());}
 @Test public void shutdownReleasesOnlyUnspawnedRewards(){var queue=new LootSequence<>(List.of(1,2,3));assertEquals(Integer.valueOf(1),queue.next());assertEquals(List.of(2,3),queue.drain());assertTrue(queue.drain().isEmpty());assertNull(queue.next());}
}
