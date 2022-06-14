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

package de.markusbordihn.playercompanions.integration;

import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.impl.ui.ElementHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.UsernameCache;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.data.Experience;
import de.markusbordihn.playercompanions.data.PlayerCompanionData;
import de.markusbordihn.playercompanions.data.PlayerCompanionsClientData;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;

public class PlayerCompanionEntityProvider implements IEntityComponentProvider {

  public static final PlayerCompanionEntityProvider INSTANCE = new PlayerCompanionEntityProvider();
  public static final ResourceLocation UID =
      new ResourceLocation(Constants.MOD_ID, "player_companion_entity_provider");

  @Override
  @OnlyIn(Dist.CLIENT)
  public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
    if (accessor.getEntity() instanceof PlayerCompanionEntity playerCompanionEntity) {
      tooltip.add(Component.literal("Type: " + playerCompanionEntity.getCompanionType()));
      if (playerCompanionEntity.getOwner() != null) {
        PlayerCompanionData data = PlayerCompanionsClientData.getCompanion(playerCompanionEntity);
        if (accessor.getEntity() instanceof OwnableEntity) {
          String ownerName =
              UsernameCache.getLastKnownUsername(playerCompanionEntity.getOwnerUUID());
          if (ownerName == null) {
            if (playerCompanionEntity.getOwner() != null) {
              ownerName = playerCompanionEntity.getOwner().getName().getString();
            } else {
              ownerName = playerCompanionEntity.getOwnerUUID().toString();
            }
          }
          tooltip.add(Component.literal("Owner: " + ownerName));
        }
        tooltip.add(Component.translatable(Constants.TEXT_PREFIX + "tamed_companion_level",
            playerCompanionEntity.getExperienceLevel(), playerCompanionEntity.getExperience(),
            Experience.getExperienceForNextLevel(playerCompanionEntity.getExperienceLevel())));
        if (data != null) {
          long respawnTimer =
              data.getEntityRespawnTimer() - java.time.Instant.now().getEpochSecond();

          if (respawnTimer <= 0) {
            if (data.isOrderedToPosition()) {
              tooltip.add(Component.literal("Order: To Position"));
            }
            if (data.isOrderedToSit()) {
              tooltip.add(Component.literal("Order: Sitting"));
            } else {
              tooltip.add(Component.literal("Order: Following"));
            }
          }
          // Display respawn timer, if any.
          if (respawnTimer >= 0) {
            tooltip.add(Component
                .translatable(Constants.TEXT_PREFIX + "tamed_companion_respawn", respawnTimer)
                .withStyle(ChatFormatting.RED));
          }

          // Aggression Level
          tooltip.add(
              Component.literal("Aggression Level: " + data.getEntityAggressionLevel().name()));
        }
      }
    }
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public IElement getIcon(EntityAccessor accessor, IPluginConfig config, IElement currentIcon) {
    if (accessor.getEntity() instanceof PlayerCompanionEntity playerCompanionEntity) {
      ItemStack itemStack = new ItemStack(playerCompanionEntity.getCompanionItem());
      if (!itemStack.isEmpty()) {
        return new ElementHelper().item(itemStack, 2F);
      }
    }
    return null;
  }

  @Override
  public ResourceLocation getUid() {
    return UID;
  }

}
