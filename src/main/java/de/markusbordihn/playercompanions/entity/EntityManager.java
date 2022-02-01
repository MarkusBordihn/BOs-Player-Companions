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

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.GameRules;

import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.config.CommonConfig;

@EventBusSubscriber
public class EntityManager {

  public static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public static final CommonConfig.Config COMMON = CommonConfig.COMMON;
  private static boolean friendlyFire = COMMON.friendlyFire.get();
  private static boolean respawnOnDeath = COMMON.respawnOnDeath.get();
  private static int respawnDelay = COMMON.respawnDelay.get();

  private static final ResourceLocation RESPAWN_MESSAGE =
      new ResourceLocation(Constants.MOD_ID, "monster_will_respawn_message");

  protected EntityManager() {}

  @SubscribeEvent
  public static void handleServerAboutToStartEvent(ServerAboutToStartEvent event) {
    respawnOnDeath = COMMON.respawnOnDeath.get();
    event.getServer();
    respawnDelay = COMMON.respawnDelay.get() * (1000 / MinecraftServer.MS_PER_TICK);
    if (respawnOnDeath) {
      log.info("{} will be respawn on death with a {} secs / {} ticks delay.",
          Constants.LOG_ICON_NAME, COMMON.respawnDelay.get(), respawnDelay);
    } else {
      log.warn("{} will NOT respawn on death.", Constants.LOG_ICON_NAME);
    }

    friendlyFire = COMMON.friendlyFire.get();
    if (friendlyFire) {
      log.warn("{} friendly fire is enabled!");
    }
  }

  @SubscribeEvent
  public static void handleLivingAttackEvent(LivingAttackEvent event) {
    LivingEntity entity = event.getEntityLiving();

    // Ignore event if friendly fire is enabled!
    if (friendlyFire || !(entity instanceof CompanionEntity)) {
      return;
    }

    // Prevent attack damage
    if (preventAttack((CompanionEntity) entity, event.getSource(), event.getAmount())) {
      event.setCanceled(true);
    }
  }

  @SubscribeEvent
  public static void handleLivingHurtEvent(LivingHurtEvent event) {
    LivingEntity entity = event.getEntityLiving();

    // Ignore event if friendly fire is enabled!
    if (friendlyFire || !(entity instanceof CompanionEntity)) {
      return;
    }

    // Prevent hurt damage
    if (preventAttack((CompanionEntity) entity, event.getSource(), event.getAmount())) {
      event.setCanceled(true);
    }
  }

  @SubscribeEvent
  public static void handleLivingDeathEvent(LivingDeathEvent event) {
    LivingEntity entity = event.getEntityLiving();

    // Ignore if feature is disabled or if it is another entity type.
    if (!respawnOnDeath || !(entity instanceof CompanionEntity)) {
      return;
    }

    CompanionEntity monsterEntity = (CompanionEntity) entity;
    LivingEntity owner = monsterEntity.getOwner();

    // Protect monster entity in the case it has a valid owner.
    if (monsterEntity.isTame() && owner != null) {
      // Chancel event to avoid dead.
      event.setCanceled(true);
      preventDead(monsterEntity, event.getSource());
    }
  }

  public static void preventDead(CompanionEntity monsterEntity, DamageSource source) {
    LivingEntity owner = monsterEntity.getOwner();

    // Reset angry for neutral mobs, if needed.
    if (monsterEntity instanceof NeutralMob neutralMobInstance && neutralMobInstance.isAngry()) {
      neutralMobInstance.stopBeingAngry();
    }

    // Clear fire, remove effects, unleash and heal.
    monsterEntity.clearFire();
    monsterEntity.dropLeash(true, true);
    monsterEntity.heal(monsterEntity.getMaxHealth());
    monsterEntity.removeAllEffects();
    monsterEntity.setHealth(monsterEntity.getMaxHealth());

    // Reset target from damage source.
    Entity damageSourceEntity = source.getEntity();
    if (damageSourceEntity != null && damageSourceEntity.isAlive()
        && damageSourceEntity instanceof Mob mobInstance
        && mobInstance.getTarget() == monsterEntity) {
      mobInstance.setTarget(null);
    }

    // Reset target from last damaging mobs, if different from damage source.
    LivingEntity damageSourceMob = monsterEntity.getLastHurtByMob();
    if (damageSourceMob != null && damageSourceMob != damageSourceEntity
        && damageSourceMob.isAlive() && damageSourceMob instanceof Mob mobInstance
        && mobInstance.getTarget() == monsterEntity) {
      mobInstance.setTarget(null);
    }

    // Set respawn timer, if needed.
    if (respawnDelay > 1) {
      monsterEntity.setRespawnTimer(respawnDelay);
    }

    // Teleport near the owner if in the same world!
    if (monsterEntity.level.equals(owner.level)) {
      BlockPos pos = owner.blockPosition();
      monsterEntity.teleportTo(pos.getX(), pos.getY(), pos.getZ());
    }

    // Send death message to the owner.
    net.minecraft.network.chat.Component deathMessage =
        monsterEntity.getCombatTracker().getDeathMessage();
    if (!monsterEntity.level.isClientSide
        && monsterEntity.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES)
        && owner instanceof ServerPlayer) {
      owner.sendMessage(deathMessage, Util.NIL_UUID);
      if (respawnOnDeath) {
        owner.sendMessage(
            new TranslatableComponent(Util.makeDescriptionId("entity", RESPAWN_MESSAGE),
                monsterEntity.getCustomCompanionName(), respawnDelay),
            Util.NIL_UUID);
      }
    }
  }

  public static boolean preventAttack(CompanionEntity monsterEntity, DamageSource source,
      float damage) {
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
    if (owner != null && damageEntity instanceof TamableAnimal tamableAnimal
        && tamableAnimal.getOwner() != null && owner.equals(tamableAnimal.getOwner())) {
      return true;
    }

    return false;
  }
}
