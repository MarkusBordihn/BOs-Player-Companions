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

import java.util.ArrayList;
import java.util.List;

public class MinecraftPart {

  private final String name;
  private final float[] offset;
  private final float[] rotation;
  private final List<MinecraftCube> cubes;
  private final List<MinecraftPart> children;

  public MinecraftPart(String name, float[] offset, float[] rotation) {
    this.name = name;
    this.offset = offset;
    this.rotation = rotation;
    this.cubes = new ArrayList<>();
    this.children = new ArrayList<>();
  }

  public String getName() {
    return name;
  }

  public float[] getOffset() {
    return offset;
  }

  public float[] getRotation() {
    return rotation;
  }

  public List<MinecraftCube> getCubes() {
    return cubes;
  }

  public List<MinecraftPart> getChildren() {
    return children;
  }

  public void addCube(MinecraftCube cube) {
    cubes.add(cube);
  }

  public void addChild(MinecraftPart child) {
    children.add(child);
  }
}
