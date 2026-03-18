package net.fluxd.irradiated.handlers;

import net.fluxd.irradiated.Irradiated;
import net.fluxd.irradiated.core.AreaManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Irradiated.MODID)
public class AreaDisplayHandler {

  private static class PlayerAreaState {
    String lastAreaName;
    long announcementEndTime;
    boolean wasShowingDistance;

    PlayerAreaState(String lastAreaName, long announcementEndTime) {
      this.lastAreaName = lastAreaName;
      this.announcementEndTime = announcementEndTime;
      this.wasShowingDistance = false;
    }
  }

  private static final HashMap<UUID, PlayerAreaState> playerStates = new HashMap<>();
  private static final double MESSAGE_DISTANCE_THRESHOLD_M = 15.0;
  private static final double BORDER_DISTANCE_THRESHOLD_M = 10.0;
  private static final long ENTERED_DURATION_MS = 1500;

  @SubscribeEvent
  public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
    if (event.side.isServer() && event.phase == TickEvent.Phase.END) {
      ServerPlayer player = (ServerPlayer) event.player;
      UUID uuid = player.getUUID();
      long now = System.currentTimeMillis();

      AreaManager.CurrentAreaResult result = AreaManager.getCurrentArea(player);
      PlayerAreaState state = playerStates.get(uuid);

      boolean areaChanged = (state == null || !result.currentName().equals(state.lastAreaName));

      if (areaChanged) {
        state = new PlayerAreaState(result.currentName(), now + ENTERED_DURATION_MS);
        playerStates.put(uuid, state);
        playAreaSound(player);
        displayMessage(player, result, true);
      } else {
        boolean isAnnouncing = now < state.announcementEndTime;
        boolean nearBorder = result.distanceToBorder() < MESSAGE_DISTANCE_THRESHOLD_M;

        if (isAnnouncing || nearBorder) {
          displayMessage(player, result, isAnnouncing);
          state.wasShowingDistance = true;
          spawnBorderParticles(player, result.borderAbsoluteRadius());
        } else if (state.wasShowingDistance) {
          player.displayClientMessage(Component.empty(), true);
          state.wasShowingDistance = false;
        }
      }
    }
  }

  private static void spawnBorderParticles(ServerPlayer player, double borderAbsoluteRadius) {
    if (player.tickCount % 2 != 0)
      return;

    double viewRadius = BORDER_DISTANCE_THRESHOLD_M; // How large is the "sphere of visibility" around the player?
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

    // 3. Calculate the angular "slice" of the border that is within the viewRadius
    // Law of Cosines: viewRadius^2 = distToSpawn^2 + borderRadius^2 -
    // 2*distToSpawn*borderRadius*cos(deltaAngle)
    // We solve for deltaAngle (the horizontal spread of the arc)
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
          player.serverLevel().sendParticles(
              ParticleTypes.WAX_OFF,
              px, py, pz,
              1, 0, 0, 0, 0.00);
        }
      }
    }
  }

  private static void displayMessage(ServerPlayer player, AreaManager.CurrentAreaResult result, boolean isAnnouncing) {
    Component message;
    if (isAnnouncing) {
      String formattedName = result.currentName().replace('&', '§');
      message = Component.literal("§eEntered: ").append(Component.literal(formattedName));
    } else {
      String formattedApproaching = result.approachingName().replace('&', '§');
      message = Component.literal("§7Approaching: ")
          .append(Component.literal(formattedApproaching))
          .append(Component.literal(String.format(" §7(%.1fm)", result.distanceToBorder())));
    }
    player.displayClientMessage(message, true);
  }

  private static void playAreaSound(ServerPlayer player) {
    player.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.AMBIENT, 0.5f, 1.0f);
  }

  @SubscribeEvent
  public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
    playerStates.remove(event.getEntity().getUUID());
  }
}