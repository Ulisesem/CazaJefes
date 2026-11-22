package dev.cazajefes.api.event;

import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/** Synchronous observation of configured AI attacks. UUIDs refer to live server entities. */
public final class BossAttackEvent extends Event {
    public enum Stage { CHARGE, RELEASE, IMPACT }
    private static final HandlerList HANDLERS = new HandlerList();
    private final UUID boss, target;
    private final String attack;
    private final Stage stage;
    private final Location location;

    public BossAttackEvent(UUID boss, UUID target, String attack, Stage stage, Location location) {
        this.boss = boss;
        this.target = target;
        this.attack = attack;
        this.stage = stage;
        this.location = location.clone();
    }

    public UUID boss() { return boss; }
    public UUID target() { return target; }
    public String attack() { return attack; }
    public Stage stage() { return stage; }
    public Location location() { return location.clone(); }

    @Override public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
}
