package net.fluxd.irradiated.core;

import net.fluxd.irradiated.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.fluxd.irradiated.Config.AreaEntry;

public class AreaManager {
  // This uses the areaEntries list you populated in your ModConfigEvent
  public static String getCurrentArea(ServerPlayer player) {
    if (Config.areaEntries == null || Config.areaEntries.isEmpty()) {
      return "outside";
    }

    // 1. Get Spawn Position
    BlockPos spawnPos = player.level().getSharedSpawnPos();

    // 2. Calculate 2D distance components
    double dx = player.getX() - spawnPos.getX();
    double dz = player.getZ() - spawnPos.getZ();

    // Calculate squared distance (x^2 + z^2) - faster than Math.sqrt
    double distanceSq = (dx * dx) + (dz * dz);

    int cumulativeRadius = 0;

    // 3. Iterate through your parsed list
    for (AreaEntry entry : Config.areaEntries) {
      cumulativeRadius += entry.radius();

      // Compare distance squared to radius squared
      // (dist <= radius) is mathematically same as (dist^2 <= radius^2)
      if (distanceSq <= (double) cumulativeRadius * cumulativeRadius) {
        return entry.name();
      }
    }

    return "outside";
  }
}