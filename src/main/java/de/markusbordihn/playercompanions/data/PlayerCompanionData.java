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
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import de.markusbordihn.playercompanions.entity.PlayerCompanionType;
import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;

public class PlayerCompanionData {

  public static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final String ACTIVE_TAG = "Active";
  private static final String LEVEL_TAG = "Level";
  private static final String NAME_TAG = "Name";
  private static final String OWNER_TAG = "Owner";
  private static final String OWNER_NAME_TAG = "OwnerName";
  private static final String POSITION_TAG = "Position";
  private static final String REMOVED_TAG = "Removed";
  private static final String TYPE_TAG = "Type";
  private static final String UUID_TAG = "UUID";
  private static final String ENTITY_DATA_TAG = "EntityData";
  private static final String ENTITY_HEALTH_MAX_TAG = "EntityHealthMax";
  private static final String ENTITY_HEALTH_TAG = "EntityHealth";
  private static final String ENTITY_ID_TAG = "EntityId";
  private static final String ENTITY_RESPAWN_TIMER_TAG = "EntityRespawnTimer";
  private static final String ENTITY_SITTING_TAG = "EntitySitting";
  private static final String ENTITY_TYPE_TAG = "EntityType";

  private BlockPos blockPos;
  private boolean active = true;
  private boolean hasOwner = false;
  private boolean isRemoved = false;
  private PlayerCompanionType type = PlayerCompanionType.UNKNOWN;
  private PlayerCompanionEntity companionEntity;
  private ResourceKey<Level> level;
  private String levelName = "";
  private String name = "";
  private String ownerName = "";
  private UUID companionUUID = null;
  private UUID ownerUUID = null;
  private CompoundTag entityData;
  private EntityType<?> entityType;
  private boolean entitySitting = false;
  private float entityHealth;
  private float entityHealthMax;
  private int entityId;
  private int entityRespawnTimer;

  public PlayerCompanionData(PlayerCompanionEntity companion) {
    load(companion);
  }

  public PlayerCompanionData(CompoundTag compoundTag) {
    load(compoundTag);
  }

  public boolean hasOwner() {
    return this.ownerUUID != null;
  }

