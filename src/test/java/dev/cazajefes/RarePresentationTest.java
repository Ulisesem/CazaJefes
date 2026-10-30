package dev.cazajefes;
import org.junit.Test;
import static org.junit.Assert.*;
public class RarePresentationTest {
 @Test public void legacyEssencesDescribeActualUse(){for(String id:new String[]{"guardian","brasas","paso_viento","saturacion","flame","magneto","despertar"}){String text=RareAppearance.description(id);assertFalse(id,text.startsWith("Consulta"));assertFalse(id,text.isBlank());}}
 @Test public void wrappedDescriptionHasOnePowerLabel(){var lines=RareAppearance.powerLines("hardy");assertTrue(lines.size()>1);assertTrue(lines.get(0).startsWith("Poder: "));assertEquals(1,lines.stream().filter(s->s.contains("Poder:")).count());for(String line:lines)assertTrue(line.length()<=44);}
 @Test public void longInventoryTitleFitsHeader(){assertTrue(PremiumStyle.compactTitle("CÁMARA DE INFUSIONES · ARMADURAS · 1/10").length()<30);assertEquals("FORJA · RECETA",PremiumStyle.compactTitle("FORJA · RECETA"));}
 @Test public void burstShellMatchesDimensionsOnEveryPoint(){for(var p:BurstVisuals.scaledSphere(2,4))assertEquals(1,p.x()*p.x()/4+p.y()*p.y()/16+p.z()*p.z()/4,.000001);}
}
