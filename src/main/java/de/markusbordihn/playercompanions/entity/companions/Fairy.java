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

package de.markusbordihn.playercompanions.entity.companions;

import java.util.Map;
import com.google.common.collect.Maps;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.Util;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.entity.PlayerCompanionNames;
import de.markusbordihn.playercompanions.entity.PlayerCompanionVariant;
import de.markusbordihn.playercompanions.entity.ai.goal.AvoidCreeperGoal;
import de.markusbordihn.playercompanions.entity.ai.goal.FleeGoal;
import de.markusbordihn.playercompanions.entity.ai.goal.MoveToPositionGoal;
import de.markusbordihn.playercompanions.entity.ai.goal.RandomFlyAroundGoal;
import de.markusbordihn.playercompanions.entity.type.healer.HealerEntityFlyingAround;
import de.markusbordihn.playercompanions.item.ModItems;
import de.markusbordihn.playercompanions.sounds.ModSoundEvents;

public class Fairy extends HealerEntityFlyingAround {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  // General Information
  public static final String ID = "fairy";
  public static final String NAME = "Fairy";
  public static final Ingredient FOOD_ITEMS = Ingredient.of(Items.CAKE, Items.COOKIE);

  // Entity texture by variant
  private static final Map<PlayerCompanionVariant, ResourceLocation> TEXTURE_BY_VARIANT =
      Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(PlayerCompanionVariant.DEFAULT,
            new ResourceLocation(Constants.MOD_ID, "textures/entity/fairy/fairy_default.png"));
        hashMap.put(PlayerCompanionVariant.BLUE,
            new ResourceLocation(Constants.MOD_ID, "textures/entity/fairy/fairy_blue.png"));
        hashMap.put(PlayerCompanionVariant.RED,
            new ResourceLocation(Constants.MOD_ID, "textures/entity/fairy/fairy_red.png"));
      });

  // Companion Item by variant
  private static final Map<PlayerCompanionVariant, Item> COMPANION_ITEM_BY_VARIANT =
      Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(PlayerCompanionVariant.DEFAULT, ModItems.FAIRY_DEFAULT.get());
        hashMap.put(PlayerCompanionVariant.BLUE, ModItems.FAIRY_BLUE.get());
        hashMap.put(PlayerCompanionVariant.RED, ModItems.FAIRY_RED.get());
      });

  public Fairy(EntityType<? extends PlayerCompanionEntity> entityType, Level level) {
    super(entityType, level, TEXTURE_BY_VARIANT, COMPANION_ITEM_BY_VARIANT);
  }

  public static AttributeSupplier.Builder createAttributes() {
    return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.4F)
        .add(Attributes.FLYING_SPEED, 0.4F).add(Attributes.MAX_HEALTH, 10.0D)
        .add(Attributes.ATTACK_DAMAGE, 1.0D);
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();

    this.goalSelector.addGoal(0, new FloatGoal(this));
    this.goalSelector.addGoal(1, new FleeGoal(this, 1.0D));
    this.goalSelector.addGoal(1, new PanicGoal(this, 1.0D));
    this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
    this.goalSelector.addGoal(1, new MoveToPositionGoal(this, 1.0D, 0.5F));
    this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
    this.goalSelector.addGoal(2, new AvoidCreeperGoal(this));
    this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.0D, 8.0F, 1.0F, true));
    this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D));
    this.goalSelector.addGoal(4, new RandomFlyAroundGoal(this));
    this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
  }

  @Override
  protected PathNavigation createNavigation(Level level) {
    FlyingPathNavigation flyingPathNavigation = new FlyingPathNavigation(this, level);
    flyingPathNavigation.setCanOpenDoors(true);
    flyingPathNavigation.setCanFloat(true);
    flyingPathNavigation.setCanPassDoors(true);
    return flyingPathNavigation;
  }

  @Override
  public void finalizeSpawn() {
    super.finalizeSpawn();

    // Give default item, if hand is empty.
    ItemStack itemStack = this.getItemBySlot(EquipmentSlot.MAINHAND);
    if (itemStack != null && itemStack.isEmpty()) {
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.POTION));
    }
  }

  @Override
  public Item getTameItem() {
    return ModItems.TAME_CAKE.get();
  }

  @Override
  public Ingredient getFoodItems() {
    return FOOD_ITEMS;
  }

  @Override
  public String getRandomName() {
    return PlayerCompanionNames.getRandomFemaleCompanionName();
  }

  @Override
  public ParticleOptions getParticleType() {
    return ParticleTypes.END_ROD;
  }

  @Override
  public SoundEvent getPetSound() {
    return ModSoundEvents.COMPANION_FAIRY_HAGGLE.get();
  }

  @Override
  public SoundEvent getAmbientSound() {
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
  public float getSoundVolume() {
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
  public boolean canSitOnShoulder() {
    return this.hasRideCooldown();
  }

  @Override
  public int getEntityGuiScaling() {
    return 65;
  }

  @Override
  public void onMainHandItemSlotChange(ItemStack itemStack) {
    this.setGlowInTheDark(itemStack.is(Items.TORCH));
  }

  @Override
  public void tick() {
    super.tick();
    this.increaseRideCooldownCounter();
  }

}
