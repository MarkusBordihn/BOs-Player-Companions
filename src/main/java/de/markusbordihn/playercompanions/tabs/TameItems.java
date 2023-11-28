/*
 * Copyright 2023 Markus Bordihn
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

import de.markusbordihn.playercompanions.item.ModItems;
import net.minecraft.world.item.CreativeModeTab.DisplayItemsGenerator;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Output;

public class TameItems implements DisplayItemsGenerator {

  protected TameItems() {
  }

  @Override
  public void accept(ItemDisplayParameters itemDisplayParameters, Output output) {
    // Tame Items
    output.accept(ModItems.TAME_APPLE.get());
    output.accept(ModItems.TAME_BONE.get());
    output.accept(ModItems.TAME_CAKE.get());
    output.accept(ModItems.TAME_CARROT.get());
    output.accept(ModItems.TAME_HONEYCOMP.get());
    output.accept(ModItems.TAME_RAW_MUTTON.get());
    output.accept(ModItems.TAME_SEAGRASS.get());
    output.accept(ModItems.TAME_SWEET_BERRIES.get());
    output.accept(ModItems.TAME_WHEAT_SEEDS.get());
  }
}
