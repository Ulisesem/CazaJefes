package dev.cazajefes;

import com.google.gson.JsonElement;
import dev.cazajefes.ai.BossAttack;
import dev.cazajefes.ai.BossEnvironment;
import dev.cazajefes.ai.BossProfile;
import dev.cazajefes.ai.CustomBoss;
import dev.cazajefes.ai.KinematicBoss;
import dev.cazajefes.ai.visual.BossVisuals;
import dev.cazajefes.ai.visual.PacketBossVisuals;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

final class CustomEncounter implements BossEnvironment {
    private final CazaJefesPlugin plugin;
    private final dev.cazajefes.ai.BossManager loop;
    private final BossDisplacement displacement;
    private final BossVisuals visuals=new PacketBossVisuals();
    private final Map<String,BossProfile> profiles=new HashMap<>();
    private final Map<UUID,BossStages> stages=new HashMap<>();

    CustomEncounter(CazaJefesPlugin plugin){
        this.plugin=plugin;
        this.displacement=new BossDisplacement(plugin);
        for(var definition:BossCatalog.all()){
            profiles.put(definition.id(),read(definition));
        }
        loop=new dev.cazajefes.ai.BossManager(plugin);
    }

    private BossProfile read(BossCatalog.Definition definition){
        boolean aquatic=AquaticCombat.aquatic(definition.entityType());
        try{
            return BossProfile.read(definition.custom().ai(),definition.custom().damage(),aquatic);
        }catch(RuntimeException invalid){
            plugin.getLogger().warning("IA "+definition.id()+": "+invalid.getMessage()+". Se usan sus ataques base.");
            return BossProfile.read(null,definition.custom().damage(),aquatic);
        }
    }

    BossProfile profile(BossCatalog.Definition definition){return profiles.computeIfAbsent(definition.id(),id->read(definition));}

    void attach(LivingEntity body,BossCatalog.Definition definition){
        remove(body.getUniqueId());
        equip(body,definition);
        stages.put(body.getUniqueId(),new BossStages(body,definition,plugin.languages));
        loop.add(new KinematicBoss(body,profile(definition),this));
    }

    void remove(UUID uuid){
        loop.remove(uuid);
        stages.remove(uuid);
        displacement.remove(uuid);
    }

    boolean owns(LivingEntity entity){return loop.owns(entity.getUniqueId());}

    boolean attacking(LivingEntity entity){
        var boss=loop.get(entity.getUniqueId());
        return boss!=null&&boss.state()==dev.cazajefes.ai.BossState.ATTACKING;
    }

    boolean push(org.bukkit.entity.Entity entity,org.bukkit.util.Vector velocity){
        if(!(loop.get(entity.getUniqueId()) instanceof KinematicBoss boss))return false;
        displacement.remove(entity.getUniqueId());
        boss.push(velocity);
        return true;
    }

    void stop(){loop.stop();stages.clear();displacement.clear();}

    @Override public Player acquire(CustomBoss boss){
        double range=boss.profile().detectionRange();
        if(boss.profile().aquatic()&&boss.body().isInWater()){
            var swimmer=ThreatTable.pick(boss.body(),range,player->AquaticCombat.water(player.getLocation()));
            if(swimmer!=null)return swimmer;
        }
        return ThreatTable.pick(boss.body(),range,player->true);
    }

    @Override public boolean validTarget(CustomBoss boss,Player player){
        double range=boss.profile().detectionRange();
        return player!=null&&CombatGuard.eligible(player)&&player.getWorld()==boss.body().getWorld()
            &&player.getLocation().distanceSquared(boss.body().getLocation())<=range*range
            &&ArenaBoundary.allows(boss.body(),player.getLocation());
    }

    @Override public boolean mayMove(CustomBoss boss,Location destination){
        return destination.getWorld()==boss.body().getWorld()&&destination.getWorld().getWorldBorder().isInside(destination)
            &&ArenaBoundary.allows(boss.body(),destination);
    }

    @Override public boolean mayHit(CustomBoss boss,LivingEntity victim){
        var data=victim.getPersistentDataContainer();
        return victim.isValid()&&!victim.isDead()&&!victim.isInvulnerable()&&victim!=boss.body()
            &&!BossIntegrity.boss(victim)&&!data.has(new NamespacedKey("cazajefes","boss_minion"),PersistentDataType.BYTE)
            &&!data.has(new NamespacedKey("cazajefes","death_visual"),PersistentDataType.BYTE)
            &&(!(victim instanceof Player player)||CombatGuard.eligible(player))
            &&ArenaBoundary.allows(boss.body(),victim.getLocation());
    }

    @Override public void damage(CustomBoss boss,LivingEntity victim,double damage){
        if(damage<=0||!mayHit(boss,victim))return;
        if(victim instanceof Player player)AbilityDamage.hit(player,boss.body(),damage,true);
        else victim.damage(damage,boss.body());
    }

    @Override public void warning(CustomBoss boss,Player target,BossAttack attack,int ticks){
        var at=boss.body().getLocation();
        var definition=BossCatalog.get(boss.body().getPersistentDataContainer().get(
            new NamespacedKey("cazajefes","boss_type"),PersistentDataType.STRING));
        effect(boss,attack,"charge",BossParticles.Shape.RING,at,
            Math.max(.85,boss.body().getWidth()*.65),boss.body().getHeight(),true);
        for(Player viewer:at.getWorld().getNearbyPlayers(at,32)){
            if(!ArenaBoundary.allows(boss.body(),viewer.getLocation()))continue;
            String id=definition==null?attack.id():definition.id()+"."+attack.id();
            String name=plugin.languages.attack(viewer,id,attack.name());
            BossWarnings.send(viewer,Component.text("⚠ "+plugin.languages.text(viewer,"attack.loading","CARGANDO")
                +" · "+name+" · "+String.format(Locale.ROOT,"%.1f",ticks/20.0)+" s",NamedTextColor.GOLD));
        }
    }

