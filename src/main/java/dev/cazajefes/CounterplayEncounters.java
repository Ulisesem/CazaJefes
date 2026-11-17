package dev.cazajefes;

import dev.cazajefes.ai.AttackMotion;
import dev.cazajefes.ai.CustomBoss;
import java.util.*;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/** All sequences advance on the boss FSM and own their temporary weak points. */
final class CounterplayEncounters implements Listener {
    private static final NamespacedKey BASE=new NamespacedKey("cazajefes","encounter_base_health");
    private static final NamespacedKey PEAK=new NamespacedKey("cazajefes","encounter_peak_players");

    private static final class Fight {
        final LivingEntity body;
        final EncounterDesign.Rules rules;
        final EncounterDesign.Family family;
        final BossCatalog.Definition definition;
        final double baseMax;
        int players;
        double multiplier=1,appliedMax;
        long ready,vulnerable;
        Sequence sequence;

        Fight(LivingEntity body,BossCatalog.Definition definition,EncounterDesign.Rules rules){
            this.body=body;
            this.definition=definition;
            this.rules=rules;
            family=EncounterDesign.family(definition);
            baseMax=body.getPersistentDataContainer().getOrDefault(BASE,PersistentDataType.DOUBLE,body.getMaxHealth());
            appliedMax=body.getMaxHealth();
            body.getPersistentDataContainer().set(BASE,PersistentDataType.DOUBLE,baseMax);
            ready=Bukkit.getCurrentTick()+140;
        }
    }

    private final CazaJefesPlugin plugin;
    private final Map<UUID,Fight> fights=new HashMap<>();
    CounterplayEncounters(CazaJefesPlugin plugin){this.plugin=plugin;}

    void track(LivingEntity body,BossCatalog.Definition definition){
        try{
            var rules=EncounterDesign.rules(definition.custom().options().encounter());
            if(rules.enabled())fights.put(body.getUniqueId(),new Fight(body,definition,rules));
        }catch(RuntimeException invalid){
            plugin.getLogger().warning("combat.encounter de "+definition.id()+": "+invalid.getMessage());
        }
    }

    void update(LivingEntity body){
        var fight=fights.get(body.getUniqueId());
        if(fight==null||body.isDead()||!body.isValid())return;
        int recorded=BossParticipation.participants(body).size();
        int previous=body.getPersistentDataContainer().getOrDefault(PEAK,PersistentDataType.INTEGER,1);
        int count=Math.clamp(Math.max(previous,recorded),1,fight.rules.maximumPlayers());
        if(count==fight.players)return;
        fight.players=count;
        body.getPersistentDataContainer().set(PEAK,PersistentDataType.INTEGER,count);
        if(fight.rules.scaling()){
            double ratio=body.getHealth()/Math.max(1,body.getMaxHealth());
            fight.multiplier=EncounterDesign.healthMultiplier(count);
            var max=body.getAttribute(Attribute.MAX_HEALTH);
            if(max!=null){
                max.setBaseValue(fight.baseMax*fight.multiplier);
                fight.appliedMax=max.getValue();
                body.setHealth(Math.clamp(ratio*fight.appliedMax,.001,fight.appliedMax));
            }
            announce(fight,"ADAPTACIÓN · "+count+" cazadores · resistencia ×"+fight.multiplier,NamedTextColor.AQUA);
        }
    }

    AttackMotion begin(CustomBoss boss,Player target){
        var fight=fights.get(boss.body().getUniqueId());
        if(fight==null||fight.sequence!=null||Bukkit.getCurrentTick()<fight.ready
            ||boss.body().getLocation().distanceSquared(target.getLocation())>400)return null;
        var sequence=new Sequence(boss,fight,target);
        fight.sequence=sequence;
        fight.ready=Bukkit.getCurrentTick()+fight.rules.cooldown();
        return sequence;
    }

    @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
    public void incoming(EntityDamageByEntityEvent event){
        var fight=fights.get(event.getEntity().getUniqueId());
        if(fight==null||event.getDamage()<=0)return;
        Entity source=event.getDamager();
        if(source instanceof Projectile projectile&&projectile.getShooter() instanceof Entity shooter)source=shooter;
        if(!(source instanceof Player player)||!plugin.guard.canAttackBoss(player)
            ||!ArenaBoundary.allows(fight.body,player.getLocation()))return;
        double factor=fight.rules.scaling()?Math.min(1,fight.appliedMax/(fight.baseMax*fight.multiplier)):1;
        if(Bukkit.getCurrentTick()<fight.vulnerable)factor*=1+fight.rules.vulnerabilityBonus();
        event.setDamage(event.getDamage()*factor);
        SafeDamage.normalize(event);
    }

    private List<Player> audience(Fight fight){
        var ids=BossParticipation.participants(fight.body);
        var focus=plugin.customEncounter.active(fight.body.getUniqueId());
        return fight.body.getWorld().getNearbyPlayers(fight.body.getLocation(),28).stream()
            .filter(p->CombatGuard.eligible(p)&&ArenaBoundary.allows(fight.body,p.getLocation())
                &&(ids.contains(p.getUniqueId())||focus!=null&&p.equals(focus.target()))).toList();
    }

