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
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public class MoveToPositionGoal extends PlayerCompanionGoal {

  private final double speedModifier;
  private final float stopDistance;

  private int timeToRecalcPath;
  private float oldWaterCost;
  private BlockPos targetPosition;

  public MoveToPositionGoal(
      PlayerCompanionEntity playerCompanionEntity, double speedModifier, float stopDistance) {
    super(playerCompanionEntity);
    this.speedModifier = speedModifier;
    this.stopDistance = stopDistance;
    this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
  }

  @Override
  public boolean canUse() {
    if (!this.playerCompanionEntity.hasOwnerAndIsAlive()) {
      return false;
    }
    BlockPos blockPos = this.playerCompanionEntity.getOrderedToPosition();
    if (blockPos == null
        || !this.playerCompanionEntity.isOrderedToPosition()
        || this.playerCompanionEntity.distanceToSqr(
                blockPos.getX(), blockPos.getY(), blockPos.getZ())
            < this.stopDistance * this.stopDistance) {
      return false;
    }
    this.targetPosition = blockPos;
    return true;
  }

  @Override
  public boolean canContinueToUse() {
    if (!this.playerCompanionEntity.hasOwnerAndIsAlive()
        || this.navigation.isDone()
        || !this.playerCompanionEntity.isOrderedToPosition()) {
      return false;
    } else {
      return this.playerCompanionEntity.distanceToSqr(
              this.targetPosition.getX(), this.targetPosition.getY(), this.targetPosition.getZ())
          > this.stopDistance * this.stopDistance;
    }
  }

  @Override
  public void start() {
    this.timeToRecalcPath = 0;
    this.oldWaterCost = this.playerCompanionEntity.getPathfindingMalus(BlockPathTypes.WATER);
    this.playerCompanionEntity.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
  }

  @Override
  public void stop() {
    this.targetPosition = null;
    this.playerCompanionEntity.setOrderedToPosition(null);
    this.navigation.stop();
    this.playerCompanionEntity.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
  }

  @Override
  public void tick() {
    this.playerCompanionEntity
        .getLookControl()
        .setLookAt(
            this.targetPosition.getX(),
            this.targetPosition.getY(),
            this.targetPosition.getZ(),
            10.0F,
            this.playerCompanionEntity.getMaxHeadXRot());

    if (--this.timeToRecalcPath <= 0) {
      this.timeToRecalcPath = this.adjustedTickDelay(10);
      if (!this.playerCompanionEntity.isLeashed() && !this.playerCompanionEntity.isPassenger()) {
        this.navigation.moveTo(
            this.targetPosition.getX(),
            this.targetPosition.getY(),
            this.targetPosition.getZ(),
            this.speedModifier);
      }
    }
  }
}
