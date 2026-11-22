package dev.cazajefes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
final class BossAnnouncements {
 private BossAnnouncements(){}
 static Component awakened(String player,BossCatalog.Definition boss){
  var color=boss.tier().color;
  return Component.text(boss.mini()?"MINIJEFE DESPERTADO":"JEFE DESPERTADO",color).decorate(TextDecoration.BOLD)
      .append(Component.text(" » ",NamedTextColor.DARK_GRAY))
      .append(Component.text(player,NamedTextColor.WHITE))
      .append(Component.text(" ha despertado a ",NamedTextColor.GRAY))
      .append(Component.text(boss.name(),color).decorate(TextDecoration.BOLD))
      .append(Component.text(" · "+boss.tier().display,color));
 }
}
