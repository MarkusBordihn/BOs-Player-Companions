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

package de.markusbordihn.playercompanions.entity.collector;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.config.CommonConfig;
import de.markusbordihn.playercompanions.data.PlayerCompanionData;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.entity.PlayerCompanionType;

@EventBusSubscriber
public class CollectorEntity extends PlayerCompanionEntity implements NeutralMob {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final CommonConfig.Config COMMON = CommonConfig.COMMON;
  private static int collectorTypeRadius = COMMON.collectorTypeRadius.get();

  private static final short COLLECT_TICK = 60;
  private short ticker = 0;

  public static final MobCategory CATEGORY = MobCategory.CREATURE;

  public CollectorEntity(EntityType<? extends CollectorEntity> entityType, Level level) {
    super(entityType, level);
    this.setTame(false);
    this.setCompanionType(PlayerCompanionType.COLLECTOR);
    this.setCompanionTypeIcon(new ItemStack(Items.CHEST));

    // Distribute Ticks along several entities
    this.ticker = (short) this.random.nextInt(0, 30);
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

  public static AttributeSupplier.Builder createAttributes() {
    return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3F)
        .add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.ATTACK_DAMAGE, 1.0D);
  }

  @Override
  public void tick() {
    super.tick();

    // Automatic collect items in the defined radius
    if (!this.level.isClientSide && collectorTypeRadius > 0 && ticker++ >= COLLECT_TICK) {
      List<ItemEntity> itemEntities = this.level.getEntities(EntityType.ITEM,
          new AABB(this.blockPosition()).inflate(collectorTypeRadius), entity -> true);
      if (!itemEntities.isEmpty()) {
        PlayerCompanionData companionData = getData();
        if (companionData != null) {
          for (ItemEntity itemEntity : itemEntities) {
            if (itemEntity.isAlive() && companionData.storeInventoryItem(itemEntity.getItem())) {
              itemEntity.remove(RemovalReason.DISCARDED);
            }
          }
        }
      }
      ticker = 0;
    }
  }

}
