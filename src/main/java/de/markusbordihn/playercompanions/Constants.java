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

package de.markusbordihn.playercompanions;

import net.minecraftforge.fml.ModList;

public final class Constants {

  protected Constants() {
  }

  // General Mod definitions
  public static final String LOG_NAME = "Bo's Player Companions";
  public static final String LOG_ICON = "👾";
  public static final String LOG_ICON_NAME = LOG_ICON + " " + LOG_NAME;
  public static final String LOG_REGISTER_PREFIX = LOG_ICON + " Register Player Companions";
  public static final String LOG_SPAWN_PREFIX = LOG_ICON + " Enable spawn for";
  public static final String LOG_PLAYER_COMPANION_DATA_PREFIX = "";
  public static final String MOD_COMMAND = "companions";
  public static final String MOD_ID = "player_companions";
  public static final String MOD_NAME = "Bo's Player Companions";

  // Prefixes
  public static final String TEXT_PREFIX = "text.player_companions.";
  public static final String KEY_PREFIX = "key.player_companions.";

  // 3rd Party Mods
  public static final String NEAT_MOD = "neat";
  public static final String NEAT_NAME = "Neat";
  public static final boolean NEAT_LOADED = ModList.get().isLoaded(NEAT_MOD);
}
