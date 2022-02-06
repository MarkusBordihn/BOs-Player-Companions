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

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.entity.CompanionEntity;

@EventBusSubscriber
public class PlayerCompanionManager {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static short ticks = 0;
  private static final short SYNC_TICK = 25;
  private static Set<Entity> entitySet = ConcurrentHashMap.newKeySet();

  protected PlayerCompanionManager() {}

  @SubscribeEvent
  public static void handleServerAboutToStartEvent(ServerAboutToStartEvent event) {
    log.info("Loaded Companion Manager ...");
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void handleEntityJoinWorldEvent(EntityJoinWorldEvent event) {
    updateOrRegisterCompanion(event.getEntity());
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void handleEntityTeleportEvent(EntityTeleportEvent event) {
    updateOrRegisterCompanion(event.getEntity());
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void handleEntityTravelToDimensionEvent(EntityTravelToDimensionEvent event) {
    updateOrRegisterCompanion(event.getEntity());
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void handleEntityLeaveWorldEvent(EntityLeaveWorldEvent event) {
    updateCompanionData(event.getEntity());
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void handleLivingDamageEvent(LivingDamageEvent event) {
    scheduleCompanionDataUpdate(event.getEntity());
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void handleLivingHurtEvent(LivingHurtEvent event) {
    scheduleCompanionDataUpdate(event.getEntity());
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void handleLivingHealEvent(LivingHealEvent event) {
    scheduleCompanionDataUpdate(event.getEntity());
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
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

  private static void scheduleCompanionDataUpdate(Entity entity) {
    entitySet.add(entity);
  }

  private static void syncCompanionData() {
    if (entitySet.isEmpty()) {
      return;
    }
    log.debug("Sync {} companion data ...", entitySet.size());
    Iterator<Entity> entityIterator = entitySet.iterator();
    while (entityIterator.hasNext()) {
      Entity entity = entityIterator.next();
      if (entity != null) {
        updateOrRegisterCompanion(entity);
      }
      entityIterator.remove();
    }
  }

  private static void updateOrRegisterCompanion(Entity entity) {
    if (entity instanceof CompanionEntity companionEntity
        && !companionEntity.getLevel().isClientSide) {
      PlayerCompanionsServerData.get().updateOrRegisterCompanion(companionEntity);
    }
  }

  private static void updateCompanionData(Entity entity) {
    if (entity instanceof CompanionEntity companionEntity
        && !companionEntity.getLevel().isClientSide) {
      PlayerCompanionsServerData.get().updatePlayerCompanionData(companionEntity);
    }
  }

}
