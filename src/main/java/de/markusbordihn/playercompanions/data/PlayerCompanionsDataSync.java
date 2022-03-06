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
