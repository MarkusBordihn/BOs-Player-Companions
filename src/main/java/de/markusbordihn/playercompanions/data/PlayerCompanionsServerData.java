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
import net.minecraft.server.level.ServerPlayer;
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
import de.markusbordihn.playercompanions.network.NetworkHandler;

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

  public PlayerCompanionData getCompanion(UUID companionUUID) {
    return playerCompanionsMap.get(companionUUID);
  }

  public Map<UUID, PlayerCompanionData> getCompanions() {
    return playerCompanionsMap;
  }

  public Set<PlayerCompanionData> getCompanions(UUID ownerUUID) {
    return companionsPerPlayerMap.get(ownerUUID);
  }

  public void updateOrRegisterCompanion(PlayerCompanionEntity companionEntity) {
    if (playerCompanionsMap.get(companionEntity.getUUID()) == null) {
      registerCompanion(companionEntity);
    } else {
      updatePlayerCompanion(companionEntity);
    }
  }

  public void updatePlayerCompanion(PlayerCompanionEntity companionEntity) {
    PlayerCompanionData currentPlayerCompanion = playerCompanionsMap.get(companionEntity.getUUID());
    if (currentPlayerCompanion == null) {
      log.error("Player Companion {} does not exists!", companionEntity);
      return;
    }
    PlayerCompanionData playerCompanion = new PlayerCompanionData(companionEntity);

    // Only update the data in the case there is a relevant change.
    if (Boolean.FALSE.equals(playerCompanion.changed(currentPlayerCompanion))) {
      return;
    }
    log.debug("Update player companion from {} to {} ...", currentPlayerCompanion, playerCompanion);
    addPlayerCompanion(playerCompanion);

    // Store data to disk.
    this.setDirty();

    // Sync data (client-side) with player companion owner, if any.
    syncPlayerCompanion(playerCompanion);
  }

  public void updatePlayerCompanionData(PlayerCompanionEntity companionEntity) {
    if (companionEntity.getId() > 1) {
      updatePlayerCompanion(companionEntity);
    }
  }

  public void registerCompanion(PlayerCompanionEntity companionEntity) {
    if (playerCompanionsMap.get(companionEntity.getUUID()) != null) {
      log.warn("Companion {} is already registered!", companionEntity);
      return;
    }
    log.debug("Register companion {} ...", companionEntity);
    addPlayerCompanion(new PlayerCompanionData(companionEntity));
    this.setDirty();
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
    log.info("{} loading data ... {}", Constants.LOG_ICON_NAME, compoundTag);
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

  public String exportClientData(UUID ownerUUID) {
    // Pre-checks to avoid errors or edge conditions like server start.
    if (ownerUUID == null) {
      return "";
    }
    Set<PlayerCompanionData> playerCompanions = getCompanions(ownerUUID);
    if (playerCompanions == null || playerCompanions.isEmpty()) {
      return "";
    }

    // Create client data
    CompoundTag compoundTag = new CompoundTag();
    compoundTag.putLong(LAST_UPDATE_TAG, new Date().getTime());
    ListTag companionListTag = new ListTag();

    // Iterate over all known player companions for the owner and get their meta data.
    Iterator<PlayerCompanionData> playerCompanionIterator = playerCompanions.iterator();
    while (playerCompanionIterator.hasNext()) {
      PlayerCompanionData playerCompanion = playerCompanionIterator.next();
      if (playerCompanion != null) {
        CompoundTag playerCompanionCompoundTag = new CompoundTag();
        playerCompanion.saveMetaData(playerCompanionCompoundTag);
        companionListTag.add(playerCompanionCompoundTag);
      }
    }
    compoundTag.put(COMPANIONS_TAG, companionListTag);

    return compoundTag.getAsString();
  }

  public void syncPlayerCompanion(PlayerCompanionData playerCompanion) {
    // Only sync player companions, if we have a valid owner.
    if (!playerCompanion.hasOwner()) {
      return;
    }
    ServerPlayer serverPlayer = server.getPlayerList().getPlayer(playerCompanion.getOwnerUUID());
    if (serverPlayer != null) {
      NetworkHandler.updatePlayerCompanionsData(serverPlayer);
    }
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
