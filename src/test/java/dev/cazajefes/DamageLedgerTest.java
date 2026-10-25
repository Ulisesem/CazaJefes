package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
public class DamageLedgerTest {
 @Test public void overkillAndInvalidDamageAreNotCredited(){assertEquals(4,DamageLedger.accepted(100,4),0);assertEquals(0,DamageLedger.accepted(-1,50),0);assertEquals(0,DamageLedger.accepted(Double.NaN,50),0);assertEquals(7.5,DamageLedger.accepted(7.5,50),0);}
 @Test public void damageAndPlayerIdentitySurvivePersistence(){UUID first=UUID.randomUUID(),second=UUID.randomUUID();Map<UUID,DamageLedger.Entry> data=new HashMap<>();data.put(first,new DamageLedger.Entry(first,"Cazador_1",21.5));data.put(second,new DamageLedger.Entry(second,"Cazador_2",44));assertEquals(data,DamageLedger.parse(DamageLedger.encode(data)));assertEquals(second,DamageLedger.sorted(data).get(0).id());}
 @Test public void malformedAndNonFiniteRowsDoNotEnterScoreboard(){UUID id=UUID.randomUUID();assertTrue(DamageLedger.parse("broken;"+id+":Test:NaN;"+id+":Test:Infinity;"+id+":Test:-20").isEmpty());assertTrue(DamageLedger.parse(null).isEmpty());}
}
