package de.markusbordihn.playercompanions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.event.server.ServerStartingEvent;

import de.markusbordihn.playercompanions.data.PlayerCompanionsData;

public class ServerSetup {

  public static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  protected ServerSetup() {}

  public static void handleServerStartingEvent(ServerStartingEvent event) {
    log.info("{} Server Starting setup ...", Constants.LOG_REGISTER_PREFIX);
    PlayerCompanionsData.prepare(event.getServer());
  }

}
