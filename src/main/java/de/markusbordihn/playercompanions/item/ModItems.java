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
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.MaterialColor;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.entity.ModEntityType;
import de.markusbordihn.playercompanions.item.companions.*;
import de.markusbordihn.playercompanions.item.tameitems.*;
import de.markusbordihn.playercompanions.tabs.PlayerCompanionsTab;
import de.markusbordihn.playercompanions.Annotations.TemplateEntryPoint;
import de.markusbordihn.playercompanions.block.ModBlocks;

public class ModItems {

  protected ModItems() {

  }

  public static final DeferredRegister<Item> ITEMS =
      DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);

  @TemplateEntryPoint("Register Items")

  // Captured Companions
  public static final RegistryObject<Item> FAIRY_DEFAULT =
      ITEMS.register(FairyItem.ID + "_default", FairyItem::new);

  public static final RegistryObject<Item> PIG = ITEMS.register(PigItem.ID, PigItem::new);

  public static final RegistryObject<Item> SMALL_GHAST =
      ITEMS.register(SmallGhastItem.ID, SmallGhastItem::new);

  public static final RegistryObject<Item> SMALL_SLIME_BLACK =
      ITEMS.register(SmallSlimeItem.ID + "_black", () -> new SmallSlimeItem(DyeColor.BLACK));
  public static final RegistryObject<Item> SMALL_SLIME_BLUE =
      ITEMS.register(SmallSlimeItem.ID + "_blue", () -> new SmallSlimeItem(DyeColor.BLUE));
  public static final RegistryObject<Item> SMALL_SLIME_BROWN =
      ITEMS.register(SmallSlimeItem.ID + "_brown", () -> new SmallSlimeItem(DyeColor.BROWN));
  public static final RegistryObject<Item> SMALL_SLIME_CYAN =
      ITEMS.register(SmallSlimeItem.ID + "_cyan", () -> new SmallSlimeItem(DyeColor.CYAN));
  public static final RegistryObject<Item> SMALL_SLIME_GRAY =
      ITEMS.register(SmallSlimeItem.ID + "_gray", () -> new SmallSlimeItem(DyeColor.GRAY));
  public static final RegistryObject<Item> SMALL_SLIME_GREEN =
      ITEMS.register(SmallSlimeItem.ID + "_green", () -> new SmallSlimeItem(DyeColor.GREEN));
  public static final RegistryObject<Item> SMALL_SLIME_LIGHT_BLUE =
      ITEMS.register(SmallSlimeItem.ID + "_light_blue", () -> new SmallSlimeItem(DyeColor.BLUE));
  public static final RegistryObject<Item> SMALL_SLIME_LIGHT_GRAY = ITEMS
      .register(SmallSlimeItem.ID + "_light_gray", () -> new SmallSlimeItem(DyeColor.LIGHT_GRAY));
  public static final RegistryObject<Item> SMALL_SLIME_LIME =
      ITEMS.register(SmallSlimeItem.ID + "_lime", () -> new SmallSlimeItem(DyeColor.LIME));
  public static final RegistryObject<Item> SMALL_SLIME_MAGENTA =
      ITEMS.register(SmallSlimeItem.ID + "_magenta", () -> new SmallSlimeItem(DyeColor.MAGENTA));
  public static final RegistryObject<Item> SMALL_SLIME_ORANGE =
      ITEMS.register(SmallSlimeItem.ID + "_orange", () -> new SmallSlimeItem(DyeColor.ORANGE));
  public static final RegistryObject<Item> SMALL_SLIME_PINK =
      ITEMS.register(SmallSlimeItem.ID + "_pink", () -> new SmallSlimeItem(DyeColor.PINK));
  public static final RegistryObject<Item> SMALL_SLIME_PURPLE =
      ITEMS.register(SmallSlimeItem.ID + "_purple", () -> new SmallSlimeItem(DyeColor.PURPLE));
  public static final RegistryObject<Item> SMALL_SLIME_RED =
      ITEMS.register(SmallSlimeItem.ID + "_red", () -> new SmallSlimeItem(DyeColor.RED));
  public static final RegistryObject<Item> SMALL_SLIME_WHITE =
      ITEMS.register(SmallSlimeItem.ID + "_white", () -> new SmallSlimeItem(DyeColor.WHITE));
  public static final RegistryObject<Item> SMALL_SLIME_YELLOW =
      ITEMS.register(SmallSlimeItem.ID + "_yellow", () -> new SmallSlimeItem(DyeColor.YELLOW));

  public static final RegistryObject<Item> SNAIL = ITEMS.register(SnailItem.ID, SnailItem::new);

  // Tame Items
  public static final RegistryObject<Item> TAME_APPLE =
      ITEMS.register("tame_apple", TameApple::new);
  public static final RegistryObject<Item> TAME_BONE = ITEMS.register("tame_bone", TameBone::new);
  public static final RegistryObject<Item> TAME_CARROT =
      ITEMS.register("tame_carrot", TameCarrot::new);
  public static final RegistryObject<Item> TAME_CAKE = ITEMS.register("tame_cake", TameCake::new);
  public static final RegistryObject<Item> TAME_SEAGRASS =
      ITEMS.register("tame_seagrass", TameBone::new);

  @TemplateEntryPoint("Register Block Items")

  public static final RegistryObject<Item> COMPANION_GHOST = ITEMS.register("companion_ghost",
      () -> new BlockItem(ModBlocks.COMPANION_GHOST.get(), new Item.Properties()));

  @TemplateEntryPoint("Register Spawn Eggs")

  public static final RegistryObject<Item> FAIRY_SPAWN_EGG = ITEMS.register("fairy_spawn_egg",
      () -> new ForgeSpawnEggItem(ModEntityType.FAIRY::get, MaterialColor.GOLD.col,
          MaterialColor.SNOW.col, new Item.Properties().tab(PlayerCompanionsTab.TAB_SPAWN_EGGS)));
  public static final RegistryObject<Item> PIG_SPAWN_EGG = ITEMS.register("pig_spawn_egg",
      () -> new ForgeSpawnEggItem(ModEntityType.PIG::get, MaterialColor.GOLD.col,
          MaterialColor.COLOR_PINK.col,
          new Item.Properties().tab(PlayerCompanionsTab.TAB_SPAWN_EGGS)));
  public static final RegistryObject<Item> SNAIL_SPAWN_EGG = ITEMS.register("snail_spawn_egg",
      () -> new ForgeSpawnEggItem(ModEntityType.SNAIL::get, MaterialColor.GOLD.col,
          MaterialColor.SNOW.col, new Item.Properties().tab(PlayerCompanionsTab.TAB_SPAWN_EGGS)));
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
