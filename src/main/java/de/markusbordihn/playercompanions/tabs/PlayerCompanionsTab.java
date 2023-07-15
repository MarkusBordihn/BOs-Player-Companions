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

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.item.ModItems;

public class PlayerCompanionsTab {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  protected PlayerCompanionsTab() {}

  public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
      DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Constants.MOD_ID);

  public static final RegistryObject<CreativeModeTab> TAB_SPAWN_EGGS =
      CREATIVE_TABS.register("spawn_eggs",
          () -> CreativeModeTab.builder()
              .icon(() -> ModItems.SMALL_SLIME_SPAWN_EGG.get().getDefaultInstance())
              .displayItems(new SpawnEggsItems())
              .title(Component.translatable("itemGroup.player_companions.spawn_eggs")).build());

  public static final RegistryObject<CreativeModeTab> TAB_COMPANIONS =
      CREATIVE_TABS.register("player_companions",
          () -> CreativeModeTab.builder()
              .icon(() -> ModItems.SMALL_SLIME_GREEN.get().getDefaultInstance())
              .displayItems(new PlayerCompanionsItems())
              .title(Component.translatable("itemGroup.player_companions")).build());

  public static final RegistryObject<CreativeModeTab> TAB_TAME_ITEMS =
      CREATIVE_TABS.register("tame_items",
          () -> CreativeModeTab.builder().icon(() -> ModItems.TAME_APPLE.get().getDefaultInstance())
              .displayItems(new TameItems())
              .title(Component.translatable("itemGroup.player_companions.tame_items")).build());

}
