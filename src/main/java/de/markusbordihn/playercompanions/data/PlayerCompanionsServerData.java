/*
 * Copyright 2021 Markus Bordihn
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

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.config.CommonConfig;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.item.CapturedCompanion;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@EventBusSubscriber
public class PlayerCompanionsServerData extends SavedData {

  public static final String COMPANIONS_TAG = "Companions";
  public static final String NPC_TAG = "NPCs";
  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  protected static final CommonConfig.Config COMMON = CommonConfig.COMMON;
  private static final String PLAYER_COMPANIONS_FILE_ID = Constants.MOD_ID;
  private static ConcurrentHashMap<UUID, PlayerCompanionData> playerCompanionsMap =
      new ConcurrentHashMap<>();
  private static ConcurrentHashMap<UUID, Set<PlayerCompanionData>> companionsPerPlayerMap =
      new ConcurrentHashMap<>();
  private static MinecraftServer server;
  private static PlayerCompanionsServerData data;
  private static long nextBackupTime = 0;

  public PlayerCompanionsServerData() {
    this.setDirty();
  }

  @SubscribeEvent
  public static void handleServerAboutToStartEvent(ServerAboutToStartEvent event) {
    playerCompanionsMap = new ConcurrentHashMap<>();
    companionsPerPlayerMap = new ConcurrentHashMap<>();

    if (Boolean.TRUE.equals(COMMON.dataBackupEnabled.get())) {
      nextBackupTime =
          java.time.Instant.now().getEpochSecond() + (60 * COMMON.dataBackupInterval.get());
      log.info(
          "Enable automatic data backups every {} minutes, next backup will run at {} ...",
          COMMON.dataBackupInterval.get(),
          nextBackupTime);
    } else {
      log.warn(
          "Automatic Data backups are deactivated, please make sure to create regular backups!");
    }
  }

  public static void prepare(MinecraftServer server) {
    // Make sure we preparing the data only once for the same server!
    if (server == null
        || server == PlayerCompanionsServerData.server && PlayerCompanionsServerData.data != null) {
      return;
    }

    log.info("{} preparing data for {}", Constants.LOG_ICON_NAME, server);
    PlayerCompanionsServerData.server = server;

    // Using a global approach and storing relevant data in the overworld only!
    PlayerCompanionsServerData.data =
        server
            .getLevel(Level.OVERWORLD)
            .getDataStorage()
            .computeIfAbsent(
                PlayerCompanionsServerData::load,
                PlayerCompanionsServerData::new,
                PlayerCompanionsServerData.getFileId());
  }

  public static void setData(PlayerCompanionsServerData data) {
    PlayerCompanionsServerData.data = data;
    PlayerCompanionsServerData.data.setDirty();
  }

  public static boolean available() {
    PlayerCompanionsServerData.get();
    return PlayerCompanionsServerData.data != null;
  }

  public static PlayerCompanionsServerData get() {
    if (PlayerCompanionsServerData.data == null) {
      prepare(ServerLifecycleHooks.getCurrentServer());
    }
    return PlayerCompanionsServerData.data;
  }

  public static String getFileId() {
    return PLAYER_COMPANIONS_FILE_ID;
  }

  private static void addPlayerCompanion(PlayerCompanionData playerCompanion) {
    playerCompanionsMap.put(playerCompanion.getUUID(), playerCompanion);
    UUID ownerUUID = playerCompanion.getOwnerUUID();
    if (ownerUUID != null) {
      Set<PlayerCompanionData> playerCompanions =
          companionsPerPlayerMap.computeIfAbsent(ownerUUID, key -> ConcurrentHashMap.newKeySet());
      // Make sure to remove existing entries with the same id, because Set's not supporting forced
      // adds like Maps and always relies on the equal function.
      if (playerCompanions.contains(playerCompanion)) {
        playerCompanions.remove(playerCompanion);
      }
      playerCompanions.add(playerCompanion);
    }
  }

  private static void addPlayerCompanion(CompoundTag compoundTag) {
    addPlayerCompanion(new PlayerCompanionData(compoundTag));
  }

  public static PlayerCompanionsServerData load(CompoundTag compoundTag) {
    // Create a backup before we loading anything!
    if (Boolean.TRUE.equals(COMMON.dataBackupEnabled.get())) {
      PlayerCompanionsServerDataBackup.saveBackup(compoundTag);
      if (COMMON.dataBackupInterval.get() > 0) {
        updateBackupTime(
            java.time.Instant.now().getEpochSecond() + (60 * COMMON.dataBackupInterval.get()));
      }
    }

    // Create a new data instance and set last update field.
    PlayerCompanionsServerData playerCompanionsData = new PlayerCompanionsServerData();
    log.info("{} loading data ...", Constants.LOG_ICON_NAME);

    // Restoring companions data
    if (compoundTag.contains(COMPANIONS_TAG)) {
      ListTag companionListTag = compoundTag.getList(COMPANIONS_TAG, 10);
      for (int i = 0; i < companionListTag.size(); ++i) {
        addPlayerCompanion(companionListTag.getCompound(i));
      }
    }

    return playerCompanionsData;
  }

  private static void updateBackupTime(long backupTime) {
    if (nextBackupTime != backupTime
        && backupTime > 0
        && backupTime >= java.time.Instant.now().getEpochSecond()) {
      log.info(
          "{} next planned backup is scheduled for {} in about {} min.",
          Constants.LOG_ICON_NAME,
          backupTime,
          (backupTime - java.time.Instant.now().getEpochSecond()) / 60);
      nextBackupTime = backupTime;
    }
  }

  public PlayerCompanionData getCompanion(ItemStack itemStack) {
    UUID companionUUID = null;
    CompoundTag compoundTag = itemStack.getOrCreateTag();
    if (compoundTag.hasUUID(CapturedCompanion.COMPANION_UUID_TAG)) {
      companionUUID = compoundTag.getUUID(CapturedCompanion.COMPANION_UUID_TAG);
    }
    if (companionUUID != null) {
      return getCompanion(companionUUID);
    }
    return null;
  }

  public boolean hasCompanion(ItemStack itemStack) {
    return getCompanion(itemStack) != null;
  }

  public PlayerCompanionData getCompanion(PlayerCompanionEntity playerCompanionEntity) {
    return getCompanion(playerCompanionEntity.getUUID());
  }

  public PlayerCompanionData getCompanion(UUID companionUUID) {
    return playerCompanionsMap.get(companionUUID);
  }

  public Entity getCompanionEntity(UUID companionUUID, ServerLevel serverLevel) {
    PlayerCompanionData playerCompanionData = getCompanion(companionUUID);
    if (playerCompanionData != null && serverLevel != null) {
      return serverLevel.getEntity(playerCompanionData.getUUID());
    }
    return null;
  }

  public Map<UUID, PlayerCompanionData> getCompanions() {
    return playerCompanionsMap;
  }

  public Set<PlayerCompanionData> getCompanions(UUID ownerUUID) {
    return companionsPerPlayerMap.get(ownerUUID);
  }

  public Set<Entity> getCompanionsEntity(UUID ownerUUID, ServerLevel serverLevel) {
    Set<Entity> result = new HashSet<>();
    Set<PlayerCompanionData> playerCompanionsData = getCompanions(ownerUUID);
    if (playerCompanionsData != null) {
      for (PlayerCompanionData playerCompanionData : playerCompanionsData) {
        Entity entity = getCompanionEntity(playerCompanionData.getUUID(), serverLevel);
        if (entity != null) {
          result.add(entity);
        }
      }
    }
    return result;
  }

  public void updateOrRegisterCompanion(PlayerCompanionEntity companionEntity) {
    if (playerCompanionsMap.get(companionEntity.getUUID()) == null) {
      registerCompanion(companionEntity);
    } else {
      updatePlayerCompanion(companionEntity);
    }
  }

  public PlayerCompanionData updatePlayerCompanion(PlayerCompanionEntity companionEntity) {
    PlayerCompanionData playerCompanion = playerCompanionsMap.get(companionEntity.getUUID());
    if (playerCompanion == null) {
      log.error(
          "Failed to update player companion {} because it does not exists!", companionEntity);
      registerCompanion(companionEntity);
      return null;
    }

    // Update the existing data with current data.
    playerCompanion.load(companionEntity);

    // Update Companions per Player Map for faster and easier access.
    UUID ownerUUID = playerCompanion.getOwnerUUID();
    if (ownerUUID != null) {
      Set<PlayerCompanionData> playerCompanions =
          companionsPerPlayerMap.computeIfAbsent(ownerUUID, key -> ConcurrentHashMap.newKeySet());
      // Make sure to remove existing entries with the same id, because Set's not supporting forced
      // adds like Maps and always relies on the equal function.
      if (playerCompanions.contains(playerCompanion)) {
        playerCompanions.remove(playerCompanion);
      }
      playerCompanions.add(playerCompanion);
    }

    // Store data to disk.
    this.setDirty();

    // Sync data (server -> client-side) with player companion owner, if any.
    syncPlayerCompanionData(playerCompanion);

    return playerCompanion;
  }

  public void updatePlayerCompanionData(PlayerCompanionEntity companionEntity) {
    if (companionEntity.getId() > 1) {
      updatePlayerCompanion(companionEntity);
    }
  }

  public PlayerCompanionData registerCompanion(PlayerCompanionEntity companionEntity) {
    return registerCompanion(companionEntity, true);
  }

  public PlayerCompanionData registerCompanion(
      PlayerCompanionEntity companionEntity, boolean requiredOwner) {
    if (playerCompanionsMap.get(companionEntity.getUUID()) != null) {
      log.warn("Companion {} is already registered!", companionEntity);
      return playerCompanionsMap.get(companionEntity.getUUID());
    }
    if (requiredOwner && !companionEntity.hasOwner()) {
      log.debug(
          "Skipping companion {} for registration because it has no owner, yet!", companionEntity);
      return null;
    }
    if (companionEntity.hasOwner()) {
      log.info("Register companion {} for {} ...", companionEntity, companionEntity.getOwner());
    }
    PlayerCompanionData playerCompanion = new PlayerCompanionData(companionEntity);
    addPlayerCompanion(playerCompanion);
    this.setDirty();

    // Sync data (server -> client-side) with player companion owner, if any.
    if (playerCompanion.hasOwner()) {
      syncPlayerCompanionData(playerCompanion);
    }

    return playerCompanion;
  }

  public void unregisterCompanion(PlayerCompanionEntity companionEntity) {
    PlayerCompanionData playerCompanion = playerCompanionsMap.remove(companionEntity.getUUID());
    if (playerCompanion != null) {
      log.info("Unregister Player Companion {} ...", playerCompanion.getUUID());
      UUID ownerUUID = playerCompanion.getOwnerUUID();
      if (ownerUUID != null) {
        Set<PlayerCompanionData> playerCompanions = companionsPerPlayerMap.get(ownerUUID);
        if (playerCompanions != null) {
          log.info(
              "Unregister Player Companion {} from owner {} ...",
              playerCompanion.getUUID(),
              ownerUUID);
          playerCompanions.remove(playerCompanion);
        }

        // Sync data (server -> client-side) with player companion owner.
        syncPlayerCompanionsData(ownerUUID);
      }
      this.setDirty();
    }
  }

  public void syncPlayerCompanionsData(UUID ownerUUID) {
    if (ownerUUID == null) {
      return;
    }
    Set<PlayerCompanionData> playerCompanionsData = getCompanions(ownerUUID);
    PlayerCompanionsServerDataClientSync.syncPlayerCompanionData(ownerUUID, playerCompanionsData);
  }

  public void syncPlayerCompanionData(PlayerCompanionData playerCompanionData) {
    PlayerCompanionsServerDataClientSync.syncPlayerCompanionData(playerCompanionData);
  }

  @Override
  public CompoundTag save(CompoundTag compoundTag) {
    log.info("{} saving data ... {}", Constants.LOG_ICON_NAME, this);

    // Iterate throw all companions and store their full data (meta + entity data).
    ListTag companionListTag = new ListTag();
    Iterator<PlayerCompanionData> playerCompanionIterator = playerCompanionsMap.values().iterator();
    while (playerCompanionIterator.hasNext()) {
      PlayerCompanionData playerCompanion = playerCompanionIterator.next();
      if (playerCompanion != null) {
        CompoundTag playerCompanionCompoundTag = new CompoundTag();
        playerCompanion.save(playerCompanionCompoundTag);
        companionListTag.add(playerCompanionCompoundTag);
      }
    }
    compoundTag.put(COMPANIONS_TAG, companionListTag);

    // Iterate all NPC.
    ListTag npcListTag = new ListTag();
    compoundTag.put(NPC_TAG, npcListTag);

    // Create a automatic backup, if enabled for the configured interval.
    if (Boolean.TRUE.equals(COMMON.dataBackupEnabled.get())
        && nextBackupTime > 0
        && java.time.Instant.now().getEpochSecond() >= nextBackupTime) {
      PlayerCompanionsServerDataBackup.saveBackup(compoundTag);
      updateBackupTime(
          java.time.Instant.now().getEpochSecond() + (60 * COMMON.dataBackupInterval.get()));
    }

    return compoundTag;
  }
}
