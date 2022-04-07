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

import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.entity.TameablePlayerCompanion;
import de.markusbordihn.playercompanions.tabs.PlayerCompanionsTab;
import de.markusbordihn.playercompanions.text.TranslatableText;

public class CompanionTameItem extends Item {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public static final Set<String> TAMEABLE_MOB_TYPES = Collections.emptySet();

  public CompanionTameItem() {
    this(new Item.Properties().tab(PlayerCompanionsTab.TAB_TAME_ITEMS));
  }

  public CompanionTameItem(Properties properties) {
    super(properties);
  }

  public void convertEntityToItem(Item companionItem, Player player, LivingEntity livingEntity,
      Level level) {

    // Capture mob inside item.
    log.debug("Capturing mob {} for {} with {}", livingEntity, player, companionItem);
    ItemStack itemStack = new ItemStack(companionItem);
    CompoundTag compoundTag = itemStack.getOrCreateTag();
    compoundTag.putUUID(CapturedCompanion.COMPANION_UUID_TAG, livingEntity.getUUID());
    itemStack.save(compoundTag);

    // Confirm that we have a valid item before discard the entity from the world.
    if (itemStack.isEmpty() || !(itemStack.getItem() instanceof CapturedCompanion)) {
      log.error("Unable to create captured companion item, got {} instead!", itemStack);
      return;
    }

    // Discarded Entity from the world
    livingEntity.setRemoved(RemovalReason.DISCARDED);

    // Try to give Player new item in inventory or drop item.
    Inventory playerInventory = player.getInventory();
    if (playerInventory == null || playerInventory.getFreeSlot() == -1
        || !playerInventory.add(itemStack)) {
      BlockPos blockPos = livingEntity.blockPosition();
      if (!level.addFreshEntity(
          new ItemEntity(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), itemStack))) {
        log.error("Unable to give item {} to player {} or to drop it to the world {}!", itemStack,
            player, level);
      } else {
        log.warn("Dropped captured companion item for {} with {}, because inventory is full!",
            player, itemStack);
      }
    } else {
      log.info("Gave Player {} captured companion item with {}.", player, companionItem);
    }
  }

  public boolean canTameCompanion(LivingEntity livingEntity) {
    return livingEntity instanceof PlayerCompanionEntity;
  }

  public boolean canTameCompanionType(String mobType) {
    return !mobType.isEmpty();
  }

  public Set<String> getTameableMobTypes() {
    return TAMEABLE_MOB_TYPES;
  }

  @Override
  public InteractionResult interactLivingEntity(ItemStack itemStack, Player player,
      LivingEntity livingEntity, InteractionHand hand) {

    // Ignore players and dead entities for capturing.
    if (livingEntity == null || livingEntity instanceof Player || livingEntity.isDeadOrDying()) {
      return InteractionResult.FAIL;
    }

    // Check if we could catch the mob Type
    String mobType = livingEntity.getType().getRegistryName().toString();
    if (!canTameCompanion(livingEntity) || !canTameCompanionType(mobType)) {
      return InteractionResult.FAIL;
    }

    // Interact with entity if it is TameablePlayerCompanion compatible
    if (livingEntity instanceof TameablePlayerCompanion tameablePlayerCompanion) {
      Level level = player.level;
      if (!level.isClientSide) {
        if (tameablePlayerCompanion.canTamePlayerCompanion(itemStack, player, livingEntity, hand)) {
          InteractionResult result =
              tameablePlayerCompanion.tamePlayerCompanion(itemStack, player, livingEntity, hand);
          if (result == InteractionResult.SUCCESS) {
            if (livingEntity instanceof PlayerCompanionEntity companionEntity) {
              Item companionItem = companionEntity.getCompanionItem();
              if (companionItem != null) {
                convertEntityToItem(companionItem, player, livingEntity, level);
              } else {
                log.error("Unable to capture companion entity {} in item {}!", livingEntity,
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
  public void appendHoverText(ItemStack itemStack, @Nullable Level level,
      List<Component> tooltipList, TooltipFlag tooltipFlag) {
    // Display description.
    tooltipList.add(new TranslatableComponent(Constants.TEXT_PREFIX + "tame_companion"));

    // Display possible tameable mobs.
    Set<String> tameableMobTypes = getTameableMobTypes();
    if (!tameableMobTypes.isEmpty()) {
      TranslatableComponent tameableMobTypeOverview =
          (TranslatableComponent) new TranslatableComponent("")
              .withStyle(ChatFormatting.DARK_GREEN);
      for (String tamableMob : tameableMobTypes) {
        TranslatableComponent acceptedMobName = TranslatableText.getEntityName(tamableMob);
        if (!acceptedMobName.getString().isBlank()) {
          tameableMobTypeOverview.append(acceptedMobName).append(", ")
              .withStyle(ChatFormatting.DARK_GREEN);
        }
      }
      if (!tameableMobTypeOverview.getString().isBlank()) {
        TranslatableComponent tameableMobsOverview =
            (TranslatableComponent) new TranslatableComponent(
                Constants.TEXT_PREFIX + "tameable_companions").append(" ")
                    .withStyle(ChatFormatting.GREEN);
        tameableMobsOverview.append(tameableMobTypeOverview).append("...");
        tooltipList.add(tameableMobsOverview);
      }
    }
  }

}
