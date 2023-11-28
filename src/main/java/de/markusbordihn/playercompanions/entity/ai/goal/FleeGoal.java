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

import de.markusbordihn.playercompanions.entity.AggressionLevel;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;

public class FleeGoal extends PlayerCompanionGoal {

  public static final int WATER_CHECK_DISTANCE_VERTICAL = 1;
  protected final double speedModifier;
  protected double posX;
  protected double posY;
  protected double posZ;
  protected boolean isRunning;

  public FleeGoal(PlayerCompanionEntity playerCompanionEntity, double speedModifier) {
    super(playerCompanionEntity);
    this.speedModifier = speedModifier;
    this.setFlags(EnumSet.of(Goal.Flag.MOVE));
  }

  @Override
  public boolean canUse() {
    if (this.playerCompanionEntity.getLastHurtByMob() == null
        || this.playerCompanionEntity.getAggressionLevel() != AggressionLevel.PASSIVE_FLEE) {
      return false;
    }
    return this.findRandomPosition();
  }

  protected boolean findRandomPosition() {
    Vec3 vec3 = DefaultRandomPos.getPos(this.playerCompanionEntity, 12, 12);
    if (vec3 == null) {
      return false;
    } else {
      this.posX = vec3.x;
      this.posY = vec3.y;
      this.posZ = vec3.z;
      return true;
    }
  }

  public boolean isRunning() {
    return this.isRunning;
  }

  @Override
  public void start() {
    this.playerCompanionEntity.getNavigation().moveTo(this.posX, this.posY, this.posZ,
        this.speedModifier);
    this.isRunning = true;
  }

  @Override
  public void stop() {
    this.isRunning = false;
  }

  @Override
  public boolean canContinueToUse() {
    return !this.playerCompanionEntity.getNavigation().isDone()
        && this.playerCompanionEntity.getAggressionLevel() == AggressionLevel.PASSIVE_FLEE;
  }
}
