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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.markusbordihn.playercompanions.network;

import de.markusbordihn.playercompanions.entity.AggressionLevel;
import de.markusbordihn.playercompanions.entity.CompanionCommand;
import de.markusbordihn.playercompanions.network.message.CompanionAggressionMessage;
import de.markusbordihn.playercompanions.network.message.CompanionCollectorActiveMessage;
import de.markusbordihn.playercompanions.network.message.CompanionCommandMessage;
import de.markusbordihn.playercompanions.network.message.ShrineRespawnMessage;
import de.markusbordihn.playercompanions.network.message.ShrineXpReduceMessage;
import io.netty.buffer.Unpooled;
import java.util.UUID;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;

public class FabricNetworkHandler implements CompanionNetwork {

  public static void registerServerReceiver() {
    ServerPlayNetworking.registerGlobalReceiver(CompanionCommandMessage.ID,
      (server, serverPlayer, handler, buf, responseSender) ->
        server.execute(() -> CompanionCommandMessage.decode(buf).handleServer(serverPlayer)));
    ServerPlayNetworking.registerGlobalReceiver(CompanionAggressionMessage.ID,
      (server, serverPlayer, handler, buf, responseSender) ->
        server.execute(() -> CompanionAggressionMessage.decode(buf).handleServer(serverPlayer)));
    ServerPlayNetworking.registerGlobalReceiver(CompanionCollectorActiveMessage.ID,
      (server, serverPlayer, handler, buf, responseSender) ->
        server.execute(
          () -> CompanionCollectorActiveMessage.decode(buf).handleServer(serverPlayer)));
    ServerPlayNetworking.registerGlobalReceiver(ShrineRespawnMessage.ID,
      (server, serverPlayer, handler, buf, responseSender) ->
        server.execute(() -> ShrineRespawnMessage.decode(buf).handleServer(serverPlayer)));
    ServerPlayNetworking.registerGlobalReceiver(ShrineXpReduceMessage.ID,
      (server, serverPlayer, handler, buf, responseSender) ->
        server.execute(() -> ShrineXpReduceMessage.decode(buf).handleServer(serverPlayer)));
  }

  @Override
  public void sendCompanionCommand(UUID companionUUID, CompanionCommand command) {
    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
    new CompanionCommandMessage(companionUUID, command).encode(buf);
    ClientPlayNetworking.send(CompanionCommandMessage.ID, buf);
  }

  @Override
  public void sendCompanionAggression(UUID companionUUID, AggressionLevel level) {
    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
    new CompanionAggressionMessage(companionUUID, level).encode(buf);
    ClientPlayNetworking.send(CompanionAggressionMessage.ID, buf);
  }

  @Override
  public void sendCollectorActive(UUID companionUUID, boolean active) {
    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
    new CompanionCollectorActiveMessage(companionUUID, active).encode(buf);
    ClientPlayNetworking.send(CompanionCollectorActiveMessage.ID, buf);
  }

  @Override
  public void sendShrineRespawn(UUID companionUUID) {
    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
    new ShrineRespawnMessage(companionUUID).encode(buf);
    ClientPlayNetworking.send(ShrineRespawnMessage.ID, buf);
  }

  @Override
  public void sendShrineXpReduce(UUID companionUUID) {
    FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
    new ShrineXpReduceMessage(companionUUID).encode(buf);
    ClientPlayNetworking.send(ShrineXpReduceMessage.ID, buf);
  }
}
