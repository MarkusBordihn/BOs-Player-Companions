/*
 * Copyright 2022 Markus Bordihn
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

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.markusbordihn.playercompanions.data.PlayerCompanionsServerDataBackup;
import java.io.File;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class BackupCommand extends CustomCommand {

  private static final BackupCommand command = new BackupCommand();

  public static ArgumentBuilder<CommandSourceStack, ?> register() {
    return Commands.literal("backup").requires(cs -> cs.hasPermission(2)).executes(command)
        .then(Commands.literal("list").executes(command::runList))
        .then(Commands.literal("load").then(
            Commands.argument("file_name", StringArgumentType.string()).executes(command::runLoad)))
        .then(Commands.literal("save").executes(command::runSave));
  }

  @Override
  public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
    sendFeedback(context,
        "The player companion backup commands allows to list, load or save the player companions data from/to the world/player_companions folder.\n===");
    return 0;
  }

  public int runList(CommandContext<CommandSourceStack> context) {
    List<File> backupFiles = PlayerCompanionsServerDataBackup.listBackup();
    if (backupFiles.isEmpty()) {
      sendFeedback(context, "Unable to find any backups!");
    } else {
      sendFeedback(context, String.format("Found %s backups files ...\n===", backupFiles.size()));
      for (File backupFile : backupFiles) {
        sendFeedback(context, String.format("\u25CB %s", backupFile.getName()
            .replace("-" + PlayerCompanionsServerDataBackup.BACKUP_FILE_NAME, "")));
      }
    }
    return 0;
  }

  public int runLoad(CommandContext<CommandSourceStack> context) {
    final String fileName = StringArgumentType.getString(context, "file_name") + "-"
        + PlayerCompanionsServerDataBackup.BACKUP_FILE_NAME;
    if (fileName.isEmpty() || fileName.contains("..") || fileName.contains("|")
        || fileName.contains("/") || fileName.contains("\\")
        || !fileName.endsWith(PlayerCompanionsServerDataBackup.BACKUP_FILE_NAME)) {
      sendFeedback(context, String.format("Invalid file name %s !", fileName));
    } else {
      sendFeedback(context, String.format("Try to load backups from %s...", fileName));
      if (PlayerCompanionsServerDataBackup.loadBackup(fileName)) {
        sendFeedback(context, "Backup was successfully loaded.");
        sendFeedback(context, "Please restart the server to make sure that the new data are used!");
      } else {
        sendFeedback(context,
            "Failed to load backup, please check the logs for additional information!");
      }
    }
    return 0;
  }

  public int runSave(CommandContext<CommandSourceStack> context) {
    sendFeedback(context, "Save backup ...");
    if (PlayerCompanionsServerDataBackup.saveBackup()) {
      sendFeedback(context, "Backup was successfully created.");
    } else {
      sendFeedback(context,
          "Failed to create backup, please check the logs for additional information!");
    }
    return 0;
  }

}
