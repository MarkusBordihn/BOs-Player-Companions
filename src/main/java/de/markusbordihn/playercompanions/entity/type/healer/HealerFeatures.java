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

package de.markusbordihn.playercompanions.entity.type.healer;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.config.CommonConfig;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.entity.PlayerCompanionsFeatures;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class HealerFeatures extends PlayerCompanionsFeatures {

  private static final CommonConfig.Config COMMON = CommonConfig.COMMON;

  private static final short HEALER_TICK = 20 * 2;
  private static final int PARTICLE_FRAMES = 3;

  protected HealerFeatures(PlayerCompanionEntity playerCompanionEntity, Level level) {
    super(playerCompanionEntity, level);
  }

  @SubscribeEvent
  public static void handleServerAboutToStartEvent(ServerAboutToStartEvent event) {

    if (COMMON.healerTypeRadius.get() > 0
        && COMMON.healerTypeMinAmount.get() < COMMON.healerTypeMaxAmount.get()) {
      log.info("{} Healer will automatically heal between {} - {} hp in a {} block radius.",
          Constants.LOG_ICON, COMMON.healerTypeMinAmount.get(), COMMON.healerTypeMaxAmount.get(),
          COMMON.healerTypeRadius.get());
    } else {
      log.info("{} Healer will not automatically heal!", Constants.LOG_ICON);
    }
  }

  private void healerTick() {

    // Automatic heal entities in the defined radius.
    if (COMMON.healerTypeRadius.get() > 0 && ticker++ >= HEALER_TICK) {
      boolean hasHealthSomething = this.getOwner() != null && healEntity(level, this.getOwner());

      // 1. Priority: Heal owner

      // 2. Priority: Heal self
      if (!hasHealthSomething && healEntity(level, this.playerCompanionEntity)) {
        hasHealthSomething = true;
      }

      // 3. Priority: Heal other players in radius.
      if (!hasHealthSomething) {
        List<Player> playerEntities = this.level.getEntities(EntityType.PLAYER,
            new AABB(playerCompanionEntity.blockPosition()).inflate(COMMON.healerTypeRadius.get()),
            entity -> true);
        for (Player player : playerEntities) {
          if (player != this.getOwner() && healEntity(level, player)) {
            hasHealthSomething = true;
            break;
          }
        }
      }

      // 4. Priority: Heal owned tamed animals regardless of type.
      if (!hasHealthSomething && this.getOwner() != null) {
        List<TamableAnimal> tamableAnimals = playerCompanionEntity.level().getEntitiesOfClass(
            TamableAnimal.class,
            new AABB(playerCompanionEntity.blockPosition()).inflate(COMMON.healerTypeRadius.get()),
            entity -> true);
        for (TamableAnimal tamableAnimal : tamableAnimals) {
          if (tamableAnimal != this.playerCompanionEntity
              && tamableAnimal.getOwner() == this.getOwner() && healEntity(level, tamableAnimal)) {
            hasHealthSomething = true;
            break;
          }
        }
      }

      // Increase experience if we have health something (server-side)
      if (hasHealthSomething && !level.isClientSide) {
        distributeExperience(1);
      }

      ticker = 0;
    }
  }

  public boolean healEntity(Level level, LivingEntity livingEntity) {
    if (!livingEntity.isAlive() || livingEntity.getHealth() >= livingEntity.getMaxHealth()) {
      return false;
    }

    if (level.isClientSide) {
      healAnimation(livingEntity, level);
    } else {
      int healingAmount = playerCompanionEntity.getHealingAmountFromExperienceLevel(
          getExperienceLevel(), COMMON.healerTypeMinAmount.get(), COMMON.healerTypeMaxAmount.get());
      livingEntity.heal(healingAmount);
      return true;
    }
    return false;
  }

  public void healAnimation(LivingEntity livingEntity, Level level) {
    if (!level.isClientSide || livingEntity == null) {
      return;
    }

    // Target position
    double targetX = livingEntity.getX() + 0.5;
    double targetY = livingEntity.getY() + 0.5;
    double targetZ = livingEntity.getZ() + 0.5;

    // Show particles at ?
    for (int i = 0; i < 3; ++i) {
      level.addParticle(ParticleTypes.HEART, targetX, targetY, targetZ, 1D, 1D, 1D);
    }

    // Is Target healing itself ?
    if (livingEntity == playerCompanionEntity) {
      return;
    }

    // Source position
    double x = playerCompanionEntity.getX() + 0.5;
    double y = playerCompanionEntity.getY() + 0.5;
    double z = playerCompanionEntity.getZ() + 0.5;

    // Show particle trail to targeted direction.
    double targetXRatio = (targetX - x) / PARTICLE_FRAMES;
    double targetYRatio = (targetY - y) / PARTICLE_FRAMES;
    double targetZRatio = (targetZ - z) / PARTICLE_FRAMES;
    for (int i = 0; i < PARTICLE_FRAMES; ++i) {
      x += targetXRatio;
      y += targetYRatio;
      z += targetZRatio;
      level.addParticle(ParticleTypes.HEART, x, y, z, 1D, 1D, 1D);
    }
  }

  @Override
  public void tick() {
    super.tick();
    healerTick();
  }

}
