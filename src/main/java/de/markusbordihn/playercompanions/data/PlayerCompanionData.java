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

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.entity.type.PlayerCompanionType;

public class PlayerCompanionData {

  public static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final String ACTIVE_TAG = "Active";
  private static final String ENTITY_DATA_TAG = "EntityData";
  private static final String ENTITY_HEALTH_MAX_TAG = "EntityHealthMax";
  private static final String ENTITY_HEALTH_TAG = "EntityHealth";
  private static final String ENTITY_ID_TAG = "EntityId";
  private static final String ENTITY_RESPAWN_TIMER_TAG = "EntityRespawnTimer";
  private static final String ENTITY_SITTING_TAG = "EntitySitting";
  private static final String ENTITY_ORDERED_TO_POSITION = "EntityOrderedToPosition";
  private static final String ENTITY_TARGET_TAG = "EntityTarget";
  private static final String ENTITY_TYPE_TAG = "EntityType";
  private static final String LEVEL_TAG = "Level";
  private static final String NAME_TAG = "Name";
  private static final String OWNER_NAME_TAG = "OwnerName";
  private static final String OWNER_TAG = "Owner";
  private static final String POSITION_TAG = "Position";
  private static final String REMOVED_TAG = "Removed";
  private static final String TYPE_TAG = "Type";
  private static final String UUID_TAG = "UUID";

  private BlockPos blockPos;
  private CompoundTag entityData;
  private EntityType<?> entityType;
  private NonNullList<ItemStack> handItems = NonNullList.withSize(2, ItemStack.EMPTY);
  private NonNullList<ItemStack> inventoryItems = NonNullList.withSize(16, ItemStack.EMPTY);
  private PlayerCompanionEntity companionEntity;
  private PlayerCompanionType type = PlayerCompanionType.UNKNOWN;
  private ResourceKey<Level> level;
  private ClientLevel clientLevel;
  private ServerLevel serverLevel;
  private String entityTarget = "";
  private String levelName = "";
  private String name = "";
  private String ownerName = "";
  private UUID companionUUID = null;
  private UUID ownerUUID = null;
  private boolean active = true;
  private boolean entityOrderedToPosition = false;
  private boolean entitySitting = false;
  private boolean hasOwner = false;
  private boolean isRemoved = false;
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

  public boolean isOrderedToPosition() {
    return this.entityOrderedToPosition;
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
    this.setDirty();
  }

  public EntityType<?> getEntityType() {
    return this.entityType;
  }

