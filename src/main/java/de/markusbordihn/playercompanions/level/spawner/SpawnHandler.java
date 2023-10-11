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

package de.markusbordihn.playercompanions.level.spawner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.Biome.BiomeCategory;
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
import de.markusbordihn.playercompanions.entity.PlayerCompanionEntity;
import de.markusbordihn.playercompanions.entity.companions.Dobutsu;
import de.markusbordihn.playercompanions.entity.companions.Fairy;
import de.markusbordihn.playercompanions.entity.companions.Firefly;
import de.markusbordihn.playercompanions.entity.companions.Lizard;
import de.markusbordihn.playercompanions.entity.companions.ModEntityType;
import de.markusbordihn.playercompanions.entity.companions.Pig;
import de.markusbordihn.playercompanions.entity.companions.Raptor;
import de.markusbordihn.playercompanions.entity.companions.Rooster;
import de.markusbordihn.playercompanions.entity.companions.Samurai;
import de.markusbordihn.playercompanions.entity.companions.SmallGhast;
import de.markusbordihn.playercompanions.entity.companions.SmallSlime;
import de.markusbordihn.playercompanions.entity.companions.Snail;
import de.markusbordihn.playercompanions.entity.companions.WelshCorgi;

@Mod.EventBusSubscriber()
public class SpawnHandler {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public static final CommonConfig.Config COMMON = CommonConfig.COMMON;

  protected SpawnHandler() {}

  @SubscribeEvent
  public static void onWorldLoad(ServerAboutToStartEvent event) {
    logSpawn(Dobutsu.NAME, COMMON.dobutsuSpawnEnable.get(), COMMON.dobutsuWeight.get(),
        COMMON.dobutsuMinGroup.get(), COMMON.dobutsuMaxGroup.get());
    logSpawn(Fairy.NAME, COMMON.fairySpawnEnable.get(), COMMON.fairyWeight.get(),
        COMMON.fairyMinGroup.get(), COMMON.fairyMaxGroup.get());
    logSpawn(Firefly.NAME, COMMON.fireflySpawnEnable.get(), COMMON.fireflyWeight.get(),
        COMMON.fireflyMinGroup.get(), COMMON.fireflyMaxGroup.get());
    logSpawn(Lizard.NAME, COMMON.lizardSpawnEnable.get(), COMMON.lizardWeight.get(),
        COMMON.lizardMinGroup.get(), COMMON.lizardMaxGroup.get());
    logSpawn(Pig.NAME, COMMON.pigSpawnEnable.get(), COMMON.pigWeight.get(),
        COMMON.pigMinGroup.get(), COMMON.pigMaxGroup.get());
    logSpawn(Raptor.NAME, COMMON.raptorSpawnEnable.get(), COMMON.raptorWeight.get(),
        COMMON.raptorMinGroup.get(), COMMON.raptorMaxGroup.get());
    logSpawn(Rooster.NAME, COMMON.roosterSpawnEnable.get(), COMMON.roosterWeight.get(),
        COMMON.roosterMinGroup.get(), COMMON.roosterMaxGroup.get());
    logSpawn(Samurai.NAME, COMMON.samuraiSpawnEnable.get(), COMMON.samuraiWeight.get(),
        COMMON.samuraiMinGroup.get(), COMMON.samuraiMaxGroup.get());
    logSpawn(SmallGhast.NAME, COMMON.smallGhastSpawnEnable.get(), COMMON.smallGhastWeight.get(),
        COMMON.smallGhastMinGroup.get(), COMMON.smallGhastMaxGroup.get());
    logSpawn(SmallSlime.NAME, COMMON.smallSlimeSpawnEnable.get(), COMMON.smallSlimeWeight.get(),
        COMMON.smallSlimeMinGroup.get(), COMMON.smallSlimeMaxGroup.get());
    logSpawn(Snail.NAME, COMMON.snailSpawnEnable.get(), COMMON.snailWeight.get(),
        COMMON.snailMinGroup.get(), COMMON.snailMaxGroup.get());
    logSpawn(WelshCorgi.NAME, COMMON.welshCorgiSpawnEnable.get(), COMMON.welshCorgiWeight.get(),
        COMMON.welshCorgiMinGroup.get(), COMMON.welshCorgiMaxGroup.get());
  }

