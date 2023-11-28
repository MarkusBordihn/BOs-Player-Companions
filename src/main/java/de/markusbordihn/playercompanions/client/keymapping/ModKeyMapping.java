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

package de.markusbordihn.playercompanions.client.keymapping;

import com.mojang.blaze3d.platform.InputConstants;
import de.markusbordihn.playercompanions.Constants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

public class ModKeyMapping {

  public static final KeyMapping AGGRESSION_KEY =
      new KeyMapping(
          Constants.KEY_PREFIX + "aggression",
          KeyConflictContext.IN_GAME,
          InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_LEFT_ALT),
          Constants.KEY_PREFIX + "category");
  public static final KeyMapping COMMAND_KEY =
      new KeyMapping(
          Constants.KEY_PREFIX + "control",
          KeyConflictContext.IN_GAME,
          InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_LEFT_CONTROL),
          Constants.KEY_PREFIX + "category");
  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  protected ModKeyMapping() {}

  public static void registerKeyMapping(RegisterKeyMappingsEvent event) {
    log.info("{} Key Mapping ...", Constants.LOG_REGISTER_PREFIX);
    event.register(ModKeyMapping.AGGRESSION_KEY);
    event.register(ModKeyMapping.COMMAND_KEY);
  }
}
