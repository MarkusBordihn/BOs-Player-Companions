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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.storage.LevelResource;

import net.minecraftforge.server.ServerLifecycleHooks;

import de.markusbordihn.playercompanions.Constants;

public class PlayerCompanionsServerDataBackup {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public static final File BACKUP_FOLDER =
      new File(ServerLifecycleHooks.getCurrentServer().getWorldPath(LevelResource.ROOT).toFile(),
          Constants.MOD_ID);
  public static final String BACKUP_FILE_NAME = "player_companions_data.nbt";

  private static CompoundTag lastBackupCompoundTag = null;

  protected PlayerCompanionsServerDataBackup() {}

  public static boolean saveBackup() {
    CompoundTag compoundTag = new CompoundTag();
    PlayerCompanionsServerData.get().save(compoundTag);
    return saveBackup(compoundTag);
  }

  public static boolean saveBackup(CompoundTag compoundTag) {
    if (compoundTag.equals(lastBackupCompoundTag)) {
      log.warn("{} skipping Backup, because data are already saved!", Constants.LOG_ICON_NAME);
      return false;
    }
    if (compoundTag.isEmpty() || compoundTag.size() == 0) {
      log.warn("{} skipping Backup, because data are empty!", Constants.LOG_ICON_NAME);
      return false;
    }
    File file = new File(BACKUP_FOLDER.getAbsoluteFile(),
        new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-" + BACKUP_FILE_NAME);
    log.info("{} creating Backup {} ...", Constants.LOG_ICON_NAME, file.getName());
    try {
      if (!file.getParentFile().exists()) {
        log.info("{} creating backup folder at {}", Constants.LOG_ICON_NAME, file.getParentFile());
        file.getParentFile().mkdirs();
      }
      NbtIo.writeCompressed(compoundTag, file);
      log.info("{} saved backup at {}", Constants.LOG_ICON_NAME, file);
      lastBackupCompoundTag = compoundTag;
      return true;
    } catch (final IOException exception) {
      log.error("{} failed save backup with exception: {}", Constants.LOG_ICON_NAME, exception);
      return false;
    }
  }

  public static boolean loadBackup(String fileName) {
    File file = new File(BACKUP_FOLDER.getAbsoluteFile(), fileName);
    if (!file.exists()) {
      log.error("{} unable to read backup file from {}!", Constants.LOG_ICON_NAME, file);
      return false;
    }
    CompoundTag compoundTag = loadBackup(file);
    if (compoundTag == null) {
      log.warn("{} loaded backup from {} was empty!", Constants.LOG_ICON_NAME, file);
      return false;
    }
    PlayerCompanionsServerData.setData(PlayerCompanionsServerData.load(compoundTag));
    return true;
  }

  public static CompoundTag loadBackup(File file) {
    if (file == null || !file.exists()) {
      log.error("{} unable to read backup file from {}!", Constants.LOG_ICON_NAME, file);
      return null;
    }
    try {
      CompoundTag compoundTag = NbtIo.readCompressed(file);
      if (compoundTag == null) {
        log.warn("{} loaded backup from {} was empty!", Constants.LOG_ICON_NAME, file);
      } else {
        log.info("{} loaded backup from {}", Constants.LOG_ICON_NAME, file);
      }
      return compoundTag;
    } catch (final IOException exception) {
      log.error("{} failed load backup with exception: {}", Constants.LOG_ICON_NAME, exception);
    }
    return null;
  }

  public static List<File> listBackup() {
    List<File> backupFiles = new ArrayList<>();
    if (!BACKUP_FOLDER.exists()) {
      log.error("{} unable to find backup folder {}!", Constants.LOG_ICON_NAME, BACKUP_FOLDER);
      return backupFiles;
    }

    File[] files = BACKUP_FOLDER.listFiles();
    for (File file : files) {
      // We are only interested in files which ends with the backup file name.
      if (file.getName().endsWith(BACKUP_FILE_NAME)) {
        backupFiles.add(file);
      }
    }
    return backupFiles;
  }

}
