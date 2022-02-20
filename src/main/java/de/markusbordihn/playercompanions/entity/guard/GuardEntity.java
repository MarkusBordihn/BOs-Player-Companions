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

package de.markusbordihn.playercompanions.entity.guard;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.entity.PlayerCompanionType;

public class GuardEntity extends PlayerCompanionEntity implements NeutralMob {

  private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING =
      SynchedEntityData.defineId(GuardEntity.class, EntityDataSerializers.BOOLEAN);

  public static final MobCategory CATEGORY = MobCategory.CREATURE;

  public GuardEntity(EntityType<? extends GuardEntity> entityType, Level level) {
    super(entityType, level);
    this.setTame(false);
    this.setCompanionType(PlayerCompanionType.GUARD);
    this.setCompanionTypeIcon(new ItemStack(Items.NETHERITE_SWORD));
  }

  public boolean isCharging() {
    return this.entityData.get(DATA_IS_CHARGING);
  }

  public void setCharging(boolean charging) {
    this.entityData.set(DATA_IS_CHARGING, charging);
  }

  class GuardAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
    private final GuardEntity guard;

    public GuardAvoidEntityGoal(GuardEntity entity, Class<T> p_30455_, float p_30456_,
        double p_30457_, double p_30458_) {
      super(entity, p_30455_, p_30456_, p_30457_, p_30458_);
      this.guard = entity;
    }

    @Override
    public boolean canUse() {
      if (super.canUse() && this.toAvoid instanceof Llama lama) {
        return !this.guard.isTame() && this.avoidLlama(lama);
      } else {
        return false;
      }
    }

    private boolean avoidLlama(Llama lama) {
      return lama.getStrength() >= GuardEntity.this.random.nextInt(5);
    }

    @Override
    public void start() {
      GuardEntity.this.setTarget((LivingEntity) null);
      super.start();
    }

    @Override
    public void tick() {
      GuardEntity.this.setTarget((LivingEntity) null);
      super.tick();
    }
  }

  public static AttributeSupplier.Builder createAttributes() {
    return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3F)
        .add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.ATTACK_DAMAGE, 2.0D);
  }

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.entityData.define(DATA_IS_CHARGING, false);
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(3,
        new GuardEntity.GuardAvoidEntityGoal<>(this, Llama.class, 24.0F, 1.5D, 1.5D));
    this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
    this.goalSelector.addGoal(5, new MeleeAttackGoal(this, this.flying() ? 0.25D : 1.0D, true));
    this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
    this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
    this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)).setAlertOthers());
    this.targetSelector.addGoal(4,
        new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
    this.targetSelector.addGoal(7,
        new NearestAttackableTargetGoal<>(this, AbstractSkeleton.class, false));
    this.targetSelector.addGoal(8, new ResetUniversalAngerTargetGoal<>(this, true));
  }
}
