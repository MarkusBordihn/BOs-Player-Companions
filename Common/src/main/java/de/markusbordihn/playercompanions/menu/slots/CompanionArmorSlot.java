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

package de.markusbordihn.playercompanions.menu.slots;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class CompanionArmorSlot extends Slot {

  private static final ResourceLocation[] EMPTY_ICONS = {
    InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS,
    InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS,
    InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE,
    InventoryMenu.EMPTY_ARMOR_SLOT_HELMET
  };

  private final EquipmentSlot equipmentSlot;

  public CompanionArmorSlot(Container container, int index, int x, int y,
    EquipmentSlot equipmentSlot) {
    super(container, index, x, y);
    this.equipmentSlot = equipmentSlot;
  }

  @Override
  public boolean mayPlace(ItemStack stack) {
    if (stack.isEmpty()) {
      return false;
    }
    if (stack.getItem() instanceof Equipable equipable) {
      return equipable.getEquipmentSlot() == equipmentSlot;
    }
    return false;
  }

  @Override
  public boolean mayPickup(net.minecraft.world.entity.player.Player player) {
    ItemStack stack = getItem();
    return (stack.isEmpty() || player.isCreative()
      || !EnchantmentHelper.hasBindingCurse(stack)) && super.mayPickup(player);
  }

  @Override
  public int getMaxStackSize() {
    return 1;
  }

  @Override
  public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
    return Pair.of(InventoryMenu.BLOCK_ATLAS, EMPTY_ICONS[equipmentSlot.getIndex()]);
  }
}
