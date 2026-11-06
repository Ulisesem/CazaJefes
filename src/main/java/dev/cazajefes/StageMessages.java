package dev.cazajefes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.*;
final class StageMessages {
 static Component format(BossCatalog.Definition boss,int stage){return format(boss,stage,null,null);}
 static Component format(BossCatalog.Definition boss,int stage,Languages languages,org.bukkit.entity.Player player){String roman=stage==1?"Ⅰ":stage==2?"Ⅱ":"Ⅲ";String title=stage==1?"ᴀᴄᴇᴄʜᴏ":stage==2?"ʀᴜᴘᴛᴜʀᴀ":"ꜰᴜʀɪᴀ ꜰɪɴᴀʟ";
 String label="ᴇᴛᴀᴘᴀ";
 String name=boss.name();
 String line=stage==1?"El combate comienza":stage==2?"Su poder está aumentando":"¡Su poder ha despertado!";
 if(languages!=null){
  label=languages.text(player,"stage.label",label);
  name=languages.boss(player,boss,"name");
  title=languages.text(player,"stage."+stage+".title",title);
  line=languages.text(player,"stage."+stage+".line",line);
 }
 return Component.empty().decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC,false).append(Component.text("━━━━ "+label+" "+roman+" / Ⅲ ━━━━",NamedTextColor.GOLD)).append(Component.newline()).append(Component.text(name,NamedTextColor.AQUA)).append(Component.text(" · "+title,NamedTextColor.GOLD)).append(Component.newline()).append(Component.text(line,NamedTextColor.AQUA));}
}
