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

import de.markusbordihn.playercompanions.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CompanionCommand {

  public static final int FAILURE = 0;
  public static final int SINGLE_SUCCESS = 1;

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  protected CompanionCommand() {
  }

  public static int sendFailureMessage(CommandSourceStack context, String message) {
    context.sendFailure(Component.literal(message));
    return FAILURE;
  }

  public static int sendSuccessMessage(CommandSourceStack context, String message) {
    context.sendSuccess(() -> Component.literal(message), true);
    return SINGLE_SUCCESS;
  }

  public static int sendSuccessMessage(
    CommandSourceStack context, String message, ChatFormatting formatting) {
    context.sendSuccess(() -> Component.literal(message).withStyle(formatting), true);
    return SINGLE_SUCCESS;
  }
}
