/**
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

import net.minecraft.world.item.CreativeModeTab.DisplayItemsGenerator;
import net.minecraft.world.item.CreativeModeTab.ItemDisplayParameters;
import net.minecraft.world.item.CreativeModeTab.Output;

import de.markusbordihn.playercompanions.item.ModItems;

public class PlayerCompanionsItems implements DisplayItemsGenerator {
  protected PlayerCompanionsItems() {}

  @Override
  public void accept(ItemDisplayParameters itemDisplayParameters, Output output) {
    // Player Companion Items
    output.accept(ModItems.DOBUTSU_DEFAULT.get());
    output.accept(ModItems.DOBUTSU_CREEPER.get());
    output.accept(ModItems.DOBUTSU_ENDERMAN.get());
    output.accept(ModItems.FAIRY_DEFAULT.get());
    output.accept(ModItems.FAIRY_BLUE.get());
    output.accept(ModItems.FAIRY_RED.get());
    output.accept(ModItems.FIREFLY_DEFAULT.get());
    output.accept(ModItems.LIZARD_DEFAULT.get());
    output.accept(ModItems.LIZARD_DESERT.get());
    output.accept(ModItems.LIZARD_GREEN.get());
    output.accept(ModItems.SAMURAI_DEFAULT.get());
    output.accept(ModItems.SAMURAI_BLUE.get());
    output.accept(ModItems.SAMURAI_BLACK.get());
    output.accept(ModItems.PIG_DEFAULT.get());
    output.accept(ModItems.PIG_SPOTTED.get());
    output.accept(ModItems.RAPTOR_DEFAULT.get());
    output.accept(ModItems.RAPTOR_PINK.get());
    output.accept(ModItems.ROOSTER_DEFAULT.get());
    output.accept(ModItems.ROOSTER_MIXED.get());
    output.accept(ModItems.SMALL_GHAST_DEFAULT.get());
    output.accept(ModItems.SMALL_SLIME_DEFAULT.get());
    output.accept(ModItems.SMALL_SLIME_BLACK.get());
    output.accept(ModItems.SMALL_SLIME_BLUE.get());
    output.accept(ModItems.SMALL_SLIME_BROWN.get());
    output.accept(ModItems.SMALL_SLIME_CYAN.get());
    output.accept(ModItems.SMALL_SLIME_GRAY.get());
    output.accept(ModItems.SMALL_SLIME_GREEN.get());
    output.accept(ModItems.SMALL_SLIME_LIGHT_BLUE.get());
    output.accept(ModItems.SMALL_SLIME_LIGHT_GRAY.get());
    output.accept(ModItems.SMALL_SLIME_LIME.get());
    output.accept(ModItems.SMALL_SLIME_MAGENTA.get());
    output.accept(ModItems.SMALL_SLIME_ORANGE.get());
    output.accept(ModItems.SMALL_SLIME_PINK.get());
    output.accept(ModItems.SMALL_SLIME_PURPLE.get());
    output.accept(ModItems.SMALL_SLIME_RED.get());
    output.accept(ModItems.SMALL_SLIME_WHITE.get());
    output.accept(ModItems.SMALL_SLIME_YELLOW.get());
    output.accept(ModItems.SNAIL_DEFAULT.get());
    output.accept(ModItems.SNAIL_BROWN.get());
    output.accept(ModItems.WELSH_CORGI_DEFAULT.get());
    output.accept(ModItems.WELSH_CORGI_MIXED.get());
    output.accept(ModItems.WELSH_CORGI_BLACK.get());
  }
}
