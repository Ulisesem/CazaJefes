package dev.cazajefes;
/** Shared limits for equipped armor essences; strongest piece wins. */
final class ArmorRareRules {
 static int maximum(String id){var d=RareDefinitions.get(id);if(d!=null)return d.maximum();return switch(id){case "hardy","regeneration","strength","aura_esporas"->3;default->5;};}
 static double hearts(int level){return Math.max(0,Math.min(3,level))*2.0;}
 static int repair(int level){return 3*Math.max(1,Math.min(5,level));}
 static double counter(int level){return .1*Math.max(0,Math.min(5,level));}
 static String title(String id){return switch(id){case "hardy"->"Corazón Indómito";case "fly"->"Alas del Firmamento";case "aura_esporas"->"Jardín de la Ruina";case "durability"->"Forja Viviente";case "regeneration"->"Pulso de Aurora";case "strength"->"Furia del Titán";case "turtle"->"Baluarte de Obsidiana";case "garlic"->"Sello del Alba";default->null;};}
 static String description(String id){var d=RareDefinitions.get(id);if(d!=null)return d.description();return switch(id){case "hardy"->"+1 corazón máximo por nivel. Solo cuenta la pieza de mayor nivel. Máximo III: +3 corazones.";case "fly"->"Vuelo alimentado por saturación; cada nivel reduce su consumo. Máximo V.";case "aura_esporas"->"Aura de veneno I–III a enemigos jugadores a 4 bloques durante combate. Respeta aliados del marcador. Máximo III.";case "durability"->"Repara 3 puntos por nivel en cada pieza equipada cada 5 segundos. Máximo V: 15 puntos.";case "regeneration"->"Regeneración I–III mientras llevas armadura y tienes saturación. Solo cuenta la pieza más fuerte.";case "strength"->"Fuerza I–III mientras llevas armadura y tienes saturación. No se suma entre piezas.";case "turtle"->"10/20/30/40/50% de anular el daño adicional de Puñalada por la Espalda.";case "garlic"->"10/20/30/40/50% de impedir el robo de vida de las rares vampíricas.";default->null;};}
}
