package net.fluxd.irradiated.core;

import net.fluxd.irradiated.Utils;
import net.fluxd.irradiated.handlers.PlayerAreaHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class BorderMessageDisplay {
  private static final long ENTERED_DURATION_MS = 1500;

  public static void handle(ServerPlayer player, AreaManager.CurrentAreaResult result,
      PlayerAreaHandler.PlayerAreaState state, boolean isNearBorder) {
    boolean isAnnouncing = state.lastCrossingTime != null
        && System.currentTimeMillis() < state.lastCrossingTime + ENTERED_DURATION_MS;
    if (isAnnouncing || isNearBorder) {
      displayMessage(player, result, isAnnouncing);
      state.wasShowingMessage = true;
    } else if (state.wasShowingMessage) {
      player.displayClientMessage(Component.empty(), true);
      state.wasShowingMessage = false;
    }
  }

  private static void displayMessage(ServerPlayer player, AreaManager.CurrentAreaResult result, boolean isAnnouncing) {
    Component message;
    if (isAnnouncing) {
      String formattedName = Utils.formatString(result.currentArea().name());
      message = Component.literal("§7Entered: ").append(Component.literal(formattedName));
    } else {
      String formattedApproaching = Utils.formatString(result.approachingArea().name());
      message = Component.literal("§7Approaching: ")
          .append(Component.literal(formattedApproaching))
          .append(Component.literal(String.format(" §7(%.1fm)", result.distanceToBorder())));
    }
    player.displayClientMessage(message, true);
  }
}
