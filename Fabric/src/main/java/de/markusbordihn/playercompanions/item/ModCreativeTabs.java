/*
 * Copyright 2026 Markus Bordihn
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

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.block.ModBlocks;
import de.markusbordihn.playercompanions.entity.CompanionEntityType;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModCreativeTabs {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private ModCreativeTabs() {
  }

  public static void register() {
    log.info("{} Creative Tabs ...", Constants.LOG_REGISTER_PREFIX);

    CreativeModeTab tab = FabricItemGroup.builder()
      .title(Component.translatable("itemGroup.player_companions"))
      .icon(() -> {
        Item pigEgg = ModItems.COMPANION_SPAWN_EGGS.get(CompanionEntityType.PIG);
        return pigEgg != null ? new ItemStack(pigEgg) : ItemStack.EMPTY;
      })
      .displayItems((params, output) -> {
        output.accept(ModBlocks.COMPANION_SHRINE);

        // Spawn Eggs
        for (CompanionEntityType type : CompanionEntityType.values()) {
          Item egg = ModItems.COMPANION_SPAWN_EGGS.get(type);
          if (egg != null) {
            output.accept(egg);
          }
        }
      })
      .build();

    Registry.register(
      BuiltInRegistries.CREATIVE_MODE_TAB,
      new ResourceLocation(Constants.MOD_ID, "player_companions"),
      tab);
  }
}
