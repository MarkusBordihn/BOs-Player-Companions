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


package de.markusbordihn.playercompanions.entity.ai.goal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;

public class ShootLargeFireballGoal extends PlayerCompanionGoal {

  private final int explosionPower;
  private int chargeTime;

  public ShootLargeFireballGoal(PlayerCompanionEntity playerCompanionEntity) {
    super(playerCompanionEntity);
    this.explosionPower = this.playerCompanionEntity.getExplosionPower();
  }

  @Override
  public boolean canUse() {
    return this.playerCompanionEntity.shouldAttack() && this.playerCompanionEntity.canAttack();
  }

  @Override
  public void start() {
    this.chargeTime = 0;
  }

  @Override
  public void stop() {
    this.playerCompanionEntity.setCharging(false);
  }

  @Override
  public boolean requiresUpdateEveryTick() {
    return true;
  }

  @Override
  public void tick() {
    super.tick();

    LivingEntity livingEntity = this.playerCompanionEntity.getTarget();
    if (livingEntity != null && livingEntity.isAlive()) {

      // Only shot if we have a line of sight.
      if (livingEntity.distanceToSqr(this.playerCompanionEntity) < 4096.0D
          && this.playerCompanionEntity.hasLineOfSight(livingEntity)) {
        Level level = this.playerCompanionEntity.level();
        ++this.chargeTime;
        if (this.chargeTime == 10 && !this.playerCompanionEntity.isSilent()) {
          level.levelEvent((Player) null, 1015, this.playerCompanionEntity.blockPosition(), 0);
        }

        if (this.chargeTime == 20) {
          Vec3 vec3 = this.playerCompanionEntity.getViewVector(1.0F);
          double x = livingEntity.getX() - (this.playerCompanionEntity.getX() + vec3.x * 4.0D);
          double y = livingEntity.getY(0.5D) - (0.5D + this.playerCompanionEntity.getY(0.5D));
          double z = livingEntity.getZ() - (this.playerCompanionEntity.getZ() + vec3.z * 4.0D);
          if (!this.playerCompanionEntity.isSilent()) {
            level.levelEvent((Player) null, 1016, this.playerCompanionEntity.blockPosition(), 0);
          }

          LargeFireball largeFireball =
              new LargeFireball(level, this.playerCompanionEntity, x, y, z, this.explosionPower);
          largeFireball.setPos(this.playerCompanionEntity.getX() + vec3.x * 4.0D,
              this.playerCompanionEntity.getY(0.5D) + 0.5D, largeFireball.getZ() + vec3.z * 4.0D);
          level.addFreshEntity(largeFireball);
          this.chargeTime = -40;
        }
      } else if (this.chargeTime > 0) {
        --this.chargeTime;
      }
      this.playerCompanionEntity.setCharging(this.chargeTime > 10);
    }
  }
}
