package dev.cazajefes;

import org.bukkit.entity.EntityType;

/** Brief combat notes used by the bestiary, without internal identifiers. */
final class BossLore {
    private BossLore() {}

    static String describe(EntityType type, boolean mini) {
        return switch (type) {
            case SKELETON, STRAY, BOGGED, PILLAGER -> "Mantiene la distancia y cambia de posición. Acorta el espacio antes de que prepare su siguiente ataque.";
            case SPIDER, CAVE_SPIDER -> "Un depredador de la Grieta que busca los flancos. No pierdas de vista a sus crías.";
            case BLAZE, GHAST, PHANTOM -> "Domina el espacio aéreo. Busca una posición desde la que puedas seguir sus movimientos.";
            case GUARDIAN, ELDER_GUARDIAN, DOLPHIN, SQUID, GLOW_SQUID, AXOLOTL -> "Defiende su territorio bajo el agua. Vigila la respiración y deja una ruta de salida.";
            case SHULKER -> "Alterna ataques y cambios de posición. Localiza su siguiente aparición antes de acercarte.";
            case WARDEN -> "Un soberano del abismo que castiga la proximidad. Reserva espacio para esquivar sus ataques.";
            case EVOKER, WITCH -> "Controla la zona con magia. Evita permanecer en el mismo lugar durante sus preparaciones.";
            case RAVAGER, HOGLIN, ZOGLIN, IRON_GOLEM, GIANT -> "Combate de frente y presiona a corta distancia. Conserva margen para retirarte cuando entre en furia.";
            default -> mini
                ? "Un animal territorial alterado por la Grieta. Persigue a los cazadores y se vuelve más peligroso al perder vida."
                : "Un guardián de combate cercano. Cambia de ritmo en cada etapa y reserva su definitivo para la furia final.";
        };
    }
}
