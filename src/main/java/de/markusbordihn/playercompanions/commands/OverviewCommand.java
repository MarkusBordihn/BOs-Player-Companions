/**
 * Copyright 2021 Markus Bordihn
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

package de.markusbordihn.playercompanions.commands;

import java.util.Iterator;
import java.util.Set;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

import de.markusbordihn.playercompanions.data.PlayerCompanion;
import de.markusbordihn.playercompanions.data.PlayerCompanionsServerData;

public class OverviewCommand extends CustomCommand {
  private static final OverviewCommand command = new OverviewCommand();

  public static ArgumentBuilder<CommandSourceStack, ?> register() {
    return Commands.literal("overview").executes(command);
  }

  @Override
  public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    ServerPlayer player = context.getSource().getPlayerOrException();
    Set<PlayerCompanion> playerCompanionsSet = PlayerCompanionsServerData.get().getCompanions(player.getUUID());
    if (playerCompanionsSet.isEmpty()) {
      sendFeedback(context, "Unable to find any owned companions!");
      return 0;
    }
    Iterator<PlayerCompanion> playerCompanionIterator = playerCompanionsSet.iterator();
    sendFeedback(context, "Your Companions\n===");
    while (playerCompanionIterator.hasNext()) {
      PlayerCompanion playerCompanion = playerCompanionIterator.next();
      if (playerCompanion != null) {
        sendFeedback(context, String.format("%s", playerCompanion));
      }
    }
    return 0;
  }
}
