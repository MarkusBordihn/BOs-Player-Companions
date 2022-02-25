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

package de.markusbordihn.playercompanions.client.keymapping;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;

import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import de.markusbordihn.playercompanions.Constants;

public class ModKeyMapping {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  protected ModKeyMapping() {}

  public static final KeyMapping KEY_ALT = new KeyMapping(Constants.KEY_PREFIX + "alt",
      KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_LEFT_ALT),
      Constants.KEY_PREFIX + "category");

  public static final KeyMapping KEY_COMMAND =
      new KeyMapping(Constants.KEY_PREFIX + "control", KeyConflictContext.IN_GAME,
          InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_LEFT_CONTROL),
          Constants.KEY_PREFIX + "category");

  public static void registerKeyMapping(final FMLClientSetupEvent event) {
    log.info("{} Key Mapping ...", Constants.LOG_REGISTER_PREFIX);

    event.enqueueWork(() -> {
      ClientRegistry.registerKeyBinding(ModKeyMapping.KEY_ALT);
      ClientRegistry.registerKeyBinding(ModKeyMapping.KEY_COMMAND);
    });
  }
}
