package net.fluxd.irradiated.handlers;

import net.fluxd.irradiated.Irradiated;
import net.fluxd.irradiated.core.AreaManager;
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
  private static final double BORDER_THRESHOLD = 10.0;
  private static final long ANNOUNCE_DURATION_MS = 1500;

  @SubscribeEvent
  public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
    if (event.side.isServer() && event.phase == TickEvent.Phase.END) {
      ServerPlayer player = (ServerPlayer) event.player;
      UUID uuid = player.getUUID();
      long now = System.currentTimeMillis();

      AreaManager.CurrentAreaResult result = AreaManager.getCurrentArea(player);
      PlayerAreaState state = playerStates.get(uuid);

      // Check change against the CURRENT area name
      boolean areaChanged = (state == null || !result.currentName().equals(state.lastAreaName));

      if (areaChanged) {
        state = new PlayerAreaState(result.currentName(), now + ANNOUNCE_DURATION_MS);
        playerStates.put(uuid, state);

        playAreaSound(player);
        displayMessage(player, result, true);
      } else {
        boolean isAnnouncing = now < state.announcementEndTime;
        boolean nearBorder = result.distanceToBorder() < BORDER_THRESHOLD;

        if (isAnnouncing) {
          displayMessage(player, result, true);
          state.wasShowingDistance = true;
        } else if (nearBorder) {
          displayMessage(player, result, false);
          state.wasShowingDistance = true;
        } else if (state.wasShowingDistance) {
          player.displayClientMessage(Component.empty(), true);
          state.wasShowingDistance = false;
        }
      }
    }
  }

  private static void displayMessage(ServerPlayer player, AreaManager.CurrentAreaResult result, boolean isAnnouncing) {
    Component message;

    if (isAnnouncing) {
      String formattedName = result.currentName().replace('&', '§');
      message = Component.literal("§eEntering: ").append(Component.literal(formattedName));
    } else {
      // Use approachingName when near border
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