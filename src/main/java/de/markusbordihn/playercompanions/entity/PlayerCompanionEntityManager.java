/**
 * Copyright 2021 Markus Bordihn
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;

import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.config.CommonConfig;

@EventBusSubscriber
public class PlayerCompanionEntityManager {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final CommonConfig.Config COMMON = CommonConfig.COMMON;
  private static boolean friendlyFire = COMMON.friendlyFire.get();

  protected PlayerCompanionEntityManager() {}

  @SubscribeEvent
  public static void handleServerAboutToStartEvent(ServerAboutToStartEvent event) {
    friendlyFire = COMMON.friendlyFire.get();
    if (friendlyFire) {
      log.warn("{} friendly fire is enabled!", Constants.LOG_ICON_NAME);
    } else {
      log.info("{} friendly fire is disabled.", Constants.LOG_ICON_NAME);
    }
  }

  @SubscribeEvent
  public static void handleLivingAttackEvent(LivingAttackEvent event) {
    LivingEntity entity = event.getEntityLiving();

    // Ignore event if friendly fire is enabled!
    if (friendlyFire || !(entity instanceof PlayerCompanionEntity)) {
      return;
    }

    // Prevent attack damage
    if (preventAttack((PlayerCompanionEntity) entity, event.getSource())) {
      event.setCanceled(true);
    }
  }

  @SubscribeEvent
  public static void handleLivingHurtEvent(LivingHurtEvent event) {
    LivingEntity entity = event.getEntityLiving();

    // Ignore event if friendly fire is enabled!
    if (friendlyFire || !(entity instanceof PlayerCompanionEntity)) {
      return;
    }

    // Prevent hurt damage
    if (preventAttack((PlayerCompanionEntity) entity, event.getSource())) {
      event.setCanceled(true);
    }
  }

  public static boolean preventAttack(PlayerCompanionEntity monsterEntity, DamageSource source) {
    // Sneaking will bypass the protection.
    if (monsterEntity == null || source == null || monsterEntity.getOwner() == null
        || source.getEntity() == null || source.getEntity().isCrouching()) {
      return false;
    }

    Entity damageEntity = source.getEntity();
    LivingEntity owner = monsterEntity.getOwner();

    // Avoid damage directly from the owner.
    if (owner != null && owner.equals(damageEntity)) {
      return true;
    }

    // Avoid damage from other tamed animals.
    return owner != null && damageEntity instanceof TamableAnimal tamableAnimal
        && tamableAnimal.getOwner() != null && owner.equals(tamableAnimal.getOwner());
  }
}
