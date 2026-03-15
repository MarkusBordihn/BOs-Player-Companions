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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.config.TamingConfig;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public record CompanionBiomeModifier(
  HolderSet<Biome> biomes,
  MobSpawnSettings.SpawnerData spawnerData,
  MobCategory mobCategory)
  implements BiomeModifier {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final Set<EntityType<?>> loggedTypes = new HashSet<>();

  public static Codec<CompanionBiomeModifier> makeCodec() {
    return RecordCodecBuilder.create(
      builder ->
        builder
          .group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(CompanionBiomeModifier::biomes),
            MobSpawnSettings.SpawnerData.CODEC
              .fieldOf("spawn")
              .forGetter(CompanionBiomeModifier::spawnerData),
            MobCategory.CODEC
              .fieldOf("mob_category")
              .forGetter(CompanionBiomeModifier::mobCategory))
          .apply(builder, CompanionBiomeModifier::new));
  }

  @Override
  public void modify(
    Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
    if (phase == Phase.ADD
      && this.biomes.contains(biome)
      && TamingConfig.NATURAL_SPAWNING_ENABLED) {
      if (loggedTypes.add(spawnerData.type)) {
        log.info("{} {} (weight {}, {}-{}) in {}+ biomes",
          Constants.LOG_REGISTER_PREFIX,
          spawnerData.type,
          spawnerData.getWeight(),
          spawnerData.minCount,
          spawnerData.maxCount,
          biome.unwrapKey().map(k -> k.location().toString()).orElse("?"));
      }
      builder.getMobSpawnSettings().addSpawn(mobCategory, spawnerData);
    }
  }

  @Override
  public Codec<? extends BiomeModifier> codec() {
    return CompanionBiomeModifiers.COMPANION_BIOME_MODIFIER.get();
  }
}
