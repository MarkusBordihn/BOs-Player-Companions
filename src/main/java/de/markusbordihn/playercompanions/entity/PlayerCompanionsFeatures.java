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

package de.markusbordihn.playercompanions.entity;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import de.markusbordihn.playercompanions.Constants;

public class PlayerCompanionsFeatures {

  protected final Random random = new Random();

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  protected short ticker = 0;

  protected Level level;
  protected PlayerCompanionEntity playerCompanionEntity;
  protected NeutralMob neutralMob;

  // Animation related information
  private float flap;
  private float flapSpeed;
  private float oFlapSpeed;
  private float oFlap;
  private float flapping = 1.0F;

  public PlayerCompanionsFeatures(PlayerCompanionEntity playerCompanionEntity, Level level) {
    // Store general references
    this.level = level;
    this.playerCompanionEntity = playerCompanionEntity;

    // Pre-cast neutral mobs
    if (playerCompanionEntity instanceof NeutralMob neutralMobCast) {
      this.neutralMob = neutralMobCast;
    }

    // Distribute Ticks along several entities
    this.ticker = (short) this.random.nextInt(0, 25);
  }

  public int getExperienceLevel() {
    return this.playerCompanionEntity.getExperienceLevel();
  }

  public LivingEntity getOwner() {
    return playerCompanionEntity.getOwner();
  }

  public float getOFlap() {
    return this.oFlap;
  }

  public float getFlap() {
    return this.flap;
  }

  public float getOFlapSpeed() {
    return this.oFlapSpeed;
  }

  public float getFlapSpeed() {
    return this.flapSpeed;
  }

  protected void aiStep() {
    // Placeholder function
  }

  public void aiFlappingStep() {
    this.oFlap = this.flap;
    this.oFlapSpeed = this.flapSpeed;
    this.flapSpeed = (float) (this.flapSpeed + (this.playerCompanionEntity.isOnGround() ? -1 : 4) * 0.3D);
    this.flapSpeed = Mth.clamp(this.flapSpeed, 0.0F, 1.0F);
    if (!this.playerCompanionEntity.isOnGround() && this.flapping < 1.0F) {
      this.flapping = 1.0F;
    }

    this.flapping = (float) (this.flapping * 0.9D);
    Vec3 vec3 = this.playerCompanionEntity.getDeltaMovement();
    if (!this.playerCompanionEntity.isOnGround() && vec3.y < 0.0D) {
      this.playerCompanionEntity.setDeltaMovement(vec3.multiply(1.0D, 0.6D, 1.0D));
    }

    this.flap += this.flapping * 2.0F;
  }

  protected void tick() {
    // Placeholder function
  }

}
