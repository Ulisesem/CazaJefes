package dev.cazajefes;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

final class BeamPath {
   record Segment(Location start, Vector direction, double length) {}
   static Vector reflect(Vector direction, Vector normal) {
      return direction.clone().subtract(normal.clone().multiply(2 * direction.dot(normal)));
   }
   static List<Segment> trace(Location origin, Vector direction, double range, boolean bounce) {
      var segments = new ArrayList<Segment>(2);
      Location at = origin.clone();
      Vector heading = direction.clone().normalize();
      for (int leg = 0; leg < (bounce ? 2 : 1) && range > .15; leg++) {
         var hit = at.getWorld().rayTraceBlocks(at,heading,range,FluidCollisionMode.NEVER,true);
         double length = hit == null ? range : hit.getHitPosition().distance(at.toVector());
         if (length > .02) segments.add(new Segment(at,heading,length));
         if (hit == null || hit.getHitBlockFace() == null || length < .05) break;
         heading = reflect(heading,hit.getHitBlockFace().getDirection());
         at = hit.getHitPosition().toLocation(origin.getWorld()).add(heading.clone().multiply(.06));
         range -= length + .06;
      }
      return segments;
   }
   static boolean hits(List<Segment> path, BoundingBox box) {
      for (var leg : path) {
         Vector start = leg.start().toVector();
         if (box.contains(start) || box.rayTrace(start,leg.direction(),leg.length()) != null) return true;
      }
      return false;
   }
   static boolean same(List<Segment> announced, List<Segment> current) {
      if (announced.size() != current.size()) return false;
      for (int i=0;i<announced.size();i++) {
         var a=announced.get(i);var b=current.get(i);
         if (Math.abs(a.length()-b.length()) > .12 || a.start().distanceSquared(b.start()) > .0144
             || a.direction().distanceSquared(b.direction()) > .001) return false;
      }
      return true;
   }
}
