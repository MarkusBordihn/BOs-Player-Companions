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

package de.markusbordihn.playercompanions.entity.type.collector;

import java.util.List;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.config.CommonConfig;
import de.markusbordihn.playercompanions.data.PlayerCompanionData;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.entity.PlayerCompanionsFeatures;

@EventBusSubscriber
public class CollectorFeatures extends PlayerCompanionsFeatures {

  private static final CommonConfig.Config COMMON = CommonConfig.COMMON;
  private static int collectorTypeRadius = COMMON.collectorTypeRadius.get();

  private static final short COLLECT_TICK = 20 * 3;

  public CollectorFeatures(PlayerCompanionEntity playerCompanionEntity, Level level) {
    super(playerCompanionEntity, level);
  }

  @SubscribeEvent
  public static void handleServerAboutToStartEvent(ServerAboutToStartEvent event) {
    collectorTypeRadius = COMMON.collectorTypeRadius.get();
    if (collectorTypeRadius > 0) {
      log.info("{} Collector will automatically collect items in a {} block radius.",
          Constants.LOG_ICON, collectorTypeRadius);
    } else {
      log.info("{} Collector will not automatically collect items!", Constants.LOG_ICON);
    }
  }

  private void collectorTick() {
    // Automatic collect items in the defined radius
    if (!this.level.isClientSide && collectorTypeRadius > 0 && ticker++ >= COLLECT_TICK) {
      List<ItemEntity> itemEntities = this.level.getEntities(EntityType.ITEM,
          new AABB(playerCompanionEntity.blockPosition()).inflate(collectorTypeRadius),
          entity -> true);
      if (!itemEntities.isEmpty()) {
        PlayerCompanionData companionData = playerCompanionEntity.getData();
        if (companionData != null) {
          boolean hasCollectSomething = false;
          for (ItemEntity itemEntity : itemEntities) {
            if (itemEntity.isAlive() && companionData.storeInventoryItem(itemEntity.getItem())) {
              itemEntity.remove(RemovalReason.DISCARDED);
              hasCollectSomething = true;
            }
          }

          // Increase experience, if we collected something (server-side).
          if (hasCollectSomething) {
            distributeExperience(1);
          }
        }
      }
      ticker = 0;
    }
  }

  @Override
  public void tick() {
    super.tick();
    collectorTick();
  }

}
