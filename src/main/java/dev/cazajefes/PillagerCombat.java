package dev.cazajefes;

import java.util.*;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/** Scripted crossbow ammunition avoids unsupported vanilla enchantment combinations. */
final class PillagerCombat implements Listener {
    private static final NamespacedKey BOSS = new NamespacedKey("cazajefes", BossKeys.BOSS);
    private static final NamespacedKey SPECIAL = new NamespacedKey("cazajefes", "crossbow_retribution");
    private static final NamespacedKey ARROW = new NamespacedKey("cazajefes", "raider_arrow");
    private static final String NAME = "Tempestad del Destierro";
    private static final class Raider {
        final Pillager boss; final BossCatalog.Definition definition; final PillagerRules.Hits hits; final PillagerRules.Burst burst=new PillagerRules.Burst();
        UUID focus; long chargeUntil, nextVolley, nextShot;
        Raider(Pillager boss, BossCatalog.Definition definition, int threshold) {
            this.boss = boss; this.definition = definition; hits = new PillagerRules.Hits(threshold);
        }
    }
    private record Shot(Arrow arrow, UUID owner, UUID volley, UUID target, boolean guided, boolean special, double damage, long expires) {}
    private static final class Volley {
        final Set<UUID> hit = new HashSet<>(); final long expires;
        Volley(long expires) { this.expires = expires; }
    }
    private final CazaJefesPlugin plugin;
    private final Map<UUID, Raider> raiders = new HashMap<>();
    private final Map<UUID, Shot> shots = new LinkedHashMap<>();
    private final Map<UUID, Volley> volleys = new HashMap<>();
    private final BukkitTask timer;

    PillagerCombat(CazaJefesPlugin plugin) {
        this.plugin = plugin;
        timer = plugin.getServer().getScheduler().runTaskTimer(plugin, this::tick, 5, 5);
    }
    void track(LivingEntity entity, BossCatalog.Definition definition) {
        if (entity instanceof Pillager pillager) raiders.putIfAbsent(entity.getUniqueId(),
            new Raider(pillager, definition, plugin.getConfig().getInt("combat.pillager-retaliation-hits", 5)));
    }
    boolean charging(UUID id) { Raider state = raiders.get(id); return state != null && (state.chargeUntil > 0 || state.burst.active()); }
    static boolean specialShot(Entity entity) {
        return entity.getPersistentDataContainer().has(SPECIAL, PersistentDataType.BYTE);
    }
    static double arrowRangeSquared(Entity entity){return entity.getPersistentDataContainer().has(ARROW,PersistentDataType.BYTE)||entity.getPersistentDataContainer().has(SkeletonCombat.ARROW,PersistentDataType.BYTE)?2304:576;}
    private Player target(Raider state) {
        Player focus = state.focus == null ? null : plugin.getServer().getPlayer(state.focus);
        if (eligible(state.boss, focus)) return focus;
        return state.boss.getWorld().getPlayers().stream().filter(p -> eligible(state.boss, p))
            .min(Comparator.comparingDouble(p -> p.getLocation().distanceSquared(state.boss.getLocation()))).orElse(null);
    }
    private boolean eligible(Pillager boss, Player player) {
        return player != null && CombatGuard.eligible(player) && player.getWorld().equals(boss.getWorld())
            && player.getLocation().distanceSquared(boss.getLocation()) <= 2304 && boss.hasLineOfSight(player);
    }
    private void tick() {
        long now = System.currentTimeMillis();
        for (Iterator<Shot> it = shots.values().iterator(); it.hasNext();) {
            Shot shot = it.next();
            if (!shot.arrow().isValid() || now >= shot.expires()) { shot.arrow().remove(); it.remove(); }
            else guide(shot);
        }
        volleys.values().removeIf(v -> now >= v.expires);
        for (Raider state : new ArrayList<>(raiders.values())) {
            Pillager boss = state.boss;
            if (!boss.isValid() || boss.isDead()) { remove(boss.getUniqueId()); continue; }
            if (boss.isInvulnerable()) continue;
            Player player = target(state);
            if(state.burst.active()){
                if(player==null){state.burst.cancel();state.nextVolley=now+PillagerRules.COOLDOWN;continue;}
                if(state.burst.take(now)){fire(state,player,true,now);if(!state.burst.active())state.nextVolley=now+PillagerRules.COOLDOWN;}
                continue;
            }
            if (state.chargeUntil > 0) {
                if (now < state.chargeUntil) {
                    if (player != null) BossWarnings.send(player, Component.text("⚠ CARGANDO · " + NAME, NamedTextColor.GOLD));
                    continue;
                }
                state.chargeUntil = 0; boss.setChargingAttack(false);
                state.hits.consume();
                if (player != null){state.burst.start(now);if(state.burst.take(now))fire(state,player,true,now);}else state.nextVolley=now+PillagerRules.COOLDOWN;
                continue;
            }
            if (player == null || plugin.abilities.occupied(boss)) continue;
            if (now >= state.nextVolley && state.hits.charged(now)) {
                state.chargeUntil = now + PillagerRules.CHARGE; boss.setChargingAttack(true);
                boss.getPathfinder().stopPathfinding();
                BossWarnings.send(player, Component.text("⚠ CARGANDO · " + NAME, NamedTextColor.GOLD));
                boss.getWorld().playSound(boss.getLocation(), Sound.ITEM_CROSSBOW_QUICK_CHARGE_3, 1, .8f);
            } else if (now >= state.nextShot) {
                fire(state, player, false, now); state.nextShot = now + 2500;
            }
        }
    }
    private void guide(Shot shot) {
        Arrow arrow = shot.arrow();
        if (!shot.guided() || arrow.isInBlock() || arrow.getTicksLived() > 20) return;
        Player player = plugin.getServer().getPlayer(shot.target());
        Entity owner = plugin.getServer().getEntity(shot.owner());
        if (!(owner instanceof Pillager boss) || !eligible(boss, player)) return;
        var effect = player.getPotionEffect(org.bukkit.potion.PotionEffectType.LEVITATION);
        int level = effect == null ? 0 : effect.getAmplifier() + 1;
        Vector desired = PillagerRules.aim(arrow.getLocation().toVector(), player.getEyeLocation().toVector(),
            player.getVelocity(), PillagerRules.ARROW_SPEED, level, !player.isOnGround());
        Vector current = arrow.getVelocity();
        if (current.lengthSquared() > .001) {
            Vector correction = current.normalize().multiply(.65).add(desired.multiply(.35));
            arrow.setVelocity(correction.normalize().multiply(PillagerRules.ARROW_SPEED));
        }
    }

