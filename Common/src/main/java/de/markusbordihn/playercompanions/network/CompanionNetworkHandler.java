/*
 * Copyright 2026 Markus Bordihn
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

package de.markusbordihn.playercompanions.network;

import de.markusbordihn.playercompanions.entity.AggressionLevel;
import de.markusbordihn.playercompanions.entity.CompanionCommand;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CompanionNetworkHandler {

  private static final Logger log = LogManager.getLogger(CompanionNetworkHandler.class);

  private static CompanionNetwork networkHandler;

  private CompanionNetworkHandler() {
  }

  public static void setHandler(CompanionNetwork handler) {
    log.info("Registering companion network handler: {}", handler.getClass().getSimpleName());
    networkHandler = handler;
  }

  public static void sendCompanionCommand(UUID companionUUID, CompanionCommand command) {
    if (networkHandler == null) {
      log.error("No network handler registered — cannot send companion command");
      return;
    }
    networkHandler.sendCompanionCommand(companionUUID, command);
  }

  public static void sendCompanionAggression(UUID companionUUID, AggressionLevel level) {
    if (networkHandler == null) {
      log.error("No network handler registered — cannot send companion aggression");
      return;
    }
    networkHandler.sendCompanionAggression(companionUUID, level);
  }

  public static void sendCollectorActive(UUID companionUUID, boolean active) {
    if (networkHandler == null) {
      log.error("No network handler registered — cannot send collector active");
      return;
    }
    networkHandler.sendCollectorActive(companionUUID, active);
  }

  public static void sendShrineRespawn(UUID companionUUID) {
    if (networkHandler == null) {
      log.error("No network handler registered — cannot send shrine respawn");
      return;
    }
    networkHandler.sendShrineRespawn(companionUUID);
  }

  public static void sendShrineXpReduce(UUID companionUUID) {
    if (networkHandler == null) {
      log.error("No network handler registered — cannot send shrine XP reduce");
      return;
    }
    networkHandler.sendShrineXpReduce(companionUUID);
  }
}
