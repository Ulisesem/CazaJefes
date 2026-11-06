package dev.cazajefes.ai.visual;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.attribute.Attributes;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.particle.Particle;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.util.Vector3f;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.*;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;

public final class PacketBossVisuals implements BossVisuals {
    private static final double RANGE=40;
    private static final AtomicInteger VIRTUAL_IDS=new AtomicInteger(Integer.MAX_VALUE);
    private static final Vector3f ZERO=new Vector3f(0,0,0);

    @Override public View boss(LivingEntity body){
        body.setVisibleByDefault(false);
        return new PacketView(body.getEntityId(),body.getUniqueId(),SpigotConversionUtil.fromBukkitEntityType(body.getType()),body,null,0);
    }

    @Override public View projectile(Location origin){
        return projectile(origin,Projectile.ARROW);
    }

    @Override public View projectile(Location origin,Projectile kind){
        var type=switch(kind){
            case ARROW -> EntityTypes.ARROW;
            case FIREWORK -> EntityTypes.FIREWORK_ROCKET;
            case SHULKER -> EntityTypes.SHULKER_BULLET;
            case SPELL -> EntityTypes.SMALL_FIREBALL;
        };
        var view=new PacketView(VIRTUAL_IDS.getAndDecrement(),UUID.randomUUID(),type,null,null,0);
        view.move(origin);
        return view;
    }

    @Override public View meteor(Location origin,ItemStack helmet){
        var view=new PacketView(VIRTUAL_IDS.getAndDecrement(),UUID.randomUUID(),EntityTypes.ARMOR_STAND,null,helmet,-1.55);
        view.move(origin);
        return view;
    }

    @Override public void explosion(Location center){
        for(Player viewer:center.getWorld().getNearbyPlayers(center,RANGE)){
            if(!receives(viewer,center))continue;
            send(viewer,new WrapperPlayServerParticle(new Particle<>(ParticleTypes.EXPLOSION),true,
                point(center,0),ZERO,0,1,true));
            send(viewer,new WrapperPlayServerParticle(new Particle<>(ParticleTypes.FLAME),true,
                point(center,0),new Vector3f(.38f,.18f,.38f),.045f,12,true));
        }
    }

    private static final class PacketView implements View {
        private final int id;
        private final UUID uuid;
        private final EntityType species;
        private final LivingEntity body;
        private final ItemStack helmet;
        private final double offset;
        private final Set<UUID> viewers=new HashSet<>();
        private Location previous;
        private boolean closed;
        private List<Equipment> cachedEquipment=List.of();
        private int nextAudience,nextEquipment;
        private double lastScale=Double.NaN;

        PacketView(int id,UUID uuid,EntityType species,LivingEntity body,ItemStack helmet,double offset){
            this.id=id;
            this.uuid=uuid;
            this.species=species;
            this.body=body;
            this.helmet=helmet;
            this.offset=offset;
        }

        @Override public void move(Location at){
            if(closed)return;
            int tick=Bukkit.getCurrentTick();
            if(previous!=null&&previous.getWorld()!=at.getWorld()){
                destroyAll();
                previous=null;
            }
            if(tick>=nextAudience){
                refresh(at);
                nextAudience=tick+5;
            }
            if(viewers.isEmpty())return;
            boolean changed=previous==null||previous.distanceSquared(at)>.000001
                ||Math.abs(previous.getYaw()-at.getYaw())>.6||Math.abs(previous.getPitch()-at.getPitch())>.6;
            if(changed){
                for(UUID uuid:viewers){
                    var viewer=Bukkit.getPlayer(uuid);
                    if(viewer==null||viewer.getWorld()!=at.getWorld())continue;
                    send(viewer,new WrapperPlayServerEntityTeleport(id,point(at,offset),at.getYaw(),at.getPitch(),false));
                    if(body!=null)send(viewer,new WrapperPlayServerEntityHeadLook(id,at.getYaw()));
                }
                previous=at.clone();
            }
            if(body!=null&&tick>=nextEquipment){
                var current=equipment();
                if(!cachedEquipment.equals(current)){
                    cachedEquipment=current;
                    for(UUID uuid:viewers){
                        var viewer=Bukkit.getPlayer(uuid);
                        if(viewer!=null&&viewer.getWorld()==at.getWorld())
                            send(viewer,new WrapperPlayServerEntityEquipment(id,current));
                    }
                }
                var attribute=body.getAttribute(Attribute.SCALE);
                if(attribute!=null&&attribute.getValue()!=lastScale){
                    lastScale=attribute.getValue();
                    for(UUID uuid:viewers){
                        var viewer=Bukkit.getPlayer(uuid);
                        if(viewer!=null&&viewer.getWorld()==at.getWorld())sendScale(viewer,lastScale);
                    }
                }
                nextEquipment=tick+10;
            }
        }

