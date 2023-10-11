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

package de.markusbordihn.playercompanions.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.markusbordihn.playercompanions.Constants;

public enum PlayerCompanionVariant {
  BLACK,
  BLUE,
  BROWN,
  CREEPER,
  CYAN,
  DARK_GREEN,
  DEFAULT,
  DEFAULT_ACTION,
  DESERT,
  ENDERMAN,
  GRAY,
  GREEN,
  LIGHT_BLUE,
  LIGHT_GRAY,
  LIME,
  MAGENTA,
  MIXED,
  NONE,
  ORANGE,
  PINK,
  PURPLE,
  RED,
  SPOTTED,
  WHITE,
  YELLOW;

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public static PlayerCompanionVariant getOrDefault(String value) {
    if (value != null && !value.isEmpty()) {
      try {
        return valueOf(value);
      } catch (IllegalArgumentException exception) {
        log.warn("Unable to find a valid variant for {}!", value);
      }
    }
    return PlayerCompanionVariant.DEFAULT;
  }

  public String getSuffix() {
    return "_" + this.name().toLowerCase();
  }

}
