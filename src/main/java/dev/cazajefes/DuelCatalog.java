package dev.cazajefes;
import java.util.*;
import org.bukkit.Material;
final class DuelCatalog {
 record Relic(String id,String name,Material gear,String material,Material icon,Material reagent,String power,long cooldown,boolean armor){}
 static final List<Relic> ALL=List.of(
 new Relic("hemorragia","Juramento Carmesí",Material.NETHERITE_SWORD,"Sangre Cristalizada",Material.REDSTONE,Material.FERMENTED_SPIDER_EYE,"Tercer golpe cargado: Wither I durante 2 s",12000,false),
 new Relic("fractura","Desgarro del Bastión",Material.NETHERITE_AXE,"Fragmento del Bastión",Material.PRISMARINE_CRYSTALS,Material.IRON_BLOCK,"Golpe cargado: +20 % daño y Debilidad I · 2 s",10000,false),
 new Relic("ocaso","Arco del Ocaso",Material.BOW,"Lente del Ocaso",Material.ECHO_SHARD,Material.ENDER_EYE,"Disparo a 12 m: ceguera 1 s y brillo 3 s",14000,false),
 new Relic("resonancia","Voz de la Tormenta",Material.CROSSBOW,"Resonador de Tormenta",Material.AMETHYST_SHARD,Material.LIGHTNING_ROD,"Disparo: +15 % daño y Lentitud I · 2 s",12000,false),
 new Relic("vigia","Vigía de Ónix",Material.NETHERITE_HELMET,"Ojo de Ónix",Material.ENDER_EYE,Material.OBSIDIAN,"Recibe una flecha: -20 % daño y revela al tirador",12000,true),
 new Relic("quitina","Coraza de Quitina",Material.NETHERITE_CHESTPLATE,"Quitina Resonante",Material.PRISMARINE_SHARD,Material.SPIDER_EYE,"Recibe cuerpo a cuerpo: -20 % daño y Resistencia I · 2 s",14000,true),
 new Relic("cadenas","Grebas Rompecadenas",Material.NETHERITE_LEGGINGS,"Fibra Rompecadenas",Material.PHANTOM_MEMBRANE,Material.COBWEB,"Si recibes daño con lentitud: la limpia y da Velocidad I · 2 s",14000,true),
 new Relic("espectral","Paso Espectral",Material.NETHERITE_BOOTS,"Pluma Espectral",Material.FEATHER,Material.RABBIT_FOOT,"Recibe daño con ≤40 % vida: Velocidad II · 3 s",18000,true));
 static Relic get(String id){return ALL.stream().filter(r->r.id().equals(id)).findFirst().orElse(null);}
 static double multiplier(String id){return id.equals("fractura")?1.2:id.equals("resonancia")?1.15:id.equals("vigia")||id.equals("quitina")?.8:1;}
 static boolean validHit(boolean cancelled,double damage){return !cancelled&&Double.isFinite(damage)&&damage>0;}
 static boolean ready(long now,long next){return now>=next;}
 static boolean condition(String id,boolean projectile,boolean charged,double rangeSquared,boolean slowed,double healthRatio){return switch(id){case "hemorragia","fractura"->!projectile&&charged;case "ocaso"->projectile&&rangeSquared>=144;case "resonancia","vigia"->projectile;case "quitina"->!projectile;case "cadenas"->slowed;case "espectral"->healthRatio<=.4;default->false;};}
 static final class Streak {UUID target;int count;long until;boolean third(UUID victim,long now){return victim.equals(target)&&now<=until&&count>=2;}void accepted(UUID victim,long now){if(!victim.equals(target)||now>until)count=0;target=victim;until=now+6000;count=Math.min(3,count+1);}void consume(){count=0;}}
}
