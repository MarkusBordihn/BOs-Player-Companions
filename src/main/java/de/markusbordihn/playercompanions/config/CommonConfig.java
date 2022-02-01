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

package de.markusbordihn.playercompanions.config;

import org.apache.commons.lang3.tuple.Pair;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;

import de.markusbordihn.playercompanions.Constants;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class CommonConfig {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public static final ForgeConfigSpec commonSpec;
  public static final Config COMMON;

  protected CommonConfig() {}

  static {
    com.electronwill.nightconfig.core.Config.setInsertionOrderPreserved(true);
    final Pair<Config, ForgeConfigSpec> specPair =
        new ForgeConfigSpec.Builder().configure(Config::new);
    commonSpec = specPair.getRight();
    COMMON = specPair.getLeft();
    log.info("{} Common config ...", Constants.LOG_REGISTER_PREFIX);
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, commonSpec);
  }

  public static class Config {

    public final ForgeConfigSpec.BooleanValue respawnOnDeath;
    public final ForgeConfigSpec.IntValue respawnDelay;
    public final ForgeConfigSpec.BooleanValue enableCompanionGhost;
    public final ForgeConfigSpec.BooleanValue friendlyFire;

    public final ForgeConfigSpec.BooleanValue smallSlimeSpawnEnable;
    public final ForgeConfigSpec.IntValue smallSlimeMinGroup;
    public final ForgeConfigSpec.IntValue smallSlimeMaxGroup;
    public final ForgeConfigSpec.IntValue smallSlimeWeight;

    public final ForgeConfigSpec.BooleanValue smallGhastSpawnEnable;
    public final ForgeConfigSpec.IntValue smallGhastMinGroup;
    public final ForgeConfigSpec.IntValue smallGhastMaxGroup;
    public final ForgeConfigSpec.IntValue smallGhastWeight;
    public final ForgeConfigSpec.IntValue smallGhastExplosionPower;

    Config(ForgeConfigSpec.Builder builder) {
      builder.comment("Player Companion's (General configuration)");

      // General Config
      builder.push("General");
      respawnOnDeath =
          builder.comment("Respawn companion on death.").define("respawnOnDeath", true);
      respawnDelay = builder.comment("Respawn delay in seconds (1200 secs = in-game day).")
          .defineInRange("respawnDelay", 1200, 1, 8400);
      friendlyFire = builder
          .comment("Allow's damage to an owned companion by the owner or their owned companions.")
          .define("friendlyFire", false);
      enableCompanionGhost = builder.comment("Enable / Disable ghosts for died player companions.")
          .define("enableCompanionGhost", true);
      builder.pop();

      // Small Slime
      builder.push("Small Slime");
      smallSlimeSpawnEnable = builder.comment("Enable/Disable the small slime spawn.")
          .define("smallSlimeSpawnEnable", true);
      smallSlimeMinGroup =
          builder.comment("Min group size.").defineInRange("smallSlimeMinGroup", 1, 0, 64);
      smallSlimeMaxGroup =
          builder.comment("Max group size.").defineInRange("smallSlimeMaxGroup", 2, 0, 64);
      smallSlimeWeight =
          builder.comment("Spawn weight.").defineInRange("smallSlimeWeight", 5, 0, 100);
      builder.pop();

      // Small Ghast
      builder.push("Small Ghast");
      smallGhastExplosionPower =
          builder.comment("Explosion power").defineInRange("smallGhastExplosionPower", 0, 0, 16);
      smallGhastSpawnEnable = builder.comment("Enable/Disable the small ghast spawn.")
          .define("smallGhastSpawnEnable", true);
      smallGhastMinGroup =
          builder.comment("Min group size.").defineInRange("smallGhastMinGroup", 1, 0, 64);
      smallGhastMaxGroup =
          builder.comment("Max group size.").defineInRange("smallGhastMaxGroup", 2, 0, 64);
      smallGhastWeight =
          builder.comment("Spawn weight.").defineInRange("smallGhastWeight", 5, 0, 100);
      builder.pop();

    }
  }
}
