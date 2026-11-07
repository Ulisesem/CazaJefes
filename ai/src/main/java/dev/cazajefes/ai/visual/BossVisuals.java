package dev.cazajefes.ai.visual;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

public interface BossVisuals {
    interface View extends AutoCloseable {
        void move(Location at);
        @Override void close();
    }

    View boss(LivingEntity body);
    enum Projectile { ARROW, FIREWORK, SHULKER, SPELL, TRIDENT }
    View projectile(Location origin);
    default View projectile(Location origin,Projectile kind){return projectile(origin);}
    View meteor(Location origin,ItemStack helmet);
    void explosion(Location center);
}
