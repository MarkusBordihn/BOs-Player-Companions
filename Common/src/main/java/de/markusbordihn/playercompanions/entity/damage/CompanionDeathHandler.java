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

package de.markusbordihn.playercompanions.entity.damage;

import de.markusbordihn.easynpc.entity.easynpc.EasyNPC;
import de.markusbordihn.easynpc.entity.easynpc.data.OwnerDataCapable;
import de.markusbordihn.easynpc.entity.easynpc.data.ProgressionDataCapable;
import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.commands.CompanionSpawnCooldown;
import de.markusbordihn.playercompanions.entity.companion.PlayerCompanion;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

public class CompanionDeathHandler {

  private static final String DEATH_PREFIX = Constants.TEXT_PREFIX + "death.";

  private CompanionDeathHandler() {
  }

  public static void handleDeath(PlayerCompanion companion, DamageSource damageSource) {
    if (!(companion instanceof EasyNPC<?> easyNPC)) {
      return;
    }
    OwnerDataCapable<?> ownerData = easyNPC.getEasyNPCOwnerData();
    if (ownerData == null || ownerData.getOwnerUUID() == null) {
      return;
    }

    // Record death for respawn cooldown tracking
    CompanionSpawnCooldown.recordDeath(easyNPC.getEntityUUID());

    // Decrease experience level on death
    if (companion instanceof ProgressionDataCapable<?> progression
      && !progression.isMinExperienceLevel()) {
      progression.decreaseExperienceAndExperienceLevel();
    }

    Entity entity = companion.asEntity();
    if (entity.level().isClientSide) {
      return;
    }
    if (entity.level().getServer() == null) {
      return;
    }
    ServerPlayer owner =
      entity.level().getServer().getPlayerList().getPlayer(ownerData.getOwnerUUID());
    if (owner == null) {
      return;
    }

    Component companionName = entity.getDisplayName();
    BlockPos pos = entity.blockPosition();
    Entity sourceEntity = damageSource.getEntity();

    Component message;
    if (sourceEntity != null && sourceEntity != entity) {
      message = Component.translatable(
          DEATH_PREFIX + "notification_cause",
          companionName, sourceEntity.getDisplayName(),
          pos.getX(), pos.getY(), pos.getZ())
        .withStyle(ChatFormatting.RED);
    } else {
      message = Component.translatable(
          DEATH_PREFIX + "notification",
          companionName,
          pos.getX(), pos.getY(), pos.getZ())
        .withStyle(ChatFormatting.RED);
    }

    owner.sendSystemMessage(message);

    // Play a sad sound to draw the owner's attention
    owner.level().playSound(
      null, owner.blockPosition(),
      SoundEvents.ANVIL_LAND, SoundSource.PLAYERS,
      0.5F, 0.5F);
  }
}
