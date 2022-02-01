/**
 * Copyright 2021 Markus Bordihn
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

package de.markusbordihn.playercompanions.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.MaterialColor;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.entity.ModEntityType;
import de.markusbordihn.playercompanions.tabs.PlayerCompanionsTab;
import de.markusbordihn.playercompanions.Annotations.TemplateEntryPoint;
import de.markusbordihn.playercompanions.block.ModBlocks;

public class ModItems {

  protected ModItems() {

  }

  public static final DeferredRegister<Item> ITEMS =
      DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);

  @TemplateEntryPoint("Register Items")

  @TemplateEntryPoint("Register Block Items")
  public static final RegistryObject<Item> COMPANION_GHOST =
      ITEMS.register("companion_ghost", () -> new BlockItem(ModBlocks.COMPANION_GHOST.get(),
          new Item.Properties()));

  @TemplateEntryPoint("Register Spawn Eggs")

  public static final RegistryObject<Item> SMALL_GHAST_SPAWN_EGG =
      ITEMS.register("small_ghast_spawn_egg",
          () -> new ForgeSpawnEggItem(ModEntityType.SMALL_GHAST::get, MaterialColor.GOLD.col,
              MaterialColor.SNOW.col,
              new Item.Properties().tab(PlayerCompanionsTab.TAB_SPAWN_EGGS)));
  public static final RegistryObject<Item> SMALL_SLIME_SPAWN_EGG =
      ITEMS.register("small_slime_spawn_egg",
          () -> new ForgeSpawnEggItem(ModEntityType.SMALL_SLIME::get, MaterialColor.GOLD.col,
              MaterialColor.COLOR_GREEN.col,
              new Item.Properties().tab(PlayerCompanionsTab.TAB_SPAWN_EGGS)));
}
