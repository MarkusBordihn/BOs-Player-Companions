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

package de.markusbordihn.playercompanions.container.slots;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.container.CompanionMenu;
import de.markusbordihn.playercompanions.item.CapturedCompanion;
import de.markusbordihn.playercompanions.item.CompanionTameItem;

public class InventorySlot extends Slot {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private CompanionMenu menu;

  public InventorySlot(CompanionMenu menu, Container container, int index, int x, int y) {
    super(container, index, x, y);
    this.menu = menu;
  }

  @Override
  public void set(ItemStack itemStack) {
    super.set(itemStack);

    // Update menu only if we changed the slot to avoid updated for place and take actions.
    this.menu.setInventoryChanged(this.getSlotIndex(), itemStack);
  }

  @Override
  public boolean mayPlace(ItemStack itemStack) {
    Item item = itemStack.getItem();
    return (!(item instanceof CapturedCompanion) && !(item instanceof CompanionTameItem));
  }

}
