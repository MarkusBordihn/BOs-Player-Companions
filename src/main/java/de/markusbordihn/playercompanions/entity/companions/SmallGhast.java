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

package de.markusbordihn.playercompanions.entity.companions;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.config.CommonConfig;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.entity.PlayerCompanionVariant;
import de.markusbordihn.playercompanions.entity.ai.goal.FleeGoal;
import de.markusbordihn.playercompanions.entity.ai.goal.MoveToPositionGoal;
import de.markusbordihn.playercompanions.entity.ai.goal.ShootLargeFireballGoal;
import de.markusbordihn.playercompanions.entity.type.guard.GuardEntityFlying;
import de.markusbordihn.playercompanions.item.ModItems;

public class SmallGhast extends GuardEntityFlying implements NeutralMob {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final CommonConfig.Config COMMON = CommonConfig.COMMON;

  // General Information
  public static final String ID = "small_ghast";
  public static final String NAME = "Small Ghast";
  public static final Ingredient FOOD_ITEMS = Ingredient.of(Items.BONE);

  // Variants
  public static final List<PlayerCompanionVariant> VARIANTS =
      List.of(PlayerCompanionVariant.DEFAULT);

  // Companion Item by variant
  private static final Map<PlayerCompanionVariant, Item> COMPANION_ITEM_BY_VARIANT = Util.make(
      new EnumMap<>(PlayerCompanionVariant.class),
      hashMap -> hashMap.put(PlayerCompanionVariant.DEFAULT, ModItems.SMALL_GHAST_DEFAULT.get()));

  public SmallGhast(EntityType<? extends PlayerCompanionEntity> entityType, Level level) {
    super(entityType, level, COMPANION_ITEM_BY_VARIANT);
  }

  static class SmallGhastLookGoal extends Goal {
    private final SmallGhast smallGhast;

    public SmallGhastLookGoal(SmallGhast smallGhast) {
      this.smallGhast = smallGhast;
      this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    public boolean canUse() {
      return true;
    }

    @Override
    public void tick() {
      if (this.smallGhast.getTarget() == null) {
        Vec3 vec3 = this.smallGhast.getDeltaMovement();
        this.smallGhast.setYRot(-((float) Mth.atan2(vec3.x, vec3.z)) * (180F / (float) Math.PI));
        this.smallGhast.yBodyRot = this.smallGhast.getYRot();
      } else {
        LivingEntity livingEntity = this.smallGhast.getTarget();
        if (livingEntity != null && livingEntity.distanceToSqr(this.smallGhast) < 4096.0D) {
          double d1 = livingEntity.getX() - this.smallGhast.getX();
          double d2 = livingEntity.getZ() - this.smallGhast.getZ();
          this.smallGhast.setYRot(-((float) Mth.atan2(d1, d2)) * (180F / (float) Math.PI));
          this.smallGhast.yBodyRot = this.smallGhast.getYRot();
        }
      }
    }
  }

  public static AttributeSupplier.Builder createAttributes() {
    return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3F)
        .add(Attributes.FLYING_SPEED, 0.4F).add(Attributes.MAX_HEALTH, 10.0D)
        .add(Attributes.ATTACK_DAMAGE, 2.0D);
  }

  public static boolean checkGhastSpawnRules(EntityType<SmallGhast> entityType, LevelAccessor level,
      MobSpawnType mobSpawnType, BlockPos blockPos, RandomSource random) {
    return level.getDifficulty() != Difficulty.PEACEFUL
        && checkMobSpawnRules(entityType, level, mobSpawnType, blockPos, random);
  }

  public int getRemainingPersistentAngerTime() {
    return this.entityData.get(DATA_REMAINING_ANGER_TIME);
  }

  public void setRemainingPersistentAngerTime(int angerTime) {
    this.entityData.set(DATA_REMAINING_ANGER_TIME, angerTime);
  }

  public UUID getPersistentAngerTarget() {
    return this.persistentAngerTarget;
  }

  public void setPersistentAngerTarget(@Nullable UUID uuid) {
    this.persistentAngerTarget = uuid;
  }

  public void startPersistentAngerTimer() {
    this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();

    this.goalSelector.addGoal(1, new MoveToPositionGoal(this, 1.0D, 0.5F));
    this.goalSelector.addGoal(1, new FleeGoal(this, 1.0D));
    this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
    this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
    this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 8.0F, 2.0F, true));
    this.goalSelector.addGoal(7, new SmallGhast.SmallGhastLookGoal(this));
    this.goalSelector.addGoal(7, new ShootLargeFireballGoal(this));
    this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
    this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

    this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
    this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)).setAlertOthers());
    this.targetSelector.addGoal(4,
        new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
    this.targetSelector.addGoal(7,
        new NearestAttackableTargetGoal<>(this, AbstractSkeleton.class, false));
    this.targetSelector.addGoal(8, new ResetUniversalAngerTargetGoal<>(this, true));
  }

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.entityData.define(DATA_REMAINING_ANGER_TIME, 0);
  }

  @Override
  public void addAdditionalSaveData(CompoundTag compoundTag) {
    super.addAdditionalSaveData(compoundTag);
    this.addPersistentAngerSaveData(compoundTag);
  }

  @Override
  public void readAdditionalSaveData(CompoundTag compoundTag) {
    super.readAdditionalSaveData(compoundTag);
    this.readPersistentAngerSaveData(this.level, compoundTag);
  }

  @Override
  public int getExplosionPower() {
    return COMMON.smallGhastExplosionPower.get();
  }

  @Override
  public boolean doPlayJumpSound() {
    return false;
  }

  @Override
  public Item getTameItem() {
    return ModItems.TAME_BONE.get();
  }

  @Override
  public Ingredient getFoodItems() {
    return FOOD_ITEMS;
  }

  @Override
  public PlayerCompanionVariant getRandomVariant() {
    if (VARIANTS.size() > 1 && this.random.nextInt(2) == 0) {
      return VARIANTS.get(this.random.nextInt(VARIANTS.size()));
    }
    return PlayerCompanionVariant.DEFAULT;
  }

  @Override
  public SoundEvent getPetSound() {
    return SoundEvents.GHAST_AMBIENT;
  }


  @Override
  public SoundSource getSoundSource() {
    return SoundSource.HOSTILE;
  }

  @Override
  public SoundEvent getAmbientSound() {
    return SoundEvents.GHAST_AMBIENT;
  }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSource) {
    return SoundEvents.GHAST_HURT;
  }

  @Override
  protected SoundEvent getDeathSound() {
    return SoundEvents.GHAST_DEATH;
  }

  @Override
  public Vec3 getLeashOffset() {
    return new Vec3(0.0D, 0.72F * this.getEyeHeight(), this.getBbWidth() * 0.4F);
  }

  @Override
  public float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
    return 1.35F;
  }

  @Override
  public float getScale() {
    return 0.75F;
  }

  @Override
  public int getEntityGuiScaling() {
    return 50;
  }

  @Override
  public int getEntityGuiTop() {
    return 42;
  }

}
