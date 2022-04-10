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

package de.markusbordihn.playercompanions.utils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.io.IOUtils;

import com.mojang.authlib.GameProfile;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.GameProfileCache;

public class Players {

  protected Players() {}

  public static Optional<GameProfile> getGameProfile(MinecraftServer server, Component component) {
    return getGameProfile(server, component.getString());
  }

  public static Optional<GameProfile> getGameProfile(MinecraftServer server, String username) {
    if (server == null || username == null || username.isEmpty()) {
      return Optional.empty();
    }
    final GameProfileCache gameProfileCache = server.getProfileCache();
    return gameProfileCache.get(username);
  }

  public static String getUserTexture(String user) {
    String sessionURL =
        String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s", user);
    try {
      String data = IOUtils.toString(new URL(sessionURL), StandardCharsets.UTF_8);
      if (data == null || data.isEmpty()) {
        return null;
      }
      return data;
    } catch (IOException | org.apache.http.ParseException iOException) {
      return null;
    }
  }

}
