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

package de.markusbordihn.playercompanions.entity;

import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;

import de.markusbordihn.playercompanions.data.PlayerCompanionData;
import de.markusbordihn.playercompanions.data.PlayerCompanionsServerData;
import de.markusbordihn.playercompanions.entity.type.PlayerCompanionType;
import de.markusbordihn.playercompanions.utils.Names;

public class PlayerCompanionEntityData extends TamableAnimal {

  // Synced Entity Data
  private static final EntityDataAccessor<Boolean> DATA_ACTIVE =
      SynchedEntityData.defineId(PlayerCompanionEntity.class, EntityDataSerializers.BOOLEAN);
  private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING =
      SynchedEntityData.defineId(PlayerCompanionEntity.class, EntityDataSerializers.BOOLEAN);
  private static final EntityDataAccessor<Integer> DATA_COLOR =
      SynchedEntityData.defineId(PlayerCompanionEntity.class, EntityDataSerializers.INT);
  private static final EntityDataAccessor<Integer> DATA_EXPERIENCE =
      SynchedEntityData.defineId(PlayerCompanionEntity.class, EntityDataSerializers.INT);
  private static final EntityDataAccessor<Integer> DATA_EXPERIENCE_LEVEL =
      SynchedEntityData.defineId(PlayerCompanionEntity.class, EntityDataSerializers.INT);
  private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME =
      SynchedEntityData.defineId(PlayerCompanionEntity.class, EntityDataSerializers.INT);
  private static final EntityDataAccessor<Integer> DATA_RESPAWN_TIMER =
      SynchedEntityData.defineId(PlayerCompanionEntity.class, EntityDataSerializers.INT);
  private static final EntityDataAccessor<String> DATA_CUSTOM_COMPANION_NAME =
      SynchedEntityData.defineId(PlayerCompanionEntity.class, EntityDataSerializers.STRING);
  private static final EntityDataAccessor<String> DATA_VARIANT =
      SynchedEntityData.defineId(PlayerCompanionEntity.class, EntityDataSerializers.STRING);

  // Stored Entity Data Tags
  private static final String DATA_ACTIVE_TAG = "Active";
  private static final String DATA_COLOR_TAG = "Color";
  private static final String DATA_CUSTOM_COMPANION_NAME_TAG = "CompanionCustomName";
  private static final String DATA_EXPERIENCE_LEVEL_TAG = "CompanionExperienceLevel";
  private static final String DATA_EXPERIENCE_TAG = "CompanionExperience";
  private static final String DATA_RESPAWN_TIMER_TAG = "CompanionRespawnTicker";
  private static final String DATA_VARIANT_TAG = "Variant";

  // Default values
  private int explosionPower = 0;
  private int jumpMoveDelay = 10;

  // Temporary states
  private BlockPos orderedToPosition = null;
  private ItemStack companionTypeIcon = new ItemStack(Items.BONE);
  private PlayerCompanionType companionType = PlayerCompanionType.UNKNOWN;
  private UUID persistentAngerTarget;
  private boolean isDirty = false;

