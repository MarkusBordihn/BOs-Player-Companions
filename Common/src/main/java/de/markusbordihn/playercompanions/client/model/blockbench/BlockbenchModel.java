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

import java.util.List;

public class BlockbenchModel {

  private final int textureWidth;
  private final int textureHeight;
  private final List<BlockbenchElement> elements;
  private final List<BlockbenchGroup> groups;
  private final List<BlockbenchOutlinerNode> outliner;

  public BlockbenchModel(
    int textureWidth,
    int textureHeight,
    List<BlockbenchElement> elements,
    List<BlockbenchGroup> groups,
    List<BlockbenchOutlinerNode> outliner) {
    this.textureWidth = textureWidth;
    this.textureHeight = textureHeight;
    this.elements = elements;
    this.groups = groups;
    this.outliner = outliner;
  }

  public int getTextureWidth() {
    return textureWidth;
  }

  public int getTextureHeight() {
    return textureHeight;
  }

  public List<BlockbenchElement> getElements() {
    return elements;
  }

  public List<BlockbenchGroup> getGroups() {
    return groups;
  }

  public List<BlockbenchOutlinerNode> getOutliner() {
    return outliner;
  }
}
