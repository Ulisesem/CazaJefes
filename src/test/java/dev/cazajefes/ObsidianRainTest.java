package dev.cazajefes;

import org.junit.Test;
import static org.junit.Assert.*;

public class ObsidianRainTest {
   @Test public void eachPointInTheTwentyBlockSquareBelongsToExactlyOneWave() {
      for (int x=-10;x<10;x++) for (int z=-10;z<10;z++) {
         int hits=0;
         for (int pulse=0;pulse<4;pulse++) if (ObsidianRain.hits(x+.5,z+.5,pulse)) hits++;
         assertEquals(1,hits);
      }
      for (int pulse=0;pulse<4;pulse++) {
         assertFalse(ObsidianRain.hits(10,0,pulse));
         assertFalse(ObsidianRain.hits(0,-10.01,pulse));
         assertEquals(4,ObsidianRain.centers(pulse).size());
      }
   }

   @Test public void cornerIsInTheSquareEvenBeyondTheCircularRadius() {
      assertTrue(BossAbilityCatalog.hits(BossAbilityCatalog.Pattern.OBSIDIAN,9.9,9.9,2,4,10,0));
   }
}
