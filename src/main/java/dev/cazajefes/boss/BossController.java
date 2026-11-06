package dev.cazajefes.boss;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/** Shared contract for the movement controller attached to a live encounter. */
public abstract class BossController {
    protected final LivingEntity boss;

    protected BossController(LivingEntity boss) {
        this.boss = boss;
    }

    public abstract Player pickTarget();
    public abstract void tick(Player target, int phase);
}
