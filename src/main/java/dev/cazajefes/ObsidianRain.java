package dev.cazajefes;

import java.util.ArrayList;
import java.util.List;

final class ObsidianRain {
   record Point(double x, double z) {}
   private ObsidianRain() {}

   static boolean hits(double x, double z, int pulse) {
      if (x < -10 || x >= 10 || z < -10 || z >= 10) return false;
      int column = (int)((x + 10) / 5);
      int row = (int)((z + 10) / 5);
      return (row + column) % 4 == pulse % 4;
   }

   static List<Point> centers(int pulse) {
      var points = new ArrayList<Point>(4);
      for (double x = -7.5; x <= 7.5; x += 5)
         for (double z = -7.5; z <= 7.5; z += 5)
            if (hits(x,z,pulse)) points.add(new Point(x,z));
      return points;
   }
}
