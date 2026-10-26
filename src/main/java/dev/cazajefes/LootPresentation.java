package dev.cazajefes;
import java.util.*;
import org.bukkit.inventory.ItemStack;
final class LootPresentation {
 static <T> List<T> split(int amount,java.util.function.Supplier<T> copy){List<T> out=new ArrayList<>();for(int i=0;i<amount;i++)out.add(copy.get());return out;}
 static List<ItemStack> units(ItemStack stack){return split(stack.getAmount(),()->{ItemStack unit=stack.clone();unit.setAmount(1);return unit;});}
 static boolean essence(String id){return "rare_essence".equals(id)||"rare_essence_primordial".equals(id)||"rare_essence_abyssal".equals(id);}
}
