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

package de.markusbordihn.playercompanions.entity.type.supporter;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.config.CommonConfig;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.entity.PlayerCompanionsFeatures;
import de.markusbordihn.playercompanions.entity.type.PlayerCompanionType;
import java.util.List;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
public class SupporterFeatures extends PlayerCompanionsFeatures {

  private static final CommonConfig.Config COMMON = CommonConfig.COMMON;

  private static final short SUPPORTER_TICK = 180;

  protected SupporterFeatures(PlayerCompanionEntity playerCompanionEntity, Level level) {
    super(playerCompanionEntity, level);
  }

  @SubscribeEvent
  public static void handleServerAboutToStartEvent(ServerAboutToStartEvent event) {
    if (COMMON.supporterTypeRadius.get() > 0) {
      log.info("{} Supporter will automatically buff in a {} block radius.", Constants.LOG_ICON,
          COMMON.supporterTypeRadius.get());
    } else {
      log.info("{} Supporter will not automatically buff!", Constants.LOG_ICON);
    }
  }

  private void supporterTick() {

    // Automatic buff entities in the defined radius.
    if (!level.isClientSide && COMMON.supporterTypeRadius.get() > 0 && ticker++ >= SUPPORTER_TICK) {
      boolean hasBuffSomething = this.getOwner() != null && buffLivingEntity(this.getOwner());

      // 1. Priority: Buff owner.

      // 2. Priority: Buff self.
      if (!hasBuffSomething && buffLivingEntity(this.playerCompanionEntity)) {
        hasBuffSomething = true;
      }

      // 3. Priority: Buff other players in radius.
      if (!hasBuffSomething) {
        List<Player> playerEntities = this.level.getEntities(EntityType.PLAYER,
            new AABB(playerCompanionEntity.blockPosition()).inflate(
                COMMON.supporterTypeRadius.get()),
            entity -> true);
        for (Player player : playerEntities) {
          if (player != this.getOwner() && buffLivingEntity(player)) {
            hasBuffSomething = true;
            break;
          }
        }
      }

      // 4. Priority: Buff owned healer.
      if (!hasBuffSomething && this.getOwner() != null) {
        List<PlayerCompanionEntity> playerCompanions =
            playerCompanionEntity.level().getEntitiesOfClass(PlayerCompanionEntity.class,
                new AABB(playerCompanionEntity.blockPosition()).inflate(
                    COMMON.supporterTypeRadius.get()),
                entity -> true);
        for (PlayerCompanionEntity playerCompanion : playerCompanions) {
          if (playerCompanion != this.playerCompanionEntity
              && playerCompanion.getCompanionType() == PlayerCompanionType.HEALER
              && playerCompanion.getOwner() == this.getOwner()
              && buffLivingEntity(playerCompanion)) {
            hasBuffSomething = true;
            break;
          }
        }
      }

      // 5. Priority: Buff owned tamed animals regardless of type.
      if (!hasBuffSomething && this.getOwner() != null) {
        List<TamableAnimal> tamableAnimals =
            playerCompanionEntity.level().getEntitiesOfClass(TamableAnimal.class,
                new AABB(playerCompanionEntity.blockPosition()).inflate(
                    COMMON.supporterTypeRadius.get()),
                entity -> true);
        for (TamableAnimal tamableAnimal : tamableAnimals) {
          if (tamableAnimal != this.playerCompanionEntity
              && tamableAnimal.getOwner() == this.getOwner() && buffLivingEntity(tamableAnimal)) {
            hasBuffSomething = true;
            break;
          }
        }
      }

      // Increase experience if we have buff something (server-side)
      if (hasBuffSomething) {
        distributeExperience(1);
      }

      ticker = 0;
    }
  }

  public boolean buffLivingEntity(LivingEntity livingEntity) {
    if (!livingEntity.isAlive()) {
      return false;
    }

    boolean level1Buff = false;
    boolean level5Buff = false;
    boolean level10Buff = false;
    boolean level20Buff = false;

    // Level 1
    level1Buff = buffLivingEntityDamageResistance(livingEntity);

    // Level 5
    if (getExperienceLevel() >= 5) {
      level5Buff = buffLivingEntityDamageBoost(livingEntity);
    }

    // Level 10 tbd

    // Level 20 tbd
    if (getExperienceLevel() >= 20) {
      level20Buff = buffLivingEntityFireResistance(livingEntity);
    }

    // Level 30 tbd

    // Level 40 tbd

    // Level 50 tbd

    // Level 60 tbd

    return level1Buff || level5Buff || level10Buff || level20Buff;
  }

  public boolean buffLivingEntityDamageBoost(LivingEntity livingEntity) {
    if (COMMON.supporterTypeDamageBoostDuration.get() > 0 && !livingEntity.hasEffect(
        MobEffects.DAMAGE_BOOST)) {
      livingEntity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,
          COMMON.supporterTypeDamageBoostDuration.get(), 0, false, false, true));
      return true;
    }
    return false;
  }

  public boolean buffLivingEntityDamageResistance(LivingEntity livingEntity) {
    if (COMMON.supporterTypeDamageResistanceDuration.get() > 0
        && !livingEntity.hasEffect(MobEffects.DAMAGE_RESISTANCE)) {
      livingEntity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,
          COMMON.supporterTypeDamageResistanceDuration.get(), 0, false, false, true));
      return true;
    }
    return false;
  }

  public boolean buffLivingEntityFireResistance(LivingEntity livingEntity) {
    if (COMMON.supporterTypeFireResistanceDuration.get() > 0
        && !livingEntity.hasEffect(MobEffects.FIRE_RESISTANCE)) {
      livingEntity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE,
          COMMON.supporterTypeFireResistanceDuration.get(), 0, false, false, true));
      return true;
    }
    return false;
  }

  @Override
  public void tick() {
    super.tick();
    supporterTick();
  }

}
