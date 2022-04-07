/**
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

package de.markusbordihn.playercompanions.data;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.server.ServerLifecycleHooks;

import de.markusbordihn.playercompanions.Constants;

public class PlayerCompanionsServerDataBackup {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  static final File BACKUP_FOLDER =
      new File(ServerLifecycleHooks.getCurrentServer().getWorldPath(LevelResource.ROOT).toFile(),
          Constants.MOD_ID);
  static final String BACKUP_FILE_NAME = "player_companions_data_backup.nbt";

  protected PlayerCompanionsServerDataBackup() {}

  public static void saveBackup(CompoundTag compoundTag) {
    log.info("{} Creating Backup ... for {}", Constants.LOG_ICON_NAME, compoundTag);
    File file = new File(BACKUP_FOLDER.getAbsoluteFile(),
        new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-" + BACKUP_FILE_NAME);
    try {
      file.getParentFile().mkdirs();
      NbtIo.write(compoundTag, file);
      log.info("{} Created backup at {}", Constants.LOG_ICON_NAME, file);
    } catch (final IOException exception) {
      log.error("{} Failed creating backup with exception: {}", Constants.LOG_ICON_NAME, exception);
    }
  }

}