        private void refresh(Location at){
            var eligible=new HashSet<UUID>();
            for(Player player:at.getWorld().getNearbyPlayers(at,RANGE)){
                if(!receives(player,at))continue;
                UUID viewer=player.getUniqueId();
                eligible.add(viewer);
                if(viewers.add(viewer))spawn(player,at);
            }
            viewers.removeIf(uuid->{
                if(eligible.contains(uuid))return false;
                Player viewer=Bukkit.getPlayer(uuid);
                if(viewer!=null&&viewer.getWorld()==at.getWorld())send(viewer,new WrapperPlayServerDestroyEntities(id));
                return true;
            });
        }

        private void spawn(Player viewer,Location at){
            send(viewer,new WrapperPlayServerSpawnEntity(id,Optional.of(uuid),species,point(at,offset),
                at.getPitch(),at.getYaw(),at.getYaw(),0,Optional.empty()));
            var metadata=new ArrayList<EntityData<?>>();
            metadata.add(new EntityData<>(5,EntityDataTypes.BOOLEAN,true));
            if(species.equals(EntityTypes.FIREWORK_ROCKET))
                metadata.add(new EntityData<>(8,EntityDataTypes.ITEMSTACK,
                    SpigotConversionUtil.fromBukkitItemStack(new ItemStack(org.bukkit.Material.FIREWORK_ROCKET))));
            if(helmet!=null)metadata.add(new EntityData<>(0,EntityDataTypes.BYTE,(byte)0x20));
            if(body!=null){
                Component name=body.customName();
                if(name!=null){
                    metadata.add(new EntityData<>(2,EntityDataTypes.OPTIONAL_ADV_COMPONENT,Optional.of(name)));
                    metadata.add(new EntityData<>(3,EntityDataTypes.BOOLEAN,true));
                }
                if(body instanceof Slime slime)metadata.add(new EntityData<>(16,EntityDataTypes.INT,slime.getSize()));
            }
            send(viewer,new WrapperPlayServerEntityMetadata(id,metadata));
            if(body!=null&&body.getAttribute(Attribute.SCALE)!=null){
                double scale=body.getAttribute(Attribute.SCALE).getValue();
                sendScale(viewer,scale);
            }
            var equipment=equipment();
            if(!equipment.isEmpty())send(viewer,new WrapperPlayServerEntityEquipment(id,equipment));
        }

        private void sendScale(Player viewer,double scale){
            send(viewer,new WrapperPlayServerUpdateAttributes(id,List.of(
                new WrapperPlayServerUpdateAttributes.Property(Attributes.SCALE,scale,List.of()))));
        }

        private List<Equipment> equipment(){
            if(helmet!=null)return List.of(item(EquipmentSlot.HELMET,helmet));
            if(body==null||body.getEquipment()==null)return List.of();
            var gear=body.getEquipment();
            return List.of(item(EquipmentSlot.MAIN_HAND,gear.getItemInMainHand()),item(EquipmentSlot.OFF_HAND,gear.getItemInOffHand()),
                item(EquipmentSlot.HELMET,gear.getHelmet()),item(EquipmentSlot.CHEST_PLATE,gear.getChestplate()),
                item(EquipmentSlot.LEGGINGS,gear.getLeggings()),item(EquipmentSlot.BOOTS,gear.getBoots()));
        }

        private void destroyAll(){
            for(UUID uuid:viewers){
                Player viewer=Bukkit.getPlayer(uuid);
                if(viewer!=null&&(previous==null||viewer.getWorld()==previous.getWorld()))
                    send(viewer,new WrapperPlayServerDestroyEntities(id));
            }
            viewers.clear();
        }

        @Override public void close(){
            if(closed)return;
            closed=true;
            destroyAll();
            if(body!=null&&body.isValid()&&!body.isDead()){
                body.setVisibleByDefault(true);
                body.setGravity(true);
            }
        }
    }

    private static Equipment item(EquipmentSlot slot,ItemStack item){
        return new Equipment(slot,item==null?com.github.retrooper.packetevents.protocol.item.ItemStack.EMPTY:
            SpigotConversionUtil.fromBukkitItemStack(item));
    }

    private static boolean receives(Player viewer,Location at){
        return viewer.getLocation().distanceSquared(at)<=RANGE*RANGE
            && viewer.isChunkSent(org.bukkit.Chunk.getChunkKey(at.getBlockX()>>4,at.getBlockZ()>>4));
    }

    private static Vector3d point(Location at,double offset){
        return new Vector3d(at.getX(),at.getY()+offset,at.getZ());
    }

    private static void send(Player viewer,PacketWrapper<?> packet){
        PacketEvents.getAPI().getPlayerManager().sendPacket(viewer,packet);
    }
}
