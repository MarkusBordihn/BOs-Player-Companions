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

package de.markusbordihn.playercompanions.entity.taming;

import de.markusbordihn.playercompanions.config.TamingConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class CompanionFoodRegistry {

  private CompanionFoodRegistry() {
  }

  public static String getCompanionTypeName(EntityType<?> companionType) {
    ResourceLocation typeId = BuiltInRegistries.ENTITY_TYPE.getKey(companionType);
    if (typeId == null) {
      return null;
    }

    String path = typeId.getPath();
    if (path.endsWith("_companion")) {
      return path.substring(0, path.length() - "_companion".length());
    }

    return path;
  }

  public static int getTrustValue(EntityType<?> companionType, Item item) {
    String typeName = getCompanionTypeName(companionType);
    if (typeName == null) {
      return 0;
    }

    ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
    if (itemId == null) {
      return 0;
    }

    return TamingConfig.getFoodTrustValue(typeName, itemId.toString());
  }

  public static boolean isValidFood(EntityType<?> companionType, Item item) {
    return getTrustValue(companionType, item) > 0;
  }

  public static List<Item> getFoodItems(EntityType<?> companionType) {
    String typeName = getCompanionTypeName(companionType);
    if (typeName == null) {
      return List.of();
    }

    Map<String, Integer> foods = TamingConfig.getFoodsForCompanion(typeName);
    List<Item> items = new ArrayList<>();

    for (String itemIdString : foods.keySet()) {
      ResourceLocation itemId = ResourceLocation.tryParse(itemIdString);
      if (itemId != null) {
        Item item = BuiltInRegistries.ITEM.get(itemId);
        if (item != null && item != Items.AIR) {
          items.add(item);
        }
      }
    }

    return items;
  }

  public static Ingredient getFoodIngredient(EntityType<?> companionType) {
    List<Item> foods = getFoodItems(companionType);
    if (foods.isEmpty()) {
      return Ingredient.EMPTY;
    }

    return Ingredient.of(foods.toArray(new Item[0]));
  }
}
