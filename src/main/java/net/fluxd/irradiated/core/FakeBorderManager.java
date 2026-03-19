package net.fluxd.irradiated.core;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.joml.Vector3f;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FakeBorderManager {
  private static final AtomicInteger FAKE_ID_COUNTER = new AtomicInteger(2000000);
  private static final Map<UUID, Map<BlockPos, Integer>> playerActiveDisplays = new ConcurrentHashMap<>();

  // --- REFLECTION FIELDS ---
  // We fetch these once so we don't slow down the server
  private static final EntityDataAccessor<BlockState> BLOCK_STATE_ACCESSOR;
  private static final EntityDataAccessor<Vector3f> SCALE_ACCESSOR;

  static {
    // "f_268512_" is the SRG name for DATA_BLOCK_STATE_ID in 1.20.1
    // "f_268597_" is the SRG name for DATA_SCALE_ID in 1.20.1
    BLOCK_STATE_ACCESSOR = ObfuscationReflectionHelper.getPrivateValue(Display.BlockDisplay.class, null, "f_268512_");
    SCALE_ACCESSOR = ObfuscationReflectionHelper.getPrivateValue(Display.class, null, "f_268597_");
  }

  public static void tick(ServerPlayer player, double borderAbsoluteRadius) {
    if (player.tickCount % 5 != 0)
      return;

    double viewRadius = 6.0;
    double viewRadiusSq = viewRadius * viewRadius;
    UUID uuid = player.getUUID();

    Map<BlockPos, Integer> activeForPlayer = playerActiveDisplays.computeIfAbsent(uuid, k -> new HashMap<>());
    Set<BlockPos> blocksInSphere = new HashSet<>();

    BlockPos spawnPos = player.level().getSharedSpawnPos();
    double pdx = player.getX() - spawnPos.getX();
    double pdz = player.getZ() - spawnPos.getZ();
    double distToSpawn = Math.sqrt(pdx * pdx + pdz * pdz);

    if (Math.abs(distToSpawn - borderAbsoluteRadius) <= viewRadius) {
      double cosAngle = (Math.pow(distToSpawn, 2) + Math.pow(borderAbsoluteRadius, 2) - viewRadiusSq)
          / (2 * distToSpawn * borderAbsoluteRadius);
      cosAngle = Math.max(-1, Math.min(1, cosAngle));
      double deltaAngle = Math.acos(cosAngle);
      double centerAngle = Math.atan2(pdz, pdx);
      double angleStep = 1.0 / borderAbsoluteRadius;

      for (double a = centerAngle - deltaAngle; a <= centerAngle + deltaAngle; a += angleStep) {
        int bx = (int) Math.floor(spawnPos.getX() + borderAbsoluteRadius * Math.cos(a));
        int bz = (int) Math.floor(spawnPos.getZ() + borderAbsoluteRadius * Math.sin(a));

        int playerY = player.getBlockY();
        for (int y = playerY - (int) viewRadius; y <= playerY + (int) viewRadius; y++) {
          BlockPos pos = new BlockPos(bx, y, bz);
          if (pos.distToCenterSqr(player.getX(), player.getY(), player.getZ()) <= viewRadiusSq) {
            blocksInSphere.add(pos.immutable());
          }
        }
      }
    }

    // Cleanup
    List<Integer> idsToRemove = new ArrayList<>();
    activeForPlayer.entrySet().removeIf(entry -> {
      if (!blocksInSphere.contains(entry.getKey())) {
        idsToRemove.add(entry.getValue());
        return true;
      }
      return false;
    });

    if (!idsToRemove.isEmpty()) {
      player.connection.send(new ClientboundRemoveEntitiesPacket(idsToRemove.stream().mapToInt(i -> i).toArray()));
    }

    // Spawn
    for (BlockPos pos : blocksInSphere) {
      if (!activeForPlayer.containsKey(pos)) {
        int safeId = FAKE_ID_COUNTER.getAndIncrement();
        spawnFakeBlockDisplay(player, pos, safeId);
        activeForPlayer.put(pos, safeId);
      }
    }
  }

  private static void spawnFakeBlockDisplay(ServerPlayer player, BlockPos pos, int entityId) {
    ClientboundAddEntityPacket addPacket = new ClientboundAddEntityPacket(
        entityId, UUID.randomUUID(),
        pos.getX(), pos.getY(), pos.getZ(),
        0, 0, EntityType.BLOCK_DISPLAY, 0, Vec3.ZERO, 0);

    // Create dummy entity to generate the data packet
    Display.BlockDisplay tempDisplay = new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, player.level());

    // Use the Reflection-fetched Accessors to set the data
    tempDisplay.getEntityData().set(BLOCK_STATE_ACCESSOR, Blocks.CYAN_STAINED_GLASS.defaultBlockState());
    tempDisplay.getEntityData().set(SCALE_ACCESSOR, new Vector3f(1.001f, 1.001f, 1.001f)); // Slightly larger to prevent
                                                                                           // flickering

    // Optional: Make it glow so it's bright even in caves
    tempDisplay.setGlowingTag(true);

    player.connection.send(addPacket);
    player.connection.send(new ClientboundSetEntityDataPacket(entityId, tempDisplay.getEntityData().packDirty()));
  }

  public static void cleanup(ServerPlayer player) {
    Map<BlockPos, Integer> active = playerActiveDisplays.remove(player.getUUID());
    if (active != null && !active.isEmpty()) {
      player.connection.send(new ClientboundRemoveEntitiesPacket(active.values().stream().mapToInt(i -> i).toArray()));
    }
  }
}