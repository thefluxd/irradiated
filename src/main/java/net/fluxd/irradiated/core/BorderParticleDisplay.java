package net.fluxd.irradiated.core;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;

public class BorderParticleDisplay {
  private static final double BORDER_SPHERE_M = 10.0;

  public static void handle(ServerPlayer player, AreaManager.CurrentAreaResult result, boolean isNearBorder) {
    if (isNearBorder) {
      spawnBorderParticles(player, result.borderAbsoluteRadius());
    }
  }

  private static void spawnBorderParticles(ServerPlayer player, double borderAbsoluteRadius) {
    if (player.tickCount % 2 != 0)
      return;

    double viewRadius = BORDER_SPHERE_M; // How large is the "sphere of visibility" around the player?
    double angleStepSize = 0.5;

    BlockPos spawnPos = player.level().getSharedSpawnPos();
    // Player's 2D position relative to spawn
    double pdx = player.getX() - spawnPos.getX();
    double pdz = player.getZ() - spawnPos.getZ();
    double distToSpawn = Math.sqrt(pdx * pdx + pdz * pdz);

    // Check if the player is even close enough to the border to see it
    double distToBorder = Math.abs(distToSpawn - borderAbsoluteRadius);
    if (distToBorder > viewRadius) {
      return;
    }

    // Calculate the angular "slice" of the border that is within the viewRadius
    double viewRadiusSq = viewRadius * viewRadius;
    double cosAngle = (Math.pow(distToSpawn, 2) + Math.pow(borderAbsoluteRadius, 2) - viewRadiusSq)
        / (2 * distToSpawn * borderAbsoluteRadius);

    // Clamp cosAngle to valid range for acos
    cosAngle = Math.max(-1, Math.min(1, cosAngle));
    double deltaAngle = Math.acos(cosAngle);

    double centerAngle = Math.atan2(pdz, pdx);

    double angleStep = angleStepSize / borderAbsoluteRadius;

    for (double a = centerAngle - deltaAngle; a <= centerAngle + deltaAngle; a += angleStep) {
      // Snap to block center
      double px = Math.floor(spawnPos.getX() + borderAbsoluteRadius * Math.cos(a)) + 0.5;
      double pz = Math.floor(spawnPos.getZ() + borderAbsoluteRadius * Math.sin(a)) + 0.5;

      // Vertical "Sphere" check
      // Only spawn particles if the block is within the 3D sphere of the player
      int playerBlockY = player.getBlockY();
      for (int yOffset = -(int) viewRadius; yOffset <= (int) viewRadius; yOffset++) {
        double py = (playerBlockY + yOffset) + 0.5;

        double dy = py - player.getY();
        double dx = px - player.getX();
        double dz = pz - player.getZ();

        // Check 3D distance to ensure it looks like a sphere intersection
        if ((dx * dx + dy * dy + dz * dz) <= viewRadiusSq) {
          // Send particle only to specific player
          player.serverLevel().sendParticles(
              player,
              ParticleTypes.WAX_OFF, // Particle type
              true, // Force (ignore distance/settings)
              px, py, pz, // Position (X, Y, Z)
              1, // Count
              0.0, 0.0, 0.0, // Speed/Offset (X, Y, Z)
              0.0 // Particle speed
          );
        }
      }
    }
  }
}
