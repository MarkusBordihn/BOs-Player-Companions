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

import de.markusbordihn.easynpc.api.handler.EasyNPCEntityHandler;
import de.markusbordihn.easynpc.data.npc.NPCEntityMetadata;
import de.markusbordihn.easynpc.data.saveddata.NPCEntityData;
import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.commands.CompanionSpawnCooldown;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public record ShrineRespawnMessage(UUID companionUUID) {

  public static final ResourceLocation ID =
    new ResourceLocation(Constants.MOD_ID, "shrine_respawn");

  public static ShrineRespawnMessage decode(FriendlyByteBuf buf) {
    return new ShrineRespawnMessage(buf.readUUID());
  }

  public void encode(FriendlyByteBuf buf) {
    buf.writeUUID(companionUUID);
  }

  public void handleServer(ServerPlayer serverPlayer) {
    Optional<NPCEntityMetadata> meta =
      NPCEntityData.get(serverPlayer.getServer()).getMetadata(companionUUID);
    if (meta.isEmpty() || !meta.get().hasRemovalReason()) {
      return;
    }

    long gameTime = ((ServerLevel) serverPlayer.level()).getGameTime();
    if (!CompanionSpawnCooldown.canSpawn(companionUUID, gameTime)) {
      return;
    }

    ServerLevel serverLevel = (ServerLevel) serverPlayer.level();
    if (EasyNPCEntityHandler.spawn(companionUUID, serverLevel, serverPlayer.position())) {
      CompanionSpawnCooldown.recordSpawn(companionUUID, serverLevel.getGameTime());
      serverPlayer.closeContainer();
    }
  }
}
