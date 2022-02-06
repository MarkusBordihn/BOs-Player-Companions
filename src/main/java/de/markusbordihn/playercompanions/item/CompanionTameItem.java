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
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.entity.CompanionEntity;
import de.markusbordihn.playercompanions.entity.TameablePlayerCompanion;
import de.markusbordihn.playercompanions.tabs.PlayerCompanionsTab;

public class CompanionTameItem extends Item {

  public static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public CompanionTameItem() {
    this(new Item.Properties().stacksTo(1).durability(16).tab(PlayerCompanionsTab.TAB_TAME_ITEMS));
  }

  public CompanionTameItem(Properties properties) {
    super(properties);
  }

  public void convertEntityToItem(Item companionItem, Player player, LivingEntity livingEntity,
      InteractionHand hand) {
    log.info("Convert Living Entity: {}", livingEntity);

    // Capture mob inside item.
    log.debug("Capturing mob {} ...", livingEntity.getName());
    ItemStack itemStack = new ItemStack(companionItem);
    CompoundTag compoundTag = itemStack.getOrCreateTag();
    compoundTag.putUUID(CapturedCompanion.COMPANION_UUID_TAG, livingEntity.getUUID());
    itemStack.save(compoundTag);

    // Discarded Entity from the world
    livingEntity.setRemoved(RemovalReason.DISCARDED);

    // Give Player new item
    player.getInventory().add(itemStack);
  }

  public boolean canTameCompanion(LivingEntity livingEntity) {
    return livingEntity instanceof CompanionEntity;
  }

  public boolean canTameCompanionType(String mobType) {
    return !mobType.isEmpty();
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
            if (livingEntity instanceof CompanionEntity companionEntity) {
              Item companionItem = companionEntity.getCompanionItem();
              if (companionItem != null) {
                convertEntityToItem(companionItem, player, livingEntity, hand);
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
    tooltipList.add(new TranslatableComponent(Constants.TEXT_PREFIX + "tame_companion"));
  }

}