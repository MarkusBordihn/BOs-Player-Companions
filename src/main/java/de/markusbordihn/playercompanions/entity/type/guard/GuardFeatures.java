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

package de.markusbordihn.playercompanions.entity.type.guard;

import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.entity.PlayerCompanionsFeatures;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class GuardFeatures extends PlayerCompanionsFeatures {

  private static final short GUARD_TICK = 20 * 60;

  public GuardFeatures(PlayerCompanionEntity playerCompanionEntity, Level level) {
    super(playerCompanionEntity, level);
  }

  private void guardTick() {
    if (!level.isClientSide && ticker++ >= GUARD_TICK) {
      ticker = 0;
    }
  }

  @Override
  public void aiStep() {
    if (!this.level.isClientSide && this.neutralMob != null) {
      this.neutralMob.updatePersistentAnger((ServerLevel) this.level, true);
    }
  }

  @Override
  public void tick() {
    super.tick();
    guardTick();
  }
}
