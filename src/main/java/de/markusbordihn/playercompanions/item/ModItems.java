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

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.MaterialColor;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.entity.PlayerCompanionVariant;
import de.markusbordihn.playercompanions.entity.companions.*;
import de.markusbordihn.playercompanions.item.companions.*;
import de.markusbordihn.playercompanions.item.tameitems.*;
import de.markusbordihn.playercompanions.tabs.PlayerCompanionsTab;
import de.markusbordihn.playercompanions.Annotations.TemplateEntryPoint;

public class ModItems {

  protected ModItems() {

  }

  public static final DeferredRegister<Item> ITEMS =
      DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);

  @TemplateEntryPoint("Register Items")

  // Dobutsu
  public static final RegistryObject<Item> DOBUTSU_DEFAULT =
      ITEMS.register(DobutsuItem.ID + PlayerCompanionVariant.DEFAULT.getSuffix(),
          () -> new DobutsuItem(PlayerCompanionVariant.DEFAULT));
  public static final RegistryObject<Item> DOBUTSU_CREEPER =
      ITEMS.register(DobutsuItem.ID + PlayerCompanionVariant.CREEPER.getSuffix(),
          () -> new DobutsuItem(PlayerCompanionVariant.CREEPER));
  public static final RegistryObject<Item> DOBUTSU_ENDERMAN =
      ITEMS.register(DobutsuItem.ID + PlayerCompanionVariant.ENDERMAN.getSuffix(),
          () -> new DobutsuItem(PlayerCompanionVariant.ENDERMAN));

  // Fairies
  public static final RegistryObject<Item> FAIRY_DEFAULT =
      ITEMS.register(FairyItem.ID + PlayerCompanionVariant.DEFAULT.getSuffix(),
          () -> new FairyItem(PlayerCompanionVariant.DEFAULT));
  public static final RegistryObject<Item> FAIRY_BLUE =
      ITEMS.register(FairyItem.ID + PlayerCompanionVariant.BLUE.getSuffix(),
          () -> new FairyItem(PlayerCompanionVariant.BLUE));
  public static final RegistryObject<Item> FAIRY_RED =
      ITEMS.register(FairyItem.ID + PlayerCompanionVariant.RED.getSuffix(),
          () -> new FairyItem(PlayerCompanionVariant.RED));

  // Firefly
  public static final RegistryObject<Item> FIREFLY_DEFAULT =
      ITEMS.register(FireflyItem.ID + PlayerCompanionVariant.DEFAULT.getSuffix(),
          () -> new FireflyItem(PlayerCompanionVariant.DEFAULT));

  // Lizard
  public static final RegistryObject<Item> LIZARD_DEFAULT =
      ITEMS.register(LizardItem.ID + PlayerCompanionVariant.DEFAULT.getSuffix(),
          () -> new LizardItem(PlayerCompanionVariant.DEFAULT));
  public static final RegistryObject<Item> LIZARD_DESERT =
      ITEMS.register(LizardItem.ID + PlayerCompanionVariant.DESERT.getSuffix(),
          () -> new LizardItem(PlayerCompanionVariant.DESERT));
  public static final RegistryObject<Item> LIZARD_GREEN =
      ITEMS.register(LizardItem.ID + PlayerCompanionVariant.GREEN.getSuffix(),
          () -> new LizardItem(PlayerCompanionVariant.GREEN));

  // Samurai
  public static final RegistryObject<Item> SAMURAI_DEFAULT =
      ITEMS.register(SamuraiItem.ID + PlayerCompanionVariant.DEFAULT.getSuffix(),
          () -> new SamuraiItem(PlayerCompanionVariant.DEFAULT));
  public static final RegistryObject<Item> SAMURAI_BLUE =
      ITEMS.register(SamuraiItem.ID + PlayerCompanionVariant.BLUE.getSuffix(),
          () -> new SamuraiItem(PlayerCompanionVariant.BLUE));
  public static final RegistryObject<Item> SAMURAI_BLACK =
      ITEMS.register(SamuraiItem.ID + PlayerCompanionVariant.BLACK.getSuffix(),
          () -> new SamuraiItem(PlayerCompanionVariant.BLACK));

  // Pigs
  public static final RegistryObject<Item> PIG_DEFAULT =
      ITEMS.register(PigItem.ID + PlayerCompanionVariant.DEFAULT.getSuffix(),
          () -> new PigItem(PlayerCompanionVariant.DEFAULT));
  public static final RegistryObject<Item> PIG_SPOTTED =
      ITEMS.register(PigItem.ID + PlayerCompanionVariant.SPOTTED.getSuffix(),
          () -> new PigItem(PlayerCompanionVariant.SPOTTED));

  // Rooster
  public static final RegistryObject<Item> ROOSTER_DEFAULT =
      ITEMS.register(RoosterItem.ID + PlayerCompanionVariant.DEFAULT.getSuffix(),
          () -> new RoosterItem(PlayerCompanionVariant.DEFAULT));
  public static final RegistryObject<Item> ROOSTER_MIXED =
      ITEMS.register(RoosterItem.ID + PlayerCompanionVariant.MIXED.getSuffix(),
          () -> new RoosterItem(PlayerCompanionVariant.MIXED));

  // Small Ghast
  public static final RegistryObject<Item> SMALL_GHAST_DEFAULT =
      ITEMS.register(SmallGhastItem.ID + PlayerCompanionVariant.DEFAULT.getSuffix(),
          () -> new SmallGhastItem(PlayerCompanionVariant.DEFAULT));

  // Slimes
  public static final RegistryObject<Item> SMALL_SLIME_DEFAULT =
      ITEMS.register(SmallSlimeItem.ID + PlayerCompanionVariant.DEFAULT.getSuffix(),
          () -> new SmallSlimeItem(PlayerCompanionVariant.DEFAULT));
  public static final RegistryObject<Item> SMALL_SLIME_BLACK =
      ITEMS.register(SmallSlimeItem.ID + PlayerCompanionVariant.BLACK.getSuffix(),
          () -> new SmallSlimeItem(PlayerCompanionVariant.BLACK));
  public static final RegistryObject<Item> SMALL_SLIME_BLUE =
      ITEMS.register(SmallSlimeItem.ID + PlayerCompanionVariant.BLUE.getSuffix(),
          () -> new SmallSlimeItem(PlayerCompanionVariant.BLUE));
  public static final RegistryObject<Item> SMALL_SLIME_BROWN =
      ITEMS.register(SmallSlimeItem.ID + PlayerCompanionVariant.BROWN.getSuffix(),
          () -> new SmallSlimeItem(PlayerCompanionVariant.BROWN));
  public static final RegistryObject<Item> SMALL_SLIME_CYAN =
      ITEMS.register(SmallSlimeItem.ID + PlayerCompanionVariant.CYAN.getSuffix(),
          () -> new SmallSlimeItem(PlayerCompanionVariant.CYAN));
  public static final RegistryObject<Item> SMALL_SLIME_GRAY =
      ITEMS.register(SmallSlimeItem.ID + PlayerCompanionVariant.GRAY.getSuffix(),
          () -> new SmallSlimeItem(PlayerCompanionVariant.GRAY));
  public static final RegistryObject<Item> SMALL_SLIME_GREEN =
      ITEMS.register(SmallSlimeItem.ID + PlayerCompanionVariant.GREEN.getSuffix(),
          () -> new SmallSlimeItem(PlayerCompanionVariant.GREEN));
  public static final RegistryObject<Item> SMALL_SLIME_LIGHT_BLUE =
      ITEMS.register(SmallSlimeItem.ID + PlayerCompanionVariant.LIGHT_BLUE.getSuffix(),
          () -> new SmallSlimeItem(PlayerCompanionVariant.LIGHT_BLUE));
  public static final RegistryObject<Item> SMALL_SLIME_LIGHT_GRAY =
      ITEMS.register(SmallSlimeItem.ID + PlayerCompanionVariant.LIGHT_GRAY.getSuffix(),
          () -> new SmallSlimeItem(PlayerCompanionVariant.LIGHT_GRAY));
  public static final RegistryObject<Item> SMALL_SLIME_LIME =
      ITEMS.register(SmallSlimeItem.ID + PlayerCompanionVariant.LIME.getSuffix(),
          () -> new SmallSlimeItem(PlayerCompanionVariant.LIME));
  public static final RegistryObject<Item> SMALL_SLIME_MAGENTA =
      ITEMS.register(SmallSlimeItem.ID + PlayerCompanionVariant.MAGENTA.getSuffix(),
          () -> new SmallSlimeItem(PlayerCompanionVariant.MAGENTA));
  public static final RegistryObject<Item> SMALL_SLIME_ORANGE =
      ITEMS.register(SmallSlimeItem.ID + PlayerCompanionVariant.ORANGE.getSuffix(),
          () -> new SmallSlimeItem(PlayerCompanionVariant.ORANGE));
  public static final RegistryObject<Item> SMALL_SLIME_PINK =
      ITEMS.register(SmallSlimeItem.ID + PlayerCompanionVariant.PINK.getSuffix(),
          () -> new SmallSlimeItem(PlayerCompanionVariant.PINK));
  public static final RegistryObject<Item> SMALL_SLIME_PURPLE =
      ITEMS.register(SmallSlimeItem.ID + PlayerCompanionVariant.PURPLE.getSuffix(),
          () -> new SmallSlimeItem(PlayerCompanionVariant.PURPLE));
  public static final RegistryObject<Item> SMALL_SLIME_RED =
      ITEMS.register(SmallSlimeItem.ID + PlayerCompanionVariant.RED.getSuffix(),
          () -> new SmallSlimeItem(PlayerCompanionVariant.RED));
  public static final RegistryObject<Item> SMALL_SLIME_WHITE =
      ITEMS.register(SmallSlimeItem.ID + PlayerCompanionVariant.WHITE.getSuffix(),
          () -> new SmallSlimeItem(PlayerCompanionVariant.WHITE));
  public static final RegistryObject<Item> SMALL_SLIME_YELLOW =
      ITEMS.register(SmallSlimeItem.ID + PlayerCompanionVariant.YELLOW.getSuffix(),
          () -> new SmallSlimeItem(PlayerCompanionVariant.YELLOW));

  // Snail
  public static final RegistryObject<Item> SNAIL_DEFAULT =
      ITEMS.register(SnailItem.ID + PlayerCompanionVariant.DEFAULT.getSuffix(),
          () -> new SnailItem(PlayerCompanionVariant.DEFAULT));
  public static final RegistryObject<Item> SNAIL_BROWN =
      ITEMS.register(SnailItem.ID + PlayerCompanionVariant.BROWN.getSuffix(),
          () -> new SnailItem(PlayerCompanionVariant.BROWN));

  // Welsh Corgi
  public static final RegistryObject<Item> WELSH_CORGI_DEFAULT =
      ITEMS.register(WelshCorgiItem.ID + PlayerCompanionVariant.DEFAULT.getSuffix(),
          () -> new WelshCorgiItem(PlayerCompanionVariant.DEFAULT));
  public static final RegistryObject<Item> WELSH_CORGI_MIXED =
      ITEMS.register(WelshCorgiItem.ID + PlayerCompanionVariant.MIXED.getSuffix(),
          () -> new WelshCorgiItem(PlayerCompanionVariant.MIXED));
  public static final RegistryObject<Item> WELSH_CORGI_BLACK =
      ITEMS.register(WelshCorgiItem.ID + PlayerCompanionVariant.BLACK.getSuffix(),
          () -> new WelshCorgiItem(PlayerCompanionVariant.BLACK));

  // Tame Items
  public static final RegistryObject<Item> TAME_APPLE =
      ITEMS.register("tame_apple", TameApple::new);
  public static final RegistryObject<Item> TAME_BONE = ITEMS.register("tame_bone", TameBone::new);
  public static final RegistryObject<Item> TAME_CAKE = ITEMS.register("tame_cake", TameCake::new);
  public static final RegistryObject<Item> TAME_CARROT =
      ITEMS.register("tame_carrot", TameCarrot::new);
  public static final RegistryObject<Item> TAME_HONEYCOMP =
      ITEMS.register("tame_honeycomb", TameHoneycomp::new);
  public static final RegistryObject<Item> TAME_SEAGRASS =
      ITEMS.register("tame_seagrass", TameSeagrass::new);
  public static final RegistryObject<Item> TAME_SWEET_BERRIES =
      ITEMS.register("tame_sweet_berries", TameSweetBerries::new);
  public static final RegistryObject<Item> TAME_WHEAT_SEEDS =
      ITEMS.register("tame_wheat_seeds", TameWheatSeeds::new);

  @TemplateEntryPoint("Register Block Items")

  @TemplateEntryPoint("Register Spawn Eggs")
  public static final RegistryObject<Item> DOBUTSU_SPAWN_EGG = ITEMS.register("dobutsu_spawn_egg",
      () -> new ForgeSpawnEggItem(ModEntityType.DOBUTSU::get, MaterialColor.COLOR_PURPLE.col,
          MaterialColor.COLOR_PURPLE.col,
          new Item.Properties().rarity(Rarity.EPIC).tab(PlayerCompanionsTab.TAB_SPAWN_EGGS)));
  public static final RegistryObject<Item> FAIRY_SPAWN_EGG = ITEMS.register("fairy_spawn_egg",
      () -> new ForgeSpawnEggItem(ModEntityType.FAIRY::get, MaterialColor.COLOR_PURPLE.col,
          MaterialColor.COLOR_BLUE.col,
          new Item.Properties().rarity(Rarity.EPIC).tab(PlayerCompanionsTab.TAB_SPAWN_EGGS)));
  public static final RegistryObject<Item> FIREFLY_SPAWN_EGG = ITEMS.register("firefly_spawn_egg",
      () -> new ForgeSpawnEggItem(ModEntityType.FIREFLY::get, MaterialColor.COLOR_YELLOW.col,
          MaterialColor.COLOR_ORANGE.col,
          new Item.Properties().rarity(Rarity.EPIC).tab(PlayerCompanionsTab.TAB_SPAWN_EGGS)));
  public static final RegistryObject<Item> LIZARD_SPAWN_EGG = ITEMS.register("lizard_spawn_egg",
      () -> new ForgeSpawnEggItem(ModEntityType.LIZARD::get, MaterialColor.COLOR_ORANGE.col,
          MaterialColor.COLOR_BROWN.col,
          new Item.Properties().rarity(Rarity.EPIC).tab(PlayerCompanionsTab.TAB_SPAWN_EGGS)));
  public static final RegistryObject<Item> PIG_SPAWN_EGG = ITEMS.register("pig_spawn_egg",
      () -> new ForgeSpawnEggItem(ModEntityType.PIG::get, MaterialColor.COLOR_PINK.col,
          MaterialColor.COLOR_PINK.col,
          new Item.Properties().rarity(Rarity.EPIC).tab(PlayerCompanionsTab.TAB_SPAWN_EGGS)));
  public static final RegistryObject<Item> ROOSTER_SPAWN_EGG = ITEMS.register("rooster_spawn_egg",
      () -> new ForgeSpawnEggItem(ModEntityType.ROOSTER::get, MaterialColor.COLOR_ORANGE.col,
          MaterialColor.COLOR_RED.col,
          new Item.Properties().rarity(Rarity.EPIC).tab(PlayerCompanionsTab.TAB_SPAWN_EGGS)));
  public static final RegistryObject<Item> SAMURAI_SPAWN_EGG = ITEMS.register("samurai_spawn_egg",
      () -> new ForgeSpawnEggItem(ModEntityType.SAMURAI::get, MaterialColor.COLOR_BLACK.col,
          MaterialColor.COLOR_RED.col,
          new Item.Properties().rarity(Rarity.EPIC).tab(PlayerCompanionsTab.TAB_SPAWN_EGGS)));
  public static final RegistryObject<Item> SMALL_GHAST_SPAWN_EGG =
      ITEMS.register("small_ghast_spawn_egg",
          () -> new ForgeSpawnEggItem(ModEntityType.SMALL_GHAST::get, MaterialColor.SNOW.col,
              MaterialColor.SNOW.col,
              new Item.Properties().rarity(Rarity.EPIC).tab(PlayerCompanionsTab.TAB_SPAWN_EGGS)));
  public static final RegistryObject<Item> SMALL_SLIME_SPAWN_EGG =
      ITEMS.register("small_slime_spawn_egg",
          () -> new ForgeSpawnEggItem(ModEntityType.SMALL_SLIME::get, MaterialColor.COLOR_GREEN.col,
              MaterialColor.COLOR_GREEN.col,
              new Item.Properties().rarity(Rarity.EPIC).tab(PlayerCompanionsTab.TAB_SPAWN_EGGS)));
  public static final RegistryObject<Item> SNAIL_SPAWN_EGG = ITEMS.register("snail_spawn_egg",
      () -> new ForgeSpawnEggItem(ModEntityType.SNAIL::get, MaterialColor.COLOR_ORANGE.col,
          MaterialColor.COLOR_BLUE.col,
          new Item.Properties().rarity(Rarity.EPIC).tab(PlayerCompanionsTab.TAB_SPAWN_EGGS)));
  public static final RegistryObject<Item> WELSH_CORGI_SPAWN_EGG =
      ITEMS.register("welsh_corgi_spawn_egg",
          () -> new ForgeSpawnEggItem(ModEntityType.WELSH_CORGI::get,
              MaterialColor.COLOR_ORANGE.col, MaterialColor.COLOR_ORANGE.col,
              new Item.Properties().rarity(Rarity.EPIC).tab(PlayerCompanionsTab.TAB_SPAWN_EGGS)));
}
