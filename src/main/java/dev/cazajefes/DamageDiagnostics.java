package dev.cazajefes;
import java.util.*;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.persistence.PersistentDataType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
final class DamageDiagnostics implements Listener {
 private static final NamespacedKey BOSS=new NamespacedKey("cazajefes",BossKeys.BOSS);
 private final CazaJefesPlugin plugin;private final Map<UUID,Long> next=new HashMap<>();
 DamageDiagnostics(CazaJefesPlugin plugin){this.plugin=plugin;}
 private void report(LivingEntity boss,Player player,String reason,double base,double damage){long now=System.currentTimeMillis();if(now<next.getOrDefault(boss.getUniqueId(),0L))return;next.put(boss.getUniqueId(),now+5000);if(next.size()>256)next.entrySet().removeIf(e->e.getValue()<now);
  plugin.getLogger().warning("[Daño boss] "+boss.getType()+" "+boss.getUniqueId()+" hp="+boss.getHealth()+" base="+base+" final="+damage+" jugador="+player.getName()+" modo="+player.getGameMode()+" motivo="+reason);
  if(player.hasPermission("cazajefes.admin"))player.sendMessage(Component.text("CazaJefes · Golpe sin efecto: "+reason+". Usa /ri diagnostico para ver el estado.",NamedTextColor.YELLOW));
 }
 @EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=false)public void hit(EntityDamageByEntityEvent event){if(!plugin.getConfig().getBoolean("combat.damage-diagnostics",true)||!(event.getEntity() instanceof LivingEntity boss)||!boss.getPersistentDataContainer().has(BOSS,PersistentDataType.BYTE)||plugin.rebirth.handled(event))return;
  Entity source=event.getDamager();if(source instanceof Projectile projectile&&projectile.getShooter() instanceof Entity owner)source=owner;if(!(source instanceof Player player))return;
  double base=event.getDamage(),damage=event.getFinalDamage();
  if(event.isCancelled()&&base==0&&Math.abs(damage)<0.000001)return;if(event.isCancelled()){report(boss,player,plugin.guard.rejection(event),base,damage);return;}if(damage<=0){report(boss,player,"daño absorbido o ataque sin fuerza",base,damage);return;}
  double health=boss.getHealth(),absorption=boss.getAbsorptionAmount();boolean reborn=BossRebirth.used(boss);plugin.getServer().getScheduler().runTask(plugin,()->{if(boss.isValid()&&!boss.isDead()&&BossRebirth.used(boss)==reborn&&boss.getHealth()>=health&&boss.getAbsorptionAmount()>=absorption)report(boss,player,"el evento permitió daño pero la vida no cambió",base,damage);});
 }
 void inspect(CommandSender sender){if(!AdminPermissions.allows(sender::hasPermission,"rareitemhunter.admin.diagnostico")){sender.sendMessage("Necesitas permiso cazajefes.admin.");return;}if(!(sender instanceof Player p)){sender.sendMessage("Ejecuta /ri diagnostico junto al boss.");return;}
  p.sendMessage("[Burst] "+plugin.burstLaunch.describe(p));
  LivingEntity boss=p.getWorld().getLivingEntities().stream().filter(e->e.getPersistentDataContainer().has(BOSS,PersistentDataType.BYTE)&&e.getLocation().distanceSquared(p.getLocation())<=4096).min(Comparator.comparingDouble(e->e.getLocation().distanceSquared(p.getLocation()))).orElse(null);if(boss==null){p.sendMessage("No hay un boss despierto a 64 bloques.");return;}
  String state="versión="+plugin.getPluginMeta().getVersion()+" boss="+boss.getType()+" uuid="+boss.getUniqueId()+" hp="+boss.getHealth()+"/"+boss.getMaxHealth()+" invulnerable="+boss.isInvulnerable()+" inmunidadTicks="+boss.getNoDamageTicks()+" absorción="+boss.getAbsorptionAmount()+" armadura="+(boss.getAttribute(Attribute.ARMOR)==null?0:boss.getAttribute(Attribute.ARMOR).getValue())+" modo="+p.getGameMode()+" autorizado="+plugin.guard.canAttackBoss(p)+" cuerpoVisible="+CombatHitbox.visible(p,boss)+" etapa="+BossStages.phase(boss)+" renacimientoUsado="+BossRebirth.used(boss)+" manoSecundaria="+(boss.getEquipment()==null?"ninguna":boss.getEquipment().getItemInOffHand().getType());
  p.sendMessage(Component.text("Diagnóstico CazaJefes · "+state,NamedTextColor.GREEN));plugin.getLogger().info("[Diagnóstico boss] "+state);
 }
}
