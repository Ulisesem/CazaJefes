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
    View projectile(Location origin);
    View meteor(Location origin,ItemStack helmet);
    void explosion(Location center);
}
