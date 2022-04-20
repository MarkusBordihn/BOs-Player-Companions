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

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.entity.PlayerCompanionCommand;
import de.markusbordihn.playercompanions.network.message.MessageCommandPlayerCompanion;
import de.markusbordihn.playercompanions.network.message.MessagePlayerCompanionData;
import de.markusbordihn.playercompanions.network.message.MessagePlayerCompanionsData;
import de.markusbordihn.playercompanions.network.message.MessageSkinChangePlayerCompanion;

@EventBusSubscriber
public class NetworkHandler {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final String PROTOCOL_VERSION = "1";
  public static final SimpleChannel INSTANCE =
      NetworkRegistry.newSimpleChannel(new ResourceLocation(Constants.MOD_ID, "network"),
          () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
  private static ConcurrentHashMap<UUID, ServerPlayer> serverPlayerMap = new ConcurrentHashMap<>();
  private static int id = 0;
  private static String lastCompanionDataPackage;
  private static String lastCompanionsDataPackage;

  protected NetworkHandler() {}

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void handlePlayerChangedDimensionEvent(PlayerChangedDimensionEvent event) {
    addServerPlayer(event.getPlayer());
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void handlePlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
    addServerPlayer(event.getPlayer());
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void handlePlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
    removeServerPlayer(event.getPlayer());
  }

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

      // Send Player Companion Skin Change: Client -> Server
      INSTANCE.registerMessage(id++, MessageSkinChangePlayerCompanion.class, (message, buffer) -> {
        buffer.writeUtf(message.getPlayerCompanionUUID());
        buffer.writeUtf(message.getSkin());
      }, buffer -> new MessageSkinChangePlayerCompanion(buffer.readUtf(), buffer.readUtf()),
          MessageSkinChangePlayerCompanion::handle);

      // Sync full Player Companion Data: Server -> Client
      INSTANCE.registerMessage(id++, MessagePlayerCompanionsData.class,
          (message, buffer) -> buffer.writeUtf(message.getData()),
          buffer -> new MessagePlayerCompanionsData(buffer.readUtf()),
          MessagePlayerCompanionsData::handle);

      // Sync single Player Companion Data: Server -> Client
      INSTANCE.registerMessage(id++, MessagePlayerCompanionData.class, (message, buffer) -> {
        buffer.writeUtf(message.getPlayerCompanionUUID());
        buffer.writeUtf(message.getData());
      }, buffer -> new MessagePlayerCompanionData(buffer.readUtf(), buffer.readUtf()),
          MessagePlayerCompanionData::handle);
    });
  }

  /**
   * Send player companion commands.
   */
  public static void commandPlayerCompanion(String playerCompanionUUID,
      PlayerCompanionCommand command) {
    if (playerCompanionUUID != null && command != null) {
      log.debug("commandPlayerCompanion {} {}", playerCompanionUUID, command);
      INSTANCE
          .sendToServer(new MessageCommandPlayerCompanion(playerCompanionUUID, command.toString()));
    }
  }

  /**
   * Send player companion skin change.
   */
  public static void skinChangePlayerCompanion(String playerCompanionUUID, String skin) {
    if (playerCompanionUUID != null && skin != null) {
      log.debug("skinChangePlayerCompanion {} {}", playerCompanionUUID, skin);
      INSTANCE.sendToServer(new MessageSkinChangePlayerCompanion(playerCompanionUUID, skin));
    }
  }

  /**
   * Send full companion data to the owner, if data has changed.
   */
  public static void updatePlayerCompanionsData(UUID ownerUUID, String companionsData) {
    if (ownerUUID != null && companionsData != null && !companionsData.isBlank()
        && !companionsData.equals(lastCompanionsDataPackage)) {
      ServerPlayer serverPlayer = getServerPlayer(ownerUUID);
      if (serverPlayer == null) {
        return;
      }
      log.debug("Sending Player Companions data to {}: {}", serverPlayer, companionsData);
      INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
          new MessagePlayerCompanionsData(companionsData));
      lastCompanionsDataPackage = companionsData;
    }
  }

  /**
   * Send specific player companion data to the owner, if data has changed.
   */
  public static void updatePlayerCompanionData(UUID playerCompanionUUID, UUID ownerUUID,
      String companionData) {
    if (playerCompanionUUID != null && ownerUUID != null && companionData != null
        && !companionData.isBlank() && !companionData.equals(lastCompanionDataPackage)) {
      ServerPlayer serverPlayer = getServerPlayer(ownerUUID);
      if (serverPlayer == null) {
        return;
      }
      log.debug("Sending Player Companions data for {} to {}: {}", playerCompanionUUID,
          serverPlayer, companionData);
      INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
          new MessagePlayerCompanionData(playerCompanionUUID.toString(), companionData));
      lastCompanionDataPackage = companionData;
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
