package dev.cazajefes;

import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.*;
import org.bukkit.util.Vector;

/** Accepted-hit PvP powers, strongest armor level, per-power cooldowns and bounded transient state. */
final class JailRares implements Listener {
 record Proc(JailRareCatalog.Spec spec,int level){}
 record Pair(UUID owner,UUID target){}
 record Stack(int count,long until){}
 record Mark(int level,long until){}
 record Plan(Player attacker,LivingEntity victim,Player defender,LivingEntity source,List<Proc> offense,List<Proc> defense,ItemStack weapon,boolean melee,boolean focus){}
 static boolean secondary;
 private final CazaJefesPlugin plugin;
 private final RelicItems items;
 private final Map<EntityDamageEvent,Plan> pending=new WeakHashMap<>();
 private final Map<String,Long> cooldowns=new HashMap<>();
 private final Map<Pair,Stack> combos=new HashMap<>(),received=new HashMap<>();
 private final Map<UUID,Mark> anchor=new HashMap<>(),focus=new HashMap<>(),hex=new HashMap<>();
 private final Map<EntityRegainHealthEvent,Double> initialHealing=new WeakHashMap<>();
 private final Set<org.bukkit.scheduler.BukkitTask> tasks=new HashSet<>();
 private final org.bukkit.scheduler.BukkitTask cleaner;
 JailRares(CazaJefesPlugin plugin,RelicItems items){this.plugin=plugin;this.items=items;cleaner=plugin.getServer().getScheduler().runTaskTimer(plugin,this::prune,20,20);}
 private long now(){return System.currentTimeMillis();}
 private String key(Player owner,String id){return owner.getUniqueId()+":"+id;}
 private boolean ready(Player owner,String id){String key=key(owner,id);return (cooldowns.containsKey(key)||cooldowns.size()<8192)&&now()>=cooldowns.getOrDefault(key,0L);}
 private void spend(Player owner,JailRareCatalog.Spec spec){cooldowns.put(key(owner,spec.id()),now()+spec.cooldown()*1000L);}
 private Map<String,Integer> armor(Player player){Map<String,Integer> out=new HashMap<>();for(ItemStack piece:player.getInventory().getArmorContents())for(var entry:items.activeProperties(piece).entrySet()){var spec=JailRareCatalog.get(entry.getKey());if(spec!=null&&spec.group()==RareCompatibility.Group.ARMOR)out.merge(entry.getKey(),entry.getValue(),Math::max);}return out;}
 private boolean allowed(Player owner,LivingEntity target){return !target.equals(owner)&&target.getWorld().equals(owner.getWorld())&&(!(target instanceof Player player)||RareCombat.enemy(owner,player));}
 private int count(Map<Pair,Stack> map,Pair pair){Stack stack=map.get(pair);return stack!=null&&stack.until()>now()?stack.count():0;}
 private boolean rolling(JailRareCatalog.Spec spec,int level){return spec.effect().equals("combo")||spec.effect().equals("breaker")||Math.random()<JailRareCatalog.chance(level);}
 static boolean low(double health,double maximum){return maximum>0&&health/maximum<.35;}
 @EventHandler(priority=EventPriority.HIGH,ignoreCancelled=true)
 public void prepare(EntityDamageEvent event){
  if(!RareCombat.accepted(false,event.getFinalDamage())||!(event.getEntity() instanceof LivingEntity victim))return;
  Entity damager=event instanceof EntityDamageByEntityEvent hit?hit.getDamager():null;
  Entity source=damager instanceof Projectile shot&&shot.getShooter() instanceof Entity owner?owner:damager;
  Player attacker=source instanceof Player player&&CombatGuard.eligible(player)?player:null;
  Player defender=victim instanceof Player player&&CombatGuard.eligible(player)?player:null;
  if(attacker!=null&&!allowed(attacker,victim))return;
  LivingEntity livingSource=source instanceof LivingEntity living?living:null;
  boolean melee=damager instanceof LivingEntity&&!secondary&&!RangedRares.secondary;
  List<Proc> defense=new ArrayList<>(),offense=new ArrayList<>();double reduction=1,multiplier=1,extra=0;
  if(defender!=null)for(var entry:armor(defender).entrySet()){
   var spec=JailRareCatalog.get(entry.getKey());int n=entry.getValue();if(!ready(defender,spec.id()))continue;
   boolean eligible=switch(spec.effect()){
    case "heal_low","speed_low"->low(Math.max(.01,defender.getHealth()-event.getFinalDamage()),defender.getMaxHealth());
    case "cleanse"->defender.hasPotionEffect(PotionEffectType.POISON)||defender.hasPotionEffect(PotionEffectType.WITHER);
    case "repel"->livingSource!=null&&melee;
    case "fire_guard"->Set.of(EntityDamageEvent.DamageCause.FIRE,EntityDamageEvent.DamageCause.FIRE_TICK,EntityDamageEvent.DamageCause.LAVA,EntityDamageEvent.DamageCause.HOT_FLOOR).contains(event.getCause());
    case "focus"->damager instanceof Projectile||RangedRares.secondary;
    case "antidote"->false;
    default->true;};
   if(!eligible||!rolling(spec,n))continue;defense.add(new Proc(spec,n));if(spec.effect().equals("guard"))reduction*=1-(.03+.03*n);if(spec.effect().equals("fire_guard"))reduction*=1-(.05+.15*n);
  }
  ItemStack weapon=attacker==null?null:attacker.getInventory().getItemInMainHand().clone();boolean focusHit=false;
  if(attacker!=null&&melee&&RareCompatibility.material(weapon.getType())==RareCompatibility.Group.MELEE){
   Mark mark=focus.get(attacker.getUniqueId());if(mark!=null&&mark.until()>now()&&armor(attacker).getOrDefault("voto_cazador",0)>0){focusHit=true;multiplier+=.05+.05*mark.level();}
   for(var entry:items.activeProperties(weapon).entrySet()){
    var spec=JailRareCatalog.get(entry.getKey());if(spec==null||spec.group()!=RareCompatibility.Group.MELEE||!ready(attacker,spec.id()))continue;int n=entry.getValue();Pair pair=new Pair(attacker.getUniqueId(),victim.getUniqueId());
    boolean eligible=switch(spec.effect()){
     case "combo"->count(combos,pair)>=2;
     case "breaker"->count(received,pair)>=3;
     case "pursuit"->victim instanceof Player p?p.isSprinting():victim.getVelocity().clone().setY(0).lengthSquared()>.04;
     case "venom"->victim.hasPotionEffect(PotionEffectType.POISON);
     case "spectral"->victim.hasPotionEffect(PotionEffectType.INVISIBILITY)||victim.hasPotionEffect(PotionEffectType.GLOWING);
     case "pull"->attacker.getLocation().distanceSquared(victim.getLocation())>4;
     case "harvest"->victim.getHealth()/victim.getMaxHealth()<.4;
     case "sky"->!victim.isOnGround()&&victim.getLocation().getY()>attacker.getLocation().getY()+.3;
     case "fury"->low(attacker.getHealth(),attacker.getMaxHealth());
     case "food"->attacker.getFoodLevel()<14;
     case "repair_weapon"->weapon.getItemMeta() instanceof Damageable d&&d.getDamage()>0;
     default->true;};
    if(!eligible||!rolling(spec,n))continue;offense.add(new Proc(spec,n));switch(spec.effect()){
     case "combo"->extra+=n;
     case "pursuit"->multiplier+=.05*n;
     case "venom","spectral"->multiplier+=.05+.05*n;
     case "sky","fury","scorch"->{if(!spec.effect().equals("scorch")||victim.getFireTicks()>0)multiplier+=.04+.04*n;else offense.remove(offense.size()-1);}
     default->{}
    }
   }
  }
  if(multiplier>1)SafeDamage.scale(event,JailRareCatalog.outgoingCap(multiplier));
  if(extra>0)event.setDamage(EntityDamageEvent.DamageModifier.BASE,event.getDamage()+Math.min(3,extra));
  if(reduction<1)SafeDamage.scale(event,JailRareCatalog.defensiveCap(reduction));
  if(defender!=null||attacker!=null)pending.put(event,new Plan(attacker,victim,defender,livingSource,List.copyOf(offense),List.copyOf(defense),weapon,melee,focusHit));
 }
 @EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=false)
 public void commit(EntityDamageEvent event){Plan plan=pending.remove(event);if(plan==null||!RareCombat.accepted(event.isCancelled(),event.getFinalDamage()))return;
  if(plan.attacker()!=null&&plan.melee()){
   Pair pair=new Pair(plan.attacker().getUniqueId(),plan.victim().getUniqueId());if(items.activePropertyLevel(plan.weapon(),"combo_ruptura")>0&&combos.size()<4096)combos.put(pair,new Stack(count(combos,pair)+1,now()+5000));
   if(plan.focus())focus.remove(plan.attacker().getUniqueId());
  }
  if(plan.defender()!=null&&plan.source()!=null&&plan.melee()&&items.activePropertyLevel(plan.defender().getInventory().getItemInMainHand(),"ruptura_combo")>0&&received.size()<4096){Pair pair=new Pair(plan.defender().getUniqueId(),plan.source().getUniqueId());received.put(pair,new Stack(count(received,pair)+1,now()+5000));}
  for(Proc proc:plan.offense()){spend(plan.attacker(),proc.spec());if(proc.spec().effect().equals("combo"))combos.remove(new Pair(plan.attacker().getUniqueId(),plan.victim().getUniqueId()));if(proc.spec().effect().equals("breaker"))received.remove(new Pair(plan.attacker().getUniqueId(),plan.victim().getUniqueId()));}
  for(Proc proc:plan.defense()){spend(plan.defender(),proc.spec());if(proc.spec().effect().equals("anchor")&&anchor.size()<4096)anchor.put(plan.defender().getUniqueId(),new Mark(proc.level(),now()+750));if(proc.spec().effect().equals("focus")&&focus.size()<4096)focus.put(plan.defender().getUniqueId(),new Mark(proc.level(),now()+4000));}
  if(plan.focus()&&plan.offense().isEmpty())plan.attacker().sendMessage("§6Voto del Centinela §7» Contraataque activado.");
  if(!plan.offense().isEmpty()||!plan.defense().isEmpty())later(()->activate(plan));
 }
 private void activate(Plan plan){List<String> offensive=new ArrayList<>(),defensive=new ArrayList<>();Player owner=plan.attacker(),defender=plan.defender();LivingEntity target=plan.victim();
  if(owner!=null&&owner.isOnline()&&!owner.isDead())for(Proc proc:plan.offense())if(applyOffense(owner,target,proc,plan.weapon())){offensive.add(proc.spec().title());spark(owner);}
  if(defender!=null&&defender.isOnline()&&!defender.isDead())for(Proc proc:plan.defense())if(applyDefense(defender,plan.source(),proc)){defensive.add(proc.spec().title());spark(defender);}
  if(owner!=null&&!offensive.isEmpty()){message(owner,offensive);if(target instanceof Player victim&&victim.isOnline())victim.sendMessage("§c§lRELIQUIAS §8» §f"+owner.getName()+" §7activó: "+String.join(" · ",offensive));}
  if(defender!=null&&!defensive.isEmpty())message(defender,defensive);
 }
 private boolean applyOffense(Player owner,LivingEntity target,Proc proc,ItemStack weapon){int n=proc.level();String effect=proc.spec().effect();if(effect.equals("harvest"))return heal(owner,n);if(effect.equals("food")){owner.setFoodLevel(Math.min(20,owner.getFoodLevel()+n));return true;}if(effect.equals("repair_weapon")){ItemStack current=owner.getInventory().getItemInMainHand();if(sameWeapon(current,weapon)&&repair(current,4*n)){owner.getInventory().setItemInMainHand(current);return true;}return false;}if(effect.equals("stride"))return potion(owner,PotionEffectType.SPEED,20*(1+n),0);
  if(!target.isValid()||target.isDead()||!allowed(owner,target))return false;
  return switch(effect){
   case "hex"->{Mark old=hex.get(target.getUniqueId());if(hex.size()>=4096&&old==null)yield false;int level=old!=null&&old.until()>now()?Math.max(n,old.level()):n;hex.put(target.getUniqueId(),new Mark(level,now()+4000));yield true;}
   case "pull"->{Vector delta=owner.getLocation().toVector().subtract(target.getLocation().toVector()).setY(0);if(delta.lengthSquared()>.01)target.setVelocity(delta.normalize().multiply(.3+.12*n).setY(.15));yield true;}
   case "launch"->{Vector v=target.getVelocity();target.setVelocity(v.setY(Math.max(v.getY(),.25+.12*n)));yield true;}
   case "daze"->{boolean weak=potion(target,PotionEffectType.WEAKNESS,20*(1+n),0);boolean nausea=potion(target,PotionEffectType.NAUSEA,40,0);yield weak||nausea;}
   case "track"->potion(target,PotionEffectType.GLOWING,20*(2+n),0);
   case "breaker"->{push(owner,target,.6+.15*n);potion(target,PotionEffectType.WEAKNESS,20*(1+n),0);yield true;}
   default->true;
  };
 }
 private boolean applyDefense(Player player,LivingEntity source,Proc proc){int n=proc.level();return switch(proc.spec().effect()){
  case "heal_low"->heal(player,n);
  case "speed_low"->potion(player,PotionEffectType.SPEED,20*(1+n),1);
  case "repel"->{if(source==null||!source.isValid()||source.isDead()||source instanceof Player attacker&&!RareCombat.enemy(player,attacker))yield false;push(player,source,.35+.1*n);potion(source,PotionEffectType.WEAKNESS,20*(1+n),0);yield true;}
  case "cleanse"->{if(player.hasPotionEffect(PotionEffectType.POISON)){player.removePotionEffect(PotionEffectType.POISON);yield true;}if(player.hasPotionEffect(PotionEffectType.WITHER)){player.removePotionEffect(PotionEffectType.WITHER);yield true;}yield false;}
  case "anchor"->true;
  case "veil"->potion(player,PotionEffectType.INVISIBILITY,20*(1+n),0);
  case "repair_armor"->repairArmor(player,4+4*n);
  case "focus"->true;
  default->true;
 };}
 private boolean heal(Player player,double amount){if(player.getHealth()>=player.getMaxHealth())return false;var heal=new EntityRegainHealthEvent(player,amount,EntityRegainHealthEvent.RegainReason.CUSTOM);Bukkit.getPluginManager().callEvent(heal);if(heal.isCancelled()||!Double.isFinite(heal.getAmount())||heal.getAmount()<=0)return false;player.setHealth(Math.min(player.getMaxHealth(),player.getHealth()+Math.min(amount,heal.getAmount())));return true;}
 private boolean potion(LivingEntity target,PotionEffectType type,int ticks,int amplifier){if(target instanceof Player player&&(type==PotionEffectType.NAUSEA||type==PotionEffectType.BLINDNESS))ticks=RareDefinitions.clarityTicks(ticks,items.armorLevel(player,"clarity"));return target.addPotionEffect(new PotionEffect(type,ticks,amplifier));}
 private void push(Player owner,LivingEntity target,double strength){Vector away=target.getLocation().toVector().subtract(owner.getLocation().toVector()).setY(0);if(away.lengthSquared()>.01)target.setVelocity(away.normalize().multiply(strength).setY(.25));}
 private boolean repair(ItemStack item,int amount){if(item==null||!(item.getItemMeta() instanceof Damageable meta)||meta.getDamage()<=0)return false;meta.setDamage(Math.max(0,meta.getDamage()-amount));item.setItemMeta(meta);return true;}
 private boolean sameWeapon(ItemStack a,ItemStack b){if(a==null||b==null||a.getType()!=b.getType())return false;ItemStack x=a.clone(),y=b.clone();if(x.getItemMeta() instanceof Damageable d){d.setDamage(0);x.setItemMeta(d);}if(y.getItemMeta() instanceof Damageable d){d.setDamage(0);y.setItemMeta(d);}return x.isSimilar(y);}
 private boolean repairArmor(Player owner,int amount){ItemStack[] armor=owner.getInventory().getArmorContents();int slot=-1,damage=0;for(int i=0;i<armor.length;i++)if(armor[i]!=null&&armor[i].getItemMeta() instanceof Damageable d&&d.getDamage()>damage){slot=i;damage=d.getDamage();}if(slot<0||!repair(armor[slot],amount))return false;owner.getInventory().setArmorContents(armor);return true;}
 @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)public void velocity(PlayerVelocityEvent event){Mark mark=anchor.get(event.getPlayer().getUniqueId());if(mark==null||mark.until()<=now()||armor(event.getPlayer()).getOrDefault("ancla_firme",0)==0)return;Vector velocity=event.getVelocity();velocity.setX(velocity.getX()*(.8-.1*mark.level())).setZ(velocity.getZ()*(.8-.1*mark.level()));if(velocity.getY()>0)velocity.setY(velocity.getY()*.8);event.setVelocity(velocity);anchor.remove(event.getPlayer().getUniqueId());}
 @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)public void antidote(EntityPotionEffectEvent event){if(!(event.getEntity() instanceof Player player)||!CombatGuard.eligible(player)||event.getNewEffect()==null||event.getNewEffect().getType()!=PotionEffectType.POISON)return;int n=armor(player).getOrDefault("antidoto_carmesi",0);if(n<=0||!ready(player,"antidoto_carmesi")||Math.random()>=JailRareCatalog.chance(n))return;event.setCancelled(true);spend(player,JailRareCatalog.get("antidoto_carmesi"));message(player,List.of("Antídoto Carmesí (Antidote)"));}
 @EventHandler(priority=EventPriority.LOWEST)public void rememberHealing(EntityRegainHealthEvent event){initialHealing.put(event,event.getAmount());}
 @EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)public void hexHealing(EntityRegainHealthEvent event){Mark mark=hex.get(event.getEntity().getUniqueId());if(mark==null||mark.until()<=now()||!Double.isFinite(event.getAmount())||event.getAmount()<=0)return;double original=initialHealing.getOrDefault(event,event.getAmount());event.setAmount(Math.max(event.getAmount()*(1-.1*mark.level()),Math.min(event.getAmount(),original*.5)));}
 private void spark(Player owner){CosmeticFX.emit(Particle.ENCHANT,owner.getLocation().add(0,1,0),6,.3,.5,.3,0);}
 private void message(Player owner,List<String> names){owner.sendMessage("§6§lRELIQUIAS §8» §f"+String.join(" §8· §f",names));}
 private void later(Runnable action){if(tasks.size()>=512)return;final org.bukkit.scheduler.BukkitTask[] task={null};task[0]=plugin.getServer().getScheduler().runTask(plugin,()->{tasks.remove(task[0]);action.run();});tasks.add(task[0]);}
 private void prune(){long stamp=now();cooldowns.values().removeIf(t->t<=stamp);combos.values().removeIf(s->s.until()<=stamp);received.values().removeIf(s->s.until()<=stamp);anchor.values().removeIf(m->m.until()<=stamp);focus.values().removeIf(m->m.until()<=stamp);hex.values().removeIf(m->m.until()<=stamp);}
 @EventHandler public void quit(PlayerQuitEvent event){UUID id=event.getPlayer().getUniqueId();anchor.remove(id);focus.remove(id);hex.remove(id);combos.keySet().removeIf(p->p.owner().equals(id)||p.target().equals(id));received.keySet().removeIf(p->p.owner().equals(id)||p.target().equals(id));}
 void stop(){cleaner.cancel();for(var task:tasks)task.cancel();tasks.clear();pending.clear();cooldowns.clear();combos.clear();received.clear();anchor.clear();focus.clear();hex.clear();initialHealing.clear();secondary=false;}
}
