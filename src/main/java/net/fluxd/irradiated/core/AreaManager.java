package net.fluxd.irradiated.core;

import net.fluxd.irradiated.config.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.List;

public class AreaManager {
  public static enum AreaType {
    USER, SPAWN, RADIATION, NONE
  }

  public record Area(AreaType type, String name) {
  }

  public record AreaEntry(int radius, Area area) {
  }

  public record CurrentAreaResult(Area currentArea, Area approachingArea, double distanceToBorder,
      double borderAbsoluteRadius) {
  }

  public static CurrentAreaResult getCurrentArea(ServerPlayer player) {
    List<AreaEntry> entries = Config.AREAS.getValue().get(player.level().dimension().location().toString());
    if (entries == null || entries.isEmpty()) {
      return new CurrentAreaResult(NONE_AREA, NONE_AREA, Double.MAX_VALUE,
          0);
    }

    BlockPos spawnPos = getSpawnPos(player);
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

  // TODO: make it more customizable
  private static BlockPos getSpawnPos(ServerPlayer player) {
    if (player.level().dimension() == Level.OVERWORLD) {
      return player.level().getSharedSpawnPos();
    } else if (player.level().dimension() == Level.NETHER) {
      // Adjust coords to nether
      BlockPos spawnPos = player.level().getSharedSpawnPos();
      double netherX = spawnPos.getX() / 8.0;
      double netherZ = spawnPos.getZ() / 8.0;
      return new BlockPos((int) netherX, spawnPos.getY(), (int) netherZ);
    }

    return new BlockPos(0, 70, 0);
  }

  private static final Area SPAWN_AREA = new Area(AreaType.SPAWN, "Spawn");
  private static final Area RADIATION_AREA = new Area(AreaType.RADIATION, "&4&lRADIATION");
  private static final Area NONE_AREA = new Area(AreaType.NONE, "None");
}