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

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import de.markusbordihn.playercompanions.entity.CompanionType;
import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.entity.CompanionEntity;

public class PlayerCompanion {

  public static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final String ACTIVE_TAG = "Active";
  private static final String LEVEL_TAG = "Level";
  private static final String NAME_TAG = "Name";
  private static final String OWNER_TAG = "Owner";
  private static final String POSITION_TAG = "Pos";
  private static final String REMOVED_TAG = "Removed";
  private static final String RESPAWN_TIMER_TAG = "RespawnTimer";
  private static final String TYPE_TAG = "Type";
  private static final String UUID_TAG = "UUID";

  private BlockPos blockPos;
  private boolean active = true;
  private boolean hasOwner = false;
  private boolean respawnTimer = false;
  private boolean isRemoved = false;
  private CompanionType type = CompanionType.UNKNOWN;
  private ResourceKey<Level> level;
  private String levelName = "";
  private String name = "";
  private UUID companionUUID = null;
  private UUID ownerUUID = null;

  public PlayerCompanion(CompanionEntity companion) {
    this.companionUUID = companion.getUUID();
    this.name = companion.getCustomCompanionName();
    this.type = companion.getCompanionType();
    this.hasOwner = companion.hasOwner();
    if (this.hasOwner) {
      this.ownerUUID = companion.getOwnerUUID();
    }
    this.respawnTimer = companion.hasRespawnTimer();
    this.blockPos = companion.blockPosition();
    this.level = companion.getLevel().dimension();
    this.levelName = this.level.getRegistryName() + "/" + this.level.location();
  }

  public PlayerCompanion(CompoundTag compoundTag) {
    this.companionUUID = compoundTag.getUUID(UUID_TAG);
    this.name = compoundTag.getString(NAME_TAG);
    if (compoundTag.contains(TYPE_TAG)) {
      this.type = CompanionType.valueOf(compoundTag.getString(TYPE_TAG));
    }
    if (compoundTag.hasUUID(OWNER_TAG)) {
      this.ownerUUID = compoundTag.getUUID(OWNER_TAG);
    }
    this.active = compoundTag.getBoolean(ACTIVE_TAG);
    this.isRemoved = compoundTag.getBoolean(REMOVED_TAG);
    this.hasOwner = this.companionUUID != null;
    this.respawnTimer = compoundTag.getBoolean(RESPAWN_TIMER_TAG);
    this.blockPos = NbtUtils.readBlockPos(compoundTag.getCompound(POSITION_TAG));
    if (compoundTag.contains(LEVEL_TAG)) {
      this.levelName = compoundTag.getString(LEVEL_TAG);
      if (this.levelName.contains("/")) {
        String[] levelNameParts = this.levelName.split("/");
        ResourceLocation registryName = new ResourceLocation(levelNameParts[0]);
        ResourceLocation locationName = new ResourceLocation(levelNameParts[1]);
        this.level = ResourceKey.create(ResourceKey.createRegistryKey(registryName), locationName);
      }
    }
  }

  public UUID getUUID() {
    return this.companionUUID;
  }

  public String getName() {
    return this.name;
  }

  public UUID getOwnerUUID() {
    if (this.ownerUUID == null) {
      return null;
    }
    return this.ownerUUID;
  }

  public CompanionType getType() {
    return this.type;
  }

  public boolean hasRespawnTimer() {
    return this.respawnTimer;
  }

  public CompoundTag save(CompoundTag compoundTag) {
    compoundTag.putUUID(UUID_TAG, this.companionUUID);
    compoundTag.putString(NAME_TAG, this.name);
    compoundTag.putString(TYPE_TAG, this.type.name());
    if (this.ownerUUID != null) {
      compoundTag.putUUID(OWNER_TAG, this.ownerUUID);
    }
    compoundTag.putBoolean(ACTIVE_TAG, this.active);
    compoundTag.putBoolean(REMOVED_TAG, this.isRemoved);
    compoundTag.putBoolean(RESPAWN_TIMER_TAG, this.respawnTimer);
    compoundTag.put(POSITION_TAG, NbtUtils.writeBlockPos(this.blockPos));
    if (!this.levelName.isEmpty()) {
      compoundTag.putString(LEVEL_TAG, this.levelName);
    }
    return compoundTag;
  }

  public boolean is(PlayerCompanion playerCompanion) {
    return this.companionUUID == playerCompanion.companionUUID;
  }

  public boolean changed(PlayerCompanion playerCompanion) {
    return playerCompanion.getUUID() != this.getUUID()
        || !playerCompanion.getName().equals(this.getName())
        || playerCompanion.getOwnerUUID() != this.getOwnerUUID()
        || playerCompanion.getType() != this.getType()
        || playerCompanion.hasRespawnTimer() != this.hasRespawnTimer();
  }

  public String toString() {
    return "PlayerCompanion['" + this.name + "', type=" + this.type + ", owner=" + this.ownerUUID
        + ", x=" + this.blockPos.getX() + ", y=" + this.blockPos.getY() + ", z="
        + this.blockPos.getZ() + ", hasRespawnTimer=" + this.respawnTimer + ", id="
        + this.companionUUID + "]";
  }

  @Override
  public boolean equals(Object object) {
    if (object == this) {
      return true;
    }

    if (!(object instanceof PlayerCompanion)) {
      return false;
    }

    PlayerCompanion playerCompanion = (PlayerCompanion) object;
    return playerCompanion.getUUID().equals(this.companionUUID);
  }

  @Override
  public int hashCode() {
    return this.companionUUID.hashCode();
  }
}
