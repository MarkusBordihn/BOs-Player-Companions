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

package de.markusbordihn.playercompanions.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.google.common.collect.Sets;

public class Names {

  private static Random rand = new Random();

  protected static final List<String> NPC_FEMALE_NAMES = new ArrayList<>(Sets.newHashSet(
  // @formatter:off
    "Amy",
    "Asuna",
    "Deedlit",
    "Faith",
    "Sonya",
    "Meredith",
    "Monika",
    "Elizabeth"
  // @formatter:on
  ));

  protected static final List<String> NPC_MALE_NAMES = new ArrayList<>(Sets.newHashSet(
  // @formatter:off
    "Adam",
    "Aron",
    "Beowulf",
    "Bob",
    "Cain",
    "Ethan",
    "Guy",
    "Jack",
    "Jason",
    "John",
    "Kawo",
    "Miso",
    "Parn",
    "Romolo"
  // @formatter:on
  ));

  protected static final List<String> NPC_MISC_NAMES = new ArrayList<>(Sets.newHashSet(
  // @formatter:off
    "Quinn"
  // @formatter:on
  ));

  protected static final List<String> MOB_FEMALE_NAMES = new ArrayList<>(Sets.newHashSet(
  // @formatter:off
    "Alice",
    "Bella",
    "Bonnie",
    "Chloe",
    "Lina",
    "Lucy",
    "Luna",
    "Marina",
    "Princess"
  // @formatter:on
  ));

  protected static final List<String> MOB_MALE_NAMES = new ArrayList<>(Sets.newHashSet(
  // @formatter:off
    "Andrew",
    "Coco",
    "Derek",
    "Larry",
    "Marin",
    "Max",
    "Oskar",
    "Prince",
    "Roker",
    "Simba",
    "Spike",
    "Tiger"
  // @formatter:on
  ));

  protected static final List<String> MOB_MISC_NAMES = new ArrayList<>(Sets.newHashSet(
  // @formatter:off
    "Alex",
    "Angel",
    "Charlie",
    "Buddy",
    "Jule",
    "Creamy",
    "Smokie"
  // @formatter:on
  ));

  protected Names() {}

  public static String getRandomFemaleNpcName() {
    return NPC_FEMALE_NAMES.get(rand.nextInt(0, NPC_FEMALE_NAMES.size()));
  }

  public static String getRandomMaleNpcName() {
    return NPC_MALE_NAMES.get(rand.nextInt(0, NPC_MALE_NAMES.size()));
  }

  public static String getRandomMiscNpcName() {
    return NPC_MISC_NAMES.get(rand.nextInt(0, NPC_MISC_NAMES.size()));
  }

  public static String getRandomMobName() {
    int pool = rand.nextInt(0,3);
    if (pool == 0) {
      return getRandomMaleMobName();
    } else if (pool == 1) {
      return getRandomFemaleMobName();
    } else {
      return getRandomMiscMobName();
    }
  }

  public static String getRandomFemaleMobName() {
    return MOB_FEMALE_NAMES.get(rand.nextInt(0, MOB_FEMALE_NAMES.size()));
  }

  public static String getRandomFemaleAndMiscMobName() {
    int pool = rand.nextInt(0, 2);
    if (pool == 0) {
      return getRandomMiscMobName();
    }
    return getRandomFemaleMobName();
  }

  public static String getRandomMaleMobName() {
    return MOB_MALE_NAMES.get(rand.nextInt(0, MOB_MALE_NAMES.size()));
  }

  public static String getRandomMiscMobName() {
    return MOB_MISC_NAMES.get(rand.nextInt(0, MOB_MISC_NAMES.size()));
  }
}
