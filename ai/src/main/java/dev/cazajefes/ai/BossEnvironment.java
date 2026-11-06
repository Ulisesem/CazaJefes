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
    default void release(CustomBoss boss,BossAttack attack) {}
    default void impact(CustomBoss boss,BossAttack attack,Location at) { visuals().explosion(at); }
    default void heartbeat(CustomBoss boss) {}
    default void blocked(CustomBoss boss,Player target,Location attempted) {}
    default void groundWarning(CustomBoss boss,Location center,double radius,int age) {}
    BossVisuals visuals();
}
