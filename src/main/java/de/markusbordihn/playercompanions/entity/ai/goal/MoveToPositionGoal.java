package de.markusbordihn.playercompanions.entity.ai.goal;

import java.util.EnumSet;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;

public class MoveToPositionGoal extends PlayerCompanionGoal {

  private final double speedModifier;
  private final float stopDistance;

  private int timeToRecalcPath;
  private float oldWaterCost;
  private BlockPos targetPosition;

  public MoveToPositionGoal(PlayerCompanionEntity playerCompanionEntity, double speedModifier,
      float stopDistance) {
    super(playerCompanionEntity);
    this.speedModifier = speedModifier;
    this.stopDistance = stopDistance;
    this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
  }

  @Override
  public boolean canUse() {
    if (!this.playerCompanionEntity.hasOwner()) {
      return false;
    }
    BlockPos blockPos = this.playerCompanionEntity.getOrderedToPosition();
    if (blockPos == null || !this.playerCompanionEntity.isOrderedToPosition()
        || this.playerCompanionEntity.distanceToSqr(blockPos.getX(), blockPos.getY(),
            blockPos.getZ()) < this.stopDistance * this.stopDistance) {
      return false;
    }
    this.targetPosition = blockPos;
    return true;
  }

  @Override
  public boolean canContinueToUse() {
    if (!this.playerCompanionEntity.hasOwner() || this.navigation.isDone() || !this.playerCompanionEntity.isOrderedToPosition()) {
      return false;
    } else {
      return this.playerCompanionEntity.distanceToSqr(this.targetPosition.getX(),
          this.targetPosition.getY(),
          this.targetPosition.getZ()) > this.stopDistance * this.stopDistance;
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
    this.playerCompanionEntity.getLookControl().setLookAt(this.targetPosition.getX(),
        this.targetPosition.getY(), this.targetPosition.getZ(), 10.0F,
        this.playerCompanionEntity.getMaxHeadXRot());

    if (--this.timeToRecalcPath <= 0) {
      this.timeToRecalcPath = this.adjustedTickDelay(10);
      if (!this.playerCompanionEntity.isLeashed() && !this.playerCompanionEntity.isPassenger()) {
        this.navigation.moveTo(this.targetPosition.getX(), this.targetPosition.getY(),
            this.targetPosition.getZ(), this.speedModifier);
      }
    }
  }

}
