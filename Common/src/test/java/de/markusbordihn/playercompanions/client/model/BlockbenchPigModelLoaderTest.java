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

public class BlockbenchPigModelLoaderTest {

  @Test
  public void testPigModelParsing() throws Exception {
    InputStream modelStream =
      getClass()
        .getResourceAsStream("/assets/player_companions/models/entity/pig.bbmodel");
    assertNotNull(modelStream, "pig.bbmodel should exist in resources");

    BlockbenchModel blockbenchModel = BlockbenchModelReader.read(modelStream);
    assertNotNull(blockbenchModel, "BlockbenchModel should not be null");

    MinecraftModel minecraftModel = BlockbenchModelConverter.convert(blockbenchModel);
    assertNotNull(minecraftModel, "MinecraftModel should not be null");

    LayerDefinition parsedLayer = createLayerDefinition(minecraftModel);
    assertNotNull(parsedLayer, "Layer definition should not be null");

    Field meshField = LayerDefinition.class.getDeclaredField("mesh");
    meshField.setAccessible(true);
    MeshDefinition parsedMesh = (MeshDefinition) meshField.get(parsedLayer);
    assertNotNull(parsedMesh, "Mesh definition should not be null");

    PartDefinition parsedRoot = parsedMesh.getRoot();
    assertNotNull(parsedRoot, "Root part definition should not be null");

    PartDefinition body = parsedRoot.getChild("body");
    assertNotNull(body, "Body part should exist");
    assertPartPose(body, 0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.0F, "body");

    PartDefinition bodyR1 = body.getChild("body_r1");
    assertNotNull(bodyR1, "body_r1 part should exist");
    assertEquals(1, getCubeCount(bodyR1), "body_r1 should have 1 cube");
    assertPartPose(bodyR1, 0.0F, -13.0F, 2.0F, 1.5708F, 0.0F, 0.0F, "body_r1");

    PartDefinition bags = body.getChild("bags");
    assertNotNull(bags, "Bags part should exist");
    assertTrue(hasCubes(bags), "Bags should have direct cubes");
    assertEquals(3, getCubeCount(bags), "Bags should have 3 cubes");
    assertPartPose(bags, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, "bags");

    PartDefinition rightBagR1 = bags.getChild("right_bag_r1");
    assertNotNull(rightBagR1, "right_bag_r1 part should exist");
    assertEquals(1, getCubeCount(rightBagR1), "right_bag_r1 should have 1 cube");
    assertPartPose(rightBagR1, -6.0F, -9.5F, 1.0F, 0.0F, 3.1416F, 0.0F, "right_bag_r1");

    PartDefinition head = parsedRoot.getChild("head");
    assertNotNull(head, "Head part should exist");
    assertTrue(hasCubes(head), "Head should have direct cubes");
    assertEquals(2, getCubeCount(head), "Head should have 2 cubes");
    assertPartPose(head, 0.0F, 12.0F, -6.0F, 0.0F, 0.0F, 0.0F, "head");

    PartDefinition eyebrownLeftR1 = head.getChild("eyebrown_left_r1");
    assertNotNull(eyebrownLeftR1, "eyebrown_left_r1 part should exist");
    assertEquals(1, getCubeCount(eyebrownLeftR1), "eyebrown_left_r1 should have 1 cube");
    assertPartPose(eyebrownLeftR1, 2.975F, -1.975F, -8.0F, 0.0F, 0.0F, -0.0873F,
      "eyebrown_left_r1");

    PartDefinition eyebrownRightR1 = head.getChild("eyebrown_right_r1");
    assertNotNull(eyebrownRightR1, "eyebrown_right_r1 part should exist");
    assertEquals(1, getCubeCount(eyebrownRightR1), "eyebrown_right_r1 should have 1 cube");
    assertPartPose(eyebrownRightR1, -2.0F, -2.5F, -8.0F, 0.0F, 0.0F, 0.0873F, "eyebrown_right_r1");

    PartDefinition rightHindLeg = parsedRoot.getChild("right_hind_leg");
    assertNotNull(rightHindLeg, "right_hind_leg part should exist");
    assertTrue(hasCubes(rightHindLeg), "right_hind_leg should have direct cubes");
    assertPartPose(rightHindLeg, 0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.0F, "right_hind_leg");

    PartDefinition leftHindLeg = parsedRoot.getChild("left_hind_leg");
    assertNotNull(leftHindLeg, "left_hind_leg part should exist");
    assertTrue(hasCubes(leftHindLeg), "left_hind_leg should have direct cubes");
    assertPartPose(leftHindLeg, 0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.0F, "left_hind_leg");

    PartDefinition rightFrontLeg = parsedRoot.getChild("right_front_leg");
    assertNotNull(rightFrontLeg, "right_front_leg part should exist");
    assertTrue(hasCubes(rightFrontLeg), "right_front_leg should have direct cubes");
    assertPartPose(rightFrontLeg, 0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.0F, "right_front_leg");

    PartDefinition leftFrontLeg = parsedRoot.getChild("left_front_leg");
    assertNotNull(leftFrontLeg, "left_front_leg part should exist");
    assertTrue(hasCubes(leftFrontLeg), "left_front_leg should have direct cubes");
    assertPartPose(leftFrontLeg, 0.0F, 24.0F, 0.0F, 0.0F, 0.0F, 0.0F, "left_front_leg");

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

    float[] offset = part.getOffset();
    float[] rotation = part.getRotation();
    PartDefinition partDef = parent.addOrReplaceChild(
      part.getName(),
      cubeBuilder,
      PartPose.offsetAndRotation(
        offset[0], offset[1], offset[2],
        rotation[0], rotation[1], rotation[2]));

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
