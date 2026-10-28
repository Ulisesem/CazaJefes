package dev.cazajefes;
import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.*;
final class BossDisplacement {
 private final BossAnchor anchor;
 private final CazaJefesPlugin plugin;
 private static final class State {int phase,pending,last;long lift,repel,release,lastHud;final BurstCadence cadence=new BurstCadence();}
 private final Map<UUID,State> states=new HashMap<>();
 BossDisplacement(CazaJefesPlugin plugin){this.plugin=plugin;anchor=new BossAnchor(plugin);}
 boolean tick(LivingEntity boss,BossCatalog.Definition definition,int phase,long now,boolean otherCast){
  State state=states.computeIfAbsent(boss.getUniqueId(),id->new State());
  if(state.phase!=phase){anchor.remove(boss.getUniqueId());state.phase=phase;state.pending=0;state.cadence.initialize(now);state.lift=state.cadence.next();state.repel=state.lift;}
  List<Player> players=boss.getWorld().getPlayers().stream().filter(p->CombatGuard.eligible(p)&&CombatHitbox.distanceSquared(p.getEyeLocation(),boss)<=144&&CombatHitbox.visible(p,boss)).toList();
  if(state.pending!=0){String name=state.pending==1?StageAttackRules.LIFT:StageAttackRules.REPEL;
   if(now<state.release){if(now>=state.lastHud){warn(players,"⚠ CARGANDO · "+name+" · "+Math.max(1,(state.release-now+999)/1000)+"s");state.lastHud=now+350;}return true;}
   int attack=state.pending;state.last=attack;state.pending=0;state.cadence.released(now);state.lift=state.cadence.next();state.repel=state.lift;
   Location center=boss.getLocation();BurstVisuals.show(plugin,center.clone().add(0,1,0),4);
   boss.getWorld().playSound(center,attack==1?Sound.ENTITY_BREEZE_WIND_BURST:Sound.ENTITY_WARDEN_SONIC_BOOM,1,attack==1?1.3f:.7f);
   for(Player player:players){if(!BurstVisuals.movable(player.isOp()))continue;var delta=player.getLocation().toVector().subtract(center.toVector()).setY(0);if(delta.lengthSquared()<.001)delta=new Vector(0,0,1);delta.normalize();plugin.burstLaunch.launch(player,delta.multiply(attack==1?CombatPower.ASCENT_LATERAL:CombatPower.repel()).setY(attack==1?StageAttackRules.lift(phase):.5));}
   plugin.shulkers.afterBurst(boss);warn(players,"✦ "+name+" · "+definition.name());return true;
  }
  if(anchor.held(boss.getUniqueId()))return true;
  if(!otherCast&&!players.isEmpty()){
   if(now>=state.lift)state.pending=phase>=3&&state.last==1?2:1;
   if(state.pending!=0){anchor.hold(boss,48);state.release=now+1500;state.lastHud=0;boss.getWorld().playSound(boss.getLocation(),Sound.BLOCK_BEACON_POWER_SELECT,.8f,.7f);warn(players,"⚠ CARGANDO · "+(state.pending==1?StageAttackRules.LIFT:StageAttackRules.REPEL));return true;}
   if(now>=state.lastHud&&!BossWarnings.active(players.get(0))){String text=StageAttackRules.LIFT+" · recarga "+Math.max(0,(state.lift-now+999)/1000)+"s";if(phase>=3)text+="  |  "+StageAttackRules.REPEL+" · "+Math.max(0,(state.repel-now+999)/1000)+"s";warn(players,text);state.lastHud=now+1000;}
  }
  return false;
 }
 private void warn(List<Player> players,String text){for(Player player:players)BossWarnings.send(player,Component.text(text,NamedTextColor.WHITE).decorate(TextDecoration.BOLD));}
 boolean retaliate(UUID id,long now){State s=states.get(id);if(s==null||s.pending!=0||anchor.held(id)||now<s.lift)return false;s.lift=now;return true;}
 boolean busy(UUID id){State state=states.get(id);return state!=null&&state.pending!=0||anchor.held(id);}
 void remove(UUID id){anchor.remove(id);states.remove(id);}void clear(){anchor.clear();states.clear();}
}
