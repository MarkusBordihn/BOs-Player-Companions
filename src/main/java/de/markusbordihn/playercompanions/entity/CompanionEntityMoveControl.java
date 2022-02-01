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

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CompanionEntityMoveControl extends MoveControl {
  public static final float MIN_SPEED = 5.0E-4F;
  public static final float MIN_SPEED_SQR = 2.5000003E-7F;
  protected static final int MAX_TURN = 90;
  private int jumpDelay;
  private int jumpMoveDelay;
  private int waitDelay;

  public CompanionEntityMoveControl(Mob mob) {
    super(mob);
  }

  public void setWantedMovement(double speed) {
    this.speedModifier = speed;
    this.operation = MoveControl.Operation.MOVE_TO;
  }

  @Override
  public void tick() {
    if (this.mob instanceof CompanionEntity companionEntity) {

      // More advanced moving for Monsters
      if (this.operation == MoveControl.Operation.MOVE_TO) {
        boolean skipMovement = false;
        this.operation = MoveControl.Operation.WAIT;
        double newX = this.wantedX - this.mob.getX();
        double newY = this.wantedY - this.mob.getY();
        double newZ = this.wantedZ - this.mob.getZ();
        double d3 = newX * newX + newY * newY + newZ * newZ;
        if (d3 < 2.5000003E-7F) {
          this.mob.setZza(0.0F);
          return;
        }

        // Jump during moving if needed.
        if (companionEntity.keepOnJumping() && this.mob.isOnGround() && this.jumpMoveDelay-- <= 0) {
          this.jumpMoveDelay = companionEntity.getJumpMoveDelay();
          this.mob.getJumpControl().jump();
          if (companionEntity.doPlayJumpSound()) {
            this.mob.playSound(companionEntity.getJumpSound(), companionEntity.getSoundVolume(),
                companionEntity.getSoundPitch());
          }
          skipMovement = true;
        }

        // Perform normal movements.
        if (!skipMovement) {
          float f9 = (float) (Mth.atan2(newZ, newX) * 180F / (float) Math.PI) - 90.0F;
          this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f9, 90.0F));
          this.mob.setSpeed(
              (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
          BlockPos blockPos = this.mob.blockPosition();
          BlockState blockState = this.mob.level.getBlockState(blockPos);
          VoxelShape voxelShape = blockState.getCollisionShape(this.mob.level, blockPos);
          if (newY > this.mob.maxUpStep
              && newX * newX + newZ * newZ < Math.max(1.0F, this.mob.getBbWidth())
              || !voxelShape.isEmpty()
                  && this.mob.getY() < voxelShape.max(Direction.Axis.Y) + blockPos.getY()
                  && !blockState.is(BlockTags.DOORS) && !blockState.is(BlockTags.FENCES)) {
            this.mob.getJumpControl().jump();
            this.operation = MoveControl.Operation.JUMPING;
          }
        }
      } else if (this.operation == MoveControl.Operation.WAIT && companionEntity.isOrderedToSit()) {

        // Wait on place if ordered to sit.
        if (this.waitDelay-- <= 0) {
          this.waitDelay = companionEntity.getWaitDelay();
          this.mob.playSound(companionEntity.getWaitSound(), companionEntity.getSoundVolume(),
              companionEntity.getSoundPitch());
        }
      } else if (this.operation == MoveControl.Operation.WAIT) {

        // Keep jumping if not ordered to sit.
        if (companionEntity.keepOnJumping()
            && this.mob.isOnGround() && this.jumpDelay-- <= 0) {
          this.jumpDelay = companionEntity.getJumpDelay();
          this.mob.getJumpControl().jump();
          if (companionEntity.doPlayJumpSound()) {
            this.mob.playSound(companionEntity.getJumpSound(), companionEntity.getSoundVolume(),
                companionEntity.getSoundPitch());
          }
        }
        this.mob.setZza(0.0F);
      } else {
        super.tick();
      }
    } else {
      super.tick();
    }
  }

}
