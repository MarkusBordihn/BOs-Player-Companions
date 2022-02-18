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

package de.markusbordihn.playercompanions.network.message;

import java.util.UUID;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.entity.PlayerCompanionCommand;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;

public class MessageCommandPlayerCompanion {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private final String playerCompanionUUID;
  private final String command;

  public MessageCommandPlayerCompanion(String playerCompanionUUID, String command) {
    this.playerCompanionUUID = playerCompanionUUID;
    this.command = command;
  }

  public String getCommand() {
    return this.command;
  }

  public String getPlayerCompanionUUID() {
    return this.playerCompanionUUID;
  }

  public static void handle(MessageCommandPlayerCompanion message,
      Supplier<NetworkEvent.Context> contextSupplier) {
    NetworkEvent.Context context = contextSupplier.get();
    context.enqueueWork(() -> handlePacket(message, context));
    context.setPacketHandled(true);
  }

  public static void handlePacket(MessageCommandPlayerCompanion message,
      NetworkEvent.Context context) {
    ServerPlayer serverPlayer = context.getSender();
    ServerLevel serverLevel = serverPlayer.getLevel();
    UUID uuid = UUID.fromString(message.getPlayerCompanionUUID());
    Entity entity = serverLevel.getEntity(uuid);

    // Only accepts commands from owner
    if (entity instanceof PlayerCompanionEntity playerCompanionEntity
        && serverPlayer.getUUID().equals(playerCompanionEntity.getOwnerUUID())) {
      PlayerCompanionCommand command = PlayerCompanionCommand.valueOf(message.getCommand());
      playerCompanionEntity.handleCommand(command);
      log.debug("Player Companion command {} ({}) for {} from {}", command, message.getCommand(),
          playerCompanionEntity, serverPlayer);
    }

  }

}
