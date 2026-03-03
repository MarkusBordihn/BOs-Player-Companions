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

package de.markusbordihn.playercompanions.network;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.entity.AggressionLevel;
import de.markusbordihn.playercompanions.entity.CompanionCommand;
import de.markusbordihn.playercompanions.network.message.CompanionAggressionMessage;
import de.markusbordihn.playercompanions.network.message.CompanionCollectorActiveMessage;
import de.markusbordihn.playercompanions.network.message.CompanionCommandMessage;
import de.markusbordihn.playercompanions.network.message.ShrineRespawnMessage;
import de.markusbordihn.playercompanions.network.message.ShrineXpReduceMessage;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ForgeNetworkHandler implements CompanionNetwork {

  private static final String PROTOCOL_VERSION = "1";
  private static final SimpleChannel CHANNEL =
    NetworkRegistry.newSimpleChannel(
      new ResourceLocation(Constants.MOD_ID, "main"),
      () -> PROTOCOL_VERSION,
      PROTOCOL_VERSION::equals,
      PROTOCOL_VERSION::equals);

  public static void register() {
    CHANNEL.registerMessage(0, CompanionCommandMessage.class,
      CompanionCommandMessage::encode,
      CompanionCommandMessage::decode,
      (msg, ctx) -> {
        ctx.get().enqueueWork(() -> msg.handleServer(ctx.get().getSender()));
        ctx.get().setPacketHandled(true);
      },
      Optional.of(NetworkDirection.PLAY_TO_SERVER));
    CHANNEL.registerMessage(1, CompanionAggressionMessage.class,
      CompanionAggressionMessage::encode,
      CompanionAggressionMessage::decode,
      (msg, ctx) -> {
        ctx.get().enqueueWork(() -> msg.handleServer(ctx.get().getSender()));
        ctx.get().setPacketHandled(true);
      },
      Optional.of(NetworkDirection.PLAY_TO_SERVER));
    CHANNEL.registerMessage(2, CompanionCollectorActiveMessage.class,
      CompanionCollectorActiveMessage::encode,
      CompanionCollectorActiveMessage::decode,
      (msg, ctx) -> {
        ctx.get().enqueueWork(() -> msg.handleServer(ctx.get().getSender()));
        ctx.get().setPacketHandled(true);
      },
      Optional.of(NetworkDirection.PLAY_TO_SERVER));
    CHANNEL.registerMessage(3, ShrineRespawnMessage.class,
      ShrineRespawnMessage::encode,
      ShrineRespawnMessage::decode,
      (msg, ctx) -> {
        ctx.get().enqueueWork(() -> msg.handleServer(ctx.get().getSender()));
        ctx.get().setPacketHandled(true);
      },
      Optional.of(NetworkDirection.PLAY_TO_SERVER));
    CHANNEL.registerMessage(4, ShrineXpReduceMessage.class,
      ShrineXpReduceMessage::encode,
      ShrineXpReduceMessage::decode,
      (msg, ctx) -> {
        ctx.get().enqueueWork(() -> msg.handleServer(ctx.get().getSender()));
        ctx.get().setPacketHandled(true);
      },
      Optional.of(NetworkDirection.PLAY_TO_SERVER));
  }

  @Override
  public void sendCompanionCommand(UUID companionUUID, CompanionCommand command) {
    CHANNEL.sendToServer(new CompanionCommandMessage(companionUUID, command));
  }

  @Override
  public void sendCompanionAggression(UUID companionUUID, AggressionLevel level) {
    CHANNEL.sendToServer(new CompanionAggressionMessage(companionUUID, level));
  }

  @Override
  public void sendCollectorActive(UUID companionUUID, boolean active) {
    CHANNEL.sendToServer(new CompanionCollectorActiveMessage(companionUUID, active));
  }

  @Override
  public void sendShrineRespawn(UUID companionUUID) {
    CHANNEL.sendToServer(new ShrineRespawnMessage(companionUUID));
  }

  @Override
  public void sendShrineXpReduce(UUID companionUUID) {
    CHANNEL.sendToServer(new ShrineXpReduceMessage(companionUUID));
  }
}
