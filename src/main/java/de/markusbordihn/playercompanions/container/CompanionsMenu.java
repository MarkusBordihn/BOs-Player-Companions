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

package de.markusbordihn.playercompanions.container;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.container.slots.FavouriteCompanionSlot;
import de.markusbordihn.playercompanions.data.PlayerCompanionData;
import de.markusbordihn.playercompanions.data.PlayerCompanionsServerData;
import de.markusbordihn.playercompanions.item.ModItems;

public class CompanionsMenu extends AbstractContainerMenu {

  public static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  // Defining basic layout options
  private static int containerSize = 6;
  private static int slotSize = 18;
  private static int slotSpacing = 8;

  // Define containers
  private final Container container;
  private final ContainerData data;
  private final Player player;

  public CompanionsMenu(int windowIdIn, Inventory inventory) {
    this(windowIdIn, inventory, new SimpleContainer(3),
        new SimpleContainerData(0), ModContainer.COMPANIONS_MENU.get());
  }

  public CompanionsMenu(int windowId, Inventory playerInventory, FriendlyByteBuf data) {
    this(windowId, playerInventory, new SimpleContainer(3), new SimpleContainerData(0),
        ModContainer.COMPANIONS_MENU.get());
  }

  public CompanionsMenu(final int windowId, final Inventory playerInventory,
      final Container container, final ContainerData containerData,
      final MenuType<CompanionsMenu> menuType) {
    super(ModContainer.COMPANIONS_MENU.get(), windowId);

    this.player = playerInventory.player;
    this.container = container;
    this.data = containerData;

    log.info("CompanionsMenu: {}", this.player.getLevel().isClientSide);

    // Define slots and position on UI (note: order sensitive)
    this.container.setItem(0, new ItemStack(ModItems.COMPANION_GHOST.get()));
    this.addSlot(new FavouriteCompanionSlot(container, 0, 0, 0));

    // Player Inventory Slots
    int playerInventoryStartPositionY = 99;
    for (int inventoryRow = 0; inventoryRow < 3; ++inventoryRow) {
      for (int inventoryColumn = 0; inventoryColumn < 9; ++inventoryColumn) {
        this.addSlot(new Slot(playerInventory, inventoryColumn + inventoryRow * 9 + 9,
            slotSpacing + inventoryColumn * slotSize,
            playerInventoryStartPositionY + inventoryRow * slotSize));
      }
    }

    // Player Hotbar
    int hotbarStartPositionY = 157;
    for (int playerInventorySlot = 0; playerInventorySlot < 9; ++playerInventorySlot) {
      this.addSlot(new Slot(playerInventory, playerInventorySlot,
          slotSpacing + playerInventorySlot * slotSize, hotbarStartPositionY));
    }
  }

  @Override
  public boolean stillValid(Player player) {
    return this.container.stillValid(player);
  }

}
