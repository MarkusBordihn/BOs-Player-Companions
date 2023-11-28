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

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.Output;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerCompanionsTab {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  public static CreativeModeTab TAB_SPAWN_EGGS;
  public static CreativeModeTab TAB_COMPANIONS;
  public static CreativeModeTab TAB_TAME_ITEMS;

  protected PlayerCompanionsTab() {}

  public static void handleCreativeModeTabRegister(CreativeModeTabEvent.Register event) {

    log.info("{} creative mod tabs ...", Constants.LOG_REGISTER_PREFIX);

    TAB_SPAWN_EGGS =
        event.registerCreativeModeTab(
            new ResourceLocation(Constants.MOD_ID, "spawn_eggs"),
            builder -> {
              builder
                  .icon(() -> new ItemStack(ModItems.SMALL_SLIME_SPAWN_EGG.get()))
                  .displayItems(PlayerCompanionsTab::addSpawnEggsTabItems)
                  .title(Component.translatable("itemGroup.player_companions.spawn_eggs"))
                  .build();
            });

    TAB_COMPANIONS =
        event.registerCreativeModeTab(
            new ResourceLocation(Constants.MOD_ID, "player_companions"),
            builder -> {
              builder
                  .icon(() -> new ItemStack(ModItems.SMALL_SLIME_GREEN.get()))
                  .displayItems(PlayerCompanionsTab::addPlayerCompanionsTabItems)
                  .title(Component.translatable("itemGroup.player_companions"))
                  .build();
            });

    TAB_TAME_ITEMS =
        event.registerCreativeModeTab(
            new ResourceLocation(Constants.MOD_ID, "tame_items"),
            builder -> {
              builder
                  .icon(() -> new ItemStack(ModItems.TAME_APPLE.get()))
                  .displayItems(PlayerCompanionsTab::addTameItemsTabItems)
                  .title(Component.translatable("itemGroup.player_companions.tame_items"))
                  .build();
            });
  }

  private static void addSpawnEggsTabItems(
      FeatureFlagSet featureFlagSet, Output outputTab, boolean hasPermissions) {
    outputTab.accept(ModItems.DOBUTSU_SPAWN_EGG.get());
    outputTab.accept(ModItems.FAIRY_SPAWN_EGG.get());
    outputTab.accept(ModItems.FIREFLY_SPAWN_EGG.get());
    outputTab.accept(ModItems.LIZARD_SPAWN_EGG.get());
    outputTab.accept(ModItems.PIG_SPAWN_EGG.get());
    outputTab.accept(ModItems.RAPTOR_SPAWN_EGG.get());
    outputTab.accept(ModItems.ROOSTER_SPAWN_EGG.get());
    outputTab.accept(ModItems.SAMURAI_SPAWN_EGG.get());
    outputTab.accept(ModItems.SMALL_GHAST_SPAWN_EGG.get());
    outputTab.accept(ModItems.SMALL_SLIME_SPAWN_EGG.get());
    outputTab.accept(ModItems.SNAIL_SPAWN_EGG.get());
    outputTab.accept(ModItems.WELSH_CORGI_SPAWN_EGG.get());
  }

  private static void addPlayerCompanionsTabItems(
      FeatureFlagSet featureFlagSet, Output outputTab, boolean hasPermissions) {
    outputTab.accept(ModItems.DOBUTSU_DEFAULT.get());
    outputTab.accept(ModItems.DOBUTSU_CREEPER.get());
    outputTab.accept(ModItems.DOBUTSU_ENDERMAN.get());
    outputTab.accept(ModItems.FAIRY_DEFAULT.get());
    outputTab.accept(ModItems.FAIRY_BLUE.get());
    outputTab.accept(ModItems.FAIRY_RED.get());
    outputTab.accept(ModItems.FIREFLY_DEFAULT.get());
    outputTab.accept(ModItems.LIZARD_DEFAULT.get());
    outputTab.accept(ModItems.LIZARD_DESERT.get());
    outputTab.accept(ModItems.LIZARD_GREEN.get());
    outputTab.accept(ModItems.SAMURAI_DEFAULT.get());
    outputTab.accept(ModItems.SAMURAI_BLUE.get());
    outputTab.accept(ModItems.SAMURAI_BLACK.get());
    outputTab.accept(ModItems.PIG_DEFAULT.get());
    outputTab.accept(ModItems.PIG_SPOTTED.get());
    outputTab.accept(ModItems.RAPTOR_DEFAULT.get());
    outputTab.accept(ModItems.RAPTOR_PINK.get());
    outputTab.accept(ModItems.RAPTOR_DARK_GREEN.get());
    outputTab.accept(ModItems.ROOSTER_DEFAULT.get());
    outputTab.accept(ModItems.ROOSTER_MIXED.get());
    outputTab.accept(ModItems.SMALL_GHAST_DEFAULT.get());
    outputTab.accept(ModItems.SMALL_SLIME_DEFAULT.get());
    outputTab.accept(ModItems.SMALL_SLIME_BLACK.get());
    outputTab.accept(ModItems.SMALL_SLIME_BLUE.get());
    outputTab.accept(ModItems.SMALL_SLIME_BROWN.get());
    outputTab.accept(ModItems.SMALL_SLIME_CYAN.get());
    outputTab.accept(ModItems.SMALL_SLIME_GRAY.get());
    outputTab.accept(ModItems.SMALL_SLIME_GREEN.get());
    outputTab.accept(ModItems.SMALL_SLIME_LIGHT_BLUE.get());
    outputTab.accept(ModItems.SMALL_SLIME_LIGHT_GRAY.get());
    outputTab.accept(ModItems.SMALL_SLIME_LIME.get());
    outputTab.accept(ModItems.SMALL_SLIME_MAGENTA.get());
    outputTab.accept(ModItems.SMALL_SLIME_ORANGE.get());
    outputTab.accept(ModItems.SMALL_SLIME_PINK.get());
    outputTab.accept(ModItems.SMALL_SLIME_PURPLE.get());
    outputTab.accept(ModItems.SMALL_SLIME_RED.get());
    outputTab.accept(ModItems.SMALL_SLIME_WHITE.get());
    outputTab.accept(ModItems.SMALL_SLIME_YELLOW.get());
    outputTab.accept(ModItems.SNAIL_DEFAULT.get());
    outputTab.accept(ModItems.SNAIL_BROWN.get());
    outputTab.accept(ModItems.WELSH_CORGI_DEFAULT.get());
    outputTab.accept(ModItems.WELSH_CORGI_MIXED.get());
    outputTab.accept(ModItems.WELSH_CORGI_BLACK.get());
  }

  private static void addTameItemsTabItems(
      FeatureFlagSet featureFlagSet, Output outputTab, boolean hasPermissions) {
    outputTab.accept(ModItems.TAME_APPLE.get());
    outputTab.accept(ModItems.TAME_BONE.get());
    outputTab.accept(ModItems.TAME_CAKE.get());
    outputTab.accept(ModItems.TAME_CARROT.get());
    outputTab.accept(ModItems.TAME_HONEYCOMP.get());
    outputTab.accept(ModItems.TAME_RAW_MUTTON.get());
    outputTab.accept(ModItems.TAME_SEAGRASS.get());
    outputTab.accept(ModItems.TAME_SWEET_BERRIES.get());
    outputTab.accept(ModItems.TAME_WHEAT_SEEDS.get());
  }
}
