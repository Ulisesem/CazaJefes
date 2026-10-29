package dev.cazajefes;
final class RareAliases {static String canonical(String id){if(id==null)return null;return switch(id){case "veneno"->"poison";case "vampirismo"->"vampiricregeneration";default->id;};}static boolean duplicate(String id){return !id.equals(canonical(id));}}
