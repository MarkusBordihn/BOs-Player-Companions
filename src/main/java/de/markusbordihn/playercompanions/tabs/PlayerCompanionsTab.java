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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.event.CreativeModeTabEvent;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.item.ModItems;

public class PlayerCompanionsTab {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  protected PlayerCompanionsTab() {}

  public static CreativeModeTab TAB_SPAWN_EGGS;

  public static CreativeModeTab TAB_COMPANIONS;

  public static CreativeModeTab TAB_TAME_ITEMS;

  public static void handleCreativeModeTabRegister(CreativeModeTabEvent.Register event) {

    log.info("{} creative mod tabs ...", Constants.LOG_REGISTER_PREFIX);

    TAB_SPAWN_EGGS = event
        .registerCreativeModeTab(new ResourceLocation(Constants.MOD_ID, "spawn_eggs"), builder -> {
          builder.icon(() -> new ItemStack(ModItems.SMALL_SLIME_SPAWN_EGG.get()))
              .displayItems(new SpawnEggsItems())
              .title(Component.translatable("itemGroup.player_companions.spawn_eggs")).build();
        });

    TAB_COMPANIONS = event.registerCreativeModeTab(
        new ResourceLocation(Constants.MOD_ID, "player_companions"), builder -> {
          builder.icon(() -> new ItemStack(ModItems.SMALL_SLIME_GREEN.get()))
              .displayItems(new PlayerCompanionsItems())
              .title(Component.translatable("itemGroup.player_companions")).build();
        });

    TAB_TAME_ITEMS = event
        .registerCreativeModeTab(new ResourceLocation(Constants.MOD_ID, "tame_items"), builder -> {
          builder.icon(() -> new ItemStack(ModItems.TAME_APPLE.get())).displayItems(new TameItems())
              .title(Component.translatable("itemGroup.player_companions.tame_items")).build();
        });

  }

}
