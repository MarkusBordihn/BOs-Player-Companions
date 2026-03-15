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

import de.markusbordihn.playercompanions.config.Config;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("unused")
@Mod(Constants.MOD_ID)
public class PlayerCompanions {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  @SuppressWarnings({"java:S1118", "java:S2440"})
  public PlayerCompanions(FMLJavaModLoadingContext context) {
    final IEventBus modEventBus = context.getModEventBus();

    log.info("Initializing {} (Forge) ...", Constants.MOD_NAME);

    log.info("{} Constants ...", Constants.LOG_REGISTER_PREFIX);
    Constants.GAME_DIR = FMLPaths.GAMEDIR.get();
    Constants.CONFIG_DIR = FMLPaths.CONFIGDIR.get();

    log.info("{} Configuration ...", Constants.LOG_REGISTER_PREFIX);
    Config.register(FMLEnvironment.dist == Dist.DEDICATED_SERVER);

    log.info("{} Entity Data Serializers ...", Constants.LOG_REGISTER_PREFIX);
    de.markusbordihn.playercompanions.network.CompanionEntityDataSerializers.register();

    log.info("{} Entity Types ...", Constants.LOG_REGISTER_PREFIX);
    de.markusbordihn.playercompanions.entity.ModEntityType.ENTITY_TYPES.register(modEventBus);

    log.info("{} Items ...", Constants.LOG_REGISTER_PREFIX);
    de.markusbordihn.playercompanions.item.ModItems.ITEMS.register(modEventBus);

    log.info("{} Creative Tabs ...", Constants.LOG_REGISTER_PREFIX);
    de.markusbordihn.playercompanions.item.ModCreativeTabs.TABS.register(modEventBus);

    log.info("{} Blocks ...", Constants.LOG_REGISTER_PREFIX);
    de.markusbordihn.playercompanions.block.ForgeModBlocks.BLOCKS.register(modEventBus);

    log.info("{} Network Handler ...", Constants.LOG_REGISTER_PREFIX);
    de.markusbordihn.playercompanions.network.ForgeNetworkHandler.register();
    de.markusbordihn.playercompanions.network.CompanionNetworkHandler.setHandler(
      new de.markusbordihn.playercompanions.network.ForgeNetworkHandler());

    log.info("{} Menu Types ...", Constants.LOG_REGISTER_PREFIX);
    de.markusbordihn.playercompanions.menu.ForgeModMenuTypes.MENU_TYPES.register(modEventBus);
    de.markusbordihn.playercompanions.entity.CompanionMenuHandler.setMenuOpener(
      new de.markusbordihn.playercompanions.menu.MenuOpener());
    modEventBus.addListener((net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent event) ->
      event.enqueueWork(() -> {
        de.markusbordihn.playercompanions.menu.ForgeModMenuTypes.register();
        de.markusbordihn.playercompanions.block.ForgeModBlocks.register();
        de.markusbordihn.playercompanions.menu.CompanionShrineHandler.setOpener(
          new de.markusbordihn.playercompanions.menu.ForgeCompanionShrineOpener());
      }));

    // Initialize the client mod initializer
    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> new PlayerCompanionsClient(modEventBus));
  }
}
