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
import de.markusbordihn.playercompanions.block.ForgeModBlocks;
import de.markusbordihn.playercompanions.entity.CompanionEntityType;
import de.markusbordihn.playercompanions.entity.ModEntityType;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModItems {

  public static final DeferredRegister<Item> ITEMS =
    DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);
  public static final Map<CompanionEntityType, RegistryObject<Item>> COMPANION_SPAWN_EGGS =
    new EnumMap<>(CompanionEntityType.class);

  public static final RegistryObject<Item> COMPANION_SHRINE_ITEM = ITEMS.register(
    "companion_shrine",
    () -> new BlockItem(ForgeModBlocks.COMPANION_SHRINE.get(), new Item.Properties()));

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  static {
    log.info("{} Spawn Egg Items ...", Constants.LOG_REGISTER_PREFIX);

    for (CompanionEntityType entityType : CompanionEntityType.values()) {
      log.info("Registering companion spawn egg with id {}.", entityType.getId());
      COMPANION_SPAWN_EGGS.put(
        entityType,
        registerSpawnEgg(
          entityType.getId(), () -> ModEntityType.COMPANION_TYPE.get(entityType).get()));
    }
  }

  private ModItems() {
  }

  private static RegistryObject<Item> registerSpawnEgg(
    String id, Supplier<? extends EntityType<?>> entityTypeSupplier) {
    String spawnEggId = id + "_spawn_egg";
    int backgroundColor;
    int highlightColor;
    if (id.contains("rooster")) {
      backgroundColor = 0xFFFFFF;
      highlightColor = 0xFF0000;
    } else {
      backgroundColor = 0xF0A5A2;
      highlightColor = 0xDB7C6A;
    }
    return ITEMS.register(
      spawnEggId,
      () ->
        new ForgeSpawnEggItem(
          () -> (EntityType<? extends Mob>) entityTypeSupplier.get(), backgroundColor,
          highlightColor,
          new Item.Properties().rarity(Rarity.EPIC)));
  }
}
