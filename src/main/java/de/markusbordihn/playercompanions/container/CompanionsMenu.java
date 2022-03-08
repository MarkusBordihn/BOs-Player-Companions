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

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.container.slots.ArmorSlot;
import de.markusbordihn.playercompanions.container.slots.DummySlot;
import de.markusbordihn.playercompanions.container.slots.HandSlot;
import de.markusbordihn.playercompanions.container.slots.InventorySlot;
import de.markusbordihn.playercompanions.data.PlayerCompanionData;
import de.markusbordihn.playercompanions.data.PlayerCompanionsClientData;
import de.markusbordihn.playercompanions.data.PlayerCompanionsServerData;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.entity.type.PlayerCompanionType;

public class CompanionsMenu extends AbstractContainerMenu {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  // Defining basic layout options
  private static int armorContainerSize = 4;
  private static int equipmentContainerSize = 8;
  private static int handContainerSize = 2;
  private static int inventoryContainerSize = 16;
  private static int slotSize = 18;

  // Define containers
  private final Container armorContainer;
  private final Container equipmentContainer;
  private final Container handContainer;
  private final Container inventoryContainer;

  // Define others
  private PlayerCompanionEntity playerCompanionEntity;
  private final Level level;
  private final Player player;
  private final PlayerCompanionData playerCompanionData;
  private final UUID playerCompanionUUID;

  // Define states
  private boolean dataLoaded = false;

  public CompanionsMenu(int windowId, Inventory inventory, UUID playerCompanionUUID) {
    this(windowId, inventory, new SimpleContainer(armorContainerSize),
        new SimpleContainer(equipmentContainerSize), new SimpleContainer(handContainerSize),
        new SimpleContainer(inventoryContainerSize), playerCompanionUUID);
  }

  public CompanionsMenu(int windowId, Inventory playerInventory, FriendlyByteBuf data) {
    this(windowId, playerInventory, new SimpleContainer(armorContainerSize),
        new SimpleContainer(equipmentContainerSize), new SimpleContainer(handContainerSize),
        new SimpleContainer(inventoryContainerSize), data.readUUID());
  }

