/*
 * Copyright 2023 Markus Bordihn
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

package de.markusbordihn.playercompanions.utils;

import de.markusbordihn.playercompanions.Constants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TitleUtils {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  public static final void setTitle(String title, String subTitle, ServerPlayer serverPlayer) {
    setTitle(title != null && !title.isBlank() ? Component.literal(title) : null,
        subTitle != null ? Component.literal(subTitle) : null, serverPlayer);
  }

  public static final void setTitle(Component title, Component subTitle,
      ServerPlayer serverPlayer) {
    if (subTitle != null) {
      serverPlayer.connection.send(new ClientboundSetSubtitleTextPacket(subTitle));
    }
    if (title != null) {
      serverPlayer.connection.send(new ClientboundSetTitleTextPacket(title));
    }
  }

}
