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

package de.markusbordihn.playercompanions.entity.type.follower;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import de.markusbordihn.playercompanions.entity.AggressionLevel;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntityFlying;
import de.markusbordihn.playercompanions.entity.type.PlayerCompanionType;

public class FollowerEntityFlying extends PlayerCompanionEntityFlying {

  protected FollowerFeatures followerFeatures;

  public FollowerEntityFlying(EntityType<? extends PlayerCompanionEntity> entityType,
      Level level) {
    super(entityType, level);
    this.setCompanionType(PlayerCompanionType.FOLLOWER);
    this.setCompanionTypeIcon(new ItemStack(Items.CARROT_ON_A_STICK));
    this.setAggressionLevel(AggressionLevel.PASSIVE);

    // Shared Follower Features
    this.followerFeatures = new FollowerFeatures(this, level);
  }

  @Override
  public boolean isSupportedAggressionLevel(AggressionLevel aggressionLevel) {
    return aggressionLevel == AggressionLevel.PASSIVE_FLEE
        || aggressionLevel == AggressionLevel.PASSIVE;
  }

  @Override
  public void tick() {
    super.tick();
    this.followerFeatures.tick();
  }

}