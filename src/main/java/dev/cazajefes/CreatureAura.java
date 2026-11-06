package dev.cazajefes;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;

/** Visual vocabulary for creatures, independent of the combat and sound profiles. */
enum CreatureAura {
    HIVE(Form.HIVE, 0xfca329, 0xffef93, Particle.FALLING_HONEY),
    WEB(Form.WEB, 0xb7d871, 0xc6b5e7, Particle.SPORE_BLOSSOM_AIR),
    SHELL(Form.SHELL, 0xb88558, 0xffd392, Particle.WAX_ON),
    REEF_SHELL(Form.SHELL, 0x519876, 0xa3f4c7, Particle.SPLASH),
    BLOOM(Form.SHELL, 0xbd6848, 0x8fd66f, Particle.SPORE_BLOSSOM_AIR),
    SPORES(Form.SHELL, 0xbf3f4b, 0xffc9b2, Particle.SPORE_BLOSSOM_AIR),
    GROVE(Form.TRACK, 0x7caf51, 0xf2db9d, Particle.HAPPY_VILLAGER),
    STAMPEDE(Form.TRACK, 0xd49658, 0xffdb9a, Particle.ASH),
    HUNTER(Form.TRACK, 0xf88a49, 0xffe5b6, Particle.CRIT),
    FROST(Form.SHARDS, 0x8ad9ff, 0xf1fcff, Particle.SNOWFLAKE),
    WINGS(Form.WINGS, 0x66deaf, 0xffc859, Particle.END_ROD),
    WIND(Form.WINGS, 0x8fdafa, 0xf3fbff, Particle.CLOUD),
    NIGHT_FLIGHT(Form.WINGS, 0x8c74ed, 0x78eedb, Particle.SOUL),
    EMBER_FLIGHT(Form.WINGS, 0xe77952, 0xfbd6ad, Particle.SMALL_FLAME),
    SUN(Form.ORBIT, 0xff751f, 0xffd66b, Particle.FLAME),
    GEYSER(Form.RIPPLE, 0xff5f36, 0xffc54b, Particle.LAVA),
    JELLY(Form.RIPPLE, 0x74dc57, 0xddfd80, Particle.HAPPY_VILLAGER),
    CURRENT(Form.RIPPLE, 0x31c5dc, 0xadf3ff, Particle.SPLASH),
    INK(Form.RIPPLE, 0x69549b, 0xd1b5ff, Particle.SQUID_INK),
    LUMEN(Form.ORBIT, 0x27e8ca, 0x96fff2, Particle.GLOW),
    PRISM(Form.PRISM, 0x2bd0c0, 0xe5ffa3, Particle.ELECTRIC_SPARK),
    SHULKER(Form.PRISM, 0x994dd0, 0xeec6ff, Particle.REVERSE_PORTAL),
    SCULK(Form.RIPPLE, 0x257a92, 0x71ebdc, Particle.SCULK_SOUL),
    RIFT(Form.ORBIT, 0x8a50e2, 0xe5a7ff, Particle.REVERSE_PORTAL),
    HEX(Form.ORBIT, 0xa65aee, 0xf4a3eb, Particle.WITCH),
    RUNES(Form.ORBIT, 0x985cff, 0x69ecea, Particle.ENCHANT),
    STONE(Form.SHELL, 0x9daab3, 0xffdd96, Particle.ELECTRIC_SPARK),
    WOOD(Form.PRISM, 0xb5784a, 0xffb64d, Particle.SPORE_BLOSSOM_AIR),
    UNDEAD(Form.SHARDS, 0x67926d, 0xb4ec9b, Particle.SOUL),
    DUNES(Form.TRACK, 0xce9d64, 0xffdea0, Particle.ASH),
    BONE(Form.SHARDS, 0xd9d4bb, 0xffffff, Particle.CRIT),
    WITHER(Form.SHARDS, 0x736291, 0xd9afe3, Particle.SOUL),
    SWAMP(Form.SHARDS, 0x95bb4f, 0xc5db8c, Particle.SPORE_BLOSSOM_AIR),
    RAIDER(Form.TRACK, 0xbb6870, 0xffbf75, Particle.CRIT),
    NETHER(Form.SHARDS, 0xe5553e, 0xffb557, Particle.SMALL_FLAME);

    private enum Form { HIVE, WEB, SHELL, TRACK, SHARDS, WINGS, ORBIT, RIPPLE, PRISM }

    final Color primary;
    final Color accent;
    final Particle particle;
    private final Form form;

    CreatureAura(Form form, int primary, int accent, Particle particle) {
        this.form = form;
        this.primary = Color.fromRGB(primary);
        this.accent = Color.fromRGB(accent);
        this.particle = particle;
    }

