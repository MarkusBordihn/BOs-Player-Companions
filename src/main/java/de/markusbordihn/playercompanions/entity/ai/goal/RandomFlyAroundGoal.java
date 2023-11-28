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

public class RandomFlyAroundGoal extends PlayerCompanionGoal {

  public RandomFlyAroundGoal(PlayerCompanionEntity playerCompanionEntity) {
    super(playerCompanionEntity);
    this.setFlags(EnumSet.of(Goal.Flag.MOVE));
  }

  @Override
  public boolean canUse() {
    if (!this.playerCompanionEntity.hasOwnerAndIsAlive()
        || this.playerCompanionEntity.isOrderedToSit()
        || this.playerCompanionEntity.isOrderedToPosition()) {
      return false;
    }
    return !this.playerCompanionEntity.getMoveControl().hasWanted() && this.random.nextInt(7) == 0;
  }

  @Override
  public boolean canContinueToUse() {
    return false;
  }

  @Override
  public void tick() {
    BlockPos blockPos = this.playerCompanionEntity.ownerBlockPosition();
    for (int i = 0; i < 3; ++i) {
      BlockPos randomBlockPos = blockPos.offset(this.random.nextInt(15) - 7,
          this.random.nextInt(11) - 5, this.random.nextInt(15) - 7);
      if (this.playerCompanionEntity.level().isEmptyBlock(randomBlockPos)) {
        this.playerCompanionEntity.getMoveControl().setWantedPosition(randomBlockPos.getX() + 0.5D,
            randomBlockPos.getY() + 0.5D, randomBlockPos.getZ() + 0.5D, 0.25D);
        if (this.playerCompanionEntity.getTarget() == null) {
          this.playerCompanionEntity.getLookControl().setLookAt(randomBlockPos.getX() + 0.5D,
              randomBlockPos.getY() + 0.5D, randomBlockPos.getZ() + 0.5D, 180.0F, 20.0F);
        }
        break;
      }
    }

  }

}
