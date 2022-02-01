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

package de.markusbordihn.playercompanions.level.spawner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;

import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.config.CommonConfig;
import de.markusbordihn.playercompanions.entity.ModEntityType;
import de.markusbordihn.playercompanions.entity.follower.SmallSlime;
import de.markusbordihn.playercompanions.entity.guard.SmallGhast;

@Mod.EventBusSubscriber()
public class SpawnHandler {

  public static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public static final CommonConfig.Config COMMON = CommonConfig.COMMON;

  protected SpawnHandler() {}

  @SubscribeEvent
  public static void onWorldLoad(ServerAboutToStartEvent event) {
    logSpawn(SmallSlime.NAME, COMMON.smallSlimeSpawnEnable.get(), COMMON.smallSlimeWeight.get(),
        COMMON.smallSlimeMinGroup.get(), COMMON.smallSlimeMaxGroup.get());
  }

  @SubscribeEvent()
  public static void handleBiomeLoadingEvent(BiomeLoadingEvent event) {
    if (event.getName() == null) {
      return;
    }
    ResourceKey<Biome> biomeKey = ResourceKey.create(Registry.BIOME_REGISTRY, event.getName());
    boolean isSwamp = BiomeDictionary.hasType(biomeKey, Type.SWAMP);
    boolean isJungle = BiomeDictionary.hasType(biomeKey, Type.JUNGLE);
    boolean isNether = BiomeDictionary.hasType(biomeKey, Type.NETHER);

    // Small Slime Spawn
    if (Boolean.TRUE.equals(COMMON.smallSlimeSpawnEnable.get()) && (isJungle || isSwamp)) {
      event.getSpawns().getSpawner(SmallSlime.CATEGORY)
          .add(new MobSpawnSettings.SpawnerData(ModEntityType.SMALL_SLIME.get(),
              COMMON.smallSlimeWeight.get(), COMMON.smallSlimeMinGroup.get(),
              COMMON.smallSlimeMaxGroup.get()));
    }

    // Small Ghast Spawn
    if (Boolean.TRUE.equals(COMMON.smallGhastSpawnEnable.get()) && isNether) {
      event.getSpawns().getSpawner(SmallGhast.CATEGORY)
          .add(new MobSpawnSettings.SpawnerData(ModEntityType.SMALL_GHAST.get(),
              COMMON.smallGhastWeight.get(), COMMON.smallGhastMinGroup.get(),
              COMMON.smallGhastMaxGroup.get()));
    }
  }

  public static void registerSpawnPlacements(final FMLCommonSetupEvent event) {
    log.info("{} Spawn Placements ...", Constants.LOG_REGISTER_PREFIX);

    SpawnPlacements.register(ModEntityType.SMALL_SLIME.get(), SpawnPlacements.Type.ON_GROUND,
        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SmallSlime::checkAnimalSpawnRules);
    SpawnPlacements.register(ModEntityType.SMALL_GHAST.get(), SpawnPlacements.Type.ON_GROUND,
        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SmallGhast::checkMobSpawnRules);
  }

  public static void logSpawn(String name, boolean enabled, int weight, int minGroup,
      int maxGroup) {
    if (enabled) {
      log.info("{} {} with {} weight and {}-{} per group.", Constants.LOG_SPAWN_PREFIX, name,
          weight, minGroup, maxGroup);
    }
  }

}