  public CompanionsMenu(final int windowId, final Inventory playerInventory,
      final Container armorContainer, final Container equipmentContainer,
      final Container handContainer, final Container inventoryContainer, UUID playerCompanionUUID) {
    super(ModContainer.COMPANIONS_MENU.get(), windowId);

    // Make sure the passed container matched the expected sizes
    checkContainerSize(armorContainer, armorContainerSize);
    checkContainerSize(equipmentContainer, equipmentContainerSize);
    checkContainerSize(handContainer, handContainerSize);
    checkContainerSize(inventoryContainer, inventoryContainerSize);

    this.dataLoaded = false;

    // Container
    this.armorContainer = armorContainer;
    this.equipmentContainer = equipmentContainer;
    this.handContainer = handContainer;
    this.inventoryContainer = inventoryContainer;

    // Other
    this.player = playerInventory.player;
    this.playerCompanionUUID = playerCompanionUUID;
    this.level = this.player.getLevel();

    // Getting relevant data from client or server side.
    if (this.level.isClientSide) {
      this.playerCompanionData = PlayerCompanionsClientData.getCompanion(this.playerCompanionUUID);
    } else {
      this.playerCompanionData =
          PlayerCompanionsServerData.get().getCompanion(this.playerCompanionUUID);
    }
    if (this.playerCompanionData == null) {
      log.error("Unable to find Player Companion Data for {} on {}!", playerCompanionUUID,
          this.level.isClientSide ? "Client" : "Server");
      return;
    }

    // Get relevant entity, if available.
    this.playerCompanionEntity = this.playerCompanionData.getPlayerCompanionEntity();

    // Loading armor, hand and inventory on server side.
    if (!this.level.isClientSide) {
      loadArmor();
      loadHand();
      loadInventory();
    }

    PlayerCompanionType companionType = this.playerCompanionData.getType();
    log.debug("CompanionsMenu client:{} companion:{} data:{} entity:{}", this.level.isClientSide,
        this.playerCompanionUUID, this.playerCompanionData, this.playerCompanionEntity);

    // Player Companion Amor Slots (left / slot: 3 - 0)
    int playerCompanionEquipmentLeftStartPositionY = 17;
    int playerCompanionEquipmentLeftStartPositionX = 6;
    for (int armorSlot = 3; armorSlot >= 0; armorSlot--) {
      this.addSlot(new ArmorSlot(this, this.armorContainer, 3 - armorSlot,
          playerCompanionEquipmentLeftStartPositionX,
          playerCompanionEquipmentLeftStartPositionY + armorSlot * slotSize));
    }

    // Player Companion Equipment Slots (right / slot: 4 - 7)
    int playerCompanionEquipmentRightStartPositionY = 17;
    int playerCompanionEquipmentRightStartPositionX = 96;
    for (int equipmentSlot = 0; equipmentSlot < 4; ++equipmentSlot) {
      this.addSlot(new DummySlot(this.equipmentContainer, equipmentSlot + 4,
          playerCompanionEquipmentRightStartPositionX,
          playerCompanionEquipmentRightStartPositionY + equipmentSlot * slotSize));
    }

    // Player Companion Main Hand Slot (left / bottom: 0)
    int playerCompanionMainHandStartPositionY = 89;
    int playerCompanionMainHandStartPositionX = 6;
    this.addSlot(new HandSlot(this, this.handContainer, EquipmentSlot.MAINHAND.getIndex(),
        playerCompanionMainHandStartPositionX, playerCompanionMainHandStartPositionY));

    // Player Companion Off Hand Slot (right / bottom: 1)
    int playerCompanionOffHandStartPositionY = 89;
    int playerCompanionOffHandStartPositionX = 96;
    this.addSlot(new HandSlot(this, this.handContainer, EquipmentSlot.OFFHAND.getIndex(),
        playerCompanionOffHandStartPositionX, playerCompanionOffHandStartPositionY));

    // Player Companion Inventory Slots
    if (companionType == PlayerCompanionType.COLLECTOR) {
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

    // Player Inventory Slots
    int playerInventoryStartPositionY = 151;
    int playerInventoryStartPositionX = 6;
    for (int inventoryRow = 0; inventoryRow < 3; ++inventoryRow) {
      for (int inventoryColumn = 0; inventoryColumn < 9; ++inventoryColumn) {
        this.addSlot(new Slot(playerInventory, inventoryColumn + inventoryRow * 9 + 9,
            playerInventoryStartPositionX + inventoryColumn * slotSize,
            playerInventoryStartPositionY + inventoryRow * slotSize));
      }
    }

    // Player Hotbar Slots
    int hotbarStartPositionY = 209;
    int hotbarStartPositionX = 6;
    for (int playerInventorySlot = 0; playerInventorySlot < 9; ++playerInventorySlot) {
      this.addSlot(new Slot(playerInventory, playerInventorySlot,
          hotbarStartPositionX + playerInventorySlot * slotSize, hotbarStartPositionY));
    }

    if (!this.level.isClientSide) {
      this.dataLoaded = true;
    }
  }

  public void loadArmor() {
    if (this.level.isClientSide || this.playerCompanionData == null) {
      return;
    }
    NonNullList<ItemStack> armor = this.playerCompanionData.getArmorItems();
    for (int index = 0; index < armor.size(); index++) {
      this.armorContainer.setItem(index, armor.get(index));
    }
  }

  public void saveArmor() {
    if (this.playerCompanionData == null) {
      return;
    }
    for (int index = 0; index < armorContainerSize; index++) {
      this.playerCompanionData.setArmorItem(index, this.armorContainer.getItem(index));
    }
  }

  public void setArmorChanged(EquipmentSlot equipmentSlot, int slot, ItemStack itemStack) {
    if (this.level.isClientSide) {
      return;
    }
    // Armor inventory should be synced to entity directly, if possible.
    if (this.playerCompanionEntity != null) {
      this.playerCompanionEntity.setArmorItem(equipmentSlot, itemStack);
    } else if (this.dataLoaded && this.playerCompanionData != null) {
      this.playerCompanionData.setArmorItem(slot, itemStack);
    }
  }

  public void loadHand() {
    if (this.level.isClientSide || this.playerCompanionData == null) {
      return;
    }
    NonNullList<ItemStack> hand = this.playerCompanionData.getHandItems();
    for (int index = 0; index < hand.size(); index++) {
      this.handContainer.setItem(index, hand.get(index));
    }
  }

  public void setHandChanged(int slot, ItemStack itemStack) {
    if (this.level.isClientSide) {
      return;
    }
    // Hand inventory should be synced to entity directly, if possible.
    if (this.playerCompanionEntity != null) {
      this.playerCompanionEntity.setHandItem(slot, itemStack);
    } else if (this.dataLoaded && this.playerCompanionData != null) {
      this.playerCompanionData.setHandItem(slot, itemStack);
    }
  }

  public void loadInventory() {
    if (this.level.isClientSide || this.playerCompanionData == null) {
      return;
    }
    NonNullList<ItemStack> inventory = this.playerCompanionData.getInventoryItems();
    for (int index = 0; index < inventory.size(); index++) {
      this.inventoryContainer.setItem(index, inventory.get(index));
    }
  }

  public void setInventoryChanged(int slot, ItemStack itemStack) {
    if (this.level.isClientSide) {
      return;
    }
    // Inventory only needs to be synced to our own data.
    if (this.dataLoaded && this.playerCompanionData != null) {
      this.playerCompanionData.setInventoryItem(slot, itemStack);
    }
  }

  public Entity getPlayerCompanionEntity() {
    if (this.playerCompanionData == null) {
      return null;
    }
    return this.level.getEntity(this.playerCompanionData.getEntityId());
  }

  @Override
  public ItemStack quickMoveStack(Player player, int slotIndex) {
    Slot slot = this.slots.get(slotIndex);
    if (!slot.hasItem()) {
      return ItemStack.EMPTY;
    }

    ItemStack itemStack = slot.getItem();

    // Store changes if itemStack is not empty.
    if (itemStack.isEmpty()) {
      slot.set(ItemStack.EMPTY);
    } else {
      slot.setChanged();
    }

    return ItemStack.EMPTY;
  }

  @Override
  public boolean stillValid(Player player) {
    return this.inventoryContainer.stillValid(player);
  }

}
