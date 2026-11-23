package dev.cazajefes;

import java.util.*;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import net.kyori.adventure.text.format.NamedTextColor;

final class RecipePresentation {
 static final int LIST_BACK=49,ARMOR_LIST_SEAL=51;
 static final int[] CATEGORIES={19,22,25,28,31,34};
 static final List<RaresMenu.Category> HOME=List.of(RaresMenu.Category.ESSENCES,RaresMenu.Category.SPELLS,
     RaresMenu.Category.CRYSTALS,RaresMenu.Category.MATERIALS,RaresMenu.Category.CURRENCIES,RaresMenu.Category.ALL);
 enum Parent {ROOT,GROUPS,RANKS,LIST}

 static boolean visible(String key) {
  return CraftAvailability.allowed(key)&&!key.equals("sello_resonancia_primigenia");
 }
 static Map<Integer,RaresMenu.Category> homeCategories(List<Integer> slots) {
  if(slots.size()!=HOME.size())throw new IllegalArgumentException("Se requieren seis apartados");
  Map<Integer,RaresMenu.Category> categories=new LinkedHashMap<>();
  for(int i=0;i<HOME.size();i++)categories.put(slots.get(i),HOME.get(i));
  return Map.copyOf(categories);
 }
 static Parent parent(boolean detail,boolean groups,boolean ranks,BossCatalog.Tier rank,RareCompatibility.Group group) {
  if(detail)return Parent.LIST;
  if(groups||ranks)return Parent.ROOT;
  if(group!=null)return Parent.GROUPS;
  return rank!=null?Parent.RANKS:Parent.ROOT;
 }
 static int pages(int recipes,int pageSize) {
  if(recipes<0||pageSize<1||pageSize>28)throw new IllegalArgumentException("Paginación inválida");
  return Math.max(1,(recipes+pageSize-1)/pageSize);
 }
 static String description(RaresMenu.Category category) {
  return switch(category) {
   case ESSENCES -> "Habilidades para mejorar tu equipo.";
   case SPELLS -> "Habilidades que activas con un libro.";
   case ALL -> "Explora todas las recetas disponibles.";
   case CRYSTALS -> "Combina cristales y mejora su categoría.";
   case MATERIALS -> "Componentes para crear esencias y reliquias.";
   case CURRENCIES -> "Crea una brújula para encontrar huevos de jefe.";
   default -> "Consulta sus ingredientes y resultado.";
  };
 }
 static void frame(Inventory inventory,Material accent) {
  ItemStack pane=PremiumStyle.tile(accent," ",NamedTextColor.DARK_GRAY,false);
  for(int slot=0;slot<inventory.getSize();slot++)inventory.setItem(slot,pane);
  for(int slot:PremiumStyle.GRID)inventory.setItem(slot,null);
 }
 static Material accent(RaresMenu.Category category){return Material.BLUE_STAINED_GLASS_PANE;}
}