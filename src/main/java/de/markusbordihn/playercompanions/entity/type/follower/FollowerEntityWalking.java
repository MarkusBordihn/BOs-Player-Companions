/*
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

import de.markusbordihn.playercompanions.entity.AggressionLevel;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntityWalking;
import de.markusbordihn.playercompanions.entity.PlayerCompanionVariant;
import de.markusbordihn.playercompanions.entity.type.PlayerCompanionType;
import de.markusbordihn.playercompanions.entity.type.PlayerCompanionTypeIcon;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FollowerEntityWalking extends PlayerCompanionEntityWalking {

  private static final Set<AggressionLevel> AGGRESSION_LEVELS =
      EnumSet.of(AggressionLevel.PASSIVE_FLEE, AggressionLevel.PASSIVE);
  protected FollowerFeatures followerFeatures;

  public FollowerEntityWalking(
      EntityType<? extends PlayerCompanionEntity> entityType, Level level) {
    this(entityType, level, null);
  }

  public FollowerEntityWalking(
      EntityType<? extends PlayerCompanionEntity> entityType,
      Level level,
      Map<PlayerCompanionVariant, Item> companionItemByVariant) {
    super(entityType, level, companionItemByVariant);
    this.setAggressionLevel(getDefaultAggressionLevel());

    // Shared Follower Features
    this.followerFeatures = new FollowerFeatures(this, level);
  }

  @Override
  public Set<AggressionLevel> getAggressionLevels() {
    return AGGRESSION_LEVELS;
  }

  @Override
  public AggressionLevel getDefaultAggressionLevel() {
    return AggressionLevel.PASSIVE;
  }

  @Override
  public AggressionLevel getFirstAggressionLevel() {
    return AggressionLevel.PASSIVE_FLEE;
  }

  @Override
  public AggressionLevel getLastAggressionLevel() {
    return AggressionLevel.PASSIVE;
  }

  @Override
  public PlayerCompanionType getCompanionType() {
    return PlayerCompanionType.FOLLOWER;
  }

  @Override
  public ItemStack getCompanionTypeIcon() {
    return PlayerCompanionTypeIcon.COLLECTOR;
  }

  @Override
  public void tick() {
    super.tick();
    this.followerFeatures.tick();
  }
}
