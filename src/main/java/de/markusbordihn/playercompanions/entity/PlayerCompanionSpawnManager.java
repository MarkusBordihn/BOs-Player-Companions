/**
 * Copyright 2022 Markus Bordihn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.markusbordihn.playercompanions.entity;

import java.util.Iterator;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.data.PlayerCompanionData;
import de.markusbordihn.playercompanions.data.PlayerCompanionsServerData;

public class PlayerCompanionSpawnManager {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final String FALL_DISTANCE_TAG = "FallDistance";
  private static final String FIRE_TAG = "Fire";
  private static final String HEALTH_TAG = "Health";
  private static final String MOTION_TAG = "Motion";
  private static final String ON_GROUND_TAG = "OnGround";

  public static final String COMPANION_UUID_TAG = "CompanionUUID";

  protected PlayerCompanionSpawnManager() {

  }

  public static boolean spawn(UUID uuid, ServerPlayer serverPlayer) {
    return spawn(uuid, serverPlayer, serverPlayer.getLevel());
  }

  public static boolean spawn(UUID uuid, ServerPlayer serverPlayer, ServerLevel serverLevel) {
    return spawn(uuid, serverPlayer, serverLevel, serverPlayer.getOnPos().above());
  }

  public static boolean spawn(ItemStack itemStack, ServerPlayer serverPlayer,
      ServerLevel serverLevel, BlockPos blockPos) {
    return spawn(getCompanionUUID(itemStack), serverPlayer, serverLevel, blockPos);
  }

  public static boolean spawn(UUID uuid, ServerPlayer serverPlayer, ServerLevel serverLevel,
      BlockPos blockPos) {
    log.info("Spawn {} {} {} {}", uuid, blockPos, serverPlayer, serverLevel);

    // Check if UUID is valid.
    if (uuid == null) {
      log.error("Unable to find companion with UUID {} for player {} in {}.", uuid, serverPlayer,
          serverLevel);
      return false;
    }

    // Check if companion already exists in current level.
    Entity playerCompanion = getCompanionEntity(uuid, serverLevel);
    if (playerCompanion != null && playerCompanion.isAlive()) {
      log.debug("Found existing player companion {}", playerCompanion);
    }

    // Make sure companion doesn't exists in other levels.
    despawnCompanionExcept(uuid, serverLevel);

    // Remove dead companions before respawn.
    if (playerCompanion != null && !playerCompanion.isAlive()) {
      playerCompanion.remove(RemovalReason.KILLED);
      playerCompanion = null;
    }

    // If companion exits and alive, just port the companion to the player.
    if (playerCompanion != null && playerCompanion.isAlive()) {
      if (playerCompanion.closerThan(serverPlayer, 16)) {
        if (playerCompanion instanceof PlayerCompanionEntity playerCompanionEntity) {
          playerCompanionEntity.setOrderedToPosition(
              new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
        } else {
          serverPlayer.sendSystemMessage(Component.translatable(
              Constants.TEXT_PREFIX + "companion_is_near_you", playerCompanion.getName()));
        }
      } else {
        BlockState blockState = serverLevel.getBlockState(blockPos);
        if (isValidSpawnPlace(blockState)) {
          playerCompanion.teleportTo(blockPos.getX() + 0.5, blockPos.getY() + 0.5,
              blockPos.getZ() + 0.5);
          log.debug("Teleport player companion {} to position ...", playerCompanion, blockPos);
        } else {
          Vec3 playerPosition = serverPlayer.position();
          playerCompanion.teleportTo(playerPosition.x, playerPosition.y, playerPosition.z);
          log.debug("Teleport player companion {} to player ...", playerCompanion, serverPlayer);
        }
        return true;
      }
      return false;
    }

    // Prepare spawn of companion based on the existing data.
    Entity entity = PlayerCompanionSpawnManager.createCompanionEntity(uuid, serverLevel);
    if (entity != null) {
      // Make sure we have an empty Block to spawn the entity, otherwise try above block.
      BlockState blockState = serverLevel.getBlockState(blockPos);
      if (!isValidSpawnPlace(blockState)) {
        blockPos = blockPos.above();
        blockState = serverLevel.getBlockState(blockPos);
      }

      // Only spawn on empty blocks like air,water, grass, sea grass.
      if (isValidSpawnPlace(blockState)) {
        // Adjust entity position to spawn position.
        entity.setPos(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);

        // Add entity to the world.
        log.debug("Spawn player companion {} ...", entity);
        if (serverLevel.addFreshEntity(entity)) {
          playerCompanion = entity;
          if (entity instanceof PlayerCompanionEntity playerCompanionEntity) {
            playerCompanionEntity.finalizeSpawn();
          }
          return true;
        }
        return false;
      }
    }
    return false;
  }

  public static boolean despawn(UUID uuid, ServerLevel serverLevel) {
    if (getCompanionEntity(uuid,
        serverLevel) instanceof PlayerCompanionEntity playerCompanionEntity) {
      return PlayerCompanionSpawnManager.despawn(playerCompanionEntity);
    }
    return false;
  }

  public static boolean despawn(LivingEntity livingEntity) {
    // Ignore everything which is not a player companions entity.
    if (!(livingEntity instanceof PlayerCompanionEntity playerCompanionEntity)) {
      return false;
    }

    // Sync data before we despawn living entity.
    PlayerCompanionData playerCompanionData =
        PlayerCompanionsServerData.get().updatePlayerCompanion(playerCompanionEntity);
    playerCompanionData.syncEntityData(livingEntity);
    log.debug("Despawn companion {} with {} ...", livingEntity, playerCompanionData);

    // Discarded Entity from the world.
    livingEntity.setRemoved(RemovalReason.DISCARDED);

    return true;
  }

  public static Entity getCompanionEntity(UUID uuid, ServerLevel serverLevel) {
    return uuid != null ? serverLevel.getEntity(uuid) : null;
  }

  public static UUID getCompanionUUID(ItemStack itemStack) {
    CompoundTag compoundTag = itemStack.getOrCreateTag();
    if (compoundTag.hasUUID(COMPANION_UUID_TAG)) {
      return compoundTag.getUUID(COMPANION_UUID_TAG);
    }
    return null;
  }

  public static void despawnCompanionExcept(UUID uuid, ServerLevel serverLevel) {
    MinecraftServer server = serverLevel.getServer();
    Iterator<ServerLevel> serverLevels = server.getAllLevels().iterator();
    while (serverLevels.hasNext()) {
      ServerLevel serverLevelToCheck = serverLevels.next();
      if (serverLevelToCheck != serverLevel) {
        Entity playerCompanion = getCompanionEntity(uuid, serverLevelToCheck);
        if (playerCompanion != null) {
          playerCompanion.remove(RemovalReason.CHANGED_DIMENSION);
        }
      }
    }
  }

  public static Entity createCompanionEntity(ItemStack itemStack, Level level) {
    if (level instanceof ServerLevel serverLevel) {
      return createCompanionEntity(itemStack, serverLevel);
    }
    return null;
  }

  public static Entity createCompanionEntity(ItemStack itemStack, ServerLevel serverLevel) {
    PlayerCompanionData playerCompanion = PlayerCompanionsServerData.get().getCompanion(itemStack);
    if (playerCompanion == null) {
      log.error("Unable to find player companion for item {}", itemStack);
      return null;
    }
    return createCompanionEntity(playerCompanion, serverLevel);
  }

  public static Entity createCompanionEntity(UUID uuid, ServerLevel serverLevel) {
    PlayerCompanionData playerCompanion = PlayerCompanionsServerData.get().getCompanion(uuid);
    if (playerCompanion == null) {
      log.error("Unable to find player companion with UUID {}", uuid);
      return null;
    }
    return createCompanionEntity(playerCompanion, serverLevel);
  }

  public static Entity createCompanionEntity(PlayerCompanionData playerCompanion,
      ServerLevel serverLevel) {
    EntityType<?> entityType = playerCompanion.getEntityType();
    CompoundTag entityData = playerCompanion.getEntityData();
    Entity entity = entityType.create(serverLevel);
    if (entity != null && !entityData.isEmpty()) {

      // Restore health, if needed
      if (entityData.contains(HEALTH_TAG) && entityData.getFloat(HEALTH_TAG) <= 0) {
        entityData.putFloat(HEALTH_TAG, playerCompanion.getEntityHealthMax());
      }

      // Remove negative effects
      if (entityData.contains(FIRE_TAG) && entityData.getShort(FIRE_TAG) > 0) {
        entityData.putShort(FIRE_TAG, (short) 0);
      }
      if (entityData.contains(FALL_DISTANCE_TAG) && entityData.getFloat(FALL_DISTANCE_TAG) > 0) {
        entityData.putFloat(FALL_DISTANCE_TAG, 0);
      }
      if (entityData.contains(MOTION_TAG)) {
        entityData.put(MOTION_TAG, newDoubleList(0, 0, 0));
      }
      if (entityData.contains(ON_GROUND_TAG) && !entityData.getBoolean(ON_GROUND_TAG)) {
        entityData.putBoolean(ON_GROUND_TAG, true);
      }

      entity.load(entityData);
    }
    return entity;
  }

  public static boolean isValidSpawnPlace(BlockState blockState) {
    return blockState.isAir() || blockState.is(Blocks.WATER) || blockState.is(Blocks.GRASS)
        || blockState.is(Blocks.SEAGRASS) || blockState.is(Blocks.SNOW)
        || blockState.is(Blocks.FERN);
  }

  private static ListTag newDoubleList(double... values) {
    ListTag listTag = new ListTag();
    for (double value : values) {
      listTag.add(DoubleTag.valueOf(value));
    }
    return listTag;
  }

}
