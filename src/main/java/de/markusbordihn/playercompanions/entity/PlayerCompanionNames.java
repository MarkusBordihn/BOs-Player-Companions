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

package de.markusbordihn.playercompanions.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Sets;

import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.config.CommonConfig;

@EventBusSubscriber
public class PlayerCompanionNames {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static Random rand = new Random();

  // Config values
  protected static final CommonConfig.Config COMMON = CommonConfig.COMMON;

  private static List<String> namesNPCFemale = new ArrayList<>(Arrays.asList("Unnamed female NPC"));
  private static List<String> namesNPCMale = new ArrayList<>(Arrays.asList("Unnamed male NPC"));
  private static List<String> namesNPCMisc = new ArrayList<>(Arrays.asList("Unnamed misc NPC"));

  private static List<String> namesCompanionFemale =
      new ArrayList<>(Arrays.asList("Unnamed female companion"));
  private static List<String> namesCompanionMale =
      new ArrayList<>(Arrays.asList("Unnamed male companion"));
  private static List<String> namesCompanionMisc =
      new ArrayList<>(Arrays.asList("Unnamed misc companion"));

  protected PlayerCompanionNames() {}

  @SubscribeEvent
  public static void handleServerAboutToStartEvent(ServerAboutToStartEvent event) {

    if (!COMMON.namesNPCFemale.get().isEmpty()) {
      log.info("Loading about {} female NPC names ...", COMMON.namesNPCFemale.get().size());
      namesNPCFemale = new ArrayList<>(Sets.newHashSet(COMMON.namesNPCFemale.get()));
    }

    if (!COMMON.namesNPCMale.get().isEmpty()) {
      log.info("Loading about {} male NPC names ...", COMMON.namesNPCMale.get().size());
      namesNPCMale = new ArrayList<>(Sets.newHashSet(COMMON.namesNPCMale.get()));
    }

    if (!COMMON.namesNPCMisc.get().isEmpty()) {
      log.info("Loading about {} misc NPC names ...", COMMON.namesNPCMisc.get().size());
      namesNPCMisc = new ArrayList<>(Sets.newHashSet(COMMON.namesNPCMisc.get()));
    }

    if (!COMMON.namesCompanionFemale.get().isEmpty()) {
      log.info("Loading about {} female companion names ...",
          COMMON.namesCompanionFemale.get().size());
      namesCompanionFemale = new ArrayList<>(Sets.newHashSet(COMMON.namesCompanionFemale.get()));
    }

    if (!COMMON.namesCompanionMale.get().isEmpty()) {
      log.info("Loading about {} male companion names ...", COMMON.namesCompanionMale.get().size());
      namesCompanionMale = new ArrayList<>(Sets.newHashSet(COMMON.namesCompanionMale.get()));
    }

    if (!COMMON.namesCompanionMisc.get().isEmpty()) {
      log.info("Loading about {} misc companion names ...", COMMON.namesCompanionMisc.get().size());
      namesCompanionMisc = new ArrayList<>(Sets.newHashSet(COMMON.namesCompanionMisc.get()));
    }
  }

  public static String getRandomFemaleNpcName() {
    return namesNPCFemale.get(rand.nextInt(0, namesNPCFemale.size()));
  }

  public static String getRandomMaleNpcName() {
    return namesNPCMale.get(rand.nextInt(0, namesNPCMale.size()));
  }

  public static String getRandomMiscNpcName() {
    return namesNPCMisc.get(rand.nextInt(0, namesNPCMisc.size()));
  }

  public static String getRandomCompanionName() {
    int pool = rand.nextInt(0, 3);
    if (pool == 0) {
      return getRandomMaleCompanionName();
    } else if (pool == 1) {
      return getRandomFemaleCompanionName();
    } else {
      return getRandomMiscCompanionName();
    }
  }

  public static String getRandomFemaleCompanionName() {
    return namesCompanionFemale.get(rand.nextInt(0, namesCompanionFemale.size()));
  }

  public static String getRandomMaleCompanionName() {
    return namesCompanionMale.get(rand.nextInt(0, namesCompanionMale.size()));
  }

  public static String getRandomMiscCompanionName() {
    return namesCompanionMisc.get(rand.nextInt(0, namesCompanionMisc.size()));
  }

  public static String getRandomFemaleAndMiscCompanionName() {
    int pool = rand.nextInt(0, 2);
    if (pool == 0) {
      return getRandomMiscCompanionName();
    }
    return getRandomFemaleCompanionName();
  }

  public static String getRandomMaleAndMiscCompanionName() {
    int pool = rand.nextInt(0, 2);
    if (pool == 0) {
      return getRandomMiscCompanionName();
    }
    return getRandomMaleCompanionName();
  }

}
