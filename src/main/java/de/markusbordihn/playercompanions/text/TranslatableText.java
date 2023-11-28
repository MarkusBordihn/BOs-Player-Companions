/*
 * Copyright 2022 Markus Bordihn
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

package de.markusbordihn.playercompanions.text;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class TranslatableText {

  private static Map<String, TranslatableComponent> entityNameCache = new ConcurrentHashMap<>();
  private static Map<String, TranslatableComponent> itemNameCache = new ConcurrentHashMap<>();

  protected TranslatableText() {}

  @SubscribeEvent
  public static void handleServerAboutToStartEvent(ServerAboutToStartEvent event) {
    entityNameCache = new ConcurrentHashMap<>();
    itemNameCache = new ConcurrentHashMap<>();
  }

  public static TranslatableComponent getEntityName(String entityName) {
    // Format entity name to entity.x.y format for automatic translations.
    if (!entityName.contains("entity.") && entityName.contains(":")) {
      entityName = "entity." + entityName.replace(':', '.');
    }
    return entityNameCache.computeIfAbsent(
        entityName,
        key -> {
          TranslatableComponent translatableComponent = new TranslatableComponent(key);
          if (!translatableComponent.getString().equals(key)) {
            return translatableComponent;
          }
          return null;
        });
  }

  public static TranslatableComponent getItemName(ItemStack itemStack) {
    return getItemName(itemStack.getDescriptionId());
  }

  public static TranslatableComponent getItemName(String itemName) {
    return itemNameCache.computeIfAbsent(
        itemName,
        key -> {
          TranslatableComponent translatableComponent = new TranslatableComponent(itemName);
          if (!translatableComponent.getString().equals(itemName)) {
            return translatableComponent;
          }
          return null;
        });
  }
}
