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

import java.util.UUID;

import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;

public interface PlayerCompanionsDataSync {

  public PlayerCompanionEntity getSyncReference();

  public void setDataSyncNeeded(boolean dirty);

  public boolean getDataSyncNeeded();

  public default boolean hasSyncReference() {
    return getSyncReference() != null;
  }

  public default void syncData() {
    if (hasSyncReference()) {
      syncData(getSyncReference());
      setDataSyncNeeded(false);
    }
  }

  public default void registerData() {
    if (hasSyncReference()) {
      registerData(getSyncReference());
      setDataSyncNeeded(false);
    }
  }

  public default boolean syncDataIfNeeded() {
    if (getDataSyncNeeded()) {
      syncData();
      return true;
    }
    return false;
  }

  public default void syncData(PlayerCompanionEntity playerCompanionEntity) {
    PlayerCompanionsServerData serverData = getServerData();
    if (serverData != null) {
      serverData.updateOrRegisterCompanion(playerCompanionEntity);
    }
  }

  public default void registerData(PlayerCompanionEntity playerCompanionEntity) {
    PlayerCompanionsServerData serverData = getServerData();
    if (serverData != null) {
      serverData.registerCompanion(playerCompanionEntity);
    }
  }

  public default PlayerCompanionData getData(UUID uuid) {
    PlayerCompanionsServerData serverData = getServerData();
    if (serverData == null || uuid == null) {
      return null;
    }
    return serverData.getCompanion(uuid);
  }

  public default PlayerCompanionsServerData getServerData() {
    return PlayerCompanionsServerData.get();
  }

}
