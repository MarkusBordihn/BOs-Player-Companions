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

package de.markusbordihn.playercompanions.client.model;

import de.markusbordihn.playercompanions.Constants;
import de.markusbordihn.playercompanions.client.model.blockbench.BlockbenchModel;
import de.markusbordihn.playercompanions.client.model.blockbench.BlockbenchModelReader;
import de.markusbordihn.playercompanions.client.model.minecraft.BlockbenchModelConverter;
import de.markusbordihn.playercompanions.client.model.minecraft.MinecraftCube;
import de.markusbordihn.playercompanions.client.model.minecraft.MinecraftModel;
import de.markusbordihn.playercompanions.client.model.minecraft.MinecraftPart;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockbenchModelLoader {

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);
  private static final Map<String, LayerDefinition> MODEL_CACHE = new HashMap<>();

  private BlockbenchModelLoader() {
  }

  public static LayerDefinition loadModel(
    String modelName, Supplier<LayerDefinition> fallback) {
    if (MODEL_CACHE.containsKey(modelName)) {
      log.debug("Loading cached model: {}", modelName);
      return MODEL_CACHE.get(modelName);
    }

    try {
      ResourceLocation modelLocation =
        new ResourceLocation(Constants.MOD_ID, "models/entity/" + modelName + ".bbmodel");
      log.info("Attempting to load Blockbench model: {}", modelLocation);

      Resource resource =
        Minecraft.getInstance()
          .getResourceManager()
          .getResource(modelLocation)
          .orElseThrow(() -> new RuntimeException("Model file not found: " + modelLocation));

      try (InputStream inputStream = resource.open()) {
        BlockbenchModel blockbenchModel = BlockbenchModelReader.read(inputStream);
        MinecraftModel minecraftModel = BlockbenchModelConverter.convert(blockbenchModel);
        LayerDefinition layerDefinition = createLayerDefinition(minecraftModel);

        MODEL_CACHE.put(modelName, layerDefinition);
        log.info("Successfully loaded and cached Blockbench model: {}", modelName);

        return layerDefinition;
      }
    } catch (Exception e) {
      if (fallback != null) {
        log.warn("Failed to load Blockbench model '{}', using fallback: {}", modelName,
          e.getMessage());
        LayerDefinition fallbackLayer = fallback.get();
        MODEL_CACHE.put(modelName, fallbackLayer);
        return fallbackLayer;
      } else {
        log.error("Failed to load Blockbench model '{}' and no fallback provided: {}", modelName,
          e.getMessage(), e);
        return null;
      }
    }
  }

  private static LayerDefinition createLayerDefinition(MinecraftModel model) {
    MeshDefinition meshDefinition = new MeshDefinition();
    PartDefinition root = meshDefinition.getRoot();
    for (MinecraftPart part : model.getRootParts()) {
      addPart(root, part);
    }

    return LayerDefinition.create(meshDefinition, model.getTextureWidth(),
      model.getTextureHeight());
  }

  private static void addPart(PartDefinition parent, MinecraftPart part) {
    CubeListBuilder cubeBuilder = CubeListBuilder.create();
    for (MinecraftCube cube : part.getCubes()) {
      float[] pos = cube.getPosition();
      float[] dim = cube.getDimensions();
      int[] uv = cube.getUvOffset();

      cubeBuilder = cubeBuilder.texOffs(uv[0], uv[1])
        .addBox(pos[0], pos[1], pos[2], dim[0], dim[1], dim[2], new CubeDeformation(0.0F));

      if (cube.isMirror()) {
        cubeBuilder = cubeBuilder.mirror();
      }
    }

    // Create part with offset and rotation
    float[] offset = part.getOffset();
    float[] rotation = part.getRotation();
    PartDefinition partDef = parent.addOrReplaceChild(
      part.getName(),
      cubeBuilder,
      PartPose.offsetAndRotation(
        offset[0], offset[1], offset[2],
        rotation[0], rotation[1], rotation[2]));

    // Add children recursively
    for (MinecraftPart child : part.getChildren()) {
      addPart(partDef, child);
    }
  }

  public static void clearCache() {
    MODEL_CACHE.clear();
    log.info("Cleared Blockbench model cache");
  }
}
