package dev.cazajefes.ai;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface CustomBoss {
    LivingEntity body();
    BossProfile profile();
    BossState state();
    Player target();
    BossEnvironment environment();
    default Location origin() { return body().getEyeLocation(); }
    void tick(long tick);
    void stun(int ticks);
    void close();
}
