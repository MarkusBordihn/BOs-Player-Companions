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

package de.markusbordihn.playercompanions.entity.healer;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.config.CommonConfig;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.entity.PlayerCompanionType;

@EventBusSubscriber
public class HealerEntity extends PlayerCompanionEntity implements NeutralMob {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final CommonConfig.Config COMMON = CommonConfig.COMMON;
  private static int healerTypeRadius = COMMON.healerTypeRadius.get();
  private static int healerTypeAmount = COMMON.healerTypeAmount.get();

  private static final short HEALER_TICK = 50;
  private short ticker = 0;

  public static final MobCategory CATEGORY = MobCategory.CREATURE;

  public HealerEntity(EntityType<? extends HealerEntity> entityType, Level level) {
    super(entityType, level);
    this.setTame(false);
    this.setCompanionType(PlayerCompanionType.HEALER);
    this.setCompanionTypeIcon(new ItemStack(Items.POTION));

    // Distribute Ticks along several entities
    this.ticker = (short) this.random.nextInt(0, 25);
  }

  @SubscribeEvent
  public static void handleServerAboutToStartEvent(ServerAboutToStartEvent event) {
    healerTypeRadius = COMMON.healerTypeRadius.get();
    healerTypeAmount = COMMON.healerTypeAmount.get();

    if (healerTypeRadius > 0) {
      log.info("{} Healer will automatically heal {} hp in a {} block radius.", Constants.LOG_ICON,
          healerTypeAmount, healerTypeRadius);
    } else {
      log.info("{} Healer will not automatically heal!", Constants.LOG_ICON);
    }
  }

  public static AttributeSupplier.Builder createAttributes() {
    return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.5F)
        .add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.ATTACK_DAMAGE, 0.5D);
  }

  @Override
  public void tick() {
    super.tick();

    // Automatic heal entities in the defined radius.
    if (!this.level.isClientSide && healerTypeRadius > 0 && ticker++ >= HEALER_TICK) {

      // Heal all players in radius.
      List<Player> playerEntities = this.level.getEntities(EntityType.PLAYER,
          new AABB(this.blockPosition()).inflate(healerTypeRadius), entity -> true);
      for (Player player : playerEntities) {
        if (player.isAlive() && player.getHealth() < player.getMaxHealth()) {
          player.heal(healerTypeAmount);
        }
      }

      // Heal owned player companions.
      if (this.hasOwner()) {
        List<PlayerCompanionEntity> playerCompanions =
            this.level.getEntitiesOfClass(PlayerCompanionEntity.class,
                new AABB(this.blockPosition()).inflate(healerTypeRadius), entity -> true);
        for (PlayerCompanionEntity playerCompanion : playerCompanions) {
          if (playerCompanion.getOwner() == this.getOwner() && playerCompanion.isAlive()
              && playerCompanion.getHealth() < playerCompanion.getMaxHealth()) {
            playerCompanion.heal(healerTypeAmount);
          }
        }
      }

      ticker = 0;
    }
  }

}
