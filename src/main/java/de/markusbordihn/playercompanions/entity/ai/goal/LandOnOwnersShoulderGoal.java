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
import net.minecraft.server.level.ServerPlayer;

public class LandOnOwnersShoulderGoal extends PlayerCompanionGoal {

  private ServerPlayer owner;
  private boolean isSittingOnShoulder;

  public LandOnOwnersShoulderGoal(PlayerCompanionEntity playerCompanionEntity) {
    super(playerCompanionEntity);
    if (this.playerCompanionEntity.hasOwner()
        && this.playerCompanionEntity.getOwner() instanceof ServerPlayer serverPlayer) {
      this.owner = serverPlayer;
    }
  }

  @Override
  public boolean canUse() {
    if (!this.playerCompanionEntity.hasOwner()) {
      return false;
    }
    if (this.owner == null
        && this.playerCompanionEntity.getOwner() instanceof ServerPlayer serverPlayer) {
      this.owner = serverPlayer;
    }
    return this.owner != null
        && !owner.isSpectator()
        && !owner.getAbilities().flying
        && !owner.isInWater()
        && !owner.isInPowderSnow
        && !this.playerCompanionEntity.isOrderedToSit()
        && !this.playerCompanionEntity.isOrderedToPosition()
        && this.playerCompanionEntity.canSitOnShoulder();
  }

  @Override
  public boolean isInterruptable() {
    return !this.isSittingOnShoulder;
  }

  @Override
  public void start() {
    this.isSittingOnShoulder = false;
  }

  @Override
  public void stop() {
    this.playerCompanionEntity.setRideCooldownCounter(0);
  }

  @Override
  public void tick() {
    if (!this.isSittingOnShoulder
        && !this.playerCompanionEntity.isInSittingPose()
        && !this.playerCompanionEntity.isLeashed()) {
      if (this.playerCompanionEntity.getBoundingBox().intersects(this.owner.getBoundingBox())) {
        this.isSittingOnShoulder = this.playerCompanionEntity.setEntityOnShoulder(this.owner);
      }
    }
  }
}