  @SubscribeEvent()
  public static void handleBiomeLoadingEvent(BiomeLoadingEvent event) {
    ResourceLocation biomeRegistry = event.getName();
    if (biomeRegistry == null) {
      return;
    }
    BiomeCategory biomeCategory = event.getCategory();
    ResourceKey<Biome> biomeKey = ResourceKey.create(Registry.BIOME_REGISTRY, biomeRegistry);
    boolean isBadlands = biomeKey == Biomes.BADLANDS;
    boolean isBeach =
        biomeCategory == BiomeCategory.BEACH || BiomeDictionary.hasType(biomeKey, Type.BEACH);
    boolean isDarkForest = biomeKey == Biomes.DARK_FOREST;
    boolean isDesert = biomeKey == Biomes.DESERT;
    boolean isFlowerForest = biomeKey == Biomes.FLOWER_FOREST;
    boolean isLushCaves = biomeKey == Biomes.LUSH_CAVES;
    boolean isJungle =
        biomeCategory == BiomeCategory.JUNGLE || BiomeDictionary.hasType(biomeKey, Type.JUNGLE);
    boolean isNether =
        biomeCategory == BiomeCategory.NETHER || BiomeDictionary.hasType(biomeKey, Type.NETHER);
    boolean isNetherWastes = biomeKey == Biomes.NETHER_WASTES;
    boolean isPlains =
        biomeCategory == BiomeCategory.PLAINS || BiomeDictionary.hasType(biomeKey, Type.PLAINS);
    boolean isSwamp =
        biomeCategory == BiomeCategory.SWAMP || BiomeDictionary.hasType(biomeKey, Type.SWAMP);
    boolean isTaiga = biomeCategory == BiomeCategory.TAIGA;
    boolean isMountain =
        biomeCategory == BiomeCategory.MOUNTAIN || BiomeDictionary.hasType(biomeKey, Type.MOUNTAIN);

    // Dobutsu Spawn
    if (Boolean.TRUE.equals(COMMON.dobutsuSpawnEnable.get()) && isDarkForest) {
      event.getSpawns().getSpawner(PlayerCompanionEntity.CATEGORY)
          .add(new MobSpawnSettings.SpawnerData(ModEntityType.DOBUTSU.get(),
              COMMON.dobutsuWeight.get(), COMMON.dobutsuMinGroup.get(),
              COMMON.dobutsuMaxGroup.get()));
    }

    // Fairy Spawn
    if (Boolean.TRUE.equals(COMMON.fairySpawnEnable.get()) && (isFlowerForest || isLushCaves)) {
      event.getSpawns().getSpawner(PlayerCompanionEntity.CATEGORY)
          .add(new MobSpawnSettings.SpawnerData(ModEntityType.FAIRY.get(), COMMON.fairyWeight.get(),
              COMMON.fairyMinGroup.get(), COMMON.fairyMaxGroup.get()));
    }

    // Firefly Spawn
    if (Boolean.TRUE.equals(COMMON.fireflySpawnEnable.get()) && (isPlains || isSwamp)) {
      event.getSpawns().getSpawner(PlayerCompanionEntity.CATEGORY)
          .add(new MobSpawnSettings.SpawnerData(ModEntityType.FIREFLY.get(),
              COMMON.fireflyWeight.get(), COMMON.fireflyMinGroup.get(),
              COMMON.fireflyMaxGroup.get()));
    }

    // Lizard Spawn
    if (Boolean.TRUE.equals(COMMON.lizardSpawnEnable.get())
        && (isDesert || isJungle || isSwamp || isBadlands) && !isFlowerForest) {
      event.getSpawns().getSpawner(PlayerCompanionEntity.CATEGORY)
          .add(new MobSpawnSettings.SpawnerData(ModEntityType.LIZARD.get(),
              COMMON.lizardWeight.get(), COMMON.lizardMinGroup.get(), COMMON.lizardMaxGroup.get()));
    }

    // Pig Spawn
    if (Boolean.TRUE.equals(COMMON.pigSpawnEnable.get()) && isPlains && !isFlowerForest) {
      event.getSpawns().getSpawner(PlayerCompanionEntity.CATEGORY)
          .add(new MobSpawnSettings.SpawnerData(ModEntityType.PIG.get(), COMMON.pigWeight.get(),
              COMMON.pigMinGroup.get(), COMMON.pigMaxGroup.get()));
    }

    // Raptor Spawn
    if (Boolean.TRUE.equals(COMMON.raptorSpawnEnable.get()) && isJungle) {
      event.getSpawns().getSpawner(PlayerCompanionEntity.CATEGORY)
          .add(new MobSpawnSettings.SpawnerData(ModEntityType.RAPTOR.get(),
              COMMON.raptorWeight.get(), COMMON.raptorMinGroup.get(), COMMON.raptorMaxGroup.get()));
    }

    // Rooster Spawn
    if (Boolean.TRUE.equals(COMMON.roosterSpawnEnable.get()) && isPlains && !isFlowerForest) {
      event.getSpawns().getSpawner(PlayerCompanionEntity.CATEGORY)
          .add(new MobSpawnSettings.SpawnerData(ModEntityType.ROOSTER.get(),
              COMMON.roosterWeight.get(), COMMON.roosterMinGroup.get(),
              COMMON.roosterMaxGroup.get()));
    }

    // Samurai Spawn
    if (Boolean.TRUE.equals(COMMON.samuraiSpawnEnable.get()) && isMountain && !isFlowerForest) {
      event.getSpawns().getSpawner(PlayerCompanionEntity.CATEGORY)
          .add(new MobSpawnSettings.SpawnerData(ModEntityType.SAMURAI.get(),
              COMMON.samuraiWeight.get(), COMMON.samuraiMinGroup.get(),
              COMMON.samuraiMaxGroup.get()));
    }

    // Small Ghast Spawn
    if (Boolean.TRUE.equals(COMMON.smallGhastSpawnEnable.get()) && (isNetherWastes || isNether)) {
      event.getSpawns().getSpawner(PlayerCompanionEntity.CATEGORY)
          .add(new MobSpawnSettings.SpawnerData(ModEntityType.SMALL_GHAST.get(),
              COMMON.smallGhastWeight.get(), COMMON.smallGhastMinGroup.get(),
              COMMON.smallGhastMaxGroup.get()));
    }

    // Small Slime Spawn
    if (Boolean.TRUE.equals(COMMON.smallSlimeSpawnEnable.get()) && (isJungle || isSwamp)) {
      event.getSpawns().getSpawner(PlayerCompanionEntity.CATEGORY)
          .add(new MobSpawnSettings.SpawnerData(ModEntityType.SMALL_SLIME.get(),
              COMMON.smallSlimeWeight.get(), COMMON.smallSlimeMinGroup.get(),
              COMMON.smallSlimeMaxGroup.get()));
    }

    // Snail
    if (Boolean.TRUE.equals(COMMON.snailSpawnEnable.get()) && isBeach) {
      event.getSpawns().getSpawner(PlayerCompanionEntity.CATEGORY)
          .add(new MobSpawnSettings.SpawnerData(ModEntityType.SNAIL.get(), COMMON.snailWeight.get(),
              COMMON.snailMinGroup.get(), COMMON.snailMaxGroup.get()));
    }

    // Welsh Corgi
    if (Boolean.TRUE.equals(COMMON.welshCorgiSpawnEnable.get()) && isTaiga) {
      event.getSpawns().getSpawner(PlayerCompanionEntity.CATEGORY)
          .add(new MobSpawnSettings.SpawnerData(ModEntityType.WELSH_CORGI.get(),
              COMMON.welshCorgiWeight.get(), COMMON.welshCorgiMinGroup.get(),
              COMMON.welshCorgiMaxGroup.get()));
    }
  }

