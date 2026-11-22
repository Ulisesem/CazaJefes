package dev.cazajefes;

import dev.cazajefes.ai.BossAttack;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/** Visual charge lines and attack release sounds for boss attacks. */
final class CombatTelegraph {
    private CombatTelegraph() {}

    /** Draw a particle line from boss to target, warning nearby players of an incoming attack. */
    static void charge(LivingEntity boss, Player target, BossAttack attack) {
        var from = boss.getEyeLocation();
        var end = target.getLocation().add(0, .12, 0);
        var delta = end.toVector().subtract(from.toVector());
        double length = delta.length();
        if (length < .01 || length > 60) return;
        var obstruction = from.getWorld().rayTraceBlocks(from, delta.clone().normalize(), length,
            FluidCollisionMode.NEVER, true);
        if (obstruction != null) return;
        var viewers = from.getWorld().getNearbyPlayers(from, 32).stream()
            .filter(viewer -> ArenaBoundary.allows(boss, viewer.getLocation())).toList();
        var dust = new Particle.DustTransition(Color.fromRGB(255, 166, 64), Color.RED, .7f);
        for (int i = 1; i <= 12; i++) {
            var at = from.clone().add(delta.clone().multiply(i / 12.0));
            if (!at.isChunkLoaded()) continue;
            for (var viewer : viewers) CosmeticFX.warning(viewer, at, null, dust);
        }
        if (attack.special() && ArenaBoundary.allows(boss, end)) {
            for (int i = 0; i < 16; i++) {
                double angle = i * Math.PI / 8;
                var at = end.clone().add(Math.cos(angle) * 1.1, 0, Math.sin(angle) * 1.1);
                if (!at.isChunkLoaded()) continue;
                var floor = at.getWorld().rayTraceBlocks(at.clone().add(0, 1, 0), new org.bukkit.util.Vector(0, -1, 0),
                    3, FluidCollisionMode.NEVER, true);
                if (floor == null) continue;
                var ground = floor.getHitPosition().toLocation(at.getWorld()).add(0, .06, 0);
                for (var viewer : viewers) CosmeticFX.warning(viewer, ground, null, dust);
            }
        }
    }

    /** Play a sound effect at the boss location based on its entity type when an attack releases. */
    static void release(LivingEntity boss, BossAttack attack) {
        Sound sound = switch (boss.getType()) {
            case DROWNED, GUARDIAN, ELDER_GUARDIAN -> Sound.ITEM_TRIDENT_THROW;
            case PILLAGER -> Sound.ITEM_CROSSBOW_SHOOT;
            case SKELETON, STRAY, BOGGED -> Sound.ENTITY_SKELETON_SHOOT;
            case SHULKER -> Sound.ENTITY_SHULKER_SHOOT;
            case EVOKER, ILLUSIONER, WITCH -> Sound.ENTITY_EVOKER_CAST_SPELL;
            default -> attack.special() ? Sound.ENTITY_PLAYER_ATTACK_SWEEP : Sound.ENTITY_PLAYER_ATTACK_STRONG;
        };
        var at = boss.getLocation();
        for (var viewer : at.getWorld().getNearbyPlayers(at, 32))
            if (ArenaBoundary.allows(boss, viewer.getLocation()))
                viewer.playSound(at, sound, .75f, attack.special() ? .8f : 1.15f);
    }
}
