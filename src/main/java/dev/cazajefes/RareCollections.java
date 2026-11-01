package dev.cazajefes;
import java.util.*;
/** Canonical craftable powers; no aliases or boss gear IDs are counted as separate essences. */
final class RareCollections {
 static List<String> ids(){LinkedHashSet<String> ids=new LinkedHashSet<>(RareDefinitions.ALL.keySet());ids.addAll(List.of("magneto","strength","turtle","garlic","saturacion","flame","guardian","brasas","paso_viento","aura_esporas","despertar"));return List.copyOf(ids);}
}
