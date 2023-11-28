/*
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

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.network.message.MessageCommandPlayerCompanion;
import de.markusbordihn.playercompanions.network.message.MessagePlayerCompanionData;
import de.markusbordihn.playercompanions.network.message.MessagePlayerCompanionsData;
import de.markusbordihn.playercompanions.network.message.MessageSkinChange;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@EventBusSubscriber
public class NetworkHandler {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final int PROTOCOL_VERSION = 4;
  private static final SimpleChannel SIMPLE_CHANNEL =
      ChannelBuilder.named(new ResourceLocation(Constants.MOD_ID, "network"))
          .networkProtocolVersion(PROTOCOL_VERSION)
          .simpleChannel();
  private static int id = 0;
  private static final ConcurrentHashMap<UUID, ServerPlayer> serverPlayerMap = new ConcurrentHashMap<>();

  protected NetworkHandler() {}

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void handlePlayerChangedDimensionEvent(PlayerChangedDimensionEvent event) {
    addServerPlayer(event.getEntity());
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void handlePlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
    addServerPlayer(event.getEntity());
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void handlePlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
    removeServerPlayer(event.getEntity());
  }

  public static void registerNetworkHandler(final FMLCommonSetupEvent event) {

    log.info(
        "{} Network Handler for {} with version {} ...",
        Constants.LOG_REGISTER_PREFIX,
        SIMPLE_CHANNEL,
        PROTOCOL_VERSION);

    event.enqueueWork(
        () -> {

          // Send Player Companion Command: Client -> Server
          SIMPLE_CHANNEL
              .messageBuilder(
                  MessageCommandPlayerCompanion.class, id++, NetworkDirection.PLAY_TO_SERVER)
              .encoder(MessageCommandPlayerCompanion::encode)
              .decoder(MessageCommandPlayerCompanion::decode)
              .consumerNetworkThread(MessageCommandPlayerCompanion::handle)
              .add();

          // Sync single Player Companion Data: Server -> Client
          SIMPLE_CHANNEL
              .messageBuilder(
                  MessagePlayerCompanionData.class, id++, NetworkDirection.PLAY_TO_CLIENT)
              .encoder(MessagePlayerCompanionData::encode)
              .decoder(MessagePlayerCompanionData::decode)
              .consumerNetworkThread(MessagePlayerCompanionData::handle)
              .add();

          // Sync full Player Companion Data: Server -> Client
          SIMPLE_CHANNEL
              .messageBuilder(
                  MessagePlayerCompanionsData.class, id++, NetworkDirection.PLAY_TO_CLIENT)
              .encoder(MessagePlayerCompanionsData::encode)
              .decoder(MessagePlayerCompanionsData::decode)
              .consumerNetworkThread(MessagePlayerCompanionsData::handle)
              .add();

          // Skin Change: Client -> Server
          SIMPLE_CHANNEL
              .messageBuilder(MessageSkinChange.class, id++, NetworkDirection.PLAY_TO_SERVER)
              .encoder(MessageSkinChange::encode)
              .decoder(MessageSkinChange::decode)
              .consumerNetworkThread(MessageSkinChange::handle)
              .add();
        });
  }

  public static <M> void sendToServer(M message) {
    try {
      SIMPLE_CHANNEL.send(message, PacketDistributor.SERVER.noArg());
    } catch (Exception e) {
      log.error("Failed to send {} to server, got error: {}", message, e.getMessage());
    }
  }

  public static <M> void sendToPlayer(ServerPlayer serverPlayer, M message) {
    try {
      SIMPLE_CHANNEL.send(message, PacketDistributor.PLAYER.with(serverPlayer));
    } catch (Exception e) {
      log.error(
          "Failed to send {} to player {}, got error: {}", message, serverPlayer, e.getMessage());
    }
  }

  public static void addServerPlayer(Player player) {
    if (player instanceof ServerPlayer serverPlayer) {
      addServerPlayer(serverPlayer.getUUID(), serverPlayer);
    }
  }

  public static void addServerPlayer(UUID uuid, ServerPlayer serverPlayer) {
    serverPlayerMap.put(uuid, serverPlayer);
  }

  public static void removeServerPlayer(Player player) {
    if (player instanceof ServerPlayer serverPlayer) {
      removeServerPlayer(serverPlayer.getUUID());
    }
  }

  public static void removeServerPlayer(UUID uuid) {
    serverPlayerMap.remove(uuid);
  }

  public static ServerPlayer getServerPlayer(UUID uuid) {
    return serverPlayerMap.get(uuid);
  }
}
