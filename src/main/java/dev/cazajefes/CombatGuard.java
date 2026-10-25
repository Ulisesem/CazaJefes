package dev.cazajefes;

import java.util.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.persistence.PersistentDataType;

/** Restricts combat to nearby survival players and prevents unattended loot farming. */
final class CombatGuard implements Listener {
    private final CazaJefesPlugin plugin;
    private final NamespacedKey boss, minion, credit, parent, encounter;
    CombatGuard(CazaJefesPlugin plugin) {
        this.plugin=plugin;
        boss=new NamespacedKey(plugin,BossKeys.BOSS); minion=new NamespacedKey(plugin,"boss_minion");
        credit=new NamespacedKey(plugin,"last_player_hit"); parent=new NamespacedKey(plugin,"minion_parent");
        encounter=new NamespacedKey(plugin,"encounter_id");
    }
    static boolean eligible(Player p) { return p.isOnline() && !p.isDead() && (p.getGameMode()==GameMode.SURVIVAL || p.getGameMode()==GameMode.ADVENTURE); }
    boolean canAttackBoss(Player p) {
        return CombatAccess.canAttackBoss(p.getGameMode(), p.isOnline(), p.isDead(), p.hasPermission("cazajefes.admin"),
            plugin.getConfig().getBoolean("combat.allow-creative-admin-tests", true));
    }
    private boolean combatant(Entity e) { return e.getPersistentDataContainer().has(boss,PersistentDataType.BYTE) || e.getPersistentDataContainer().has(minion,PersistentDataType.BYTE); }
    private Entity source(Entity entity) {
        return entity instanceof Projectile projectile && projectile.getShooter() instanceof Entity shooter ? shooter : entity;
    }
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void restrict(EntityDamageByEntityEvent event) {
        Entity attacker=source(event.getDamager()), victim=event.getEntity();
        if (combatant(victim)) {
            if (attacker instanceof Player player) {
                if (!canAttackBoss(player) || !attacker.getWorld().equals(victim.getWorld()) || attacker.getLocation().distanceSquared(victim.getLocation())>576
                    || !player.hasLineOfSight(victim)) event.setCancelled(true);
            } else if (!combatant(attacker)) event.setCancelled(true);
        }
        if (combatant(attacker) && victim instanceof Player player && (!eligible(player)
            || !attacker.getWorld().equals(victim.getWorld()) || attacker.getLocation().distanceSquared(victim.getLocation())>576
            || attacker instanceof LivingEntity living && !living.hasLineOfSight(victim))) event.setCancelled(true);
    }
    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void credit(EntityDamageByEntityEvent event) { record(event); }
    void record(EntityDamageByEntityEvent event) {
        if (event.getEntity().getPersistentDataContainer().has(boss,PersistentDataType.BYTE)
            && source(event.getDamager()) instanceof Player player && canAttackBoss(player))
            event.getEntity().getPersistentDataContainer().set(credit,PersistentDataType.LONG,System.currentTimeMillis());
    }
    void clearCredit(Entity entity) { entity.getPersistentDataContainer().remove(credit); }
    boolean canDrop(Entity entity) {
        long last=entity.getPersistentDataContainer().getOrDefault(credit,PersistentDataType.LONG,0L);
        long age=System.currentTimeMillis()-last;
        return age>=0 && age<=Math.max(10,Math.min(600,plugin.getConfig().getLong("combat.loot-credit-seconds",120)))*1000;
    }
    @EventHandler(priority=EventPriority.MONITOR)
    public void death(EntityDeathEvent event) {
        String id=event.getEntity().getPersistentDataContainer().get(encounter,PersistentDataType.STRING);
        if (id!=null) for (Entity entity : event.getEntity().getWorld().getEntities()) {
            if (id.equals(entity.getPersistentDataContainer().get(parent,PersistentDataType.STRING))) entity.remove();
        }
    }
    @EventHandler public void load(ChunkLoadEvent event) {
        // Minions are temporary; discard them on chunk reload rather than leave orphan armies.
        for (Entity entity:event.getChunk().getEntities()) if (entity.getPersistentDataContainer().has(minion,PersistentDataType.BYTE)) entity.remove();
    }
    void cleanup() {
        for (World world:Bukkit.getWorlds()) for (Entity entity:world.getEntities()) {
            if (entity.getPersistentDataContainer().has(minion,PersistentDataType.BYTE)) entity.remove();
            if (entity.getPersistentDataContainer().has(boss,PersistentDataType.BYTE) && entity instanceof LivingEntity living) {
                living.setInvulnerable(false); living.setAI(true);
            }
        }
    }
    private boolean protectedEgg(org.bukkit.block.Block block) {
        return plugin.bosses()!=null && plugin.bosses().isEggBlock(block);
    }
    @EventHandler(ignoreCancelled=true)
    public void physics(org.bukkit.event.block.BlockPhysicsEvent event) {
        if (protectedEgg(event.getBlock())) event.setCancelled(true);
    }
    @EventHandler(ignoreCancelled=true)
    public void flow(org.bukkit.event.block.BlockFromToEvent event) {
        if (protectedEgg(event.getBlock()) || protectedEgg(event.getToBlock())) event.setCancelled(true);
    }
    @EventHandler(ignoreCancelled=true)
    public void moveEgg(EntityChangeBlockEvent event) {
        if (protectedEgg(event.getBlock())) event.setCancelled(true);
    }
    @EventHandler(ignoreCancelled=true)
    public void piston(org.bukkit.event.block.BlockPistonExtendEvent event) {
        if (event.getBlocks().stream().anyMatch(b -> protectedEgg(b) || protectedEgg(b.getRelative(org.bukkit.block.BlockFace.UP)))) event.setCancelled(true);
    }
    @EventHandler(ignoreCancelled=true)
    public void retract(org.bukkit.event.block.BlockPistonRetractEvent event) {
        if (event.getBlocks().stream().anyMatch(b -> protectedEgg(b) || protectedEgg(b.getRelative(org.bukkit.block.BlockFace.UP)))) event.setCancelled(true);
    }
    @EventHandler(ignoreCancelled=true)
    public void explode(EntityExplodeEvent event) {
        event.blockList().removeIf(b -> protectedEgg(b) || protectedEgg(b.getRelative(org.bukkit.block.BlockFace.UP)));
    }
    @EventHandler(ignoreCancelled=true)
    public void blockExplode(org.bukkit.event.block.BlockExplodeEvent event) {
        event.blockList().removeIf(b -> protectedEgg(b) || protectedEgg(b.getRelative(org.bukkit.block.BlockFace.UP)));
    }
    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void breakEgg(org.bukkit.event.block.BlockBreakEvent event) {
        if ((protectedEgg(event.getBlock()) || protectedEgg(event.getBlock().getRelative(org.bukkit.block.BlockFace.UP)))
            && !event.getPlayer().hasPermission("cazajefes.admin")) event.setCancelled(true);
    }
    @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
    public void craft(org.bukkit.event.inventory.CraftItemEvent event) {
        if (event.getRecipe() instanceof Keyed keyed && keyed.getKey().getNamespace().equals("cazajefes")
            && !event.getWhoClicked().hasPermission("rareitemhunter.hunter.craft")) event.setCancelled(true);
    }
}
