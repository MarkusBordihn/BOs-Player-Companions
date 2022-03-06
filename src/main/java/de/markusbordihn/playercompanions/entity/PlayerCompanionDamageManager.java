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

import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.config.CommonConfig;
import de.markusbordihn.playercompanions.entity.type.PlayerCompanionType;

@EventBusSubscriber
public class PlayerCompanionDamageManager {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final CommonConfig.Config COMMON = CommonConfig.COMMON;
  private static boolean friendlyFire = COMMON.friendlyFire.get();

  protected PlayerCompanionDamageManager() {}

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
    // Ignore event if friendly fire is enabled!
    if (friendlyFire) {
      return;
    }

    // Should we prevent the attack damage ?
    if (event.getEntityLiving() instanceof TamableAnimal tamableAnimal
        && preventAttack(tamableAnimal, event.getSource())) {
      event.setCanceled(true);
    }
  }

  @SubscribeEvent
  public static void handleLivingHurtEvent(LivingHurtEvent event) {
    // Ignore event if friendly fire is enabled!
    if (friendlyFire) {
      return;
    }

    // Should we prevent the attack damage ?
    if (event.getEntityLiving() instanceof TamableAnimal tamableAnimal
        && preventAttack(tamableAnimal, event.getSource())) {
      event.setCanceled(true);
    }
  }

  public static boolean preventAttack(TamableAnimal attackedTamableAnimal, DamageSource source) {
    // Avoid null errors and entities without any owner.
    if (attackedTamableAnimal == null || source == null
        || attackedTamableAnimal.getOwner() == null) {
      return false;
    }
    LivingEntity owner = attackedTamableAnimal.getOwner();
    UUID ownerUUID = owner.getUUID();

    // Avoid damage directly from owner and other players depending on additional conditions.
    Entity damageSourceEntity = source.getEntity();
    if (damageSourceEntity instanceof Player player) {

      // Avoid damage directly from the owner, but sneaking bypass the protection for owned tamed.
      if (player.getUUID().equals(ownerUUID)) {
        return !player.isCrouching();
      }

      // Collector and Follower Player companions could not be hurt by other players.
      if (attackedTamableAnimal instanceof PlayerCompanionEntity playerCompanionEntity
          && (playerCompanionEntity.getCompanionType() == PlayerCompanionType.COLLECTOR
              || playerCompanionEntity.getCompanionType() == PlayerCompanionType.FOLLOWER)) {
        return false;
      }
    }

    // Avoid damage from other tamed animals with the same owner.
    return damageSourceEntity instanceof TamableAnimal tamableAnimal
        && tamableAnimal.getOwner() != null && ownerUUID.equals(tamableAnimal.getOwner().getUUID());
  }
}
