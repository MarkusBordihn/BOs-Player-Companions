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

package de.markusbordihn.playercompanions.entity.guard;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.config.CommonConfig;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.item.ModItems;

@EventBusSubscriber
public class SmallGhast extends GuardEntity {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final CommonConfig.Config COMMON = CommonConfig.COMMON;

  // General Information
  public static final String ID = "small_ghast";
  public static final String NAME = "Small Ghast";
  public static final EntityDimensions entityDimensions = new EntityDimensions(0.675f, 0.9f, false);

  // Config settings
  private static int explosionPower = COMMON.smallGhastExplosionPower.get();

  public SmallGhast(EntityType<? extends GuardEntity> entityType, Level level) {
    super(entityType, level);
    flying(true);
  }

  @SubscribeEvent
  public static void handleServerAboutToStartEvent(ServerAboutToStartEvent event) {
    explosionPower = COMMON.smallGhastExplosionPower.get();
  }

  public int getExplosionPower() {
    return explosionPower;
  }

  class SmallGhastShootFireballGoal extends Goal {

    private int chargeTime;

    public boolean canUse() {
      return SmallGhast.this.getTarget() != null;
    }

    @Override
    public void start() {
      this.chargeTime = 0;
    }

    @Override
    public void stop() {
       SmallGhast.this.setCharging(false);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
      return true;
    }

    @Override
    public void tick() {
      LivingEntity livingEntity =  SmallGhast.this.getTarget();
      if (livingEntity != null) {
        // Ignore entities from the same Owner.
        if (livingEntity instanceof PlayerCompanionEntity companionEntity
            && companionEntity.getOwner() ==  SmallGhast.this.getOwner()) {
           SmallGhast.this.setTarget(null);
          return;
        }

        // Only shot if we have a line of sight.
        if (livingEntity.distanceToSqr( SmallGhast.this) < 4096.0D
            &&  SmallGhast.this.hasLineOfSight(livingEntity)) {
          Level level =  SmallGhast.this.level;
          ++this.chargeTime;
          if (this.chargeTime == 10 && ! SmallGhast.this.isSilent()) {
            level.levelEvent((Player) null, 1015,  SmallGhast.this.blockPosition(), 0);
          }

          if (this.chargeTime == 20) {
            Vec3 vec3 =  SmallGhast.this.getViewVector(1.0F);
            double d2 = livingEntity.getX() - ( SmallGhast.this.getX() + vec3.x * 4.0D);
            double d3 = livingEntity.getY(0.5D) - (0.5D +  SmallGhast.this.getY(0.5D));
            double d4 = livingEntity.getZ() - ( SmallGhast.this.getZ() + vec3.z * 4.0D);
            if (! SmallGhast.this.isSilent()) {
              level.levelEvent((Player) null, 1016,  SmallGhast.this.blockPosition(), 0);
            }

            LargeFireball largeFireball = new LargeFireball(level,  SmallGhast.this, d2, d3, d4,
                 SmallGhast.this.getExplosionPower());
            largeFireball.setPos( SmallGhast.this.getX() + vec3.x * 4.0D,
                 SmallGhast.this.getY(0.5D) + 0.5D, largeFireball.getZ() + vec3.z * 4.0D);
            level.addFreshEntity(largeFireball);
            this.chargeTime = -40;
          }
        } else if (this.chargeTime > 0) {
          --this.chargeTime;
        }
         SmallGhast.this.setCharging(this.chargeTime > 10);
      }
    }
  }

  public static boolean checkGhastSpawnRules(EntityType<SmallGhast> entityType, LevelAccessor level,
      MobSpawnType mobSpawnType, BlockPos blockPos, Random random) {
    return level.getDifficulty() != Difficulty.PEACEFUL
        && checkMobSpawnRules(entityType, level, mobSpawnType, blockPos, random);
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(7, new SmallGhast.SmallGhastShootFireballGoal());
  }

  @Override
  public Item getTameItem() {
    return ModItems.TAME_BONE.get();
  }

  @Override
  protected Ingredient getFoodItems() {
    return Ingredient.of(Items.BONE);
  }

  @Override
  public Item getCompanionItem() {
    return ModItems.SMALL_GHAST.get();
  }

  @Override
  public SoundEvent getPetSound() {
    return SoundEvents.GHAST_AMBIENT;
  }


  @Override
  public SoundSource getSoundSource() {
    return SoundSource.HOSTILE;
  }

  @Override
  protected SoundEvent getAmbientSound() {
    return SoundEvents.GHAST_AMBIENT;
  }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSource) {
    return SoundEvents.GHAST_HURT;
  }

  @Override
  protected SoundEvent getDeathSound() {
    return SoundEvents.GHAST_DEATH;
  }

  @Override
  public Vec3 getLeashOffset() {
    return new Vec3(0.0D, 0.72F * this.getEyeHeight(), this.getBbWidth() * 0.4F);
  }

  @Override
  public float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
    return 1.45F;
  }

  @Override
  public float getScale() {
    return 0.75F;
  }

  @Override
  public EntityDimensions getDimensions(Pose pose) {
    return new EntityDimensions(0.75f, 1.70f, false);
  }

}
