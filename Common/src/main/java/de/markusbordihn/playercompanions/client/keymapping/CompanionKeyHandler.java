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

package de.markusbordihn.playercompanions.client.keymapping;

import com.mojang.blaze3d.platform.InputConstants;
import de.markusbordihn.playercompanions.entity.AggressionLevel;
import de.markusbordihn.playercompanions.entity.CompanionCommand;
import de.markusbordihn.playercompanions.entity.CompanionRole;
import de.markusbordihn.playercompanions.entity.companion.PlayerCompanion;
import de.markusbordihn.playercompanions.network.CompanionNetworkHandler;
import java.util.List;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.glfw.GLFW;

public class CompanionKeyHandler {

  public static final String CATEGORY = "key.categories.player_companions";

  public static final KeyMapping COMMAND_KEY = new KeyMapping(
    "key.player_companions.command",
    InputConstants.Type.KEYSYM,
    GLFW.GLFW_KEY_LEFT_CONTROL,
    CATEGORY);

  public static final KeyMapping AGGRESSION_KEY = new KeyMapping(
    "key.player_companions.aggression",
    InputConstants.Type.KEYSYM,
    GLFW.GLFW_KEY_LEFT_ALT,
    CATEGORY);

  private CompanionKeyHandler() {
  }

  public static void tick() {
    while (COMMAND_KEY.consumeClick()) {
      handleCommandKey();
    }
    while (AGGRESSION_KEY.consumeClick()) {
      handleAggressionKey();
    }
  }

  private static void handleCommandKey() {
    PlayerCompanion companion = findNearestOwnedCompanion();
    if (companion == null) {
      return;
    }
    CompanionCommand current = companion.getCompanionCommand();
    CompanionCommand next = current == CompanionCommand.SIT
      ? CompanionCommand.FOLLOW : CompanionCommand.SIT;
    CompanionNetworkHandler.sendCompanionCommand(companion.asEntity().getUUID(), next);
  }

  private static void handleAggressionKey() {
    PlayerCompanion companion = findNearestOwnedCompanion();
    if (companion == null || companion.getCompanionRole() != CompanionRole.GUARD) {
      return;
    }
    AggressionLevel next =
      companion.getAggressionLevel().next(AggressionLevel.GUARD_LEVELS);
    CompanionNetworkHandler.sendCompanionAggression(companion.asEntity().getUUID(), next);
  }

  private static PlayerCompanion findNearestOwnedCompanion() {
    Minecraft minecraft = Minecraft.getInstance();
    Player player = minecraft.player;
    if (player == null || minecraft.level == null) {
      return null;
    }
    List<Mob> candidates = minecraft.level.getEntitiesOfClass(
      Mob.class,
      player.getBoundingBox().inflate(16),
      e -> e instanceof PlayerCompanion pc && pc.isOwner(player));
    if (candidates.isEmpty()) {
      return null;
    }
    candidates.sort((a, b) ->
      Double.compare(a.distanceToSqr(player), b.distanceToSqr(player)));
    return (PlayerCompanion) candidates.get(0);
  }
}
