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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

import de.markusbordihn.playercompanions.Constants;

public class PlayerCompanionDataHelper {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  protected PlayerCompanionDataHelper() {}

  public static CompoundTag saveArmorItems(CompoundTag compoundTag, NonNullList<ItemStack> armor) {
    ListTag listTag = new ListTag();
    for (int i = 0; i < armor.size(); ++i) {
      ItemStack itemStack = armor.get(i);
      if (!itemStack.isEmpty()) {
        CompoundTag compoundTagSlot = new CompoundTag();
        compoundTagSlot.putByte("Slot", (byte) i);
        itemStack.save(compoundTagSlot);
        listTag.add(compoundTagSlot);
      }
    }
    if (!listTag.isEmpty()) {
      compoundTag.put("Armor", listTag);
    }
    return compoundTag;
  }

  public static void loadArmorItems(CompoundTag compoundTag, NonNullList<ItemStack> armor) {
    resetNonNullList(armor);
    ListTag listTag = compoundTag.getList("Armor", 10);
    for (int i = 0; i < listTag.size(); ++i) {
      CompoundTag compoundTagSlot = listTag.getCompound(i);
      int index = compoundTagSlot.getByte("Slot") & 255;
      if (index >= 0 && index < armor.size()) {
        armor.set(index, ItemStack.of(compoundTagSlot));
      }
    }
  }

  public static CompoundTag saveInventoryItems(CompoundTag compoundTag,
      NonNullList<ItemStack> inventory) {
    ListTag listTag = new ListTag();
    for (int i = 0; i < inventory.size(); ++i) {
      ItemStack itemStack = inventory.get(i);
      if (!itemStack.isEmpty()) {
        CompoundTag compoundTagSlot = new CompoundTag();
        compoundTagSlot.putByte("Slot", (byte) i);
        itemStack.save(compoundTagSlot);
        listTag.add(compoundTagSlot);
      }
    }
    if (!listTag.isEmpty()) {
      compoundTag.put("Inventory", listTag);
    }
    return compoundTag;
  }

  public static void loadInventoryItems(CompoundTag compoundTag, NonNullList<ItemStack> inventory) {
    resetNonNullList(inventory);
    ListTag listTag = compoundTag.getList("Inventory", 10);
    for (int i = 0; i < listTag.size(); ++i) {
      CompoundTag compoundTagSlot = listTag.getCompound(i);
      int index = compoundTagSlot.getByte("Slot") & 255;
      if (index >= 0 && index < inventory.size()) {
        inventory.set(index, ItemStack.of(compoundTagSlot));
      }
    }
  }

  public static CompoundTag saveHandItems(CompoundTag compoundTag, NonNullList<ItemStack> hand) {
    ListTag listTag = new ListTag();
    for (int i = 0; i < hand.size(); ++i) {
      ItemStack itemStack = hand.get(i);
      if (!itemStack.isEmpty()) {
        CompoundTag compoundTagSlot = new CompoundTag();
        compoundTagSlot.putByte("Slot", (byte) i);
        itemStack.save(compoundTagSlot);
        listTag.add(compoundTagSlot);
      }
    }
    if (!listTag.isEmpty()) {
      compoundTag.put("Hand", listTag);
    }
    return compoundTag;
  }

  public static void loadHandItems(CompoundTag compoundTag, NonNullList<ItemStack> hand) {
    resetNonNullList(hand);
    ListTag listTag = compoundTag.getList("Hand", 10);
    for (int i = 0; i < listTag.size(); ++i) {
      CompoundTag compoundTagSlot = listTag.getCompound(i);
      int index = compoundTagSlot.getByte("Slot") & 255;
      if (index >= 0 && index < hand.size()) {
        hand.set(index, ItemStack.of(compoundTagSlot));
      }
    }
  }

  private static void resetNonNullList(NonNullList<ItemStack> list) {
    for (int index = 0; index < list.size(); ++index) {
      list.set(index, ItemStack.EMPTY);
    }
  }

}
