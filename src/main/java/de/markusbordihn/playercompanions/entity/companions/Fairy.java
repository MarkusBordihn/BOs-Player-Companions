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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import com.google.common.collect.Maps;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.Util;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
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
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.entity.ai.goal.AvoidCreeperGoal;
import de.markusbordihn.playercompanions.entity.ai.goal.FleeGoal;
import de.markusbordihn.playercompanions.entity.ai.goal.MoveToPositionGoal;
import de.markusbordihn.playercompanions.entity.ai.goal.RandomFlyAroundGoal;
import de.markusbordihn.playercompanions.entity.type.healer.HealerEntityFlyingAround;
import de.markusbordihn.playercompanions.item.ModItems;
import de.markusbordihn.playercompanions.sounds.ModSoundEvents;
import de.markusbordihn.playercompanions.utils.Names;

public class Fairy extends HealerEntityFlyingAround {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  // General Information
  public static final String ID = "fairy";
  public static final String NAME = "Fairy";
  public static final Ingredient FOOD_ITEMS = Ingredient.of(Items.CAKE, Items.COOKIE);

  // Variants
  public static final String DEFAULT_VARIANT = "default";
  public static final String BLUE_VARIANT = "blue";
  public static final String RED_HAIR_VARIANT = "red_hair";

  // Cache
  private ResourceLocation textureCache = null;

  // Entity texture by color
  private static final Map<String, ResourceLocation> TEXTURE_BY_VARIANT =
      Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(DEFAULT_VARIANT,
            new ResourceLocation(Constants.MOD_ID, "textures/entity/fairy/fairy_default.png"));
        hashMap.put(BLUE_VARIANT,
            new ResourceLocation(Constants.MOD_ID, "textures/entity/fairy/fairy_blue.png"));
        hashMap.put(RED_HAIR_VARIANT,
            new ResourceLocation(Constants.MOD_ID, "textures/entity/fairy/fairy_red_hair.png"));
      });

  // Companion Item by variant
  private static final Map<String, Item> COMPANION_ITEM_BY_VARIANT =
      Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(DEFAULT_VARIANT, ModItems.FAIRY_DEFAULT.get());
        hashMap.put(BLUE_VARIANT, ModItems.FAIRY_BLUE.get());
        hashMap.put(RED_HAIR_VARIANT, ModItems.FAIRY_RED_HAIR.get());
      });

  public Fairy(EntityType<? extends PlayerCompanionEntity> entityType, Level level) {
    super(entityType, level);
  }

  public static AttributeSupplier.Builder createAttributes() {
    return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.4F)
        .add(Attributes.FLYING_SPEED, 0.4F).add(Attributes.MAX_HEALTH, 10.0D)
        .add(Attributes.ATTACK_DAMAGE, 1.0D);
  }

  public ResourceLocation getResourceLocation() {
    if (this.textureCache == null) {
      this.textureCache = TEXTURE_BY_VARIANT.getOrDefault(this.getVariant(),
          TEXTURE_BY_VARIANT.get(DEFAULT_VARIANT));
    }
    return this.textureCache;
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
  @Nullable
  public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor,
      DifficultyInstance difficulty, MobSpawnType mobSpawnType,
      @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
    spawnGroupData = super.finalizeSpawn(serverLevelAccessor, difficulty, mobSpawnType,
        spawnGroupData, compoundTag);
    // Use random different variants for spawn and randomly select one.
    if (this.random.nextInt(2) == 0) {
      List<String> variants = new ArrayList<>(TEXTURE_BY_VARIANT.keySet());
      setVariant(variants.get(this.random.nextInt(variants.size())));
    }
    return spawnGroupData;
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
