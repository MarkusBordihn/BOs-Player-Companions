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

package de.markusbordihn.playercompanions.entity;

import java.util.Locale;
import java.util.UUID;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.config.CommonConfig;
import de.markusbordihn.playercompanions.data.PlayerCompanionsServerData;
import de.markusbordihn.playercompanions.utils.Names;

@EventBusSubscriber
public class CompanionEntity extends TamableAnimal implements TameablePlayerCompanion {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  public static final CommonConfig.Config COMMON = CommonConfig.COMMON;

  // Entity Data and Tags
  private static final EntityDataAccessor<Integer> DATA_COLOR =
      SynchedEntityData.defineId(CompanionEntity.class, EntityDataSerializers.INT);
  private static final EntityDataAccessor<String> DATA_CUSTOM_COMPANION_NAME =
      SynchedEntityData.defineId(CompanionEntity.class, EntityDataSerializers.STRING);
  private static final EntityDataAccessor<Integer> DATA_EXPERIENCE_LEVEL =
      SynchedEntityData.defineId(CompanionEntity.class, EntityDataSerializers.INT);
  private static final EntityDataAccessor<Integer> DATA_EXPERIENCE =
      SynchedEntityData.defineId(CompanionEntity.class, EntityDataSerializers.INT);
  private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME =
      SynchedEntityData.defineId(CompanionEntity.class, EntityDataSerializers.INT);
  private static final EntityDataAccessor<Integer> DATA_RESPAWN_TIMER =
      SynchedEntityData.defineId(CompanionEntity.class, EntityDataSerializers.INT);
  private static final String DATA_COLOR_TAG = "Color";
  private static final String DATA_CUSTOM_COMPANION_NAME_TAG = "CompanionCustomName";
  private static final String DATA_EXPERIENCE_LEVEL_TAG = "CompanionExperienceLevel";
  private static final String DATA_EXPERIENCE_TAG = "CompanionExperience";
  private static final String DATA_REMAINING_ANGER_TIME_TAG = "CompanionAngerTimeRemaining";
  private static final String DATA_RESPAWN_TIMER_TAG = "CompanionRespawnTicker";

  // Custom name format
  private static final ResourceLocation CUSTOM_NAME =
      new ResourceLocation(Constants.MOD_ID, "monster");
  private static final ResourceLocation CUSTOM_NAME_TAMED =
      new ResourceLocation(Constants.MOD_ID, "monster_tamed");
  private static final ResourceLocation RESPAWN_MESSAGE =
      new ResourceLocation(Constants.MOD_ID, "monster_respawn_message");
  private static final ResourceLocation WILL_RESPAWN_MESSAGE =
      new ResourceLocation(Constants.MOD_ID, "monster_will_respawn_message");

  // Config settings
  private static boolean respawnOnDeath = COMMON.respawnOnDeath.get();
  private static int respawnDelay = COMMON.respawnDelay.get();

  // Temporary states
  private boolean isActive = true;
  private boolean isFlying = false;
  private boolean isKeepOnJumping = false;
  private boolean isTamable = true;
  private boolean wasOnGround;
  private CompanionType companionType = CompanionType.UNKNOWN;

  // Anger state
  private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
  @Nullable
  private UUID persistentAngerTarget;

  public CompanionEntity(EntityType<? extends CompanionEntity> entityType, Level level) {
    super(entityType, level);
    this.moveControl = new CompanionEntityMoveControl(this);
    this.setTame(false);
  }

  @SubscribeEvent
  public static void handleServerAboutToStartEvent(ServerAboutToStartEvent event) {
    respawnOnDeath = COMMON.respawnOnDeath.get();
    respawnDelay = COMMON.respawnDelay.get();
    if (respawnOnDeath) {
      log.info("{} will be respawn on death with a {} secs delay.", Constants.LOG_ICON_NAME,
          COMMON.respawnDelay.get(), respawnDelay);
    } else {
      log.warn("{} will NOT respawn on death!", Constants.LOG_ICON_NAME);
    }
  }

