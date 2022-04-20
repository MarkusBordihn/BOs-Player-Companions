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

import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.entity.ai.goal.AvoidCreeperGoal;
import de.markusbordihn.playercompanions.entity.ai.goal.MoveToPositionGoal;
import de.markusbordihn.playercompanions.entity.type.follower.FollowerEntityFlying;
import de.markusbordihn.playercompanions.item.ModItems;

public class Firefly extends FollowerEntityFlying {

  // General Information
  public static final String ID = "firefly";
  public static final String NAME = "firefly";
  public static final Ingredient FOOD_ITEMS = Ingredient.of(Items.HONEYCOMB);

  // Variants
  public static final String DEFAULT_VARIANT = "default";

  // Entity texture by color
  private static final Map<String, ResourceLocation> TEXTURE_BY_VARIANT =
      Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(DEFAULT_VARIANT,
            new ResourceLocation(Constants.MOD_ID, "textures/entity/firefly/firefly.png"));
      });

  // Companion Item by variant
  private static final Map<String, Item> COMPANION_ITEM_BY_VARIANT =
      Util.make(Maps.newHashMap(), hashMap -> {
        hashMap.put(DEFAULT_VARIANT, ModItems.FIREFLY_DEFAULT.get());
      });

  public Firefly(EntityType<? extends PlayerCompanionEntity> entityType, Level level) {
    super(entityType, level);
    setGlowInTheDark(true);
  }

  public static AttributeSupplier.Builder createAttributes() {
    return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3F)
        .add(Attributes.FLYING_SPEED, 0.4F).add(Attributes.MAX_HEALTH, 16.0D)
        .add(Attributes.ATTACK_DAMAGE, 0.0D);
  }

  public ResourceLocation getResourceLocation() {
    if (!this.hasTextureCache()) {
      this.setTextureCache(TEXTURE_BY_VARIANT.getOrDefault(this.getVariant(),
          TEXTURE_BY_VARIANT.get(DEFAULT_VARIANT)));
    }
    return this.getTextureCache();
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
  protected void registerGoals() {
    super.registerGoals();

    this.goalSelector.addGoal(1, new FloatGoal(this));
    this.goalSelector.addGoal(1, new PanicGoal(this, 1.0D));
    this.goalSelector.addGoal(1, new MoveToPositionGoal(this, 1.0D, 0.5F));
    this.goalSelector.addGoal(2, new AvoidCreeperGoal(this));
    this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
    this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
    this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 6.0F, 2.0F, false));
    this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
    this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
  }

  @Override
  public Item getTameItem() {
    return ModItems.TAME_HONEYCOMP.get();
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
  public Vec3 getLeashOffset() {
    return new Vec3(0.0D, 0.2F * this.getEyeHeight(), this.getBbWidth() * 0.4F);
  }

  @Override
  public float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
    return 0.12F;
  }

  @Override
  public int getEntityGuiScaling() {
    return 90;
  }

  @Override
  public int getEntityGuiTop() {
    return 0;
  }

}