    private void fire(Raider state, Player player, boolean special, long now) {
        Pillager boss = state.boss;
        if(shots.size()+(special?PillagerRules.VOLLEY_SIZE:1)>512)return;
        var levitation=player.getPotionEffect(org.bukkit.potion.PotionEffectType.LEVITATION);
        Vector direction=PillagerRules.aim(boss.getEyeLocation().toVector(),player.getEyeLocation().toVector(),player.getVelocity(),PillagerRules.ARROW_SPEED,levitation==null?0:levitation.getAmplifier()+1,!player.isOnGround());
        UUID volleyId = UUID.randomUUID();
        if (special) volleys.put(volleyId, new Volley(now + PillagerRules.ARROW_LIFETIME));
        var attribute = boss.getAttribute(Attribute.ATTACK_DAMAGE);
        double baseline = Math.max(state.definition.tier().damage, attribute == null ? 18 : attribute.getValue());
        double damage = special ? Math.min(200,CombatPower.special(baseline, state.definition.tier().ordinal(), BossStages.phase(boss), false)*1.35)
                                : Math.max(6, baseline * .55);
        damage=Math.min(200,damage*3.75); // Scripted Power X: 1 + .25*(10+1).
        final double shotDamage=damage;
        List<Vector> directions = special ? PillagerRules.fan(direction) : List.of(direction.clone().normalize());
        int shotIndex=0;
        for (Vector aim : directions) {
            long live = shots.values().stream().filter(s -> s.owner().equals(boss.getUniqueId())).count();
            if (live >= PillagerRules.MAX_LIVE_ARROWS) {
                Shot oldest = shots.values().stream().filter(s -> s.owner().equals(boss.getUniqueId())).findFirst().orElse(null);
                if (oldest != null) { oldest.arrow().remove(); shots.remove(oldest.arrow().getUniqueId()); }
            }
            Arrow arrow = boss.launchProjectile(Arrow.class, aim.clone().multiply(PillagerRules.ARROW_SPEED), a -> {
                a.getPersistentDataContainer().set(ARROW, PersistentDataType.BYTE, (byte) 1);
                if (special) a.getPersistentDataContainer().set(SPECIAL, PersistentDataType.BYTE, (byte) 1);
                a.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);
                a.setShotFromCrossbow(true); a.setPierceLevel(10); a.setKnockbackStrength(0);
                a.setDamage(shotDamage / PillagerRules.ARROW_SPEED); a.setCritical(false); a.setPersistent(false);
            });
            if (arrow.isValid()) shots.put(arrow.getUniqueId(), new Shot(arrow, boss.getUniqueId(), volleyId, player.getUniqueId(),shotIndex==0,special,damage,now + PillagerRules.ARROW_LIFETIME));
            shotIndex++;
        }
        boss.getWorld().playSound(boss.getLocation(), Sound.ITEM_CROSSBOW_SHOOT, 1.2f, special ? .65f : 1.1f);
        if (special) {
            CombatVisuals.special(boss, state.definition);
            BossWarnings.send(player, Component.text("✦ " + NAME + " · ráfaga "+state.burst.fired()+"/6 · " + state.definition.name(), NamedTextColor.GOLD));
        }
    }
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void hitBoss(EntityDamageByEntityEvent event) {
        Raider state = raiders.get(event.getEntity().getUniqueId());
        if (state == null || event.getFinalDamage() <= 0 || event.getFinalDamage() >= state.boss.getHealth()) return;
        Entity source = event.getDamager();
        if (source instanceof Projectile projectile && projectile.getShooter() instanceof Entity owner) source = owner;
        if (source instanceof Player player && plugin.guard.canAttackBoss(player)) {
            state.focus = player.getUniqueId(); state.hits.add(System.currentTimeMillis());
        }
    }
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void nativeBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Pillager && event.getEntity().getPersistentDataContainer().has(BOSS, PersistentDataType.BYTE)) event.setCancelled(true);
    }
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void nativeArrow(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof AbstractArrow arrow && arrow.getShooter() instanceof Pillager boss
            && boss.getPersistentDataContainer().has(BOSS, PersistentDataType.BYTE)
            && !arrow.getPersistentDataContainer().has(ARROW, PersistentDataType.BYTE)) event.setCancelled(true);
    }
    @EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
    public void arrowDamage(EntityDamageByEntityEvent event) {
        Shot shot = shots.get(event.getDamager().getUniqueId()); if (shot == null) return;
        if (!(event.getEntity() instanceof Player player) || !CombatGuard.eligible(player)) { event.setCancelled(true); return; }
        Volley volley = volleys.get(shot.volley());
        if (volley != null && volley.hit.contains(player.getUniqueId())) { event.setCancelled(true); return; }
        SafeDamage.setBase(event, shot.damage());
    }
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void arrowImpact(EntityDamageByEntityEvent event) {
        Shot shot = shots.get(event.getDamager().getUniqueId());
        if (shot == null || !shot.special() || event.getFinalDamage() <= 0 || !(event.getEntity() instanceof Player player)) return;
        Volley volley = volleys.get(shot.volley()); if (volley != null) volley.hit.add(player.getUniqueId());
        Entity source = plugin.getServer().getEntity(shot.owner());
        if (source == null) return;
        Vector direction = player.getLocation().toVector().subtract(source.getLocation().toVector()).setY(0);
        if (direction.lengthSquared() < .001) direction = new Vector(0,0,1);
        Vector push = direction.normalize().multiply(3.1).setY(.55);
        plugin.getServer().getScheduler().runTask(plugin, () -> { if (CombatGuard.eligible(player)) player.setVelocity(push); });
    }
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void pickup(PlayerPickupArrowEvent event) {
        if (event.getArrow().getPersistentDataContainer().has(ARROW, PersistentDataType.BYTE)) event.setCancelled(true);
    }
    @EventHandler public void death(EntityDeathEvent event) { remove(event.getEntity().getUniqueId()); }
    @EventHandler public void unload(ChunkUnloadEvent event) { for (Entity entity : event.getChunk().getEntities()) remove(entity.getUniqueId()); }
    private void remove(UUID id) {
        Raider state = raiders.remove(id); if (state == null) return; if (state.boss.isValid()) state.boss.setChargingAttack(false);
        for (Iterator<Shot> it = shots.values().iterator(); it.hasNext();) { Shot shot = it.next(); if (shot.owner().equals(id)) { shot.arrow().remove(); it.remove(); } }
    }
    void stop() { timer.cancel(); for (UUID id : new ArrayList<>(raiders.keySet())) remove(id); for (Shot shot : shots.values()) shot.arrow().remove(); shots.clear(); volleys.clear(); }
}
