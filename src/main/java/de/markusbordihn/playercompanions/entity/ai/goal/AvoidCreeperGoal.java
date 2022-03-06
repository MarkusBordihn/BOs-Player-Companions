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

import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;

import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;

public class AvoidCreeperGoal extends PlayerCompanionGoal {

  private final double walkSpeedModifier;
  private final double sprintSpeedModifier;
  @Nullable
  protected Creeper creeperToAvoid;
  protected final float maxDist;
  @Nullable
  protected Path path;
  protected final PathNavigation pathNav;
  protected static final Predicate<LivingEntity> avoidPredicate = livingEntity -> true;
  protected static final Predicate<LivingEntity> predicateOnAvoidEntity =
      EntitySelector.NO_CREATIVE_OR_SPECTATOR::test;
  private final TargetingConditions avoidEntityTargeting;

  public AvoidCreeperGoal(PlayerCompanionEntity playerCompanionEntity) {
    this(playerCompanionEntity,  5.0F, 1.0D, 1.0D);
  }

  public AvoidCreeperGoal(PlayerCompanionEntity playerCompanionEntity, float maxDist,
      double walkSpeedModifier, double sprintSpeedModifier) {
    super(playerCompanionEntity);
    this.maxDist = maxDist;
    this.walkSpeedModifier = walkSpeedModifier;
    this.sprintSpeedModifier = sprintSpeedModifier;
    this.pathNav = playerCompanionEntity.getNavigation();
    this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    this.avoidEntityTargeting = TargetingConditions.forCombat().range(maxDist)
        .selector(AvoidCreeperGoal.predicateOnAvoidEntity.and(AvoidCreeperGoal.avoidPredicate));
  }

  @Override
  public boolean canUse() {
    this.creeperToAvoid =
        this.playerCompanionEntity.level.getNearestEntity(
            this.playerCompanionEntity.level.getEntitiesOfClass(Creeper.class,
                this.playerCompanionEntity.getBoundingBox().inflate(this.maxDist, 3.0D,
                    this.maxDist),
                creeper -> true),
            this.avoidEntityTargeting, this.playerCompanionEntity,
            this.playerCompanionEntity.getX(), this.playerCompanionEntity.getY(),
            this.playerCompanionEntity.getZ());
    if (this.creeperToAvoid == null) {
      return false;
    } else {
      Vec3 vec3 =
          DefaultRandomPos.getPosAway(this.playerCompanionEntity, 16, 7, this.creeperToAvoid.position());
      if (vec3 == null) {
        return false;
      } else if (this.creeperToAvoid.distanceToSqr(vec3.x, vec3.y, vec3.z) < this.creeperToAvoid
          .distanceToSqr(this.playerCompanionEntity)) {
        return false;
      } else {
        this.path = this.pathNav.createPath(vec3.x, vec3.y, vec3.z, 0);
        return this.path != null;
      }
    }
  }

  @Override
  public boolean canContinueToUse() {
    return !this.pathNav.isDone();
  }

  @Override
  public void start() {
    this.pathNav.moveTo(this.path, this.walkSpeedModifier);
  }

  @Override
  public void stop() {
    this.creeperToAvoid = null;
  }

  @Override
  public void tick() {
    if (this.playerCompanionEntity.distanceToSqr(this.creeperToAvoid) < 49.0D) {
      this.playerCompanionEntity.getNavigation().setSpeedModifier(this.sprintSpeedModifier);
    } else {
      this.playerCompanionEntity.getNavigation().setSpeedModifier(this.walkSpeedModifier);
    }
  }
}
