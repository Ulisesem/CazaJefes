package dev.cazajefes.ai;

import dev.cazajefes.ai.visual.BossVisuals;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface BossEnvironment {
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
    BossVisuals visuals();
}
