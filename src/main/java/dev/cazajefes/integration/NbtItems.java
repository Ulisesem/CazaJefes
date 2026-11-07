package dev.cazajefes.integration;

import java.lang.reflect.Method;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/** Interoperable SNBT for externally exported items, with no change to existing item IDs. */
public final class NbtItems {
    private record Hook(Method encode, Method parse, Method decode) {}
    private record Tiles(Method get,Method modify,Method merge,Method parse) {}
    private static Hook hook;
    private static Tiles tiles;

    public static void load(Plugin owner) {
        hook = null;
        tiles = null;
        var provider = owner.getServer().getPluginManager().getPlugin("NBTAPI");
        if (provider == null || !provider.isEnabled()) return;
        try {
            var loader = provider.getClass().getClassLoader();
            var nbt = loader.loadClass("de.tr7zw.nbtapi.NBT");
            var readable = loader.loadClass("de.tr7zw.nbtapi.iface.ReadableNBT");
            hook = new Hook(nbt.getMethod("itemStackToNBT",ItemStack.class), nbt.getMethod("parseNBT",String.class),
                nbt.getMethod("itemStackFromNBT",readable));
            var state=org.bukkit.block.BlockState.class;
            var write=loader.loadClass("de.tr7zw.nbtapi.iface.ReadWriteNBT");
            tiles=new Tiles(nbt.getMethod("get",state,java.util.function.Function.class),
                nbt.getMethod("modify",state,java.util.function.Consumer.class),
                write.getMethod("mergeCompound",readable),nbt.getMethod("parseNBT",String.class));
        } catch (ReflectiveOperationException | RuntimeException | LinkageError error) {
            owner.getLogger().warning("NBTAPI no disponible para exportar y leer SNBT.");
        }
    }

    public static String snapshotTile(org.bukkit.block.BlockState state){
        var bridge=tiles;
        if(bridge==null)return null;
        try{
            return (String)bridge.get().invoke(null,state,(java.util.function.Function<Object,String>)Object::toString);
        }catch(ReflectiveOperationException|RuntimeException|LinkageError failure){
            return null;
        }
    }

    public static boolean restoreTile(org.bukkit.block.BlockState state,String snapshot){
        var bridge=tiles;
        if(bridge==null)return false;
        try{
            Object parsed=bridge.parse().invoke(null,snapshot);
            bridge.modify().invoke(null,state,(java.util.function.Consumer<Object>)target->{
                try{bridge.merge().invoke(target,parsed);}
                catch(ReflectiveOperationException failure){throw new IllegalStateException(failure);}
            });
            return true;
        }catch(ReflectiveOperationException|RuntimeException|LinkageError failure){
            return false;
        }
    }

    public static String encode(ItemStack stack) {
        if (hook == null || stack == null || stack.isEmpty()) return null;
        try {
            return hook.encode().invoke(null,stack).toString();
        } catch (ReflectiveOperationException | RuntimeException | LinkageError failure) {
            return null;
        }
    }

    public static ItemStack decode(String input) {
        if (hook == null || input == null || input.length() > 65536) return null;
        try {
            return (ItemStack) hook.decode().invoke(null,hook.parse().invoke(null,input));
        } catch (ReflectiveOperationException | RuntimeException | LinkageError failure) {
            return null;
        }
    }
}
