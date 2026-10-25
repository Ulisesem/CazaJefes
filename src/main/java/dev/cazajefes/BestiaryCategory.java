package dev.cazajefes;
import net.kyori.adventure.text.format.NamedTextColor;
enum BestiaryCategory {
 MINI("Minijefes",null,NamedTextColor.AQUA),RARE("Raros",BossCatalog.Tier.RARE,NamedTextColor.GREEN),EPIC("Épicos",BossCatalog.Tier.EPIC,NamedTextColor.LIGHT_PURPLE),LEGENDARY("Legendarios",BossCatalog.Tier.LEGENDARY,NamedTextColor.GOLD),MYTHIC("Míticos",BossCatalog.Tier.MYTHIC,NamedTextColor.DARK_PURPLE),ABYSSAL("Abismales",BossCatalog.Tier.ABYSSAL,NamedTextColor.DARK_RED);
 final String title;final BossCatalog.Tier tier;final NamedTextColor color;
 BestiaryCategory(String title,BossCatalog.Tier tier,NamedTextColor color){this.title=title;this.tier=tier;this.color=color;}
 boolean matches(BossCatalog.Definition boss){return this==MINI?boss.mini():!boss.mini()&&boss.tier()==tier;}
 static BestiaryCategory of(BossCatalog.Definition boss){for(var c:values())if(c.matches(boss))return c;throw new IllegalArgumentException("Boss sin categoría: "+boss.id());}
}
