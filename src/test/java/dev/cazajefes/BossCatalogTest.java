package dev.cazajefes;

import org.junit.Test;
import static org.junit.Assert.*;

public class BossCatalogTest {
    @Test public void retainsCatalogAndAliases() {
        assertEquals(168,BossCatalog.all().size());
        assertEquals(28,BossCatalog.all().stream().filter(BossCatalog.Definition::mini).count());
        assertEquals(BossCatalog.get("volcano").id(),BossCatalog.get("sniffer_tanque").id());
        assertEquals(BossCatalog.get("warden_abismal").id(),BossCatalog.get("dragon_grieta").id());
    }
    @Test public void idsAreUniqueAndNewNamesRetainIds() {
        assertEquals(168,BossCatalog.ids().stream().distinct().count());
        assertTrue(BossCatalog.get("coloso").name().startsWith("Gorath"));
        assertNull(BossCatalog.get("no_existe"));
        assertNull(BossCatalog.get(null));
    }
}
