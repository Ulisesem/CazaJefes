package dev.cazajefes;
import org.bukkit.entity.EntityType;
final class MinionTactics {
 enum Move{APPROACH,RETREAT,STRAFE}
 static boolean ranged(EntityType type){return switch(type){case SKELETON,STRAY,BOGGED,PILLAGER,WITCH,EVOKER,ILLUSIONER,BLAZE,GHAST,SHULKER,LLAMA,TRADER_LLAMA,SNOW_GOLEM->true;default->false;};}
 static Move move(boolean ranged,boolean visible,double distance){if(!ranged||!visible||distance>12)return Move.APPROACH;if(distance<6)return Move.RETREAT;return Move.STRAFE;}
}
