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

package de.markusbordihn.playercompanions;

import java.util.UUID;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;

public final class Constants {

  // General Mod definitions
  public static final String LOG_NAME = "Bo's Player Companions";
  public static final String LOG_ICON = "👾";
  public static final String LOG_ICON_NAME = LOG_ICON + " " + LOG_NAME;
  public static final String LOG_REGISTER_PREFIX = LOG_ICON + " Register Player Companions";
  public static final String LOG_SPAWN_PREFIX = LOG_ICON + " Enable spawn for";
  public static final String LOG_PLAYER_COMPANION_DATA_PREFIX = "";
  public static final String MOD_COMMAND = "player_companions";
  public static final String MOD_ID = "player_companions";
  public static final String MOD_NAME = "Bo's Player Companions";
  public static final String MOD_URL =
      "https://www.curseforge.com/minecraft/mc-mods/player-companions";
  // Prefixes
  public static final String MINECRAFT_PREFIX = "minecraft";
  public static final String ENTITY_TEXT_PREFIX = "entity";
  public static final String TEXT_PREFIX = "text.player_companions.";
  public static final String AGGRESSION_LEVEL_PREFIX = TEXT_PREFIX + "aggression_level.";
  public static final String COMMAND_PREFIX = TEXT_PREFIX + "commands.";
  public static final String KEY_PREFIX = "key.player_companions.";
  // Colors
  public static final int FONT_COLOR_BLACK = 0;
  public static final int FONT_COLOR_DARK_GREEN = 43520;
  public static final int FONT_COLOR_DEFAULT = 4210752;
  public static final int FONT_COLOR_GRAY = 11184810;
  public static final int FONT_COLOR_GREEN = 5635925;
  public static final int FONT_COLOR_RED = 16733525;
  public static final int FONT_COLOR_WHITE = 0xffffff;
  public static final int FONT_COLOR_YELLOW = 16777045;
  // Textures
  public static final UUID BLANK_UUID = new UUID(0L, 0L);
  public static final ResourceLocation BLANK_ENTITY_TEXTURE =
      new ResourceLocation(Constants.MOD_ID, "textures/entity/blank.png");
  public static final ResourceLocation TEXTURE_INVENTORY =
      new ResourceLocation(MINECRAFT_PREFIX, "textures/gui/container/inventory.png");
  public static final ResourceLocation TEXTURE_CHECKBOX =
      new ResourceLocation(MINECRAFT_PREFIX, "textures/gui/checkbox.png");
  // 3rd Party Mods
  public static final String NEAT_MOD = "neat";
  public static final String NEAT_NAME = "Neat";
  public static final boolean NEAT_LOADED = ModList.get().isLoaded(NEAT_MOD);
  // Resource Paths
  public static final ResourceLocation BLOCK_ATLAS =
      new ResourceLocation(Constants.MOD_ID, "textures/atlas/blocks.png");
  // Animation Math
  public static final float MATH_27DEG_TO_RAD = 0.47123894F;
  public static final float MATH_27DEG_TO_RAD_INVERTED = -0.47123894F;
  private Constants() {
  }
}
