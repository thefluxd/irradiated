package net.fluxd.irradiated.handlers;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.fluxd.irradiated.Irradiated;
import net.fluxd.irradiated.core.AreaManager;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Irradiated.MODID)
public class AreaDisplayHandler {

  // Stores the last area name for every player online
  private static final HashMap<UUID, String> playerAreaCache = new HashMap<>();

  @SubscribeEvent
  public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
    // Run only on Server side, once per tick
    if (event.side.isServer() && event.phase == TickEvent.Phase.END) {
      ServerPlayer player = (ServerPlayer) event.player;
      UUID uuid = player.getUUID();

      // 1. Get the current area based on position
      String currentArea = AreaManager.getCurrentArea(player);

      // 2. Get the last known area (default to "none" if new)
      String lastArea = playerAreaCache.getOrDefault(uuid, "none");

      // 3. Only act if the area has changed
      if (!currentArea.equals(lastArea)) {
        sendAreaMessage(player, currentArea);
        playerAreaCache.put(uuid, currentArea); // Update the cache
      }
    }
  }

  private static void sendAreaMessage(ServerPlayer player, String areaName) {
    Component message;

    // Convert '&' to '§' (Standard Minecraft color formatting)
    String formattedName = areaName.replace('&', '§');

    message = Component.literal("Entering: ")
        .append(Component.literal(formattedName));

    // The 'true' boolean here tells Minecraft to put it in the Action Bar
    player.displayClientMessage(message, true);

    // and notify with sound
    // TODO: add a toggle or remove sound, or play only when entering radiated zone
    player.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.AMBIENT, 0.5f, 1.0f);
  }

  // Clean up memory when player leaves
  @SubscribeEvent
  public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
    playerAreaCache.remove(event.getEntity().getUUID());
  }
}