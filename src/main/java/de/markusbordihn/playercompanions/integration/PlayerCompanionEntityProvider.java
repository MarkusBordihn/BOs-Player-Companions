package de.markusbordihn.playercompanions.integration;

import mcp.mobius.waila.api.EntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.api.ui.IElement;
import mcp.mobius.waila.impl.ui.ElementHelper;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;


public class PlayerCompanionEntityProvider implements IEntityComponentProvider {

  public static final PlayerCompanionEntityProvider INSTANCE = new PlayerCompanionEntityProvider();

  @Override
  @OnlyIn(Dist.CLIENT)
  public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
    tooltip.add(new TextComponent("Test"));
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
