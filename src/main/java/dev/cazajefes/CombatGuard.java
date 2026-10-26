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
    private final java.util.function.Predicate<Player> attackPolicy;
    private final Map<EntityDamageByEntityEvent,String> rejected=new WeakHashMap<>();
    private final NamespacedKey boss, minion, credit, parent, encounter;
    CombatGuard(CazaJefesPlugin plugin){this(plugin,null);}
    CombatGuard(java.util.function.Predicate<Player> policy){this(null,policy);}
    private CombatGuard(CazaJefesPlugin plugin,java.util.function.Predicate<Player> policy){this.plugin=plugin;this.attackPolicy=policy;
        boss=new NamespacedKey("cazajefes",BossKeys.BOSS);minion=new NamespacedKey("cazajefes","boss_minion");credit=new NamespacedKey("cazajefes","last_player_hit");parent=new NamespacedKey("cazajefes","minion_parent");encounter=new NamespacedKey("cazajefes","encounter_id");
    }
    private void deny(EntityDamageByEntityEvent event,String reason){rejected.put(event,reason);event.setCancelled(true);}
    String rejection(EntityDamageByEntityEvent event){return rejected.getOrDefault(event,"cancelación nativa u otro listener");}
    static boolean eligible(Player p) { return p.isOnline() && !p.isDead() && (p.getGameMode()==GameMode.SURVIVAL || p.getGameMode()==GameMode.ADVENTURE); }
    boolean canAttackBoss(Player p) {
        if(attackPolicy!=null)return attackPolicy.test(p);
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
        if(combatant(victim)){
            if(attacker instanceof Player player){
                if(!canAttackBoss(player))deny(event,"modo de juego o permisos");
                else if(!player.getWorld().equals(victim.getWorld()))deny(event,"mundos distintos");
                else if(CombatHitbox.distanceSquared(player.getEyeLocation(),victim)>576)deny(event,"fuera de alcance");
                else if(!CombatHitbox.visible(player,victim))deny(event,"pared entre jugador y cuerpo del boss");
            }else if(!combatant(attacker))deny(event,"atacante no permitido");
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
