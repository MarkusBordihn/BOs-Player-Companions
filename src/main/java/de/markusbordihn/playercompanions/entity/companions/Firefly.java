/*
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

import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.entity.PlayerCompanionVariant;
import de.markusbordihn.playercompanions.entity.ai.goal.AvoidCreeperGoal;
import de.markusbordihn.playercompanions.entity.ai.goal.MoveToPositionGoal;
import de.markusbordihn.playercompanions.entity.type.follower.FollowerEntityFlying;
import de.markusbordihn.playercompanions.item.ModItems;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
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
import net.minecraft.world.phys.Vec3;

public class Firefly extends FollowerEntityFlying {

  // General Information
  public static final String ID = "firefly";
  public static final String NAME = "Firefly";
  public static final Ingredient FOOD_ITEMS = Ingredient.of(Items.HONEYCOMB);

  // Variants
  public static final List<PlayerCompanionVariant> VARIANTS =
      List.of(PlayerCompanionVariant.DEFAULT);

  // Companion Item by variant
  private static final Map<PlayerCompanionVariant, Item> COMPANION_ITEM_BY_VARIANT =
      Util.make(
          new EnumMap<>(PlayerCompanionVariant.class),
          hashMap -> hashMap.put(PlayerCompanionVariant.DEFAULT, ModItems.FIREFLY_DEFAULT.get()));

  public Firefly(EntityType<? extends PlayerCompanionEntity> entityType, Level level) {
    super(entityType, level, COMPANION_ITEM_BY_VARIANT);
    setGlowInTheDark(true);
  }

  public static AttributeSupplier.Builder createAttributes() {
    return Mob.createMobAttributes()
        .add(Attributes.MOVEMENT_SPEED, 0.3F)
        .add(Attributes.FLYING_SPEED, 0.4F)
        .add(Attributes.MAX_HEALTH, 16.0D)
        .add(Attributes.ATTACK_DAMAGE, 0.0D);
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
  public PlayerCompanionVariant getRandomVariant() {
    if (VARIANTS.size() > 1 && this.random.nextInt(2) == 0) {
      return VARIANTS.get(this.random.nextInt(VARIANTS.size()));
    }
    return PlayerCompanionVariant.DEFAULT;
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
