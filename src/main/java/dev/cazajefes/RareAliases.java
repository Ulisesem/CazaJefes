package dev.cazajefes;

import java.text.Normalizer;
import java.util.*;

/** Commands use the short effect tag shown in parentheses; existing internal IDs remain valid. */
final class RareAliases {
 private record Names(Map<String,String> ids,Map<String,String> commands){}
 private static final class Holder {static final Names NAMES=names();}
 private static String fold(String text){return Normalizer.normalize(text,Normalizer.Form.NFD).replaceAll("\\p{M}","").toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]","");}
 private static Names names(){
  Map<String,String> ids=new LinkedHashMap<>(),commands=new LinkedHashMap<>();
  for(var definition:RareDefinitions.ALL.values()){
   String title=definition.title(),id=definition.id();int start=title.lastIndexOf('('),end=title.lastIndexOf(')');
   String command=start>=0&&end>start?title.substring(start+1,end).trim().toLowerCase(Locale.ROOT):id;
   ids.put(id,id);ids.put(fold(id),id);ids.putIfAbsent(fold(title),id);ids.putIfAbsent(command,id);ids.putIfAbsent(fold(command),id);commands.put(id,command);
  }
  for(String id:List.of("garlic","turtle","strength","aura_esporas","guardian","paso_viento","saturacion","flame","magneto","brasas")){
   // Spore already names the tool power. The armor aura retains its unambiguous ID.
   String command=id.equals("aura_esporas")?id:RareNames.original(id).toLowerCase(Locale.ROOT);
   ids.put(id,id);ids.put(fold(id),id);ids.putIfAbsent(command,id);ids.putIfAbsent(fold(command),id);commands.putIfAbsent(id,command);
  }
  ids.put("veneno","poison");ids.put("vampirismo","vampiricregeneration");
  return new Names(Map.copyOf(ids),Map.copyOf(commands));
 }
 static String canonical(String id){if(id==null)return null;String input=id.trim().toLowerCase(Locale.ROOT),known=Holder.NAMES.ids().get(input);return known!=null?known:Holder.NAMES.ids().getOrDefault(fold(input),input);}
 static String commandName(String id){String canonical=canonical(id);return canonical==null?null:Holder.NAMES.commands().getOrDefault(canonical,canonical);}
 static List<String> commandNames(){return Holder.NAMES.commands().values().stream().distinct().sorted().toList();}
 static boolean duplicate(String id){return id!=null&&!id.equals(canonical(id));}
}
