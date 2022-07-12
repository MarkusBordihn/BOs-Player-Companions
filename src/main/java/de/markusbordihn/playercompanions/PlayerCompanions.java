/**
 * Copyright 2021 Markus Bordihn
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import de.markusbordihn.playercompanions.block.ModBlocks;
import de.markusbordihn.playercompanions.client.keymapping.ModKeyMapping;
import de.markusbordihn.playercompanions.client.renderer.ClientRenderer;
import de.markusbordihn.playercompanions.client.screen.ClientScreens;
import de.markusbordihn.playercompanions.container.ModMenuTypes;
import de.markusbordihn.playercompanions.entity.companions.ModEntityType;
import de.markusbordihn.playercompanions.item.ModItems;
import de.markusbordihn.playercompanions.level.biome.ModBiomeModifiers;
import de.markusbordihn.playercompanions.level.spawner.SpawnHandler;
import de.markusbordihn.playercompanions.network.NetworkHandler;
import de.markusbordihn.playercompanions.sounds.ModSoundEvents;
import de.markusbordihn.playercompanions.utils.StopModReposts;

@Mod(Constants.MOD_ID)
public class PlayerCompanions {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public PlayerCompanions() {
    final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    final IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

    StopModReposts.checkStopModReposts();

    modEventBus.addListener(NetworkHandler::registerNetworkHandler);

    log.info("{} Menu Types ...", Constants.LOG_REGISTER_PREFIX);
    ModMenuTypes.MENU_TYPES.register(modEventBus);

    log.info("{} Entity Types ...", Constants.LOG_REGISTER_PREFIX);
    ModEntityType.ENTITIES_TYPES.register(modEventBus);

    log.info("{} Items ...", Constants.LOG_REGISTER_PREFIX);
    ModItems.ITEMS.register(modEventBus);

    log.info("{} Blocks ...", Constants.LOG_REGISTER_PREFIX);
    ModBlocks.BLOCKS.register(modEventBus);

    log.info("{} Sound Events ...", Constants.LOG_REGISTER_PREFIX);
    ModSoundEvents.SOUNDS.register(modEventBus);

    log.info("{} Biome Modifier Serializers ...", Constants.LOG_REGISTER_PREFIX);
    ModBiomeModifiers.BIOME_MODIFIER_SERIALIZERS.register(modEventBus);

    modEventBus.addListener(SpawnHandler::registerSpawnPlacements);

    forgeEventBus.addListener(ServerSetup::handleServerStartingEvent);

    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
      modEventBus.addListener(ClientRenderer::registerEntityLayerDefinitions);
      modEventBus.addListener(ClientRenderer::registerEntityRenderers);
      modEventBus.addListener(ClientScreens::registerScreens);
      modEventBus.addListener(ModKeyMapping::registerKeyMapping);
    });
  }

}
