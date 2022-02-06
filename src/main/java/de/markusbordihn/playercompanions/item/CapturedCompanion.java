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

package de.markusbordihn.playercompanions.item;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.data.PlayerCompanion;
import de.markusbordihn.playercompanions.data.PlayerCompanionsClientData;
import de.markusbordihn.playercompanions.data.PlayerCompanionsServerData;
import de.markusbordihn.playercompanions.tabs.PlayerCompanionsTab;

public class CapturedCompanion extends Item {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final String HEALTH_TAG = "Health";
  public static final String COMPANION_UUID_TAG = "CompanionUUID";

  public CapturedCompanion() {
    this(new Item.Properties().stacksTo(1).durability(16).tab(PlayerCompanionsTab.TAB_COMPANIONS));
  }

  public CapturedCompanion(Properties properties) {
    super(properties);
  }

  public boolean hasCompanion(ItemStack itemStack) {
    return getCompanionUUID(itemStack) != null;
  }

  public UUID getCompanionUUID(ItemStack itemStack) {
    CompoundTag compoundTag = itemStack.getOrCreateTag();
    if (compoundTag.hasUUID(COMPANION_UUID_TAG)) {
      return compoundTag.getUUID(COMPANION_UUID_TAG);
    }
    return null;
  }

  public Entity getCompanionEntity(ItemStack itemStack, ServerLevel serverLevel) {
    UUID companionUUID = getCompanionUUID(itemStack);
    return serverLevel.getEntity(companionUUID);
  }

