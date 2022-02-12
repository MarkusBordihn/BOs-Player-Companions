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

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonSyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.stringtemplate.v4.compiler.STParser.compoundElement_return;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.item.CapturedCompanion;

public class PlayerCompanionsClientData {

  public static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static long lastUpdate;
  private static Set<PlayerCompanionData> playerCompanionsSet = ConcurrentHashMap.newKeySet();
  private static ConcurrentHashMap<UUID, PlayerCompanionData> playerCompanionsMap =
      new ConcurrentHashMap<>();

  protected PlayerCompanionsClientData() {

  }

  public static PlayerCompanionData getCompanion(ItemStack itemStack) {
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

  public static PlayerCompanionData getCompanion(UUID companionUUID) {
    return playerCompanionsMap.get(companionUUID);
  }

  public static PlayerCompanionData getCompanion(LivingEntity livingEntity) {
    if (livingEntity instanceof PlayerCompanionEntity companionEntity) {
      return playerCompanionsMap.get(companionEntity.getUUID());
    }
    return null;
  }

  public static Set<PlayerCompanionData> getCompanions() {
    return playerCompanionsSet;
  }

  public static long getLastUpdate() {
    return lastUpdate;
  }

  public static void load(String data) {
    log.debug("{} loading client data string ... {}", Constants.LOG_ICON_NAME, data);
    CompoundTag compoundTag;
    try {
      compoundTag = TagParser.parseTag(data);
    } catch (CommandSyntaxException commandSyntaxException) {
      throw new JsonSyntaxException("Invalid nbt tag: " + commandSyntaxException.getMessage());
    }
    if (compoundTag != null) {
      load(compoundTag);
    }
  }

  public static void load(CompoundTag compoundTag) {
    log.debug("{} loading client data ... {}", Constants.LOG_ICON_NAME, compoundTag);
    lastUpdate = compoundTag.getLong(PlayerCompanionsServerData.LAST_UPDATE_TAG);

    // Restoring companions data
    if (compoundTag.contains(PlayerCompanionsServerData.COMPANIONS_TAG)) {
      ListTag companionListTag = compoundTag.getList(PlayerCompanionsServerData.COMPANIONS_TAG, 10);
      for (int i = 0; i < companionListTag.size(); ++i) {
        PlayerCompanionData playerCompanion = new PlayerCompanionData(companionListTag.getCompound(i));
        playerCompanionsMap.put(playerCompanion.getUUID(), playerCompanion);
        playerCompanionsSet.add(playerCompanion);
      }
    }
  }

}
