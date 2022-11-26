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
import java.util.UUID;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.data.PlayerCompanionData;
import de.markusbordihn.playercompanions.data.PlayerCompanionsServerData;
import de.markusbordihn.playercompanions.entity.PlayerCompanionSpawnManager;

public class SummonCommand extends CustomCommand {
  private static final SummonCommand command = new SummonCommand();

  public static ArgumentBuilder<CommandSourceStack, ?> register() {
    return Commands.literal("summon").requires(cs -> cs.hasPermission(0)).executes(command)
        .then(Commands.argument("UUID", StringArgumentType.string())
            .executes(command::runSummonCompanion));
  }

  @Override
  public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    ServerPlayer player = context.getSource().getPlayerOrException();
    Set<PlayerCompanionData> playerCompanionsSet =
        PlayerCompanionsServerData.get().getCompanions(player.getUUID());
    if (playerCompanionsSet == null || playerCompanionsSet.isEmpty()) {
      sendFeedback(context, "Unable to find any owned companions!");
      return 0;
    }
    Iterator<PlayerCompanionData> playerCompanionIterator = playerCompanionsSet.iterator();
    sendFeedback(context, "Your owned Companions\n===");
    while (playerCompanionIterator.hasNext()) {
      PlayerCompanionData playerCompanion = playerCompanionIterator.next();
      if (playerCompanion != null) {
        MutableComponent summonCommand =
            Component.translatable(Constants.COMMAND_PREFIX + "summon.action")
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GREEN)
                    .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                        "/" + Constants.MOD_COMMAND + " summon " + playerCompanion.getUUID())));
        sendFeedback(context,
            Component.translatable(Constants.COMMAND_PREFIX + "summon.list_entry",
                Component.literal(playerCompanion.getName())
                    .setStyle(Style.EMPTY.withColor(ChatFormatting.YELLOW)),
                playerCompanion.getType(), Component.literal("UUID:" + playerCompanion.getUUID())
                    .setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)),
                summonCommand));
      }
    }
    return 0;
  }

  public int runSummonCompanion(CommandContext<CommandSourceStack> context)
      throws CommandSyntaxException {
    final String companionUUID = StringArgumentType.getString(context, "UUID");

    // Check if there is a valid UUID.
    UUID uuid;
    try {
      uuid = UUID.fromString(companionUUID);
    } catch (IllegalArgumentException exception) {
      sendErrorFeedback(context, "Invalid player companions UUID " + companionUUID);
      return 0;
    }

    // Pre-check and gather relevant data.
    ServerPlayer serverPlayer = context.getSource().getPlayerOrException();
    PlayerCompanionsServerData playerCompanionsServerData = PlayerCompanionsServerData.get();
    if (uuid == null || serverPlayer == null || playerCompanionsServerData == null) {
      sendErrorFeedback(context, "Invalid arguments!");
      return 0;
    }

    // Check if there is a valid companion.
    PlayerCompanionData playerCompanionData = playerCompanionsServerData.getCompanion(uuid);
    if (playerCompanionData == null) {
      sendErrorFeedback(context, "Unable to find player companion with UUID " + companionUUID);
      return 0;
    }

    // Check if player is owner of companion.
    if (!playerCompanionData.getOwnerUUID().equals(serverPlayer.getUUID())) {
      sendErrorFeedback(context,
          "Player " + serverPlayer.getName().getString() + " is not owning player companion "
              + playerCompanionData.getName() + " with UUID " + companionUUID);
      return 0;
    }

    // Check if there is any active respawn timer.
    if (playerCompanionData.hasEntityRespawnTimer()
        && playerCompanionData.getEntityRespawnTimer() > java.time.Instant.now().getEpochSecond()) {
      sendErrorFeedback(context, "Unable summon player companion with UUID " + companionUUID
          + " because there is a active respawn timer for "
          + (playerCompanionData.getEntityRespawnTimer() - java.time.Instant.now().getEpochSecond())
          + " secs remaining!");
      return 0;
    }

    sendFeedback(context, "Try to summon " + companionUUID + " for "
        + serverPlayer.getName().getString() + " with " + playerCompanionData);
    PlayerCompanionSpawnManager.spawn(uuid, serverPlayer);
    return 0;
  }
}
