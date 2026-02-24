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

package de.markusbordihn.playercompanions.text;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.config.TamingConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

public class TamingMessages {

  private static final String TRUST_PREFIX = Constants.TEXT_PREFIX + "trust.";
  private static final String TAMED_PREFIX = Constants.TEXT_PREFIX + "tamed.";
  private static final String TAMING_PREFIX = Constants.TEXT_PREFIX + "taming.";

  private TamingMessages() {
  }

  public static void sendTrustProgress(Player player, int added, int current, int max) {
    if (!TamingConfig.SHOW_TRUST_BAR) {
      return;
    }

    MutableComponent addedComponent =
      Component.literal("+" + added).withStyle(ChatFormatting.GREEN);

    Component message =
      Component.translatable(TRUST_PREFIX + "progress", addedComponent, current, max)
        .withStyle(ChatFormatting.YELLOW);

    player.displayClientMessage(message, true); // true = actionbar
  }

  public static void sendMilestone(Player player, int milestone) {
    if (!TamingConfig.SHOW_MILESTONE_MESSAGES) {
      return;
    }

    Component message =
      Component.translatable(TRUST_PREFIX + "milestone", milestone)
        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);

    player.displayClientMessage(message, true);
  }

  public static void sendTamedMessage(Player player, Component companionName) {
    Component message =
      Component.translatable(TAMED_PREFIX + "success_you", companionName)
        .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD);

    player.displayClientMessage(message, true);
    player.sendSystemMessage(message);
  }

  public static void sendTamedActionBar(Player player) {
    Component message =
      Component.translatable(TAMED_PREFIX + "you")
        .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD);

    player.displayClientMessage(message, true);
  }

  public static void sendCooldownMessage(Player player, int secondsLeft) {
    Component message =
      Component.translatable(TRUST_PREFIX + "cooldown", secondsLeft)
        .withStyle(ChatFormatting.RED);

    player.displayClientMessage(message, true);
  }

  public static void sendWrongPlayerMessage(Player player, String trustedPlayerName) {
    Component message =
      Component.translatable(TRUST_PREFIX + "wrong_player", trustedPlayerName)
        .withStyle(ChatFormatting.RED);

    player.displayClientMessage(message, true);
  }

  public static void sendLimitReachedMessage(Player player, int limit) {
    Component message =
      Component.translatable(TAMING_PREFIX + "limit_reached", limit, limit)
        .withStyle(ChatFormatting.RED, ChatFormatting.BOLD);

    player.displayClientMessage(message, true);

    Component helpMessage =
      Component.translatable(TAMING_PREFIX + "limit_help").withStyle(ChatFormatting.GRAY);
    player.sendSystemMessage(helpMessage);
  }

  public static Component getTrustBar(int current, int max) {
    int bars = 10;
    int filled = (int) ((current / (float) max) * bars);

    MutableComponent bar = Component.literal("[");
    for (int i = 0; i < bars; i++) {
      if (i < filled) {
        bar.append(Component.literal("█").withStyle(ChatFormatting.GREEN));
      } else {
        bar.append(Component.literal("░").withStyle(ChatFormatting.GRAY));
      }
    }
    bar.append("]");
    return bar;
  }
}
