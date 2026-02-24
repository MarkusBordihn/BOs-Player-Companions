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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import de.markusbordihn.playercompanions.client.model.blockbench.BlockbenchModel;
import de.markusbordihn.playercompanions.client.model.blockbench.BlockbenchModelReader;
import de.markusbordihn.playercompanions.client.model.minecraft.BlockbenchModelConverter;
import de.markusbordihn.playercompanions.client.model.minecraft.MinecraftModel;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.junit.jupiter.api.Test;

public class BlockbenchRoosterModelLoaderTest {

  @Test
  public void testRoosterModelParsing() throws Exception {
    InputStream modelStream =
      getClass()
        .getResourceAsStream("/assets/player_companions/models/entity/rooster.bbmodel");
    assertNotNull(modelStream, "rooster.bbmodel should exist in resources");

    // Use the actual production code to parse
    BlockbenchModel blockbenchModel = BlockbenchModelReader.read(modelStream);
    assertNotNull(blockbenchModel, "BlockbenchModel should not be null");

    MinecraftModel minecraftModel = BlockbenchModelConverter.convert(blockbenchModel);
    assertNotNull(minecraftModel, "MinecraftModel should not be null");

    LayerDefinition parsedLayer = createLayerDefinition(minecraftModel);
    assertNotNull(parsedLayer, "Layer definition should not be null");

    // Get MeshDefinition via reflection
    Field meshField = LayerDefinition.class.getDeclaredField("mesh");
    meshField.setAccessible(true);
    MeshDefinition parsedMesh = (MeshDefinition) meshField.get(parsedLayer);
    assertNotNull(parsedMesh, "Mesh definition should not be null");

    PartDefinition parsedRoot = parsedMesh.getRoot();
    assertNotNull(parsedRoot, "Root part definition should not be null");

    PartDefinition body = parsedRoot.getChild("body");
    assertNotNull(body, "Body part should exist");
    assertTrue(hasCubes(body), "Body should have direct cubes");
    assertPartPose(body, 0.0F, 16.0F, 0.0F, 0.0F, 0.0F, 0.0F, "body");

    PartDefinition bodyR1 = body.getChild("body_r1");
    assertNotNull(bodyR1, "body_r1 part should exist");
    assertEquals(5, getCubeCount(bodyR1), "body_r1 should have 5 cubes");
    assertPartPose(bodyR1, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F, 0.0F, "body_r1");

    PartDefinition tail = body.getChild("tail");
    assertNotNull(tail, "Tail part should exist");
    assertPartPose(tail, 0.0F, 8.0F, 0.0F, 0.0F, 0.0F, 0.0F, "tail");

    PartDefinition tailR1 = tail.getChild("tail_r1");
    assertNotNull(tailR1, "tail_r1 part should exist");
    assertEquals(2, getCubeCount(tailR1), "tail_r1 should have 2 cubes");
    assertPartPose(tailR1, 0.0F, -12.5F, 6.5F, -0.3927F, 0.0F, 0.0F, "tail_r1");

    PartDefinition tailR2 = tail.getChild("tail_r2");
    assertNotNull(tailR2, "tail_r2 part should exist");
    assertEquals(1, getCubeCount(tailR2), "tail_r2 should have 1 cube");
    assertPartPose(tailR2, 0.0F, -15.0F, 8.5F, -0.7854F, 0.0F, 0.0F, "tail_r2");

    PartDefinition tailR3 = tail.getChild("tail_r3");
    assertNotNull(tailR3, "tail_r3 part should exist");
    assertEquals(1, getCubeCount(tailR3), "tail_r3 should have 1 cube");
    assertPartPose(tailR3, 0.0F, -15.1924F, 6.5328F, 0.3927F, 0.0F, 0.0F, "tail_r3");

    PartDefinition head = parsedRoot.getChild("head");
    assertNotNull(head, "Head part should exist");
    assertTrue(hasCubes(head), "Head should have direct cubes");

    PartDefinition featherR1 = head.getChild("feather_r1");
    assertNotNull(featherR1, "feather_r1 part should exist");
    assertEquals(2, getCubeCount(featherR1), "feather_r1 should have 2 cubes");

    PartDefinition rightLeg = parsedRoot.getChild("right_leg");
    assertNotNull(rightLeg, "right_leg part should exist");
    assertTrue(hasCubes(rightLeg), "right_leg should have direct cubes");

    PartDefinition leftLeg = parsedRoot.getChild("left_leg");
    assertNotNull(leftLeg, "left_leg part should exist");
    assertTrue(hasCubes(leftLeg), "left_leg should have direct cubes");

    PartDefinition rightWing = parsedRoot.getChild("right_wing");
    assertNotNull(rightWing, "right_wing part should exist");

    PartDefinition rightWingR1 = rightWing.getChild("right_wing_r1");
    assertNotNull(rightWingR1, "right_wing_r1 part should exist");
    assertEquals(1, getCubeCount(rightWingR1), "right_wing_r1 should have 1 cube");

    PartDefinition leftWing = parsedRoot.getChild("left_wing");
    assertNotNull(leftWing, "left_wing part should exist");
    assertTrue(hasCubes(leftWing), "left_wing should have direct cubes");

    PartDefinition rightHand = parsedRoot.getChild("right_hand");
    assertNotNull(rightHand, "right_hand part should exist");

    PartDefinition leftHand = parsedRoot.getChild("left_hand");
    assertNotNull(leftHand, "left_hand part should exist");

    PartDefinition beak = parsedRoot.getChild("beak");
    assertNotNull(beak, "beak part should exist");
    assertTrue(hasCubes(beak), "beak should have direct cubes");

    PartDefinition redThing = parsedRoot.getChild("red_thing");
    assertNotNull(redThing, "red_thing part should exist");
    assertTrue(hasCubes(redThing), "red_thing should have direct cubes");

    // Verify texture dimensions (64x64)
    Field materialField = LayerDefinition.class.getDeclaredField("material");
    materialField.setAccessible(true);
    Object material = materialField.get(parsedLayer);

    Field xTexSizeField = material.getClass().getDeclaredField("xTexSize");
    xTexSizeField.setAccessible(true);
    int xTexSize = (Integer) xTexSizeField.get(material);

    Field yTexSizeField = material.getClass().getDeclaredField("yTexSize");
    yTexSizeField.setAccessible(true);
    int yTexSize = (Integer) yTexSizeField.get(material);

    assertEquals(64, xTexSize, "Texture width should be 64");
    assertEquals(64, yTexSize, "Texture height should be 64");
  }

