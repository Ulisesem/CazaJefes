package dev.cazajefes;
import org.junit.Test;import static org.junit.Assert.*;
public class TerrainDeadlineTest {
 @Test public void restorationWaitsTwentyMinutesAfterDefeat(){long died=1000000L;long due=TerrainRestoration.deadline(died);assertEquals(died+1200000L,due);assertFalse(TerrainRestoration.due(due-1,due));assertTrue(TerrainRestoration.due(due,due));}
 @Test public void livingAndUnloadedBossesHaveNoDeadline(){assertFalse(TerrainRestoration.due(Long.MAX_VALUE,0));}
}
