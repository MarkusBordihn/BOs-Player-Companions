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

package de.markusbordihn.playercompanions.entity;

import de.markusbordihn.playercompanions.entity.type.PlayerCompanionType;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public interface PlayerCompanionAttributes {

  Set<AggressionLevel> aggressionDefaultLevels =
      Collections.unmodifiableSet(EnumSet.allOf(AggressionLevel.class));

  default Set<AggressionLevel> getAggressionLevels() {
    return aggressionDefaultLevels;
  }

  default AggressionLevel getNextAggressionLevel(AggressionLevel aggressionLevel) {
    Iterator<AggressionLevel> iterator = getAggressionLevels().iterator();
    while (iterator.hasNext()) {
      AggressionLevel aggressionLevelCurrent = iterator.next();
      if (aggressionLevel.equals(aggressionLevelCurrent) && iterator.hasNext()) {
        return iterator.next();
      }
    }
    return aggressionLevel;
  }

  default AggressionLevel getPreviousAggressionLevel(AggressionLevel aggressionLevel) {
    Iterator<AggressionLevel> iterator = getAggressionLevels().iterator();
    AggressionLevel aggressionLevelPrevious = null;
    while (iterator.hasNext()) {
      AggressionLevel aggressionLevelCurrent = iterator.next();
      if (aggressionLevel.equals(aggressionLevelCurrent) && aggressionLevelPrevious != null) {
        return aggressionLevelPrevious;
      }
      aggressionLevelPrevious = aggressionLevelCurrent;
    }
    return aggressionLevel;
  }

  default AggressionLevel getDefaultAggressionLevel() {
    return AggressionLevel.NEUTRAL;
  }

  default AggressionLevel getFirstAggressionLevel() {
    Set<AggressionLevel> aggressionLevels = getAggressionLevels();
    if (aggressionLevels != null) {
      Optional<AggressionLevel> aggressionLevel = aggressionLevels.stream().findFirst();
      if (aggressionLevel.isPresent()) {
        return aggressionLevel.get();
      }
    }
    return null;
  }

  default AggressionLevel getLastAggressionLevel() {
    Set<AggressionLevel> aggressionLevels = getAggressionLevels();
    if (aggressionLevels != null) {
      Optional<AggressionLevel> aggressionLevel =
          aggressionLevels.stream().reduce((first, second) -> second);
      if (aggressionLevel.isPresent()) {
        return aggressionLevel.get();
      }
    }
    return null;
  }

  default Item getTameItem() {
    return null;
  }

  default Ingredient getFoodItems() {
    return Ingredient.of(getTameItem());
  }

  default boolean doPlayJumpSound() {
    return true;
  }

  default int getEntityGuiScaling() {
    return 60;
  }

  default int getEntityGuiTop() {
    return 20;
  }

  default SoundEvent getPetSound() {
    return SoundEvents.WOLF_WHINE;
  }

  default SoundEvent getJumpSound() {
    return SoundEvents.SLIME_JUMP_SMALL;
  }

  default SoundEvent getWaitSound() {
    return null;
  }

  default ParticleOptions getParticleType() {
    return null;
  }

  default PlayerCompanionType getCompanionType() {
    return PlayerCompanionType.UNKNOWN;
  }

  default ItemStack getCompanionTypeIcon() {
    return null;
  }
}
