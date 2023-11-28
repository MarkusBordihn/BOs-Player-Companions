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

public class SpawnEggsItems implements DisplayItemsGenerator {
  protected SpawnEggsItems() {}

  @Override
  public void accept(ItemDisplayParameters itemDisplayParameters, Output output) {
    // Player Companion Spawn Eggs
    output.accept(ModItems.DOBUTSU_SPAWN_EGG.get());
    output.accept(ModItems.FAIRY_SPAWN_EGG.get());
    output.accept(ModItems.FIREFLY_SPAWN_EGG.get());
    output.accept(ModItems.LIZARD_SPAWN_EGG.get());
    output.accept(ModItems.PIG_SPAWN_EGG.get());
    output.accept(ModItems.RAPTOR_SPAWN_EGG.get());
    output.accept(ModItems.ROOSTER_SPAWN_EGG.get());
    output.accept(ModItems.SAMURAI_SPAWN_EGG.get());
    output.accept(ModItems.SMALL_GHAST_SPAWN_EGG.get());
    output.accept(ModItems.SMALL_SLIME_SPAWN_EGG.get());
    output.accept(ModItems.SNAIL_SPAWN_EGG.get());
    output.accept(ModItems.WELSH_CORGI_SPAWN_EGG.get());
  }
}
