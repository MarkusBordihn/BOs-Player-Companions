/*
 * Copyright 2026 Markus Bordihn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.markusbordihn.playercompanions.commands;

import de.markusbordihn.playercompanions.config.TamingConfig;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CompanionSpawnCooldown {

  public static final long ALIVE_COOLDOWN_TICKS = TamingConfig.SPAWN_COOLDOWN_ALIVE;
  public static final long DEAD_COOLDOWN_TICKS = TamingConfig.SPAWN_COOLDOWN_DEAD;

  private static final Map<UUID, Long> lastSpawnGameTime = new HashMap<>();
  private static final Set<UUID> diedCompanions = new HashSet<>();

  private CompanionSpawnCooldown() {
  }

  public static boolean canSpawn(UUID companionUUID, long currentGameTime) {
    long last = lastSpawnGameTime.getOrDefault(companionUUID, 0L);
    boolean wasDead = diedCompanions.contains(companionUUID);
    long required = wasDead ? DEAD_COOLDOWN_TICKS : ALIVE_COOLDOWN_TICKS;
    return (currentGameTime - last) >= required;
  }

  public static long remainingTicks(UUID companionUUID, long currentGameTime) {
    long last = lastSpawnGameTime.getOrDefault(companionUUID, 0L);
    boolean wasDead = diedCompanions.contains(companionUUID);
    long required = wasDead ? DEAD_COOLDOWN_TICKS : ALIVE_COOLDOWN_TICKS;
    return Math.max(0L, required - (currentGameTime - last));
  }

  public static boolean wasDead(UUID companionUUID) {
    return diedCompanions.contains(companionUUID);
  }

  public static void recordSpawn(UUID companionUUID, long currentGameTime) {
    lastSpawnGameTime.put(companionUUID, currentGameTime);
    diedCompanions.remove(companionUUID);
  }

  public static void recordDeath(UUID companionUUID) {
    diedCompanions.add(companionUUID);
  }

  public static void reduceDeathCooldown(UUID companionUUID, long ticksReduction) {
    if (!diedCompanions.contains(companionUUID)) {
      return;
    }
    lastSpawnGameTime.put(companionUUID,
      lastSpawnGameTime.getOrDefault(companionUUID, 0L) - ticksReduction);
  }
}
