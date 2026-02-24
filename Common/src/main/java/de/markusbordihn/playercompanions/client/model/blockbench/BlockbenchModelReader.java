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

package de.markusbordihn.playercompanions.client.model.blockbench;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockbenchModelReader {

  private static final Logger log = LogManager.getLogger(BlockbenchModelReader.class);
  private static final String SUPPORTED_FORMAT_VERSION = "5.0";
  private static final String SUPPORTED_MODEL_FORMAT = "modded_entity";

  public static BlockbenchModel read(InputStream inputStream) throws Exception {
    JsonObject root =
      JsonParser.parseReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        .getAsJsonObject();

    validateFormat(root);

    JsonObject resolution = root.getAsJsonObject("resolution");
    int textureWidth = resolution.get("width").getAsInt();
    int textureHeight = resolution.get("height").getAsInt();

    List<BlockbenchElement> elements = new ArrayList<>();
    JsonArray elementsArray = root.getAsJsonArray("elements");
    for (JsonElement elem : elementsArray) {
      elements.add(parseElement(elem.getAsJsonObject()));
    }

    List<BlockbenchGroup> groups = new ArrayList<>();
    JsonArray groupsArray = root.getAsJsonArray("groups");
    for (JsonElement grp : groupsArray) {
      groups.add(parseGroup(grp.getAsJsonObject()));
    }

    List<BlockbenchOutlinerNode> outliner = new ArrayList<>();
    JsonArray outlinerArray = root.getAsJsonArray("outliner");
    for (JsonElement outl : outlinerArray) {
      outliner.add(parseOutlinerNode(outl));
    }

    return new BlockbenchModel(textureWidth, textureHeight, elements, groups, outliner);
  }

  private static BlockbenchElement parseElement(JsonObject json) {
    String uuid = json.get("uuid").getAsString();
    String name = json.get("name").getAsString();

    float[] from = parseFloatArray(json.getAsJsonArray("from"));
    float[] to = parseFloatArray(json.getAsJsonArray("to"));

    int[] uvOffset = new int[]{0, 0};
    if (json.has("uv_offset")) {
      JsonArray uvArray = json.getAsJsonArray("uv_offset");
      uvOffset = new int[]{uvArray.get(0).getAsInt(), uvArray.get(1).getAsInt()};
    }

    float[] origin = new float[]{0, 0, 0};
    if (json.has("origin")) {
      origin = parseFloatArray(json.getAsJsonArray("origin"));
    }

    float[] rotation = null;
    if (json.has("rotation")) {
      rotation = parseFloatArray(json.getAsJsonArray("rotation"));
    }

    boolean mirrorUv = json.has("mirror_uv") && json.get("mirror_uv").getAsBoolean();

    return new BlockbenchElement(uuid, name, from, to, uvOffset, origin, rotation, mirrorUv);
  }

  private static BlockbenchGroup parseGroup(JsonObject json) {
    String uuid = json.get("uuid").getAsString();
    String name = json.get("name").getAsString();

    float[] origin = new float[]{0, 0, 0};
    if (json.has("origin")) {
      origin = parseFloatArray(json.getAsJsonArray("origin"));
    }

    float[] rotation = new float[]{0, 0, 0};
    if (json.has("rotation")) {
      rotation = parseFloatArray(json.getAsJsonArray("rotation"));
    }

    return new BlockbenchGroup(uuid, name, origin, rotation);
  }

  private static BlockbenchOutlinerNode parseOutlinerNode(JsonElement element) {
    if (element.isJsonPrimitive()) {
      String uuid = element.getAsString();
      BlockbenchOutlinerNode node = new BlockbenchOutlinerNode(null);
      node.addChildElement(uuid);
      return node;
    } else {
      JsonObject obj = element.getAsJsonObject();
      String uuid = obj.get("uuid").getAsString();
      BlockbenchOutlinerNode node = new BlockbenchOutlinerNode(uuid);

      if (obj.has("children")) {
        JsonArray children = obj.getAsJsonArray("children");
        for (JsonElement child : children) {
          if (child.isJsonPrimitive()) {
            node.addChildElement(child.getAsString());
          } else {
            node.addChildNode(parseOutlinerNode(child));
          }
        }
      }

      return node;
    }
  }

  private static float[] parseFloatArray(JsonArray array) {
    float[] result = new float[array.size()];
    for (int i = 0; i < array.size(); i++) {
      result[i] = array.get(i).getAsFloat();
    }
    return result;
  }

  private static void validateFormat(JsonObject root) {
    if (!root.has("meta")) {
      log.warn("Blockbench model is missing 'meta' section");
      return;
    }

    JsonObject meta = root.getAsJsonObject("meta");

    if (meta.has("format_version")) {
      String formatVersion = meta.get("format_version").getAsString();
      if (!formatVersion.startsWith("5.")) {
        log.warn("Unsupported Blockbench format version: {}. Expected version 5.x", formatVersion);
      }
    } else {
      log.warn("Blockbench model is missing 'format_version' in meta section");
    }

    if (meta.has("model_format")) {
      String modelFormat = meta.get("model_format").getAsString();
      if (!SUPPORTED_MODEL_FORMAT.equals(modelFormat)) {
        log.warn(
          "Unsupported Blockbench model format: {}. Expected '{}'. Model may not load correctly.",
          modelFormat, SUPPORTED_MODEL_FORMAT);
      }
    } else {
      log.warn("Blockbench model is missing 'model_format' in meta section");
    }
  }
}
