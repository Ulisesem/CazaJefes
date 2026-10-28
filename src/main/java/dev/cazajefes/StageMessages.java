package dev.cazajefes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.*;
final class StageMessages {
 static Component format(BossCatalog.Definition boss,int stage){String roman=stage==1?"Ⅰ":stage==2?"Ⅱ":"Ⅲ";String title=stage==1?"ᴀᴄᴇᴄʜᴏ":stage==2?"ʀᴜᴘᴛᴜʀᴀ":"ꜰᴜʀɪᴀ ꜰɪɴᴀʟ";Component rune=Component.text("|||",NamedTextColor.GOLD).decorate(TextDecoration.OBFUSCATED);
 return Component.empty().decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC,false).append(rune).append(Component.text(" ✦ ᴇᴛᴀᴘᴀ "+roman+" / Ⅲ · "+title+" ✦ ",NamedTextColor.GOLD)).append(rune).append(Component.newline()).append(Component.text("  » "+boss.name()+" · "+(stage==1?"El combate comienza":stage==2?"Su poder está aumentando":"¡Su poder ha despertado!"),boss.tier().color));}
}
