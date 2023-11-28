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

package de.markusbordihn.playercompanions.data;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.Util;

public class Experience {

  public static final int MIN_LEVEL = 1;
  public static final int MAX_LEVEL = 60;

  protected static final Map<Integer, Integer> LevelExperienceMap =
      Util.make(
          Maps.newHashMap(),
          hashMap -> {
            hashMap.put(1, 1);
            hashMap.put(2, 4);
            hashMap.put(3, 12);
            hashMap.put(4, 24);
            hashMap.put(5, 40);
            hashMap.put(6, 60);
            hashMap.put(7, 84);
            hashMap.put(8, 112);
            hashMap.put(9, 144);
            hashMap.put(10, 180);
            hashMap.put(11, 220);
            hashMap.put(12, 264);
            hashMap.put(13, 312);
            hashMap.put(14, 364);
            hashMap.put(15, 420);
            hashMap.put(16, 480);
            hashMap.put(17, 544);
            hashMap.put(18, 612);
            hashMap.put(19, 684);
            hashMap.put(20, 760);
            hashMap.put(21, 840);
            hashMap.put(22, 924);
            hashMap.put(23, 1012);
            hashMap.put(24, 1104);
            hashMap.put(25, 1200);
            hashMap.put(26, 1300);
            hashMap.put(27, 1404);
            hashMap.put(28, 1512);
            hashMap.put(29, 1624);
            hashMap.put(30, 1740);
            hashMap.put(31, 1860);
            hashMap.put(32, 1984);
            hashMap.put(33, 2112);
            hashMap.put(34, 2244);
            hashMap.put(35, 2380);
            hashMap.put(36, 2520);
            hashMap.put(37, 2664);
            hashMap.put(38, 2812);
            hashMap.put(39, 2964);
            hashMap.put(40, 3120);
            hashMap.put(41, 3280);
            hashMap.put(42, 3444);
            hashMap.put(43, 3612);
            hashMap.put(44, 3784);
            hashMap.put(45, 3960);
            hashMap.put(46, 4140);
            hashMap.put(47, 4324);
            hashMap.put(48, 4512);
            hashMap.put(49, 4704);
            hashMap.put(50, 4900);
            hashMap.put(51, 5100);
            hashMap.put(52, 5304);
            hashMap.put(53, 5512);
            hashMap.put(54, 5724);
            hashMap.put(55, 5940);
            hashMap.put(56, 6160);
            hashMap.put(57, 6384);
            hashMap.put(58, 6612);
            hashMap.put(59, 6844);
            hashMap.put(60, 7080);
          });

  protected Experience() {}

  public static int getExperienceForLevel(int level) {
    return LevelExperienceMap.getOrDefault(level, null);
  }

  public static int getExperienceForNextLevel(int level) {
    if (level < MAX_LEVEL) {
      return LevelExperienceMap.getOrDefault(level + 1, null);
    }
    return LevelExperienceMap.get(MAX_LEVEL);
  }

  public static int getExperienceForPreviousLevel(int level) {
    if (level > 1 && level <= MAX_LEVEL) {
      return LevelExperienceMap.get(level - 1);
    }
    return LevelExperienceMap.get(MIN_LEVEL);
  }

  public static int getExperienceDifferenceForLevel(int level) {
    if (level > 1) {
      return LevelExperienceMap.getOrDefault(level, 0)
          - LevelExperienceMap.getOrDefault(level - 1, 0);
    }
    return 0;
  }

  public static int getLevelFromExperience(int experience) {
    for (int level = 0; level < MAX_LEVEL; level++) {
      if (getExperienceForLevel(level) > experience) {
        return level - 1;
      }
    }
    return 1;
  }
}
