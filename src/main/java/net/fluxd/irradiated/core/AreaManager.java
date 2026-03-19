package net.fluxd.irradiated.core;

import net.fluxd.irradiated.Config;
import net.fluxd.irradiated.Config.AreaEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class AreaManager {
  public static enum AreaType {
    USER, SPAWN, RADIATION, NONE
  }

  public record Area(AreaType type, String name) {
  }

  public record CurrentAreaResult(Area currentArea, Area approachingArea, double distanceToBorder,
      double borderAbsoluteRadius) {
  }

  public static CurrentAreaResult getCurrentArea(ServerPlayer player) {
    List<AreaEntry> entries = Config.areaEntries;
    if (entries == null || entries.isEmpty()) {
      return new CurrentAreaResult(SPAWN_AREA, new Area(AreaType.NONE, "None"), Double.MAX_VALUE,
          0);
    }

    BlockPos spawnPos = player.level().getSharedSpawnPos();
    double dx = player.getX() - spawnPos.getX();
    double dz = player.getZ() - spawnPos.getZ();
    double currentDist = Math.sqrt((dx * dx) + (dz * dz));

    int cumulativeRadius = 0;

    for (int i = 0; i < entries.size(); i++) {
      int innerRadius = cumulativeRadius;
      int outerRadius = cumulativeRadius + entries.get(i).radius();

      if (currentDist <= outerRadius) {
        Area currentArea = entries.get(i).area();
        double distToInner = currentDist - innerRadius;
        double distToOuter = outerRadius - currentDist;

        if (distToInner < distToOuter) {
          Area approachingArea = (i == 0) ? SPAWN_AREA : entries.get(i - 1).area();
          return new CurrentAreaResult(currentArea, approachingArea, distToInner, innerRadius);
        } else {
          Area approachingArea = (i == entries.size() - 1) ? RADIATION_AREA : entries.get(i + 1).area();
          return new CurrentAreaResult(currentArea, approachingArea, distToOuter, outerRadius);
        }
      }
      cumulativeRadius = outerRadius;
    }

    // When in Radiation
    return new CurrentAreaResult(RADIATION_AREA, entries.get(entries.size() - 1).area(), currentDist - cumulativeRadius,
        cumulativeRadius);
  }

  private static final Area SPAWN_AREA = new Area(AreaType.SPAWN, "Spawn");
  private static final Area RADIATION_AREA = new Area(AreaType.RADIATION, "&4&lRADIATION");
  private static final Area NONE_AREA = new Area(AreaType.NONE, "None");
}