  public boolean isOrderedToSit() {
    return this.entitySitting;
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

  public String getOwnerName() {
    return this.ownerName;
  }

  public PlayerCompanionType getType() {
    return this.type;
  }

  public int getEntityId() {
    return entityId;
  }

  public void setEntityId(int entityId) {
    this.entityId = entityId;
  }

  public EntityType<?> getEntityType() {
    return this.entityType;
  }

  public float getEntityHealth() {
    return this.entityHealth;
  }

  public float getEntityHealthMax() {
    return this.entityHealthMax;
  }

  public CompoundTag getEntityData() {
    return this.entityData;
  }

  public int getEntityRespawnTimer() {
    return this.entityRespawnTimer;
  }

  public boolean hasEntityRespawnTimer() {
    return this.entityRespawnTimer > 0;
  }

  public void load(PlayerCompanionEntity companion) {
    this.companionEntity = companion;
    this.companionUUID = companion.getUUID();
    this.name = companion.getCustomCompanionName();
    this.type = companion.getCompanionType();
    this.hasOwner = companion.hasOwner();
    if (this.hasOwner) {
      this.ownerUUID = companion.getOwnerUUID();
      if (companion.getOwner() != null) {
        this.ownerName = companion.getOwner().getName().getString();
      }
    }
    this.blockPos = companion.blockPosition();
    this.level = companion.getLevel().dimension();
    this.levelName = this.level.getRegistryName() + "/" + this.level.location();
    this.entityId = companion.getId();
    this.entityType = companion.getType();
    this.entityHealth = companion.getHealth();
    this.entityHealthMax = companion.getMaxHealth();
    this.entityRespawnTimer = companion.getRespawnTimer();
    this.entitySitting = companion.isOrderedToSit();
    this.entityData = companion.serializeNBT();
    log.debug("Created PlayerCompanion {} over entity with {}", this.name, this);
  }

  public void load(CompoundTag compoundTag) {
    this.companionUUID = compoundTag.getUUID(UUID_TAG);
    this.name = compoundTag.getString(NAME_TAG);
    if (compoundTag.contains(TYPE_TAG)) {
      this.type = PlayerCompanionType.valueOf(compoundTag.getString(TYPE_TAG));
    }
    this.hasOwner = compoundTag.hasUUID(OWNER_TAG);
    if (this.hasOwner) {
      this.ownerUUID = compoundTag.getUUID(OWNER_TAG);
      if (compoundTag.contains(OWNER_NAME_TAG)) {
        this.ownerName = compoundTag.getString(OWNER_NAME_TAG);
      }
    }
    this.active = compoundTag.getBoolean(ACTIVE_TAG);
    this.isRemoved = compoundTag.getBoolean(REMOVED_TAG);
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
    if (compoundTag.contains(ENTITY_ID_TAG)) {
      this.entityId = compoundTag.getInt(ENTITY_ID_TAG);
    }
    if (compoundTag.contains(ENTITY_TYPE_TAG)) {
      this.entityType =
          Registry.ENTITY_TYPE.get(new ResourceLocation(compoundTag.getString(ENTITY_TYPE_TAG)));
    }
    this.entityHealth = compoundTag.getFloat(ENTITY_HEALTH_TAG);
    this.entityHealthMax = compoundTag.getFloat(ENTITY_HEALTH_MAX_TAG);
    this.entityRespawnTimer = compoundTag.getInt(ENTITY_RESPAWN_TIMER_TAG);
    this.entitySitting = compoundTag.getBoolean(ENTITY_SITTING_TAG);
    this.entityData = compoundTag.getCompound(ENTITY_DATA_TAG);
    log.debug("Created PlayerCompanion {} over compoundTag with {}", this.name, this);
  }

  public CompoundTag save(CompoundTag compoundTag) {
    return save(compoundTag, true);
  }

  public CompoundTag saveMetaData(CompoundTag compoundTag) {
    return save(compoundTag, false);
  }

  public CompoundTag save(CompoundTag compoundTag, boolean includeData) {
    compoundTag.putUUID(UUID_TAG, this.companionUUID);
    compoundTag.putString(NAME_TAG, this.name);
    compoundTag.putString(TYPE_TAG, this.type.name());
    if (this.ownerUUID != null) {
      compoundTag.putUUID(OWNER_TAG, this.ownerUUID);
      compoundTag.putString(OWNER_NAME_TAG, this.ownerName);
    }
    compoundTag.putBoolean(ACTIVE_TAG, this.active);
    compoundTag.putBoolean(REMOVED_TAG, this.isRemoved);
    compoundTag.put(POSITION_TAG, NbtUtils.writeBlockPos(this.blockPos));
    if (!this.levelName.isEmpty()) {
      compoundTag.putString(LEVEL_TAG, this.levelName);
    }
    compoundTag.putInt(ENTITY_ID_TAG, this.entityId);
    compoundTag.putString(ENTITY_TYPE_TAG, this.entityType.getRegistryName().toString());
    compoundTag.putFloat(ENTITY_HEALTH_TAG, this.entityHealth);
    compoundTag.putFloat(ENTITY_HEALTH_MAX_TAG, this.entityHealthMax);
    compoundTag.putInt(ENTITY_RESPAWN_TIMER_TAG, this.entityRespawnTimer);
    compoundTag.putBoolean(ENTITY_SITTING_TAG, this.entitySitting);

    // Storing current companion entity data if available (regardless of disc status)
    if (includeData) {
      if (this.companionEntity != null && this.companionEntity.isAlive()) {
        CompoundTag companionData = this.companionEntity.serializeNBT();
        compoundTag.put(ENTITY_DATA_TAG, companionData);
      } else if (this.entityData != null) {
        compoundTag.put(ENTITY_DATA_TAG, this.entityData);
      }
    }

    // Sync specific meta data from entity directly, if available.
    if (this.companionEntity != null && this.companionEntity.isAlive()) {
      compoundTag.putBoolean(ENTITY_SITTING_TAG, this.companionEntity.isOrderedToSit());
      compoundTag.putFloat(ENTITY_HEALTH_MAX_TAG, this.companionEntity.getMaxHealth());
      compoundTag.putFloat(ENTITY_HEALTH_TAG, this.companionEntity.getHealth());
      if (this.companionEntity.getOwner() != null) {
        compoundTag.putString(OWNER_NAME_TAG,
            this.companionEntity.getOwner().getName().getString());
      }
    }

    return compoundTag;
  }

  public boolean changed(PlayerCompanionData playerCompanion) {
    return playerCompanion.getUUID() != this.getUUID()
        || !playerCompanion.getName().equals(this.getName())
        || playerCompanion.getOwnerUUID() != this.getOwnerUUID()
        || playerCompanion.getType() != this.getType()
        || playerCompanion.getEntityId() != this.getEntityId()
        || playerCompanion.getEntityHealth() != this.getEntityHealth()
        || playerCompanion.getEntityRespawnTimer() != this.getEntityRespawnTimer()
        || playerCompanion.isOrderedToSit() != this.isOrderedToSit();
  }

  public String toString() {
    return "PlayerCompanion['" + this.name + "', type=" + this.type + ", owner=" + this.ownerUUID
        + "(" + this.ownerName + "), entity=" + this.entityType + ", health=" + this.entityHealth
        + "/" + this.entityHealthMax + ", x=" + this.blockPos.getX() + ", y=" + this.blockPos.getY()
        + ", z=" + this.blockPos.getZ() + ", respawnTimer=" + this.entityRespawnTimer + ", id="
        + this.entityId + ", UUID=" + this.companionUUID + "]";
  }

  public boolean is(PlayerCompanionData playerCompanion) {
    return this.companionUUID == playerCompanion.companionUUID;
  }

  @Override
  public boolean equals(Object object) {
    if (object == this) {
      return true;
    }

    if (!(object instanceof PlayerCompanionData)) {
      return false;
    }

    PlayerCompanionData playerCompanion = (PlayerCompanionData) object;
    return playerCompanion.getUUID().equals(this.companionUUID);
  }

  @Override
  public int hashCode() {
    return this.companionUUID.hashCode();
  }
}