  private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);

  protected PlayerCompanionEntityData(EntityType<? extends TamableAnimal> entityType, Level level) {
    super(entityType, level);
  }

  public boolean isTamable() {
    return !isTame();
  }

  public Player getNearestPlayer(TargetingConditions targetingConditions) {
    return this.level.getNearestPlayer(targetingConditions, this);
  }

  public PlayerCompanionType getCompanionType() {
    return this.companionType;
  }

  public void setCompanionType(PlayerCompanionType type) {
    this.companionType = type;
  }

  public ItemStack getCompanionTypeIcon() {
    return this.companionTypeIcon;
  }

  public void setCompanionTypeIcon(ItemStack icon) {
    this.companionTypeIcon = icon;
  }

  public boolean isCharging() {
    return this.entityData.get(DATA_IS_CHARGING);
  }

  public void setCharging(boolean charging) {
    this.entityData.set(DATA_IS_CHARGING, charging);
  }

  public DyeColor getColor() {
    return DyeColor.byId(this.entityData.get(DATA_COLOR));
  }

  public void setColor(DyeColor dyeColor) {
    this.entityData.set(DATA_COLOR, dyeColor.getId());
  }

  public boolean isActive() {
    return this.entityData.get(DATA_ACTIVE);
  }

  public void setActive(boolean active) {
    this.entityData.set(DATA_ACTIVE, active);
  }

  public void setDirty() {
    this.isDirty = true;
  }

  public void setDirty(boolean dirty) {
    if (!this.level.isClientSide) {
      this.isDirty = dirty;
    }
  }

  public boolean getDirty() {
    return this.isDirty;
  }

  public int getExplosionPower() {
    return this.explosionPower;
  }

  public void setExplosionPower(int explosionPower) {
    this.explosionPower = explosionPower;
  }

  public String getVariant() {
    return this.entityData.get(DATA_VARIANT);
  }

  public void setVariant(String variant) {
    this.entityData.set(DATA_VARIANT, variant);
  }

  public String getCustomCompanionName() {
    return this.entityData.get(DATA_CUSTOM_COMPANION_NAME);
  }

  public TextComponent getCustomCompanionNameComponent() {
    return new TextComponent(getCustomCompanionName());
  }

  public void setCustomCompanionName(String name) {
    this.entityData.set(DATA_CUSTOM_COMPANION_NAME, name);
  }

  public Integer getExperience() {
    return this.entityData.get(DATA_EXPERIENCE);
  }

  public void setExperience(Integer experience) {
    this.entityData.set(DATA_EXPERIENCE, experience);
  }

  public Integer getExperienceLevel() {
    return this.entityData.get(DATA_EXPERIENCE_LEVEL);
  }

  public void setExperienceLevel(Integer experience) {
    this.entityData.set(DATA_EXPERIENCE_LEVEL, experience);
  }

  public int getRemainingPersistentAngerTime() {
    return this.entityData.get(DATA_REMAINING_ANGER_TIME);
  }

  public void setRemainingPersistentAngerTime(int angerTime) {
    this.entityData.set(DATA_REMAINING_ANGER_TIME, angerTime);
  }

  public void startPersistentAngerTimer() {
    this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
  }

  @Nullable
  public UUID getPersistentAngerTarget() {
    return this.persistentAngerTarget;
  }

  public void setPersistentAngerTarget(@Nullable UUID uuid) {
    this.persistentAngerTarget = uuid;
  }

  public int getRespawnTimer() {
    return this.entityData.get(DATA_RESPAWN_TIMER);
  }

  public void setRespawnTimer(int timer) {
    if (timer > java.time.Instant.now().getEpochSecond()) {
      this.setActive(false);
    }
    this.entityData.set(DATA_RESPAWN_TIMER, timer);
  }

  public void stopRespawnTimer() {
    this.setActive(true);
    this.setRespawnTimer(0);
  }

  protected String getRandomName() {
    return Names.getRandomMobName();
  }

  public int getJumpDelay() {
    return this.random.nextInt(20) + 10;
  }

  public int getWaitDelay() {
    return this.random.nextInt(200) + 10;
  }

  public int getJumpMoveDelay() {
    return this.jumpMoveDelay;
  }

  public float getSoundPitch() {
    return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 1.4F;
  }

  public void setWantedPosition(double x, double y, double z, double speed) {
    this.moveControl.setWantedPosition(x, y, z, speed);
  }

  public boolean hasOwner() {
    return this.getOwnerUUID() != null;
  }

  public boolean isOrderedToPosition() {
    return this.orderedToPosition != null;
  }

  public void setOrderedToPosition(BlockPos blockPos) {
    this.orderedToPosition = blockPos;
    this.setDirty();
  }

  public BlockPos getOrderedToPosition() {
    return this.orderedToPosition;
  }

  public BlockPos ownerBlockPosition() {
    if (this.hasOwner()) {
      LivingEntity owner = this.getOwner();
      if (owner != null && owner.isAlive() && owner.isAddedToWorld()) {
        return owner.blockPosition();
      }
    }
    return this.blockPosition();
  }

  public boolean isWeapon(ItemStack itemStack) {
    if (itemStack.isEmpty()) {
      return false;
    }
    Item item = itemStack.getItem();
    return item instanceof SwordItem || item instanceof CrossbowItem || item instanceof TridentItem
        || item instanceof BowItem || item instanceof AxeItem;
  }

  public PlayerCompanionsServerData getServerData() {
    if (this.level.isClientSide) {
      return null;
    }
    return PlayerCompanionsServerData.get();
  }

  public PlayerCompanionData getData() {
    PlayerCompanionsServerData serverData = getServerData();
    if (serverData == null) {
      return null;
    }
    return PlayerCompanionsServerData.get().getCompanion(getUUID());
  }

  public void syncData(PlayerCompanionEntity playerCompanionEntity) {
    if (!this.level.isClientSide) {
      getServerData().updateOrRegisterCompanion(playerCompanionEntity);
    }
  }

  public void setHandItem(int slot, ItemStack itemStack) {
    if (slot == 0) {
      setItemSlot(EquipmentSlot.MAINHAND, itemStack);
    } else if (slot == 1) {
      setItemSlot(EquipmentSlot.OFFHAND, itemStack);
    }
  }

  @Override
  public void setItemSlot(EquipmentSlot equipmentSlot, ItemStack itemStack) {
    super.setItemSlot(equipmentSlot, itemStack);
    PlayerCompanionData data = this.getData();
    if (data != null) {
      switch (equipmentSlot.getType()) {
        case HAND:
          data.setHandItem(equipmentSlot.getIndex(), itemStack);
          break;
        case ARMOR:
          // data.armorItems.set(equipmentSlot.getIndex(), itemStack);
          break;
      }
    }
  }

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.entityData.define(DATA_ACTIVE, true);
    this.entityData.define(DATA_COLOR, DyeColor.GREEN.getId());
    this.entityData.define(DATA_CUSTOM_COMPANION_NAME, getRandomName());
    this.entityData.define(DATA_EXPERIENCE, 0);
    this.entityData.define(DATA_EXPERIENCE_LEVEL, 0);
    this.entityData.define(DATA_IS_CHARGING, false);
    this.entityData.define(DATA_REMAINING_ANGER_TIME, 0);
    this.entityData.define(DATA_RESPAWN_TIMER, 0);
    this.entityData.define(DATA_VARIANT, "default");
  }

  @Override
  public void addAdditionalSaveData(CompoundTag compoundTag) {
    super.addAdditionalSaveData(compoundTag);
    compoundTag.putBoolean(DATA_ACTIVE_TAG, this.isActive());
    compoundTag.putByte(DATA_COLOR_TAG, (byte) this.getColor().getId());
    compoundTag.putInt(DATA_EXPERIENCE_LEVEL_TAG, this.getExperienceLevel());
    compoundTag.putInt(DATA_EXPERIENCE_TAG, this.getExperience());
    compoundTag.putInt(DATA_RESPAWN_TIMER_TAG, this.getRespawnTimer());
    compoundTag.putString(DATA_CUSTOM_COMPANION_NAME_TAG, this.getCustomCompanionName());
    compoundTag.putString(DATA_VARIANT_TAG, this.getVariant());
  }

  @Override
  public void readAdditionalSaveData(CompoundTag compoundTag) {
    super.readAdditionalSaveData(compoundTag);
    this.setActive(compoundTag.getBoolean(DATA_ACTIVE_TAG));
    if (compoundTag.contains(DATA_COLOR_TAG, 99)) {
      this.setColor(DyeColor.byId(compoundTag.getInt(DATA_COLOR_TAG)));
    }

    this.setExperience(compoundTag.getInt(DATA_EXPERIENCE_TAG));
    this.setExperienceLevel(compoundTag.getInt(DATA_EXPERIENCE_LEVEL_TAG));

    // Handle respawn ticker
    if (compoundTag.contains(DATA_RESPAWN_TIMER_TAG)) {
      this.setRespawnTimer(compoundTag.getInt(DATA_RESPAWN_TIMER_TAG));
    }

    if (compoundTag.contains(DATA_CUSTOM_COMPANION_NAME_TAG)) {
      this.setCustomCompanionName(compoundTag.getString(DATA_CUSTOM_COMPANION_NAME_TAG));
    }

    this.setVariant(compoundTag.getString(DATA_VARIANT_TAG));

  }

  @Override
  public PlayerCompanionEntity getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
    return null;
  }
}
