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

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.data.Experience;
import de.markusbordihn.playercompanions.data.PlayerCompanionData;
import de.markusbordihn.playercompanions.data.PlayerCompanionsClientData;
import de.markusbordihn.playercompanions.data.PlayerCompanionsServerData;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.entity.PlayerCompanionVariant;
import de.markusbordihn.playercompanions.tabs.PlayerCompanionsTab;
import de.markusbordihn.playercompanions.text.TranslatableText;

public class CapturedCompanion extends Item {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final String FALL_DISTANCE_TAG = "FallDistance";
  private static final String FIRE_TAG = "Fire";
  private static final String HEALTH_TAG = "Health";
  private static final String MOTION_TAG = "Motion";
  private static final String ON_GROUND_TAG = "OnGround";

  public static final String COMPANION_UUID_TAG = "CompanionUUID";

  private PlayerCompanionVariant variant = PlayerCompanionVariant.DEFAULT;

  public CapturedCompanion() {
    this(new Item.Properties().stacksTo(1).durability(16).tab(PlayerCompanionsTab.TAB_COMPANIONS));
  }

  public CapturedCompanion(PlayerCompanionVariant variant) {
    this();
    this.variant = variant;
  }

  public CapturedCompanion(Properties properties) {
    super(properties);
  }

  public boolean hasCompanion(ItemStack itemStack) {
    return getCompanionUUID(itemStack) != null;
  }

  public boolean hasValidCompanion(ItemStack itemStack) {
    return PlayerCompanionsServerData.get().hasCompanion(itemStack);
  }

  public UUID getCompanionUUID(ItemStack itemStack) {
    CompoundTag compoundTag = itemStack.getOrCreateTag();
    if (compoundTag.hasUUID(COMPANION_UUID_TAG)) {
      return compoundTag.getUUID(COMPANION_UUID_TAG);
    }
    return null;
  }

  private CompoundTag setCompanionUUID(ItemStack itemStack, UUID uuid) {
    CompoundTag compoundTag = itemStack.getOrCreateTag();
    compoundTag.putUUID(COMPANION_UUID_TAG, uuid);
    return compoundTag;
  }


  public Entity getCompanionEntity(ItemStack itemStack, ServerLevel serverLevel) {
    UUID companionUUID = getCompanionUUID(itemStack);
    return serverLevel.getEntity(companionUUID);
  }

