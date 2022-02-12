package de.markusbordihn.playercompanions.integration;

import mcp.mobius.waila.api.EntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;

import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import de.markusbordihn.playercompanions.data.PlayerCompanionData;
import de.markusbordihn.playercompanions.data.PlayerCompanionsClientData;
import de.markusbordihn.playercompanions.entity.follower.FollowerEntity;

public class FollowerEntityProvider implements IEntityComponentProvider {

  public static final FollowerEntityProvider INSTANCE = new FollowerEntityProvider();

  @Override
  @OnlyIn(Dist.CLIENT)
  public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
    if (accessor.getEntity() instanceof FollowerEntity followerEntity) {
      PlayerCompanionData data = PlayerCompanionsClientData.getCompanion(followerEntity);
      tooltip.add(new TextComponent("Type: Follower "));
      if (followerEntity.getOwner() != null) {
        tooltip.add(new TextComponent("Owner: ").append(followerEntity.getOwner().getName()));
        if (data != null) {
          if (data.isOrderedToSit()) {
            tooltip.add(new TextComponent("Order: Sitting"));
          } else {
            tooltip.add(new TextComponent("Order: Following"));
          }
        }
      }
    }
  }
}