  public PlayerCompanionEntity getPlayerCompanionEntity() {
    if (this.companionEntity == null) {
      Entity entity = null;
      if (this.serverLevel != null && this.companionUUID != null) {
        entity = this.serverLevel.getEntity(this.companionUUID);
      } else if (this.clientLevel != null && this.entityId > 0) {
        entity = this.clientLevel.getEntity(this.entityId);
      }
      if (entity instanceof PlayerCompanionEntity playerCompanionEntity) {
        this.companionEntity = playerCompanionEntity;
      }
    }
    return this.companionEntity;
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

  public void syncEntityData(LivingEntity livingEntity) {
    if (livingEntity != null) {
      this.entityData = livingEntity.serializeNBT();
    }
  }

  public void syncEntityData() {
    PlayerCompanionEntity playerCompanionEntity = getPlayerCompanionEntity();
    syncEntityData(playerCompanionEntity);
  }

  public int getEntityRespawnTimer() {
    return this.entityRespawnTimer;
  }

  public String getEntityTarget() {
    return this.entityTarget;
  }

  public boolean hasEntityTarget() {
    return !this.entityTarget.isBlank();
  }

  public NonNullList<ItemStack> getHandItems() {
    return this.handItems;
  }

  public void setHandItems(NonNullList<ItemStack> hand) {
    this.handItems = hand;
    this.setDirty();
  }

  public void setHandItem(int index, ItemStack itemStack) {
    this.handItems.set(index, itemStack);
    this.setDirty();
  }

  public ItemStack getHandItem(int index) {
    return this.handItems.get(index);
  }

  public int getHandItemsSize() {
    return this.handItems.size();
  }

  public NonNullList<ItemStack> getInventoryItems() {
    return this.inventoryItems;
  }

  public void setInventoryItems(NonNullList<ItemStack> inventory) {
    this.inventoryItems = inventory;
    this.setDirty();
  }

  public void setInventoryItem(int index, ItemStack itemStack) {
    this.inventoryItems.set(index, itemStack);
    this.setDirty();
  }

  public ItemStack getInventoryItem(int index) {
    return this.inventoryItems.get(index);
  }

  public int getInventoryItemsSize() {
    return this.inventoryItems.size();
  }

  public boolean storeInventoryItem(ItemStack itemStack) {
    // Iterate trough item stacks to find a matching or empty item stack to store the item.
    Item item = itemStack.getItem();
    int numberOfItems = itemStack.getCount();
    for (int index = 0; index < getInventoryItemsSize(); index++) {
      ItemStack existingItems = getInventoryItem(index);
      if (!existingItems.isEmpty() && existingItems.is(item)
          && existingItems.getCount() + numberOfItems < existingItems.getMaxStackSize()) {
        existingItems.grow(numberOfItems);
        return true;
      }
      if (existingItems.isEmpty()) {
        setInventoryItem(index, new ItemStack(item));
        return true;
      }
    }
    return false;
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
    this.entityOrderedToPosition = companion.isOrderedToPosition();
    this.entityData = companion.serializeNBT();

    LivingEntity targetEntity = companion.getTarget();
    this.entityTarget = targetEntity == null ? "" : targetEntity.getEncodeId();

    // Handle level references (client and server)
    Level companionLevel = companion.getLevel();
    if (companionLevel != null) {
      if (companionLevel.isClientSide) {
        this.clientLevel = (ClientLevel) companion.getLevel();
      } else {
        this.serverLevel = (ServerLevel) companion.getLevel();
      }
    }

    // Handle hand items.
    setHandItems((NonNullList<ItemStack>) companion.getHandSlots());

    log.debug("Loaded PlayerCompanion {} data over entity with {}", this.name, this);
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
    this.entityOrderedToPosition = compoundTag.getBoolean(ENTITY_ORDERED_TO_POSITION);
    this.entityTarget = compoundTag.getString(ENTITY_TARGET_TAG);
    this.entityData = compoundTag.getCompound(ENTITY_DATA_TAG);

    // Load Hand
    PlayerCompanionDataHelper.loadHandItems(compoundTag, this.handItems);

    // Load inventory
    PlayerCompanionDataHelper.loadInventoryItems(compoundTag, this.inventoryItems);

    log.debug("Loaded PlayerCompanion {} data over compoundTag with {}", this.name, this);
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
    compoundTag.putBoolean(ENTITY_SITTING_TAG, this.entitySitting);
    compoundTag.putBoolean(ENTITY_ORDERED_TO_POSITION, this.entityOrderedToPosition);
    compoundTag.putFloat(ENTITY_HEALTH_MAX_TAG, this.entityHealthMax);
    compoundTag.putFloat(ENTITY_HEALTH_TAG, this.entityHealth);
    compoundTag.putInt(ENTITY_RESPAWN_TIMER_TAG, this.entityRespawnTimer);
    compoundTag.putString(ENTITY_TARGET_TAG, this.entityTarget);

    // Try to get current Player Companion if not exists.
    PlayerCompanionEntity playerCompanionEntity = this.getPlayerCompanionEntity();

    // Storing current companion entity data if available (regardless of disc status)
    if (includeData) {
      if (playerCompanionEntity != null && playerCompanionEntity.isAlive()) {
        this.entityData = playerCompanionEntity.serializeNBT();
      }
      if (this.entityData != null) {
        compoundTag.put(ENTITY_DATA_TAG, this.entityData);
      }
    }

    // Sync specific meta data from entity directly, if available.
    if (playerCompanionEntity != null && playerCompanionEntity.isAlive()) {
      compoundTag.putBoolean(ENTITY_SITTING_TAG, playerCompanionEntity.isOrderedToSit());
      compoundTag.putBoolean(ENTITY_ORDERED_TO_POSITION,
          playerCompanionEntity.isOrderedToPosition());
      compoundTag.putFloat(ENTITY_HEALTH_MAX_TAG, playerCompanionEntity.getMaxHealth());
      compoundTag.putFloat(ENTITY_HEALTH_TAG, playerCompanionEntity.getHealth());

      if (playerCompanionEntity.getOwner() != null) {
        compoundTag.putString(OWNER_NAME_TAG,
            playerCompanionEntity.getOwner().getName().getString());
      }
      if (playerCompanionEntity.getTarget() == null) {
        compoundTag.putString(ENTITY_TARGET_TAG, "");
      } else {
        compoundTag.putString(ENTITY_TARGET_TAG, playerCompanionEntity.getTarget().getEncodeId());
      }

      // Handle hand items.
      setHandItems((NonNullList<ItemStack>) playerCompanionEntity.getHandSlots());
    }

    // Store hand
    PlayerCompanionDataHelper.saveHandItems(compoundTag, this.handItems);

    // Store inventory
    PlayerCompanionDataHelper.saveInventoryItems(compoundTag, this.inventoryItems);

    return compoundTag;
  }

  public boolean changed(PlayerCompanionEntity companion) {
    return companion.getUUID() != this.getUUID()
        || !companion.getCustomCompanionName().equals(this.getName())
        || companion.getOwnerUUID() != this.getOwnerUUID()
        || companion.getCompanionType() != this.getType() || companion.getId() != this.getEntityId()
        || companion.getHealth() != this.getEntityHealth()
        || companion.getRespawnTimer() != this.getEntityRespawnTimer()
        || companion.isOrderedToSit() != this.isOrderedToSit()
        || companion.isOrderedToPosition() != this.isOrderedToPosition()
        || companion.getHandSlots().hashCode() != this.getHandItems().hashCode()
        || ((companion.getTarget() == null && !this.getEntityTarget().isEmpty())
            || (companion.getTarget() != null
                && !companion.getTarget().getEncodeId().equals(this.getEntityTarget())));
  }

  public boolean changed(PlayerCompanionData playerCompanion) {
    return playerCompanion.getUUID() != this.getUUID()
        || !playerCompanion.getName().equals(this.getName())
        || playerCompanion.getOwnerUUID() != this.getOwnerUUID()
        || playerCompanion.getType() != this.getType()
        || playerCompanion.getEntityId() != this.getEntityId()
        || playerCompanion.getEntityHealth() != this.getEntityHealth()
        || playerCompanion.getEntityRespawnTimer() != this.getEntityRespawnTimer()
        || !playerCompanion.getEntityTarget().equals(this.getEntityTarget())
        || playerCompanion.isOrderedToSit() != this.isOrderedToSit()
        || playerCompanion.isOrderedToPosition() != this.isOrderedToPosition()
        || playerCompanion.getHandItems().hashCode() != this.getHandItems().hashCode();
  }

  public String toString() {
    return "PlayerCompanion['" + this.name + "', type=" + this.type + ", owner=" + this.ownerUUID
        + "(" + this.ownerName + "), entity=" + this.entityType + ", health=" + this.entityHealth
        + "/" + this.entityHealthMax + ", x=" + this.blockPos.getX() + ", y=" + this.blockPos.getY()
        + ", z=" + this.blockPos.getZ() + ", hand=" + this.getHandItems() + ", respawnTimer="
        + this.entityRespawnTimer + ", id=" + this.entityId + ", UUID=" + this.companionUUID + "]";
  }

  private void setDirty() {
    PlayerCompanionsServerData serverData = PlayerCompanionsServerData.get();
    if (serverData != null) {
      serverData.setDirty();
    }
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
