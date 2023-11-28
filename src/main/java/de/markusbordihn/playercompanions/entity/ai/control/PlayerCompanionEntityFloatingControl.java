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

package de.markusbordihn.playercompanions.entity.ai.control;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerCompanionEntityFloatingControl extends MoveControl {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  protected PlayerCompanionEntity companionEntity;
  private int waitDelay;
  private int waitSoundDelay;

  public PlayerCompanionEntityFloatingControl(Mob mob) {
    super(mob);
    if (this.mob instanceof PlayerCompanionEntity playerCompanionEntity) {
      this.companionEntity = playerCompanionEntity;
    } else {
      log.error("Error, no Player Companions compatible entity: {}!", mob);
    }
  }

  public void setWantedMovement(double speed) {
    this.speedModifier = speed;
    this.operation = MoveControl.Operation.MOVE_TO;
  }

  @Override
  public void tick() {

    // More advanced moving for entities.
    if (this.operation == MoveControl.Operation.MOVE_TO) {
      this.operation = MoveControl.Operation.WAIT;
      double newX = this.wantedX - this.mob.getX();
      double newY = this.wantedY - this.mob.getY();
      double newZ = this.wantedZ - this.mob.getZ();
      double d3 = newX * newX + newY * newY + newZ * newZ;
      if (d3 < 2.5000003E-7F) {
        this.mob.setZza(0.0F);
        return;
      }

      // Perform normal movements.
      float f9 = (float) (Mth.atan2(newZ, newX) * 180F / (float) Math.PI) - 90.0F;
      this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f9, 90.0F));
      this.mob.setSpeed(
          (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
      BlockPos blockPos = this.mob.blockPosition();
      BlockState blockState = this.mob.level().getBlockState(blockPos);
      VoxelShape voxelShape = blockState.getCollisionShape(this.mob.level(), blockPos);
      if (newY > this.mob.getStepHeight()
              && newX * newX + newZ * newZ < Math.max(1.0F, this.mob.getBbWidth())
          || !voxelShape.isEmpty()
              && this.mob.getY() < voxelShape.max(Direction.Axis.Y) + blockPos.getY()
              && !blockState.is(BlockTags.DOORS)
              && !blockState.is(BlockTags.FENCES)) {
        this.mob.getJumpControl().jump();
        this.operation = MoveControl.Operation.JUMPING;
      }
    }

    // Wait on place if ordered to sit.
    else if (this.operation == MoveControl.Operation.WAIT && companionEntity.isOrderedToSit()) {
      if (this.waitDelay-- <= 0) {
        this.waitDelay = companionEntity.getWaitDelay();

        // Play wait sound in specific intervals.
        if (waitSoundDelay++ >= companionEntity.getAmbientSoundInterval()) {
          this.companionEntity.playSound(
              companionEntity.getWaitSound(),
              companionEntity.getSoundVolume(),
              companionEntity.getSoundPitch());
          waitSoundDelay = 0;
        }
      }
    } else if (this.operation == MoveControl.Operation.WAIT) {
      this.mob.setZza(0.0F);
    } else {
      super.tick();
    }
  }
}
