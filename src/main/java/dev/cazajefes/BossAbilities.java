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
 private final Map<UUID,Long> recovery=new HashMap<>(), transformations=new HashMap<>();
 boolean transforming(LivingEntity boss) {
  UUID id = boss.getUniqueId();
  Long end = transformations.get(id);
  if (end == null) return false;
  if (end > System.currentTimeMillis()) return true;
  transformations.remove(id);
  return false;
 }
 void transition(LivingEntity boss, BossCatalog.Definition definition, int phase) {
  relocating(boss);
  UUID id = boss.getUniqueId();
  long end = System.currentTimeMillis() + 1000;
  transformations.put(id, end);
  recovery.put(id, end);
  if (boss instanceof Mob mob) {
   mob.getPathfinder().stopPathfinding();
   mob.setTarget(null);
  }
  Location at = boss.getLocation();
  CombatVisuals.signatureRing(at, definition, Math.max(2, boss.getWidth()), 20);
  var participants = BossParticipation.participants(boss);
  for (Player player : boss.getWorld().getNearbyPlayers(at, 24)) {
   if (!CombatGuard.eligible(player)) continue;
   player.playSound(at, Sound.BLOCK_BEACON_POWER_SELECT, .75f, phase == 3 ? .65f : 1.1f);
   if (participants.contains(player.getUniqueId())) BossWarnings.send(player, Component.text(
      phase == 3 ? "FORMA FINAL · " + definition.name() : "SEGUNDA FORMA · " + definition.name(), NamedTextColor.LIGHT_PURPLE));
  }
 }
 void rest(LivingEntity boss){recovery.put(boss.getUniqueId(),System.currentTimeMillis()+3000);}
 boolean inRecovery(LivingEntity boss) { return recovering(boss); }
 private boolean recovering(LivingEntity boss){Long end=recovery.get(boss.getUniqueId());if(end==null)return false;if(System.currentTimeMillis()>=end){recovery.remove(boss.getUniqueId());return false;}return true;}
 private final Map<UUID,Tracked> tracked=new HashMap<>();
 private final org.bukkit.scheduler.BukkitTask burstTimer;
 private final Map<UUID,CombatScripts.Rotation> rotations=new HashMap<>();
 private final BossDisplacement displacement;private final AbyssalGaze gaze;
 private final CazaJefesPlugin plugin;private final NamespacedKey readyKey;private final Map<UUID,Cast> casts=new HashMap<>();
 BossAbilities(CazaJefesPlugin plugin){this.plugin=plugin;gaze=new AbyssalGaze(plugin);plugin.getServer().getPluginManager().registerEvents(gaze,plugin);displacement=new BossDisplacement(plugin);readyKey=new NamespacedKey(plugin,"ultimate_ready_at");burstTimer=plugin.getServer().getScheduler().runTaskTimer(plugin,this::tickBursts,5,5);}
 void track(LivingEntity boss,BossCatalog.Definition definition){tracked.put(boss.getUniqueId(),new Tracked(boss,definition));}
 private void tickBursts(){long now=System.currentTimeMillis();for(var it=tracked.entrySet().iterator();it.hasNext();){var entry=it.next();var active=entry.getValue();var boss=active.boss();if(!boss.isValid()||boss.isDead()){plugin.species.remove(entry.getKey());gaze.remove(entry.getKey());displacement.remove(entry.getKey());plugin.cinematics.remove(entry.getKey());ThreatTable.remove(entry.getKey());it.remove();continue;}if(!boss.isInvulnerable()){if(plugin.species.tick(boss,now,!occupied(boss)&&!plugin.pillagers.charging(boss.getUniqueId())&&!plugin.archers.charging(boss.getUniqueId())&&!plugin.mages.busy(boss.getUniqueId())))continue;boolean blocked=(plugin.safety!=null&&plugin.safety.claiming(boss))||recovering(boss)||busy(boss)||displacement.busy(boss.getUniqueId())||plugin.pillagers.charging(boss.getUniqueId())||plugin.archers.charging(boss.getUniqueId())||plugin.mages.busy(boss.getUniqueId());if(!gaze.tick(boss,active.definition(),BossStages.phase(boss),now,blocked))displacement.tick(boss,active.definition(),BossStages.phase(boss),now,(plugin.safety!=null&&plugin.safety.claiming(boss))||recovering(boss)||busy(boss)||plugin.pillagers.charging(boss.getUniqueId())||plugin.archers.charging(boss.getUniqueId())||plugin.mages.busy(boss.getUniqueId()));}}}
 boolean retaliate(LivingEntity boss,long now){return !occupied(boss)&&displacement.retaliate(boss.getUniqueId(),now);}
 void relocating(LivingEntity boss){Cast cast=casts.get(boss.getUniqueId());if(cast!=null)cast.finish(false);displacement.remove(boss.getUniqueId());gaze.remove(boss.getUniqueId());}
 boolean contactAllowed(LivingEntity boss){return !recovering(boss)&&!busy(boss)&&!plugin.species.defending(boss)&&!gaze.busy(boss.getUniqueId())&&!displacement.busy(boss.getUniqueId());}
 boolean casting(LivingEntity boss){return plugin.pillagers.charging(boss.getUniqueId())||plugin.archers.charging(boss.getUniqueId())||plugin.mages.busy(boss.getUniqueId())||plugin.species.defending(boss)||busy(boss)||gaze.busy(boss.getUniqueId())||displacement.busy(boss.getUniqueId());}
 boolean occupied(LivingEntity boss){return recovering(boss)||(plugin.safety!=null&&plugin.safety.claiming(boss))||casting(boss);}
 boolean busy(LivingEntity boss){return casts.containsKey(boss.getUniqueId());}
 static boolean ready(int phase,long now,long ready,boolean busy){return phase==3&&!busy&&now>=ready;}
 boolean tick(LivingEntity boss,BossCatalog.Definition definition,Player target,int phase){
  long now=System.currentTimeMillis();if(plugin.safety!=null&&plugin.safety.claiming(boss))return true;if(plugin.species.defending(boss)||gaze.busy(boss.getUniqueId())||displacement.busy(boss.getUniqueId())||busy(boss)||plugin.pillagers.charging(boss.getUniqueId())||plugin.archers.charging(boss.getUniqueId())||plugin.mages.busy(boss.getUniqueId()))return true;
  if(recovering(boss))return true;
  boolean ultimateReady=ready(phase,now,boss.getPersistentDataContainer().getOrDefault(readyKey,PersistentDataType.LONG,0L),false);
  var skill=rotations.computeIfAbsent(boss.getUniqueId(),id->new CombatScripts.Rotation())
      .next(CombatScripts.rotation(definition,phase),phase,now,ultimateReady);
  if(skill==null)return false;
  begin(boss,definition,target,skill);
  return true;
 }
 void special(LivingEntity boss,BossCatalog.Definition definition,Player target,int variant) {
  if(!occupied(boss)) tick(boss,definition,target,BossStages.phase(boss));
 }
 private void begin(LivingEntity boss,BossCatalog.Definition definition,Player target,CombatScripts.Skill skill){
  if(!boss.isValid()||boss.isDead()||boss.isInvulnerable()||!CombatGuard.eligible(target))return;
  if(skill.ultimate())boss.getPersistentDataContainer().set(readyKey,PersistentDataType.LONG,System.currentTimeMillis()+skill.cooldown()+3000);
  Cast cast=new Cast(boss,definition,target,skill);casts.put(boss.getUniqueId(),cast);cast.runTaskTimer(plugin,0,5);
 }
 private final class Cast extends BukkitRunnable {
  final int prepare, charge;
  final CombatScripts.Skill skill;
  final LivingEntity boss;
  final BossCatalog.Definition definition;
  final BossAbilityCatalog.Ultimate profile;
  final boolean ultimate;
  final Location center, beamOrigin;
  final Player target;
  final double angle;
  final List<Location> marks = new ArrayList<>();
  final AttackPresentation presentation = new AttackPresentation(plugin);
  List<Location> zone = List.of();
  Vector beamDirection;
  final BossStyle style;
  boolean modeled;
  int ticks, pulse, comboAt = -1, endAt = -1;

  Cast(LivingEntity boss, BossCatalog.Definition definition, Player target, CombatScripts.Skill skill) {
   this.boss = boss;
   style = BossStyle.of(boss.getType());
   this.definition = definition;
   this.skill = skill;
   var base = skill.profile() == null ? BossAbilityCatalog.ultimate(definition) : skill.profile();
   profile = skill.profile() == null
      ? new BossAbilityCatalog.Ultimate(base.name(),base.pattern(),base.effect(),base.radius()+2,base.pulses(),base.angle())
      : base;
   prepare = skill.prepare();
   charge = prepare + skill.warning();
   this.target = target;
   ultimate = skill.ultimate();
   center = boss.getLocation();
   beamOrigin = boss.getEyeLocation();
   Vector facing = target.getLocation().toVector().subtract(center.toVector());
   angle = Math.atan2(facing.getZ(), facing.getX()) + profile.angle();
   marks.add(target.getLocation());
   for (Player player : players()) {
    if (player != target && marks.size() < 8 && player.getLocation().distanceSquared(center) <= 144) {
     marks.add(player.getLocation());
    }
   }
   aim();
  }

  List<Player> players() {
   return center.getWorld().getNearbyPlayers(center, 32).stream()
      .filter(p -> CombatGuard.eligible(p) && p.getLocation().distanceSquared(center) <= 1024).toList();
  }

  boolean valid() {
   return boss.isValid() && !boss.isDead() && !boss.isInvulnerable()
      && boss.getWorld().equals(center.getWorld()) && boss.getLocation().distanceSquared(center) < 1600
      && (!ultimate || BossStages.phase(boss) == 3);
  }

  private void aim() {
   if (target.isOnline() && target.getWorld().equals(center.getWorld())) {
    Vector delta = target.getEyeLocation().toVector().subtract(beamOrigin.toVector());
    if (delta.lengthSquared() > .01) {
     beamDirection = delta.normalize();
     Location facing = center.clone().setDirection(beamDirection);
     boss.setRotation(facing.getYaw(), facing.getPitch());
    }
   }
   if (beamDirection == null) beamDirection = center.getDirection();
  }

  @Override public void run() {
   if (!valid()) {
    finish(false);
    return;
   }
   if (endAt >= 0 && ticks >= endAt) { finish(true); return; }
   if (boss instanceof Mob mob) {
    mob.getPathfinder().stopPathfinding();
    if (mob.getTarget() != null) mob.setTarget(null);
   }
   var audience = players();
   if (audience.isEmpty()) {
    finish(false);
    return;
   }
   if (comboAt >= 0) {
    if (ticks >= comboAt) {
     combo();
     finish(true);
     return;
    }
    presentation.clearProp();
    show(audience, "COMBINACIÓN · " + MobCombos.plan(boss.getType()).name(), 1 - (comboAt - ticks) / 60f,
       net.kyori.adventure.bossbar.BossBar.Color.WHITE);
    preview(audience);
   } else if (ticks < charge) {
    boolean preparing = ticks < prepare;
    if (preparing) aim();
    show(audience, profile.pattern() == BossAbilityCatalog.Pattern.CONE && style == BossStyle.EXECUTIONER
       ? preparing ? "ALZA EL HACHA" : "SAL DEL ABANICO ROJO"
       : profile.pattern() == BossAbilityCatalog.Pattern.OBSIDIAN && !preparing ? "¡SAL DE LAS CASILLAS ROJAS!"
       : preparing ? "PREPARACIÓN" : ultimate ? "DEFINITIVO · ¡ESQUIVA!" : "ZONA DE PELIGRO",
       ticks / (float) charge, ultimate ? net.kyori.adventure.bossbar.BossBar.Color.PURPLE
          : net.kyori.adventure.bossbar.BossBar.Color.YELLOW);
    if (ticks == 0) modeled = plugin.cinematics.play(boss, definition, BossStyle.Beat.PREPARE, audience, skill.sounds());
    if (!modeled) presentation.prop(boss, style, ticks / (float) charge, false);
    if (ticks == prepare || ticks == charge - 10) {
     cue(audience, Sound.BLOCK_NOTE_BLOCK_PLING, .65f + ticks / 50f);
    }
    if (ticks == prepare) setZone(false);
    if (ticks == charge - 10) fallingMeteor();
    if (skill.particles()) SpellVisuals.charge(boss, definition, ticks);
    preview(audience);
   } else {
    int sinceCharge = ticks - charge;
    if (sinceCharge % skill.interval() == 0) {
     show(audience, "IMPACTO " + (pulse + 1) + "/" + profile.pulses(), 1,
        net.kyori.adventure.bossbar.BossBar.Color.RED);
     boss.swingMainHand();
     boolean modelImpact = plugin.cinematics.play(boss, definition, BossStyle.Beat.IMPACT, audience, skill.sounds());
     if (!modelImpact) presentation.prop(boss, style, 1, true);
     if (skill.particles()) {
      CinematicFX.impact(boss, style, audience);
      if (profile.pattern() == BossAbilityCatalog.Pattern.CONE) axeWave(audience);
     }
     release();
     if (casts.get(boss.getUniqueId()) != this) return;
     pulse++;
     if (pulse >= profile.pulses()) {
      if (!skill.combo()) { endAt = ticks + 5; ticks += 5; return; }
      comboAt = ticks + 60;
     }
     setZone(comboAt >= 0);
     if (comboAt < 0 && profile.pattern() == BossAbilityCatalog.Pattern.OBSIDIAN)
      show(audience,"LLUVIA · CAMBIA DE CASILLA",.5f,net.kyori.adventure.bossbar.BossBar.Color.YELLOW);
    } else {
     if (sinceCharge % skill.interval() == 5) presentation.clearProp();
     if (sinceCharge % skill.interval() == 10 && profile.pattern() == BossAbilityCatalog.Pattern.CONE)
      show(audience,"SIGUIENTE CORTE · CAMBIA DE LADO",.5f,net.kyori.adventure.bossbar.BossBar.Color.YELLOW);
     if (sinceCharge % skill.interval() == skill.interval() - 10) fallingMeteor();
     preview(audience);
    }
   }
   ticks += 5;
  }

  private void show(List<Player> audience, String state, float progress,
                    net.kyori.adventure.bossbar.BossBar.Color color) {
   Component message = Component.text(state + "  ·  ", NamedTextColor.GOLD).decorate(TextDecoration.BOLD)
      .append(Component.text(profile.name(), NamedTextColor.WHITE));
   presentation.update(audience, message, progress, color);
   if (ticks % 20 == 0 || state.startsWith("IMPACTO")) for (Player player : audience) BossWarnings.send(player, message);
  }

  private void cue(List<Player> audience, Sound sound, float pitch) {
   if (skill.sounds()) for (Player player : audience) player.playSound(center, sound, .65f, pitch);
  }

  private double beamRange() {
   double max = profile.radius() + 5;
   var collision = beamOrigin.getWorld().rayTraceBlocks(beamOrigin, beamDirection, max, FluidCollisionMode.NEVER, true);
   return collision == null ? max : collision.getHitPosition().distance(beamOrigin.toVector());
  }

  private boolean beamHits(Player player, double distance) {
   if (distance <= 0) return false;
   var hitbox = player.getBoundingBox().expand(.28);
   return hitbox.contains(beamOrigin.toVector()) || hitbox.rayTrace(beamOrigin.toVector(), beamDirection, distance) != null;
  }

  boolean hits(Location point, int step) {
   if (Math.abs(point.getY() - center.getY()) > 4) return false;
   if (profile.pattern() == BossAbilityCatalog.Pattern.METEORS && !marks.isEmpty()) {
    return point.distanceSquared(marks.get(step % marks.size())) < 4;
   }
   double strikeAngle = profile.pattern() == BossAbilityCatalog.Pattern.CONE
      ? angle + (step - (profile.pulses() - 1) / 2.0) * .55 : angle;
   return BossAbilityCatalog.hits(profile.pattern(), point.getX()-center.getX(), point.getZ()-center.getZ(),
      step, profile.pulses(), profile.radius(), strikeAngle);
  }

  private void setZone(boolean combo) {
   List<Location> points = new ArrayList<>();
   double radius = profile.radius();
   if (!combo && !skill.dodgeable()) {
    if (target.getWorld() == center.getWorld()) points.add(target.getLocation().add(0,.12,0));
   } else if (!combo && profile.pattern() == BossAbilityCatalog.Pattern.BEAM) {
    double range = beamRange();
    for (double step = .5; step <= range; step += .65) {
     points.add(beamOrigin.clone().add(beamDirection.clone().multiply(step)));
    }
   } else {
    Location origin = !combo && profile.pattern() == BossAbilityCatalog.Pattern.METEORS
       ? marks.get(pulse % marks.size()) : center;
    double extent = !combo && profile.pattern() == BossAbilityCatalog.Pattern.OBSIDIAN ? 10 : origin == center ? radius + 1 : 2;
    for (double x = -extent; x <= extent; x += 1) for (double z = -extent; z <= extent; z += 1) {
     var point = origin.clone().add(x, .08, z);
     if (combo ? Math.hypot(x,z) <= radius && BossAbilityCatalog.hits(BossAbilityCatalog.Pattern.CROSS,
        x,z,0,1,radius,angle+Math.PI/4) : hits(point, pulse)) points.add(point);
    }
   }
   zone = points;
   presentation.mark(zone, combo ? Material.WHITE_STAINED_GLASS
      : ultimate ? Material.PURPLE_STAINED_GLASS : Material.RED_STAINED_GLASS);
  }

  private void preview(List<Player> audience) {
   if (!skill.particles()) return;
   int count = Math.min(36, zone.size());
   var dust = new Particle.DustOptions(comboAt >= 0 ? Color.WHITE : ultimate ? Color.PURPLE : Color.RED, .95f);
   for (int index = 0; index < count; index++) {
    Location point = zone.get(index * zone.size() / count);
    for (Player player : audience) CosmeticFX.to(player, Particle.DUST, point, 1, 0, 0, 0, 0, dust);
   }
  }

  private void axeWave(List<Player> audience) {
   double heading = angle + (pulse - (profile.pulses() - 1) / 2.0) * .55;
   var trail = new Particle.DustTransition(Color.ORANGE,Color.RED,1.3f);
   for (int i = -7; i <= 7; i++) {
    double direction = heading + i * .08;
    var at = center.clone().add(Math.cos(direction)*profile.radius(),.55,Math.sin(direction)*profile.radius());
    for (Player viewer : audience) CosmeticFX.to(viewer,Particle.DUST_COLOR_TRANSITION,at,1,0,0,0,0,trail);
   }
  }

  private void fallingMeteor() {
   if (profile.pattern() == BossAbilityCatalog.Pattern.OBSIDIAN) {
    for (var point : ObsidianRain.centers(pulse)) presentation.obsidian(center.clone().add(point.x(),.25,point.z()));
   }
   if (profile.pattern() == BossAbilityCatalog.Pattern.METEORS) presentation.meteor(marks.get(pulse % marks.size()));
  }
  void release(){
   if (!valid()) return;
   if (skill.summon() && pulse == 0 && CombatGuard.eligible(target) && target.getWorld() == boss.getWorld()) {
    plugin.bosses().summonConfiguredHorde(boss, definition, target);
   }
   if (skill.particles()) CombatVisuals.special(boss,definition);
   boolean beam = profile.pattern() == BossAbilityCatalog.Pattern.BEAM;
   double range = beam ? beamRange() : 0;
   if (skill.particles() && beam) {
    for (double step = .5; step < range; step += .6) {
     CosmeticFX.emit(Particle.END_ROD, beamOrigin.clone().add(beamDirection.clone().multiply(step)), 1);
    }
   } else if (skill.particles()) {
    int samples = Math.min(16, zone.size());
    for (int index=0; index<samples; index++) CosmeticFX.emit(Particle.CRIT, zone.get(index * zone.size() / samples), 2);
   }
   for(Player player:players()){
    boolean inPattern = skill.dodgeable()
       ? beam ? beamHits(player,range) : hits(player.getLocation(),pulse)
       : player.equals(target) && player.getLocation().distanceSquared(center) <= profile.radius() * profile.radius();
    if(!inPattern||player.getLocation().distanceSquared(boss.getLocation())>576||!boss.hasLineOfSight(player))continue;
    if(profile.pattern()==BossAbilityCatalog.Pattern.VORTEX&&pulse<profile.pulses()-1){Vector pull=center.toVector().subtract(player.getLocation().toVector());if(pull.lengthSquared()>.1)player.setVelocity(pull.normalize().multiply(.4).setY(.1));}
    var attack=boss.getAttribute(org.bukkit.attribute.Attribute.ATTACK_DAMAGE);
    double baseline=attack==null?definition.tier().damage:Math.max(definition.tier().damage,attack.getValue());
    double damage = skill.damage() < 0 ? CombatPower.special(baseline,definition.tier().ordinal(),BossStages.phase(boss),ultimate) : skill.damage();
    boolean damaged = damage > 0 && AbilityDamage.hit(player,boss,damage);
    if(damaged&&!player.isDead()&&CombatGuard.eligible(player))BossEffects.applyFrom(boss,player,BossEffects.effect(profile.effect().potion(),ultimate?80:40,0));
   }
  }
  void combo(){if(!valid())return;var plan=MobCombos.plan(boss.getType());SpellVisuals.release(boss,definition,marks);CombatVisuals.signatureRing(center,definition,profile.radius()*.55,24);center.getWorld().playSound(center,Sound.ENTITY_PLAYER_ATTACK_CRIT,.8f,.7f);for(Player player:players()){if(player.getLocation().distanceSquared(center)>profile.radius()*profile.radius()||Math.abs(player.getY()-center.getY())>4||!boss.hasLineOfSight(player))continue;double dx=player.getX()-center.getX(),dz=player.getZ()-center.getZ();if(!BossAbilityCatalog.hits(BossAbilityCatalog.Pattern.CROSS,dx,dz,0,1,profile.radius(),angle+Math.PI/4))continue;var attack=boss.getAttribute(org.bukkit.attribute.Attribute.ATTACK_DAMAGE);double base=attack==null?definition.tier().damage:attack.getValue();if(AbilityDamage.hit(player,boss,MobCombos.damage(base,BossStages.phase(boss)))){BossEffects.applyFrom(boss,player,BossEffects.effect(PotionEffectType.getByName(plan.effect()),60,0));if(plan.flame())player.setFireTicks(Math.max(player.getFireTicks(),40));if(boss.getType()==EntityType.SPIDER||boss.getType()==EntityType.CAVE_SPIDER)BossEffects.applyFrom(boss,player,BossEffects.effect(PotionEffectType.SLOWNESS,40,1));}}}
  void finish(boolean completed) {
   if (!casts.remove(boss.getUniqueId(), this)) return;
   presentation.close();
   if (completed && boss.isValid() && !boss.isDead()) {
    recovery.put(boss.getUniqueId(), System.currentTimeMillis() + skill.recovery() * 50L);
    var audience = players();
    plugin.cinematics.play(boss, definition, BossStyle.Beat.RECOVERY, audience, skill.sounds());
    for (Player player : audience) BossWarnings.send(player, Component.text(
       "RECUPERACIÓN · ¡Contraataca!", NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
   }
   if (!completed) plugin.cinematics.remove(boss.getUniqueId());
   if (ultimate && boss.isValid()) boss.getPersistentDataContainer().set(readyKey, PersistentDataType.LONG,
      System.currentTimeMillis() + skill.cooldown());
   cancel();
  }
 }
 @EventHandler public void death(EntityDeathEvent event){BossEffects.finish(event.getEntity());ThreatTable.remove(event.getEntity().getUniqueId());plugin.cinematics.remove(event.getEntity().getUniqueId());gaze.remove(event.getEntity().getUniqueId());tracked.remove(event.getEntity().getUniqueId());displacement.remove(event.getEntity().getUniqueId());rotations.remove(event.getEntity().getUniqueId());recovery.remove(event.getEntity().getUniqueId());transformations.remove(event.getEntity().getUniqueId());Cast cast=casts.get(event.getEntity().getUniqueId());if(cast!=null)cast.finish(false);}
 @EventHandler public void unload(ChunkUnloadEvent event){for(Entity entity:event.getChunk().getEntities()){plugin.species.remove(entity.getUniqueId());ThreatTable.remove(entity.getUniqueId());plugin.cinematics.remove(entity.getUniqueId());gaze.remove(entity.getUniqueId());tracked.remove(entity.getUniqueId());displacement.remove(entity.getUniqueId());rotations.remove(entity.getUniqueId());recovery.remove(entity.getUniqueId());transformations.remove(entity.getUniqueId());Cast cast=casts.get(entity.getUniqueId());if(cast!=null)cast.finish(false);}}
 void stop(){ThreatTable.clear();gaze.stop();burstTimer.cancel();recovery.clear();transformations.clear();tracked.clear();rotations.clear();BurstVisuals.stop();displacement.clear();for(Cast cast:new ArrayList<>(casts.values()))cast.finish(false);BossWarnings.clear();}
}
