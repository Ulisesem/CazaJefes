package dev.cazajefes;
import org.bukkit.Material;
final class RecipePresentation {
 static final int[] CATEGORIES={11,13,15,20,22,24,29,31,33};
 static Material accent(RaresMenu.Category category){if(category==null)return Material.CYAN_STAINED_GLASS_PANE;return switch(category){case CURRENCIES->Material.ORANGE_STAINED_GLASS_PANE;case CRYSTALS,ESSENCES->Material.PURPLE_STAINED_GLASS_PANE;case WEAPONS,PVP->Material.RED_STAINED_GLASS_PANE;case ARMOR->Material.BLUE_STAINED_GLASS_PANE;case SPELLS->Material.CYAN_STAINED_GLASS_PANE;case MATERIALS->Material.BROWN_STAINED_GLASS_PANE;case ALL->Material.GRAY_STAINED_GLASS_PANE;};}
}
