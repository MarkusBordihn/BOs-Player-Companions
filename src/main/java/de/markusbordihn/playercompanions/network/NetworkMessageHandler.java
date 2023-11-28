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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

public class NetworkMessageHandler {

  private static CompoundTag lastCompanionDataPackage;
  private static CompoundTag lastCompanionsDataPackage;

  protected NetworkMessageHandler() {}

  /** Send player companion commands. */
  public static void commandPlayerCompanion(
      String playerCompanionUUID, PlayerCompanionCommand command) {
    if (playerCompanionUUID != null && command != null) {
      NetworkHandler.sendToServer(new MessageCommandPlayerCompanion(playerCompanionUUID, command));
    }
  }

  /** Send skin change. */
  public static void skinChange(UUID uuid, SkinType skinType) {
    if (uuid != null && skinType != null) {
      NetworkHandler.sendToServer(
          new MessageSkinChange(uuid, "", "", Constants.BLANK_UUID, skinType));
    }
  }

  public static void skinChange(UUID uuid, String skin, SkinType skinType) {
    if (uuid != null && skin != null && skinType != null) {
      NetworkHandler.sendToServer(
          new MessageSkinChange(uuid, skin, "", Constants.BLANK_UUID, skinType));
    }
  }

  public static void skinChange(
      UUID uuid, String skin, String skinURL, UUID skinUUID, SkinType skinType) {
    if (uuid != null && skin != null && skinType != null) {
      NetworkHandler.sendToServer(new MessageSkinChange(uuid, skin, skinURL, skinUUID, skinType));
    }
  }

  /** Send full companion data to the owner, if data has changed. */
  public static void updatePlayerCompanionsData(UUID ownerUUID, CompoundTag companionsData) {
    if (ownerUUID != null
        && companionsData != null
        && !companionsData.isEmpty()
        && !companionsData.equals(lastCompanionsDataPackage)) {
      ServerPlayer serverPlayer = NetworkHandler.getServerPlayer(ownerUUID);
      if (serverPlayer == null) {
        return;
      }
      NetworkHandler.sendToPlayer(serverPlayer, new MessagePlayerCompanionsData(companionsData));
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
      ServerPlayer serverPlayer = NetworkHandler.getServerPlayer(ownerUUID);
      if (serverPlayer == null) {
        return;
      }
      NetworkHandler.sendToPlayer(
          serverPlayer,
          new MessagePlayerCompanionData(playerCompanionUUID.toString(), companionData));
      lastCompanionDataPackage = companionData;
    }
  }
}
