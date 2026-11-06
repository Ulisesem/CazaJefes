package dev.cazajefes;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

final class PrivateWarnings {
   private record Hook(Plugin provider,Object manager,Method send,Constructor<?> actionBar) {}
   private static Hook hook;

   static void load(Plugin owner) {
      hook=null;
      Plugin provider=owner.getServer().getPluginManager().getPlugin("packetevents");
      if(provider==null || !provider.isEnabled())return;
      try {
         var loader=provider.getClass().getClassLoader();
         String root="com.github.retrooper.packetevents.";
         var api=loader.loadClass(root+"PacketEvents").getMethod("getAPI");
         var manager=api.getReturnType().getMethod("getPlayerManager");
         var wrapper=loader.loadClass(root+"wrapper.PacketWrapper");
         var packet=loader.loadClass(root+"wrapper.play.server.WrapperPlayServerActionBar");
         hook=new Hook(provider,manager.invoke(api.invoke(null)),manager.getReturnType().getMethod("sendPacket",Object.class,wrapper),
            packet.getConstructor(Component.class));
      } catch(ReflectiveOperationException | LinkageError | RuntimeException error) {
         owner.getLogger().warning("PacketEvents no disponible para avisos; se usará Paper.");
      }
   }

   static void send(Player player,Component message) {
      if(hook!=null && hook.provider().isEnabled()) try {
         hook.send().invoke(hook.manager(),player,hook.actionBar().newInstance(message));
         return;
      } catch(ReflectiveOperationException | LinkageError | RuntimeException error) {
         hook=null;
      }
      player.sendActionBar(message);
   }
}
