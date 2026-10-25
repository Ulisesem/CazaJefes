package dev.cazajefes;
import org.bukkit.entity.EntityType;
final class PassiveCombat {static boolean assist(EntityType type,boolean enemy){return !enemy||type==EntityType.GIANT;}}