  public static AttributeSupplier.Builder createAttributes() {
    return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3F)
        .add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.ATTACK_DAMAGE, 2.0D);
  }

  protected SoundEvent getJumpSound() {
    return SoundEvents.SLIME_JUMP_SMALL;
  }

  protected SoundEvent getWaitSound() {
    return getAmbientSound();
  }

  protected Item getTameItem() {
    return null;
  }

  protected Ingredient getFoodItems() {
    return Ingredient.of(getTameItem());
  }

  protected boolean doPlayJumpSound() {
    return true;
  }

  protected boolean keepOnJumping() {
    return this.isKeepOnJumping;
  }

  protected void keepOnJumping(boolean keepOnJumping) {
    this.isKeepOnJumping = keepOnJumping;
  }

  public CompanionType getCompanionType() {
    return this.companionType;
  }

  public Item getCompanionItem() {
    return null;
  }

  public void setCompanionType(CompanionType type) {
    this.companionType = type;
  }

  protected boolean flying() {
    return this.isFlying;
  }

  protected void flying(boolean flying) {
    this.isFlying = flying;
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

  protected void tameAndFollow(Player player) {
    this.tame(player);
    this.navigation.recomputePath();
    this.setOrderedToSit(false);

    // Set and/or update custom name with owner name.
    if (getCustomCompanionName().isBlank()) {
      if (hasCustomName()) {
        setCustomCompanionName(getCustomName().getString());
      } else {
        setCustomCompanionName(getRandomName());
      }
    }
    this.setCustomName(
        new TranslatableComponent(Util.makeDescriptionId("entity", CUSTOM_NAME_TAMED),
            new TextComponent(getCustomCompanionName()), player.getName()));
    if (player instanceof ServerPlayer) {
      PlayerCompanionsServerData.get().updateOrRegisterCompanion(this);
    }
  }

  protected ParticleOptions getParticleType() {
    return null;
  }

  protected String getRandomName() {
    return Names.getRandomMobName();
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
      isActive = false;
    }
    this.entityData.set(DATA_RESPAWN_TIMER, timer);
    PlayerCompanionsServerData.get().updatePlayerCompanion(this);
  }

  public void stopRespawnTimer() {
    isActive = true;
    this.setRespawnTimer(0);
    LivingEntity owner = this.getOwner();
    if (owner != null) {
      owner.sendMessage(new TranslatableComponent(Util.makeDescriptionId("entity", RESPAWN_MESSAGE),
          this.getCustomCompanionName()), Util.NIL_UUID);
    }
    PlayerCompanionsServerData.get().updatePlayerCompanion(this);
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
  public boolean canTamePlayerCompanion(ItemStack itemStack, Player player,
      LivingEntity livingEntity, InteractionHand hand) {
    return isTamable && !this.isTame() && getTameItem() != null && itemStack.is(getTameItem())
        && player.getInventory().canPlaceItem(1, itemStack);
  }

  @Override
  public InteractionResult tamePlayerCompanion(ItemStack itemStack, Player player,
      LivingEntity livingEntity, InteractionHand hand) {
    if (!player.getAbilities().instabuild) {
      itemStack.shrink(1);
    }
    if (this.random.nextInt(4) == 0
        && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player)) {
      this.tameAndFollow(player);
      this.level.broadcastEntityEvent(this, (byte) 7);
      return InteractionResult.SUCCESS;
    } else {
      this.level.broadcastEntityEvent(this, (byte) 6);
      return InteractionResult.CONSUME;
    }
  }

  @Override
  public void tame(Player player) {
    super.tame(player);
    if (player instanceof ServerPlayer) {
      PlayerCompanionsServerData.get().updateOrRegisterCompanion(this);
    }
  }

  @Override
  public boolean causeFallDamage(float start, float end, DamageSource damageSource) {
    if (this.isFlying) {
      return false;
    }
    return super.causeFallDamage(start, end, damageSource);
  }

  @Override
  protected void checkFallDamage(double height, boolean flag, BlockState blockState,
      BlockPos blockPos) {
    if (this.isFlying) {
      return;
    }
    super.checkFallDamage(height, flag, blockState, blockPos);
  }

  @Override
  public boolean onClimbable() {
    if (this.isFlying) {
      return false;
    }
    return super.onClimbable();
  }

  @Override
  protected float getSoundVolume() {
    return 1.0F;
  }

  @Override
  protected void registerGoals() {
    this.goalSelector.addGoal(1, new FloatGoal(this));
    this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
    this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, getFoodItems(), false));
    this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 5.0F, 1.0F, true));
    this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
    this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
  }

  @Override
  public CompanionEntity getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
    return null;
  }

  @Override
  public InteractionResult mobInteract(Player player, InteractionHand hand) {
    ItemStack itemStack = player.getItemInHand(hand);
    boolean isOwner = this.isTame() && this.isOwnedBy(player);

    // Early return for client side events.
    if (this.level.isClientSide) {
      boolean isOwnedOrTamed =
          isOwner || getTameItem() != null && itemStack.is(getTameItem()) && !this.isTame();
      return isOwnedOrTamed ? InteractionResult.CONSUME : InteractionResult.PASS;
    }

    // Health companion with food item, from any player.
    Item item = itemStack.getItem();
    if (this.isTame() && this.isFood(itemStack) && this.getHealth() < this.getMaxHealth()) {
      if (!player.getAbilities().instabuild) {
        itemStack.shrink(1);
      }
      this.heal(item.getFoodProperties() != null ? item.getFoodProperties().getNutrition() : 0.5F);
      SoundEvent eatingSound = this.getEatingSound(itemStack);
      if (eatingSound != null) {
        playSound(eatingSound, 1F, 1F);
      }
      this.gameEvent(GameEvent.MOB_INTERACT, this.eyeBlockPosition());
      return InteractionResult.SUCCESS;
    }

    // Order to sit is hand is empty or has an weapon in hand (during compat)
    if (isOwner && (itemStack.isEmpty() || isWeapon(itemStack))) {
      if (this.isOrderedToSit()) {
        this.setOrderedToSit(false);
        this.navigation.recomputePath();
      } else {
        this.setOrderedToSit(true);
        this.navigation.stop();
        this.setTarget((LivingEntity) null);
      }
      return InteractionResult.SUCCESS;
    }

    return super.mobInteract(player, hand);
  }

  @Override
  public boolean isFood(ItemStack itemStack) {
    if (getTameItem() != null) {
      return getFoodItems().test(itemStack);
    }
    return super.isFood(itemStack);
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

    // Register or update Companion
    PlayerCompanionsServerData.get().updateOrRegisterCompanion(this);
  }

  @Override
  @Nullable
  public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor,
      DifficultyInstance difficulty, MobSpawnType mobSpawnType,
      @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
    spawnGroupData = super.finalizeSpawn(serverLevelAccessor, difficulty, mobSpawnType,
        spawnGroupData, compoundTag);

    // Set random custom companion name
    setCustomName(new TranslatableComponent(Util.makeDescriptionId("entity", CUSTOM_NAME),
        new TextComponent(getCustomCompanionName())));

    return spawnGroupData;
  }

  @Override
  public void tick() {
    // Perform tick for AI and other important steps.
    super.tick();

    // Allow do disable entity to save performance and to allow basic respawn logic.
    if (!isActive) {
      return;
    }

    // Shows particle and play sound after jump or fall.
    if (this.onGround && !this.wasOnGround) {
      if (this.getParticleType() != null) {
        for (int j = 0; j < 4; ++j) {
          float f = this.random.nextFloat() * ((float) Math.PI * 2F);
          float f1 = this.random.nextFloat() * 0.5F + 0.5F;
          float f2 = Mth.sin(f) * 0.5F * f1;
          float f3 = Mth.cos(f) * 0.5F * f1;
          this.level.addParticle(this.getParticleType(), this.getX() + f2, this.getY(),
              this.getZ() + f3, 0.0D, 0.0D, 0.0D);
        }
      }
      if (doPlayJumpSound() && getJumpSound() != null) {
        this.playSound(getJumpSound(), this.getSoundVolume(), this.getSoundPitch());
      }
    }
    this.wasOnGround = this.onGround;
  }

  @Override
  public void die(DamageSource damageSource) {
    LivingEntity owner = getOwner();

    // Remove fire, effects and items before dying but before we are storing the data.
    clearFire();
    dropLeash(true, true);
    removeAllEffects();

    if (isTame() || owner != null) {
      if (respawnDelay > 1) {
        setRespawnTimer((int) java.time.Instant.now().getEpochSecond() + respawnDelay);
      }
      if (respawnOnDeath) {
        owner.sendMessage(
            new TranslatableComponent(Util.makeDescriptionId("entity", WILL_RESPAWN_MESSAGE),
                getCustomCompanionName(), respawnDelay),
            Util.NIL_UUID);
      }
    }
    super.die(damageSource);
  }

  @Override
  public String toString() {
    RemovalReason removalReason = this.getRemovalReason();
    String level = this.level == null ? "~NULL~" : this.level.toString();
    String owner = this.getOwnerUUID() == null ? "~NULL~" : this.getOwnerUUID().toString();
    Boolean tamed = this.isTame();
    int id = this.getId();

    return removalReason != null
        ? String.format(Locale.ROOT,
            "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f, tamed=%s, owner='%s', removed=%s]",
            this.getClass().getSimpleName(), this.getName().getString(), id, level, this.getX(),
            this.getY(), this.getZ(), tamed, owner, removalReason)
        : String.format(Locale.ROOT,
            "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f, tamed=%s, owner='%s']",
            this.getClass().getSimpleName(), this.getName().getString(), id, level, this.getX(),
            this.getY(), this.getZ(), tamed, owner);
  }

}
