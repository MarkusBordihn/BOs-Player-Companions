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

package de.markusbordihn.playercompanions.tabs;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import de.markusbordihn.playercompanions.item.ModItems;

public class PlayerCompanionsTab {

  protected PlayerCompanionsTab() {}

  public static final CreativeModeTab TAB_SPAWN_EGGS = new CreativeModeTab("player_companions.spawn_eggs") {
    public ItemStack makeIcon() {
      return new ItemStack(ModItems.SMALL_GHAST_SPAWN_EGG.get());
    }
  };

  public static final CreativeModeTab TAB_COMPANIONS = new CreativeModeTab("player_companions") {
    public ItemStack makeIcon() {
      return new ItemStack(ModItems.SMALL_SLIME_GREEN.get());
    }
  };

  public static final CreativeModeTab TAB_TAME_ITEMS = new CreativeModeTab("player_companions.tame_items") {
        public ItemStack makeIcon() {
      return new ItemStack(ModItems.TAME_APPLE.get());
    }
  };

}
