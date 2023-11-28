/*
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

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.client.gui.GuiPosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class CommonConfig {

  public static final ForgeConfigSpec commonSpec;
  public static final Config COMMON;
  public static final String MIN_GROUP_SIZE_TEXT = "Min group size.";
  public static final String MAX_GROUP_SIZE_TEXT = "Max group size.";
  public static final String SPAWN_WEIGHT_TEXT = "Spawn weight.";
  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  static {
    com.electronwill.nightconfig.core.Config.setInsertionOrderPreserved(true);
    final Pair<Config, ForgeConfigSpec> specPair =
        new ForgeConfigSpec.Builder().configure(Config::new);
    commonSpec = specPair.getRight();
    COMMON = specPair.getLeft();
    log.info("{} Common config ...", Constants.LOG_REGISTER_PREFIX);
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, commonSpec);
  }

  protected CommonConfig() {}

  @SubscribeEvent
  public static void handleModConfigReloadEvent(ModConfigEvent.Reloading event) {
    ModConfig config = event.getConfig();
    if (config.getSpec() != commonSpec) {
      return;
    }
    log.info("Reload common config file {} ...", config.getFileName());
  }

  public static class Config {

    public final ForgeConfigSpec.BooleanValue respawnOnDeath;
    public final ForgeConfigSpec.IntValue respawnDelay;
    public final ForgeConfigSpec.BooleanValue friendlyFire;

    public final ForgeConfigSpec.IntValue maxHealth;
    public final ForgeConfigSpec.IntValue maxAttackDamage;

    public final ForgeConfigSpec.EnumValue<GuiPosition> guiPosition;
    public final ForgeConfigSpec.IntValue guiOffsetX;
    public final ForgeConfigSpec.IntValue guiOffsetY;

    public final ForgeConfigSpec.BooleanValue dataBackupEnabled;
    public final ForgeConfigSpec.IntValue dataBackupInterval;

    public final ForgeConfigSpec.ConfigValue<List<String>> namesNPCFemale;
    public final ForgeConfigSpec.ConfigValue<List<String>> namesNPCMale;
    public final ForgeConfigSpec.ConfigValue<List<String>> namesNPCMisc;

    public final ForgeConfigSpec.ConfigValue<List<String>> namesCompanionFemale;
    public final ForgeConfigSpec.ConfigValue<List<String>> namesCompanionMale;
    public final ForgeConfigSpec.ConfigValue<List<String>> namesCompanionMisc;

    public final ForgeConfigSpec.IntValue collectorTypeRadius;

    public final ForgeConfigSpec.IntValue healerTypeRadius;
    public final ForgeConfigSpec.IntValue healerTypeMinAmount;
    public final ForgeConfigSpec.IntValue healerTypeMaxAmount;

    public final ForgeConfigSpec.IntValue supporterTypeRadius;
    public final ForgeConfigSpec.IntValue supporterTypeDamageBoostDuration;
    public final ForgeConfigSpec.IntValue supporterTypeDamageResistanceDuration;
    public final ForgeConfigSpec.IntValue supporterTypeFireResistanceDuration;

    public final ForgeConfigSpec.IntValue smallGhastExplosionPower;

    Config(ForgeConfigSpec.Builder builder) {
      builder.comment("Player Companion's (General configuration)");

      builder.push("General");
      respawnOnDeath =
          builder
              .comment(
                  "Respawn companion on death, if set to false the companion could not be respawned!")
              .define("respawnOnDeath", true);
      respawnDelay =
          builder
              .comment("Respawn delay in seconds (1200 secs = in-game day).")
              .defineInRange("respawnDelay", 1200, 1, 8400);
      friendlyFire =
          builder
              .comment(
                  "Allow's damage to an owned companion by the owner or their owned companions.")
              .define("friendlyFire", false);
      builder.pop();

      builder.push("Level scaling");
      maxHealth =
          builder
              .comment("The max base health a companion can get with level 60.")
              .defineInRange("maxHealth", 20, 0, 200);
      maxAttackDamage =
          builder
              .comment("The max base attack damage a companion can get with level 60.")
              .defineInRange("maxAttackDamage", 5, 0, 200);
      builder.pop();

      builder.push("Gui");
      guiPosition =
          builder
              .comment("Position for the gui elements.")
              .defineEnum("guiPosition", GuiPosition.HOTBAR_RIGHT);
      guiOffsetX =
          builder
              .comment("The offset on X axis from chosen position.")
              .defineInRange("guiOffsetX", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
      guiOffsetY =
          builder
              .comment("The offset on X axis from chosen position.")
              .defineInRange("guiOffsetY", 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
      builder.pop();

      builder.push("Backup");
      dataBackupEnabled =
          builder.comment("Enable automatic data backups.").define("dataBackupEnabled", true);
      dataBackupInterval =
          builder
              .comment("Time between automatic backups in minutes. (0 = disabled)")
              .defineInRange("dataBackupInterval", 1440, 0, 10080);
      builder.pop();

      builder.push("NPC Names");
      namesNPCFemale =
          builder
              .comment("List of female NPC names.")
              .define(
                  "namesNPCFemale",
                  new ArrayList<String>(
                      Arrays.asList(
                          "Aika",
                          "Amy",
                          "Asuna",
                          "Beatrice",
                          "Calypso",
                          "Cassandra",
                          "Deedlit",
                          "Elizabeth",
                          "Enya",
                          "Faith",
                          "Freya",
                          "Giselle",
                          "Isolde",
                          "Julia",
                          "Meredith",
                          "Monika",
                          "Sonya")));
      namesNPCMale =
          builder
              .comment("List of male NPC names.")
              .define(
                  "namesNPCMale",
                  new ArrayList<String>(
                      Arrays.asList(
                          "Adam", "Aron", "Beowulf", "Bob", "Cain", "Ethan", "Guy", "Jack", "Jason",
                          "John", "Julian", "Kawo", "Luca", "Markus", "Miso", "Parn", "Romolo")));
      namesNPCMisc =
          builder
              .comment("List of misc NPC names.")
              .define("namesNPCMisc", new ArrayList<String>(Arrays.asList("Alexis", "Quinn")));
      builder.pop();

      builder.push("Player Companion Names");
      namesCompanionFemale =
          builder
              .comment("List of female player companion names.")
              .define(
                  "namesCompanionFemale",
                  new ArrayList<String>(
                      Arrays.asList(
                          "Alice",
                          "Alina",
                          "Amy",
                          "Ayame",
                          "Beauty",
                          "Bella",
                          "Bonnie",
                          "Carla",
                          "Carmen",
                          "Celina",
                          "Chiyo",
                          "Chloe",
                          "Conny",
                          "Dina",
                          "Emi",
                          "Emma",
                          "Fluffy",
                          "Fuyumi",
                          "Gina",
                          "Haruhi",
                          "Hope",
                          "Iivy",
                          "Isabell",
                          "Itsumi",
                          "Jessie",
                          "Kacy",
                          "Karin",
                          "Kasumi",
                          "Keira",
                          "Lina",
                          "Lucy",
                          "Luna",
                          "Mai",
                          "Marina",
                          "Megumi",
                          "Melody",
                          "Mia",
                          "Mimi",
                          "Mizue",
                          "Nami",
                          "Nicky",
                          "Princess",
                          "Rainy",
                          "Reiko",
                          "Sakura",
                          "Sandy",
                          "Takeko",
                          "Trixie",
                          "Umi",
                          "Vivi",
                          "Yona",
                          "Yukari",
                          "Zoe")));
      namesCompanionMale =
          builder
              .comment("List of male player companion names.")
              .define(
                  "namesCompanionMale",
                  new ArrayList<String>(
                      Arrays.asList(
                          "Alex",
                          "Andrew",
                          "Archie",
                          "Benny",
                          "Charlie",
                          "Coco",
                          "Derek",
                          "Eric",
                          "Felix",
                          "Frankie",
                          "Gustav",
                          "Haruo",
                          "Hector",
                          "Henry",
                          "Hunter",
                          "Ikuo",
                          "Jin",
                          "Kasimir",
                          "Kazuma",
                          "Larry",
                          "Leo",
                          "Leonardo",
                          "Loki",
                          "Marin",
                          "Masato",
                          "Max",
                          "Norio",
                          "Osamu",
                          "Oskar",
                          "Prince",
                          "Roker",
                          "Rufus",
                          "Ryu",
                          "Shadow",
                          "Shin",
                          "Simba",
                          "Snickers",
                          "Sparky",
                          "Spike",
                          "Taizo",
                          "Tiger",
                          "Timmy",
                          "Turbo",
                          "Yoshi",
                          "Yuma",
                          "Zenjiro",
                          "Zottel")));
      namesCompanionMisc =
          builder
              .comment("List of misc player companion names.")
              .define(
                  "namesCompanionMisc",
                  new ArrayList<String>(
                      Arrays.asList(
                          "Alex", "Angel", "Buddy", "Cato", "Charlie", "Cheddar", "Creamy", "Curly",
                          "Dakota", "Elisa", "Foxy", "Frana", "Inky", "Isa", "Jesse", "Jona",
                          "Joyce", "Jule", "Kaya", "Luka", "Mika", "Morgan", "Patches", "Phantom",
                          "Riley", "Robin", "Sam", "Sanja", "Sascha", "Sasha", "Skye", "Smokey",
                          "Smokie", "Toni", "Yannie")));
      builder.pop();

      builder.push("Collector Type");
      collectorTypeRadius =
          builder
              .comment("Defines the radius in which items are automatically collected.")
              .defineInRange("collectorTypeRadius", 3, 0, 16);
      builder.pop();

      builder.push("Healer Type");
      healerTypeRadius =
          builder
              .comment("Defines the radius in which players are healed.")
              .defineInRange("healerTypeRadius", 8, 0, 32);
      healerTypeMinAmount =
          builder
              .comment("Defines the min. healing amount depending on the level.")
              .defineInRange("healerTypeMinAmount", 2, 0, 16);
      healerTypeMaxAmount =
          builder
              .comment("Defines the max. healing amount depending on the level.")
              .defineInRange("healerTypeMaxAmount", 10, 0, 16);
      builder.pop();

      builder.push("Supporter Type");
      supporterTypeRadius =
          builder
              .comment("Defines the radius in which players are buffed.")
              .defineInRange("supporterTypeRadius", 8, 0, 32);
      supporterTypeDamageBoostDuration =
          builder
              .comment("Defines the amount of ticks how long the damage boost is enabled.")
              .defineInRange("supporterTypeDamageBoostDuration", 1200, 20, 6000);
      supporterTypeDamageResistanceDuration =
          builder
              .comment(
                  "Defines the amount of ticks how long the damage resistance protection is enabled.")
              .defineInRange("supporterTypeDamageResistanceDuration", 1200, 20, 6000);
      supporterTypeFireResistanceDuration =
          builder
              .comment(
                  "Defines the amount of ticks how long the fire resistance protection is enabled.")
              .defineInRange("supporterTypeFireResistanceDuration", 1200, 20, 6000);
      builder.pop();

      builder.push("Small Ghast");
      smallGhastExplosionPower =
          builder.comment("Explosion power").defineInRange("smallGhastExplosionPower", 0, 0, 16);
      builder.pop();
    }
  }
}
