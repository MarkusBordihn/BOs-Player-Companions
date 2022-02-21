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

package de.markusbordihn.playercompanions.entity.healer;

import java.util.EnumSet;
import java.util.Map;

import com.google.common.collect.Maps;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.item.ModItems;
import de.markusbordihn.playercompanions.sounds.ModSoundEvents;
import de.markusbordihn.playercompanions.utils.Names;

public class Fairy extends HealerEntity {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  // General Information
  public static final String ID = "fairy";
  public static final String NAME = "Fairy";

  // Variants
  public static final String DEFAULT_VARIANT = "default";

  // Entity texture by color
  private static final Map<String, ResourceLocation> TEXTURE_BY_VARIANT =
      Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(DEFAULT_VARIANT,
            new ResourceLocation(Constants.MOD_ID, "textures/entity/fairy/fairy_default.png"));
      });

  // Companion Item by variant
  private static final Map<String, Item> COMPANION_ITEM_BY_VARIANT =
      Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(DEFAULT_VARIANT, ModItems.FAIRY_DEFAULT.get());
      });

  public Fairy(EntityType<? extends HealerEntity> entityType, Level level) {
    super(entityType, level);
    flying(true);
    flyingAround(true);
  }

  public ResourceLocation getResourceLocation() {
    return TEXTURE_BY_VARIANT.getOrDefault(this.getVariant(),
        TEXTURE_BY_VARIANT.get(DEFAULT_VARIANT));
  }

  class FairyRandomMoveGoal extends Goal {

    public FairyRandomMoveGoal() {
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    public boolean canUse() {
      return !Fairy.this.getMoveControl().hasWanted()
          && Fairy.this.random.nextInt(reducedTickDelay(7)) == 0;
    }

    @Override
    public boolean canContinueToUse() {
      return false;
    }

    @Override
    public void tick() {
      if (Fairy.this.isOrderedToSit()) {
        return;
      }

      BlockPos blockPos = Fairy.this.ownerBlockPosition();
      for (int i = 0; i < 3; ++i) {
        BlockPos randomBlockPos = blockPos.offset(Fairy.this.random.nextInt(12) - 6,
            Fairy.this.random.nextInt(12) - 6, Fairy.this.random.nextInt(12) - 6);
        if (Fairy.this.level.isEmptyBlock(randomBlockPos)) {
          Fairy.this.moveControl.setWantedPosition(randomBlockPos.getX() + 0.5D,
              randomBlockPos.getY() + 0.5D, randomBlockPos.getZ() + 0.5D, 0.25D);
          if (Fairy.this.getTarget() == null) {
            Fairy.this.getLookControl().setLookAt(randomBlockPos.getX() + 0.5D,
                randomBlockPos.getY() + 0.5D, randomBlockPos.getZ() + 0.5D, 180.0F, 20.0F);
          }
          break;
        }
      }

    }
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();
    this.goalSelector.addGoal(8, new Fairy.FairyRandomMoveGoal());
  }

  @Override
  public Item getTameItem() {
    return ModItems.TAME_CAKE.get();
  }

  @Override
  protected Ingredient getFoodItems() {
    return Ingredient.of(Items.CAKE, Items.COOKIE);
  }

  @Override
  public Item getCompanionItem() {
    return COMPANION_ITEM_BY_VARIANT.get(this.getVariant());
  }

  @Override
  protected String getRandomName() {
    return Names.getRandomFemaleAndMiscMobName();
  }

  @Override
  protected ParticleOptions getParticleType() {
    return ParticleTypes.END_ROD;
  }

  @Override
  public SoundEvent getPetSound() {
    return ModSoundEvents.COMPANION_FAIRY_HAGGLE.get();
  }

  @Override
  protected SoundEvent getAmbientSound() {
    return ModSoundEvents.COMPANION_FAIRY_AMBIENT.get();
  }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSource) {
    return ModSoundEvents.COMPANION_FAIRY_HURT.get();
  }

  @Override
  protected SoundEvent getDeathSound() {
    return ModSoundEvents.COMPANION_FAIRY_DEATH.get();
  }

  @Override
  protected float getSoundVolume() {
    return 0.4F;
  }

  @Override
  public Vec3 getLeashOffset() {
    return new Vec3(0D, 0.86F * this.getEyeHeight(), this.getBbWidth() * 0.15F);
  }

  @Override
  public float getScale() {
    return 0.4F;
  }

  @Override
  public EntityDimensions getDimensions(Pose pose) {
    return new EntityDimensions(0.4f, 0.8f, false);
  }

}
