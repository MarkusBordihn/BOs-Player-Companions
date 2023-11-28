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
import de.markusbordihn.playercompanions.entity.ActionType;
import de.markusbordihn.playercompanions.entity.AggressionLevel;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.entity.type.PlayerCompanionType;
import java.util.UUID;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerCompanionData {

  public static final String UUID_TAG = "UUID";
  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final String ACTIVE_TAG = "Active";
  private static final String ENTITY_ACTION_TYPE = "EntityActionType";
  private static final String ENTITY_AGGRESSION_LEVEL = "EntityAggressionLevel";
  private static final String ENTITY_DATA_TAG = "EntityData";
  private static final String ENTITY_DIMENSION = "EntityDimension";
  private static final String ENTITY_EXPERIENCE_LEVEL_TAG = "EntityExperienceLevel";
  private static final String ENTITY_EXPERIENCE_TAG = "EntityExperience";
  private static final String ENTITY_HEALTH_MAX_TAG = "EntityHealthMax";
  private static final String ENTITY_HEALTH_TAG = "EntityHealth";
  private static final String ENTITY_ID_TAG = "EntityId";
  private static final String ENTITY_ORDERED_TO_POSITION = "EntityOrderedToPosition";
  private static final String ENTITY_RESPAWN_TIMER_TAG = "EntityRespawnTimer";
  private static final String ENTITY_SITTING_ON_SHOULDER_TAG = "EntitySittingOnShoulder";
  private static final String ENTITY_SITTING_TAG = "EntitySitting";
  private static final String ENTITY_TARGET_TAG = "EntityTarget";
  private static final String ENTITY_TYPE_TAG = "EntityType";
  private static final String LEVEL_TAG = "Level";
  private static final String NAME_TAG = "Name";
  private static final String OWNER_NAME_TAG = "OwnerName";
  private static final String OWNER_TAG = "Owner";
  private static final String POSITION_TAG = "Position";
  private static final String REMOVED_TAG = "Removed";
  private static final String TYPE_TAG = "Type";
  private ActionType entityActionType = ActionType.UNKNOWN;
  private AggressionLevel entityAggressionLevel = AggressionLevel.UNKNOWN;
  private BlockPos blockPos;
  private ClientLevel clientLevel;
  private CompoundTag entityData;
  private EntityType<?> entityType;
  private NonNullList<ItemStack> armorItems = NonNullList.withSize(4, ItemStack.EMPTY);
  private NonNullList<ItemStack> handItems = NonNullList.withSize(2, ItemStack.EMPTY);
  private NonNullList<ItemStack> inventoryItems = NonNullList.withSize(16, ItemStack.EMPTY);
  private PlayerCompanionEntity companionEntity;
  private PlayerCompanionType type = PlayerCompanionType.UNKNOWN;
  private ResourceKey<Level> level;
  private ServerLevel serverLevel;
  private String entityDimension = "";
  private String entityTarget = "";
  private String levelName = "";
  private String name = "";
  private String ownerName = "";
  private UUID companionUUID = null;
  private UUID ownerUUID = null;
  private boolean active = true;
  private boolean entityOrderedToPosition = false;
  private boolean entitySitOnShoulder = false;
  private boolean entitySitting = false;
  private boolean hasOwner = false;
  private boolean isRemoved = false;
  private float entityHealth;
  private float entityHealthMax;
  private int entityExperience = 1;
  private int entityExperienceLevel = 1;
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

  public boolean isSittingOnShoulder() {
    return this.entitySitOnShoulder;
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

  public String getDimensionName() {
    return this.entityDimension;
  }

  public int getExperience() {
    return this.entityExperience;
  }

  public int getExperienceLevel() {
    return this.entityExperienceLevel;
  }

  public ActionType getEntityActionType() {
    return this.entityActionType;
  }

  public void setEntityActionType(ActionType actionType) {
    this.entityActionType = actionType;
  }

  public AggressionLevel getEntityAggressionLevel() {
    return this.entityAggressionLevel;
  }

  public void setEntityAggressionLevel(AggressionLevel aggressionLevel) {
    this.entityAggressionLevel = aggressionLevel;
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

  public void syncEntityData(Entity entity) {
    if (entity instanceof LivingEntity livingEntity) {
      syncEntityData(livingEntity);
    }
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
    return this.entityTarget != null && !this.entityTarget.isBlank();
  }

  public NonNullList<ItemStack> getArmorItems() {
    return this.armorItems;
  }

  public void setArmorItems(NonNullList<ItemStack> armor) {
    this.armorItems = armor;
    this.setDirty();
  }

  public void setArmorItem(int index, ItemStack itemStack) {
    this.armorItems.set(index, itemStack);
    this.setDirty();
  }

  public ItemStack getArmorItem(int index) {
    return this.armorItems.get(index);
  }

  public int getArmorItemsSize() {
    return this.armorItems.size();
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

    // 1. Try to add stack-able items to existing stacks in the inventory.
    // Note: Items will loose their NBT Tags because they are bound to the ItemStack.
    if (itemStack.getMaxStackSize() > 1) {
      Item item = itemStack.getItem();
      int numberOfItems = itemStack.getCount();

      for (int index = 0; index < getInventoryItemsSize(); index++) {
        ItemStack existingItems = getInventoryItem(index);
        if (!existingItems.isEmpty()
            && existingItems.is(item)
            && existingItems.getCount() + numberOfItems < existingItems.getMaxStackSize()) {
          existingItems.grow(numberOfItems);
          return true;
        }
      }
    }

    // 2. Try to store items into any empty place.
    // Note: We are storing the ItemStack directly to avoid loosing any NBT Tags.
    for (int index = 0; index < getInventoryItemsSize(); index++) {
      ItemStack existingItems = getInventoryItem(index);
      if (existingItems.isEmpty()) {
        setInventoryItem(index, itemStack);
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
    this.name =
        companion.hasCustomName()
            ? companion.getCustomName().getString()
            : companion.getRandomName();
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
    this.levelName = this.level.registry() + "/" + this.level.location();
    this.entityId = companion.getId();
    this.entityActionType = companion.getActionType();
    this.entityAggressionLevel = companion.getAggressionLevel();
    this.entityDimension = companion.getDimensionName();
    this.entityExperience = companion.getExperience();
    this.entityExperienceLevel = companion.getExperienceLevel();
    this.entityHealth = companion.getHealth();
    this.entityHealthMax = companion.getMaxHealth();
    this.entityType = companion.getType();
    this.entityRespawnTimer = companion.getRespawnTimer();
    this.entitySitting = companion.isOrderedToSit();
    this.entityOrderedToPosition = companion.isOrderedToPosition();
    this.entitySitOnShoulder = companion.isSitOnShoulder();
    this.entityData = companion.serializeNBT();

    // Entity Target
    LivingEntity target = companion.getTarget();
    this.entityTarget = target == null || target.getEncodeId() == null ? "" : target.getEncodeId();

    // Handle level references (client and server)
    Level companionLevel = companion.getLevel();
    if (companionLevel != null) {
      if (companionLevel.isClientSide) {
        this.clientLevel = (ClientLevel) companion.getLevel();
      } else {
        this.serverLevel = (ServerLevel) companion.getLevel();
      }
    }

    // Handle armor items.
    setArmorItems((NonNullList<ItemStack>) companion.getArmorSlots());

    // Handle hand items.
    setHandItems((NonNullList<ItemStack>) companion.getHandSlots());

    log.debug(
        "Loaded PlayerCompanion {} data over entity with {} and data {}",
        this.name,
        this,
        this.entityData);
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
    this.entityData = compoundTag.getCompound(ENTITY_DATA_TAG);
    this.entityDimension = compoundTag.getString(ENTITY_DIMENSION);
    this.entityExperience = compoundTag.getInt(ENTITY_EXPERIENCE_TAG);
    this.entityExperienceLevel = compoundTag.getInt(ENTITY_EXPERIENCE_LEVEL_TAG);
    this.entityHealth = compoundTag.getFloat(ENTITY_HEALTH_TAG);
    this.entityHealthMax = compoundTag.getFloat(ENTITY_HEALTH_MAX_TAG);
    this.entityOrderedToPosition = compoundTag.getBoolean(ENTITY_ORDERED_TO_POSITION);
    this.entityRespawnTimer = compoundTag.getInt(ENTITY_RESPAWN_TIMER_TAG);
    this.entitySitting = compoundTag.getBoolean(ENTITY_SITTING_TAG);
    this.entityTarget = compoundTag.getString(ENTITY_TARGET_TAG);
    this.entitySitOnShoulder = compoundTag.getBoolean(ENTITY_SITTING_ON_SHOULDER_TAG);

    // Action Type
    if (compoundTag.contains(ENTITY_ACTION_TYPE)) {
      this.entityActionType = ActionType.get(compoundTag.getString(ENTITY_ACTION_TYPE));
    }

    // Aggression Level
    if (compoundTag.contains(ENTITY_AGGRESSION_LEVEL)) {
      this.entityAggressionLevel =
          AggressionLevel.get(compoundTag.getString(ENTITY_AGGRESSION_LEVEL));
    }

    // Load Armor
    PlayerCompanionDataHelper.loadArmorItems(compoundTag, this.armorItems);

    // Load Hand
    PlayerCompanionDataHelper.loadHandItems(compoundTag, this.handItems);

    // Load inventory
    PlayerCompanionDataHelper.loadInventoryItems(compoundTag, this.inventoryItems);

    log.trace("Loaded PlayerCompanion {} data over compoundTag with {}", this.name, this);
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
    compoundTag.putBoolean(ACTIVE_TAG, this.active);
    compoundTag.putBoolean(REMOVED_TAG, this.isRemoved);
    compoundTag.put(POSITION_TAG, NbtUtils.writeBlockPos(this.blockPos));
    if (!this.levelName.isEmpty()) {
      compoundTag.putString(LEVEL_TAG, this.levelName);
    }
    compoundTag.putInt(ENTITY_ID_TAG, this.entityId);
    compoundTag.putString(ENTITY_TYPE_TAG, EntityType.getKey(this.entityType).toString());
    compoundTag.putInt(ENTITY_RESPAWN_TIMER_TAG, this.entityRespawnTimer);

    // Try to get current Player Companion if not exists.
    PlayerCompanionEntity playerCompanionEntity = this.getPlayerCompanionEntity();

    // Storing current companion entity data if available (regardless of disc status)
    if (playerCompanionEntity != null && playerCompanionEntity.isAlive()) {
      this.entityData = playerCompanionEntity.serializeNBT();
    }

    // Include only companion fully entity data, if requested.
    if (includeData && this.entityData != null) {
      compoundTag.put(ENTITY_DATA_TAG, this.entityData);
    }

    // Sync specific meta data from entity directly, if available.
    // If not use stored values instead.
    if (playerCompanionEntity != null && playerCompanionEntity.isAlive()) {
      compoundTag.putString(ENTITY_ACTION_TYPE, playerCompanionEntity.getActionType().name());
      compoundTag.putString(
          ENTITY_AGGRESSION_LEVEL, playerCompanionEntity.getAggressionLevel().name());
      compoundTag.putString(ENTITY_DIMENSION, playerCompanionEntity.getDimensionName());
      compoundTag.putBoolean(ENTITY_SITTING_TAG, playerCompanionEntity.isOrderedToSit());
      compoundTag.putBoolean(
          ENTITY_SITTING_ON_SHOULDER_TAG, playerCompanionEntity.isSitOnShoulder());
      compoundTag.putBoolean(
          ENTITY_ORDERED_TO_POSITION, playerCompanionEntity.isOrderedToPosition());
      compoundTag.putFloat(ENTITY_HEALTH_MAX_TAG, playerCompanionEntity.getMaxHealth());
      compoundTag.putFloat(ENTITY_HEALTH_TAG, playerCompanionEntity.getHealth());
      compoundTag.putInt(ENTITY_EXPERIENCE_LEVEL_TAG, playerCompanionEntity.getExperienceLevel());
      compoundTag.putInt(ENTITY_EXPERIENCE_TAG, playerCompanionEntity.getExperience());

      // Set Owner, if any.
      LivingEntity owner = playerCompanionEntity.getOwner();
      if (owner != null) {
        compoundTag.putUUID(OWNER_TAG, owner.getUUID());
        compoundTag.putString(OWNER_NAME_TAG, owner.getName().getString());
      }

      // Set target, if any
      LivingEntity target = playerCompanionEntity.getTarget();
      compoundTag.putString(
          ENTITY_TARGET_TAG,
          target == null || target.getEncodeId() == null ? "" : target.getEncodeId());

      // Get current armor items from entity to be in sync.
      setArmorItems((NonNullList<ItemStack>) playerCompanionEntity.getArmorSlots());

      // Get current hand items from entity to be in sync.
      setHandItems((NonNullList<ItemStack>) playerCompanionEntity.getHandSlots());
    } else {
      // Alternative: Use cached values instead.
      compoundTag.putString(ENTITY_ACTION_TYPE, this.entityActionType.name());
      compoundTag.putString(ENTITY_AGGRESSION_LEVEL, this.entityAggressionLevel.name());
      compoundTag.putString(ENTITY_DIMENSION, this.entityDimension);
      compoundTag.putBoolean(ENTITY_SITTING_TAG, this.entitySitting);
      compoundTag.putBoolean(ENTITY_SITTING_ON_SHOULDER_TAG, this.entitySitOnShoulder);
      compoundTag.putBoolean(ENTITY_ORDERED_TO_POSITION, this.entityOrderedToPosition);
      compoundTag.putFloat(ENTITY_HEALTH_MAX_TAG, this.entityHealthMax);
      compoundTag.putFloat(ENTITY_HEALTH_TAG, this.entityHealth);
      compoundTag.putInt(ENTITY_EXPERIENCE_LEVEL_TAG, this.entityExperienceLevel);
      compoundTag.putInt(ENTITY_EXPERIENCE_TAG, this.entityExperience);

      // Set Owner, if any.
      if (this.ownerUUID != null) {
        compoundTag.putUUID(OWNER_TAG, this.ownerUUID);
        compoundTag.putString(OWNER_NAME_TAG, this.ownerName);
      }

      // Set target, if any
      compoundTag.putString(ENTITY_TARGET_TAG, this.entityTarget == null ? "" : this.entityTarget);
    }

    // Store amor items.
    PlayerCompanionDataHelper.saveArmorItems(compoundTag, this.armorItems);

    // Store hand items.
    PlayerCompanionDataHelper.saveHandItems(compoundTag, this.handItems);

    // Store inventory items.
    PlayerCompanionDataHelper.saveInventoryItems(compoundTag, this.inventoryItems);

    return compoundTag;
  }

  public String toString() {
    return "PlayerCompanion['"
        + this.name
        + "', type="
        + this.type
        + ", owner="
        + this.ownerUUID
        + "("
        + this.ownerName
        + "), entity="
        + this.entityType
        + ", experience="
        + this.entityExperience
        + ", level="
        + this.entityExperienceLevel
        + ", health="
        + this.entityHealth
        + "/"
        + this.entityHealthMax
        + ", x="
        + this.blockPos.getX()
        + ", y="
        + this.blockPos.getY()
        + ", z="
        + this.blockPos.getZ()
        + ", armor="
        + this.getArmorItems()
        + ", hand="
        + this.getHandItems()
        + ", dimension="
        + this.entityDimension
        + ", action_type = "
        + this.entityActionType.name()
        + ", aggression_level= "
        + this.entityAggressionLevel.name()
        + ", respawnTimer="
        + this.entityRespawnTimer
        + ", id="
        + this.entityId
        + ", UUID="
        + this.companionUUID
        + "]";
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

    if (!(object instanceof PlayerCompanionData playerCompanion)) {
      return false;
    }

    return playerCompanion.getUUID().equals(this.companionUUID);
  }

  @Override
  public int hashCode() {
    return this.companionUUID.hashCode();
  }
}
