/**
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

package de.markusbordihn.playercompanions.integration;

import mcp.mobius.waila.api.EntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.api.ui.IElement;
import mcp.mobius.waila.impl.ui.ElementHelper;

import snownee.jade.VanillaPlugin;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.UsernameCache;

import de.markusbordihn.playercompanions.data.PlayerCompanionData;
import de.markusbordihn.playercompanions.data.PlayerCompanionsClientData;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;


public class PlayerCompanionEntityProvider implements IEntityComponentProvider {

  public static final PlayerCompanionEntityProvider INSTANCE = new PlayerCompanionEntityProvider();

  @Override
  @OnlyIn(Dist.CLIENT)
  public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
    if (accessor.getEntity() instanceof PlayerCompanionEntity playerCompanionEntity) {
      tooltip.add(new TextComponent("Type: " + playerCompanionEntity.getCompanionType()));
      if (playerCompanionEntity.getOwner() != null) {
        PlayerCompanionData data = PlayerCompanionsClientData.getCompanion(playerCompanionEntity);
        if (!config.get(VanillaPlugin.ANIMAL_OWNER)) {
          String ownerName =
              UsernameCache.getLastKnownUsername(playerCompanionEntity.getOwnerUUID());
          tooltip.add(new TextComponent("Owner: " + ownerName));
        }
        if (data != null) {
          if (data.isOrderedToPosition()) {
            tooltip.add(new TextComponent("Order: To Position"));
          }
          if (data.isOrderedToSit()) {
            tooltip.add(new TextComponent("Order: Sitting"));
          } else {
            tooltip.add(new TextComponent("Order: Following"));
          }
        }
      }
    }
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public IElement getIcon(EntityAccessor accessor, IPluginConfig config, IElement currentIcon) {
    if (accessor.getEntity() instanceof PlayerCompanionEntity playerCompanionEntity) {
      ItemStack itemStack = new ItemStack(playerCompanionEntity.getCompanionItem());
      return new ElementHelper().item(itemStack, 2F);
    }
    return null;
  }

}
