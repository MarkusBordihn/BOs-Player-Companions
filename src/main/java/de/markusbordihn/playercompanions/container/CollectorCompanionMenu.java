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

package de.markusbordihn.playercompanions.container;

import de.markusbordihn.playercompanions.container.slots.InventorySlot;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;

public class CollectorCompanionMenu extends CompanionMenu {

  public CollectorCompanionMenu(int windowId, Inventory playerInventory, FriendlyByteBuf data) {
    super(ModMenuTypes.COLLECTOR_COMPANION_MENU.get(), windowId, playerInventory, data);
  }

  public CollectorCompanionMenu(int windowId, Inventory inventory, UUID playerCompanionUUID) {
    super(ModMenuTypes.COLLECTOR_COMPANION_MENU.get(), windowId, inventory, playerCompanionUUID);
  }

  public CollectorCompanionMenu(int windowId, Inventory playerInventory, Container armorContainer,
      Container equipmentContainer, Container handContainer, Container inventoryContainer,
      UUID playerCompanionUUID) {
    super(ModMenuTypes.COLLECTOR_COMPANION_MENU.get(), windowId, playerInventory, armorContainer,
        equipmentContainer, handContainer, inventoryContainer, playerCompanionUUID);
  }

  @Override
  public void loadAdditionalContainer() {
    loadInventory();
  }

  @Override
  public void addAdditionalSlots() {
    // Player Companion Inventory Slots
    int playerCompanionInventoryStartPositionY = 17;
    int playerCompanionInventoryStartPositionX = 119;
    for (int inventoryRow = 0; inventoryRow < 4; ++inventoryRow) {
      for (int inventoryColumn = 0; inventoryColumn < 4; ++inventoryColumn) {
        this.addSlot(
            new InventorySlot(this, this.inventoryContainer, inventoryRow + inventoryColumn * 4,
                playerCompanionInventoryStartPositionX + inventoryColumn * slotSize,
                playerCompanionInventoryStartPositionY + inventoryRow * slotSize));
      }
    }
  }

}
