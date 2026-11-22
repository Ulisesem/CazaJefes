package dev.cazajefes;

import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.*;
import org.bukkit.util.Vector;

/** Weapon-specific combos: snapshot at launch, activate after accepted damage, bounded transient state. */
final class RangedRares implements Listener {
 record Pair(UUID owner,UUID target,String power){}
 record Progress(int count,long until,boolean ranged){}
 record Ready(int level,long until){}
 record Shot(UUID owner,Map<String,Integer> powers,Location origin,double force,long until){}
 record Action(String power,int level,Pair pair,boolean trigger){}
 record Hit(Player owner,LivingEntity target,Shot shot,List<Action> actions,boolean shield,double health){}
 private final CazaJefesPlugin plugin;
 private final RelicItems items;
 private final Map<UUID,Shot> shots=new HashMap<>();
 private final Map<UUID,Boolean> gravityRestore=new HashMap<>();
 private final Map<Pair,Progress> combos=new HashMap<>();
 private final Map<String,Long> cooldowns=new HashMap<>();
 private final Map<UUID,Ready> ruined=new HashMap<>();
 private final Map<UUID,Ready> sidesteps=new HashMap<>();
 private final Map<UUID,Long> moved=new HashMap<>();
 private final Map<EntityDamageByEntityEvent,Hit> pending=new WeakHashMap<>();
 private final Set<org.bukkit.scheduler.BukkitTask> tasks=new HashSet<>();
 private final org.bukkit.scheduler.BukkitTask cleaner;
 static boolean secondary;
 RangedRares(CazaJefesPlugin plugin,RelicItems items){this.plugin=plugin;this.items=items;cleaner=plugin.getServer().getScheduler().runTaskTimer(plugin,this::prune,20,20);}
 static double bonus(int level){return 1+.15*Math.max(1,Math.min(4,level));}
 static boolean ready(long now,long expires){return now>=expires;}
 static boolean fullyCharged(double force){return force>=.95;}
 static boolean alternate(boolean previous,boolean current){return previous!=current;}
 private long now(){return System.currentTimeMillis();}
 private String key(Player p,String id){return p.getUniqueId()+":"+id;}
 private boolean available(Player p,String id){return ready(now(),cooldowns.getOrDefault(key(p,id),0L));}
 private void spend(Player p,String id){cooldowns.put(key(p,id),now()+RareBalance.cooldown(id,RangedRareCatalog.get(id).cooldown())*1000L);}
 private Map<String,Integer> powers(ItemStack weapon){Map<String,Integer> out=new LinkedHashMap<>();for(var entry:items.activeProperties(weapon).entrySet()){var spec=RangedRareCatalog.get(entry.getKey());if(spec!=null&&RangedRareCatalog.compatible(spec,weapon.getType()))out.put(entry.getKey(),entry.getValue());}return Map.copyOf(out);}
 @EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)public void bow(EntityShootBowEvent e){if(!(e.getEntity() instanceof Player p)||!CombatGuard.eligible(p))return;Map<String,Integer> powers=powers(e.getBow());if(!powers.isEmpty()&&shots.size()<1024)shots.put(e.getProjectile().getUniqueId(),new Shot(p.getUniqueId(),powers,p.getLocation().clone(),e.getForce(),now()+60000));}
 @EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)public void trident(ProjectileLaunchEvent e){if(e.getEntity() instanceof Trident t&&t.getShooter() instanceof Player p&&CombatGuard.eligible(p)&&shots.size()<1024){Map<String,Integer> powers=powers(t.getItemStack());if(!powers.isEmpty())shots.put(t.getUniqueId(),new Shot(p.getUniqueId(),powers,p.getLocation().clone(),1,now()+60000));}}
 private Progress progress(Pair key){Progress p=combos.get(key);return p!=null&&p.until()>now()?p:null;}
 static double fractureBonus(int level){return 1+.1*Math.max(1,Math.min(3,level));}
 private boolean enemy(Player owner,LivingEntity target){return !target.equals(owner)&&!target.isDead()&&target.isValid()&&target.getWorld().equals(owner.getWorld())&&(!(target instanceof Player p)||RareCombat.enemy(owner,p));}
 @EventHandler(priority=EventPriority.HIGH,ignoreCancelled=true)public void prepare(EntityDamageByEntityEvent e){if(secondary||JailRares.secondary||!(e.getEntity() instanceof LivingEntity target))return;Shot shot=shots.get(e.getDamager().getUniqueId());Player owner;if(shot!=null){owner=Bukkit.getPlayer(shot.owner());if(shot.until()<=now())return;}else if(e.getDamager() instanceof Player p){owner=p;Map<String,Integer> powers=powers(p.getInventory().getItemInMainHand());shot=new Shot(p.getUniqueId(),powers,p.getLocation().clone(),0,now()+1000);}else return;
  if(owner==null||!CombatGuard.eligible(owner)||!enemy(owner,target))return;boolean shield=false;if(!RareCombat.accepted(e.isCancelled(),e.getFinalDamage()))return;boolean ranged=e.getDamager() instanceof Projectile;List<Action> actions=new ArrayList<>();
  for(var entry:shot.powers().entrySet()){String id=entry.getKey();int n=entry.getValue();if(!available(owner,id))continue;Pair pair=new Pair(owner.getUniqueId(),target.getUniqueId(),id);Progress old=progress(pair);boolean trigger=RangedProcRules.direct(id)?ranged&&RangedProcRules.rolls(id,n,Math.random()):RangedComboRules.trigger(id,old==null?0:old.count(),old!=null&&old.ranged(),ranged,fullyCharged(shot.force()),shot.origin().distanceSquared(target.getLocation()),shield);
   if(trigger&&!RangedProcRules.direct(id)&&Math.random()>=RareBalance.chance(id,n))trigger=false;
   if(trigger&&id.equals("pulso_retorno")&&target instanceof Player victim&&items.armorCounter(victim,"garlic"))trigger=false;
   if(id.equals("ancla_abisal")&&trigger&&e.getFinalDamage()>0)SafeDamage.scale(e,1+RareBalance.damage(id,bonus(n)-1));double extra=trigger?RangedProcRules.extraDamage(id,n):0;if(extra>0)e.setDamage(EntityDamageEvent.DamageModifier.BASE,e.getDamage()+extra);actions.add(new Action(id,n,pair,trigger));}
  pending.put(e,new Hit(owner,target,shot,List.copyOf(actions),shield,target.getHealth()));}
 @EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)public void commit(EntityDamageByEntityEvent e){Hit hit=pending.remove(e);if(hit==null)return;boolean damage=RareCombat.accepted(e.isCancelled(),e.getFinalDamage());boolean ranged=e.getDamager() instanceof Projectile;for(Action a:hit.actions()){if(!damage)continue;String id=a.power();if(id.equals("quebranto_consumir")){combos.remove(a.pair());later(1,()->{if(hit.target().isValid())fx(hit.target().getLocation().add(0,1,0),Particle.CRIT,10);});continue;}if(a.trigger()){if(!available(hit.owner(),id))continue;spend(hit.owner(),id);combos.remove(a.pair());later(1,()->activate(hit,a));}else if(damage){Progress old=progress(a.pair());boolean store=!RangedProcRules.direct(id)&&RangedComboRules.stores(id,ranged,fullyCharged(hit.shot().force()),hit.shot().origin().distanceSquared(hit.target().getLocation()));if(store&&combos.size()<8192)combos.put(a.pair(),new Progress((old==null?0:old.count())+1,now()+(Set.of("cerrojo_vacio","estela_boreal","conductor_tempestades").contains(id)?8000:6000),ranged));}}}
 private void activate(Hit hit,Action action){Player owner=hit.owner();LivingEntity target=hit.target();if(!owner.isOnline()||!CombatGuard.eligible(owner)||!owner.getWorld().equals(target.getWorld()))return;String id=action.power();int n=action.level();if(id.equals("relevo_fantasma")){sidesteps.put(owner.getUniqueId(),new Ready(n,now()+6000));owner.sendMessage("§b§lRelevo Fantasma §7» Puedes desplazarte agachándote durante los próximos 6 s ("+n+"–"+(n+1)+" bloques).");if(owner.isSneaking())sidestep(owner);owner.sendActionBar(net.kyori.adventure.text.Component.text("Relevo Fantasma · agáchate para esquivar",net.kyori.adventure.text.format.NamedTextColor.AQUA));return;}if(id.equals("pulso_retorno")){double lost=Math.max(0,hit.health()-target.getHealth());double amount=Math.min(RareBalance.healing(id,n),lost);if(amount<=0)return;double before=owner.getHealth();var heal=new EntityRegainHealthEvent(owner,amount,EntityRegainHealthEvent.RegainReason.CUSTOM);Bukkit.getPluginManager().callEvent(heal);if(!heal.isCancelled()&&Double.isFinite(heal.getAmount())&&heal.getAmount()>0)owner.setHealth(Math.min(owner.getMaxHealth(),before+Math.min(amount,heal.getAmount())));double recovered=owner.getHealth()-before;notice(owner,target,id,"drenó hasta "+n+" HP · recuperaste "+String.format(java.util.Locale.ROOT,"%.1f",recovered)+" HP");fx(owner.getLocation().add(0,1,0),Particle.HEART,5);return;}if(!enemy(owner,target)){if(target.isDead()&&RangedProcRules.extraDamage(id,n)>0)notice(owner,target,id,"activada en el impacto final");return;}
  switch(id){
    case "fulgor_alba" -> activateFulgorAlba(owner,target,n);
    case "ceniza_solar" -> activateCenizaSolar(owner,target,n);
    case "sello_ruina" -> activateSelloRuina(owner,target,n);
    case "impacto_sismico" -> activateImpactoSismico(owner,target,n);
    case "flecha_silencio" -> activateFlechaSilencio(owner,target,n);
    case "impacto_ariete" -> activateImpactoAriete(owner,target,n);
    case "municion_necrotica" -> activateMunicionNecrotica(owner,target,n);
    case "destello_umbral" -> activateDestelloUmbral(owner,target,n);
    case "grillete_hierro" -> activateGrilleteHierro(owner,target,n);
    case "cerrojo_vacio","ancla_abisal" -> {pull(owner,target,n);notice(owner,target,id,"atrajo al objetivo hacia ti");}
    case "carga_inestable" -> activateCargaInestable(owner,target,n);
    case "marca_cazador" -> activateMarcaCazador(owner,target,n);
    case "ojo_tormenta" -> activateOjoTormenta(owner,target,n);
    case "estela_boreal" -> activateEstelaBoreal(owner,target,n);
    case "marea_ascendente" -> activateMareaAscendente(owner,target,n);
    case "quebranto" -> activateQuebranto(owner,target,n);
    case "conductor_tempestades" -> activateConductorTempestades(owner,target,n);
    default -> fx(target.getLocation().add(0,1,0),Particle.ENCHANT,8);
  }
 }
 /* --- Activators for ranged rares, extracted from collapsed switch --- */
 private void activateFulgorAlba(Player owner,LivingEntity target,int n){
   if(potion(target,"fulgor_alba",PotionEffectType.GLOWING,20*(2+n),0))
     notice(owner,target,"fulgor_alba","reveló la silueta durante "+(2+n)+" s");
   fx(target.getEyeLocation(),Particle.END_ROD,16);
 }
 private void activateCenizaSolar(Player owner,LivingEntity target,int n){
   var burn=new EntityCombustByEntityEvent(owner,target,(float)(RareBalance.ticks("ceniza_solar",20*(2+n))/20.0));
   Bukkit.getPluginManager().callEvent(burn);
   if(!burn.isCancelled()&&!target.isDead()){
     target.setFireTicks(Math.max(target.getFireTicks(),(int)(burn.getDuration()*20)));
     notice(owner,target,"ceniza_solar","aplicó fuego al objetivo");
   }
   fx(target.getEyeLocation(),Particle.FLAME,16);
 }
 private void activateSelloRuina(Player owner,LivingEntity target,int n){
   long until=now()+RareBalance.ticks("sello_ruina",80)*50L;if(!RareCounterplay.curse(target.getUniqueId(),"ruin",until))return;
   Ready prior=ruined.get(target.getUniqueId());
   if(ruined.size()<1024||prior!=null){
     int level=prior!=null&&prior.until()>now()?Math.max(n,prior.level()):n;
     ruined.put(target.getUniqueId(),new Ready(level,until));
     notice(owner,target,"sello_ruina","redujo las curas durante 4 s");
     fx(target.getEyeLocation(),Particle.WITCH,18);
   }
 }
 private void activateImpactoSismico(Player owner,LivingEntity target,int n){
   if(potion(target,"impacto_sismico",PotionEffectType.LEVITATION,10*n,0)){
     Vector launch=target.getVelocity();
     launch.setY(Math.max(launch.getY(),RareBalance.force("impacto_sismico",.3+.1*n)));
     target.setVelocity(launch);
     notice(owner,target,"impacto_sismico","levantó al objetivo brevemente");
   }
   ring(target.getLocation(),1.2,Particle.CLOUD);
 }
 private void activateFlechaSilencio(Player owner,LivingEntity target,int n){
   if(potion(target,"flecha_silencio",PotionEffectType.WEAKNESS,20*(2+n),0))
     notice(owner,target,"flecha_silencio","aplicó Debilidad I durante "+(2+n)+" s");
   fx(target.getEyeLocation(),Particle.ENCHANT,16);
 }
 private void activateImpactoAriete(Player owner,LivingEntity target,int n){
   Vector away=target.getLocation().toVector().subtract(owner.getLocation().toVector()).setY(0);
   if(away.lengthSquared()<.01)away=owner.getLocation().getDirection().setY(0);
   if(away.lengthSquared()>.01)
     target.setVelocity(away.normalize().multiply(RareBalance.force("impacto_ariete",.65+.25*n)).setY(.25));
   ring(target.getLocation(),1.1,Particle.CRIT);
   notice(owner,target,"impacto_ariete","repelió al objetivo");
 }
 private void activateMunicionNecrotica(Player owner,LivingEntity target,int n){
   if(potion(target,"municion_necrotica",PotionEffectType.WITHER,40*n,0))
     notice(owner,target,"municion_necrotica","aplicó Wither I durante "+(2*n)+" s");
   fx(target.getEyeLocation(),Particle.SOUL,16);
 }
 private void activateDestelloUmbral(Player owner,LivingEntity target,int n){
   if(potion(target,"destello_umbral",PotionEffectType.BLINDNESS,20*n,0))
     notice(owner,target,"destello_umbral","aplicó Ceguera durante "+n+" s");
   fx(target.getEyeLocation(),Particle.SQUID_INK,12);
 }
 private void activateGrilleteHierro(Player owner,LivingEntity target,int n){
   if(potion(target,"grillete_hierro",PotionEffectType.SLOWNESS,40,n-1))
     notice(owner,target,"grillete_hierro","aplicó Lentitud "+RareAppearance.roman(n)+" durante 2 s");
   ring(target.getLocation().add(0,.5,0),.9,Particle.ELECTRIC_SPARK);
 }
 private void activateCargaInestable(Player owner,LivingEntity target,int n){
   fx(target.getLocation().add(0,1,0),Particle.EXPLOSION,1);
   target.getWorld().playSound(target.getLocation(),Sound.ENTITY_GENERIC_EXPLODE,.5f,1.5f);
   notice(owner,target,"carga_inestable","añadió "+RangedProcRules.extraDamage("carga_inestable",n)+" HP al impacto");
 }
 private void activateMarcaCazador(Player owner,LivingEntity target,int n){
   potion(target,"marca_cazador",PotionEffectType.WITHER,40*n,0);
   ring(target.getLocation(),1,Particle.SOUL);
   notice(owner,target,"marca_cazador","añadió "+n+" HP al impacto e intentó aplicar Wither I");
 }
 private void activateOjoTormenta(Player owner,LivingEntity target,int n){
   potion(target,"ojo_tormenta",PotionEffectType.SLOWNESS,60,0);
   fx(target.getEyeLocation(),Particle.ELECTRIC_SPARK,24);
   target.getWorld().playSound(target.getLocation(),Sound.ENTITY_LIGHTNING_BOLT_IMPACT,.4f,1.7f);
   notice(owner,target,"ojo_tormenta","añadió "+n+" HP y aplicó descarga ralentizante");
 }
 private void activateEstelaBoreal(Player owner,LivingEntity target,int n){
   if(potion(target,"estela_boreal",PotionEffectType.SLOWNESS,60,n-1))
     notice(owner,target,"estela_boreal","aplicó Lentitud "+RareAppearance.roman(n)+" durante 3 s");
   for(int t:new int[]{0,10,20,30,40})later(t+1,()->{
     if(target.isValid()&&!target.isDead()){
       ring(target.getLocation().add(0,.6,0),1.0,Particle.SNOWFLAKE);
       fx(target.getEyeLocation(),Particle.SNOWFLAKE,10);
     }
   });
 }
 private void activateMareaAscendente(Player owner,LivingEntity target,int n){
   for(LivingEntity other:nearby(owner,target.getLocation(),4)){
     Vector away=other.getLocation().toVector().subtract(target.getLocation().toVector());
     if(away.lengthSquared()<.01)away=owner.getLocation().getDirection();
     other.setVelocity(away.setY(0).normalize().multiply(RareBalance.force("marea_ascendente",.5+.18*n)).setY(.35));
   }
   ring(target.getLocation(),4,Particle.SPLASH);
 }
 private void activateQuebranto(Player owner,LivingEntity target,int n){
   boolean slow=potion(target,"quebranto",PotionEffectType.SLOWNESS,20*(3+n),0);
   boolean weak=potion(target,"quebranto",PotionEffectType.WEAKNESS,20*(3+n),0);
   ring(target.getLocation(),1.2,Particle.CRIT);
   if(slow||weak)
     notice(owner,target,"quebranto","aplicó Lentitud I y Debilidad I durante "+(3+n)+" s");
 }
 private void activateConductorTempestades(Player owner,LivingEntity target,int n){
   for(LivingEntity other:nearby(owner,target.getLocation(),4))
     if(!other.equals(target)&&target.hasLineOfSight(other)){
       fx(other.getLocation().add(0,1,0),Particle.ELECTRIC_SPARK,16);
       damage(owner,other,RareBalance.damage("conductor_tempestades",2*n));
       break;
     }
 }
 private void pull(Player owner,LivingEntity target,int level){Vector v=owner.getLocation().toVector().subtract(target.getLocation().toVector()).setY(0);if(v.lengthSquared()>.1)target.setVelocity(v.normalize().multiply(RareBalance.force("cerrojo_vacio",.3+.15*level)).setY(.2));fx(target.getLocation().add(0,1,0),Particle.REVERSE_PORTAL,12);}
 @EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)public void impact(ProjectileHitEvent e){Shot shot=shots.get(e.getEntity().getUniqueId());if(shot==null||shot.until()<=now()||e.getHitEntity()!=null||e.getHitBlock()==null)return;Player owner=Bukkit.getPlayer(shot.owner());if(owner==null||!CombatGuard.eligible(owner))return;String id=shot.powers().containsKey("detonacion_grieta")?"detonacion_grieta":shot.powers().containsKey("resaca")?"resaca":null;if(id==null||!available(owner,id)||tasks.size()>128)return;int n=shot.powers().get(id);if(Math.random()>=RareBalance.chance(id,n))return;spend(owner,id);Location at=e.getHitBlock().getLocation().add(.5,.5,.5);if(e.getHitBlockFace()!=null)at.add(e.getHitBlockFace().getDirection().multiply(.65));ring(at,3,id.equals("resaca")?Particle.SPLASH:Particle.ELECTRIC_SPARK);
  if(id.equals("detonacion_grieta")){double radius=RangedProcRules.blastRadius(n);for(int tick:new int[]{1,14,27})later(tick,()->{if(valid(owner,at)){ring(at,radius,Particle.ELECTRIC_SPARK);at.getWorld().playSound(at,Sound.BLOCK_AMETHYST_BLOCK_CHIME,.35f,1.5f);}});later(40,()->{if(!valid(owner,at))return;fx(at,Particle.EXPLOSION,1);at.getWorld().playSound(at,Sound.ENTITY_GENERIC_EXPLODE,.7f,1.2f);int affected=0;for(LivingEntity target:nearby(owner,at,radius)){double health=target.getHealth(),absorption=target.getAbsorptionAmount();damage(owner,target,RangedProcRules.blastDamage(n));if(target.getHealth()<health||target.getAbsorptionAmount()<absorption){affected++;notice(owner,target,id,"explosión radial activada");}}if(affected==0)owner.sendMessage("§bDetonación de la Grieta §7» La carga explotó sin dañar objetivos.");});return;}
  for(int t=0;t<3;t++)later(t*20+1,()->{if(valid(owner,at)){ring(at,3,Particle.SPLASH);for(LivingEntity target:nearby(owner,at,3))potion(target,"resaca",PotionEffectType.SLOWNESS,40,n-1);}});}
 @EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)public void sneak(PlayerToggleSneakEvent e){if(e.isSneaking())sidestep(e.getPlayer());}
 private void sidestep(Player p){Ready r=sidesteps.get(p.getUniqueId());if(r==null||r.until()<=now()||!CombatGuard.eligible(p))return;Vector facing=p.getLocation().getDirection().setY(0);if(facing.lengthSquared()<.01)return;Vector side=new Vector(-facing.getZ(),0,facing.getX()).normalize();double distance=RangedProcRules.stepDistance(r.level(),Math.random());Location from=p.getLocation();Location to=RelevoRoute.destination(from,side,distance,at->EncounterSafety.clear(p,at));if(to==null){p.sendActionBar(net.kyori.adventure.text.Component.text("Relevo Fantasma · No hay un destino libre cercano",net.kyori.adventure.text.format.NamedTextColor.AQUA));return;}later(1,()->{if(valid(p,to)&&p.getLocation().distanceSquared(from)<1&&EncounterSafety.clear(p,to)&&p.teleport(to,PlayerTeleportEvent.TeleportCause.PLUGIN)){sidesteps.remove(p.getUniqueId());fx(from.add(0,1,0),Particle.REVERSE_PORTAL,12);p.sendMessage("§b§lRelevo Fantasma §7» Te desplazaste "+String.format(java.util.Locale.ROOT,"%.1f",distance)+" bloques.");}});}
 private void notice(Player owner,LivingEntity target,String id,String detail){String name=target instanceof Player player?player.getName():target.customName()!=null?net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(target.customName()):target.getType().name();owner.sendMessage("§b§l"+RareAppearance.title(id)+" §7» §f"+name+" §7· "+detail);if(target instanceof Player player)player.sendMessage("§c§l"+RareAppearance.title(id)+" §7» §f"+owner.getName()+" §7activó esta rare contra ti.");}
 private boolean valid(Player owner,Location at){return owner.isOnline()&&CombatGuard.eligible(owner)&&owner.getWorld().equals(at.getWorld())&&at.getWorld().isChunkLoaded(at.getBlockX()>>4,at.getBlockZ()>>4);}
 private List<LivingEntity> nearby(Player owner,Location at,double radius){if(!valid(owner,at))return List.of();return at.getWorld().getNearbyEntities(at,radius,radius,radius).stream().filter(e->e instanceof LivingEntity l&&enemy(owner,l)&&l.getLocation().distanceSquared(at)<=radius*radius&&owner.hasLineOfSight(l)&&visibleFrom(at,l)).map(e->(LivingEntity)e).sorted(Comparator.comparingDouble(e->e.getLocation().distanceSquared(at))).limit(8).toList();}
 private boolean visibleFrom(Location at,LivingEntity target){Location origin=at.clone().add(0,.05,0);Vector direction=target.getEyeLocation().toVector().subtract(origin.toVector());double length=direction.length();return length<.01||at.getWorld().rayTraceBlocks(origin,direction.multiply(1/length),length,FluidCollisionMode.NEVER,true)==null;}
 @EventHandler(priority=EventPriority.HIGH,ignoreCancelled=true)public void reduceHealing(EntityRegainHealthEvent event){Ready mark=ruined.get(event.getEntity().getUniqueId());if(mark!=null&&mark.until()>now()&&RareCounterplay.healingBlocked(event.getEntity().getUniqueId(),"ruin")&&Double.isFinite(event.getAmount())&&event.getAmount()>0)event.setAmount(event.getAmount()*RangedProcRules.healingFactor(mark.level()));}
 private boolean potion(LivingEntity target,String id,PotionEffectType type,int ticks,int amplifier){int duration=RareBalance.ticks(id,ticks);if(target instanceof Player player&&(type==PotionEffectType.BLINDNESS||type==PotionEffectType.NAUSEA))duration=RareDefinitions.clarityTicks(duration,items.armorLevel(player,"clarity"));return target.addPotionEffect(new PotionEffect(type,duration,amplifier));}
 private void damage(Player owner,LivingEntity target,double amount){if(!enemy(owner,target))return;secondary=true;try{target.damage(amount,owner);}finally{secondary=false;}}
 private void fx(Location at,Particle particle,int count){CosmeticFX.emit(particle,at,count,.2,.3,.2,0);}
 private void ring(Location at,double radius,Particle particle){for(int i=0;i<24;i++){double angle=i*Math.PI/12;CosmeticFX.emit(particle,at.clone().add(Math.cos(angle)*radius,.15,Math.sin(angle)*radius),1);}}
 private void later(long delay,Runnable action){if(tasks.size()>=256)return;final org.bukkit.scheduler.BukkitTask[] task={null};task[0]=plugin.getServer().getScheduler().runTaskLater(plugin,()->{tasks.remove(task[0]);action.run();},delay);tasks.add(task[0]);}
 private void prune(){long now=now();shots.entrySet().removeIf(entry->{Entity entity=Bukkit.getEntity(entry.getKey());return entry.getValue().until()<=now||entity==null||!entity.isValid()||entity instanceof AbstractArrow arrow&&arrow.isInBlock();});combos.values().removeIf(v->v.until()<=now);cooldowns.values().removeIf(v->v<=now);sidesteps.values().removeIf(v->v.until()<=now);ruined.values().removeIf(v->v.until()<=now);moved.keySet().removeIf(id->{Player p=Bukkit.getPlayer(id);return p==null||!p.isOnline();});}
 @EventHandler public void quit(PlayerQuitEvent e){UUID id=e.getPlayer().getUniqueId();moved.remove(id);sidesteps.remove(id);combos.keySet().removeIf(k->k.owner().equals(id)||k.target().equals(id));shots.values().removeIf(s->s.owner().equals(id));}
 void stop(){for(var entry:gravityRestore.entrySet()){Entity entity=Bukkit.getEntity(entry.getKey());if(entity!=null&&entity.isValid())entity.setGravity(entry.getValue());}gravityRestore.clear();cleaner.cancel();for(var t:tasks)t.cancel();tasks.clear();shots.clear();combos.clear();cooldowns.clear();sidesteps.clear();ruined.clear();moved.clear();pending.clear();secondary=false;}
}
