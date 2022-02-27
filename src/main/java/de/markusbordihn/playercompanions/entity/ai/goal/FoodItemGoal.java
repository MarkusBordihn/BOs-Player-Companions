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

import javax.annotation.Nullable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;

import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;

public class FoodItemGoal extends PlayerCompanionGoal {

  private static final TargetingConditions TEMP_TARGETING =
      TargetingConditions.forNonCombat().range(10.0D).ignoreLineOfSight();
  private final TargetingConditions targetingConditions;
  private final double speedModifier;
  private double px;
  private double py;
  private double pz;
  private double pRotX;
  private double pRotY;

  @Nullable
  protected Player player;
  private int calmDown;
  private boolean isRunning;
  private final Ingredient items;
  private final boolean canScare;

  public FoodItemGoal(PlayerCompanionEntity playerCompanionEntity, double speedModifier) {
    super(playerCompanionEntity);
    this.speedModifier = speedModifier;
    this.items = this.playerCompanionEntity.getFoodItems();
    this.canScare = true;
    this.targetingConditions = TEMP_TARGETING.copy().selector(this::shouldFollow);
  }

  private boolean shouldFollow(LivingEntity livingEntity) {
    return this.items.test(livingEntity.getMainHandItem())
        || this.items.test(livingEntity.getOffhandItem());
  }

  @Override
  public boolean canUse() {
    if (!this.playerCompanionEntity.canEat()) {
      return false;
    } else if (this.calmDown > 0) {
      --this.calmDown;
      return false;
    } else {
      this.player = this.playerCompanionEntity.getNearestPlayer(this.targetingConditions);
      return this.player != null;
    }
  }

  @Override
  public boolean canContinueToUse() {
    if (!this.playerCompanionEntity.canEat()) {
      return false;
    } else if (this.canScare()) {
      if (this.playerCompanionEntity.distanceToSqr(this.player) < 36.0D) {
        if (this.player.distanceToSqr(this.px, this.py, this.pz) > 0.010000000000000002D) {
          return false;
        }

        if (Math.abs(this.player.getXRot() - this.pRotX) > 5.0D
            || Math.abs(this.player.getYRot() - this.pRotY) > 5.0D) {
          return false;
        }
      } else {
        this.px = this.player.getX();
        this.py = this.player.getY();
        this.pz = this.player.getZ();
      }

      this.pRotX = this.player.getXRot();
      this.pRotY = this.player.getYRot();
    }

    return this.canUse();
  }

  protected boolean canScare() {
    return this.canScare;
  }

  @Override
  public void start() {
    this.px = this.player.getX();
    this.py = this.player.getY();
    this.pz = this.player.getZ();
    this.isRunning = true;
  }

  @Override
  public void stop() {
    this.player = null;
    this.playerCompanionEntity.getNavigation().stop();
    this.calmDown = reducedTickDelay(100);
    this.isRunning = false;
  }

  @Override
  public void tick() {
    this.playerCompanionEntity.getLookControl().setLookAt(this.player,
        this.playerCompanionEntity.getMaxHeadYRot() + 20.0F,
        this.playerCompanionEntity.getMaxHeadXRot());
    if (this.playerCompanionEntity.distanceToSqr(this.player) < 6.25D) {
      this.playerCompanionEntity.getNavigation().stop();
    } else {
      this.playerCompanionEntity.getNavigation().moveTo(this.player, this.speedModifier);
    }

  }

  public boolean isRunning() {
    return this.isRunning;
  }

}
