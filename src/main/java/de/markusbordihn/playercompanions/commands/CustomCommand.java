/*
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

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import de.markusbordihn.playercompanions.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class CustomCommand implements Command<CommandSourceStack> {
  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  protected CustomCommand() {}

  public static void sendFeedback(CommandContext<CommandSourceStack> context, String feedback) {
    CommandSourceStack commandSource = context.getSource();
    commandSource.sendSuccess(() -> Component.literal(feedback), false);
  }

  public static void sendErrorFeedback(
      CommandContext<CommandSourceStack> context, String feedback) {
    CommandSourceStack commandSource = context.getSource();
    commandSource.sendSuccess(
        () -> Component.literal(feedback).setStyle(Style.EMPTY.withColor(ChatFormatting.RED)),
        false);
  }

  public static void sendFeedback(CommandContext<CommandSourceStack> context, Component component) {
    CommandSourceStack commandSource = context.getSource();
    commandSource.sendSuccess(() -> component, false);
  }
}
