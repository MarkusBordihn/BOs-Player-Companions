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

package de.markusbordihn.playercompanions.network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.data.PlayerCompanionsServerData;
import de.markusbordihn.playercompanions.entity.PlayerCompanionCommand;
import de.markusbordihn.playercompanions.network.message.MessageCommandPlayerCompanion;
import de.markusbordihn.playercompanions.network.message.MessagePlayerCompanionsData;

@EventBusSubscriber
public class NetworkHandler {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final String PROTOCOL_VERSION = "1";
  public static final SimpleChannel INSTANCE =
      NetworkRegistry.newSimpleChannel(new ResourceLocation(Constants.MOD_ID, "network"),
          () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
  private static int id = 0;

  protected NetworkHandler() {}

  public static void registerNetworkHandler(final FMLCommonSetupEvent event) {

    log.info("{} Network Handler for {} with version {} ...", Constants.LOG_REGISTER_PREFIX,
        INSTANCE, PROTOCOL_VERSION);

    event.enqueueWork(() -> {
      // Send Player Companion Command: Client -> Server
      INSTANCE.registerMessage(id++, MessageCommandPlayerCompanion.class, (message, buffer) -> {
        buffer.writeUtf(message.getPlayerCompanionUUID());
        buffer.writeUtf(message.getCommand());
      }, buffer -> new MessageCommandPlayerCompanion(buffer.readUtf(), buffer.readUtf()),
          MessageCommandPlayerCompanion::handle);

      // Sync Player Companion Data: Server -> Client
      INSTANCE.registerMessage(id++, MessagePlayerCompanionsData.class, (message, buffer) -> {
        buffer.writeUtf(message.getData());
      }, buffer -> new MessagePlayerCompanionsData(buffer.readUtf()),
          MessagePlayerCompanionsData::handle);
    });
  }

  @SubscribeEvent
  public static void handlePlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
    Player player = event.getPlayer();

    // Sending companion Data
    if (player instanceof ServerPlayer serverPlayer) {
      updatePlayerCompanionsData(serverPlayer);
    }
  }

  public static void commandPlayerCompanion(String playerCompanionUUID,
      PlayerCompanionCommand command) {
    if (playerCompanionUUID != null && command != null) {
      log.debug("commandPlayerCompanion {} {}", playerCompanionUUID, command);
      INSTANCE.sendToServer(new MessageCommandPlayerCompanion(playerCompanionUUID, command.toString()));
    }
  }

  public static void updatePlayerCompanionsData(ServerPlayer serverPlayer) {
    String companionData =
        PlayerCompanionsServerData.get().exportClientData(serverPlayer.getUUID());
    if (companionData != null && !companionData.isBlank()) {
      log.debug("Sending Player Companions data {} to {}", companionData, serverPlayer);
      INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
          new MessagePlayerCompanionsData(companionData));
    }
  }

}
