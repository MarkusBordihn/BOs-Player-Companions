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

package de.markusbordihn.playercompanions.data;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import de.markusbordihn.playercompanions.network.NetworkHandler;

public class PlayerCompanionsServerDataClientSync {

  protected PlayerCompanionsServerDataClientSync() {}

  public static void syncPlayerCompanionData(PlayerCompanionData playerCompanionData) {
    // Only sync player companions, if we have a valid owner.
    if (playerCompanionData == null || !playerCompanionData.hasOwner()) {
      return;
    }

    CompoundTag data = exportPlayerCompanionData(playerCompanionData);
    UUID playerCompanionUUID = playerCompanionData.getUUID();
    UUID ownerUUID = playerCompanionData.getOwnerUUID();

    NetworkHandler.updatePlayerCompanionData(playerCompanionUUID, ownerUUID, data);
  }

  public static void syncPlayerCompanionData(UUID ownerUUID,
      Set<PlayerCompanionData> playerCompanionsData) {
    // Only sync player companions, if we have a valid owner.
    if (ownerUUID == null || playerCompanionsData == null || playerCompanionsData.isEmpty()) {
      return;
    }
    CompoundTag data = exportPlayerCompanionsData(playerCompanionsData);
    NetworkHandler.updatePlayerCompanionsData(ownerUUID, data);
  }

  public static String exportPlayerCompanionDataString(PlayerCompanionData playerCompanionData) {
    CompoundTag compoundTag = exportPlayerCompanionData(playerCompanionData);
    return compoundTag != null && !compoundTag.isEmpty() ? compoundTag.getAsString() : "";
  }

  public static CompoundTag exportPlayerCompanionData(PlayerCompanionData playerCompanionData) {

    // Pre-checks to avoid errors or edge conditions like server start.
    if (playerCompanionData == null || playerCompanionData.getUUID() == null
        || playerCompanionData.getOwnerUUID() == null) {
      return null;
    }

    // Create client data
    CompoundTag compoundTag = new CompoundTag();
    playerCompanionData.saveMetaData(compoundTag);

    // Return data as string.
    return compoundTag;
  }

  public static String exportPlayerCompanionsDataString(Set<PlayerCompanionData> playerCompanionsData) {
    CompoundTag compoundTag = exportPlayerCompanionsData(playerCompanionsData);
    return compoundTag != null && !compoundTag.isEmpty() ? compoundTag.getAsString() : "";
  }

  public static CompoundTag exportPlayerCompanionsData(Set<PlayerCompanionData> playerCompanionsData) {
    // Pre-checks to avoid errors or edge conditions like server start.
    if (playerCompanionsData == null || playerCompanionsData.isEmpty()) {
      return null;
    }

    // Create client data
    CompoundTag compoundTag = new CompoundTag();
    ListTag companionListTag = new ListTag();

    // Iterate over all known player companions for the owner and get their meta data.
    Iterator<PlayerCompanionData> playerCompanionIterator = playerCompanionsData.iterator();
    while (playerCompanionIterator.hasNext()) {
      PlayerCompanionData playerCompanion = playerCompanionIterator.next();
      if (playerCompanion != null) {
        CompoundTag playerCompanionCompoundTag = new CompoundTag();
        playerCompanion.saveMetaData(playerCompanionCompoundTag);
        companionListTag.add(playerCompanionCompoundTag);
      }
    }
    compoundTag.put(PlayerCompanionsServerData.COMPANIONS_TAG, companionListTag);

    return compoundTag;
  }

}
