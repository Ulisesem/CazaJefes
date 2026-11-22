package dev.cazajefes.ai;

import dev.cazajefes.ai.visual.BossVisuals;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface BossEnvironment {
    default boolean pursueFlyingPlayers(CustomBoss boss){return false;}
    default double climbSpeed(CustomBoss boss){return .35;}
    default double climbRange(CustomBoss boss){return 6;}
    default boolean waterWalking(CustomBoss boss){return !boss.profile().aquatic();}
    /** A temporary attack owns the main hand until its weapon cycle completes. */
    default boolean weaponLocked(CustomBoss boss){return false;}
    Player acquire(CustomBoss boss);
    boolean validTarget(CustomBoss boss, Player player);
    boolean mayMove(CustomBoss boss, Location destination);
    boolean mayHit(CustomBoss boss, LivingEntity entity);
    void damage(CustomBoss boss, LivingEntity victim, double damage);
    void warning(CustomBoss boss, Player player, BossAttack attack, int ticks);
    default boolean canSee(CustomBoss boss,Player target){return boss.body().hasLineOfSight(target);}
    default void attackHit(CustomBoss boss,Player target,BossAttack attack) {}
    default void projectileHit(CustomBoss boss,LivingEntity target,BossVisuals.Projectile kind) {}
    default int phase(CustomBoss boss){return 1;}
    default void lightning(CustomBoss boss,Location at) { visuals().explosion(at); }
    default void dislodge(CustomBoss boss,Player target) {}
    default void release(CustomBoss boss,BossAttack attack) {}
    default void impact(CustomBoss boss,BossAttack attack,Location at) { visuals().explosion(at); }
    default void heartbeat(CustomBoss boss) {}
    default AttackMotion signature(CustomBoss boss,Player target){return null;}
    default boolean occupied(CustomBoss boss){return false;}
    default boolean blocked(CustomBoss boss,Player target,Location attempted) {return false;}
    default void groundWarning(CustomBoss boss,Location center,double radius,int age) {}

    /** Check if the boss may affect the given entity (arena boundary, eligibility, etc.). Default delegates to mayHit. */
    default boolean mayAffect(CustomBoss boss, LivingEntity entity) { return mayHit(boss, entity); }

    /** Apply melee damage. Default delegates to damage(). */
    default void meleeDamage(CustomBoss boss, LivingEntity victim, double damage) { damage(boss, victim, damage); }

    /** Apply meteor (fixed) damage that bypasses normal damage events. Default delegates to damage(). */
    default void meteorDamage(CustomBoss boss, LivingEntity victim, double damage) { damage(boss, victim, damage); }

    /** Apply precision (fixed) damage. Default delegates to damage(). */
    default void precisionDamage(CustomBoss boss, LivingEntity victim, double damage) { damage(boss, victim, damage); }

    /** Return the movement speed multiplier for the boss. Default is 1.0 (no modification). */
    default double movementSpeed(CustomBoss boss) { return 1.0; }

    /** Return true to show a meteor trail particle effect. Default is false. */
    default boolean meteorTrail(CustomBoss boss) { return false; }

    BossVisuals visuals();
}
