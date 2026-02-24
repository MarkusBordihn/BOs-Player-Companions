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

package de.markusbordihn.playercompanions.config;

import de.markusbordihn.playercompanions.Constants;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CompanionNameConfig {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final String LOG_PREFIX = "[CompanionNames]";

  private static final String FILE_FEMALE = "names_female.txt";
  private static final String FILE_MALE = "names_male.txt";
  private static final String FILE_MISC = "names_misc.txt";

  // @formatter:off
  private static final List<String> DEFAULT_FEMALE = List.of(
      "Abby", "Alina", "Amy", "Ayame", "Beauty", "Bella", "Bonnie", "Carla", "Carmen",
      "Celina", "Chiyo", "Chloe", "Conny", "Dina", "Emi", "Emma", "Fluffy", "Fuyumi",
      "Gina", "Haruhi", "Hope", "Iivy", "Isabell", "Itsumi", "Jessie", "Kacy", "Karin",
      "Kasumi", "Keira", "Lina", "Lucy", "Luna", "Mai", "Marina", "Megumi", "Melody",
      "Mia", "Mimi", "Mizue", "Nami", "Nicky", "Princess", "Rainy", "Reiko", "Sakura",
      "Sandy", "Takeko", "Trixie", "Umi", "Vivi", "Yona", "Yukari", "Zoe");

  private static final List<String> DEFAULT_MALE = List.of(
      "Alex", "Andrew", "Archie", "Benny", "Charlie", "Coco", "Derek", "Eric", "Felix",
      "Frankie", "Gustav", "Haruo", "Hector", "Henry", "Hunter", "Ikuo", "Jin", "Kasimir",
      "Kazuma", "Larry", "Leo", "Leonardo", "Loki", "Marin", "Masato", "Max", "Norio",
      "Osamu", "Oskar", "Prince", "Roker", "Rufus", "Ryu", "Shadow", "Shin", "Simba",
      "Snickers", "Sparky", "Spike", "Taizo", "Tiger", "Timmy", "Turbo", "Yoshi", "Yuma",
      "Zenjiro", "Zottel");

  private static final List<String> DEFAULT_MISC = List.of(
      "Angel", "Buddy", "Cato", "Cheddar", "Creamy", "Curly", "Dakota", "Elisa", "Foxy",
      "Frana", "Inky", "Isa", "Jesse", "Jona", "Joyce", "Jule", "Kaya", "Luka", "Mika",
      "Morgan", "Patches", "Phantom", "Riley", "Robin", "Sam", "Sanja", "Sascha", "Sasha",
      "Skye", "Smokey", "Smokie", "Toni", "Yannie");
  // @formatter:on

  private static List<String> allNames = List.of();

  private CompanionNameConfig() {
  }

  public static void load() {
    Path configDir = resolveConfigDir();
    if (configDir == null) {
      log.error("{} Config directory not available, using default names", LOG_PREFIX);
      allNames = combineNames(DEFAULT_FEMALE, DEFAULT_MALE, DEFAULT_MISC);
      return;
    }

    List<String> female = loadOrCreate(configDir.resolve(FILE_FEMALE), DEFAULT_FEMALE);
    List<String> male = loadOrCreate(configDir.resolve(FILE_MALE), DEFAULT_MALE);
    List<String> misc = loadOrCreate(configDir.resolve(FILE_MISC), DEFAULT_MISC);
    allNames = combineNames(female, male, misc);

    log.info("{} Loaded {} companion names ({} female, {} male, {} misc)",
      LOG_PREFIX, allNames.size(), female.size(), male.size(), misc.size());
  }

  private static List<String> combineNames(
    List<String> female, List<String> male, List<String> misc) {
    List<String> combined = new ArrayList<>(female.size() + male.size() + misc.size());
    combined.addAll(female);
    combined.addAll(male);
    combined.addAll(misc);
    return combined;
  }

  public static String getRandomName() {
    if (allNames.isEmpty()) {
      return null;
    }
    return allNames.get(ThreadLocalRandom.current().nextInt(allNames.size()));
  }

  public static boolean shouldAutoName() {
    return TamingConfig.AUTO_NAME_ON_TAME && !allNames.isEmpty();
  }

  private static Path resolveConfigDir() {
    Path base = Constants.CONFIG_DIR;
    if (base == null) {
      base = Constants.GAME_DIR.resolve("config");
    }
    Path dir = base.resolve(Constants.MOD_ID);
    try {
      return Files.createDirectories(dir);
    } catch (IOException e) {
      log.error("{} Failed to create config directory {}:", LOG_PREFIX, dir, e);
      return null;
    }
  }

  private static List<String> loadOrCreate(Path file, List<String> defaults) {
    if (!Files.exists(file)) {
      writeNameFile(file, defaults);
      return new ArrayList<>(defaults);
    }
    return readNameFile(file);
  }

  private static List<String> readNameFile(Path file) {
    try {
      return Files.readAllLines(file).stream()
        .map(String::trim)
        .filter(line -> !line.isEmpty() && !line.startsWith("#"))
        .collect(Collectors.toCollection(ArrayList::new));
    } catch (IOException e) {
      log.error("{} Failed to read {}:", LOG_PREFIX, file, e);
      return new ArrayList<>();
    }
  }

  private static void writeNameFile(Path file, List<String> names) {
    List<String> lines = new ArrayList<>();
    lines.add("# Player Companions - Name List");
    lines.add("# One name per line. Lines starting with # are ignored.");
    lines.add("# Delete this file to regenerate defaults.");
    lines.add("");
    lines.addAll(names);
    try {
      Files.write(file, lines);
      log.info("{} Created default name file {} with {} names", LOG_PREFIX, file, names.size());
    } catch (IOException e) {
      log.error("{} Failed to write {}:", LOG_PREFIX, file, e);
    }
  }
}
