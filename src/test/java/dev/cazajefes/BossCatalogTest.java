package dev.cazajefes;

import org.junit.Test;
import static org.junit.Assert.*;

public class BossCatalogTest {
    @Test public void retainsCatalogAndAliases() {
        assertEquals(168,BossCatalog.all().size());
        assertEquals(28,BossCatalog.all().stream().filter(BossCatalog.Definition::mini).count());
        assertEquals(BossCatalog.get("volkar_corazon_del_cataclismo").id(),BossCatalog.get("volkar_corazon_del_cataclismo").id());
        assertEquals(BossCatalog.get("nharzul_soberano_del_abismo").id(),BossCatalog.get("nharzul_soberano_del_abismo").id());
    }
    @Test public void idsAreUniqueAndNewNamesRetainIds() {
        assertEquals(168,BossCatalog.ids().stream().distinct().count());
        assertTrue(BossCatalog.get("gorath_quebrantador_de_mundos").name().startsWith("Gorath"));
        assertNull(BossCatalog.get("no_existe"));
        assertNull(BossCatalog.get(null));
    }
}
