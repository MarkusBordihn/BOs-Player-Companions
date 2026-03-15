/*
 * Copyright 2025 Markus Bordihn
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

package de.markusbordihn.playercompanions;

import de.markusbordihn.playercompanions.client.keymapping.CompanionKeyHandler;
import de.markusbordihn.playercompanions.client.model.ModModelLayer;
import de.markusbordihn.playercompanions.client.renderer.EntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerCompanionsClient implements ClientModInitializer {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  @Override
  public void onInitializeClient() {
    log.info("Initializing {} (Fabric-Client) ...", Constants.MOD_NAME);

    log.info("{} Model Layer Definitions ...", Constants.LOG_REGISTER_PREFIX);
    ModModelLayer.registerEntityLayerDefinitions();

    log.info("{} Renderer ...", Constants.LOG_REGISTER_PREFIX);
    EntityRenderer.register();

    log.info("{} Network Handler (client) ...", Constants.LOG_REGISTER_PREFIX);
    de.markusbordihn.playercompanions.network.CompanionNetworkHandler.setHandler(
      new de.markusbordihn.playercompanions.network.FabricNetworkHandler());

    log.info("{} Screens ...", Constants.LOG_REGISTER_PREFIX);
    de.markusbordihn.playercompanions.client.screen.FabricScreenRegistry.register();

    log.info("{} Key Mappings ...", Constants.LOG_REGISTER_PREFIX);
    KeyBindingHelper.registerKeyBinding(CompanionKeyHandler.COMMAND_KEY);
    KeyBindingHelper.registerKeyBinding(CompanionKeyHandler.AGGRESSION_KEY);
    ClientTickEvents.END_CLIENT_TICK.register(client -> CompanionKeyHandler.tick());
  }
}
