package dev.cazajefes;
final class RareNames {
 static String original(String id){return switch(id){
 case "poison" -> "Poison";
 case "backstab" -> "Backstab";
 case "blinding" -> "Blinding";
 case "calllighting" -> "CallLighting";
 case "confuse" -> "Confuse";
 case "disarm" -> "Disarm";
 case "slow" -> "Slow";
 case "vampiricregeneration" -> "VampiricRegeneration";
 case "weaken" -> "Weaken";
 case "execute" -> "Execute";
 case "bleed" -> "Bleed";
 case "fertilize" -> "Fertilize";
 case "meltobsidian" -> "MeltObsidian";
 case "smelt" -> "Smelt";
 case "spore" -> "Spore";
 case "veinminer" -> "VeinMiner";
 case "treefeller" -> "TreeFeller";
 case "magneto" -> "Magneto";
 case "burst" -> "Burst";
 case "craftitem" -> "CraftItem";
 case "fireresistance" -> "FireResistance";
 case "greaterburst" -> "GreaterBurst";
 case "haste" -> "Haste";
 case "invisibility" -> "Invisibility";
 case "magicbag" -> "MagicBag";
 case "repairitem" -> "RepairItem";
 case "durability" -> "Durability";
 case "regeneration" -> "Regeneration";
 case "nofalldamage" -> "NoFallDamage";
 case "doublejump" -> "DoubleJump";
 case "fly" -> "Fly";
 case "hardy" -> "Hardy";
 case "clarity" -> "Clarity";
 case "hungersteal" -> "HungerSteal";
 case "strength" -> "Strength";
 case "turtle" -> "Turtle";
 case "garlic" -> "Garlic";
 case "aura_esporas" -> "Spore";
 case "guardian" -> "Guardian";
 case "paso_viento" -> "PasoViento";
 case "saturacion" -> "Saturation";
 case "flame" -> "Flame";
 case "brasas" -> "Embers";
 default->id;};}
 static String title(String id,String custom){String old=original(id);return custom.contains("(")?custom:custom+" ("+old+")";}
 static String bare(String title){int at=title.lastIndexOf(" (");return at<0?title:title.substring(0,at);}
}
