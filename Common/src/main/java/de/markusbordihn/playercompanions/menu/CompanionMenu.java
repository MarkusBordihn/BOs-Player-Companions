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

import de.markusbordihn.playercompanions.entity.companion.PlayerCompanion;
import de.markusbordihn.playercompanions.menu.slots.CompanionArmorSlot;
import de.markusbordihn.playercompanions.menu.slots.CompanionHandSlot;
import de.markusbordihn.playercompanions.menu.slots.DummySlot;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class CompanionMenu extends AbstractContainerMenu {

  protected static final Logger log = LogManager.getLogger(CompanionMenu.class);

  protected static final int SLOT_SIZE = 18;
  private static final int ARMOR_X = 6;
  private static final int ARMOR_Y = 17;
  private static final int EQUIP_X = 96;
  private static final int EQUIP_Y = 17;
  private static final int EQUIP_COUNT = 4;
  private static final int MAINHAND_X = 6;
  private static final int MAINHAND_Y = 89;
  private static final int OFFHAND_X = 96;
  private static final int OFFHAND_Y = 89;
  private static final int PLAYER_INV_X = 6;
  private static final int PLAYER_INV_Y = 151;
  private static final int HOTBAR_Y = 209;

  protected final UUID companionUUID;
  protected final SimpleContainer armorContainer;
  protected final SimpleContainer equipmentContainer;
  protected final SimpleContainer handContainer;
  protected final Level level;
  protected final Player player;

  protected CompanionMenu(MenuType<?> menuType, int windowId, Inventory playerInventory,
    UUID companionUUID) {
    super(menuType, windowId);
    this.companionUUID = companionUUID;
    this.player = playerInventory.player;
    this.level = playerInventory.player.level();
    this.armorContainer = new SimpleContainer(4);
    this.equipmentContainer = new SimpleContainer(EQUIP_COUNT);
    this.handContainer = new SimpleContainer(2);

    if (!this.level.isClientSide) {
      loadCompanionEquipment();
    }

    addCompanionSlots();
    addRoleSlots();
    addPlayerInventorySlots(playerInventory);
  }

  protected CompanionMenu(MenuType<?> menuType, int windowId, Inventory playerInventory,
    FriendlyByteBuf data) {
    this(menuType, windowId, playerInventory, data.readUUID());
  }

  private void loadCompanionEquipment() {
    Mob companion = findCompanion();
    if (companion == null) {
      return;
    }
    armorContainer.setItem(0, companion.getItemBySlot(EquipmentSlot.HEAD));
    armorContainer.setItem(1, companion.getItemBySlot(EquipmentSlot.CHEST));
    armorContainer.setItem(2, companion.getItemBySlot(EquipmentSlot.LEGS));
    armorContainer.setItem(3, companion.getItemBySlot(EquipmentSlot.FEET));
    handContainer.setItem(0, companion.getItemBySlot(EquipmentSlot.MAINHAND));
    handContainer.setItem(1, companion.getItemBySlot(EquipmentSlot.OFFHAND));
  }

  private void addCompanionSlots() {
    EquipmentSlot[] armorTypes = {
      EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    // Armor slots
    for (int i = 0; i < 4; i++) {
      addSlot(new CompanionArmorSlot(armorContainer, i, ARMOR_X, ARMOR_Y + i * SLOT_SIZE,
        armorTypes[i]));
    }
    // Equipment/dummy slots
    for (int i = 0; i < EQUIP_COUNT; i++) {
      addSlot(new DummySlot(equipmentContainer, i, EQUIP_X, EQUIP_Y + i * SLOT_SIZE));
    }
    // Hand slots
    addSlot(new CompanionHandSlot(handContainer, EquipmentSlot.MAINHAND.getIndex(),
      MAINHAND_X, MAINHAND_Y, EquipmentSlot.MAINHAND));
    addSlot(new CompanionHandSlot(handContainer, EquipmentSlot.OFFHAND.getIndex(),
      OFFHAND_X, OFFHAND_Y, EquipmentSlot.OFFHAND));
  }

  protected abstract void addRoleSlots();

  private void addPlayerInventorySlots(Inventory playerInventory) {
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 9; col++) {
        addSlot(new Slot(playerInventory, col + row * 9 + 9,
          PLAYER_INV_X + col * SLOT_SIZE, PLAYER_INV_Y + row * SLOT_SIZE));
      }
    }
    for (int col = 0; col < 9; col++) {
      addSlot(new Slot(playerInventory, col, PLAYER_INV_X + col * SLOT_SIZE, HOTBAR_Y));
    }
  }

  protected Mob findCompanion() {
    return level.getEntitiesOfClass(Mob.class,
        player.getBoundingBox().inflate(64),
        e -> e instanceof PlayerCompanion && e.getUUID().equals(companionUUID)).stream()
      .findFirst().orElse(null);
  }

  public UUID getCompanionUUID() {
    return companionUUID;
  }

  @Override
  public boolean stillValid(Player player) {
    return player.isAlive();
  }

  @Override
  public ItemStack quickMoveStack(Player player, int slotIndex) {
    Slot slot = slots.get(slotIndex);
    if (!slot.hasItem()) {
      return ItemStack.EMPTY;
    }

    ItemStack stack = slot.getItem().copy();
    int companionSlots = 4 + EQUIP_COUNT + 2 + getRoleSlotCount();
    int totalSlots = slots.size();

    if (slotIndex < companionSlots) {
      if (!moveItemStackTo(stack, companionSlots, totalSlots, false)) {
        return ItemStack.EMPTY;
      }
    } else {
      if (!moveItemStackTo(stack, 0, companionSlots, false)) {
        return ItemStack.EMPTY;
      }
    }

    if (stack.isEmpty()) {
      slot.set(ItemStack.EMPTY);
    } else {
      slot.setChanged();
    }
    return stack;
  }

  protected int getRoleSlotCount() {
    return 0;
  }
}
