/*
 * Copyright 2026 Markus Bordihn
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

package de.markusbordihn.playercompanions.entity;

import de.markusbordihn.easynpc.api.npc.base.ChickenBase;
import de.markusbordihn.easynpc.api.npc.base.slime.SlimeSmallBase;
import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.config.TamingConfig;
import java.util.EnumMap;
import java.util.Map;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModEntityType {

  public static final Map<CompanionEntityType, EntityType<?>> COMPANION_TYPE =
    new EnumMap<>(CompanionEntityType.class);
  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  static {
    for (CompanionEntityType type : CompanionEntityType.values()) {
      log.info("Registering companion entity type {}", type.getResourceKey());
      COMPANION_TYPE.put(
        type,
        Registry.register(
          BuiltInRegistries.ENTITY_TYPE,
          new ResourceLocation(Constants.MOD_ID, type.getId()),
          type.getBuilder().build(type.getResourceKey().toString())));
    }
  }

  private ModEntityType() {
  }

  public static <T extends net.minecraft.world.entity.Entity> EntityType<T> getEntityType(
    CompanionEntityType type) {
    return (EntityType<T>) COMPANION_TYPE.get(type);
  }

  public static void registerEntityAttributes() {
    for (CompanionEntityType type : CompanionEntityType.values()) {
      switch (type) {
        case ROOSTER:
          FabricDefaultAttributeRegistry.register(
            (EntityType<? extends LivingEntity>) COMPANION_TYPE.get(type),
            ChickenBase.createAttributes().add(Attributes.ATTACK_DAMAGE, 3.0).build());
          break;
        case SMALL_SLIME:
          FabricDefaultAttributeRegistry.register(
            (EntityType<? extends LivingEntity>) COMPANION_TYPE.get(type),
            SlimeSmallBase.createAttributes().build());
          break;
        default:
          FabricDefaultAttributeRegistry.register(
            (EntityType<? extends LivingEntity>) COMPANION_TYPE.get(type),
            Pig.createAttributes().build());
          break;
      }
    }
  }

  public static void registerSpawnPlacements() {
    log.info("{} Companion Spawn Placements ...", Constants.LOG_REGISTER_PREFIX);
    for (CompanionEntityType type : CompanionEntityType.values()) {
      SpawnPlacements.register(
        (EntityType) COMPANION_TYPE.get(type),
        SpawnPlacements.Type.ON_GROUND,
        Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
        CompanionSpawnRules::checkCompanionSpawnRules);
    }
  }

  public static void registerBiomeSpawns() {
    if (!TamingConfig.NATURAL_SPAWNING_ENABLED) {
      log.info("Natural spawning is disabled via config, skipping biome spawn registration.");
      return;
    }
    log.info("{} Companion Biome Spawns ...", Constants.LOG_REGISTER_PREFIX);

    BiomeModifications.addSpawn(
      BiomeSelectors.foundInOverworld(),
      MobCategory.CREATURE,
      getEntityType(CompanionEntityType.SMALL_SLIME),
      TamingConfig.SPAWN_WEIGHT_SMALL_SLIME,
      TamingConfig.SPAWN_MIN_GROUP_SIZE,
      TamingConfig.SPAWN_MAX_GROUP_SIZE);

    BiomeModifications.addSpawn(
      BiomeSelectors.includeByKey(Biomes.PLAINS, Biomes.MEADOW, Biomes.SAVANNA),
      MobCategory.CREATURE,
      getEntityType(CompanionEntityType.PIG),
      TamingConfig.SPAWN_WEIGHT_PIG,
      TamingConfig.SPAWN_MIN_GROUP_SIZE,
      TamingConfig.SPAWN_MAX_GROUP_SIZE);

    BiomeModifications.addSpawn(
      BiomeSelectors.includeByKey(Biomes.PLAINS, Biomes.FOREST, Biomes.JUNGLE),
      MobCategory.CREATURE,
      getEntityType(CompanionEntityType.ROOSTER),
      TamingConfig.SPAWN_WEIGHT_ROOSTER,
      TamingConfig.SPAWN_MIN_GROUP_SIZE,
      TamingConfig.SPAWN_MAX_GROUP_SIZE);
  }
}
