/*
 * Copyright 2026 Markus Bordihn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.markusbordihn.playercompanions.network.message;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.commands.CompanionSpawnCooldown;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public record ShrineXpReduceMessage(UUID companionUUID) {

  public static final long TICKS_PER_LEVEL = 2400L;

  public static final ResourceLocation ID =
    new ResourceLocation(Constants.MOD_ID, "shrine_xp_reduce");

  public static ShrineXpReduceMessage decode(FriendlyByteBuf buf) {
    return new ShrineXpReduceMessage(buf.readUUID());
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeUUID(companionUUID);
  }

  public void handleServer(ServerPlayer serverPlayer) {
    if (serverPlayer.experienceLevel < 1) {
      return;
    }
    long gameTime = serverPlayer.level().getGameTime();
    if (CompanionSpawnCooldown.remainingTicks(companionUUID, gameTime) <= 0) {
      return;
    }

    serverPlayer.giveExperienceLevels(-1);
    CompanionSpawnCooldown.reduceDeathCooldown(companionUUID, TICKS_PER_LEVEL);
  }
}
