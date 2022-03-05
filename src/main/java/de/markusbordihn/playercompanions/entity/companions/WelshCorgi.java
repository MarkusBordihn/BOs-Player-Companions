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

package de.markusbordihn.playercompanions.entity.companions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.entity.ai.goal.MoveToPositionGoal;
import de.markusbordihn.playercompanions.item.ModItems;

public class WelshCorgi extends PlayerCompanionEntity {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  // General Information
  public static final String ID = "welsh_corgi";
  public static final String NAME = "welsh_corgi";

  public WelshCorgi(EntityType<? extends PlayerCompanionEntity> entityType, Level level) {
    super(entityType, level);
  }

  @Override
  protected void registerGoals() {
    super.registerGoals();

    this.goalSelector.addGoal(1, new FloatGoal(this));
    this.goalSelector.addGoal(1, new MoveToPositionGoal(this, 1.0D, 0.5F));
    this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
    this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
    this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0D, 6.0F, 2.0F, false));
    this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
    this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
  }

  @Override
  public Item getTameItem() {
    return ModItems.TAME_BONE.get();
  }

  @Override
  public Ingredient getFoodItems() {
    return Ingredient.of(Items.BONE);
  }

  @Override
  public Item getCompanionItem() {
    return ModItems.WELSH_CORGI.get();
  }

  @Override
  public Vec3 getLeashOffset() {
    return new Vec3(0.0D, 0.7F * this.getEyeHeight(), this.getBbWidth() * 0.7F);
  }

  @Override
  public float getStandingEyeHeight(Pose pose, EntityDimensions entityDimensions) {
    return 0.9F;
  }

}
