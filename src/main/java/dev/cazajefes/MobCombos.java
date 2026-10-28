package dev.cazajefes;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
/** Mob families share mechanics, while encounter profiles retain their own geometry. */
final class MobCombos {
 record Plan(String name,String enchantment,String effect,boolean flame){}
 static Plan plan(EntityType type){return switch(type){
 case SPIDER,CAVE_SPIDER,SILVERFISH,ENDERMITE->new Plan("Seda del Verdugo","Perdición de los artrópodos","POISON",false);
 case DROWNED,GUARDIAN,ELDER_GUARDIAN,DOLPHIN,SQUID,GLOW_SQUID,AXOLOTL,TURTLE->new Plan("Marea del Empalador","Empalamiento","SLOWNESS",false);
 case BLAZE,MAGMA_CUBE,GHAST,HOGLIN,ZOMBIFIED_PIGLIN->new Plan("Cicatriz del Averno","Aspecto ígneo","WEAKNESS",true);
 case STRAY,BOGGED,SNOW_GOLEM->new Plan("Agujas de Escarcha","Poder","SLOWNESS",false);
 case SKELETON,PILLAGER->new Plan("Marca del Tirador","Perforación","WEAKNESS",false);
 case ZOMBIE,HUSK,ZOMBIE_VILLAGER,GIANT,WITHER_SKELETON,PHANTOM,WITHER->new Plan("Réquiem del Caído","Castigo","WITHER",false);
 case WARDEN,SHULKER,ENDERMAN,BREEZE->new Plan("Fractura Resonante","Ráfaga","DARKNESS",false);
 case EVOKER,ILLUSIONER,WITCH->new Plan("Sello de Condena","Maldición arcana","WITHER",false);
 default->new Plan("Zarpa del Depredador","Filo","SLOWNESS",false);
 };}
 static double damage(double baseline,int stage){return Math.min(120,Math.max(8,baseline*(.65+stage*.1)));}
}
