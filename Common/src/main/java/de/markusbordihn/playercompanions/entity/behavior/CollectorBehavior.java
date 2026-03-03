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

package de.markusbordihn.playercompanions.entity.behavior;

import de.markusbordihn.playercompanions.entity.CompanionCommand;
import de.markusbordihn.playercompanions.entity.companion.PlayerCompanion;
import java.util.List;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class CollectorBehavior {

  private static final double PICKUP_RADIUS = 4.0;

  private CollectorBehavior() {
  }

  public static boolean tick(PlayerCompanion companion,
    net.minecraft.world.SimpleContainer inventory) {
    if (companion.level().isClientSide || !companion.isOwned()
      || companion.getCompanionCommand() == CompanionCommand.SIT) {
      return false;
    }
    Level level = companion.level();
    net.minecraft.world.entity.Mob mob = companion.asMob();
    AABB searchBox = mob.getBoundingBox().inflate(PICKUP_RADIUS);
    List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, searchBox,
      item -> !item.isRemoved() && item.isAlive());
    boolean collected = false;
    for (ItemEntity itemEntity : items) {
      if (inventory.canAddItem(itemEntity.getItem())) {
        ItemStack remaining = addToInventory(inventory, itemEntity.getItem().copy());
        if (remaining.isEmpty()) {
          itemEntity.discard();
          collected = true;
        } else {
          itemEntity.setItem(remaining);
        }
      }
    }
    return collected;
  }

  private static ItemStack addToInventory(net.minecraft.world.SimpleContainer inventory,
    ItemStack stack) {
    for (int i = 0; i < inventory.getContainerSize(); i++) {
      ItemStack slot = inventory.getItem(i);
      if (slot.isEmpty()) {
        inventory.setItem(i, stack);
        return ItemStack.EMPTY;
      }
      if (ItemStack.isSameItemSameTags(slot, stack) && slot.getCount() < slot.getMaxStackSize()) {
        int space = slot.getMaxStackSize() - slot.getCount();
        int take = Math.min(space, stack.getCount());
        slot.grow(take);
        stack.shrink(take);
        if (stack.isEmpty()) {
          return ItemStack.EMPTY;
        }
      }
    }
    return stack;
  }
}
