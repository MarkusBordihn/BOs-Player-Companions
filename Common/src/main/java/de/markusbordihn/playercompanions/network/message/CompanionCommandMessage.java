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

package de.markusbordihn.playercompanions.network.message;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.entity.CompanionBehaviorHandler;
import de.markusbordihn.playercompanions.entity.CompanionCommand;
import de.markusbordihn.playercompanions.entity.companion.PlayerCompanion;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Mob;

public record CompanionCommandMessage(UUID companionUUID, CompanionCommand command) {

  public static final ResourceLocation ID =
    new ResourceLocation(Constants.MOD_ID, "companion_command");

  public static CompanionCommandMessage decode(FriendlyByteBuf buf) {
    return new CompanionCommandMessage(buf.readUUID(), buf.readEnum(CompanionCommand.class));
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeUUID(companionUUID);
    buf.writeEnum(command);
  }

  public void handleServer(ServerPlayer serverPlayer) {
    serverPlayer.level().getEntitiesOfClass(Mob.class,
        serverPlayer.getBoundingBox().inflate(64),
        e -> e instanceof PlayerCompanion && e.getUUID().equals(companionUUID))
      .stream().findFirst()
      .filter(PlayerCompanion.class::isInstance)
      .map(PlayerCompanion.class::cast)
      .filter(playerCompanion -> playerCompanion.isOwner(serverPlayer))
      .ifPresent(
        playerCompanion -> CompanionBehaviorHandler.applyCommand(playerCompanion, command));
  }
}