    @Override public void release(CustomBoss boss,BossAttack attack){
        var at=boss.body().getLocation();
        effect(boss,attack,"release",BossParticles.Shape.BEAM,at,
            Math.max(.8,boss.body().getWidth()*.55),boss.body().getHeight(),false);
    }

    @Override public void impact(CustomBoss boss,BossAttack attack,Location at){
        effect(boss,attack,"impact",BossParticles.Shape.PULSE,at,
            Math.max(1,attack.radius()),1.1,false);
    }

    private void effect(CustomBoss boss,BossAttack attack,String stage,BossParticles.Shape fallback,
                        Location at,double radius,double height,boolean warning){
        var id=boss.body().getPersistentDataContainer().get(new NamespacedKey("cazajefes","boss_type"),PersistentDataType.STRING);
        var definition=BossCatalog.get(id);
        if(definition==null)return;
        var style=CreatureAura.of(definition.entityType());
        var effect=BossParticles.attack(definition,attack.id(),stage,fallback);
        double angle=effect.shape()==BossParticles.Shape.PULSE&&effect.speed()>0?-Math.PI*.5/effect.speed():org.bukkit.Bukkit.getCurrentTick()*.21;
        effect.show(at,radius,height,angle,style.primary,style.accent,warning);
        if(effect.shape()==BossParticles.Shape.PULSE&&effect.speed()>0){
            for(int frame=1;frame<=2;frame++){
                double renderedAngle=angle+frame*Math.PI*.5/effect.speed();
                plugin.getServer().getScheduler().runTaskLater(plugin,()->{
                    if(at.isChunkLoaded())effect.show(at,radius,height,renderedAngle,style.primary,style.accent,warning);
                },frame*3L);
            }
        }
    }

    @Override public void heartbeat(CustomBoss boss){
        var tracker=stages.get(boss.body().getUniqueId());
        if(tracker!=null)tracker.update();
        var body=boss.body();
        var definition=BossCatalog.get(body.getPersistentDataContainer().get(
            new NamespacedKey("cazajefes","boss_type"),PersistentDataType.STRING));
        if(definition!=null&&!body.isInvulnerable()
            &&displacement.tick(body,definition,BossStages.phase(body),System.currentTimeMillis(),
                boss.state()==dev.cazajefes.ai.BossState.ATTACKING||boss.state()==dev.cazajefes.ai.BossState.STUNNED
                    &&!displacement.busy(body.getUniqueId()))){
            boss.stun(6);
        }
        if(boss.body().isInWater())boss.body().setRemainingAir(boss.body().getMaximumAir());
    }

    @Override public BossVisuals visuals(){return visuals;}

    private void equip(LivingEntity body,BossCatalog.Definition definition){
        var config=definition.custom().ai();
        if(config==null||!config.has("equipment")||body.getEquipment()==null)return;
        var items=new java.util.EnumMap<EquipmentSlot,ItemStack>(EquipmentSlot.class);
        try{
            for(var entry:config.getAsJsonObject("equipment").entrySet()){
                EquipmentSlot slot=switch(entry.getKey()){
                    case "mainHand" -> EquipmentSlot.HAND;
                    case "offHand" -> EquipmentSlot.OFF_HAND;
                    case "helmet" -> EquipmentSlot.HEAD;
                    case "chestplate" -> EquipmentSlot.CHEST;
                    case "leggings" -> EquipmentSlot.LEGS;
                    case "boots" -> EquipmentSlot.FEET;
                    default -> throw new IllegalArgumentException("ranura inválida: "+entry.getKey());
                };
                items.put(slot,item(entry.getValue()));
            }
        }catch(RuntimeException invalid){
            plugin.getLogger().warning("Equipo IA "+definition.id()+": "+invalid.getMessage());
            return;
        }
        items.forEach((slot,item)->{
            body.getEquipment().setItem(slot,item,true);
            body.getEquipment().setDropChance(slot,0);
        });
    }

    private static ItemStack item(JsonElement value){
        var detail=value.isJsonObject()?value.getAsJsonObject():null;
        var material=Material.matchMaterial(detail==null?value.getAsString():detail.get("material").getAsString());
        if(material==null||(!material.isItem()&&!material.isAir()))throw new IllegalArgumentException("material inválido");
        var stack=new ItemStack(material);
        if(detail==null||material.isAir())return stack;
        var meta=stack.getItemMeta();
        if(detail.has("unbreakable"))meta.setUnbreakable(detail.get("unbreakable").getAsBoolean());
        if(detail.has("enchantments"))for(var enchant:detail.getAsJsonObject("enchantments").entrySet()){
            var key=NamespacedKey.fromString(enchant.getKey());
            var type=key==null?null:Registry.ENCHANTMENT.get(key);
            int level=enchant.getValue().getAsInt();
            if(type==null||level<1||level>100)throw new IllegalArgumentException("encantamiento inválido: "+enchant.getKey());
            meta.addEnchant(type,level,true);
        }
        stack.setItemMeta(meta);
        return stack;
    }
}
