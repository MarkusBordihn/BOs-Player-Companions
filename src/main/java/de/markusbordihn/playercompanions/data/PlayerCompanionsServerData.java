/**
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

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.SavedData;

import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.server.ServerLifecycleHooks;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.item.CapturedCompanion;

@EventBusSubscriber
public class PlayerCompanionsServerData extends SavedData {

  public static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public static final String COMPANIONS_TAG = "Companions";
  public static final String LAST_UPDATE_TAG = "LastUpdate";
  public static final String NPC_TAG = "NPCs";

  private static ConcurrentHashMap<UUID, PlayerCompanionData> playerCompanionsMap =
      new ConcurrentHashMap<>();
  private static ConcurrentHashMap<UUID, Set<PlayerCompanionData>> companionsPerPlayerMap =
      new ConcurrentHashMap<>();

  private static MinecraftServer server;
  private static PlayerCompanionsServerData data;

  private static final String PLAYER_COMPANIONS_FILE_ID = Constants.MOD_ID;

  private long lastUpdate;


  @SubscribeEvent
  public static void handleServerAboutToStartEvent(ServerAboutToStartEvent event) {
    playerCompanionsMap = new ConcurrentHashMap<>();
    companionsPerPlayerMap = new ConcurrentHashMap<>();
  }

  public PlayerCompanionsServerData() {
    this.setDirty();
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
    PlayerCompanionsServerData.data = server.getLevel(Level.OVERWORLD).getDataStorage()
        .computeIfAbsent(PlayerCompanionsServerData::load, PlayerCompanionsServerData::new,
            PlayerCompanionsServerData.getFileId());
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

  public static String getFileId(DimensionType dimensionType) {
    return PLAYER_COMPANIONS_FILE_ID + dimensionType.getFileSuffix();
  }

  public long getLastUpdate() {
    return lastUpdate;
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
    for (PlayerCompanionData playerCompanionData : playerCompanionsData) {
      Entity entity = getCompanionEntity(playerCompanionData.getUUID(), serverLevel);
      if (entity != null) {
        result.add(entity);
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
      log.error("Failed to update player companion {} because it does not exists!",
          companionEntity);
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
    if (playerCompanionsMap.get(companionEntity.getUUID()) != null) {
      log.warn("Companion {} is already registered!", companionEntity);
      return playerCompanionsMap.get(companionEntity.getUUID());
    }
    if (!companionEntity.hasOwner()) {
      log.debug("Skipping companion {} for registration because it has no owner, yet!", companionEntity);
      return null;
    }
    log.info("Register companion {} for {} ...", companionEntity, companionEntity.getOwner());
    PlayerCompanionData playerCompanion = new PlayerCompanionData(companionEntity);
    addPlayerCompanion(playerCompanion);
    this.setDirty();

    // Sync data (server -> client-side) with player companion owner, if any.
    syncPlayerCompanionData(playerCompanion);

    return playerCompanion;
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
    PlayerCompanionsServerData playerCompanionsData = new PlayerCompanionsServerData();
    log.info("{} loading data ...", Constants.LOG_ICON_NAME);
    playerCompanionsData.lastUpdate = compoundTag.getLong(LAST_UPDATE_TAG);

    // Restoring companions data
    if (compoundTag.contains(COMPANIONS_TAG)) {
      ListTag companionListTag = compoundTag.getList(COMPANIONS_TAG, 10);
      for (int i = 0; i < companionListTag.size(); ++i) {
        addPlayerCompanion(companionListTag.getCompound(i));
      }
    }

    return playerCompanionsData;
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
    compoundTag.putLong(LAST_UPDATE_TAG, new Date().getTime());

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

    // Iterate all NPC (wip).
    ListTag npcListTag = new ListTag();
    compoundTag.put(NPC_TAG, npcListTag);

    return compoundTag;
  }

}
