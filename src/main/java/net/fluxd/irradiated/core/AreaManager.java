package net.fluxd.irradiated.core;

import net.fluxd.irradiated.Config;
import net.fluxd.irradiated.Config.AreaEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class AreaManager {
  public record CurrentAreaResult(String currentName, String approachingName, double distanceToBorder,
      double borderAbsoluteRadius) {
  }

  public static CurrentAreaResult getCurrentArea(ServerPlayer player) {
    List<AreaEntry> entries = Config.areaEntries;
    if (entries == null || entries.isEmpty()) {
      return new CurrentAreaResult("outside", "none", Double.MAX_VALUE, 0);
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
        String currentName = entries.get(i).name();
        double distToInner = currentDist - innerRadius;
        double distToOuter = outerRadius - currentDist;

        if (distToInner < distToOuter) {
          String approaching = (i == 0) ? "Spawn" : entries.get(i - 1).name();
          return new CurrentAreaResult(currentName, approaching, distToInner, innerRadius);
        } else {
          String approaching = (i == entries.size() - 1) ? "outside" : entries.get(i + 1).name();
          return new CurrentAreaResult(currentName, approaching, distToOuter, outerRadius);
        }
      }
      cumulativeRadius = outerRadius;
    }

    return new CurrentAreaResult("outside", entries.get(entries.size() - 1).name(), currentDist - cumulativeRadius,
        cumulativeRadius);
  }
}