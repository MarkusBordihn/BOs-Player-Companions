/*
 * Copyright 2026 Markus Bordihn
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

package de.markusbordihn.playercompanions.entity.taming;

import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import de.markusbordihn.easynpc.entity.easynpc.data.OwnerDataCapable;
import de.markusbordihn.easynpc.handler.OwnerHandler;
import de.markusbordihn.playercompanions.config.CompanionNameConfig;
import de.markusbordihn.playercompanions.entity.CompanionRelationship;
import de.markusbordihn.playercompanions.entity.companion.PlayerCompanion;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class TamingInteractionHandler {

  private TamingInteractionHandler() {
  }

  public static InteractionResult handleTamingInteraction(
    PlayerCompanion companion, Player player, InteractionHand hand) {

    if (companion instanceof EasyNPC<?> easyNPC) {
      OwnerDataCapable<?> ownerData = easyNPC.getEasyNPCOwnerData();
      if (ownerData != null && ownerData.getOwnerUUID() != null) {
        return InteractionResult.PASS;
      }
    }

    CompanionRelationship relationship = companion.getRelationship();
    if (relationship == null) {
      return InteractionResult.PASS;
    }

    ItemStack heldItem = player.getItemInHand(hand);
    if (heldItem.isEmpty()) {
      return InteractionResult.PASS;
    }

    int trustValue =
      CompanionFoodRegistry.getTrustValue(companion.getType(), heldItem.getItem());
    if (trustValue <= 0) {
      return InteractionResult.PASS;
    }

    if (companion.level().isClientSide) {
      return InteractionResult.CONSUME;
    }

    if (TamingHandler.hasReachedCompanionLimit(player)) {
      TamingFeedbackHandler.handleLimitReachedFeedback(companion, player);
      return InteractionResult.CONSUME;
    }

    TameResult result =
      TamingHandler.handleFeeding(
        relationship, trustValue, player, companion.level().getGameTime());

    switch (result) {
      case SUCCESS:
        TamingFeedbackHandler.handleSuccessFeedback(
          companion, player, trustValue, relationship.getLevel());
        if (!player.getAbilities().instabuild) {
          heldItem.shrink(1);
        }
        break;

      case TAMED:
        if (companion instanceof EasyNPC<?> easyNPC) {
          OwnerHandler.setOwner(easyNPC, player);
          companion.asMob().getNavigation().stop();
          companion.asMob().setTarget(null);
          companion.level().broadcastEntityEvent(companion.asEntity(), (byte) 7);
        }

        // Clean up hint handler (removes wild nametag)
        TamingHintHandler hintHandler = companion.getTamingHintHandler();
        if (hintHandler != null) {
          hintHandler.onTamed(companion);
        }

        // Auto-name the companion
        if (CompanionNameConfig.shouldAutoName()) {
          String randomName = CompanionNameConfig.getRandomName();
          if (randomName != null) {
            companion.asMob().setCustomName(Component.literal(randomName));
            companion.asMob().setCustomNameVisible(true);
          }
        }

        relationship.reset();

        // Grant taming advancement
        CompanionAdvancements.grantTamed(player);
        TamingFeedbackHandler.handleTamedFeedback(companion, player);
        if (!player.getAbilities().instabuild) {
          heldItem.shrink(1);
        }
        break;

      case COOLDOWN:
        TamingFeedbackHandler.handleCooldownFeedback(
          companion, player, relationship.getCooldown());
        break;

      case WRONG_PLAYER:
        TamingFeedbackHandler.handleWrongPlayerFeedback(
          companion, player, relationship.getTamingPlayer());
        break;

      case ALREADY_TAMED:
        TamingFeedbackHandler.handleAlreadyTamedFeedback(companion, player);
        break;

      default:
        break;
    }

    return InteractionResult.CONSUME;
  }
}
