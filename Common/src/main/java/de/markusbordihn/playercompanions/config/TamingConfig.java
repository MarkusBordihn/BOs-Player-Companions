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

package de.markusbordihn.playercompanions.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@SuppressWarnings({"java:S1104", "java:S1444", "java:S3008"})
public class TamingConfig extends Config {

  public static final String CONFIG_FILE_NAME = "taming.cfg";
  public static final String CONFIG_FILE_HEADER =
    """
      Player Companions - Taming Configuration
      
      General Taming Settings
      trustRequired: Trust points required to tame a companion (default: 100)
      feedCooldown: Cooldown between feedings in ticks, 20 ticks = 1 second (default: 100)
      onlyOnePlayerCanTame: Can only the first player who feeds continue taming? (default: true)
      companionLimitPerPlayer: Max companions per player, 0 = unlimited (default: 0)
      
      Companion Physics
      companionPushable: Companions can be pushed by players and entities (default: true)
      companionVulnerable: Companions can take damage (default: true)
      ownerMustSneakToHurt: Owner must sneak to damage own companion (default: true)
      
      Visual Feedback
      showTrustParticles: Show particle effects when feeding (default: true)
      showMilestoneMessages: Show messages at 25%, 50%, 75% trust (default: true)
      showTrustBar: Show trust progress bar in actionbar (default: true)
      showTamingHints: Show proactive taming hints on wild companions (default: true)
      autoNameOnTame: Automatically name companions when tamed (default: true)
      
      Food Values
      Define trust values for different foods per companion type.
      Format: food.<companion_type>.<item_id> = <trust_value>
      """;
  // Food Values (loaded dynamically)
  private static final Map<String, Integer> FOOD_VALUES = new HashMap<>();
  // General Settings
  public static int TRUST_REQUIRED = 100;
  public static int FEED_COOLDOWN = 100;
  public static boolean ONLY_ONE_PLAYER_CAN_TAME = true;
  public static int COMPANION_LIMIT_PER_PLAYER = 0;
  // Companion Physics
  public static boolean COMPANION_PUSHABLE = true;
  public static boolean COMPANION_VULNERABLE = true;
  public static boolean OWNER_MUST_SNEAK_TO_HURT = true;
  // Visual Feedback
  public static boolean SHOW_TRUST_PARTICLES = true;
  public static boolean SHOW_MILESTONE_MESSAGES = true;
  public static boolean SHOW_TRUST_BAR = true;
  public static boolean SHOW_TAMING_HINTS = true;
  public static boolean AUTO_NAME_ON_TAME = true;

  public static void registerConfig() {
    registerConfigFile(CONFIG_FILE_NAME, CONFIG_FILE_HEADER);
    parseConfigFile();
  }

  public static void parseConfigFile() {
    File configFile = getConfigFile(CONFIG_FILE_NAME);
    Properties properties = readConfigFile(configFile);
    Properties unmodifiedProperties = (Properties) properties.clone();

    // General Settings
    TRUST_REQUIRED = parseConfigValue(properties, "trustRequired", TRUST_REQUIRED);
    FEED_COOLDOWN = parseConfigValue(properties, "feedCooldown", FEED_COOLDOWN);
    ONLY_ONE_PLAYER_CAN_TAME =
      parseConfigValue(properties, "onlyOnePlayerCanTame", ONLY_ONE_PLAYER_CAN_TAME);
    COMPANION_LIMIT_PER_PLAYER =
      parseConfigValue(properties, "companionLimitPerPlayer", COMPANION_LIMIT_PER_PLAYER);

    // Companion Physics
    COMPANION_PUSHABLE = parseConfigValue(properties, "companionPushable", COMPANION_PUSHABLE);
    COMPANION_VULNERABLE =
      parseConfigValue(properties, "companionVulnerable", COMPANION_VULNERABLE);
    OWNER_MUST_SNEAK_TO_HURT =
      parseConfigValue(properties, "ownerMustSneakToHurt", OWNER_MUST_SNEAK_TO_HURT);

    // Visual Feedback
    SHOW_TRUST_PARTICLES = parseConfigValue(properties, "showTrustParticles", SHOW_TRUST_PARTICLES);
    SHOW_MILESTONE_MESSAGES =
      parseConfigValue(properties, "showMilestoneMessages", SHOW_MILESTONE_MESSAGES);
    SHOW_TRUST_BAR = parseConfigValue(properties, "showTrustBar", SHOW_TRUST_BAR);
    SHOW_TAMING_HINTS = parseConfigValue(properties, "showTamingHints", SHOW_TAMING_HINTS);
    AUTO_NAME_ON_TAME = parseConfigValue(properties, "autoNameOnTame", AUTO_NAME_ON_TAME);

    // Food Values - Pig
    parseFoodValue(properties, "pig", "minecraft:wheat_seeds", 5);
    parseFoodValue(properties, "pig", "minecraft:carrot", 10);
    parseFoodValue(properties, "pig", "minecraft:potato", 10);
    parseFoodValue(properties, "pig", "minecraft:beetroot", 8);
    parseFoodValue(properties, "pig", "minecraft:golden_carrot", 25);

    // Food Values - Rooster
    parseFoodValue(properties, "rooster", "minecraft:wheat_seeds", 8);
    parseFoodValue(properties, "rooster", "minecraft:beetroot_seeds", 8);
    parseFoodValue(properties, "rooster", "minecraft:melon_seeds", 8);
    parseFoodValue(properties, "rooster", "minecraft:pumpkin_seeds", 8);
    parseFoodValue(properties, "rooster", "minecraft:torchflower_seeds", 10);
    parseFoodValue(properties, "rooster", "minecraft:golden_apple", 30);

    // Food Values - Small Slime
    parseFoodValue(properties, "small_slime", "minecraft:slime_ball", 15);
    parseFoodValue(properties, "small_slime", "minecraft:honey_bottle", 20);
    parseFoodValue(properties, "small_slime", "minecraft:honey_block", 40);

    updateConfigFileIfChanged(configFile, CONFIG_FILE_HEADER, properties, unmodifiedProperties);
  }

  private static void parseFoodValue(
    Properties properties, String companionType, String itemId, int defaultValue) {
    String key = "food." + companionType + "." + itemId;
    int value = parseConfigValue(properties, key, defaultValue);
    FOOD_VALUES.put(companionType + ":" + itemId, value);
  }

  public static int getFoodTrustValue(String companionType, String itemId) {
    return FOOD_VALUES.getOrDefault(companionType + ":" + itemId, 0);
  }

  public static boolean isValidFood(String companionType, String itemId) {
    return getFoodTrustValue(companionType, itemId) > 0;
  }

  public static Map<String, Integer> getFoodsForCompanion(String companionType) {
    Map<String, Integer> foods = new HashMap<>();
    String prefix = companionType + ":";
    FOOD_VALUES.forEach(
      (key, value) -> {
        if (key.startsWith(prefix)) {
          String itemId = key.substring(prefix.length());
          foods.put(itemId, value);
        }
      });
    return foods;
  }
}