  public boolean spawnCompanion(ItemStack itemStack, BlockPos blockPos, Player player,
      Level level) {
    Entity playerCompanion = null;

    // Check if companion already exists
    if (level instanceof ServerLevel serverLevel) {
      playerCompanion = getCompanionEntity(itemStack, serverLevel);
      log.info("Found existing player companion {}", playerCompanion);
    }

    // If companion exits, just port the companion to the player.
    if (playerCompanion != null && playerCompanion.isAlive()) {

      if (playerCompanion.closerThan(player, 16)) {
        player
            .sendMessage(new TranslatableComponent(Constants.TEXT_PREFIX + "companion_is_near_you",
                playerCompanion.getName()), Util.NIL_UUID);
      } else {
        Vec3 playerPosition = player.position();
        playerCompanion.teleportTo(playerPosition.x, playerPosition.y, playerPosition.z);
        log.info("Teleport existing companion {}", playerCompanion);
        return true;
      }
      return false;
    }

    // Prepare spawn of companion based on the existing data.
    Entity entity = getCompanionEntity(itemStack, level);
    if (entity != null) {
      // Make sure we have an empty Block to spawn the entity, otherwise try above block.
      BlockState blockState = level.getBlockState(blockPos);
      if (!blockState.isAir() && !blockState.is(Blocks.WATER) && !blockState.is(Blocks.GRASS)
          && !blockState.is(Blocks.SEAGRASS)) {
        blockPos = blockPos.above();
        blockState = level.getBlockState(blockPos);
      }

      // Only spawn on empty blocks like air,water, grass, sea grass.
      if (blockState.is(Blocks.AIR) || blockState.is(Blocks.WATER) || blockState.is(Blocks.GRASS)
          || blockState.is(Blocks.SEAGRASS)) {
        // Adjust entity position to spawn position.
        entity.setPosRaw(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        // Add entity to the world.
        log.debug("Spawn companion {} with {}", entity);
        if (level.addFreshEntity(entity)) {
          playerCompanion = entity;
          return true;
        }
        return false;
      }
    }
    return false;
  }

  public boolean despawnCompanion() {
    return true;
  }

  public Entity getCompanionEntity(ItemStack itemStack, Level level) {
    PlayerCompanion playerCompanion = PlayerCompanionsServerData.get().getCompanion(itemStack);
    EntityType<?> entityType = playerCompanion.getEntityType();
    CompoundTag entityData = playerCompanion.getEntityData();
    Entity entity = entityType.create(level);
    if (entity != null && !entityData.isEmpty()) {

      // Restore health, if needed
      if (entityData.contains(HEALTH_TAG) && entityData.getFloat(HEALTH_TAG) <= 0.0) {
        entityData.putFloat(HEALTH_TAG, 8.0F);
      }
      entity.load(entityData);
    }
    return entity;
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {

    // Ignore client side.
    Level level = context.getLevel();
    if (level.isClientSide) {
      return InteractionResult.SUCCESS;
    }

    BlockPos blockPos = context.getClickedPos();
    BlockState blockState = level.getBlockState(blockPos);
    ItemStack itemStack = context.getItemInHand();
    Player player = context.getPlayer();

    if (!hasCompanion(itemStack)) {
      return InteractionResult.FAIL;
    }

    // Check if there is any active respawn timer.
    PlayerCompanion playerCompanion = PlayerCompanionsServerData.get().getCompanion(itemStack);
    if (playerCompanion != null && playerCompanion.hasEntityRespawnTimer()
        && playerCompanion.getEntityRespawnTimer() > java.time.Instant.now().getEpochSecond()) {
      log.info("Companion could respawn in {} secs ...",
          playerCompanion.getEntityRespawnTimer() - java.time.Instant.now().getEpochSecond());
      return InteractionResult.FAIL;
    }

    // Place directly on grass or similar blocks.
    if ((blockState.is(Blocks.GRASS) || blockState.is(Blocks.SEAGRASS))
        && spawnCompanion(itemStack, blockPos, player, level)) {
      return InteractionResult.CONSUME;
    }

    // Check if we can release the captured mob above.
    BlockPos blockPosAbove = blockPos.above();
    BlockState blockStateBlockAbove = level.getBlockState(blockPosAbove);
    if ((blockStateBlockAbove.is(Blocks.AIR) || blockStateBlockAbove.is(Blocks.WATER))
        && spawnCompanion(itemStack, blockPosAbove, player, level)) {
      return InteractionResult.CONSUME;
    }


    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  @Override
  public boolean isBarVisible(ItemStack itemStack) {
    return true;
  }

  @Override
  public int getUseDuration(ItemStack itemStack) {
    return 32000;
  }

  @Override
  public int getBarWidth(ItemStack itemStack) {
    PlayerCompanion playerCompanion = PlayerCompanionsClientData.getCompanion(itemStack);
    if (playerCompanion != null) {
      return Math
          .round(playerCompanion.getEntityHealth() * (13f / playerCompanion.getEntityHealthMax()));
    }
    return super.getBarWidth(itemStack);
  }

  @Override
  public int getBarColor(ItemStack itemStack) {
    PlayerCompanion playerCompanion = PlayerCompanionsClientData.getCompanion(itemStack);
    if (playerCompanion != null) {
      float color =
          Math.max(0.0F, playerCompanion.getEntityHealth() / playerCompanion.getEntityHealthMax());
      return Mth.hsvToRgb(color / 3.0F, 1.0F, 1.0F);
    }
    return super.getBarColor(itemStack);
  }

  @Override
  public void appendHoverText(ItemStack itemStack, @Nullable Level level,
      List<Component> tooltipList, TooltipFlag tooltipFlag) {
    PlayerCompanion playerCompanion = PlayerCompanionsClientData.getCompanion(itemStack);
    if (playerCompanion != null) {
      tooltipList.add(new TranslatableComponent(Constants.TEXT_PREFIX + "tamed_companion_name",
          playerCompanion.getName()).withStyle(ChatFormatting.GOLD));
      tooltipList.add(new TranslatableComponent(Constants.TEXT_PREFIX + "tamed_companion_health",
          playerCompanion.getEntityHealth()));
      tooltipList.add(new TranslatableComponent(Constants.TEXT_PREFIX + "tamed_companion_owner",
          playerCompanion.getOwnerName()));
      tooltipList.add(new TranslatableComponent(Constants.TEXT_PREFIX + "tamed_companion_food"));
    }
  }

}
