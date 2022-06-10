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

package de.markusbordihn.playercompanions.integration;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.entity.type.collector.CollectorEntityFloating;
import de.markusbordihn.playercompanions.entity.type.collector.CollectorEntityWalking;
import de.markusbordihn.playercompanions.entity.type.guard.GuardEntityFloating;
import de.markusbordihn.playercompanions.entity.type.guard.GuardEntityWalking;

@WailaPlugin
public class HwylaPlugin implements IWailaPlugin {

  @Override
  public void registerClient(IWailaClientRegistration registration) {
    // General provider
    registration.registerEntityIcon(PlayerCompanionEntityProvider.INSTANCE,
        PlayerCompanionEntity.class);
    registration.registerEntityComponent(PlayerCompanionEntityProvider.INSTANCE,
        PlayerCompanionEntity.class);

    // Type specific provider
    registration.registerEntityComponent(CollectorEntityProvider.INSTANCE,
        CollectorEntityFloating.class);
    registration.registerEntityComponent(CollectorEntityProvider.INSTANCE,
        CollectorEntityWalking.class);
    registration.registerEntityComponent(GuardEntityProvider.INSTANCE, GuardEntityFloating.class);
    registration.registerEntityComponent(GuardEntityProvider.INSTANCE, GuardEntityWalking.class);
  }

}
