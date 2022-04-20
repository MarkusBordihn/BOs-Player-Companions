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

public class NamesUtils {

  private static Random rand = new Random();

  protected static final List<String> NPC_FEMALE_NAMES = new ArrayList<>(Sets.newHashSet(
  // @formatter:off
    "Aika",
    "Amy",
    "Asuna",
    "Beatrice",
    "Calypso",
    "Cassandra",
    "Deedlit",
    "Elizabeth",
    "Enya",
    "Faith",
    "Freya",
    "Giselle",
    "Isolde",
    "Julia",
    "Meredith",
    "Monika",
    "Sonya"
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
    "Julian",
    "Kawo",
    "Luca",
    "Markus",
    "Miso",
    "Parn",
    "Romolo"
  // @formatter:on
  ));

  protected static final List<String> NPC_MISC_NAMES = new ArrayList<>(Sets.newHashSet(
  // @formatter:off
    "Alexis",
    "Quinn"
  // @formatter:on
  ));

  protected static final List<String> MOB_FEMALE_NAMES = new ArrayList<>(Sets.newHashSet(
  // @formatter:off
    "Alice",
    "Alina",
    "Amy",
    "Ayame",
    "Beauty",
    "Bella",
    "Bonnie",
    "Carla",
    "Carmen",
    "Celina",
    "Chiyo",
    "Chloe",
    "Conny",
    "Dina",
    "Emi",
    "Emma",
    "Fluffy",
    "Fuyumi",
    "Gina",
    "Haruhi",
    "Hope",
    "Iivy",
    "Isabell",
    "Itsumi",
    "Jessie",
    "Kacy",
    "Karin",
    "Kasumi",
    "Keira",
    "Lina",
    "Lucy",
    "Luna",
    "Mai",
    "Marina",
    "Megumi",
    "Melody",
    "Mia",
    "Mimi",
    "Mizue",
    "Nami",
    "Nicky",
    "Princess",
    "Rainy",
    "Reiko",
    "Sakura",
    "Sandy",
    "Takeko",
    "Trixie",
    "Umi",
    "Vivi",
    "Yona",
    "Yukari",
    "Zoe"
  // @formatter:on
  ));

  protected static final List<String> MOB_MALE_NAMES = new ArrayList<>(Sets.newHashSet(
  // @formatter:off
    "Alex",
    "Andrew",
    "Archie",
    "Benny",
    "Charlie",
    "Coco",
    "Derek",
    "Eric",
    "Felix",
    "Frankie",
    "Gustav",
    "Haruo",
    "Hector",
    "Henry",
    "Hunter",
    "Ikuo",
    "Jin",
    "Kasimir",
    "Kazuma",
    "Larry",
    "Leo",
    "Leonardo",
    "Loki",
    "Marin",
    "Masato",
    "Max",
    "Norio",
    "Osamu",
    "Oskar",
    "Prince",
    "Roker",
    "Rufus",
    "Ryu",
    "Shadow",
    "Shin",
    "Simba",
    "Snickers",
    "Sparky",
    "Spike",
    "Taizo",
    "Tiger",
    "Timmy",
    "Turbo",
    "Yoshi",
    "Yuma",
    "Zenjiro",
    "Zottel"
  // @formatter:on
  ));

  protected static final List<String> MOB_MISC_NAMES = new ArrayList<>(Sets.newHashSet(
  // @formatter:off
    "Alex",
    "Angel",
    "Buddy",
    "Cato",
    "Charlie",
    "Cheddar",
    "Creamy",
    "Curly",
    "Dakota",
    "Elisa",
    "Foxy",
    "Frana",
    "Inky",
    "Isa",
    "Jesse",
    "Jona",
    "Joyce",
    "Jule",
    "Kaya",
    "Luka",
    "Mika",
    "Morgan",
    "Patches",
    "Phantom",
    "Riley",
    "Robin",
    "Sam",
    "Sanja",
    "Sascha",
    "Sasha",
    "Skye",
    "Smokey",
    "Smokie",
    "Toni",
    "Yannie"
  // @formatter:on
  ));

  protected NamesUtils() {}

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

  public static String getRandomMaleMobName() {
    return MOB_MALE_NAMES.get(rand.nextInt(0, MOB_MALE_NAMES.size()));
  }

  public static String getRandomMiscMobName() {
    return MOB_MISC_NAMES.get(rand.nextInt(0, MOB_MISC_NAMES.size()));
  }

  public static String getRandomFemaleAndMiscMobName() {
    int pool = rand.nextInt(0, 2);
    if (pool == 0) {
      return getRandomMiscMobName();
    }
    return getRandomFemaleMobName();
  }

  public static String getRandomMaleAndMiscMobName() {
    int pool = rand.nextInt(0, 2);
    if (pool == 0) {
      return getRandomMiscMobName();
    }
    return getRandomMaleMobName();
  }

}
