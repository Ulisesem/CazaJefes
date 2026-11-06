package dev.cazajefes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.*;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

final class VoidAnchors {
   private record Anchor(Interaction entity, Location position) {}
   private static final NamespacedKey OWNER = new NamespacedKey("cazajefes","anchor_owner");
   private final List<Anchor> active = new ArrayList<>();
   private final List<Location> safe = new ArrayList<>();
   private static int count;

   void start(Location center, UUID owner) {
      if (count >= 48) return;
      for (int i=0;i<3 && count<48;i++) {
         double angle=i*Math.PI*2/3;
         var at=center.clone().add(Math.cos(angle)*3.5,0,Math.sin(angle)*3.5);
         if (!at.isChunkLoaded() || !at.getBlock().isPassable()) continue;
         var entity=at.getWorld().spawn(at,Interaction.class,hitbox -> {
            hitbox.setPersistent(false);
            hitbox.setInteractionWidth(1.3f);
            hitbox.setInteractionHeight(1.65f);
            hitbox.setResponsive(true);
            hitbox.getPersistentDataContainer().set(OWNER,PersistentDataType.STRING,owner.toString());
         });
         if (entity.isValid()) { active.add(new Anchor(entity,at));count++; }
      }
   }

   void update(List<Player> audience, boolean draw) {
      for (var iterator=active.iterator();iterator.hasNext();) {
         Anchor anchor=iterator.next();
         var hit=anchor.entity().getLastAttack();
         var click=anchor.entity().getLastInteraction();
         var interaction=hit==null || click!=null && click.getTimestamp()>hit.getTimestamp() ? click : hit;
         Player player=interaction==null ? null : interaction.getPlayer().getPlayer();
         if (player != null && audience.contains(player) && player.getLocation().distanceSquared(anchor.position()) <= 30
             && player.hasLineOfSight(anchor.entity())) {
            safe.add(anchor.position());
            anchor.entity().remove();
            iterator.remove();
            count--;
            player.playSound(anchor.position(),Sound.BLOCK_AMETHYST_BLOCK_BREAK,.8f,1.3f);
            BossWarnings.send(player,net.kyori.adventure.text.Component.text("ANCLA ROTA · ENTRA AL CÍRCULO VERDE"));
         } else if (draw) {
            CinematicFX.ring(anchor.position(),audience,.72,0,7,Color.FUCHSIA,Color.WHITE);
            CosmeticFX.emit(Particle.END_ROD,anchor.position().clone().add(0,.9,0),2,.2,.35,.2,.02);
         }
      }
      if (draw) for (var position : safe) CinematicFX.ring(position,audience,2,0,10,Color.LIME,Color.AQUA);
   }

   boolean shelters(Location point) {
      return safe.stream().anyMatch(at -> at.distanceSquared(point)<=4);
   }

   void close() {
      for (var anchor : active) anchor.entity().remove();
      count-=active.size();
      active.clear();
      safe.clear();
   }
}
