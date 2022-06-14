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

import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.levelgen.Heightmap;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.entity.companions.Dobutsu;
import de.markusbordihn.playercompanions.entity.companions.Fairy;
import de.markusbordihn.playercompanions.entity.companions.Firefly;
import de.markusbordihn.playercompanions.entity.companions.ModEntityType;
import de.markusbordihn.playercompanions.entity.companions.SmallGhast;
import de.markusbordihn.playercompanions.entity.companions.SmallSlime;
import de.markusbordihn.playercompanions.entity.companions.Snail;


public class SpawnHandler {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  protected SpawnHandler() {}

  public static void registerSpawnPlacements(final FMLCommonSetupEvent event) {
    log.info("{} Spawn Placements ...", Constants.LOG_REGISTER_PREFIX);
    event.enqueueWork(() -> {
      SpawnPlacements.register(ModEntityType.DOBUTSU.get(), SpawnPlacements.Type.ON_GROUND,
          Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Dobutsu::checkAnimalSpawnRules);
      SpawnPlacements.register(ModEntityType.FAIRY.get(), SpawnPlacements.Type.ON_GROUND,
          Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Fairy::checkAnimalSpawnRules);
      SpawnPlacements.register(ModEntityType.FIREFLY.get(), SpawnPlacements.Type.ON_GROUND,
          Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Firefly::checkAnimalSpawnRules);
      SpawnPlacements.register(ModEntityType.PIG.get(), SpawnPlacements.Type.ON_GROUND,
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

}