    private void announce(Fight fight,String message,NamedTextColor color){
        for(var viewer:audience(fight))BossWarnings.send(viewer,Component.text(message,color));
    }

    private final class Sequence implements AttackMotion {
        final CustomBoss boss;
        final Fight fight;
        final VoidAnchors anchors=new VoidAnchors();
        final List<Location> marks=new ArrayList<>();
        final Set<UUID> victims=new HashSet<>();
        final int phase,charge,required;
        final double orientation,radius;
        int tick;
        boolean closed,opened;

        Sequence(CustomBoss boss,Fight fight,Player target){
            this.boss=boss;
            this.fight=fight;
            phase=BossStages.phase(fight.body);
            charge=Math.max(40,fight.rules.charge()-(phase-1)*8);
            orientation=((fight.definition.id().hashCode()&255)/256.0)*Math.PI*2;
            radius=phase==1?2.2:phase==2?2.7:3.1;
            marks.add(target.getLocation().clone());
            audience(fight).stream().filter(p->!p.equals(target))
                .sorted(Comparator.comparingDouble(p->p.getLocation().distanceSquared(fight.body.getLocation())))
                .limit(EncounterDesign.targetCount(fight.players)-1)
                .forEach(p->marks.add(p.getLocation().clone()));
            if(fight.rules.weakpoints()){
                var center=fight.body.getLocation();
                double distance=Math.max(1.8,fight.body.getBoundingBox().getWidthX()/2+1.2);
                for(int i=0;i<3;i++){
                    double angle=orientation+i*2*Math.PI/3;
                    Location at=center.clone().add(Math.cos(angle)*distance,.15,Math.sin(angle)*distance);
                    if(!at.isChunkLoaded()||!ArenaBoundary.allows(fight.body,at))continue;
                    if(!at.getBlock().isPassable())continue;
                    anchors.add(at,fight.body.getUniqueId());
                }
            }
            required=Math.min(fight.family.required,anchors.remaining());
            emit(dev.cazajefes.api.event.BossAttackEvent.Stage.CHARGE,fight.body.getLocation());
            if(required>0)announce(fight,fight.family.name+" · "+fight.family.hint,NamedTextColor.GOLD);
            else announce(fight,fight.family.name+" · ¡Sal de las marcas!",NamedTextColor.GOLD);
            fight.body.getWorld().playSound(fight.body.getLocation(),Sound.BLOCK_BEACON_POWER_SELECT,.7f,.8f);
        }

        @Override public boolean mobile(){return false;}

        @Override public boolean tick(){
            if(closed||!fight.body.isValid()||fight.body.isDead())return true;
            if(!opened){
                anchors.update(audience(fight),tick%6==0,"SELLO ROTO · "+fight.family.name);
                if(required>0&&anchors.broken()>=required){
                    opened=true;
                    openCounterattack();
                    return true;
                }
            }
            if(tick<charge){
                if(tick%6==0)preview(0);
                if(tick%20==0)announce(fight,fight.family.name+" · "+Math.max(1,(charge-tick+19)/20)+" s",NamedTextColor.GOLD);
            }else if(tick==charge){
                anchors.close();
                emit(dev.cazajefes.api.event.BossAttackEvent.Stage.RELEASE,fight.body.getLocation());
                hit(0);
                announce(fight,"SEGUNDA OLEADA · "+fight.family.name,NamedTextColor.RED);
            }else if(tick<charge+18){
                if(tick%4==0)preview(1);
            }else if(tick==charge+18){
                hit(1);
                if(victims.isEmpty()){
                    opened=true;
                    openCounterattack();
                }else announce(fight,"RECUPERACIÓN · ¡Acércate y ataca!",NamedTextColor.GREEN);
            }
            return ++tick>=charge+18+fight.rules.vulnerabilityTicks();
        }

        private void openCounterattack(){
            fight.vulnerable=Bukkit.getCurrentTick()+fight.rules.vulnerabilityTicks();
            fight.body.getWorld().playSound(fight.body.getLocation(),Sound.BLOCK_AMETHYST_BLOCK_BREAK,1,1.25f);
            announce(fight,"PUNTO DÉBIL · +"+Math.round(fight.rules.vulnerabilityBonus()*100)+" % de daño · "
                +fight.rules.vulnerabilityTicks()/20.0+" s",NamedTextColor.GREEN);
            // The AI resumes only after the advertised counterattack window.
            plugin.getServer().getScheduler().runTask(plugin,()->{
                if(fight.body.isValid()&&!fight.body.isDead()&&plugin.customEncounter.active(fight.body.getUniqueId())==boss)
                    boss.holdSpecial(fight.rules.vulnerabilityTicks());
            });
        }

        private Location center(int index,int step){
            var at=marks.get(index).clone();
            if(step==1&&(fight.family==EncounterDesign.Family.HUNTER||fight.family==EncounterDesign.Family.SWARM))
                at.add(Math.cos(orientation)*2,0,Math.sin(orientation)*2);
            return at;
        }

