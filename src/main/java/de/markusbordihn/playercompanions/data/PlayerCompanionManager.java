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

package de.markusbordihn.playercompanions.data;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;

@EventBusSubscriber
public class PlayerCompanionManager {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static Set<Entity> entitySet = ConcurrentHashMap.newKeySet();
  private static final short SYNC_TICK = 25;
  private static short ticks = 0;

  protected PlayerCompanionManager() {}

  @SubscribeEvent
  public static void handleServerAboutToStartEvent(ServerAboutToStartEvent event) {
    log.info("{} Player Companion Data Manager ...", Constants.LOG_REGISTER_PREFIX);
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public static void handleEntityJoinWorldEvent(EntityJoinWorldEvent event) {
    updateOrRegisterCompanion(event.getEntity());
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public static void handleEntityTeleportEvent(EntityTeleportEvent event) {
    updateOrRegisterCompanion(event.getEntity());
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public static void handleEntityTravelToDimensionEvent(EntityTravelToDimensionEvent event) {
    updateOrRegisterCompanion(event.getEntity());
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public static void handleEntityLeaveWorldEvent(EntityLeaveWorldEvent event) {
    updateCompanionData(event.getEntity());
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public static void handleLivingDamageEvent(LivingDamageEvent event) {
    scheduleCompanionDataUpdate(event.getEntity());
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public static void handleLivingHurtEvent(LivingHurtEvent event) {
    scheduleCompanionDataUpdate(event.getEntity());
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public static void handleLivingHealEvent(LivingHealEvent event) {
    scheduleCompanionDataUpdate(event.getEntity());
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public static void handleLivingDeathEvent(LivingDeathEvent event) {
    updateCompanionData(event.getEntity());
  }

  @SubscribeEvent
  public static void handleClientServerTickEvent(TickEvent.ServerTickEvent event) {
    if (event.phase == TickEvent.Phase.END && ticks++ >= SYNC_TICK) {
      syncCompanionData();
      ticks = 0;
    }
  }

  @SubscribeEvent
  public static void handlePlayerChangedDimensionEvent(PlayerChangedDimensionEvent event) {
    verifyPlayerCompanionForPlayer(event.getPlayer());
    syncPlayerCompanionsDataToPlayer(event.getPlayer());
  }

  @SubscribeEvent
  public static void handlePlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
    syncPlayerCompanionsDataToPlayer(event.getPlayer());
  }

  private static void scheduleCompanionDataUpdate(Entity entity) {
    if (entity instanceof PlayerCompanionEntity playerCompanionEntity
        && playerCompanionEntity.hasOwner()) {
      entitySet.add(entity);
    }
  }

  private static void syncCompanionData() {
    if (entitySet.isEmpty()) {
      return;
    }
    log.debug("Sync date for {} companions with: {}", entitySet.size(), entitySet);
    Iterator<Entity> entityIterator = entitySet.iterator();
    while (entityIterator.hasNext()) {
      Entity entity = entityIterator.next();
      if (entity != null) {
        updateOrRegisterCompanion(entity);
      }
      entityIterator.remove();
    }
  }

  private static void syncPlayerCompanionsDataToPlayer(Player player) {
    if (player instanceof ServerPlayer serverPlayer) {
      PlayerCompanionsServerData.get().syncPlayerCompanionsData(serverPlayer.getUUID());
    }
  }

  private static void updateOrRegisterCompanion(Entity entity) {
    if (entity instanceof PlayerCompanionEntity playerCompanionEntity
        && !playerCompanionEntity.getLevel().isClientSide && playerCompanionEntity.hasOwner()) {
      log.debug("Update or register Companion {}", entity);
      PlayerCompanionsServerData.get().updateOrRegisterCompanion(playerCompanionEntity);
    }
  }

  private static void updateCompanionData(Entity entity) {
    if (entity instanceof PlayerCompanionEntity playerCompanionEntity
        && !playerCompanionEntity.getLevel().isClientSide && playerCompanionEntity.hasOwner()) {
      log.debug("Update Companion Data {}", entity);
      PlayerCompanionsServerData.get().updatePlayerCompanionData(playerCompanionEntity);
    }
  }

  private static void verifyPlayerCompanionForPlayer(Player player) {
    if (player instanceof ServerPlayer serverPlayer) {
      PlayerCompanionsServerData data = PlayerCompanionsServerData.get();
      if (data == null) {
        return;
      }
      MinecraftServer server = serverPlayer.getServer();
      Iterator<ServerLevel> serverLevels = server.getAllLevels().iterator();

      // Get relevant entities from owners level.
      Set<Entity> playerCompanionsEntityInOwnersDimension =
          data.getCompanionsEntity(player.getUUID(), serverPlayer.getLevel());

      // Check for duplicates from other levels.
      while (serverLevels.hasNext()) {
        ServerLevel serverLevel = serverLevels.next();
        if (serverPlayer.getLevel() != serverLevel) {
          for (Entity playerCompanionEntity : playerCompanionsEntityInOwnersDimension) {
            Entity entity = serverLevel.getEntity(playerCompanionEntity.getUUID());
            if (entity != null) {
              entity.remove(RemovalReason.CHANGED_DIMENSION);
            }
          }
        }
      }

    }
  }

}
