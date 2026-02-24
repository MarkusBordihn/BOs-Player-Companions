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

package de.markusbordihn.playercompanions.entity.taming;

import de.markusbordihn.easynpc.data.saveddata.NPCEntityData;
import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.config.TamingConfig;
import de.markusbordihn.playercompanions.entity.CompanionRelationship;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

public class TamingHandler {

  private TamingHandler() {
  }

  public static TameResult handleFeeding(
    CompanionRelationship relationship, int trustAmount, Player player, long gameTime) {

    if (!relationship.canInteract(player)) {
      if (relationship.getCooldown() > 0) {
        return TameResult.COOLDOWN;
      }
      if (relationship.getTamingPlayer() != null) {
        return TameResult.WRONG_PLAYER;
      }
    }

    if (relationship.getTamingPlayer() == null) {
      relationship.setTamingPlayer(player.getUUID());
    }

    relationship.addLevel(trustAmount);
    relationship.recordInteraction(gameTime);

    if (relationship.getLevel() >= TamingConfig.TRUST_REQUIRED) {
      return TameResult.TAMED;
    }

    return TameResult.SUCCESS;
  }

  public static boolean hasReachedCompanionLimit(Player player) {
    if (TamingConfig.COMPANION_LIMIT_PER_PLAYER <= 0) {
      return false;
    }

    MinecraftServer server = player.getServer();
    if (server == null) {
      return false;
    }

    NPCEntityData npcData = NPCEntityData.get(server);
    if (npcData == null) {
      return false;
    }

    long companionCount =
      npcData.getEntriesByOwner(player.getUUID()).stream()
        .filter(
          entry ->
            entry.metadata() != null
              && entry.metadata().hasEntityType()
              && entry.metadata().entityType().startsWith(Constants.MOD_ID + ":"))
        .count();

    return companionCount >= TamingConfig.COMPANION_LIMIT_PER_PLAYER;
  }
}
