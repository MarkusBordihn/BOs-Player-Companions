/*
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

package de.markusbordihn.playercompanions.entity.ai.goal;

import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import java.util.EnumSet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;

public class MeleeAttackGoal extends PlayerCompanionGoal {

  private static final long COOLDOWN_BETWEEN_CAN_USE_CHECKS = 20L;
  private final double speedModifier;
  private final boolean followingTargetEvenIfNotSeen;
  private Path path;
  private double pathTargetX;
  private double pathTargetY;
  private double pathTargetZ;
  private int ticksUntilNextPathRecalculation;
  private int ticksUntilNextAttack;
  private long lastCanUseCheck;
  private int failedPathFindingPenalty = 0;
  private final boolean canPenalize = false;

  public MeleeAttackGoal(PlayerCompanionEntity playerCompanionEntity, double speedModifier,
      boolean followingTargetEvenIfNotSeen) {
    super(playerCompanionEntity);
    this.speedModifier = speedModifier;
    this.followingTargetEvenIfNotSeen = followingTargetEvenIfNotSeen;
    this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
  }

  @Override
  public boolean canUse() {
    long gameTime = this.playerCompanionEntity.level().getGameTime();
    if (gameTime - this.lastCanUseCheck < COOLDOWN_BETWEEN_CAN_USE_CHECKS
        || !this.playerCompanionEntity.shouldAttack()) {
      return false;
    } else {
      this.lastCanUseCheck = gameTime;
      if (!this.playerCompanionEntity.canAttack()) {
        return false;
      } else {
        LivingEntity livingEntity = this.playerCompanionEntity.getTarget();
        if (canPenalize) {
          if (--this.ticksUntilNextPathRecalculation <= 0) {
            this.path = this.playerCompanionEntity.getNavigation().createPath(livingEntity, 0);
            this.ticksUntilNextPathRecalculation =
                4 + this.playerCompanionEntity.getRandom().nextInt(7);
            return this.path != null;
          } else {
            return true;
          }
        }
        this.path = this.playerCompanionEntity.getNavigation().createPath(livingEntity, 0);
        if (this.path != null) {
          return true;
        } else if (livingEntity != null) {
          return this.getAttackReachSqr(livingEntity) >= this.playerCompanionEntity
              .distanceToSqr(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
        }
      }
    }
    return false;
  }

  @Override
  public boolean canContinueToUse() {
    LivingEntity livingEntity = this.playerCompanionEntity.getTarget();
    if (!this.playerCompanionEntity.shouldAttack() || !this.playerCompanionEntity.canAttack()) {
      return false;
    } else if (!this.followingTargetEvenIfNotSeen) {
      return !this.playerCompanionEntity.getNavigation().isDone();
    } else
      return livingEntity == null
          || this.playerCompanionEntity.isWithinRestriction(livingEntity.blockPosition());
  }

  @Override
  public void start() {
    this.playerCompanionEntity.getNavigation().moveTo(this.path, this.speedModifier);
    this.playerCompanionEntity.setAggressive(true);
    this.ticksUntilNextPathRecalculation = 0;
    this.ticksUntilNextAttack = 0;
  }

  @Override
  public void stop() {
    LivingEntity livingEntity = this.playerCompanionEntity.getTarget();
    if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
      this.playerCompanionEntity.setTarget(null);
    }
    this.playerCompanionEntity.setAggressive(false);
    this.playerCompanionEntity.getNavigation().stop();
  }

  @Override
  public boolean requiresUpdateEveryTick() {
    return true;
  }

  @Override
  public void tick() {
    LivingEntity livingEntity = this.playerCompanionEntity.getTarget();
    if (livingEntity != null) {
      this.playerCompanionEntity.getLookControl().setLookAt(livingEntity, 30.0F, 30.0F);
      double distance = this.playerCompanionEntity.distanceToSqr(livingEntity.getX(),
          livingEntity.getY(), livingEntity.getZ());
      this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
      if ((this.followingTargetEvenIfNotSeen
          || this.playerCompanionEntity.getSensing().hasLineOfSight(livingEntity))
          && this.ticksUntilNextPathRecalculation <= 0
          && (this.pathTargetX == 0.0D && this.pathTargetY == 0.0D && this.pathTargetZ == 0.0D
          || livingEntity.distanceToSqr(this.pathTargetX, this.pathTargetY,
          this.pathTargetZ) >= 1.0D
          || this.playerCompanionEntity.getRandom().nextFloat() < 0.05F)) {
        this.pathTargetX = livingEntity.getX();
        this.pathTargetY = livingEntity.getY();
        this.pathTargetZ = livingEntity.getZ();
        this.ticksUntilNextPathRecalculation =
            4 + this.playerCompanionEntity.getRandom().nextInt(7);
        if (this.canPenalize) {
          this.ticksUntilNextPathRecalculation += failedPathFindingPenalty;
          Path navigationPath = this.playerCompanionEntity.getNavigation().getPath();
          if (navigationPath != null) {
            net.minecraft.world.level.pathfinder.Node finalPathPoint = navigationPath.getEndNode();
            if (finalPathPoint != null && livingEntity.distanceToSqr(finalPathPoint.x,
                finalPathPoint.y, finalPathPoint.z) < 1) {
              failedPathFindingPenalty = 0;
            } else {
              failedPathFindingPenalty += 10;
            }
          } else {
            failedPathFindingPenalty += 10;
          }
        }
        if (distance > 1024.0D) {
          this.ticksUntilNextPathRecalculation += 10;
        } else if (distance > 256.0D) {
          this.ticksUntilNextPathRecalculation += 5;
        }

        if (!this.playerCompanionEntity.getNavigation().moveTo(livingEntity, this.speedModifier)) {
          this.ticksUntilNextPathRecalculation += 15;
        }

        this.ticksUntilNextPathRecalculation =
            this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
      }

      this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
      this.checkAndPerformAttack(livingEntity, distance);
    }
  }

  protected void checkAndPerformAttack(LivingEntity livingEntity, double attackDistance) {
    double distance = this.getAttackReachSqr(livingEntity);
    if (attackDistance <= distance && this.ticksUntilNextAttack <= 0) {
      this.resetAttackCooldown();
      this.playerCompanionEntity.swing(InteractionHand.MAIN_HAND);
      this.playerCompanionEntity.doHurtTarget(livingEntity);
    }

  }

  protected void resetAttackCooldown() {
    this.ticksUntilNextAttack = this.adjustedTickDelay(20);
  }

  protected boolean isTimeToAttack() {
    return this.ticksUntilNextAttack <= 0;
  }

  protected int getTicksUntilNextAttack() {
    return this.ticksUntilNextAttack;
  }

  protected int getAttackInterval() {
    return this.adjustedTickDelay(20);
  }

  protected double getAttackReachSqr(LivingEntity livingEntity) {
    return this.playerCompanionEntity.getBbWidth() * 2.0F * this.playerCompanionEntity.getBbWidth()
        * 2.0F + livingEntity.getBbWidth();
  }
}
