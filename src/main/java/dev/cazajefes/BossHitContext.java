package dev.cazajefes;

/** Separates basic scripted damage from spells in the common Bukkit damage listener. */
final class BossHitContext {
    private static final ThreadLocal<Boolean> MELEE=new ThreadLocal<>();
    private BossHitContext() {}
    static boolean melee(){return Boolean.TRUE.equals(MELEE.get());}
    static void melee(Runnable action) {
        Boolean previous=MELEE.get();
        MELEE.set(true);
        try { action.run(); }
        finally { if(previous==null)MELEE.remove();else MELEE.set(previous); }
    }
}
