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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.InputStream;
import org.junit.jupiter.api.Test;

public class BlockbenchModelReaderTest {

  @Test
  public void testReadPigModel() throws Exception {
    InputStream modelStream =
      getClass().getResourceAsStream("/assets/player_companions/models/entity/pig.bbmodel");
    assertNotNull(modelStream, "pig.bbmodel should exist in test resources");

    BlockbenchModel model = BlockbenchModelReader.read(modelStream);

    // Test resolution
    assertEquals(64, model.getTextureWidth());
    assertEquals(64, model.getTextureHeight());

    // Test elements count
    assertEquals(13, model.getElements().size(), "pig.bbmodel should have 13 elements");

    // Test groups count
    assertEquals(7, model.getGroups().size(), "pig.bbmodel should have 7 groups");

    // Test specific element (head)
    BlockbenchElement headElement = findElement(model, "head");
    assertNotNull(headElement, "Should find 'head' element");
    assertEquals("head", headElement.getName());
    assertArrayEquals(new float[]{-4, 8, -14}, headElement.getFrom());
    assertArrayEquals(new float[]{4, 16, -6}, headElement.getTo());
    assertFalse(headElement.hasRotation(), "head element should not have rotation");

    // Test specific element with rotation (eyebrown_left)
    BlockbenchElement eyeElement = findElement(model, "eyebrown_left");
    assertNotNull(eyeElement, "Should find 'eyebrown_left' element");
    assertTrue(eyeElement.hasRotation(), "eyebrown_left should have rotation");
    assertArrayEquals(new float[]{0, 0, -5}, eyeElement.getRotation());

    // Test specific group (head)
    BlockbenchGroup headGroup = findGroup(model, "head");
    assertNotNull(headGroup, "Should find 'head' group");
    assertArrayEquals(new float[]{0, 12, -6}, headGroup.getOrigin());
  }

  private BlockbenchElement findElement(BlockbenchModel model, String name) {
    return model.getElements().stream()
      .filter(e -> e.getName().equals(name))
      .findFirst()
      .orElse(null);
  }

  private BlockbenchGroup findGroup(BlockbenchModel model, String name) {
    return model.getGroups().stream()
      .filter(g -> g.getName().equals(name))
      .findFirst()
      .orElse(null);
  }

  @Test
  public void testMirrorUvFlag() throws Exception {
    InputStream modelStream =
      getClass().getResourceAsStream("/assets/player_companions/models/entity/pig.bbmodel");
    BlockbenchModel model = BlockbenchModelReader.read(modelStream);

    BlockbenchElement leftHindLeg = findElement(model, "left_hind_leg");
    assertNotNull(leftHindLeg);
    assertTrue(leftHindLeg.isMirrorUv(), "left_hind_leg should have mirror_uv=true");

    BlockbenchElement rightHindLeg = findElement(model, "right_hind_leg");
    assertNotNull(rightHindLeg);
    assertFalse(rightHindLeg.isMirrorUv(), "right_hind_leg should have mirror_uv=false");
  }

  @Test
  public void testOutlinerHierarchy() throws Exception {
    InputStream modelStream =
      getClass().getResourceAsStream("/assets/player_companions/models/entity/pig.bbmodel");
    BlockbenchModel model = BlockbenchModelReader.read(modelStream);

    assertEquals(6, model.getOutliner().size(), "Should have 6 root outliner entries");
    assertNotNull(model.getOutliner().get(0).getUuid(), "Root outliner node should have UUID");
  }
}
