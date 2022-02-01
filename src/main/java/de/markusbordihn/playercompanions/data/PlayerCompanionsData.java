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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.server.ServerLifecycleHooks;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.entity.CompanionEntity;

@EventBusSubscriber
public class PlayerCompanionsData extends SavedData {

  public static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public static final String COMPANIONS_TAG = "Companions";
  public static final String NPC_TAG = "NPCs";

  private static ConcurrentHashMap<UUID, PlayerCompanion> playerCompanionsMap =
      new ConcurrentHashMap<>();
  private static ConcurrentHashMap<UUID, Set<PlayerCompanion>> companionsPerPlayerMap =
      new ConcurrentHashMap<>();
  private static MinecraftServer server;
  private static PlayerCompanionsData data;
  private static final String PLAYER_COMPANIONS_FILE_ID = Constants.MOD_ID;

  private long lastUpdate;

  @SubscribeEvent
  public static void handleServerAboutToStartEvent(ServerAboutToStartEvent event) {
    playerCompanionsMap = new ConcurrentHashMap<>();
    companionsPerPlayerMap = new ConcurrentHashMap<>();
  }

  public PlayerCompanionsData() {
    this.setDirty();
  }

  public static void prepare(MinecraftServer server) {
    // Make sure we preparing the data only once for the same server!
    if (server == PlayerCompanionsData.server && PlayerCompanionsData.data != null) {
      return;
    }

    log.info("{} preparing data for {}", Constants.LOG_ICON_NAME, server);
    PlayerCompanionsData.server = server;

    // Using a global approach and storing relevant data in the overworld only!
    PlayerCompanionsData.data = server.getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(
        PlayerCompanionsData::load, PlayerCompanionsData::new, PlayerCompanionsData.getFileId());
  }

  public static PlayerCompanionsData get() {
    if (PlayerCompanionsData.data == null) {
      prepare(ServerLifecycleHooks.getCurrentServer());
    }
    return PlayerCompanionsData.data;
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

  public PlayerCompanion getCompanion(UUID companionUUID) {
    return playerCompanionsMap.get(companionUUID);
  }

  public Map<UUID, PlayerCompanion> getCompanions() {
    return playerCompanionsMap;
  }

  public Set<PlayerCompanion> getCompanions(UUID ownerUUID) {
    return companionsPerPlayerMap.get(ownerUUID);
  }

  public void updateOrRegisterCompanion(CompanionEntity companionEntity) {
    if (playerCompanionsMap.get(companionEntity.getUUID()) == null) {
      registerCompanion(companionEntity);
    } else {
      updateCompanion(companionEntity);
    }
  }

  public void updateCompanion(CompanionEntity companionEntity) {
    if (playerCompanionsMap.get(companionEntity.getUUID()) == null) {
      log.error("Companion {} does not exists!", companionEntity);
      return;
    }
    PlayerCompanion playerCompanion = new PlayerCompanion(companionEntity);
    if (Boolean.FALSE
        .equals(playerCompanion.changed(playerCompanionsMap.get(companionEntity.getUUID())))) {
      return;
    }
    log.info("Update companion {} ...", companionEntity);
    add(playerCompanion);
    this.setDirty();
  }

  public void registerCompanion(CompanionEntity companionEntity) {
    if (playerCompanionsMap.get(companionEntity.getUUID()) != null) {
      log.warn("Companion {} is already registered!", companionEntity);
      return;
    }
    log.info("Register companion {} ...", companionEntity);
    PlayerCompanion playerCompanion = new PlayerCompanion(companionEntity);
    add(playerCompanion);
    this.setDirty();
  }

  private static void add(PlayerCompanion playerCompanion) {
    playerCompanionsMap.put(playerCompanion.getUUID(), playerCompanion);
    UUID ownerUUID = playerCompanion.getOwnerUUID();
    if (ownerUUID != null) {
      Set<PlayerCompanion> playerCompanions =
          companionsPerPlayerMap.computeIfAbsent(ownerUUID, k -> ConcurrentHashMap.newKeySet());
      log.info("{} {}", playerCompanion, companionsPerPlayerMap);
      playerCompanions.add(playerCompanion);
    }
  }

  private static void add(CompoundTag compoundTag) {
    PlayerCompanion playerCompanion = new PlayerCompanion(compoundTag);
    add(playerCompanion);
  }

  public static PlayerCompanionsData load(CompoundTag compoundTag) {
    PlayerCompanionsData playerCompanionsData = new PlayerCompanionsData();
    log.info("{} loading data ... {}", Constants.LOG_ICON_NAME, compoundTag);
    playerCompanionsData.lastUpdate = compoundTag.getLong("LastUpdate");

    // Restoring companions data
    if (compoundTag.contains(COMPANIONS_TAG)) {
      ListTag companionListTag = compoundTag.getList(COMPANIONS_TAG, 10);
      for (int i = 0; i < companionListTag.size(); ++i) {
        add(companionListTag.getCompound(i));
      }
    }
    return playerCompanionsData;
  }

  @Override
  public CompoundTag save(CompoundTag compoundTag) {
    log.info("{} saving data ... {}", Constants.LOG_ICON_NAME, this);
    compoundTag.putLong("LastUpdate", new Date().getTime());

    // Iterate throw all companions.
    ListTag companionListTag = new ListTag();
    Iterator<PlayerCompanion> playerCompanionIterator = playerCompanionsMap.values().iterator();
    while (playerCompanionIterator.hasNext()) {
      PlayerCompanion playerCompanion = playerCompanionIterator.next();
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

    return compoundTag;
  }

}
