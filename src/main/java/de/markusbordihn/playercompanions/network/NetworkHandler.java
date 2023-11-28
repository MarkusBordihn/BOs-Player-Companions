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
import de.markusbordihn.playercompanions.entity.PlayerCompanionCommand;
import de.markusbordihn.playercompanions.network.message.MessageCommandPlayerCompanion;
import de.markusbordihn.playercompanions.network.message.MessagePlayerCompanionData;
import de.markusbordihn.playercompanions.network.message.MessagePlayerCompanionsData;
import de.markusbordihn.playercompanions.network.message.MessageSkinChange;
import de.markusbordihn.playercompanions.skin.SkinType;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.nbt.CompoundTag;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@EventBusSubscriber
public class NetworkHandler {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final String PROTOCOL_VERSION = "4";
  public static final SimpleChannel INSTANCE =
      NetworkRegistry.newSimpleChannel(
          new ResourceLocation(Constants.MOD_ID, "network"),
          () -> PROTOCOL_VERSION,
          PROTOCOL_VERSION::equals,
          PROTOCOL_VERSION::equals);
  private static ConcurrentHashMap<UUID, ServerPlayer> serverPlayerMap = new ConcurrentHashMap<>();
  private static int id = 0;
  private static CompoundTag lastCompanionDataPackage;
  private static CompoundTag lastCompanionsDataPackage;

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
        INSTANCE,
        PROTOCOL_VERSION);

    event.enqueueWork(
        () -> {

          // Send Player Companion Command: Client -> Server
          INSTANCE.registerMessage(
              id++,
              MessageCommandPlayerCompanion.class,
              MessageCommandPlayerCompanion::encode,
              MessageCommandPlayerCompanion::decode,
              MessageCommandPlayerCompanion::handle);

          // Sync single Player Companion Data: Server -> Client
          INSTANCE.registerMessage(
              id++,
              MessagePlayerCompanionData.class,
              MessagePlayerCompanionData::encode,
              MessagePlayerCompanionData::decode,
              MessagePlayerCompanionData::handle);

          // Sync full Player Companion Data: Server -> Client
          INSTANCE.registerMessage(
              id++,
              MessagePlayerCompanionsData.class,
              MessagePlayerCompanionsData::encode,
              MessagePlayerCompanionsData::decode,
              MessagePlayerCompanionsData::handle);

          // Skin Change: Client -> Server
          INSTANCE.registerMessage(
              id++,
              MessageSkinChange.class,
              MessageSkinChange::encode,
              MessageSkinChange::decode,
              MessageSkinChange::handle);
        });
  }

  /** Send player companion commands. */
  public static void commandPlayerCompanion(
      String playerCompanionUUID, PlayerCompanionCommand command) {
    if (playerCompanionUUID != null && command != null) {
      INSTANCE.sendToServer(new MessageCommandPlayerCompanion(playerCompanionUUID, command));
    }
  }

  /** Send skin change. */
  public static void skinChange(UUID uuid, SkinType skinType) {
    if (uuid != null && skinType != null) {
      INSTANCE.sendToServer(new MessageSkinChange(uuid, "", "", Constants.BLANK_UUID, skinType));
    }
  }

  public static void skinChange(UUID uuid, String skin, SkinType skinType) {
    if (uuid != null && skin != null && skinType != null) {
      INSTANCE.sendToServer(new MessageSkinChange(uuid, skin, "", Constants.BLANK_UUID, skinType));
    }
  }

  public static void skinChange(
      UUID uuid, String skin, String skinURL, UUID skinUUID, SkinType skinType) {
    if (uuid != null && skin != null && skinType != null) {
      INSTANCE.sendToServer(new MessageSkinChange(uuid, skin, skinURL, skinUUID, skinType));
    }
  }

  /** Send full companion data to the owner, if data has changed. */
  public static void updatePlayerCompanionsData(UUID ownerUUID, CompoundTag companionsData) {
    if (ownerUUID != null
        && companionsData != null
        && !companionsData.isEmpty()
        && !companionsData.equals(lastCompanionsDataPackage)) {
      ServerPlayer serverPlayer = getServerPlayer(ownerUUID);
      if (serverPlayer == null) {
        return;
      }
      INSTANCE.send(
          PacketDistributor.PLAYER.with(() -> serverPlayer),
          new MessagePlayerCompanionsData(companionsData));
      lastCompanionsDataPackage = companionsData;
    }
  }

  /** Send specific player companion data to the owner, if data has changed. */
  public static void updatePlayerCompanionData(
      UUID playerCompanionUUID, UUID ownerUUID, CompoundTag companionData) {
    if (playerCompanionUUID != null
        && ownerUUID != null
        && companionData != null
        && !companionData.isEmpty()
        && !companionData.equals(lastCompanionDataPackage)) {
      ServerPlayer serverPlayer = getServerPlayer(ownerUUID);
      if (serverPlayer == null) {
        return;
      }
      INSTANCE.send(
          PacketDistributor.PLAYER.with(() -> serverPlayer),
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
