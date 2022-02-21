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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.item.ModItems;

public class Pig extends CollectorEntity {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  // General Information
  public static final String ID = "pig";
  public static final String NAME = "Pig";

  public Pig(EntityType<? extends CollectorEntity> entityType, Level level) {
    super(entityType, level);
  }

  public static AttributeSupplier.Builder createAttributes() {
    return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.2F)
        .add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.ATTACK_DAMAGE, 1.0D);
  }

  @Override
  public Item getTameItem() {
    return ModItems.TAME_CARROT.get();
  }

  @Override
  protected Ingredient getFoodItems() {
    return Ingredient.of(Items.CARROT, Items.POTATO, Items.BEETROOT);
  }

  @Override
  public Item getCompanionItem() {
    return ModItems.PIG.get();
  }

  @Override
  public SoundEvent getPetSound() {
    return SoundEvents.PIG_AMBIENT;
  }

  @Override
  protected SoundEvent getAmbientSound() {
    return SoundEvents.PIG_AMBIENT;
  }

  @Override
  protected SoundEvent getHurtSound(DamageSource damageSource) {
    return SoundEvents.PIG_HURT;
  }

  @Override
  protected SoundEvent getDeathSound() {
    return SoundEvents.PIG_DEATH;
  }

  @Override
  public Vec3 getLeashOffset() {
    return new Vec3(0.0D, 0.55F * this.getEyeHeight(), this.getBbWidth() * 0.4F);
  }

  @Override
  public float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
    return 0.81F;
  }

  @Override
  public EntityDimensions getDimensions(Pose pose) {
    return new EntityDimensions(0.8f, 1.0f, false);
  }

}
