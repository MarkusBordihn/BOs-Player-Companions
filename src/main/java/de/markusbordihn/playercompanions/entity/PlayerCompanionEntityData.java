package de.markusbordihn.playercompanions.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
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

import de.markusbordihn.playercompanions.utils.Names;

public class PlayerCompanionEntityData extends TamableAnimal {

  // Entity Data and Tags
  private static final EntityDataAccessor<Integer> DATA_COLOR =
      SynchedEntityData.defineId(PlayerCompanionEntity.class, EntityDataSerializers.INT);
  private static final EntityDataAccessor<String> DATA_CUSTOM_COMPANION_NAME =
      SynchedEntityData.defineId(PlayerCompanionEntity.class, EntityDataSerializers.STRING);
  private static final EntityDataAccessor<Integer> DATA_EXPERIENCE_LEVEL =
      SynchedEntityData.defineId(PlayerCompanionEntity.class, EntityDataSerializers.INT);
  private static final EntityDataAccessor<Integer> DATA_EXPERIENCE =
      SynchedEntityData.defineId(PlayerCompanionEntity.class, EntityDataSerializers.INT);
  private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME =
      SynchedEntityData.defineId(PlayerCompanionEntity.class, EntityDataSerializers.INT);
  private static final EntityDataAccessor<Integer> DATA_RESPAWN_TIMER =
      SynchedEntityData.defineId(PlayerCompanionEntity.class, EntityDataSerializers.INT);
  private static final String DATA_COLOR_TAG = "Color";
  private static final String DATA_CUSTOM_COMPANION_NAME_TAG = "CompanionCustomName";
  private static final String DATA_EXPERIENCE_LEVEL_TAG = "CompanionExperienceLevel";
  private static final String DATA_EXPERIENCE_TAG = "CompanionExperience";
  private static final String DATA_REMAINING_ANGER_TIME_TAG = "CompanionAngerTimeRemaining";
  private static final String DATA_RESPAWN_TIMER_TAG = "CompanionRespawnTicker";

  // Temporary states
  private boolean isActive = true;
  private boolean isFlying = false;
  private boolean isKeepOnJumping = false;
  private boolean isTamable = true;
  private PlayerCompanionType companionType = PlayerCompanionType.UNKNOWN;
  private ItemStack companionTypeIcon = new ItemStack(Items.BONE);

  protected PlayerCompanionEntityData(EntityType<? extends TamableAnimal> entityType, Level level) {
    super(entityType, level);
  }

  public boolean isActive() {
    return this.isActive;
  }

  public boolean flying() {
    return this.isFlying;
  }

  public void flying(boolean flying) {
    this.isFlying = flying;
  }

  public boolean isTamable() {
    return this.isTamable;
  }

  protected boolean keepOnJumping() {
    return this.isKeepOnJumping;
  }

  protected void keepOnJumping(boolean keepOnJumping) {
    this.isKeepOnJumping = keepOnJumping;
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

  public DyeColor getColor() {
    return DyeColor.byId(this.entityData.get(DATA_COLOR));
  }

  public void setColor(DyeColor dyeColor) {
    this.entityData.set(DATA_COLOR, dyeColor.getId());
  }

  public String getCustomCompanionName() {
    return this.entityData.get(DATA_CUSTOM_COMPANION_NAME);
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

  public int getRespawnTimer() {
    return this.entityData.get(DATA_RESPAWN_TIMER);
  }

  public void setRespawnTimer(int timer) {
    if (timer > java.time.Instant.now().getEpochSecond()) {
      this.isActive = false;
    }
    this.entityData.set(DATA_RESPAWN_TIMER, timer);
  }

  public void stopRespawnTimer() {
    this.isActive = true;
    this.setRespawnTimer(0);
  }

  protected String getRandomName() {
    return Names.getRandomMobName();
  }

  protected int getJumpDelay() {
    return this.random.nextInt(20) + 10;
  }

  protected int getWaitDelay() {
    return this.random.nextInt(200) + 10;
  }

  protected int getJumpMoveDelay() {
    return 10;
  }

  protected float getSoundPitch() {
    return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 1.4F;
  }

  public boolean hasOwner() {
    return this.getOwnerUUID() != null;
  }


  public boolean isWeapon(ItemStack itemStack) {
    if (itemStack.isEmpty()) {
      return false;
    }
    Item item = itemStack.getItem();
    return item instanceof SwordItem || item instanceof CrossbowItem || item instanceof TridentItem
        || item instanceof BowItem || item instanceof AxeItem;
  }


  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.entityData.define(DATA_COLOR, DyeColor.GREEN.getId());
    this.entityData.define(DATA_CUSTOM_COMPANION_NAME, getRandomName());
    this.entityData.define(DATA_EXPERIENCE, 0);
    this.entityData.define(DATA_EXPERIENCE_LEVEL, 0);
    this.entityData.define(DATA_REMAINING_ANGER_TIME, 0);
    this.entityData.define(DATA_RESPAWN_TIMER, 0);
  }

  @Override
  public void addAdditionalSaveData(CompoundTag compoundTag) {
    super.addAdditionalSaveData(compoundTag);
    compoundTag.putByte(DATA_COLOR_TAG, (byte) this.getColor().getId());
    compoundTag.putString(DATA_CUSTOM_COMPANION_NAME_TAG, this.getCustomCompanionName());
    compoundTag.putInt(DATA_EXPERIENCE_TAG, this.getExperience());
    compoundTag.putInt(DATA_EXPERIENCE_LEVEL_TAG, this.getExperienceLevel());
    compoundTag.putInt(DATA_RESPAWN_TIMER_TAG, this.getRespawnTimer());
  }

  @Override
  public void readAdditionalSaveData(CompoundTag compoundTag) {
    super.readAdditionalSaveData(compoundTag);
    if (compoundTag.contains(DATA_COLOR_TAG, 99)) {
      this.setColor(DyeColor.byId(compoundTag.getInt(DATA_COLOR_TAG)));
    }
    if (compoundTag.contains(DATA_CUSTOM_COMPANION_NAME_TAG)) {
      this.setCustomCompanionName(compoundTag.getString(DATA_CUSTOM_COMPANION_NAME_TAG));
    }
    this.setExperience(compoundTag.getInt(DATA_EXPERIENCE_TAG));
    this.setExperienceLevel(compoundTag.getInt(DATA_EXPERIENCE_LEVEL_TAG));

    // Handle respawn ticker
    if (compoundTag.contains(DATA_RESPAWN_TIMER_TAG)) {
      this.setRespawnTimer(compoundTag.getInt(DATA_RESPAWN_TIMER_TAG));
    }
  }

  @Override
  public PlayerCompanionEntity getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
    return null;
  }
}
