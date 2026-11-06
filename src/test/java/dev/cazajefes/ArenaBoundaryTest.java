package dev.cazajefes;

import org.junit.Test;
import java.util.Set;
import static org.junit.Assert.*;

public class ArenaBoundaryTest {
   @Test public void allowsUnmanagedCombatAndKeepsManagedCombatInsideItsArena() {
      assertTrue(ArenaBoundary.sharesArena(Set.of(),Set.of()));
      assertTrue(ArenaBoundary.sharesArena(Set.of("arena_a"),Set.of("arena_a","arena_b")));
      assertFalse(ArenaBoundary.sharesArena(Set.of("arena_a"),Set.of()));
      assertFalse(ArenaBoundary.sharesArena(Set.of(),Set.of("arena_a")));
      assertFalse(ArenaBoundary.sharesArena(Set.of("arena_a"),Set.of("arena_b")));
   }
}
