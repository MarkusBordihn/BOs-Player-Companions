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

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

public class PlayerCompanionDataHelper {

  protected PlayerCompanionDataHelper() {}

  public static CompoundTag saveInventory(CompoundTag compoundTag, NonNullList<ItemStack> inventory) {
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

  public static void loadInventory(CompoundTag compoundTag, NonNullList<ItemStack> inventory) {
    ListTag listTag = compoundTag.getList("Inventory", 10);
    for (int i = 0; i < listTag.size(); ++i) {
      CompoundTag compoundTagSlot = listTag.getCompound(i);
      int index = compoundTagSlot.getByte("Slot") & 255;
      if (index >= 0 && index < inventory.size()) {
        inventory.set(index, ItemStack.of(compoundTagSlot));
      }
    }
  }

}