  // Helper method from BlockbenchModelLoader to create LayerDefinition
  private LayerDefinition createLayerDefinition(MinecraftModel model) {
    MeshDefinition meshDefinition = new MeshDefinition();
    PartDefinition root = meshDefinition.getRoot();
    for (de.markusbordihn.playercompanions.client.model.minecraft.MinecraftPart part : model.getRootParts()) {
      addPart(root, part);
    }
    return LayerDefinition.create(meshDefinition, model.getTextureWidth(),
      model.getTextureHeight());
  }

  private void addPart(PartDefinition parent,
    de.markusbordihn.playercompanions.client.model.minecraft.MinecraftPart part) {
    CubeListBuilder cubeBuilder = CubeListBuilder.create();
    for (de.markusbordihn.playercompanions.client.model.minecraft.MinecraftCube cube : part.getCubes()) {
      float[] pos = cube.getPosition();
      float[] dim = cube.getDimensions();
      int[] uv = cube.getUvOffset();

      cubeBuilder = cubeBuilder.texOffs(uv[0], uv[1])
        .addBox(pos[0], pos[1], pos[2], dim[0], dim[1], dim[2],
          new net.minecraft.client.model.geom.builders.CubeDeformation(0.0F));

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
    for (de.markusbordihn.playercompanions.client.model.minecraft.MinecraftPart child : part.getChildren()) {
      addPart(partDef, child);
    }
  }

  private boolean hasCubes(PartDefinition part) {
    try {
      Field cubesField = PartDefinition.class.getDeclaredField("cubes");
      cubesField.setAccessible(true);
      Object cubes = cubesField.get(part);

      if (cubes instanceof List) {
        return !((List<?>) cubes).isEmpty();
      }

      return false;
    } catch (Exception e) {
      System.err.println("Failed to check cubes: " + e.getMessage());
      return false;
    }
  }

  private int getCubeCount(PartDefinition part) {
    try {
      Field cubesField = PartDefinition.class.getDeclaredField("cubes");
      cubesField.setAccessible(true);
      Object cubes = cubesField.get(part);

      if (cubes instanceof List) {
        return ((List<?>) cubes).size();
      }

      return 0;
    } catch (Exception e) {
      System.err.println("Failed to get cube count: " + e.getMessage());
      return 0;
    }
  }

  private void assertPartPose(PartDefinition part, float x, float y, float z,
    float xRot, float yRot, float zRot, String partName) {
    try {
      Field partPoseField = PartDefinition.class.getDeclaredField("partPose");
      partPoseField.setAccessible(true);
      PartPose pose = (PartPose) partPoseField.get(part);

      Field xField = PartPose.class.getDeclaredField("x");
      xField.setAccessible(true);
      float actualX = (Float) xField.get(pose);

      Field yField = PartPose.class.getDeclaredField("y");
      yField.setAccessible(true);
      float actualY = (Float) yField.get(pose);

      Field zField = PartPose.class.getDeclaredField("z");
      zField.setAccessible(true);
      float actualZ = (Float) zField.get(pose);

      Field xRotField = PartPose.class.getDeclaredField("xRot");
      xRotField.setAccessible(true);
      float actualXRot = (Float) xRotField.get(pose);

      Field yRotField = PartPose.class.getDeclaredField("yRot");
      yRotField.setAccessible(true);
      float actualYRot = (Float) yRotField.get(pose);

      Field zRotField = PartPose.class.getDeclaredField("zRot");
      zRotField.setAccessible(true);
      float actualZRot = (Float) zRotField.get(pose);

      assertEquals(x, actualX, 0.0001F, partName + " X position mismatch");
      assertEquals(y, actualY, 0.0001F, partName + " Y position mismatch");
      assertEquals(z, actualZ, 0.0001F, partName + " Z position mismatch");
      assertEquals(xRot, actualXRot, 0.0001F, partName + " X rotation mismatch");
      assertEquals(yRot, actualYRot, 0.0001F, partName + " Y rotation mismatch");
      assertEquals(zRot, actualZRot, 0.0001F, partName + " Z rotation mismatch");
    } catch (Exception e) {
      throw new AssertionError("Failed to check PartPose for " + partName + ": " + e.getMessage(),
        e);
    }
  }
}