  public boolean spawnCompanion(ItemStack itemStack, BlockPos blockPos, Player player,
      Level level) {
    Entity playerCompanion = null;

    // Check if companion already exists in current level.
    if (level instanceof ServerLevel serverLevel) {
      playerCompanion = getCompanionEntity(itemStack, serverLevel);
      if (playerCompanion != null && playerCompanion.isAlive()) {
        log.debug("Found existing player companion {}", playerCompanion);
      }
      // Make sure companion doesn't exists in other levels
      despawnCompanionExcept(itemStack, serverLevel);
    }

    // Remove dead companions before respawn.
    if (playerCompanion != null && !playerCompanion.isAlive()) {
      playerCompanion.remove(RemovalReason.KILLED);
      playerCompanion = null;
    }

    // If companion exits and alive, just port the companion to the player.
    if (playerCompanion != null && playerCompanion.isAlive()) {

      if (playerCompanion.closerThan(player, 16)) {
        if (playerCompanion instanceof PlayerCompanionEntity playerCompanionEntity) {
          playerCompanionEntity.setOrderedToPosition(
              new BlockPos(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
        } else {
          player.sendSystemMessage(Component.translatable(
              Constants.TEXT_PREFIX + "companion_is_near_you", playerCompanion.getName()));
        }
      } else {
        BlockState blockState = level.getBlockState(blockPos);
        if (isValidSpawnPlace(blockState)) {
          playerCompanion.teleportTo(blockPos.getX() + 0.5, blockPos.getY() + 0.5,
              blockPos.getZ() + 0.5);
          log.debug("Teleport companion {} to position ...", playerCompanion, blockPos);
        } else {
          Vec3 playerPosition = player.position();
          playerCompanion.teleportTo(playerPosition.x, playerPosition.y, playerPosition.z);
          log.debug("Teleport companion {} to player ...", playerCompanion, player);
        }
        return true;
      }
      return false;
    }

    // Prepare spawn of companion based on the existing data.
    Entity entity = createCompanionEntity(itemStack, level);
    if (entity != null) {
      // Make sure we have an empty Block to spawn the entity, otherwise try above block.
      BlockState blockState = level.getBlockState(blockPos);
      if (!isValidSpawnPlace(blockState)) {
        blockPos = blockPos.above();
        blockState = level.getBlockState(blockPos);
      }

      // Only spawn on empty blocks like air,water, grass, sea grass.
      if (isValidSpawnPlace(blockState)) {
        // Adjust entity position to spawn position.
        entity.setPos(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5);

        // Add entity to the world.
        log.debug("Spawn companion {} ...", entity);
        if (level.addFreshEntity(entity)) {
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

  public boolean despawnCompanion(LivingEntity livingEntity, ItemStack itemStack) {
    UUID capturedCompanionUUID = getCompanionUUID(itemStack);
    if (!(livingEntity instanceof PlayerCompanionEntity playerCompanionEntity)
        || !playerCompanionEntity.getUUID().equals(capturedCompanionUUID)) {
      return false;
    }

    // Sync data before we despawn entity.
    PlayerCompanionData playerCompanionData =
        PlayerCompanionsServerData.get().updatePlayerCompanion(playerCompanionEntity);
    playerCompanionData.syncEntityData(livingEntity);
    log.debug("Despawn companion {} with {} ...", livingEntity, playerCompanionData);

    // Discarded Entity from the world.
    livingEntity.setRemoved(RemovalReason.DISCARDED);

    return true;
  }

  public void despawnCompanionExcept(ItemStack itemStack, ServerLevel serverLevel) {
    MinecraftServer server = serverLevel.getServer();
    Iterator<ServerLevel> serverLevels = server.getAllLevels().iterator();
    while (serverLevels.hasNext()) {
      ServerLevel serverLevelToCheck = serverLevels.next();
      if (serverLevelToCheck != serverLevel) {
        Entity playerCompanion = getCompanionEntity(itemStack, serverLevelToCheck);
        if (playerCompanion != null) {
          playerCompanion.remove(RemovalReason.CHANGED_DIMENSION);
        }
      }
    }
  }

  public boolean createTamedCompanion(ItemStack itemStack, Player player, Level level) {
    EntityType<?> entityType = this.getEntityType();
    if (entityType != null) {
      Entity entity = entityType.create(level);
      if (entity instanceof PlayerCompanionEntity playerCompanionEntity) {
        if (this.variant != null && this.variant != PlayerCompanionVariant.DEFAULT) {
          playerCompanionEntity.setVariant(this.variant);
        }
        if (itemStack.hasCustomHoverName()) {
          playerCompanionEntity.setCustomName(itemStack.getHoverName());
        }
        playerCompanionEntity.finalizeSpawn();
        playerCompanionEntity.tame(player);
        playerCompanionEntity.follow();
        setCompanionUUID(itemStack, entity.getUUID());
        return true;
      }
    }
    return false;
  }

  public Entity createCompanionEntity(ItemStack itemStack, Level level) {
    PlayerCompanionData playerCompanion = PlayerCompanionsServerData.get().getCompanion(itemStack);
    if (playerCompanion == null) {
      log.error("Unable to find player companion with UUID {} for item {}",
          getCompanionUUID(itemStack), itemStack);
      return null;
    }
    EntityType<?> entityType = playerCompanion.getEntityType();
    CompoundTag entityData = playerCompanion.getEntityData();
    Entity entity = entityType.create(level);
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
        entityData.put(MOTION_TAG, this.newDoubleList(0, 0, 0));
      }
      if (entityData.contains(ON_GROUND_TAG) && !entityData.getBoolean(ON_GROUND_TAG)) {
        entityData.putBoolean(ON_GROUND_TAG, true);
      }

      entity.load(entityData);
    }
    return entity;
  }

  public EntityType<?> getEntityType() {
    return null;
  }

  public Ingredient getEntityFood() {
    return null;
  }

  private boolean isValidSpawnPlace(BlockState blockState) {
    return blockState.isAir() || blockState.is(Blocks.WATER) || blockState.is(Blocks.GRASS)
        || blockState.is(Blocks.SEAGRASS) || blockState.is(Blocks.SNOW)
        || blockState.is(Blocks.FERN);
  }

  @Override
  public Component getName(ItemStack itemStack) {
    PlayerCompanionData playerCompanion = PlayerCompanionsClientData.getCompanion(itemStack);
    if (playerCompanion != null) {
      Component.translatable(this.getDescriptionId(itemStack))
          .append(Component.literal(": " + playerCompanion.getName()));
    }
    return Component.translatable(this.getDescriptionId(itemStack));
  }

  @Override
  public InteractionResult interactLivingEntity(ItemStack itemStack, Player player,
      LivingEntity livingEntity, InteractionHand hand) {

    // Check if we have any captured companion.
    if (!hasCompanion(itemStack)) {
      return InteractionResult.FAIL;
    }

    // Try to despawn companion (server-side only).
    Level level = player.getLevel();
    if (!level.isClientSide() && despawnCompanion(livingEntity, itemStack)) {
      return InteractionResult.CONSUME;
    }

    return InteractionResult.sidedSuccess(level.isClientSide);
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

    // Check if we have any captured companion, if not try to create one.
    if (!hasCompanion(itemStack) && !createTamedCompanion(itemStack, player, level)) {
      return InteractionResult.FAIL;
    }

    // Check if there is a valid companion.
    if (!PlayerCompanionsServerData.get().hasCompanion(itemStack)) {
      log.error("Unable to find player companion with UUID {} for item {}",
          getCompanionUUID(itemStack), itemStack);
      return InteractionResult.FAIL;
    }

    // Check if there is any active respawn timer.
    PlayerCompanionData playerCompanion = PlayerCompanionsServerData.get().getCompanion(itemStack);
    if (playerCompanion != null && playerCompanion.hasEntityRespawnTimer()
        && playerCompanion.getEntityRespawnTimer() > java.time.Instant.now().getEpochSecond()) {
      return InteractionResult.FAIL;
    }

    // Place directly on grass or similar blocks.
    if (isValidSpawnPlace(blockState) && spawnCompanion(itemStack, blockPos, player, level)) {
      return InteractionResult.CONSUME;
    }

    // Check if we can release the captured mob above.
    BlockPos blockPosAbove = blockPos.above();
    BlockState blockStateBlockAbove = level.getBlockState(blockPosAbove);
    if (isValidSpawnPlace(blockStateBlockAbove)
        && spawnCompanion(itemStack, blockPosAbove, player, level)) {
      return InteractionResult.CONSUME;
    }

    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  protected ListTag newDoubleList(double... values) {
    ListTag listTag = new ListTag();
    for (double value : values) {
      listTag.add(DoubleTag.valueOf(value));
    }
    return listTag;
  }

  @Override
  public boolean isBarVisible(ItemStack itemStack) {
    PlayerCompanionData playerCompanion = PlayerCompanionsClientData.getCompanion(itemStack);
    return playerCompanion != null;
  }

  @Override
  public int getUseDuration(ItemStack itemStack) {
    return 32000;
  }

  @Override
  public int getBarWidth(ItemStack itemStack) {
    PlayerCompanionData playerCompanion = PlayerCompanionsClientData.getCompanion(itemStack);
    if (playerCompanion != null) {
      return Math
          .round(playerCompanion.getEntityHealth() * (13f / playerCompanion.getEntityHealthMax()));
    }
    return super.getBarWidth(itemStack);
  }

  @Override
  public int getBarColor(ItemStack itemStack) {
    PlayerCompanionData playerCompanion = PlayerCompanionsClientData.getCompanion(itemStack);
    if (playerCompanion != null) {
      float barColor =
          Math.max(0.0F, playerCompanion.getEntityHealth() / playerCompanion.getEntityHealthMax());
      return Mth.hsvToRgb(barColor / 3.0F, 1.0F, 1.0F);
    }
    return super.getBarColor(itemStack);
  }

  @Override
  public boolean isEnchantable(ItemStack itemStack) {
    return false;
  }

  @Override
  public void appendHoverText(ItemStack itemStack, @Nullable Level level,
      List<Component> tooltipList, TooltipFlag tooltipFlag) {


    if (itemStack.getItem() instanceof CapturedCompanion capturedCompanion) {
      PlayerCompanionData playerCompanion = PlayerCompanionsClientData.getCompanion(itemStack);

      // Display capture companion specific information.
      if (playerCompanion != null) {
        tooltipList.add(Component
            .translatable(Constants.TEXT_PREFIX + "tamed_companion_name", playerCompanion.getName())
            .withStyle(ChatFormatting.GOLD));
        tooltipList.add(Component.translatable(Constants.TEXT_PREFIX + "tamed_companion_health",
            playerCompanion.getEntityHealth(), playerCompanion.getEntityHealthMax()));
        tooltipList.add(Component.translatable(Constants.TEXT_PREFIX + "tamed_companion_owner",
            playerCompanion.getOwnerName()));
      }

      // Display companion Type
      if (playerCompanion != null) {
        tooltipList.add(Component
            .translatable(Constants.TEXT_PREFIX + "tamed_companion_type", playerCompanion.getType())
            .withStyle(ChatFormatting.GRAY));
      }

      if (playerCompanion != null) {

        // Add experience and level, if available.
        if (playerCompanion.getExperience() > 0) {
          tooltipList.add(Component.translatable(Constants.TEXT_PREFIX + "tamed_companion_level",
              playerCompanion.getExperienceLevel(), playerCompanion.getExperience(),
              Experience.getExperienceForNextLevel(playerCompanion.getExperienceLevel())));
        }

        // Handle respawn timer, if any.
        long respawnTimer =
            playerCompanion.getEntityRespawnTimer() - java.time.Instant.now().getEpochSecond();
        if (respawnTimer <= 0) {
          if (playerCompanion.isSittingOnShoulder()) {
            tooltipList.add(Component
                .translatable(Constants.TEXT_PREFIX + "tamed_companion_status_sit_on_shoulder"));
          } else if (playerCompanion.isOrderedToPosition()) {
            tooltipList.add(Component
                .translatable(Constants.TEXT_PREFIX + "tamed_companion_status_order_to_position"));
          } else if (playerCompanion.isOrderedToSit()) {
            tooltipList.add(Component
                .translatable(Constants.TEXT_PREFIX + "tamed_companion_status_order_to_sit"));
          } else {
            tooltipList.add(Component
                .translatable(Constants.TEXT_PREFIX + "tamed_companion_status_order_to_follow"));
          }
        } else {
          tooltipList
              .add(Component.translatable(Constants.TEXT_PREFIX + "tamed_companion_status_dead"));
        }

        // Display respawn timer, if any.
        if (respawnTimer >= 0) {
          tooltipList.add(Component
              .translatable(Constants.TEXT_PREFIX + "tamed_companion_respawn", respawnTimer)
              .withStyle(ChatFormatting.RED));
        }

        tooltipList.add(Component.translatable(Constants.TEXT_PREFIX + "tamed_companion_dimension",
            playerCompanion.getDimensionName()).withStyle(ChatFormatting.GRAY));
      }

      // Display entity food.
      if (capturedCompanion.getEntityFood() != null) {
        MutableComponent foodOverview = Component.literal("").withStyle(ChatFormatting.DARK_GREEN);
        for (ItemStack foodItemStack : capturedCompanion.getEntityFood().getItems()) {
          foodOverview.append(TranslatableText.getItemName(foodItemStack)).append(", ");
        }
        foodOverview.append("...");
        tooltipList.add(Component.translatable(Constants.TEXT_PREFIX + "tamed_companion_food")
            .withStyle(ChatFormatting.GREEN).append(foodOverview));
      }

      // Display Debug information
      if (playerCompanion == null) {
        UUID uuid = getCompanionUUID(itemStack);
        if (uuid != null) {
          tooltipList.add(Component.literal("UUID: " + uuid).withStyle(ChatFormatting.GRAY));
        }
      }
    }
  }

}
