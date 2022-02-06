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

import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.settings.KeyConflictContext;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.network.NetworkHandler;
import de.markusbordihn.playercompanions.network.message.MessageOpenCompanionsMenu;

public class ModKeyMapping {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public static final KeyMapping KEY_OPEN =
      new KeyMapping("key.slots.inventory", KeyConflictContext.IN_GAME,
          InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_N), "slots.key.category");

  public static void handleKeyMapping(InputEvent.KeyInputEvent event) {
    if (KEY_OPEN.isDown()) {
      NetworkHandler.INSTANCE.sendToServer(new MessageOpenCompanionsMenu());
    }
  }
}