    static CreatureAura of(EntityType type) {
        return switch (type) {
            case BEE -> HIVE;
            case SPIDER, CAVE_SPIDER, SILVERFISH -> WEB;
            case ARMADILLO -> SHELL;
            case TURTLE -> REEF_SHELL;
            case SNIFFER -> BLOOM;
            case MOOSHROOM -> SPORES;
            case CAT, OCELOT, FOX, WOLF, RABBIT -> HUNTER;
            case CAMEL, HUSK -> DUNES;
            case HORSE, DONKEY, MULE, LLAMA, GOAT, RAVAGER -> STAMPEDE;
            case STRAY, POLAR_BEAR, SNOW_GOLEM -> FROST;
            case PARROT, CHICKEN, ALLAY -> WINGS;
            case BREEZE -> WIND;
            case PHANTOM, BAT, VEX -> NIGHT_FLIGHT;
            case GHAST -> EMBER_FLIGHT;
            case BLAZE -> SUN;
            case MAGMA_CUBE, STRIDER -> GEYSER;
            case SLIME -> JELLY;
            case AXOLOTL, DOLPHIN, DROWNED, NAUTILUS, COD, SALMON, TROPICAL_FISH, PUFFERFISH -> CURRENT;
            case SQUID -> INK;
            case GLOW_SQUID -> LUMEN;
            case GUARDIAN, ELDER_GUARDIAN -> PRISM;
            case SHULKER -> SHULKER;
            case WARDEN -> SCULK;
            case ENDERMAN, ENDERMITE, ENDER_DRAGON -> RIFT;
            case WITCH -> HEX;
            case EVOKER, ILLUSIONER -> RUNES;
            case IRON_GOLEM -> STONE;
            case CREAKING -> WOOD;
            case ZOMBIE, GIANT, ZOMBIE_VILLAGER -> UNDEAD;
            case SKELETON -> BONE;
            case WITHER_SKELETON, WITHER -> WITHER;
            case BOGGED -> SWAMP;
            case PILLAGER, VINDICATOR -> RAIDER;
            case PIGLIN, PIGLIN_BRUTE, ZOMBIFIED_PIGLIN, HOGLIN, ZOGLIN -> NETHER;
            default -> GROVE;
        };
    }

    List<AuraShape.Point> points(double width, double height, double yaw, double frameAngle, int count, int phase, int seed) {
        int total = Math.clamp(count, 0, 72);
        var result = new ArrayList<AuraShape.Point>(total);
        double radius = Math.clamp(width * .5 + .5, .8, 5);
        double body = Math.clamp(height, .5, 8);
        double signature = Integer.toUnsignedLong(seed) * (2 * Math.PI / 0x1.0p32);
        double spin = frameAngle + signature;
        double facing = Math.toRadians(yaw);
        double cosYaw = Math.cos(facing), sinYaw = Math.sin(facing);
        for (int i = 0; i < total; i++) {
            int layer = i % 2;
            double fraction = (i / 2 + .5) / Math.ceil(total * .5);
            double arc = fraction * 2 * Math.PI;
            double theta = arc + spin * (layer == 0 ? 1 : -.7);
            double r = radius, y = .18;
            switch (form) {
                case HIVE -> {
                    r *= polygon(theta, 6);
                    y += body * (.18 + layer * .6);
                }
                case WEB -> {
                    r *= polygon(theta, 8) * (layer == 0 ? .67 : 1.12);
                    y += body * (.1 + .18 * Math.abs(Math.cos(arc * 4)));
                }
                case SHELL -> {
                    double arch = Math.sin(arc * .5);
                    r *= layer == 0 ? 1 : .85 * arch;
                    y += layer == 0 ? .05 : body * (.15 + .9 * arch);
                }
                case TRACK -> {
                    theta = arc + spin * .3 + layer * .35;
                    r *= .92 + .19 * Math.cos(arc * 3);
                    y += layer * body * .28;
                }
                case SHARDS -> {
                    r *= .83 + .18 * Math.cos(theta * 5);
                    y += body * (.15 + layer * .58) * (.65 + .35 * Math.cos(arc * 5));
                }
                case WINGS -> {
                    double span = (.45 + fraction) * (phase == 3 ? 1.12 : 1);
                    double x = (layer == 0 ? -1 : 1) * radius * span;
                    double z = radius * (.25 + .75 * fraction);
                    double wingHeight = body * (.42 + .32 * Math.sin(fraction * Math.PI)
                        + .12 * Math.sin(spin * 2) * fraction);
                    result.add(new AuraShape.Point(x * cosYaw - z * sinYaw, wingHeight,
                        x * sinYaw + z * cosYaw, layer == 1));
                    continue;
                }
                case ORBIT -> {
                    y += body * (.46 + .4 * Math.sin(theta + layer * Math.PI / 2));
                    r *= 1 + layer * .13;
                }
                case RIPPLE -> {
                    r *= .74 + layer * .32 + .12 * Math.sin(spin * 2 + layer);
                    y += body * (.14 + layer * .3 + .12 * Math.cos(arc * 2 + spin));
                }
                case PRISM -> {
                    r *= polygon(theta, 4);
                    y += body * (.23 + layer * .5 + .12 * Math.sin(theta * 2));
                }
            }
            r *= 1 + .08 * Math.cos(theta * (3 + Math.floorMod(seed, 5)) + signature);
            if (phase == 3) {
                r *= 1.09;
                y += body * .08 * Math.sin(theta * 3 + signature);
            }
            result.add(new AuraShape.Point(Math.cos(theta) * r, Math.max(.1, y), Math.sin(theta) * r, layer == 1));
        }
        return result;
    }

    private static double polygon(double angle, int sides) {
        double half = Math.PI / sides;
        double local = angle - 2 * half * Math.floor((angle + half) / (2 * half));
        return Math.cos(half) / Math.cos(local);
    }
}
