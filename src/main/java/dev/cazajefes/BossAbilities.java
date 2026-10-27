package dev.cazajefes;
import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.*;
final class BossAbilities implements Listener {
 static final long SPECIAL_COOLDOWN=10000,ULTIMATE_COOLDOWN=15000;
 private record Tracked(LivingEntity boss,BossCatalog.Definition definition){}
 private final Map<UUID,Tracked> tracked=new HashMap<>();
 private final org.bukkit.scheduler.BukkitTask burstTimer;
 private final Map<UUID,StageAttackRules.Rotation> rotations=new HashMap<>();
 private final BossDisplacement displacement;
 private final CazaJefesPlugin plugin;private final NamespacedKey readyKey;private final Map<UUID,Cast> casts=new HashMap<>();
 BossAbilities(CazaJefesPlugin plugin){this.plugin=plugin;displacement=new BossDisplacement(plugin);readyKey=new NamespacedKey(plugin,"ultimate_ready_at");burstTimer=plugin.getServer().getScheduler().runTaskTimer(plugin,this::tickBursts,5,5);}
 void track(LivingEntity boss,BossCatalog.Definition definition){tracked.put(boss.getUniqueId(),new Tracked(boss,definition));}
 private void tickBursts(){long now=System.currentTimeMillis();for(var it=tracked.entrySet().iterator();it.hasNext();){var entry=it.next();var active=entry.getValue();var boss=active.boss();if(!boss.isValid()||boss.isDead()){displacement.remove(entry.getKey());it.remove();continue;}if(!boss.isInvulnerable())displacement.tick(boss,active.definition(),BossStages.phase(boss),now,busy(boss)||plugin.pillagers.charging(boss.getUniqueId()));}}
 boolean occupied(LivingEntity boss){return busy(boss)||displacement.busy(boss.getUniqueId());}
 boolean busy(LivingEntity boss){return casts.containsKey(boss.getUniqueId());}
 static boolean ready(int phase,long now,long ready,boolean busy){return phase==3&&!busy&&now>=ready;}
 boolean tick(LivingEntity boss,BossCatalog.Definition definition,Player target,int phase){
  long now=System.currentTimeMillis();if(displacement.busy(boss.getUniqueId())||busy(boss)||plugin.pillagers.charging(boss.getUniqueId()))return true;
  boolean ultimateReady=ready(phase,now,boss.getPersistentDataContainer().getOrDefault(readyKey,PersistentDataType.LONG,0L),false);
  int slot=rotations.computeIfAbsent(boss.getUniqueId(),id->new StageAttackRules.Rotation()).select(phase,now,ultimateReady);
  if(slot>=0){begin(boss,definition,target,StageAttackRules.profile(definition,phase,slot),phase>=3&&slot==1);return true;}return false;
 }
 void special(LivingEntity boss,BossCatalog.Definition definition,Player target,int variant){if(busy(boss))return;int phase=BossStages.phase(boss);int slot=Math.floorMod(variant,StageAttackRules.groundCount(phase));begin(boss,definition,target,StageAttackRules.profile(definition,phase,slot),phase>=3&&slot==1);}
 private void begin(LivingEntity boss,BossCatalog.Definition definition,Player target,BossAbilityCatalog.Ultimate profile,boolean ultimate){
  if(!boss.isValid()||boss.isDead()||boss.isInvulnerable()||!CombatGuard.eligible(target))return;
  if(ultimate)boss.getPersistentDataContainer().set(readyKey,PersistentDataType.LONG,System.currentTimeMillis()+ULTIMATE_COOLDOWN+3000);
  Cast cast=new Cast(boss,definition,target,profile,ultimate);casts.put(boss.getUniqueId(),cast);cast.runTaskTimer(plugin,0,5);
 }
 private final class Cast extends BukkitRunnable {
  final LivingEntity boss;final BossCatalog.Definition definition;final BossAbilityCatalog.Ultimate profile;final boolean ultimate;final Location center;final double angle;final List<Location> marks=new ArrayList<>();int ticks,pulse;
  Cast(LivingEntity boss,BossCatalog.Definition definition,Player target,BossAbilityCatalog.Ultimate profile,boolean ultimate){this.boss=boss;this.definition=definition;this.profile=profile;this.ultimate=ultimate;center=boss.getLocation().clone();Vector facing=target.getLocation().toVector().subtract(center.toVector());angle=Math.atan2(facing.getZ(),facing.getX())+profile.angle();for(Player p:players())if(marks.size()<8)marks.add(p.getLocation().clone());if(boss instanceof Mob mob){mob.getPathfinder().stopPathfinding();mob.setTarget(null);}}
  List<Player> players(){return center.getWorld().getPlayers().stream().filter(p->CombatGuard.eligible(p)&&p.getLocation().distanceSquared(center)<=1024).toList();}
  boolean valid(){return boss.isValid()&&!boss.isDead()&&!boss.isInvulnerable()&&boss.getWorld().equals(center.getWorld())&&boss.getLocation().distanceSquared(center)<1600&&(!ultimate||BossStages.phase(boss)==3);}
  @Override public void run(){
   if(!valid()){finish();return;}int charge=ultimate?60:20;
   if(ticks<charge){int seconds=(int)Math.ceil((charge-ticks)/20.0);Component warning=Component.text(ultimate?"⚠ PRECAUCIÓN · CARGANDO ATAQUE FINAL · ":"⚠ ATAQUE ESPECIAL · ",NamedTextColor.GOLD).decorate(TextDecoration.BOLD).append(Component.text(profile.name()+" · "+seconds+"s",NamedTextColor.RED));for(Player p:players())BossWarnings.send(p,warning);preview();if(ticks==0)center.getWorld().playSound(center,Sound.BLOCK_RESPAWN_ANCHOR_CHARGE,1,.7f);ticks+=5;return;}
   if((ticks-charge)%20==0){release();pulse++;if(pulse>=profile.pulses()){finish();return;}}ticks+=5;
  }
  boolean hits(Location point,int step){if(Math.abs(point.getY()-center.getY())>4)return false;if(profile.pattern()==BossAbilityCatalog.Pattern.METEORS&&!marks.isEmpty())return point.distanceSquared(marks.get(step%marks.size()))<4;
   return BossAbilityCatalog.hits(profile.pattern(),point.getX()-center.getX(),point.getZ()-center.getZ(),step,profile.pulses(),profile.radius(),angle);
  }
  void preview(){int count=0;double r=profile.radius();List<Player> viewers=players();for(double x=-r;x<=r&&count<64;x+=1.25)for(double z=-r;z<=r&&count<64;z+=1.25){Location point=center.clone().add(x,.18,z);if(!hits(point,Math.min(profile.pulses()-1,pulse+(ticks>=60?1:0))))continue;count++;for(Player p:viewers)p.spawnParticle(Particle.DUST,point,1,0,0,0,0,new Particle.DustOptions(CombatVisuals.signature(definition),1.4f));}if(profile.pattern()==BossAbilityCatalog.Pattern.METEORS)for(Location mark:marks)CombatVisuals.signatureRing(mark,definition,1.8,12);}
  void release(){
   if(!valid())return;CombatVisuals.special(boss,definition);center.getWorld().playSound(center,switch(profile.pattern()){case RINGS,NOVA->Sound.ENTITY_WARDEN_SONIC_BOOM;case CROSS,CONE,RUSH->Sound.ENTITY_PLAYER_ATTACK_SWEEP;case SPIRAL,VORTEX->Sound.ENTITY_BREEZE_WIND_BURST;case GEYSERS,METEORS->Sound.ENTITY_LIGHTNING_BOLT_THUNDER;default->Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE;},.8f,ultimate?.7f:1.3f);
   for(Player player:players()){
    if(!hits(player.getLocation(),pulse)||player.getLocation().distanceSquared(boss.getLocation())>576||!boss.hasLineOfSight(player))continue;
    if(profile.pattern()==BossAbilityCatalog.Pattern.VORTEX&&pulse<profile.pulses()-1){Vector pull=center.toVector().subtract(player.getLocation().toVector());if(pull.lengthSquared()>.1)player.setVelocity(pull.normalize().multiply(.4).setY(.1));}
    var attack=boss.getAttribute(org.bukkit.attribute.Attribute.ATTACK_DAMAGE);
    double baseline=attack==null?definition.tier().damage:Math.max(definition.tier().damage,attack.getValue());
    boolean damaged=AbilityDamage.hit(player,boss,CombatPower.special(baseline,definition.tier().ordinal(),BossStages.phase(boss),ultimate));
    if(damaged&&!player.isDead()&&CombatGuard.eligible(player))player.addPotionEffect(new PotionEffect(profile.effect().potion(),ultimate?80:40,0));
   }
   if(pulse+1<profile.pulses())preview();
  }
  void finish(){casts.remove(boss.getUniqueId(),this);if(ultimate&&boss.isValid())boss.getPersistentDataContainer().set(readyKey,PersistentDataType.LONG,System.currentTimeMillis()+ULTIMATE_COOLDOWN);cancel();}
 }
 @EventHandler public void death(EntityDeathEvent event){tracked.remove(event.getEntity().getUniqueId());displacement.remove(event.getEntity().getUniqueId());rotations.remove(event.getEntity().getUniqueId());Cast cast=casts.get(event.getEntity().getUniqueId());if(cast!=null)cast.finish();}
 @EventHandler public void unload(ChunkUnloadEvent event){for(Entity entity:event.getChunk().getEntities()){tracked.remove(entity.getUniqueId());displacement.remove(entity.getUniqueId());rotations.remove(entity.getUniqueId());Cast cast=casts.get(entity.getUniqueId());if(cast!=null)cast.finish();}}
 void stop(){burstTimer.cancel();tracked.clear();rotations.clear();BurstVisuals.stop();displacement.clear();for(Cast cast:new ArrayList<>(casts.values()))cast.finish();BossWarnings.clear();}
}
