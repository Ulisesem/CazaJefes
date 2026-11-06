package dev.cazajefes.integration;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/** Optional packet delivery. Paper retains authority over real items and entities. */
public final class PacketBridge {
    private record Transport(Object manager, Method send, ClassLoader loader) {
        Class<?> type(String suffix) throws ClassNotFoundException {
            return loader.loadClass("com.github.retrooper.packetevents." + suffix);
        }
        Class<?> conversion() throws ClassNotFoundException {
            return loader.loadClass("io.github.retrooper.packetevents.util.SpigotConversionUtil");
        }
        void send(Player viewer, Object packet) throws ReflectiveOperationException {
            send.invoke(manager, viewer, packet);
        }
    }
    private record Particles(Method lookup, Constructor<?> particle, Constructor<?> position,
        Constructor<?> offset, Constructor<?> packet, boolean alwaysShow) {}
    private record Equipment(Method convert, Constructor<?> entry, Constructor<?> packet, Object mainHand) {}
    private static final EnumMap<Particle,Object> simple = new EnumMap<>(Particle.class);
    private static Plugin owner;
    private static Transport transport;
    private static Particles particles;
    private static Equipment equipment;

    public static void load(Plugin plugin) {
        owner = plugin;
        transport = null;
        particles = null;
        equipment = null;
        simple.clear();
        var provider = plugin.getServer().getPluginManager().getPlugin("packetevents");
        if (provider == null || !provider.isEnabled()) return;
        try {
            var loader = provider.getClass().getClassLoader();
            var api = loader.loadClass("com.github.retrooper.packetevents.PacketEvents").getMethod("getAPI");
            var manager = api.getReturnType().getMethod("getPlayerManager");
            var wrapper = loader.loadClass("com.github.retrooper.packetevents.wrapper.PacketWrapper");
            transport = new Transport(manager.invoke(api.invoke(null)),
                manager.getReturnType().getMethod("sendPacket", Object.class, wrapper), loader);
        } catch (ReflectiveOperationException | RuntimeException | LinkageError failure) {
            fallback("transporte", failure);
            return;
        }
        try {
            var t = transport;
            var particle = t.type("protocol.particle.Particle");
            var particleType = t.type("protocol.particle.type.ParticleType");
            var position = t.type("util.Vector3d");
            var offset = t.type("util.Vector3f");
            var wrapper = t.type("wrapper.play.server.WrapperPlayServerParticle");
            Constructor<?> packet;
            boolean alwaysShow;
            try {
                packet = wrapper.getConstructor(particle, boolean.class, position, offset, float.class, int.class, boolean.class);
                alwaysShow = true;
            } catch (NoSuchMethodException ignored) {
                packet = wrapper.getConstructor(particle, boolean.class, position, offset, float.class, int.class);
                alwaysShow = false;
            }
            particles = new Particles(t.type("protocol.particle.type.ParticleTypes").getMethod("getByName", String.class),
                particle.getConstructor(particleType), position.getConstructor(double.class,double.class,double.class),
                offset.getConstructor(float.class,float.class,float.class), packet, alwaysShow);
        } catch (ReflectiveOperationException | RuntimeException | LinkageError failure) {
            fallback("partículas", failure);
        }
        try {
            var t = transport;
            var slot = t.type("protocol.player.EquipmentSlot");
            var item = t.type("protocol.item.ItemStack");
            equipment = new Equipment(t.conversion().getMethod("fromBukkitItemStack", ItemStack.class),
                t.type("protocol.player.Equipment").getConstructor(slot,item),
                t.type("wrapper.play.server.WrapperPlayServerEntityEquipment").getConstructor(int.class,List.class),
                slot.getField("MAIN_HAND").get(null));
        } catch (ReflectiveOperationException | RuntimeException | LinkageError failure) {
            fallback("equipo de entidades", failure);
        }
    }

    public static <T> void particle(Player viewer, Particle type, Location point, int count,
                             double x, double y, double z, double speed, T data) {
        var p = particles;
        if (p != null && data == null && type.getDataType() == Void.class) {
            try {
                Object effect = simple.get(type);
                if (effect == null) {
                    var protocolType = p.lookup().invoke(null,type.getKey().toString());
                    if (protocolType != null) {
                        effect = p.particle().newInstance(protocolType);
                        simple.put(type,effect);
                    }
                }
                if (effect != null) {
                    var pos = p.position().newInstance(point.getX(),point.getY(),point.getZ());
                    var offset = p.offset().newInstance((float)x,(float)y,(float)z);
                    var packet = p.alwaysShow()
                        ? p.packet().newInstance(effect,true,pos,offset,(float)speed,count,true)
                        : p.packet().newInstance(effect,true,pos,offset,(float)speed,count);
                    transport.send(viewer,packet);
                    return;
                }
            } catch (ReflectiveOperationException | RuntimeException | LinkageError failure) {
                particles = null;
                fallback("partículas", failure);
            }
        }
        viewer.spawnParticle(type,point,count,x,y,z,speed,data,true);
    }

    public static void equipment(LivingEntity entity) {
        var e = equipment;
        if (e == null || entity.getEquipment() == null || !entity.isValid()) return;
        try {
            var stack = e.convert().invoke(null,entity.getEquipment().getItemInMainHand());
            var slot = e.entry().newInstance(e.mainHand(),stack);
            for (Player viewer : entity.getTrackedBy()) if (viewer.canSee(entity)) {
                transport.send(viewer,e.packet().newInstance(entity.getEntityId(),List.of(slot)));
            }
        } catch (ReflectiveOperationException | RuntimeException | LinkageError failure) {
            equipment = null;
            fallback("equipo de entidades", failure);
        }
    }

    private static void fallback(String capability, Throwable failure) {
        owner.getLogger().warning("PacketEvents " + capability + ": " + failure.getClass().getSimpleName() + ". Se usará Paper.");
    }
}
