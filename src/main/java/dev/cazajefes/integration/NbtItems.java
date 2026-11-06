package dev.cazajefes.integration;

import java.lang.reflect.Method;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/** Interoperable SNBT for externally exported items, with no change to existing item IDs. */
public final class NbtItems {
    private record Hook(Method encode, Method parse, Method decode) {}
    private static Hook hook;

    public static void load(Plugin owner) {
        hook = null;
        var provider = owner.getServer().getPluginManager().getPlugin("NBTAPI");
        if (provider == null || !provider.isEnabled()) return;
        try {
            var loader = provider.getClass().getClassLoader();
            var nbt = loader.loadClass("de.tr7zw.nbtapi.NBT");
            var readable = loader.loadClass("de.tr7zw.nbtapi.iface.ReadableNBT");
            hook = new Hook(nbt.getMethod("itemStackToNBT",ItemStack.class), nbt.getMethod("parseNBT",String.class),
                nbt.getMethod("itemStackFromNBT",readable));
        } catch (ReflectiveOperationException | RuntimeException | LinkageError error) {
            owner.getLogger().warning("NBTAPI no disponible para exportar y leer SNBT.");
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
