/*
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

package de.markusbordihn.playercompanions.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.markusbordihn.playercompanions.Constants;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public record ModBiomeModifier(HolderSet<Biome> biomes, HolderSet<Biome> denylistBiomes,
                               MobSpawnSettings.SpawnerData spawnerData,
                               MobCategory mobCategory) implements BiomeModifier {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public static Codec<ModBiomeModifier> makeCodec() {
    return RecordCodecBuilder.create(builder -> builder
        .group(Biome.LIST_CODEC.fieldOf("biomes").forGetter(ModBiomeModifier::biomes),
            Biome.LIST_CODEC.fieldOf("denylist_biomes").forGetter(ModBiomeModifier::denylistBiomes),
            MobSpawnSettings.SpawnerData.CODEC.fieldOf("spawn")
                .forGetter(ModBiomeModifier::spawnerData),
            MobCategory.CODEC.fieldOf("mob_category").forGetter(ModBiomeModifier::mobCategory))
        .apply(builder, ModBiomeModifier::new));
  }

  @Override
  public void modify(Holder<Biome> biome, Phase phase,
      ModifiableBiomeInfo.BiomeInfo.Builder builder) {
    if (phase == Phase.ADD && this.biomes.contains(biome)
        && !biome.containsTag(Tags.Biomes.IS_MODIFIED) && !denylistBiomes.contains(biome)) {
      logSpawn(biome, mobCategory, spawnerData);
      builder.getMobSpawnSettings().addSpawn(mobCategory, spawnerData);
    }
  }

  @Override
  public Codec<? extends BiomeModifier> codec() {
    return ModBiomeModifiers.ENTITY_MODIFIER_TYPE.get();
  }

  private void logSpawn(Holder<Biome> biome, MobCategory mobCategory, SpawnerData spawnerData) {
    log.info("{} {} ({}) with weight {}, min {} and max {} in {}{}.", Constants.LOG_SPAWN_PREFIX,
        spawnerData.type, mobCategory, spawnerData.getWeight(), spawnerData.minCount,
        spawnerData.maxCount, biome.unwrapKey(),
        denylistBiomes.size() > 0 ? " (excluding " + denylistBiomes + ")" : "");
  }
}
