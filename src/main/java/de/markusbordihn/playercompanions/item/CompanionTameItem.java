/*
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

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.config.CommonConfig;
import de.markusbordihn.playercompanions.data.PlayerCompanionsServerData;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.entity.TameablePlayerCompanion;
import de.markusbordihn.playercompanions.text.TranslatableText;
import de.markusbordihn.playercompanions.utils.TitleUtils;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CompanionTameItem extends Item {

  public static final Set<String> TAMEABLE_MOB_TYPES = Collections.emptySet();
  protected static final CommonConfig.Config COMMON = CommonConfig.COMMON;
  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public CompanionTameItem() {
    this(new Item.Properties());
  }

  public CompanionTameItem(Properties properties) {
    super(properties);
  }

  public void convertEntityToItem(
      Item companionItem, ServerPlayer serverPlayer, LivingEntity livingEntity, Level level) {

    // Capture mob inside item.
    log.debug("Capturing mob {} for {} with {}", livingEntity, serverPlayer, companionItem);
    ItemStack itemStack = new ItemStack(companionItem);
    CompoundTag compoundTag = itemStack.getOrCreateTag();
    compoundTag.putUUID(CapturedCompanion.COMPANION_UUID_TAG, livingEntity.getUUID());
    itemStack.save(compoundTag);

    // Confirm that we have a valid item before discard the entity from the world.
    if (itemStack.isEmpty() || !(itemStack.getItem() instanceof CapturedCompanion)) {
      log.error("Unable to create captured companion item, got {} instead!", itemStack);
      return;
    }

    // Try to give Player new item in inventory.
    ItemStack capturedCompanionItemStack = itemStack.copy();
    Inventory playerInventory = serverPlayer.getInventory();
    int freeInventorySlot = playerInventory.getFreeSlot();
    boolean gavePlayerItemStack = false;
    if (freeInventorySlot != -1) {
      gavePlayerItemStack = playerInventory.add(freeInventorySlot, capturedCompanionItemStack);
      playerInventory.setChanged();
    }

    // Confirm that the item is in the players inventory or drop the item near the player.
    if (gavePlayerItemStack
        && !playerInventory.getItem(freeInventorySlot).isEmpty()
        && playerInventory.getItem(freeInventorySlot).getItem() instanceof CapturedCompanion
        && CapturedCompanion.getCompanionUUID(playerInventory.getItem(freeInventorySlot))
            .equals(livingEntity.getUUID())) {
      log.info(
          "Gave player {} captured companion item {} in slot {} with {}.",
          serverPlayer,
          playerInventory.getItem(freeInventorySlot),
          freeInventorySlot,
          companionItem);
      TitleUtils.setTitle(
          Component.translatable(
              Constants.TEXT_PREFIX + "captured_companion_title",
              livingEntity.getName().getString()),
          Component.translatable(
                  Constants.TEXT_PREFIX + "captured_companion_subtitle_inventory",
                  freeInventorySlot)
              .withStyle(ChatFormatting.GRAY),
          serverPlayer);
    } else {
      BlockPos blockPos = livingEntity.blockPosition();
      if (!level.addFreshEntity(
          new ItemEntity(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), itemStack))) {
        log.error(
            "Unable to give item {} to player {} or to drop it to the world {}!",
            itemStack,
            serverPlayer,
            level);
        return;
      } else {
        // Log warning and the exact reason, if we are not able to give the item to the player.
        log.warn("Dropped captured companion item for {} with {}.", serverPlayer, itemStack);
        if (freeInventorySlot == -1) {
          log.warn("Reason: Inventory is full!");
        } else if (playerInventory.getItem(freeInventorySlot).getItem()
            instanceof CapturedCompanion) {
          log.warn(
              "Reason: Was not able to store it in the inventory slot {} found captured companion item {} with UUID {} instead!",
              freeInventorySlot,
              playerInventory.getItem(freeInventorySlot).getItem(),
              CapturedCompanion.getCompanionUUID(playerInventory.getItem(freeInventorySlot)));
        } else {
          log.warn(
              "Reason: Was not able to store it in the inventory slot {} found item {} instead!",
              freeInventorySlot,
              playerInventory.getItem(freeInventorySlot));
        }

        // Give player feedback that the item was dropped to the world and not added to the
        // inventory.
        TitleUtils.setTitle(
            Component.translatable(
                Constants.TEXT_PREFIX + "captured_companion_title",
                livingEntity.getName().getString()),
            Component.translatable(Constants.TEXT_PREFIX + "captured_companion_subtitle_ground")
                .withStyle(ChatFormatting.RED),
            serverPlayer);
      }
    }

    // Discarded Entity from the world, if we are able to give or drop the item to the player.
    livingEntity.setRemoved(RemovalReason.DISCARDED);
  }

  public boolean canTameCompanion(LivingEntity livingEntity) {
    return livingEntity instanceof PlayerCompanionEntity playerCompanionEntity
        && playerCompanionEntity.isTamable();
  }

  public boolean canTameCompanionType(String mobType) {
    return !mobType.isEmpty();
  }

  public Set<String> getTameableMobTypes() {
    return TAMEABLE_MOB_TYPES;
  }

  @Override
  public InteractionResult interactLivingEntity(
      ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand hand) {

    // Ignore players and dead entities for capturing.
    if (livingEntity == null || livingEntity instanceof Player || livingEntity.isDeadOrDying()) {
      return InteractionResult.FAIL;
    }

    // Check if we could catch the mob Type
    String mobType = EntityType.getKey(livingEntity.getType()).toString();
    if (!canTameCompanion(livingEntity) || !canTameCompanionType(mobType)) {
      return InteractionResult.FAIL;
    }

    // Check if player has reached the max number of companions.
    Level level = player.level();
    if (!level.isClientSide
        && player instanceof ServerPlayer serverPlayer
        && COMMON.companionLimitPerPlayer.get() > 0) {
      int numberOfCompanions = PlayerCompanionsServerData.get().getNumberOfCompanions(serverPlayer);
      int maxNumberOfCompanions = COMMON.companionLimitPerPlayer.get();

      // Inform player that he has reached the max number of companions.
      if (numberOfCompanions >= maxNumberOfCompanions) {
        TitleUtils.setTitle(
            Component.translatable(Constants.TEXT_PREFIX + "max_number_of_companions.title"),
            Component.translatable(
                    Constants.TEXT_PREFIX + "max_number_of_companions.subtitle",
                    numberOfCompanions,
                    maxNumberOfCompanions)
                .withStyle(ChatFormatting.RED),
            serverPlayer);
        return InteractionResult.FAIL;
      }
    }

    // Interact with entity if it is TameablePlayerCompanion compatible
    if (livingEntity instanceof TameablePlayerCompanion tameablePlayerCompanion) {
      if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
        if (tameablePlayerCompanion.canTamePlayerCompanion(itemStack, player, livingEntity, hand)) {
          InteractionResult result =
              tameablePlayerCompanion.tamePlayerCompanion(itemStack, player, livingEntity, hand);
          if (result == InteractionResult.SUCCESS) {
            if (livingEntity instanceof PlayerCompanionEntity companionEntity) {
              Item companionItem = companionEntity.getCompanionItem();
              if (companionItem != null) {
                convertEntityToItem(companionItem, serverPlayer, livingEntity, level);
              } else {
                log.error(
                    "Unable to capture companion entity {} in item {}!",
                    livingEntity,
                    companionItem);
              }
            } else {
              log.error("Capturing of entity {} is not implement!", livingEntity);
            }
          }
          return result;
        }
        return InteractionResult.FAIL;
      }
    }

    return InteractionResult.PASS;
  }

  @Override
  public boolean isFoil(ItemStack itemStack) {
    return true;
  }

  @Override
  public void appendHoverText(
      ItemStack itemStack,
      @Nullable Level level,
      List<Component> tooltipList,
      TooltipFlag tooltipFlag) {
    // Display description.
    tooltipList.add(Component.translatable(Constants.TEXT_PREFIX + "tame_companion"));

    // Display possible tameable mobs.
    Set<String> tameableMobTypes = getTameableMobTypes();
    if (!tameableMobTypes.isEmpty()) {
      MutableComponent tameableMobTypeOverview =
          Component.literal("").withStyle(ChatFormatting.DARK_GREEN);
      for (String tamableMob : tameableMobTypes) {
        Component acceptedMobName = TranslatableText.getEntityName(tamableMob);
        if (acceptedMobName != null) {
          tameableMobTypeOverview
              .append(acceptedMobName)
              .append(", ")
              .withStyle(ChatFormatting.DARK_GREEN);
        }
      }
      if (!tameableMobTypeOverview.getString().isBlank()) {
        MutableComponent tameableMobsOverview =
            Component.translatable(Constants.TEXT_PREFIX + "tameable_companions")
                .append(" ")
                .withStyle(ChatFormatting.GREEN);
        tameableMobsOverview.append(tameableMobTypeOverview).append("...");
        tooltipList.add(tameableMobsOverview);
      }
    }
  }
}
