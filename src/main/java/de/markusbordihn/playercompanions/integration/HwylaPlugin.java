package de.markusbordihn.playercompanions.integration;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.entity.follower.FollowerEntity;

@WailaPlugin
public class HwylaPlugin implements IWailaPlugin {

  @Override
  public void register(IRegistrar registrar) {
    registrar.registerIconProvider(PlayerCompanionEntityProvider.INSTANCE,
        PlayerCompanionEntity.class);
    registrar.registerComponentProvider(PlayerCompanionEntityProvider.INSTANCE,
        TooltipPosition.BODY, PlayerCompanionEntity.class);
    registrar.registerComponentProvider(FollowerEntityProvider.INSTANCE, TooltipPosition.BODY,
        FollowerEntity.class);
  }

}
