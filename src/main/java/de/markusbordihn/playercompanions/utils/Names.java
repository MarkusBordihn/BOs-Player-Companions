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

import java.util.Map;
import java.util.Random;

import com.google.common.collect.Maps;

import net.minecraft.Util;

public class Names {

  private static Random rand = new Random();

  public static final Map<Integer, String> NPC_NAMES = Util.make(Maps.newHashMap(), hashMap -> {
    hashMap.put(1, "Parn");
    hashMap.put(2, "Deedlit");
    hashMap.put(3, "Amy");
    hashMap.put(4, "Ethan");
    hashMap.put(5, "Guy");
    hashMap.put(6, "Adam");
    hashMap.put(7, "Asuna");
    hashMap.put(8, "Beowulf");
    hashMap.put(9, "Romolo");
    hashMap.put(10, "Miso");
    hashMap.put(11, "Kawo");
    hashMap.put(12, "Faith");
    hashMap.put(13, "Klein");
    hashMap.put(14, "Jason");
    hashMap.put(15, "Monika");
    hashMap.put(16, "Bob");
    hashMap.put(17, "Cain");
    hashMap.put(18, "Aron");
    hashMap.put(19, "Meredith");
    hashMap.put(20, "John");
    hashMap.put(21, "Jack");
  });

  public static final Map<Integer, String> MOB_NAMES = Util.make(Maps.newHashMap(), hashMap -> {
    hashMap.put(0, "Coco");
    hashMap.put(1, "Chloe");
    hashMap.put(2, "Oskar");
    hashMap.put(3, "Simba");
    hashMap.put(4, "Tiger");
    hashMap.put(5, "Lina");
    hashMap.put(6, "Creamy");
    hashMap.put(7, "Roker");
    hashMap.put(8, "Marin");
    hashMap.put(9, "Marina");
    hashMap.put(10, "Smokie");
    hashMap.put(11, "Charlie");
    hashMap.put(12, "Max");
    hashMap.put(13, "Buddy");
    hashMap.put(14, "Bella");
    hashMap.put(15, "Luna");
    hashMap.put(16, "Princess");
    hashMap.put(17, "Lucy");
    hashMap.put(18, "Larry");
    hashMap.put(19, "Spike");
    hashMap.put(20, "Derek");
    hashMap.put(21, "Andrew");
  });

  protected Names() {}

  public static String getRandomNpcName() {
    return MOB_NAMES.getOrDefault(rand.nextInt(0, NPC_NAMES.size() + 1), NPC_NAMES.get(0));
  }

  public static String getRandomMobName() {
    return MOB_NAMES.getOrDefault(rand.nextInt(0, MOB_NAMES.size() + 1), MOB_NAMES.get(0));
  }

}
