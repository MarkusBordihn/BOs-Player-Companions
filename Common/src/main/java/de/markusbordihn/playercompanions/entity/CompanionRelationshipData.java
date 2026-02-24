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

package de.markusbordihn.playercompanions.entity;

import java.util.UUID;
import net.minecraft.nbt.CompoundTag;

public record CompanionRelationshipData(int level, int cooldown, UUID tamingPlayer,
                                        long lastInteraction) {

  public static final CompanionRelationshipData EMPTY =
    new CompanionRelationshipData(0, 0, null, 0L);
  private static final String LEVEL_TAG = "Level";
  private static final String COOLDOWN_TAG = "Cooldown";
  private static final String TAMING_PLAYER_TAG = "TamingPlayer";
  private static final String LAST_INTERACTION_TAG = "LastInteraction";

  public static CompanionRelationshipData load(CompoundTag tag) {
    int level = tag.contains(LEVEL_TAG) ? tag.getInt(LEVEL_TAG) : 0;
    int cooldown = tag.contains(COOLDOWN_TAG) ? tag.getInt(COOLDOWN_TAG) : 0;
    UUID tamingPlayer = tag.contains(TAMING_PLAYER_TAG) ? tag.getUUID(TAMING_PLAYER_TAG) : null;
    long lastInteraction =
      tag.contains(LAST_INTERACTION_TAG) ? tag.getLong(LAST_INTERACTION_TAG) : 0L;
    return new CompanionRelationshipData(level, cooldown, tamingPlayer, lastInteraction);
  }

  public CompanionRelationshipData withLevel(int newLevel) {
    return new CompanionRelationshipData(
      Math.max(0, Math.min(100, newLevel)), cooldown, tamingPlayer, lastInteraction);
  }

  public CompanionRelationshipData withCooldown(int newCooldown) {
    return new CompanionRelationshipData(level, Math.max(0, newCooldown), tamingPlayer,
      lastInteraction);
  }

  public CompanionRelationshipData withTamingPlayer(UUID player) {
    return new CompanionRelationshipData(level, cooldown, player, lastInteraction);
  }

  public CompanionRelationshipData withLastInteraction(long gameTime) {
    return new CompanionRelationshipData(level, cooldown, tamingPlayer, gameTime);
  }

  public CompanionRelationshipData reset() {
    return EMPTY;
  }

  public CompanionRelationshipData tick() {
    if (cooldown > 0) {
      return withCooldown(cooldown - 1);
    }
    return this;
  }

  public void save(CompoundTag tag) {
    tag.putInt(LEVEL_TAG, level);
    tag.putInt(COOLDOWN_TAG, cooldown);
    if (tamingPlayer != null) {
      tag.putUUID(TAMING_PLAYER_TAG, tamingPlayer);
    }
    tag.putLong(LAST_INTERACTION_TAG, lastInteraction);
  }
}
