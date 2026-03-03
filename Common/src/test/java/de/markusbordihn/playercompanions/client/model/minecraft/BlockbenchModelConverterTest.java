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

package de.markusbordihn.playercompanions.client.model.minecraft;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import de.markusbordihn.playercompanions.client.model.blockbench.BlockbenchModel;
import de.markusbordihn.playercompanions.client.model.blockbench.BlockbenchModelReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class BlockbenchModelConverterTest {

  private static MinecraftModel minecraftModel;

  @BeforeAll
  static void setup() throws Exception {
    InputStream modelStream = BlockbenchModelConverterTest.class.getResourceAsStream(
      "/assets/player_companions/models/entity/pig.bbmodel");
    BlockbenchModel blockbenchModel = BlockbenchModelReader.read(modelStream);
    minecraftModel = BlockbenchModelConverter.convert(blockbenchModel);
  }

  @Test
  void testTextureResolution() {
    assertEquals(64, minecraftModel.getTextureWidth());
    assertEquals(64, minecraftModel.getTextureHeight());
  }

  @Test
  void testRootParts() {
    assertEquals(6, minecraftModel.getRootParts().size());
    assertNotNull(findPart(minecraftModel.getRootParts(), "body"));
    assertNotNull(findPart(minecraftModel.getRootParts(), "head"));
    assertNotNull(findPart(minecraftModel.getRootParts(), "right_hind_leg"));
    assertNotNull(findPart(minecraftModel.getRootParts(), "left_hind_leg"));
    assertNotNull(findPart(minecraftModel.getRootParts(), "right_front_leg"));
    assertNotNull(findPart(minecraftModel.getRootParts(), "left_front_leg"));
  }

  @Test
  void testBodyPart() {
    MinecraftPart body = findPart(minecraftModel.getRootParts(), "body");
    assertNotNull(body);
    assertArrayEquals(new float[]{0, 24, 0}, body.getOffset(), 0.01f);
    assertArrayEquals(new float[]{0, 0, 0}, body.getRotation(), 0.01f);
    assertEquals(0, body.getCubes().size());
    assertEquals(2, body.getChildren().size());

    MinecraftPart bodyR1 = findPart(body.getChildren(), "body_r1");
    assertNotNull(bodyR1);
    assertArrayEquals(new float[]{0, -13, 2}, bodyR1.getOffset(), 0.01f);
    assertArrayEquals(new float[]{1.5708f, 0, 0}, bodyR1.getRotation(), 0.01f);
    assertEquals(1, bodyR1.getCubes().size());

    MinecraftCube bodyR1Cube = bodyR1.getCubes().get(0);
    assertArrayEquals(new int[]{26, 8}, bodyR1Cube.getUvOffset());
    assertArrayEquals(new float[]{-5, -10, -7}, bodyR1Cube.getPosition(), 0.01f);
    assertArrayEquals(new float[]{10, 16, 8}, bodyR1Cube.getDimensions(), 0.01f);
  }

  @Test
  void testBagsPart() {
    MinecraftPart body = findPart(minecraftModel.getRootParts(), "body");
    MinecraftPart bags = findPart(body.getChildren(), "bags");
    assertNotNull(bags);
    assertArrayEquals(new float[]{0, 0, 0}, bags.getOffset(), 0.01f);
    assertEquals(3, bags.getCubes().size());
    assertEquals(1, bags.getChildren().size());

    MinecraftPart rightBagR1 = findPart(bags.getChildren(), "right_bag_r1");
    assertNotNull(rightBagR1);
    assertArrayEquals(new float[]{-6, -9.5f, 1}, rightBagR1.getOffset(), 0.01f);
    assertArrayEquals(new float[]{0, 3.1416f, 0}, rightBagR1.getRotation(), 0.01f);
    assertEquals(1, rightBagR1.getCubes().size());
  }

  @Test
  void testHeadPart() {
    MinecraftPart head = findPart(minecraftModel.getRootParts(), "head");
    assertNotNull(head);
    assertArrayEquals(new float[]{0, 12, -6}, head.getOffset(), 0.01f);
    assertArrayEquals(new float[]{0, 0, 0}, head.getRotation(), 0.01f);
    assertEquals(2, head.getCubes().size());
    assertEquals(2, head.getChildren().size());

    MinecraftCube headCube = head.getCubes().stream()
      .filter(c -> c.getUvOffset()[0] == 0 && c.getUvOffset()[1] == 0)
      .findFirst().orElse(null);
    assertNotNull(headCube);
    assertArrayEquals(new float[]{-4, -4, -8}, headCube.getPosition(), 0.01f);
    assertArrayEquals(new float[]{8, 8, 8}, headCube.getDimensions(), 0.01f);

    MinecraftCube snoutCube = head.getCubes().stream()
      .filter(c -> c.getUvOffset()[0] == 16 && c.getUvOffset()[1] == 16)
      .findFirst().orElse(null);
    assertNotNull(snoutCube);
    assertArrayEquals(new float[]{-2, 0, -9}, snoutCube.getPosition(), 0.01f);
    assertArrayEquals(new float[]{4, 3, 1}, snoutCube.getDimensions(), 0.01f);
  }

  @Test
  void testEyebrows() {
    MinecraftPart head = findPart(minecraftModel.getRootParts(), "head");

    MinecraftPart eyeLeft = findPart(head.getChildren(), "eyebrown_left_r1");
    assertNotNull(eyeLeft);
    assertArrayEquals(new float[]{2.975f, -1.975f, -8}, eyeLeft.getOffset(), 0.01f);
    assertArrayEquals(new float[]{0, 0, -0.0873f}, eyeLeft.getRotation(), 0.01f);

    MinecraftPart eyeRight = findPart(head.getChildren(), "eyebrown_right_r1");
    assertNotNull(eyeRight);
    assertArrayEquals(new float[]{-2, -2.5f, -8}, eyeRight.getOffset(), 0.01f);
    assertArrayEquals(new float[]{0, 0, 0.0873f}, eyeRight.getRotation(), 0.01f);
  }

  @Test
  void testRightHindLeg() {
    MinecraftPart leg = findPart(minecraftModel.getRootParts(), "right_hind_leg");
    assertNotNull(leg);
    assertArrayEquals(new float[]{0, 24, 0}, leg.getOffset(), 0.01f);
    assertEquals(1, leg.getCubes().size());
    assertEquals(0, leg.getChildren().size());

    MinecraftCube cube = leg.getCubes().get(0);
    assertArrayEquals(new int[]{0, 17}, cube.getUvOffset());
    assertArrayEquals(new float[]{-5, -6, 4}, cube.getPosition(), 0.01f);
    assertArrayEquals(new float[]{3, 6, 4}, cube.getDimensions(), 0.01f);
    assertFalse(cube.isMirror());
  }

  @Test
  void testLeftHindLeg() {
    MinecraftPart leg = findPart(minecraftModel.getRootParts(), "left_hind_leg");
    assertNotNull(leg);
    assertArrayEquals(new float[]{0, 24, 0}, leg.getOffset(), 0.01f);
    assertEquals(1, leg.getCubes().size());

    MinecraftCube cube = leg.getCubes().get(0);
    assertArrayEquals(new int[]{0, 17}, cube.getUvOffset());
    assertArrayEquals(new float[]{2, -6, 4}, cube.getPosition(), 0.01f);
    assertArrayEquals(new float[]{3, 6, 4}, cube.getDimensions(), 0.01f);
    assertTrue(cube.isMirror());
  }

  @Test
  void testRightFrontLeg() {
    MinecraftPart leg = findPart(minecraftModel.getRootParts(), "right_front_leg");
    assertNotNull(leg);
    assertArrayEquals(new float[]{0, 24, 0}, leg.getOffset(), 0.01f);
    assertEquals(1, leg.getCubes().size());

    MinecraftCube cube = leg.getCubes().get(0);
    assertArrayEquals(new int[]{0, 16}, cube.getUvOffset());
    assertArrayEquals(new float[]{-4.998f, -7, -7}, cube.getPosition(), 0.01f);
    assertArrayEquals(new float[]{3, 7, 4}, cube.getDimensions(), 0.01f);
    assertFalse(cube.isMirror());
  }

  @Test
  void testLeftFrontLeg() {
    MinecraftPart leg = findPart(minecraftModel.getRootParts(), "left_front_leg");
    assertNotNull(leg);
    assertArrayEquals(new float[]{0, 24, 0}, leg.getOffset(), 0.01f);
    assertEquals(1, leg.getCubes().size());

    MinecraftCube cube = leg.getCubes().get(0);
    assertArrayEquals(new int[]{0, 16}, cube.getUvOffset());
    assertArrayEquals(new float[]{1.998f, -7, -7}, cube.getPosition(), 0.01f);
    assertArrayEquals(new float[]{3, 7, 4}, cube.getDimensions(), 0.01f);
    assertTrue(cube.isMirror());
  }

  private MinecraftPart findPart(List<MinecraftPart> parts, String name) {
    return parts.stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
  }

  @Test
  void testEmptyModelConversion() {
    BlockbenchModel emptyModel = new BlockbenchModel(
      64, 64,
      new ArrayList<>(),
      new ArrayList<>(),
      new ArrayList<>()
    );

    MinecraftModel converted = BlockbenchModelConverter.convert(emptyModel);
    assertNotNull(converted);
    assertEquals(64, converted.getTextureWidth());
    assertEquals(64, converted.getTextureHeight());
    assertEquals(0, converted.getRootParts().size());
  }
}