        private void preview(int step){
            var style=CreatureAura.of(fight.definition.entityType());
            var dust=new Particle.DustTransition(step==0?style.primary:Color.RED,style.accent,.85f);
            var viewers=audience(fight);
            for(int mark=0;mark<marks.size();mark++){
                var center=center(mark,step);
                for(int i=0;i<20;i++){
                    double angle=orientation+i*Math.PI/10;
                    double x=Math.cos(angle)*radius,z=Math.sin(angle)*radius;
                    if(fight.family==EncounterDesign.Family.ARCHER){
                        x=i<10?-1.1:1.1;
                        z=-radius+(i%10)*2*radius/9;
                    }else if(fight.family==EncounterDesign.Family.VOID){
                        x=i<10?0:-radius+(i-10)*2*radius/9;
                        z=i<10?-radius+i*2*radius/9:0;
                    }else if(fight.family==EncounterDesign.Family.BRUISER){
                        double arc=-Math.PI/2+i*Math.PI/19;
                        x=Math.sin(arc)*radius;
                        z=Math.cos(arc)*radius*(step==0?1:-1);
                    }
                    var offset=new Vector(x,.1,z).rotateAroundY(orientation);
                    var at=center.clone().add(offset);
                    if(!at.isChunkLoaded()||!ArenaBoundary.allows(fight.body,at))continue;
                    for(var viewer:viewers)CosmeticFX.warning(viewer,at,null,dust);
                }
            }
        }

        private void hit(int step){
            var struck=new HashSet<UUID>();
            var style=CreatureAura.of(fight.definition.entityType());
            for(int mark=0;mark<marks.size();mark++){
                var center=center(mark,step);
                if(!center.isChunkLoaded()||!ArenaBoundary.allows(fight.body,center))continue;
                emit(dev.cazajefes.api.event.BossAttackEvent.Stage.IMPACT,center);
                CosmeticFX.emit(style.particle,center.clone().add(0,.4,0),12,.5,.35,.5,.04);
                center.getWorld().playSound(center,step==0?Sound.ENTITY_PLAYER_ATTACK_SWEEP:Sound.ENTITY_WARDEN_SONIC_BOOM,.7f,1.3f);
                for(var player:center.getWorld().getNearbyPlayers(center,radius+1)){
                    if(struck.contains(player.getUniqueId())||!boss.environment().mayHit(boss,player)||!boss.environment().canSee(boss,player)
                        ||Math.abs(player.getY()-center.getY())>2.5)continue;
                    var delta=player.getLocation().toVector().subtract(center.toVector()).rotateAroundY(-orientation);
                    if(!EncounterDesign.shape(fight.family,delta.getX(),delta.getZ(),step,radius))continue;
                    struck.add(player.getUniqueId());
                    double damage=fight.definition.custom().damage()*(step==0?.7:1)*(1+(phase-1)*.05);
                    double before=player.getHealth()+player.getAbsorptionAmount();
                    boss.environment().damage(boss,player,damage);
                    if(player.getHealth()+player.getAbsorptionAmount()>=before)continue;
                    victims.add(player.getUniqueId());
                    if(player.isDead())continue;
                    var effect=switch(fight.family){
                        case HUNTER,SWARM -> PotionEffectType.POISON;
                        case CASTER,VOID -> PotionEffectType.WEAKNESS;
                        case AQUATIC -> PotionEffectType.MINING_FATIGUE;
                        default -> PotionEffectType.SLOWNESS;
                    };
                    BossEffects.applyFrom(fight.body,player,BossEffects.effect(effect,60,0));
                    if(fight.family==EncounterDesign.Family.AQUATIC){
                        Vector pull=fight.body.getLocation().toVector().subtract(player.getLocation().toVector());
                        if(pull.lengthSquared()>.01)player.setVelocity(pull.normalize().multiply(.6).setY(.12));
                    }
                }
            }
        }

        private void emit(dev.cazajefes.api.event.BossAttackEvent.Stage stage,Location at){
            plugin.getServer().getPluginManager().callEvent(new dev.cazajefes.api.event.BossAttackEvent(
                fight.body.getUniqueId(),boss.target()==null?null:boss.target().getUniqueId(),"encounter_combo",stage,at));
        }

        @Override public void close(){
            if(closed)return;
            closed=true;
            anchors.close();
            if(fight.sequence==this)fight.sequence=null;
        }
    }

    @EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true) public void unload(org.bukkit.event.world.ChunkUnloadEvent event){
        for(var entity:event.getChunk().getEntities())remove(entity.getUniqueId());
    }

    @EventHandler public void removed(org.bukkit.event.entity.EntityRemoveEvent event){
        remove(event.getEntity().getUniqueId());
    }

    void remove(UUID id){
        var fight=fights.remove(id);
        if(fight!=null&&fight.sequence!=null)fight.sequence.close();
    }

    void stop(){for(var id:new ArrayList<>(fights.keySet()))remove(id);}
}