  public static void registerSpawnPlacements(final FMLCommonSetupEvent event) {
    log.info("{} Spawn Placements ...", Constants.LOG_REGISTER_PREFIX);
    event.enqueueWork(() -> {
      SpawnPlacements.register(ModEntityType.DOBUTSU.get(), SpawnPlacements.Type.ON_GROUND,
          Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Dobutsu::checkAnimalSpawnRules);
      SpawnPlacements.register(ModEntityType.FAIRY.get(), SpawnPlacements.Type.ON_GROUND,
          Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Fairy::checkAnimalSpawnRules);
      SpawnPlacements.register(ModEntityType.FIREFLY.get(), SpawnPlacements.Type.ON_GROUND,
          Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Firefly::checkAnimalSpawnRules);
      SpawnPlacements.register(ModEntityType.LIZARD.get(), SpawnPlacements.Type.ON_GROUND,
          Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Lizard::checkAnimalSpawnRules);
      SpawnPlacements.register(ModEntityType.PIG.get(), SpawnPlacements.Type.ON_GROUND,
          Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      SpawnPlacements.register(ModEntityType.RAPTOR.get(), SpawnPlacements.Type.ON_GROUND,
          Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      SpawnPlacements.register(ModEntityType.ROOSTER.get(), SpawnPlacements.Type.ON_GROUND,
          Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      SpawnPlacements.register(ModEntityType.SAMURAI.get(), SpawnPlacements.Type.ON_GROUND,
          Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
      SpawnPlacements.register(ModEntityType.SMALL_GHAST.get(), SpawnPlacements.Type.ON_GROUND,
          Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SmallGhast::checkGhastSpawnRules);
      SpawnPlacements.register(ModEntityType.SMALL_SLIME.get(), SpawnPlacements.Type.ON_GROUND,
          Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SmallSlime::checkMobSpawnRules);
      SpawnPlacements.register(ModEntityType.SNAIL.get(), SpawnPlacements.Type.ON_GROUND,
          Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Snail::checkAnimalSpawnRules);
      SpawnPlacements.register(ModEntityType.WELSH_CORGI.get(), SpawnPlacements.Type.ON_GROUND,
          Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Snail::checkAnimalSpawnRules);
    });
  }

  private static void logSpawn(String name, boolean enabled, int weight, int minGroup,
      int maxGroup) {
    if (enabled) {
      if (minGroup == maxGroup) {
        log.info("{} {} with {} weight and {} per group.", Constants.LOG_SPAWN_PREFIX, name, weight,
            maxGroup);
      } else {
        log.info("{} {} with {} weight and {}-{} per group.", Constants.LOG_SPAWN_PREFIX, name,
            weight, minGroup, maxGroup);
      }
    }
  }

}
