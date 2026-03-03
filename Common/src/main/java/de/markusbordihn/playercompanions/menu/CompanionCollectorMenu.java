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

package de.markusbordihn.playercompanions.menu;

import de.markusbordihn.playercompanions.entity.companion.PigCompanion;
import de.markusbordihn.playercompanions.menu.slots.CompanionInventorySlot;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;

public class CompanionCollectorMenu extends CompanionMenu {

  private static final int GRID_ROWS = 4;
  private static final int GRID_COLS = 4;
  private static final int GRID_X = 119;
  private static final int GRID_Y = 17;

  public CompanionCollectorMenu(int windowId, Inventory playerInventory, FriendlyByteBuf data) {
    this(windowId, playerInventory, data.readUUID());
  }

  public CompanionCollectorMenu(int windowId, Inventory playerInventory, UUID companionUUID) {
    super(ModMenuTypes.getCollectorMenuType(), windowId, playerInventory, companionUUID);
  }

  @Override
  protected void addRoleSlots() {
    SimpleContainer inventoryContainer = findInventory();
    if (inventoryContainer == null) {
      return;
    }
    for (int row = 0; row < GRID_ROWS; row++) {
      for (int col = 0; col < GRID_COLS; col++) {
        addSlot(new CompanionInventorySlot(inventoryContainer, col + row * GRID_COLS,
          GRID_X + col * 18, GRID_Y + row * 18));
      }
    }
  }

  @Override
  protected int getRoleSlotCount() {
    return GRID_ROWS * GRID_COLS;
  }

  private SimpleContainer findInventory() {
    return level.getEntitiesOfClass(PigCompanion.class,
        player.getBoundingBox().inflate(64),
        e -> e.getUUID().equals(companionUUID)).stream()
      .findFirst()
      .map(PigCompanion::getInventory)
      .orElse(null);
  }
}
