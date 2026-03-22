package net.fluxd.irradiated.handlers;

import net.fluxd.irradiated.Irradiated;
import net.fluxd.irradiated.core.AreaManager;
import net.fluxd.irradiated.core.BorderMessageDisplay;
import net.fluxd.irradiated.core.BorderParticleDisplay;
import net.fluxd.irradiated.core.AreaManager.AreaType;
import net.fluxd.irradiated.effects.Effects;
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
public class PlayerAreaHandler {
  private static final double DISTANCE_THRESHOLD_M = 15.0;

  public static class PlayerAreaState {
    public String lastAreaName;
    public Long lastCrossingTime;
    public boolean wasShowingMessage;

    PlayerAreaState(String lastAreaName) {
      this.lastAreaName = lastAreaName;
      this.lastCrossingTime = null;
      this.wasShowingMessage = false;
    }
  }

  private static final HashMap<UUID, PlayerAreaState> playerStates = new HashMap<>();

  @SubscribeEvent
  public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
    if (event.side.isServer() && event.phase == TickEvent.Phase.END) {
      ServerPlayer player = (ServerPlayer) event.player;
      UUID uuid = player.getUUID();

      AreaManager.CurrentAreaResult result = AreaManager.getCurrentArea(player);

      PlayerAreaState state = playerStates.get(uuid);
      if (state == null)
        return;

      boolean areaChanged = !result.currentArea().name().equals(state.lastAreaName);
      if (areaChanged) {
        state.lastAreaName = result.currentArea().name();
        state.lastCrossingTime = System.currentTimeMillis();
        playSound(player);
      }

      boolean isNearBorder = result.distanceToBorder() < DISTANCE_THRESHOLD_M;
      // Skip on Spawn, dump hack but works
      if (result.approachingArea().type() == AreaType.SPAWN)
        isNearBorder = false;

      handleRadiation(player, result.currentArea());
      BorderParticleDisplay.handle(player, result, isNearBorder);
      BorderMessageDisplay.handle(player, result, state, isNearBorder);
    }
  }

  private static void handleRadiation(ServerPlayer player, AreaManager.Area currentArea) {
    // Limit to 4 times per second (for performance?)
    if (player.tickCount % 5 != 0 && currentArea.type() == AreaType.RADIATION) {
      player.addEffect(Effects.radiationInstance(50)); // 2.5 sec
    }
  }

  private static void playSound(ServerPlayer player) {
    player.playNotifySound(SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.AMBIENT, 0.5f, 1.0f);
  }

  @SubscribeEvent
  public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
    if (event.getEntity() instanceof ServerPlayer serverPlayer) {
      AreaManager.CurrentAreaResult result = AreaManager.getCurrentArea(serverPlayer);
      playerStates.put(event.getEntity().getUUID(), new PlayerAreaState(result.currentArea().name()));
    }
  }

  @SubscribeEvent
  public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
    playerStates.remove(event.getEntity().getUUID());
  }
}