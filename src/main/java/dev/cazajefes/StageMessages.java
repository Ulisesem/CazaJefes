package dev.cazajefes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.*;
final class StageMessages {
 static Component format(BossCatalog.Definition boss,int stage){String roman=stage==1?"Ⅰ":stage==2?"Ⅱ":"Ⅲ";String title=stage==1?"ᴀᴄᴇᴄʜᴏ":stage==2?"ʀᴜᴘᴛᴜʀᴀ":"ꜰᴜʀɪᴀ ꜰɪɴᴀʟ";
 return Component.empty().decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC,false).append(Component.text("━━━━ ᴇᴛᴀᴘᴀ "+roman+" / Ⅲ ━━━━",NamedTextColor.GOLD)).append(Component.newline()).append(Component.text(boss.name(),NamedTextColor.AQUA)).append(Component.text(" · "+title,NamedTextColor.GOLD)).append(Component.newline()).append(Component.text(stage==1?"El combate comienza":stage==2?"Su poder está aumentando":"¡Su poder ha despertado!",NamedTextColor.AQUA));}
}
