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

package de.markusbordihn.playercompanions.entity.ai.control;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerCompanionEntityFlyControl extends MoveControl {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private final boolean hoversInPlace;
  private final int maxTurn;
  protected PlayerCompanionEntity companionEntity;
  private int waitDelay;
  private int waitSoundDelay;

  public PlayerCompanionEntityFlyControl(Mob mob, int maxTurn, boolean hovers) {
    super(mob);
    this.maxTurn = maxTurn;
    this.hoversInPlace = hovers;
    if (this.mob instanceof PlayerCompanionEntity playerCompanionEntity) {
      this.companionEntity = playerCompanionEntity;
    } else {
      log.error("Error, no Player Companions compatible entity: {}!", mob);
    }
  }

  @Override
  public void tick() {
    if (this.operation == MoveControl.Operation.MOVE_TO) {
      this.operation = MoveControl.Operation.WAIT;
      this.mob.setNoGravity(true);
      double d0 = this.wantedX - this.mob.getX();
      double d1 = this.wantedY - this.mob.getY();
      double d2 = this.wantedZ - this.mob.getZ();
      double d3 = d0 * d0 + d1 * d1 + d2 * d2;
      if (d3 < 2.5000003E-7F) {
        this.mob.setYya(0.0F);
        this.mob.setZza(0.0F);
        return;
      }

      float f = (float) (Mth.atan2(d2, d0) * (180F / (float) Math.PI)) - 90.0F;
      this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f, 90.0F));
      float f1;
      if (this.mob.onGround()) {
        f1 = (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
      } else {
        f1 = (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.FLYING_SPEED));
      }

      this.mob.setSpeed(f1);
      double d4 = Math.sqrt(d0 * d0 + d2 * d2);
      if (Math.abs(d1) > 1.0E-5F || Math.abs(d4) > 1.0E-5F) {
        float f2 = (float) (-(Mth.atan2(d1, d4) * (180F / (float) Math.PI)));
        this.mob.setXRot(this.rotlerp(this.mob.getXRot(), f2, this.maxTurn));
        this.mob.setYya(d1 > 0.0D ? f1 : -f1);
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
      if (this.hoversInPlace) {
        this.mob.setNoGravity(false);
      }
      this.mob.setYya(0.0F);
      this.mob.setZza(0.0F);
    } else {
      if (!this.hoversInPlace || !this.mob.isAlive()) {
        this.mob.setNoGravity(false);
      }
      this.mob.setYya(0.0F);
      this.mob.setZza(0.0F);
    }
  }
}
