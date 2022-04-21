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

package de.markusbordihn.playercompanions.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.io.IOUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import com.mojang.authlib.GameProfile;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.GameProfileCache;

import de.markusbordihn.playercompanions.Constants;

public class PlayersUtils {

  protected static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  private static final String USER_REGEX = "\\w.*";

  protected PlayersUtils() {}

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

  public static String getUserTexture(MinecraftServer server, String username) {
    Optional<GameProfile> gameProfile = PlayersUtils.getGameProfile(server, username);
    if (gameProfile.isPresent() && gameProfile.get() != null && gameProfile.get().getId() != null) {
      String userUUID = gameProfile.get().getId().toString();
      if (userUUID != null && !userUUID.isEmpty()) {
        return getUserTexture(userUUID);
      }
    }

    log.error("Unable to get userUUID for user {} to load user texture!");
    return "";
  }

  public static String getUserTexture(String userUUID) {
    String sessionURL =
        String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s", userUUID);
    try {
      String data = IOUtils.toString(new URL(sessionURL), StandardCharsets.UTF_8);
      if (data == null || data.isEmpty()) {
        log.error("Unable to get user texture with {}", sessionURL);
        return null;
      }
      return getUserTextureFromSessionResponse(data);
    } catch (IOException | org.apache.http.ParseException iOException) {
      return null;
    }
  }

  public static String getUserTextureFromSessionResponse(String data) {
    if (data == null || data.isEmpty()) {
      return "";
    }
    JsonElement jsonElement;
    try {
      jsonElement = JsonParser.parseString(data);
    } catch (JsonParseException jsonParseException) {
      log.error("ERROR: Unable to parse json data: {}", data);
      return "";
    }
    if (jsonElement != null && jsonElement.isJsonObject()) {
      JsonObject jsonObject = jsonElement.getAsJsonObject();
      if (jsonObject.has("properties")) {
        JsonArray properties = jsonObject.getAsJsonArray("properties");
        log.debug("getUserTextureFromSessionRequest: {}", properties);
        for (JsonElement property : properties) {
          JsonObject propertyObject = property.getAsJsonObject();
          if (propertyObject.has("name")
              && "textures".equals(propertyObject.get("name").getAsString())
              && propertyObject.has("value")) {
            String textureData =
                new String(Base64.getDecoder().decode(propertyObject.get("value").getAsString()));
            return getUserTextureFromTextureData(textureData);
          }
        }
      }
    }
    return "";
  }

  public static String getUserTextureFromTextureData(String data) {
    if (data == null || data.isEmpty()) {
      return "";
    }
    JsonElement jsonElement;
    try {
      jsonElement = JsonParser.parseString(data);
    } catch (JsonParseException jsonParseException) {
      log.error("ERROR: Unable to parse json data: {}", data);
      return "";
    }
    if (jsonElement != null && jsonElement.isJsonObject()) {
      JsonObject jsonObject = jsonElement.getAsJsonObject();
      log.debug("getUserTextureFromTextureData: {}", jsonObject);
      if (jsonObject.has("textures")) {
        JsonObject textureObject = jsonObject.getAsJsonObject("textures");
        if (textureObject.has("SKIN")) {
          JsonObject skinObject = textureObject.getAsJsonObject("SKIN");
          if (skinObject.has("url")) {
            return skinObject.get("url").getAsString();
          }
        }
      }
    }
    return "";
  }

  public static boolean isValidPlayerName(String name) {
    return name != null && !name.isEmpty() && !name.startsWith("http") && !name.equals("htt")
        && name.length() >= 3 && name.length() <= 16 && name.matches(USER_REGEX);
  }

  public static boolean isValidUrl(String url) {
    if (url == null || url.isEmpty()
        || (!url.startsWith("http://") && !url.startsWith("https://"))) {
      return false;
    }
    try {
      new URL(url).toURI();
    } catch (MalformedURLException | URISyntaxException e) {
      return false;
    }
    return true;
  }

}
