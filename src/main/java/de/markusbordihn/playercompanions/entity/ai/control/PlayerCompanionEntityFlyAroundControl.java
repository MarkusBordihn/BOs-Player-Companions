/**
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

package de.markusbordihn.playercompanions.entity.ai.control;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

public class PlayerCompanionEntityFlyAroundControl extends PlayerCompanionEntityFloatingControl {

  public PlayerCompanionEntityFlyAroundControl(Mob mob) {
    super(mob);
  }

  @Override
  public void tick() {
    if (this.operation == MoveControl.Operation.MOVE_TO) {
      Vec3 vec3 = new Vec3(this.wantedX - this.companionEntity.getX(),
          this.wantedY - this.companionEntity.getY(), this.wantedZ - this.companionEntity.getZ());
      double d0 = vec3.length();
      if (d0 < this.companionEntity.getBoundingBox().getSize()) {
        this.operation = MoveControl.Operation.WAIT;
        this.companionEntity.setDeltaMovement(this.companionEntity.getDeltaMovement().scale(0.5D));
      } else {
        this.companionEntity.setDeltaMovement(this.companionEntity.getDeltaMovement()
            .add(vec3.scale(this.speedModifier * 0.05D / d0)));
        LivingEntity target = this.companionEntity.getTarget();
        if (target == null) {
          Vec3 vec31 = this.companionEntity.getDeltaMovement();
          this.companionEntity
              .setYRot(-((float) Mth.atan2(vec31.x, vec31.z)) * (180F / (float) Math.PI));
          this.companionEntity.yBodyRot = this.companionEntity.getYRot();
        } else {
          double d2 = target.getX() - this.companionEntity.getX();
          double d1 = target.getZ() - this.companionEntity.getZ();
          this.companionEntity.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
          this.companionEntity.yBodyRot = this.companionEntity.getYRot();
        }
      }
    } else {
      if (!this.mob.isAlive()) {
        this.mob.setNoGravity(false);
      }
      this.mob.setYya(0.0F);
      this.mob.setZza(0.0F);
    }
  }
}
